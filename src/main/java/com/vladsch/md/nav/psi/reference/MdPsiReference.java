// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.tree.IElementType;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.md.nav.parser.api.MdLinkMapProvider;
import com.vladsch.md.nav.parser.cache.MdCachedResolvedLinks;
import com.vladsch.md.nav.psi.element.*;
import com.vladsch.md.nav.psi.text.MdLineSelectionFakePsiElement;
import com.vladsch.md.nav.psi.text.MdUrlFakePsiElement;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import com.vladsch.md.nav.util.FileRef;
import com.vladsch.md.nav.util.LinkRef;
import com.vladsch.md.nav.util.Links;
import com.vladsch.md.nav.util.Match;
import com.vladsch.md.nav.util.PathInfo;
import com.vladsch.md.nav.util.ProjectFileRef;
import com.vladsch.md.nav.util.ReferenceChangeListener;
import com.vladsch.md.nav.util.Want;
import com.vladsch.md.nav.vcs.GitHubLinkResolver;
import com.vladsch.plugin.util.ElementSorter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MdPsiReference extends PsiReferenceBase<MdNamedElement> implements PsiPolyVariantReference, BindablePsiReference {
    public static final ResolveResult[] EMPTY_RESULTS = ResolveResult.EMPTY_ARRAY;
    protected ResolveResult[] resolveResults;
    protected String resolveResultsName;
    protected final ReferenceChangeListener referenceChangeListener;
    protected boolean resolveRefIsMissing;
    protected boolean resolveRefIsExternal;
    protected final boolean exactReference;

    @Override
    public String toString() {
        //PsiElement resolve = resolve();
        return "Reference for " + myElement.toString();
    }

    public MdPsiReference(@NotNull MdNamedElement element, @NotNull TextRange textRange, final boolean exactReference) {
        super(element, textRange);
        this.exactReference = exactReference;

        referenceChangeListener = name -> {
            if (resolveResultsName != null && (name == null || resolveResultsName.equals(name))) {
                resolveResults = null;
                resolveResultsName = null;
            }
        };
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return getMultiResolveResults(incompleteCode);
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        // we will handle this by renaming the element to point to the new location
        if (myElement instanceof MdLinkRef || myElement instanceof MdWikiLink && element instanceof PsiFileSystemItem) {
            PsiFileSystemItem fileSystemItem = element instanceof PsiFileSystemItem ? (PsiFileSystemItem) element : element.getContainingFile();
            LinkRef linkRef = MdPsiImplUtil.getLinkRef(myElement);
            if (linkRef != null && fileSystemItem != null) {
                GitHubLinkResolver resolver = new GitHubLinkResolver(myElement);
                ProjectFileRef targetRef = new ProjectFileRef(fileSystemItem);
                LinkRef preservedLinkRef = resolver.preserveLinkFormat(linkRef, linkRef.replaceFilePath("", targetRef, false));
                String mappedLinkRef = resolver.denormalizedLinkRef(preservedLinkRef.getFilePath());
                return myElement.setName(mappedLinkRef, MdNamedElement.REASON_BIND_TO_FILE);
            } else {
                //skip silently, caused by multiple invocations of refactoring listener
                return myElement;
            }
        } else if (element instanceof MdHeaderTextImpl && myElement instanceof MdLinkAnchorImpl) {
            String name = ((MdHeaderElementImpl) element.getParent()).getAnchorReferenceId();
            if (name != null) return myElement.setName(name, MdNamedElement.REASON_FILE_MOVED);
        } else if (element.getClass() == myElement.getClass()) {
            String name = ((MdNamedElement) element).getName();
            if (!name.isEmpty()) return myElement.setName(name, MdNamedElement.REASON_FILE_MOVED);
        }
        throw new IncorrectOperationException("Rebind cannot be performed for " + getClass());
    }

    public boolean isResolveRefMissing() {
        return resolveRefIsMissing || resolve() == null;
    }

    public boolean isResolveRefExternal() {
        return resolveRefIsExternal;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length > 0 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    /**
     * Default implementation resolves to missing element reference by namespace of the referencing element
     *
     * @param incompleteCode code is incomplete
     *
     * @return resolve results
     */
    @NotNull
    protected ResolveResult[] getMultiResolveResults(boolean incompleteCode) {
        String name = myElement.getName();
        if (!name.isEmpty() || myElement instanceof MdLinkRefElement && !((MdLinkRefElement) myElement).getNameWithAnchor().isEmpty()) {
            if (myElement instanceof MdWikiLinkRef || myElement instanceof MdLinkRefElement) {
                PsiFile containingFile = myElement.getContainingFile().getOriginalFile();// != null ? myElement.getContainingFile().getOriginalFile() : myElement.getContainingFile();

                if (containingFile.getVirtualFile() != null) {
                    LinkRef linkRef = MdPsiImplUtil.getLinkRef(myElement);

                    if (linkRef != null) {
                        GitHubLinkResolver resolver = new GitHubLinkResolver(myElement);
                        List<PathInfo> pathInfos = null;

                        // FIX: use cached resolved links if available
                        if (!incompleteCode && containingFile instanceof MdFile) {
                            if (MdCachedResolvedLinks.hasCachedLink((MdFile) containingFile, linkRef)) {
                                PathInfo resolve = resolver.resolve(linkRef, Want.INSTANCE.invoke(Links.getURL()), null);
                                if (resolve != null) {
                                    pathInfos = Collections.singletonList(resolve);
                                }
                            }
                        }

                        if (pathInfos == null) {
                            pathInfos = resolver.multiResolve(linkRef, incompleteCode ? Want.INSTANCE.invoke(Match.getLOOSE(), Links.getURL()) : Want.INSTANCE.invoke(Links.getURL()), null);
                            // FIX: update values if not incompleteCode and result is 1 element
                            if (!incompleteCode && pathInfos.size() <= 1 && !linkRef.getFilePathNoQuery().isEmpty() && containingFile instanceof MdFile) {
                                // cache the result
                                if (pathInfos.isEmpty()) {
                                    MdCachedResolvedLinks.addUndefinedCachedLink((MdFile) containingFile, linkRef);
                                } else {
                                    MdCachedResolvedLinks.addCachedLink((MdFile) containingFile, linkRef, pathInfos.get(0));
                                }
                            } else if (pathInfos.size() > 0 && !linkRef.getFilePathNoQuery().isEmpty() && !(myElement instanceof MdWikiLinkRef)) {
                                int tmp = 0;
                            }
                        }

                        if (pathInfos.size() > 0) {
                            List<ResolveResult> results = new ArrayList<>();
                            for (PathInfo pathInfo : pathInfos) {
                                if (pathInfo instanceof FileRef) {
                                    PsiFileSystemItem psiFile = pathInfo instanceof ProjectFileRef ? ((ProjectFileRef) pathInfo).getPsiFileSystemItem() : ((FileRef) pathInfo).psiFileSystemItem(myElement.getProject());
                                    if (psiFile != null) {
                                        results.add(new PsiElementResolveResult(psiFile));
                                    }
                                } else if (pathInfo instanceof LinkRef && pathInfo.isURL()) {
                                    // could be a GitHub URL, create fake URL element for navigation, but map it to textual form
                                    String mappedLinkRefText = null;
                                    String pathWithAnchor = ((LinkRef) pathInfo).getFilePathWithAnchor();
                                    for (MdLinkMapProvider provider : MdLinkMapProvider.EXTENSIONS.getValue()) {
                                        mappedLinkRefText = provider.mapLinkRef(pathWithAnchor, resolver.getRenderingProfile());
                                        if (mappedLinkRefText != null) break;
                                    }
                                    results.add(new PsiElementResolveResult(new MdUrlFakePsiElement(myElement, mappedLinkRefText == null ? pathWithAnchor : mappedLinkRefText)));
                                }
                            }

                            if (results.size() > 0) {
                                return results.toArray(EMPTY_RESULTS);
                            }
                        }

                        if (linkRef.isExternal() || linkRef.isCustomURI()) {
                            // reference is just url
                            return new ResolveResult[] { new PsiElementResolveResult(new MdUrlFakePsiElement(myElement, linkRef.getFilePathWithAnchor())) };
                        }

                        return EMPTY_RESULTS;
                    }
                }
            } else {
                //noinspection StatementWithEmptyBody
                if (myElement instanceof MdAtxHeader || myElement instanceof MdSetextHeader || myElement instanceof MdHeaderText) {
                    // do nothing, no references here
                } else if (myElement instanceof MdLinkAnchor) {
                    PsiElement parent = myElement.getParent();
                    if (parent instanceof MdLinkElement && !name.replace('#', ' ').trim().isEmpty()) {
                        MdLinkRefElement linkRefElement = ((MdLinkElement<?>) parent).getLinkRefElement();
                        if (linkRefElement != null) {
                            PsiReference reference = linkRefElement.getReference();

                            if (reference != null) {
                                PsiElement resolved = null;

                                LinkRef linkRef = MdPsiImplUtil.getLinkRef(myElement);
                                if (linkRef != null) {
                                    PathInfo cachedLink = MdCachedResolvedLinks.getCachedLink(myElement.getMdFile(), linkRef);
                                    if (cachedLink instanceof ProjectFileRef) {
                                        resolved = ((ProjectFileRef) cachedLink).getPsiFile();
                                    }
                                }

                                if (resolved == null) {
                                    resolved = reference.resolve();
                                    if (linkRef != null) {
                                        if (resolved instanceof PsiFile) {
                                            MdCachedResolvedLinks.addCachedLink(myElement.getMdFile(), linkRef, new ProjectFileRef((PsiFile) resolved));
                                        } else if (resolved == null) {
                                            MdCachedResolvedLinks.addUndefinedCachedLink(myElement.getMdFile(), linkRef);
                                        }
                                    }
                                }

                                if (resolved != null) {
                                    // NOTE: process links to files not necessarily MdFiles
                                    if (resolved instanceof PsiFile) {
                                        PsiElement element = myElement.getParent();
                                        if (element instanceof MdExplicitLink || element instanceof MdReference) {
                                            // see if it is a line reference
                                            PsiElement result = null;
                                            result = MdLineSelectionFakePsiElement.getLineSelectionElement((PsiFile) resolved, LinkRef.urlDecode(name));

                                            if (result != null) {
                                                // add it to the link cache
                                                return new ResolveResult[] { new PsiElementResolveResult(result) };
                                            }
                                        }
                                    }

                                    if (resolved instanceof MdFile) {
                                        // search for headers
                                        List<MdAnchorTarget> elements = MdPsiImplUtil.getAnchorTargets((MdFile) resolved, LinkRef.urlDecode(name), true);
                                        if (elements.size() > 0) {
                                            List<MdAnchorTarget> references = ElementSorter.sorted(elements);
                                            ArrayList<ResolveResult> results = new ArrayList<>(references.size());

                                            for (MdAnchorTarget element : references) {
                                                PsiElement identifier = element.getAnchorReferenceElement();
                                                if (identifier != null) results.add(new PsiElementResolveResult(identifier));
                                            }

                                            return results.toArray(EMPTY_RESULTS);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (myElement instanceof MdReferencingElementReference) {
                    // search references in the containing file
                    IElementType referenceType = ((MdReferencingElementReference) myElement).getReferenceType();
                    MdFile containingFile = myElement.getMdFile();
                    List<MdReferenceElement> references = MdPsiImplUtil.getReferenceElements(containingFile, referenceType, name, true);
                    List<ResolveResult> results = new ArrayList<>(references.size());

                    for (PsiElement refElement : references) {
                        if (refElement instanceof MdReferenceElement) {
                            if (((MdReferencingElementReference) myElement).isAcceptable(refElement, incompleteCode, exactReference)) {
                                PsiElement identifier = ((MdReferenceElement) refElement).getReferenceIdentifier();
                                if (identifier != null) results.add(new PsiElementResolveResult(identifier));
                            }
                        }
                    }

                    if (myElement instanceof MdEnumeratedReferenceId) {
                        // need to look in AttributeIdValues
                        List<MdAttributeIdValue> attributeIdValues = MdPsiImplUtil.listChildrenOfAnyType(containingFile, false, true, true, MdAttributeIdValue.class);
                        for (MdAttributeIdValue attributeIdValue : attributeIdValues) {
                            if (attributeIdValue.isReferenceFor(name)) {
                                results.add(new PsiElementResolveResult(attributeIdValue));
                            }
                        }
                    }

                    if (results.size() > 0) {
                        return results.toArray(EMPTY_RESULTS);
                    }
                } else if (myElement instanceof MdEmojiId) {

                } else if (myElement instanceof MdAttributeIdValue) {

                }
            }
        }
        return EMPTY_RESULTS;
    }
}
