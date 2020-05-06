/*
 * Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.vladsch.md.nav.testUtil.cases;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.md.nav.language.MdCodeStyleSettings;
import com.vladsch.md.nav.parser.cache.CachedData;
import com.vladsch.md.nav.parser.cache.ProjectFileMonitor;
import com.vladsch.md.nav.parser.cache.data.transaction.IndentingLogger;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.MdRenderingProfileManager;
import com.vladsch.md.nav.testUtil.MdSpecTest;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.renderers.LightFixtureSpecRenderer;
import icons.FlexmarkIcons;
import icons.MdEmojiIcons;
import icons.MdIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.HashMap;
import java.util.Map;

public interface MdCodeInsightFixtureSpecTestCase extends MdOptionsForStyleSettings, CodeInsightFixtureSpecTestCase {
    Map<String, DataHolder> optionsMap = new HashMap<>();

    static Map<String, DataHolder> getOptionsMap() {
        synchronized (optionsMap) {
            if (optionsMap.isEmpty()) {
                optionsMap.putAll(MdOptionsForStyleSettings.getOptionsMap());
                optionsMap.putAll(CodeInsightFixtureSpecTestCase.getOptionsMap());
            }

            return optionsMap;
        }
    }

    static void resetSettings(@NotNull Project project) {
        MdApplicationSettings.getInstance().copyFrom(new MdApplicationSettings(true));
        MdCodeStyleSettings.copySettings(new MdCodeStyleSettings(), MdCodeStyleSettings.getInstance(project));
        MdRenderingProfileManager.getProfile(project).copyFrom(new MdRenderingProfile(), true);

        validateDefaultSettings(project);
    }

    static void validateDefaultSettings(@NotNull Project project) {
        MdApplicationSettings applicationSettings = MdApplicationSettings.getInstance();
        MdRenderingProfile renderingProfile = MdRenderingProfileManager.getProfile(project);

        assert applicationSettings.equals(new MdApplicationSettings(true));
        assert renderingProfile.equals(new MdRenderingProfile());

        MdCodeStyleSettings styleSettings = MdCodeStyleSettings.getInstance(project);
        MdCodeStyleSettings newCodeStyleSettings = new MdCodeStyleSettings();
        assert styleSettings.equals(newCodeStyleSettings) : MdCodeStyleSettings.getDiff(styleSettings, newCodeStyleSettings);
    }

    @NotNull
    static String resolveIcon(@Nullable Icon icon) {
        if (icon == null) return "null";
        // check all icons for match
        String iconName = MdIcons.getIconNamesMap().get(icon);
        if (iconName != null) return iconName;
        iconName = MdEmojiIcons.getIconNamesMap().get(icon);
        if (iconName != null) return iconName;
        iconName = FlexmarkIcons.getIconNamesMap().get(icon);
        if (iconName != null) return iconName;
        return icon.toString();
    }

    @Override
    default String resolveIconName(@Nullable Icon icon) {
        return resolveIcon(icon);
    }

    @Override
    default void beforeDoHighlighting(@NotNull LightFixtureSpecRenderer<?> specRenderer, @NotNull PsiFile file) {
        ProjectFileMonitor fileMonitor = ProjectFileMonitor.getInstance(file.getProject());

        fileMonitor.checkDependencies(true, file);
        HashMap<String, VirtualFile> files = specRenderer.getAdditionalVirtualFiles();
        if (!files.isEmpty()) {
            PsiManager psiManager = PsiManager.getInstance(file.getProject());
            for (VirtualFile virtualFile : files.values()) {
                PsiFile psiFile = psiManager.findFile(virtualFile);
                if (psiFile != null) {
                    fileMonitor.checkDependencies(false, psiFile);
                }
            }
        }
    }

    @Nullable
    StringBuilder getCacheLogs();

    void setCacheLogs(@Nullable StringBuilder cacheLogs);

    default <T extends CodeInsightFixtureSpecTestCase> void augmentRenderer(@NotNull LightFixtureSpecRenderer<T> specRenderer) {

    }

    @Override
    default <T extends CodeInsightFixtureSpecTestCase> void initializeRenderer(@NotNull LightFixtureSpecRenderer<T> specRenderer) {
        DataHolder specRendererOptions = specRenderer.getOptions();

        MdApplicationSettings applicationSettings = MdApplicationSettings.getInstance();
        MdSpecTest.APPLICATION_SETTINGS_OPTION.setInstanceData(applicationSettings, specRendererOptions);

        MdCodeStyleSettings myStyleSettings = MdCodeStyleSettings.getInstance(getProject());
        MdSpecTest.STYLE_SETTINGS_OPTION.setInstanceData(myStyleSettings, specRendererOptions);

        MdRenderingProfile myRenderingProfile = MdRenderingProfileManager.getProfile(getProject());
        MdSpecTest.RENDERING_PROFILE_OPTION.setInstanceData(myRenderingProfile, specRendererOptions);

        // allow settings migration
        myRenderingProfile.validateLoadedSettings();

        augmentRenderer(specRenderer);

        if (MdSpecTest.WANT_CACHE_LOGS.get(specRendererOptions)) {
            setCacheLogs(new StringBuilder());
        }

        IndentingLogger.setWantCacheLogging(MdSpecTest.WANT_CACHE_LOGS.get(specRendererOptions), MdSpecTest.WANT_CACHE_TRACE.get(specRendererOptions), MdSpecTest.WANT_CACHE_TIMESTAMP.get(specRendererOptions));
        CachedData cachedData = CachedData.getInstance();
        cachedData.resetLogId();
        IndentingLogger.setLogCapture(getCacheLogs());
    }

    @Override
    default <T extends CodeInsightFixtureSpecTestCase> void finalizeRenderer(@NotNull LightFixtureSpecRenderer<T> specRenderer, @NotNull DataHolder specRendererOptions) {
        resetSettings(getProject());
    }

    @Override
    default <T extends CodeInsightFixtureSpecTestCase> void renderSpecTestAst(@NotNull StringBuilder ast, @NotNull LightFixtureSpecRenderer<T> specRenderer, @NotNull DataHolder specRendererOptions) {
        if (MdSpecTest.WANT_CACHE_LOGS.get(specRendererOptions)) {
            // need to wait for extra activity to run its course
            ast.append("\n----- Cache Activity -----\n");
            ast.append(getCacheLogs());
        }
    }
}
