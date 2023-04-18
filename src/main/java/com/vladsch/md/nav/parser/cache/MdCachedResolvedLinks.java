// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.vladsch.flexmark.util.misc.DelimitedBuilder;
import com.vladsch.flexmark.util.sequence.Escaping;
import com.vladsch.md.nav.MdFileType;
import com.vladsch.md.nav.parser.cache.data.CachedDataKey;
import com.vladsch.md.nav.parser.cache.data.ProjectCachedData;
import com.vladsch.md.nav.parser.cache.data.dependency.RestartableProjectFileDependency;
import com.vladsch.md.nav.parser.cache.data.transaction.CachedTransactionContext;
import com.vladsch.md.nav.parser.cache.data.transaction.IndentingLogger;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.psi.element.MdImageLinkRef;
import com.vladsch.md.nav.psi.element.MdLinkRefElement;
import com.vladsch.md.nav.psi.element.MdWikiLinkRef;
import com.vladsch.md.nav.psi.util.MdLinkType;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdDebugSettings;
import com.vladsch.md.nav.util.FileRef;
import com.vladsch.md.nav.util.ImageLinkRef;
import com.vladsch.md.nav.util.LinkRef;
import com.vladsch.md.nav.util.PathInfo;
import com.vladsch.md.nav.util.ProjectFileRef;
import com.vladsch.md.nav.util.WikiLinkRef;
import com.vladsch.plugin.util.HelpersKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.vladsch.flexmark.util.misc.Utils.removeSuffix;
import static com.vladsch.md.nav.util.PathInfo.isFileURI;

/**
 * Cache for resolved link address targets for links and images
 * <p>
 * There are two caches.
 * <p>
 * One does not add the file as dependency so is not cleared when the file is changed. This is used to allow re-use of links across file mods
 * <p>
 * The other is the current link cache which contains all resolved links in the current file and is re-computed when file is modified.
 * This cache uses {@link MdCachedFileElements} to scan for links and removes any persistent cached links which no longer match a link in the file
 * and adds any links which survived the mod
 * <p>
 * When a link target is resolved, the link type, address text and resolved target is added to the cache
 * subsequent requests for identical link type and address resolution returns cached value for resolved link
 * <p>
 * Only single resolve values can be cached this way. Multi resolve and completion has to use Link resolver as before
 */
public class MdCachedResolvedLinks {
    public static final VirtualFileManager FILE_MANAGER = VirtualFileManager.getInstance();
    static final Logger LOG_CACHE_DETAIL = IndentingLogger.LOG_COMPUTE_DETAIL;
    static final Logger LOG_CACHE = IndentingLogger.LOG_COMPUTE_RESULT;

    static byte F_VIRTUAL_FILE = 0x01;
    static byte F_PROJECT_FILE = 0x02;
    static byte F_UNDEFINED = 0x04;
    static byte F_UNDEFINED_MARKDOWN = 0x08;

    static class CachedLink {
        final @NotNull MdLinkType myLinkType;       // link type
        final @NotNull String myLinkAddress;        // original link address text
        final @NotNull String myTargetLinkAddress;  // resolved target address
        final byte myFlags;

        static CachedLink cacheLink(@NotNull MdLinkType linkType, @NotNull String linkAddress, @NotNull String targetLinkAddress, boolean isVirtualFile, boolean isProjectFile) {
            byte flags = (byte) (isVirtualFile && isFileURI(targetLinkAddress) ? isProjectFile ? F_PROJECT_FILE | F_VIRTUAL_FILE : F_VIRTUAL_FILE : 0);
            return new CachedLink(linkType, linkAddress, targetLinkAddress, flags);
        }

        static CachedLink cacheUndefinedLink(@NotNull MdLinkType linkType, @NotNull String linkAddress, boolean isMarkdown) {
            byte flags = (byte) (isMarkdown ? F_UNDEFINED | F_UNDEFINED_MARKDOWN : F_UNDEFINED);
            return new CachedLink(linkType, linkAddress, "", flags);
        }

        CachedLink(@NotNull MdLinkType linkType, @NotNull String linkAddress, @NotNull String targetLinkAddress, byte flags) {
            myLinkType = linkType;
            myLinkAddress = linkAddress;
            myTargetLinkAddress = targetLinkAddress;
            myFlags = flags;
        }

        boolean isVirtualFile() {
            return (myFlags & F_VIRTUAL_FILE) != 0;
        }

        boolean isProjectFile() {
            return (myFlags & F_PROJECT_FILE) != 0;
        }

        boolean isUndefined() {
            return (myFlags & F_UNDEFINED) != 0;
        }

        boolean isMarkdown() {
            return (myFlags & F_UNDEFINED_MARKDOWN) != 0;
        }

        boolean isValid() {
            // if file URI check if resolves to file or directory
            if (isVirtualFile()) {
                return FILE_MANAGER.findFileByUrl(myTargetLinkAddress) != null;
            }
            return true;
        }

        @Nullable
        VirtualFile getVirtualFile() {
            // if file URI and resolves to virtual file
            if (isVirtualFile()) {
                return FILE_MANAGER.findFileByUrl(myTargetLinkAddress);
            }
            return null;
        }
    }

    static class CachedLinkTarget {
        final @NotNull String myTargetLinkAddress;  // resolved target address
        final byte myFlags;

        CachedLinkTarget(@NotNull String targetLinkAddress, byte flags) {
            myTargetLinkAddress = targetLinkAddress;
            myFlags = flags;
        }

        boolean isVirtualFile() {
            return (myFlags & F_VIRTUAL_FILE) != 0;
        }

        boolean isProjectFile() {
            return (myFlags & F_PROJECT_FILE) != 0;
        }

        boolean isUndefined() {
            return (myFlags & F_UNDEFINED) != 0;
        }

        boolean isMarkdown() {
            return (myFlags & F_UNDEFINED_MARKDOWN) != 0;
        }

        boolean isValid() {
            // if file URI check if resolves to file or directory
            if (isVirtualFile()) {
                return FILE_MANAGER.findFileByUrl(myTargetLinkAddress) != null;
            }
            return true;
        }

        @Nullable
        VirtualFile getVirtualFile() {
            // if file URI and resolves to virtual file
            if (isVirtualFile()) {
                return FILE_MANAGER.findFileByUrl(myTargetLinkAddress);
            }
            return null;
        }
    }

    static class CachedLinkData {
        final @NotNull ArrayList<CachedLinkTarget> myCachedLinkTargets = new ArrayList<>();     // holds target link address & flags
        final @NotNull HashMap<String, Integer> myCachedLinkTargetIndexMap = new HashMap<>();   // holds paths of all virtual file targets to indices

        final @NotNull HashMap<String, Integer> myLinks = new HashMap<>();          // linkAddress to index into cachedLinkTargets
        final @NotNull HashMap<String, Integer> myImages = new HashMap<>();         // linkAddress to index into cachedLinkTargets
        final @NotNull HashMap<String, Integer> myWikis = new HashMap<>();          // linkAddress to index into cachedLinkTargets
        final @NotNull HashSet<String> myUndefinedExtensions = new HashSet<>();     // undefined extensions
        final @NotNull HashSet<String> myUndefinedNames = new HashSet<>();          // undefined file names with extensions

        @NotNull String myFilePath = "";
        boolean myHaveUndefinedMarkdown = false;
        boolean myIsValid = true;
        boolean myNextIsValid = true;

        CachedLinkData() {
        }

        void copyFrom(@NotNull CachedLinkData other) {
            myFilePath = other.myFilePath;
            myHaveUndefinedMarkdown = other.myHaveUndefinedMarkdown;
            myIsValid = other.myIsValid;
            myNextIsValid = other.myNextIsValid;

            myCachedLinkTargets.clear();
            myCachedLinkTargets.addAll(other.myCachedLinkTargets);

            myCachedLinkTargetIndexMap.clear();
            myCachedLinkTargetIndexMap.putAll(other.myCachedLinkTargetIndexMap);

            myLinks.clear();
            myLinks.putAll(other.myLinks);

            myImages.clear();
            myImages.putAll(other.myImages);

            myWikis.clear();
            myWikis.putAll(other.myWikis);

            myUndefinedExtensions.clear();
            myUndefinedExtensions.addAll(other.myUndefinedExtensions);

            myUndefinedNames.clear();
            myUndefinedNames.addAll(other.myUndefinedNames);
        }

        int addCachedLinkTarget(@NotNull String targetLinkAddress, byte flags) {
            int index = myCachedLinkTargetIndexMap.computeIfAbsent(targetLinkAddress, k -> {
                int i = myCachedLinkTargets.size();
                myCachedLinkTargets.add(new CachedLinkTarget(k, flags));
                return i;
            });

            return index;
        }

        @Nullable
        CachedLink getCachedLink(@NotNull MdLinkType linkType, @NotNull String linkAddress, @Nullable Integer index) {
            if (index != null) {
                if (index >= 0) {
                    CachedLinkTarget target = myCachedLinkTargets.get(index);
                    if (target != null) {
                        return new CachedLink(linkType, linkAddress, target.myTargetLinkAddress, target.myFlags);
                    }
                } else {
                    // undefined link
                    return new CachedLink(linkType, linkAddress, "", index == -2 ? (byte) (F_UNDEFINED | F_UNDEFINED_MARKDOWN) : F_UNDEFINED);
                }
            }
            return null;
        }

        @Nullable
        CachedLink getCachedLink(@NotNull MdLinkType linkType, @NotNull String linkAddress) {
            HashMap<String, Integer> cachedLinks = getCachedLinkMap(linkType);
            return getCachedLink(linkType, linkAddress, cachedLinks.get(linkAddress));
        }

        /**
         * Test if link address has a cached link
         * NOTE: Cannot just test is link address exists in corresponding index map. The cached link target at that index could have been removed.
         *
         * @param linkType    link type
         * @param linkAddress link address
         *
         * @return true if have cached key
         */
        boolean hasCachedLink(@NotNull MdLinkType linkType, @NotNull String linkAddress) {
            return getCachedLink(linkType, linkAddress) != null;
        }

        boolean addCachedLink(@NotNull CachedLink cachedLink) {
            HashMap<String, Integer> cachedLinks = getCachedLinkMap(cachedLink.myLinkType);
            if (getCachedLink(cachedLink.myLinkType, cachedLink.myLinkAddress) == null) {
                int index;

                if (cachedLink.isUndefined()) {
                    if (cachedLink.isMarkdown()) myHaveUndefinedMarkdown = true;

                    PathInfo pathInfo = new PathInfo(cachedLink.myLinkAddress);
                    myUndefinedExtensions.add(pathInfo.getExt());
                    myUndefinedNames.add(pathInfo.getFileNameNoQuery());

                    index = cachedLink.isMarkdown() ? -2 : -1;
                } else {
                    index = addCachedLinkTarget(cachedLink.myTargetLinkAddress, cachedLink.myFlags);
                }

                cachedLinks.put(cachedLink.myLinkAddress, index);
                return true;
            }

            return false;
        }

        HashMap<String, Integer> getCachedLinkMap(MdLinkType linkType) {
            switch (linkType) {
                case LINK:
                    return myLinks;
                case IMAGE:
                    return myImages;
                case WIKI:
                    return myWikis;
                default:
                    throw new IllegalStateException("Unhandled MdLinkType: " + linkType);
            }
        }

        /**
         * NOTE: only removes link address from links, images or wikis map to facilitate log of links no longer on the page
         * does not create a valid cached link data state for all other flags and values
         *
         * @param cachedLink cached link to remove
         */
        void removeCachedLink(@NotNull CachedLink cachedLink) {
            HashMap<String, Integer> cachedLinks = getCachedLinkMap(cachedLink.myLinkType);
            cachedLinks.remove(cachedLink.myLinkAddress);
        }

        boolean isNewVirtualFile(CachedLink cachedLink) {
            return !myCachedLinkTargetIndexMap.containsKey(cachedLink.myTargetLinkAddress);
        }

        boolean isNewVirtualFile(@NotNull String targetUrl) {
            return !myCachedLinkTargetIndexMap.containsKey(targetUrl);
        }

        public void forAllCachedLinks(MdLinkType linkType, Consumer<CachedLink> consumer) {
            HashMap<String, Integer> cachedLinks = getCachedLinkMap(linkType);
            for (Map.Entry<String, Integer> entry : cachedLinks.entrySet()) {
                CachedLink cachedLink = getCachedLink(linkType, entry.getKey(), entry.getValue());
                if (cachedLink != null) {
                    consumer.accept(cachedLink);
                }
            }
        }

        public boolean removeCachedLinkIf(MdLinkType linkType, Predicate<Map.Entry<String, Integer>> consumer) {
            HashMap<String, Integer> cachedLinks = getCachedLinkMap(linkType);
            return cachedLinks.entrySet().removeIf(consumer);
        }

        public boolean hasUndefinedLinks() {
            return !myUndefinedNames.isEmpty();
        }

        /**
         * Removes the string from cached link target index map and sets old index to null
         * NOTE: does not remove the linkAddresses which map to this index.
         * Getting cached link for those addresses will result in null, meaning no key
         *
         * @param targetLinkUrl target url to remove
         *
         * @return true if there was such a link
         */
        public boolean removeCachedLinkTarget(String targetLinkUrl) {
            Integer index = myCachedLinkTargetIndexMap.get(targetLinkUrl);
            if (index == null) return false;

            myCachedLinkTargets.set(index, null);
            myCachedLinkTargetIndexMap.remove(targetLinkUrl);
            return true;
        }
    }

    /**
     * Persistent links, not invalidated, computes to empty
     */
    final static CachedDataKey<MdFile, CachedLinkData> CACHED_PERSISTENT_LINKS = new CachedDataKey<MdFile, CachedLinkData>("CACHED_PERSISTENT_LINKS") {
        @NotNull
        @Override
        public CachedLinkData compute(@NotNull CachedTransactionContext<MdFile> context) {
            return new CachedLinkData();
        }

        @Override
        public boolean isValid(@NotNull CachedLinkData value) {
            return true;
        }
    };

    @SuppressWarnings("unchecked")
    public static final Class<MdLinkRefElement>[] LINK_REF_CLASSES = new Class[] { MdLinkRefElement.class };

    private static MdApplicationSettings ourApplicationSettings;

    @NotNull
    static MdDebugSettings getDebugSettings() {
        if (ourApplicationSettings == null) {
            ourApplicationSettings = MdApplicationSettings.getInstance();
        }
        return ourApplicationSettings.getDebugSettings();
    }

    /**
     * Cached links, invalidated by file changes, reload resolved links from CACHED_PERSISTENT_LINKS on compute
     */
    final static CachedDataKey<MdFile, CachedLinkData> CACHED_LINKS = new CachedDataKey<MdFile, CachedLinkData>("CACHED_LINKS") {
        @NotNull
        @Override
        public CachedLinkData compute(@NotNull CachedTransactionContext<MdFile> context) {
            MdFile file = context.getDataOwner();

            context.addDependency(file);

            String filePath = file.getVirtualFile().getPath();
            CachedLinkData persistentLinks = CachedData.get(file, CACHED_PERSISTENT_LINKS);
            CachedLinkData cachedLinkData = new CachedLinkData();
            cachedLinkData.myFilePath = filePath;
            PsiManager psiManager = PsiManager.getInstance(file.getProject());

            if (getDebugSettings().getUseFileLinkCache()) {
                if (persistentLinks.myFilePath.isEmpty() || persistentLinks.myFilePath.equals(filePath)) {
                    DelimitedBuilder out = LOG_CACHE_DETAIL.isDebugEnabled() ? new DelimitedBuilder("\n") : null;
                    if (out != null) out.append("Computing CACHED_LINKS: for ").append(filePath).mark();

                    MdCachedFileElements.findChildrenOfAnyType(file, false, false, false, LINK_REF_CLASSES, link -> {
                        String linkText = Escaping.unescapeString(link.getText(), true);

                        if (!linkText.isEmpty()) {
                            MdLinkType linkType;
                            String decodedText;

                            if (link instanceof MdImageLinkRef) {
                                decodedText = ImageLinkRef.urlDecode(linkText);
                                linkType = MdLinkType.IMAGE;
                            } else if (link instanceof MdWikiLinkRef) {
                                decodedText = WikiLinkRef.linkAsFile(linkText);
                                linkType = MdLinkType.WIKI;
                            } else {
                                // treat as link ref
                                decodedText = LinkRef.urlDecode(linkText);
                                linkType = MdLinkType.LINK;
                            }

                            String linkTextForCache = removeSuffix(new PathInfo(decodedText).getFilePathNoQuery(), "/");
                            CachedLink cachedLink = persistentLinks.getCachedLink(linkType, linkTextForCache);
                            if (cachedLink != null) {
                                // NOTE: undefined links get invalidated when file content is regenerated
                                if (!cachedLink.isUndefined()) {
                                    boolean isValid = true;

                                    if (cachedLink.isVirtualFile()) {
                                        VirtualFile virtualFile = cachedLink.getVirtualFile();
                                        if (virtualFile != null && virtualFile.isValid()) {
                                            if (cachedLink.isProjectFile()) {
                                                PsiFile psiFile = psiManager.findFile(virtualFile);
                                                if (psiFile != null) {
                                                    context.addDependency(psiFile);
                                                    if (out != null) out.append("  Adding file dependency on psiFile: ").append(psiFile.getVirtualFile().getPath()).mark();
                                                }
                                            } else {
                                                context.addDependency(virtualFile);
                                                if (out != null) out.append("  Adding file dependency on virtualFile: ").append(virtualFile.getPath()).mark();
                                            }
                                        } else {
                                            isValid = false;
                                        }
                                    }

                                    if (isValid) {
                                        cachedLinkData.addCachedLink(cachedLink);
                                        if (out != null) out.append("  Keeping cached link type: ").append(linkType).append(" link: ").append(linkTextForCache).append(" to ").append(cachedLink.myTargetLinkAddress).mark();
                                    } else {
                                        if (out != null) {
                                            persistentLinks.removeCachedLink(cachedLink);
                                            out.append("  Removing invalidated cached link type: ").append(linkType).append(" link: ").append(linkTextForCache).append(" to ").append(cachedLink.myTargetLinkAddress).mark();
                                        }
                                    }
                                }
                            } else {
                                if (out != null) out.append("  No cached link for type: ").append(linkType).append(" link: ").append(linkTextForCache).mark();
                            }
                        }
                    });

                    if (out != null) {
                        // list what is no longer on the page
                        persistentLinks.forAllCachedLinks(MdLinkType.IMAGE, cachedLink -> {
                            if (!cachedLink.isUndefined() && !cachedLinkData.myImages.containsKey(cachedLink.myLinkAddress)) {
                                out.append("  Removing not in page link type: ").append(MdLinkType.IMAGE).append(" link: ").append(cachedLink.myLinkAddress).append(" to ").append(cachedLink.myTargetLinkAddress).mark();
                            }
                        });

                        persistentLinks.forAllCachedLinks(MdLinkType.WIKI, cachedLink -> {
                            if (!cachedLink.isUndefined() && !cachedLinkData.myWikis.containsKey(cachedLink.myLinkAddress)) {
                                out.append("  Removing not in page link type: ").append(MdLinkType.WIKI).append(" link: ").append(cachedLink.myLinkAddress).append(" to ").append(cachedLink.myTargetLinkAddress).mark();
                            }
                        });

                        persistentLinks.forAllCachedLinks(MdLinkType.LINK, cachedLink -> {
                            if (!cachedLink.isUndefined() && !cachedLinkData.myLinks.containsKey(cachedLink.myLinkAddress)) {
                                out.append("  Removing not in page link type: ").append(MdLinkType.LINK).append(" link: ").append(cachedLink.myLinkAddress).append(" to ").append(cachedLink.myTargetLinkAddress).mark();
                            }
                        });

                        LOG_CACHE_DETAIL.debug(out.toString());
                    }
                } else {
                    HelpersKt.debug(LOG_CACHE, () -> String.format("   No links kept, file path changed from %s to %s", persistentLinks.myFilePath, filePath));
                }

                // NOTE: always have dependency on project files since at any time an undefined link can become defined or dependency could be added or invalidated by content change
                context.addDependency(new LinkProjectFilePredicate(file, cachedLinkData));

                HelpersKt.debug(LOG_CACHE, () -> String.format("Kept CACHED_LINKS: links: %d, images: %d wikis: %d for %s@%x", cachedLinkData.myLinks.size(), cachedLinkData.myImages.size(), cachedLinkData.myWikis.size(), filePath, file.hashCode()));
            }

            persistentLinks.copyFrom(cachedLinkData);
            return cachedLinkData;
        }

        /**
         * Always valid because stores only basic types
         * @param value cached link data
         * @return true
         */
        @Override
        public boolean isValid(@NotNull CachedLinkData value) {
            return true;
        }
    };

    static class LinkProjectFilePredicate extends RestartableProjectFileDependency {
        @NotNull CachedLinkData cachedLinkData;

        public LinkProjectFilePredicate(@NotNull MdFile file, @NotNull CachedLinkData cachedLinkData) {
            super(file);
            this.cachedLinkData = cachedLinkData;
        }

        @Override
        public boolean test(@NotNull PsiFile psiFile) {
            boolean isValid = cachedLinkData.myIsValid;
            VirtualFile virtualFile = psiFile.getVirtualFile();

            if (isValid) {
                if (!cachedLinkData.myFilePath.equals(virtualFile.getPath())) {
                    if (cachedLinkData.isNewVirtualFile(virtualFile.getUrl())) {
                        String extension = virtualFile.getExtension();

                        if (cachedLinkData.hasUndefinedLinks()) {
                            if (cachedLinkData.myUndefinedExtensions.contains(extension)) {
                                isValid = false;
                                HelpersKt.debug(LOG_CACHE, () -> String.format("Invalidating CACHED_LINKS: has undefined ext: %s, for %s", extension, cachedLinkData.myFilePath));
                            }
                        }

                        // NOTE: wiki links can have multi-resolve issues but only for markdown files which they evaluate based on file name without extension, ignoring subdirectory path
                        if (isValid && psiFile.getFileType() == MdFileType.INSTANCE) {
                            if (!cachedLinkData.myWikis.isEmpty()) {
                                // OPTIMIZE: can check if undefined names or defined wiki link address can possibly resolve to psiFile in question
                                isValid = false;

                                // NOTE: need to remove all wiki links so they all get re-computed
                                cachedLinkData.getCachedLinkMap(MdLinkType.WIKI).clear();
                                HelpersKt.debug(LOG_CACHE, () -> String.format("Invalidating CACHED_LINKS: have wiki links: %d, for %s@%x", cachedLinkData.myWikis.size(), cachedLinkData.myFilePath, myFile.hashCode()));
                            }
                        }
                    }
                }
            }

            if (!cachedLinkData.myNextIsValid) {
                isValid = false;
            }

            if (!isValid) {
                // need to remove any links which point to this file so they are re-computed just in case
                if (cachedLinkData.removeCachedLinkTarget(virtualFile.getUrl())) {
                    // Not a new dependency, this is a light virtual file need to check if timestamp changed
                    long modificationCount = virtualFile.getModificationCount();
                    long modificationStamp = virtualFile.getModificationStamp();
                    HelpersKt.debug(LOG_CACHE, () -> String.format("Removing link targets for file: %s, modCount: %d, modTimestamp: %d in %s@%x", virtualFile.getName(), modificationCount, modificationStamp, myFile.getName(), myFile.hashCode()));

                    CachedLinkData persistentLinks = CachedData.get((MdFile) myFile, CACHED_PERSISTENT_LINKS);
                    persistentLinks.copyFrom(cachedLinkData);
                }

                cachedLinkData.myIsValid = false;
                cachedLinkData.myNextIsValid = false;
            }

//            Project project = psiFile.getProject();
//            if (!isValid && !project.isDisposed()) {
//                ApplicationManager.getApplication().invokeLater(() -> {
//                    if (!project.isDisposed()) {
//                        DaemonCodeAnalyzer.getInstance(project).restart(myFile);
//                        HelpersKt.debug(LOG_CACHE, () -> String.format("CachedLinkData: Restarted code analyzer for %s", cachedLinkData.myFilePath));
//                    }
//                });
//            }

            return isValid;
        }
    }

    public static boolean hasCachedLink(@NotNull MdFile containingFile, @NotNull LinkRef linkRef) {
        if (getDebugSettings().getUseFileLinkCache()) {
            String linkRefFilePath = linkRef.getFilePath();

            if (linkRefFilePath.isEmpty()) {
                return false;
            } else {
                String linkRefPathForCache = removeSuffix(linkRef.linkToFile(linkRef.getFilePathNoQuery()), "/");
                MdLinkType linkType = getLinkType(linkRef);
                return CachedData.get(containingFile, CACHED_LINKS).getCachedLink(linkType, linkRefPathForCache) != null;
            }
        }
        return false;
    }

    @NotNull
    private static MdLinkType getLinkType(@NotNull LinkRef linkRef) {
        MdLinkType linkType;
        if (linkRef instanceof ImageLinkRef) linkType = MdLinkType.IMAGE;
        else if (linkRef instanceof WikiLinkRef) linkType = MdLinkType.WIKI;
        else linkType = MdLinkType.LINK;
        return linkType;
    }

    @Nullable
    public static PathInfo getCachedLink(@NotNull MdFile containingFile, @NotNull LinkRef linkRef) {
        String linkRefFilePath = linkRef.isNormalized() ? linkRef.getFilePath() : Escaping.unescapeString(linkRef.getFilePath());

        if (linkRefFilePath.isEmpty()) {
            return null;
        } else if (getDebugSettings().getUseFileLinkCache()) {
            String linkRefPathForCache = removeSuffix(linkRef.linkToFile(linkRef.getFilePathNoQuery()), "/");
            MdLinkType linkType = getLinkType(linkRef);

            CachedLink cachedLink = CachedData.get(containingFile, CACHED_LINKS).getCachedLink(linkType, linkRefPathForCache);
            if (cachedLink != null && !cachedLink.isUndefined()) {
                if (cachedLink.isVirtualFile()) {
                    VirtualFile virtualFile = cachedLink.getVirtualFile();
                    if (virtualFile != null && virtualFile.isValid()) {
                        HelpersKt.debug(LOG_CACHE_DETAIL, () -> String.format("Got cached file type: %s link: %s to %s", linkType, linkRefPathForCache, cachedLink.myTargetLinkAddress));
                        if (cachedLink.isProjectFile()) {
                            return new ProjectFileRef(virtualFile, containingFile.getProject());
                        } else {
                            return new FileRef(virtualFile);
                        }
                    } else {
                        HelpersKt.debug(LOG_CACHE_DETAIL, () -> String.format("Skipping cached file type: %s link: %s to %s, no virtual file", linkType, linkRefPathForCache, cachedLink.myTargetLinkAddress));
                    }
                } else {
                    // URL
                    HelpersKt.debug(LOG_CACHE_DETAIL, () -> String.format("Got cached URL type: %s link: %s to %s", linkType, linkRefPathForCache, cachedLink.myTargetLinkAddress));
                    return new PathInfo(cachedLink.myTargetLinkAddress);
                }
            }
        }
        return null;
    }

    public static void addCachedLink(@NotNull MdFile containingFile, @NotNull LinkRef linkRef, @NotNull PathInfo targetRef) {
        String linkRefFilePath = linkRef.isNormalized() ? linkRef.getFilePath() : Escaping.unescapeString(linkRef.getFilePath());

        if (!linkRefFilePath.isEmpty() && getDebugSettings().getUseFileLinkCache()) {
            String linkRefPathForCache = removeSuffix(linkRef.linkToFile(linkRef.getFilePathNoQuery()), "/");
            MdLinkType linkType = getLinkType(linkRef);

            CachedLinkData persistentLinks = CachedData.get(containingFile, CACHED_PERSISTENT_LINKS);
            CachedLinkData cachedLinkData = CachedData.get(containingFile, CACHED_LINKS);
            CachedLink cachedLink;
            boolean updateDependencies = false;

            String targetRefFilePath = targetRef.getFilePath();
            if (targetRef instanceof FileRef) {
                VirtualFile virtualFile = targetRef.getVirtualFile();
                if (virtualFile != null) {
                    updateDependencies = true;
                    cachedLink = CachedLink.cacheLink(linkType, linkRefPathForCache, virtualFile.getUrl(), true, targetRef instanceof ProjectFileRef);
                } else {
                    cachedLink = CachedLink.cacheLink(linkType, linkRefPathForCache, targetRefFilePath, false, false);
                }
            } else {
                cachedLink = CachedLink.cacheLink(linkType, linkRefPathForCache, targetRefFilePath, false, false);
            }

            if (updateDependencies) {
                // NOTE: Need to invalidate the data so that new dependencies will be used but only if not already in the link
                if (cachedLinkData.isNewVirtualFile(cachedLink)) {
                    // invalidate on next project file notification
                    cachedLinkData.myNextIsValid = false;
                    HelpersKt.debug(LOG_CACHE_DETAIL, () -> String.format("Next Invalidation on new dependency for %s link type: %s link: %s to %s", containingFile.getName(), linkType, linkRefPathForCache, cachedLink.myTargetLinkAddress));
                }
            }

            ReentrantLock lock = ProjectCachedData.fileCachedData(containingFile).getKeyLock(CACHED_LINKS);
            boolean cachedLinkAdded;

            try {
                lock.lock();
                cachedLinkAdded = cachedLinkData.addCachedLink(cachedLink);
                persistentLinks.addCachedLink(cachedLink);
            } finally {
                lock.unlock();
            }

            if (cachedLinkAdded) {
                HelpersKt.debug(LOG_CACHE_DETAIL, () -> String.format("Add cached link for %s type: %s link: %s to %s", containingFile.getName(), linkType, linkRefFilePath, cachedLink.myTargetLinkAddress));
            }
        }
    }

    public static void addUndefinedCachedLink(@NotNull MdFile containingFile, @NotNull LinkRef linkRef) {
        String linkRefFilePath = linkRef.isNormalized() ? linkRef.getFilePath() : Escaping.unescapeString(linkRef.getFilePath());

        if (!linkRefFilePath.isEmpty() && getDebugSettings().getUseFileLinkCache()) {
            String linkRefPathForCache = removeSuffix(linkRef.linkToFile(linkRef.getFilePathNoQuery()), "/");
            MdLinkType linkType = getLinkType(linkRef);

            CachedLinkData cachedLinkData = CachedData.get(containingFile, CACHED_LINKS);

            if (!cachedLinkData.hasCachedLink(linkType, linkRefPathForCache)) {
                CachedLinkData persistentLinks = CachedData.get(containingFile, CACHED_PERSISTENT_LINKS);

                // NOTE: using the cached data set/data key lock so only compute or mods to this data key for this file will block another thread
                ReentrantLock lock = ProjectCachedData.fileCachedData(containingFile).getKeyLock(CACHED_LINKS);
                boolean cachedLinkAdded;

                try {
                    lock.lock();
                    // FIX: markdown extensions can be added so this test is insufficient. need to test FileTypeRegistry for extension being markdown
                    CachedLink cachedLink = CachedLink.cacheUndefinedLink(linkType, linkRefPathForCache, linkRef.isMarkdownExt());
                    cachedLinkAdded = cachedLinkData.addCachedLink(cachedLink);
                    persistentLinks.addCachedLink(cachedLink);
                } finally {
                    lock.unlock();
                }

                if (cachedLinkAdded) {
                    HelpersKt.debug(LOG_CACHE_DETAIL, () -> String.format("Add undefined cached link type: %s link: %s", linkType, linkRefPathForCache));
                }
            }
        }
    }
}
