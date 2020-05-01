// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.flexmark;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
import com.vladsch.flexmark.ext.wikilink.internal.WikiLinkLinkResolver;
import com.vladsch.flexmark.html.Disposable;
import com.vladsch.flexmark.html.IndependentLinkResolverFactory;
import com.vladsch.flexmark.html.LinkResolver;
import com.vladsch.flexmark.html.renderer.LinkResolverBasicContext;
import com.vladsch.flexmark.html.renderer.LinkStatus;
import com.vladsch.flexmark.html.renderer.LinkType;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.md.nav.MdImageCache;
import com.vladsch.md.nav.MdProjectComponent;
import com.vladsch.md.nav.parser.api.MdLinkMapProvider;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.MdRenderingProfileManager;
import com.vladsch.md.nav.util.FileRef;
import com.vladsch.md.nav.util.LinkRef;
import com.vladsch.md.nav.util.Links;
import com.vladsch.md.nav.util.Local;
import com.vladsch.md.nav.util.PathInfo;
import com.vladsch.md.nav.util.ProjectFileRef;
import com.vladsch.md.nav.util.Remote;
import com.vladsch.md.nav.util.Want;
import com.vladsch.md.nav.vcs.GitHubLinkResolver;
import com.vladsch.md.nav.vcs.GitHubVcsRoot;
import com.vladsch.md.nav.vcs.MdLinkResolver;
import com.vladsch.plugin.util.HelpersKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.vladsch.flexmark.html.renderer.LinkType.IMAGE;
import static com.vladsch.flexmark.html.renderer.LinkType.IMAGE_REF;
import static com.vladsch.flexmark.html.renderer.LinkType.LINK_REF;

public class FlexmarkLinkResolver implements LinkResolver, Disposable {
    @Nullable public MdProjectComponent projectComponent;
    @Nullable public GitHubLinkResolver resolver;
    final private boolean useImageSerials;
    private Map<String, String> imageFileMap;
    private String htmlExportPath;
    final private boolean linkToExportedHtml;
    @Nullable protected MdRenderingProfile renderingProfile;
    @Nullable protected MdFile file;
    final Want.Options.Remotes remoteFormat;
    final Want.Options.Locals localFormat;
    final boolean openRemoteLinks;
    final boolean showUnresolvedLinkRefs;

    @Override
    public void dispose() {
        projectComponent = null;
        resolver = null;
        imageFileMap = null;
        htmlExportPath = null;
        renderingProfile = null;
        file = null;
    }

    public FlexmarkLinkResolver(LinkResolverBasicContext context) {
        DataHolder options = context.getOptions();
        Supplier<? extends MdLinkResolver> resolverSupplier = MdNavigatorExtension.LINK_RESOLVER.get(options);
        this.resolver = resolverSupplier == null ? null : (GitHubLinkResolver) resolverSupplier.get();
        this.projectComponent = this.resolver == null || this.resolver.getProject() == null ? null : MdProjectComponent.getInstance(resolver.getProject());
        this.imageFileMap = MdNavigatorExtension.HTML_IMAGE_FILE_MAP.get(options);
        this.htmlExportPath = MdNavigatorExtension.HTML_EXPORT_PATH.get(options);
        this.linkToExportedHtml = MdNavigatorExtension.LINK_TO_EXPORTED_HTML.get(options);
        this.useImageSerials = MdNavigatorExtension.USE_IMAGE_SERIALS.get(options);

        Supplier<? extends MdRenderingProfile> profileSupplier = MdNavigatorExtension.RENDERING_PROFILE.get(options);
        MdRenderingProfile renderingProfile = profileSupplier == null ? null : profileSupplier.get();
        
        MdFile file = null;

        if (resolver != null) {
            if (resolver.getProject() != null) {
                final VirtualFile virtualFile = resolver.getContainingFile().getVirtualFile();
                if (virtualFile != null) {
                    PsiFile psiFile = PsiManager.getInstance(resolver.getProject()).findFile(virtualFile);
                    if (psiFile instanceof MdFile) {
                        file = (MdFile) psiFile;
                        if (renderingProfile == null) renderingProfile = MdRenderingProfileManager.getProfile(psiFile);
                    }
                }
            }
        }
        
        this.renderingProfile = renderingProfile;
        this.file = file;

        Want.Options.Remotes remoteFormat = null;
        Want.Options.Locals localFormat = null;
        Boolean openRemoteLinks = null;
        Boolean showUnresolvedLinkRefs = null;

        if (renderingProfile != null) {
            for (MdLinkMapProvider provider : MdLinkMapProvider.EXTENSIONS.getValue()) {
                if (remoteFormat == null) remoteFormat = provider.getRemoteFormat(renderingProfile);
                if (localFormat == null) localFormat = provider.getLocalFormat(renderingProfile);
                if (openRemoteLinks == null) openRemoteLinks = provider.getOpenRemoteLinks(renderingProfile);
                if (showUnresolvedLinkRefs == null) showUnresolvedLinkRefs = provider.showUnresolvedLinkRefs(renderingProfile);

                if (remoteFormat != null && localFormat != null && openRemoteLinks != null && showUnresolvedLinkRefs != null) {
                    break;
                }
            }
        }

        this.remoteFormat = remoteFormat;
        this.localFormat = localFormat;
        this.openRemoteLinks = openRemoteLinks != null ? openRemoteLinks : false;
        this.showUnresolvedLinkRefs = showUnresolvedLinkRefs != null ? showUnresolvedLinkRefs : false;
    }

    @Nullable
    public String getLinkTarget(@NotNull String url, LinkType linkType, @NotNull boolean[] localOnly) {
        // return null if does not resolve, but only if validating links
        if (linkType != IMAGE && (htmlExportPath == null || !linkToExportedHtml)) {
            localOnly[0] = false;
            return url;
        }
        PathInfo urlInfo = new PathInfo(url);
        boolean useSerial = false;
        String serialQuery = "";

        if (resolver != null) {
            if (!resolver.isAbsoluteUnchecked(urlInfo)) {
                Want.Options.Remotes remoteFormat = this.remoteFormat;
                Want.Options.Locals localFormat = this.localFormat;

                if (!url.startsWith("#") && (linkType == WikiLinkExtension.WIKI_LINK || urlInfo.isRelative() || urlInfo.isRepoRelative() || remoteFormat != null || localFormat != null)) {
                    LinkRef targetRef;
                    if (linkType == LinkType.LINK) {
                        targetRef = LinkRef.parseLinkRef(resolver.getContainingFile(), url, null);
                    } else if (linkType == IMAGE) {
                        targetRef = LinkRef.parseImageLinkRef(resolver.getContainingFile(), url, null);
                        useSerial = useImageSerials;
                    } else if (linkType == WikiLinkExtension.WIKI_LINK) {
                        targetRef = LinkRef.parseWikiLinkRef(resolver.getContainingFile(), url, null);
                    } else {
                        targetRef = LinkRef.parseLinkRef(resolver.getContainingFile(), url, null);
                    }

                    if (!resolver.isExternalUnchecked(targetRef)) {
                        // add option reading from localOnly to resolve locally
                        PathInfo resolvedTarget = null;

                        if (remoteFormat == null) remoteFormat = Remote.getURL();
                        if (localFormat == null) localFormat = Local.getURI();

                        if (linkType == IMAGE) {
                            if (remoteFormat == Remote.getURL()) {
                                remoteFormat = Remote.getURI();
                            }
                        } else {
                            if (localOnly[0] && remoteFormat == Remote.getURL()) {
                                remoteFormat = Remote.getURI();
                            }
                        }

                        resolvedTarget = resolver.resolve(targetRef, Want.INSTANCE.invoke(remoteFormat, localFormat, Links.getURL()), null);

                        localOnly[0] = false;
                        if (resolvedTarget != null) {
                            boolean isImageForHtmlExport = imageFileMap != null && htmlExportPath != null && linkType == IMAGE;

                            FileRef resolvedTargetRef = resolvedTarget instanceof LinkRef ? ((LinkRef) resolvedTarget).getTargetRef() : null;

                            if (!isImageForHtmlExport && useSerial && resolvedTargetRef != null) {
                                if (projectComponent != null && renderingProfile != null && renderingProfile.getHtmlSettings().getImageUriSerials()) {
                                    long serial = projectComponent.getFileSerial(resolvedTargetRef.getFilePath());
                                    if (serial > 0L) {
                                        serialQuery = "?" + serial;
                                    }
                                }
                            }

                            // if original was not URI then we change it
                            if (resolvedTarget instanceof LinkRef) {
                                if (!urlInfo.isURI()) {
                                    String href;

                                    if (!linkToExportedHtml && (resolvedTarget.isLocal() ? localFormat != Local.getURI() : remoteFormat != Remote.getURL())) {
                                        // leave format as is
                                        href = resolvedTarget.getFilePath();
                                        return href + serialQuery;
                                    } else {

                                        if (isImageForHtmlExport && resolvedTargetRef != null && imageFileMap.containsKey(resolvedTargetRef.getFilePath())) {
                                            String toPath = imageFileMap.get(resolvedTargetRef.getFilePath());
                                            href = LinkRef.urlEncode(PathInfo.relativePath(htmlExportPath, toPath, true, false), null);
                                            return href;
                                        } else {
                                            if (linkToExportedHtml && htmlExportPath != null && linkType != IMAGE && resolver.getProject() != null && resolvedTargetRef != null) {
                                                // see if the target is exportable
                                                VirtualFile targetFile = resolvedTargetRef.getVirtualFile();
                                                if (targetFile != null) {
                                                    String exportedPath;

                                                    for (MdLinkMapProvider provider : MdLinkMapProvider.EXTENSIONS.getValue()) {
                                                        exportedPath = provider.mapTargetFilePath(resolver.getProject(), targetFile);
                                                        if (exportedPath != null) {
                                                            href = LinkRef.urlEncode(PathInfo.relativePath(HelpersKt.prefixWith(htmlExportPath, "/"), exportedPath, true, false), null);
                                                            return href;
                                                        }
                                                    }
                                                }
                                            }

                                            FileRef fileRef = resolvedTarget.isLocal() ? resolvedTargetRef : null;
                                            localOnly[0] = fileRef instanceof ProjectFileRef && !fileRef.equals(resolver.getContainingFile()) && !((ProjectFileRef) fileRef).isUnderVcs();
                                            href = linkType == IMAGE ? resolvedTarget.getFilePath() : ((LinkRef) resolvedTarget).getFilePathWithAnchor();
                                            if (href.startsWith(MdNavigatorExtension.FILE_URI_PREFIX) && href.length() > MdNavigatorExtension.FILE_URI_PREFIX.length() + 1 && href.charAt(MdNavigatorExtension.FILE_URI_PREFIX.length() + 1) == ':') {
                                                // replace with file:/
                                                href = "file:/" + href.substring(MdNavigatorExtension.FILE_URI_PREFIX.length());
                                            } else if (href.startsWith("https://github.com") && (href.contains("/blob/master/") || href.contains("/raw/master/"))) {
                                                // here we change master to current branch
                                                GitHubVcsRoot vcsRoot = resolver.getProjectResolver().getVcsRoot(resolver.getContainingFile());
                                                if (vcsRoot != null && vcsRoot.getRemoteBranchName() != null) {
                                                    int pos = href.indexOf("/master/");
                                                    href = href.substring(0, pos + 1) + vcsRoot.getRemoteBranchName() + href.substring(pos + "/master/".length() - 1);
                                                }
                                            }

                                            return href + serialQuery;
                                        }
                                    }
                                } else {
                                    return url + serialQuery;
                                }
                            } else {
                                return url + serialQuery;
                            }
                        }
                        return null;
                    } else {
                        localOnly[0] = false;
                    }
                }
            } else if (urlInfo.isFileURI()) {
                // change file://X: to file:/X:
                boolean isImageForHtmlExport = imageFileMap != null && htmlExportPath != null && linkType == IMAGE;
                String key = PathInfo.removeFileUriPrefix(url);

                if (linkType == IMAGE && projectComponent != null && renderingProfile != null && renderingProfile.getHtmlSettings().getImageUriSerials()) {
                    long serial = projectComponent.getFileSerial(key);
                    if (serial > 0L) {
                        serialQuery = "?" + serial;
                    }
                }

                if (isImageForHtmlExport) {
                    if (imageFileMap.containsKey(key)) {
                        String toPath = imageFileMap.get(key);
                        return LinkRef.urlEncode(PathInfo.relativePath(htmlExportPath, toPath, true, false), null);
                    }
                }

                if (localOnly[0] && MdImageCache.getInstance().isCachedFile(key)) {
                    localOnly[0] = false;
                }

                if (url.length() > MdNavigatorExtension.FILE_URI_PREFIX.length() + 1 && url.charAt(MdNavigatorExtension.FILE_URI_PREFIX.length() + 1) == ':') {
                    return url.substring(0, MdNavigatorExtension.FILE_URI_PREFIX.length() - 1) + url.substring(MdNavigatorExtension.FILE_URI_PREFIX.length()) + serialQuery;
                } else {
                    return url + serialQuery;
                }
            } else {
                localOnly[0] = false;
            }
        } else {
            localOnly[0] = false;
        }
        return url;
    }

    @NotNull
    @Override
    public ResolvedLink resolveLink(@NotNull Node node, @NotNull LinkResolverBasicContext context, ResolvedLink link) {
        ResolvedLink result = link;

        if (link.getLinkType() == LINK_REF || link.getLinkType() == IMAGE_REF) {
            if (renderingProfile != null) {
                String id = link.getUrl();
                for (MdLinkMapProvider provider : MdLinkMapProvider.EXTENSIONS.getValue()) {
                    String url = provider.mapRefLink(id, renderingProfile);
                    if (url != null) {
                        result = result.withUrl(url).withStatus(LinkStatus.VALID);
                        break;
                    }
                }
            }
        } else {
            boolean[] localOnly = new boolean[] { renderingProfile == null || !openRemoteLinks };
            String href = getLinkTarget(link.getUrl(), link.getLinkType(), localOnly);
            boolean missing = false;
            if (href == null) {
                missing = true;
                href = link.getUrl();
            }

            // remove any #'s after the first
            int pos = href.indexOf("#");
            if (pos != -1) {
                href = href.substring(0, pos) + "#" + href.substring(pos).replace("#", "").trim();
            }

            if (missing) {
                if (showUnresolvedLinkRefs) {
                    result = result.withStatus(LinkStatus.NOT_FOUND);
                }
            } else {
                if (!href.equals(link.getUrl())) {
                    result = result.withStatus(LinkStatus.VALID).withUrl(href);
                } else {
                    result = result.withStatus(LinkStatus.UNCHECKED);
                }
            }
        }

        return result;
    }

    public static class Factory extends IndependentLinkResolverFactory {
        @Override
        public Set<Class<?>> getBeforeDependents() {
            return Collections.singleton(WikiLinkLinkResolver.Factory.class);
        }

        @NotNull
        @Override
        public LinkResolver apply(@NotNull LinkResolverBasicContext context) {
            return new FlexmarkLinkResolver(context);
        }
    }
}
