
/*
 * Copyright 2013 Square, Inc.
 * No license information provided
 *
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is based on code from https://github.com/jawspeak/intellij-plugin-copy-and-open-github-url
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

import com.google.common.annotations.VisibleForTesting;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class GitHubVcsRoot {
    private static final Logger logger = org.apache.log4j.Logger.getLogger(GitHubVcsRoot.class);
    final private static Pattern INI_CATEGORY = Pattern.compile("\\[\\s*(\\w+)[\\s'\"]+(\\w+)[\\s'\"]+\\]");
    final private static Pattern URL_VALUE = Pattern.compile("\\s*url\\s*=\\s*([^\\s]*)\\.git");
    final protected static String GIT_CONFIG = "config";

    @NotNull private final String gitHubBaseUrl;
    @NotNull private final String basePath;
    @NotNull private final String projectBasePath;
    private final boolean isWiki;

    @VisibleForTesting
    protected GitHubVcsRoot(@NotNull String gitHubBaseUrl, @NotNull String basePath) {
        // strip out username if the url contains @ from URL
        // regex: ^(https?://)(?:[^@]*\Q@\E)(.*)$
        // replace with $1$2
        String _gitHubBaseUrl = gitHubBaseUrl;
        int atPos = _gitHubBaseUrl.indexOf('@', 7);
        if ((_gitHubBaseUrl.startsWith("http://") || _gitHubBaseUrl.startsWith("https://")) && atPos > 0) {
            int prefixPos = _gitHubBaseUrl.startsWith("http://") ? "http://".length() : "https://".length();
            _gitHubBaseUrl = _gitHubBaseUrl.substring(0, prefixPos) + _gitHubBaseUrl.substring(atPos+1);
            //logger.info("cleaned username from url " + gitHubBaseUrl + " -> " + _gitHubBaseUrl);
        }

        this.gitHubBaseUrl = StringUtilKt.suffixWith(_gitHubBaseUrl, '/');
        this.basePath = StringUtilKt.suffixWith(basePath, '/');
        this.isWiki = new FileRef(this.basePath + "Home.md").isUnderWikiDir();
        this.projectBasePath = this.isWiki ? new PathInfo(this.basePath).getPath() : this.basePath;
    }

    public boolean isWiki() {
        return isWiki;
    }

    @NotNull
    public String getBasePath() {
        return basePath;
    }

    @NotNull
    public String getMainRepoBaseDir() {
        return projectBasePath;
    }

    @NotNull
    public String getBaseUrl() {
        return gitHubBaseUrl;
    }

    @Nullable
    public String getRelativePath(@Nullable String path) {
        if (path != null && path.startsWith(basePath)) {
            return path.substring(basePath.length());
        }
        return null;
    }

    @NotNull
    public String urlForVcsRemote(String relativeFilePath, @Nullable String branchOrTag, @Nullable String gitHubLink) {
        return urlForVcsRemote(relativeFilePath, null, branchOrTag, gitHubLink);
    }

    @Nullable
    public String urlForVcsRemote(@NotNull VirtualFile virtualFile, boolean withExtension, @Nullable String anchor, @Nullable String branchOrTag, @Nullable String gitHubLink) {
        PathInfo pathInfo = new PathInfo(virtualFile.getPath());
        String relativePath = getRelativePath(withExtension ? pathInfo.getFilePath() : pathInfo.getFilePathNoExt());
        return relativePath == null ? null : urlForVcsRemote(relativePath, anchor, branchOrTag, gitHubLink);
    }

    @Nullable
    public String urlForVcsRemote(@NotNull FileRef fileRef, boolean withExtension, @Nullable String anchor, @Nullable String branchOrTag, @Nullable String gitHubLink) {
        String relativePath = !fileRef.isUnderWikiDir() || withExtension ? getRelativePath(fileRef.getFilePath()) : fileRef.getFileNameNoExt();
        if (isWiki && relativePath != null && relativePath.equals("Home")) relativePath = "";
        return relativePath == null ? null : urlForVcsRemote(relativePath, anchor, branchOrTag, gitHubLink);
    }

    public String urlForVcsRemote(@NotNull String relativeFilePath, @Nullable String anchor, @Nullable String branchOrTag, @Nullable String gitHubLink) {
        if (isWiki() && relativeFilePath.startsWith("../../wiki")) {
            relativeFilePath = StringUtilKt.removeStart(relativeFilePath, "../../wiki");
        }

        if (branchOrTag == null || branchOrTag.isEmpty()) branchOrTag = "master";
        if (gitHubLink == null || gitHubLink.isEmpty()) gitHubLink = "blob";

        return gitHubBaseUrl + (isWiki() ? "wiki/" : gitHubLink + "/" + branchOrTag + "/") + LinkRef.urlEncode(StringUtilKt.removeStart(relativeFilePath, "./")) + StringUtilKt.prefixWith(anchor, '#', false);
    }

    @Nullable
    protected static String getGitPath(@NotNull String filePath) {
        PathInfo filePathInfo = new PathInfo(filePath).append(".git");
        File gitFile = new File(filePathInfo.getFilePath());
        String gitPath = null;

        if (gitFile.exists()) {
            if (gitFile.isFile()) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(gitFile));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // gitdir: ../.git/modules/laravel-translation-manager.isWiki
                        if (line.startsWith("gitdir:")) {
                            line = line.substring("gitdir:".length()).trim();
                            PathInfo lineInfo = new PathInfo(line);
                            PathInfo configInfo = lineInfo.isRelative() ? filePathInfo.append(line) : lineInfo;
                            gitPath = configInfo.getFilePath();
                            break;
                        }
                    }
                } catch (IOException ignored) {
                    logger.info("Could not read " + gitFile, ignored);
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            } else {
                return filePathInfo.getFilePath();
            }
        }
        return gitPath;
    }

    @Nullable
    protected static String getBaseUrl(@NotNull File gitConfigFile) {
        String baseUrl = null;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(gitConfigFile));
            String line;
            boolean inRemoteOriginSection = false;
            while ((line = reader.readLine()) != null) {
                if (line.matches("\\s*#")) continue;
                Matcher matcher = INI_CATEGORY.matcher(line);
                if (matcher.matches()) {
                    inRemoteOriginSection = "remote".equals(matcher.group(1))
                            && "origin".equals(matcher.group(2));
                    continue;
                }
                matcher = URL_VALUE.matcher(line);
                if (inRemoteOriginSection && matcher.matches()) {
                    baseUrl = "https://" + matcher.group(1)
                            .replaceAll("git://|git@|https://", "")
                            .replaceAll(":", "/");

                    if (baseUrl.endsWith(PathInfo.WIKI_HOME_DIR_EXTENSION)) {
                        FileRef baseUrlInfo = new FileRef(baseUrl);
                        baseUrl = baseUrlInfo.getFilePathNoExt();
                    }
                    break;
                }
            }
        } catch (IOException ignored) {
            logger.info("No remote origin in " + gitConfigFile, ignored);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return baseUrl;
    }

    @Nullable
    public static GitHubVcsRoot getGitHubVcsRoot(@Nullable String path, @Nullable String basePath) {
        if (path == null || basePath == null) return null;

        String nextPath = path;
        do {
            PathInfo pathInfo = new PathInfo(nextPath);

            String gitPath = getGitPath(pathInfo.getFilePath());
            if (gitPath != null) {
                File gitConfigFile = new File(gitPath, GIT_CONFIG);
                if (gitConfigFile.exists() && gitConfigFile.isFile()) {
                    String baseUrl = getBaseUrl(gitConfigFile);
                    if (baseUrl != null) {
                        return new GitHubVcsRoot(baseUrl, pathInfo.getFilePath());
                    }

                    // this sub-module does not have a remote.
                    return null;
                }
            }

            nextPath = pathInfo.getPath();
        } while (!nextPath.isEmpty() && !nextPath.equals("/") && !nextPath.equalsIgnoreCase(basePath));

        return null;
    }
}
