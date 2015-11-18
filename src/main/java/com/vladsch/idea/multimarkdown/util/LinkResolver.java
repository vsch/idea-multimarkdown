/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.idea.multimarkdown.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import com.intellij.util.containers.HashSet;
import com.intellij.util.indexing.FileBasedIndex;
import com.vladsch.idea.multimarkdown.MultiMarkdownFileType;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import org.apache.log4j.Logger;
import org.intellij.images.fileTypes.ImageFileTypeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkResolver {
    private static final Logger logger = Logger.getLogger(LinkResolver.class);
    final private static int FLAGS_STOP = 2;
    final private static int FLAGS_ADD = 1;

    protected static final String[] EMPTY_STRINGS = new String[] { };

    enum TargetType {
        ANY, MARKDOWN, WIKI, IMAGE
    }

    enum LinkType {
        LINK, WIKI
    }

    public interface MatchFilter {
        enum Result {
            SKIP(0), ADD(FLAGS_ADD), SKIP_STOP(FLAGS_STOP), ADD_STOP(FLAGS_ADD | FLAGS_STOP);

            int flags;

            Result(int flags) {
                this.flags = flags;
            }

            boolean hasStop() {
                return (flags & FLAGS_STOP) != 0;
            }

            boolean hasAdd() {
                return (flags & FLAGS_ADD) != 0;
            }
        }

        @NotNull Result process(@NotNull LinkResolver resolver, @NotNull VirtualFile file, @Nullable FileReference fileReference);
    }

    public interface MatchComparator {
        @NotNull
        ArrayList<FileReference> sort(@NotNull LinkResolver resolver, @NotNull ArrayList<FileReference> matches);
        @Nullable
        FileReference bestMatch(@NotNull LinkResolver resolver, @NotNull ArrayList<FileReference> matches);
    }

    @NotNull protected final FileReference containingFile;
    @NotNull protected final TargetType targetType;
    @Nullable protected final Project project;
    @NotNull protected final LinkType linkType; // set if link ref is a wiki link ref
    @NotNull protected final FilePathInfo linkRefInfo;
    @NotNull protected final String anchor;
    @NotNull protected final String branchOrTag;

    @Nullable private ArrayList<FileReference> exactMatches;
    @Nullable private ArrayList<FileReference> availableMatches;
    @Nullable private Boolean canResolveLocally;
    @Nullable private Boolean canResolveRemotely;

    @NotNull private LazyCachedValue<LinkReference> bestMatch = new LazyCachedValue<LinkReference>(new LazyCachedValue.Loader<LinkReference>() {
        @Override
        public LinkReference load() {
            return loadBestMatch();
        }
    });

    @NotNull private final LazyCachedValue<GitHubRepo> gitHubRepo = new LazyCachedValue<GitHubRepo>(new LazyCachedValue.Loader<GitHubRepo>() {
        @Override
        public GitHubRepo load() {
            return loadGitHubRepo();
        }
    });

    public LinkResolver(@NotNull FileReference containingFile, @NotNull String linkRef, @NotNull LinkType linkType, @NotNull TargetType targetType, @Nullable String anchor, @Nullable String branchOrTag) {
        this.containingFile = containingFile;
        this.project = containingFile.getProject();
        this.linkRefInfo = new FilePathInfo(linkRef);
        this.linkType = linkType;
        this.targetType = targetType;
        this.anchor = anchor == null ? "" : anchor;
        this.branchOrTag = branchOrTag == null ? "" : branchOrTag;
    }

    // load and cache github repo value for the containingFile or null if there is github repo in the parent path
    @Nullable
    private GitHubRepo loadGitHubRepo() {
        MultiMarkdownProjectComponent projectComponent = getProject() == null ? null : MultiMarkdownPlugin.getProjectComponent(getProject());
        return projectComponent == null ? null : projectComponent.getGitHubRepo(containingFile.getFilePath());
    }

    @Nullable
    public Project getProject() {
        return project == null || project.isDisposed() ? null : project;
    }

    // which target type must use link's extension for exact match
    // images and any type
    public boolean mustHaveExactExtension() {
        return targetType == TargetType.IMAGE || targetType == TargetType.ANY;
    }

    // which target type match one of a set of extensions for a non-exact match
    public boolean mustHaveNotExactExtension() {
        return targetType == TargetType.IMAGE;
    }

    // no types can use exact relative path from the link because all can link from
    // main project to Wiki and vice-versa which will change the subdirectories used.
    // all wiki pages act as if they are in mainProj/wiki/ directory and to reach files in the
    // main project they need ../blob/master/   prefix in their links
    //
    // all main project files act as if they are in mainProj/blob/master/ directory and need
    // ../wiki/ prefix to reach any wiki file
    //
    // this makes subdir part of the link only testable after the actual file is resolved and
    // it can be determined whether it is in a wiki or not.
    //
    // You can also link to other projects in the same account using the relative link
    // for now the plugin will not have this config but it can in the future, use a map from VCS roots to
    // map local relative links to remote relative or absolute liks
    //
    public boolean exactMatchNoWildcardSubDirs() {
        return false;
    }

    @Nullable
    public GitHubRepo getGitHubRepo() {
        return gitHubRepo.get();
    }

    @NotNull
    public String[] typeExtensions() {
        switch (targetType) {
            case MARKDOWN:
                return FilePathInfo.MARKDOWN_EXTENSIONS;
            case WIKI:
                return FilePathInfo.WIKI_PAGE_EXTENSIONS;
            case IMAGE:
                return FilePathInfo.IMAGE_EXTENSIONS;

            case ANY:
            default:
                return EMPTY_STRINGS;
        }
    }

    @NotNull
    protected Pattern getMatchPattern(boolean exactMatch) {
        // return a regex that will match most loosely a file path to be used by this link
        String pattern;
        Pattern matchPattern;

        // we always match subdirectories for markdown and wiki's, even for exact match since if the destination is a wiki page then no directories will be used
        // image target types have no pattern subdirectories but use exact type
        String subDirPattern = "(?:.*)/";

        if (linkType == LinkType.WIKI) {
            // spaces match - and spaces, all subdirectories under Wiki Home match, only WIKI targets accepted, no case sensitivity
            if (exactMatch && !linkRefInfo.isEmpty()) {
                String filenamePattern = "\\Q" + linkRefInfo.getFileName().replaceAll(" ", "-") + "\\E";
                String extensionPattern = "";
                String anchorPattern = "";
                pattern = "^\\Q" + containingFile.getWikiHome() + "\\E" + subDirPattern + filenamePattern + anchorPattern + extensionPattern + "$";
                matchPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            } else {
                String filenamePattern = linkRefInfo.isEmpty() ? "(.*)" : "\\Q" + linkRefInfo.getFileNameNoExt().replaceAll("-| ", "\\E(?:-| )\\Q") + "\\E";
                String extensionPattern = !linkRefInfo.hasExt() ? "" : "(?:\\Q" + linkRefInfo.getExt().replaceAll("-| ", "\\E(?:-| )\\Q") + "\\E)?";
                String anchorPattern = anchor.isEmpty() ? "" : "(?:\\Q#" + anchor.replaceAll("-| ", "\\E(?:-| )\\Q") + "\\E)?";
                pattern = "^\\Q" + containingFile.getWikiHome() + "\\E" + subDirPattern + filenamePattern + anchorPattern + extensionPattern + "$";
                matchPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            }
        } else {
            // this is all other links, no space dash equivalence, %23 in link matches # in file, files under project or gitHubRepoHome will be matched
            // extension pattern is optional added to a list of all extensions for the targetType, with zero or none matching
            // anchor is an optional pattern right after the name so extensions follow, all subdirectories between project or repo home are optional
            // matching is not case sensitive even for exact match because the final could be a wiki page

            // project base, if none then file path
            String prefixPath = getProject() != null && getProject().getBasePath() != null ? getProject().getBasePath() : containingFile.getFilePath();
            String[] typeExtensions = typeExtensions();
            boolean hadExtension = false;
            String extensionPattern = "";

            for (String ext : typeExtensions) {
                if (!extensionPattern.isEmpty()) extensionPattern += "|";
                if (!ext.isEmpty()) {
                    extensionPattern += "(?:\\Q" + ext.replace("%23", "#") + "\\E)";
                    if (ext.equalsIgnoreCase(linkRefInfo.getExt())) hadExtension = true;
                }
            }

            if (!hadExtension && linkRefInfo.hasExt()) {
                if (!extensionPattern.isEmpty()) extensionPattern += "|";
                extensionPattern += "(?:\\Q" + linkRefInfo.getExt().replace("%23", "#") + "\\E)";
            }

            if (!extensionPattern.isEmpty()) extensionPattern = "(?:" + extensionPattern + ")";

            if (exactMatch && !linkRefInfo.isEmpty()) {
                String filenamePattern = "\\Q" + linkRefInfo.getFileNameNoExt().replace("%23", "#") + "\\E";
                String anchorPattern = "";

                // match all extensions for the type by default, if linkref has extension and must have it for exact match
                if (mustHaveExactExtension()) {
                    extensionPattern = !linkRefInfo.hasExt() ? "" : "\\Q" + linkRefInfo.getExt().replace("%23", "#") + "\\E";
                } else {
                    extensionPattern += "?";
                }

                if (exactMatchNoWildcardSubDirs()) {
                    subDirPattern = "";
                    prefixPath = linkRefInfo.getFilePath();
                }

                // if no project or repo base path then prefix is the file path
                pattern = "^\\Q" + prefixPath + "\\E" + subDirPattern + filenamePattern + anchorPattern + extensionPattern + "$";

                matchPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            } else {
                String filenamePattern = linkRefInfo.isEmpty() ? "(.*)" : "\\Q" + linkRefInfo.getFileNameNoExt().replace("%23", "#") + "\\E";
                String anchorPattern = anchor.isEmpty() ? "" : "(?:\\Q#" + anchor + "\\E)?";

                if (!mustHaveNotExactExtension()) extensionPattern += "?";

                pattern = "^\\Q" + prefixPath + "\\E" + subDirPattern + filenamePattern + anchorPattern + extensionPattern + "$";
                matchPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            }
        }

        return matchPattern;
    }

    @NotNull
    protected String getProjectFileType(@Nullable String targetFileType) {
        Project project = getProject();
        if (project == null || (targetFileType == null && targetType == TargetType.ANY)) return "";

        switch (targetType) {
            case WIKI:
            case MARKDOWN:
                targetFileType = String.valueOf(MultiMarkdownFileType.INSTANCE);
                break;

            case IMAGE:
                targetFileType = String.valueOf(ImageFileTypeManager.getInstance().getImageFileType());
                break;

            case ANY:
            default:
                targetFileType = "";
                break;
        }

        return targetFileType;
    }

    @NotNull
    public static ArrayList<FileReference> getMatches(final LinkResolver resolver, final @NotNull Pattern matchPattern, @Nullable ArrayList<FileReference> fromList, @Nullable final MatchFilter matchFilter) {
        String targetFileType = resolver.getProjectFileType("");

        // process the files that match the pattern and put them in the list
        final ArrayList<FileReference> matches = new ArrayList<FileReference>();
        if (fromList == null) {
            final Project project = resolver.getProject();
            if (targetFileType.isEmpty() || project == null) {
                return new ArrayList<FileReference>(0);
            } else {
                Set<String> targetFileTypes = new HashSet<String>();
                targetFileTypes.add(targetFileType);

                if (matchFilter != null) {
                    FileBasedIndex.getInstance().getFilesWithKey(FilenameIndex.NAME, targetFileTypes, new Processor<VirtualFile>() {
                        @Override
                        public boolean process(VirtualFile file) {
                            String filePath = file.getPath();
                            Matcher matcher = matchPattern.matcher(filePath);
                            if (matcher.matches()) {
                                MatchFilter.Result result = matchFilter.process(resolver, file, null);
                                if (result.hasAdd()) matches.add(new FileReference(file, project));
                                return !result.hasStop();
                            }
                            return true;
                        }
                    }, GlobalSearchScope.projectScope(project));
                } else {
                    FileBasedIndex.getInstance().getFilesWithKey(FilenameIndex.NAME, targetFileTypes, new Processor<VirtualFile>() {
                        @Override
                        public boolean process(VirtualFile file) {
                            String filePath = file.getPath();
                            Matcher matcher = matchPattern.matcher(filePath);
                            if (matcher.matches()) {
                                matches.add(new FileReference(file, project));
                            }
                            return true;
                        }
                    }, GlobalSearchScope.projectScope(project));
                }
            }
        } else {
            if (matchFilter != null) {
                for (FileReference fileReference : fromList) {
                    Matcher matcher = matchPattern.matcher(fileReference.getFullFilePath());
                    if (matcher.matches()) {
                        assert fileReference.virtualFile != null : "FileReference already has non-null virtualFile field";
                        MatchFilter.Result result = matchFilter.process(resolver, fileReference.virtualFile, fileReference);
                        if (result.hasAdd()) matches.add(fileReference);
                        if (result.hasStop()) break;
                    }
                }
            } else {
                for (FileReference fileReference : fromList) {
                    Matcher matcher = matchPattern.matcher(fileReference.getFullFilePath());
                    if (matcher.matches()) {
                        matches.add(fileReference);
                    }
                }
            }
        }
        return matches;
    }

    // available filter will allow errors and warnings but keep all that can possibly be used as a link
    // sub-directory differences are ignored, as long as the link is reachable,
    // presence or absence of extension ignored
    // lack of url encoding for # in link ignored
    // space/dash equivalence for wiki
    // matching is not case sensitive even for non-wiki
    // will potentially match anchor from link in file name if that works
    // will match link with no extension to file with a compatible extension for the type
    // see getMatchPattern(false)
    final static MatchFilter GITHUB_AVAILABLE_MATCH_FILTER = new MatchFilter() {
        @Override
        @NotNull
        public Result process(@NotNull LinkResolver resolver, @NotNull VirtualFile file, @Nullable FileReference fileReference) {
            // todo: see if file can be processed
            return Result.ADD;
        }
    };

    // exact filter will not allow errors, only warnings
    // used to get set of matching files for a link, that are matching on loose standards
    // sub-directory differences are ignored, as long as the link is reachable,
    // extension must match if present, ignored if not present
    // lack of url encoding for # in link ignored
    // space/dash equivalence for wiki
    // matching is not case sensitive even for non-wiki
    // will potentially match anchor
    // see getMatchPattern(false)
    final static MatchFilter GITHUB_EXACT_MATCH_FILTER = new MatchFilter() {
        @Override
        @NotNull
        public Result process(@NotNull LinkResolver resolver, @NotNull VirtualFile file, @Nullable FileReference fileReference) {
            return Result.ADD;
        }
    };

    // best filter will filter out the best candidate our of a set
    // used to get the resolved reference
    final static MatchFilter GITHUB_BEST_MATCH_FILTER = new MatchFilter() {
        @Override
        @NotNull
        public Result process(@NotNull LinkResolver resolver, @NotNull VirtualFile file, @Nullable FileReference fileReference) {
            return Result.ADD;
        }
    };

    @NotNull
    public ArrayList<FileReference> getAvailableMatches() {
        if (availableMatches == null) {
            availableMatches = getMatches(this, getMatchPattern(false), null, GITHUB_AVAILABLE_MATCH_FILTER);
        }
        return availableMatches;
    }

    @NotNull
    public ArrayList<FileReference> getExactMatches() {
        if (exactMatches == null) {
            exactMatches = getMatches(this, getMatchPattern(true), availableMatches, GITHUB_EXACT_MATCH_FILTER);
        }
        return exactMatches;
    }

    final static public MatchComparator GITHUB_MATCH_COMPARATOR = new MatchComparator() {
        @NotNull
        @Override
        public ArrayList<FileReference> sort(@NotNull LinkResolver resolver, @NotNull ArrayList<FileReference> matches) {
            return matches;
        }

        @Nullable
        @Override
        public FileReference bestMatch(@NotNull LinkResolver resolver, @NotNull ArrayList<FileReference> matches) {
            return matches.size() > 0 ? matches.get(0) : null;
        }
    };

    @Nullable
    private LinkReference loadBestMatch() {
        ArrayList<FileReference> matches = getMatches(this, getMatchPattern(true), exactMatches != null ? exactMatches : availableMatches, GITHUB_BEST_MATCH_FILTER);
        FileReference bestFileReference = GITHUB_MATCH_COMPARATOR.bestMatch(this, matches);
        if (bestFileReference != null) {
            return new LinkReference(this, bestFileReference);
        }
        return null;
    }

    @Nullable
    public LinkReference getBestMatch() {
        return bestMatch.get();
    }
}
