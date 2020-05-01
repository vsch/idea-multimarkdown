// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package icons;

import com.vladsch.plugin.util.HelpersKt;
import com.vladsch.plugin.util.ui.Helpers;

import javax.swing.Icon;
import java.util.HashMap;

public class FlexmarkIcons {
    static Icon load(String path) {
        return Helpers.load(path, MdIcons.class);
    }

    public static final Icon EXTENSION = load("/icons/svg/application-flexmark-extension.svg");

    public static final HashMap<Icon, String> iconNamesMap = new HashMap<>();

    public static HashMap<Icon, String> getIconNamesMap() {
        if (iconNamesMap.isEmpty()) {
            iconNamesMap.put(EXTENSION, "FlexmarkIcons.EXTENSION");
            iconNamesMap.put(Element.SECTION, "FlexmarkIcons.Element.SECTION");
            iconNamesMap.put(Element.SECTION_FAIL, "FlexmarkIcons.Element.SECTION_FAIL");
            iconNamesMap.put(Element.FLEXMARK_SPEC, "FlexmarkIcons.Element.FLEXMARK_SPEC");
            iconNamesMap.put(Element.HIDDEN_FLEXMARK_SPEC, "FlexmarkIcons.Element.HIDDEN_FLEXMARK_SPEC");
            iconNamesMap.put(Element.MULTI_FLEXMARK_SPEC, "FlexmarkIcons.Element.MULTI_FLEXMARK_SPEC");
            iconNamesMap.put(Element.FLEXMARK_SPEC_ERRORS, "FlexmarkIcons.Element.FLEXMARK_SPEC_ERRORS");
            iconNamesMap.put(Element.HIDDEN_FLEXMARK_SPEC_ERRORS, "FlexmarkIcons.Element.HIDDEN_FLEXMARK_SPEC_ERRORS");
            iconNamesMap.put(Element.MULTI_FLEXMARK_SPEC_ERRORS, "FlexmarkIcons.Element.MULTI_FLEXMARK_SPEC_ERRORS");
            iconNamesMap.put(Element.SPEC_EXAMPLE, "FlexmarkIcons.Element.SPEC_EXAMPLE");
            iconNamesMap.put(Element.HIDDEN_SPEC_EXAMPLE, "FlexmarkIcons.Element.HIDDEN_SPEC_EXAMPLE");
            iconNamesMap.put(Element.MULTI_SPEC_EXAMPLE, "FlexmarkIcons.Element.MULTI_SPEC_EXAMPLE");
            iconNamesMap.put(Element.FLEXMARK_SPEC_EXAMPLE, "FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE");
            iconNamesMap.put(Element.SPEC_EXAMPLE_IGNORED, "FlexmarkIcons.Element.SPEC_EXAMPLE_IGNORED");
            iconNamesMap.put(Element.SPEC_EXAMPLE_ERRORS, "FlexmarkIcons.Element.SPEC_EXAMPLE_ERRORS");
            iconNamesMap.put(Element.MULTI_SPEC_EXAMPLE_ERRORS, "FlexmarkIcons.Element.MULTI_SPEC_EXAMPLE_ERRORS");
            iconNamesMap.put(Element.FLEXMARK_SPEC_EXAMPLE_FAIL, "FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_FAIL");
            iconNamesMap.put(Element.FLEXMARK_SPEC_EXAMPLE_TIMED, "FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_TIMED");
            iconNamesMap.put(Element.FLEXMARK_SPEC_EXAMPLE_FILE_EOL, "FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_FILE_EOL");
            iconNamesMap.put(Element.FLEXMARK_SPEC_EXAMPLE_NO_FILE_EOL, "FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_NO_FILE_EOL");

            iconNamesMap.put(IntentionActions.FlexmarkSpec, "FlemarkIcons.IntentionActions.FlexmarkSpec`");
        }

        return iconNamesMap;
    }

    public static class Element {
        public static final Icon SECTION = load("/icons/svg/application-spec-section.svg");
        public static final Icon SECTION_FAIL = load("/icons/svg/application-spec-section-fail.svg");
        public static final Icon FLEXMARK_SPEC = load("/icons/svg/application-flexmark-spec.svg");
        public static final Icon HIDDEN_FLEXMARK_SPEC = load("/icons/svg/application-flexmark-spec-hidden.svg");
        public static final Icon MULTI_FLEXMARK_SPEC = load("/icons/svg/application-flexmark-spec-multi.svg");
        public static final Icon FLEXMARK_SPEC_ERRORS = load("/icons/svg/application-spec-errors.svg");
        public static final Icon HIDDEN_FLEXMARK_SPEC_ERRORS = load("/icons/svg/application-spec-errors-hidden.svg");
        public static final Icon MULTI_FLEXMARK_SPEC_ERRORS = load("/icons/svg/application-spec-errors-multi.svg");
        public static final Icon SPEC_EXAMPLE = load("/icons/svg/application-spec-example.svg");
        public static final Icon HIDDEN_SPEC_EXAMPLE = load("/icons/svg/application-spec-example-hidden.svg");
        public static final Icon MULTI_SPEC_EXAMPLE = load("/icons/svg/application-spec-example-multi.svg");
        public static final Icon FLEXMARK_SPEC_EXAMPLE = load("/icons/svg/application-spec-example.svg");
        public static final Icon SPEC_EXAMPLE_IGNORED = load("/icons/svg/application-spec-example-ignored.svg");
        public static final Icon SPEC_EXAMPLE_ERRORS = load("/icons/svg/application-spec-example-errors.svg");
        public static final Icon MULTI_SPEC_EXAMPLE_ERRORS = load("/icons/svg/application-spec-example-errors-multi.svg");
        public static final Icon FLEXMARK_SPEC_EXAMPLE_FAIL = load("/icons/svg/application-spec-example-fail.svg");
        public static final Icon FLEXMARK_SPEC_EXAMPLE_TIMED = load("/icons/svg/application-spec-example-timed.svg");
        public static final Icon FLEXMARK_SPEC_EXAMPLE_FILE_EOL = load("/icons/svg/application-spec-example-file-eol.svg");
        public static final Icon FLEXMARK_SPEC_EXAMPLE_NO_FILE_EOL = load("/icons/svg/application-spec-example-no-file-eol.svg");
    }

    public static class IntentionActions {
        public static final Icon FlexmarkSpec = MdIcons.load("/icons/svg/intention-action-flexmark-spec.svg"); // 16x16

        private static final HashMap<String, Icon> intentionActionsMap = new HashMap<String, Icon>();
        static {
            intentionActionsMap.put(MdIcons.MULTIMARKDOWN + "/Flexmark Spec", FlexmarkSpec);
        }
        public static Icon getCategoryIcon(String[] category) {
            String categoryName = HelpersKt.splice(category, "/");
            if (intentionActionsMap.containsKey(categoryName)) {
                return intentionActionsMap.get(categoryName);
            }
            return FlexmarkSpec;
        }
    }
}
