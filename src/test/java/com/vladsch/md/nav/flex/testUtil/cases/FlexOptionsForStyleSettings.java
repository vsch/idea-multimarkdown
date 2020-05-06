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

package com.vladsch.md.nav.flex.testUtil.cases;

import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.flex.parser.MdSpecExampleStripTrailingSpacesExtension;
import com.vladsch.md.nav.flex.settings.FlexmarkDebugSettings;
import com.vladsch.md.nav.flex.settings.FlexmarkHtmlSettings;
import com.vladsch.md.nav.parser.MdLexParser;
import com.vladsch.md.nav.settings.TrailingSpacesType;
import com.vladsch.md.nav.testUtil.cases.MdOptionsForStyleSettings;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public interface FlexOptionsForStyleSettings extends MdOptionsForStyleSettings {
    Map<String, DataHolder> optionsMap = new HashMap<>();

    static Map<String, DataHolder> getOptionsMap() {
        synchronized (optionsMap) {
            if (optionsMap.isEmpty()) {
                optionsMap.putAll(MdOptionsForStyleSettings.getOptionsMap());

                // style settings
                optionsMap.put("spec-keep-none", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.setTrailingSpacesOption(MdSpecExampleStripTrailingSpacesExtension.OPTION_ID, TrailingSpacesType.KEEP_NONE.intValue)));
                optionsMap.put("spec-keep-all", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.setTrailingSpacesOption(MdSpecExampleStripTrailingSpacesExtension.OPTION_ID, TrailingSpacesType.KEEP_ALL.intValue)));
                optionsMap.put("spec-keep-break", new MutableDataSet().set(STYLE_SETTINGS, (it) -> it.setTrailingSpacesOption(MdSpecExampleStripTrailingSpacesExtension.OPTION_ID, TrailingSpacesType.KEEP_LINE_BREAK.intValue)));

                // flexmark spec example option
                optionsMap.put("flexmark-spec", new MutableDataSet()
                        .set(PARSER_SETTINGS, s -> s.setOptionsFlags(s.getOptionsFlags() | MdLexParser.FLEXMARK_FRONT_MATTER))
                        .set(APPLICATION_SETTINGS, s -> s.getDebugSettings().getExtension(FlexmarkDebugSettings.KEY).setEnableFlexmarkFeatures(true))
                        .set(HTML_SETTINGS, s -> {
                            FlexmarkHtmlSettings flexmarkHtmlSettings = s.getExtension(FlexmarkHtmlSettings.KEY);
                            flexmarkHtmlSettings.getFlexmarkSectionLanguages().putAll(FlexmarkHtmlSettings.DEFAULT_SECTION_LANGUAGES);
                        }));
            }
            return optionsMap;
        }
    }

    static DataHolder tocTitleOption(@Nullable String params) {
        if (params != null) {
            return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.TOC_TITLE = params);
        }

        throw new IllegalStateException("'type' option requires non-empty text argument");
    }

    static DataHolder marginOption(@Nullable Integer params) {
        int value = params != null ? params : -1;
        return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.RIGHT_MARGIN = value);
    }

    static DataHolder indentSizeOption(@Nullable Integer params) {
        int value = params != null ? params : -1;
        return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.INDENT_SIZE = value);
    }

    static DataHolder tabSizeOption(@Nullable Integer params) {
        int value = params != null ? params : -1;
        return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.TAB_SIZE = value);
    }

    static DataHolder keepBlankLinesOption(@Nullable Integer params) {
        int value = params != null ? params : -1;
        return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.KEEP_BLANK_LINES = value);
    }

    static DataHolder codeFenceMarkerLengthOption(@Nullable Integer params) {
        int value = params != null ? params : -1;
        return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.CODE_FENCE_MARKER_LENGTH = value);
    }

    static DataHolder definitionMarkerSpacesOption(@Nullable Integer params) {
        int value = params != null ? params : -1;
        return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.DEFINITION_MARKER_SPACES = value);
    }

    static DataHolder tocHeadingLevelsOption(@Nullable Integer params) {
        int value = params != null ? params : -1;
        return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.TOC_HEADING_LEVELS = value);
    }

    static DataHolder tocTitleLevelOption(@Nullable Integer params) {
        int value = params != null ? params : -1;
        return new MutableDataSet().set(STYLE_SETTINGS, settings -> settings.TOC_TITLE_LEVEL = value);
    }
}
