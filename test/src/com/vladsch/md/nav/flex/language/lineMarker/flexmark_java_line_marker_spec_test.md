---
title: Flexmark Line Marker Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

# Java Test Case

```````````````````````````````` example(Java Test Case: 1) options(add-spec-file, source-name[com/vladsch/md/nav/flex/language/lineMarker/SampleTest.java])
package com.vladsch.md.nav.flex.language.lineMarker;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.test.util.ComboSpecTestCase;
import com.vladsch.flexmark.test.util.FlexmarkSpecExampleRenderer;
import com.vladsch.flexmark.test.util.SpecExampleRenderer;
import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SampleTest extends ComboSpecTestCase {
    private static final String SPEC_RESOURCE = "sample_spec_test.md";
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(SampleTest.class, SPEC_RESOURCE);
    public static final DataKey<Boolean> SELF = new DataKey<>("SELF", false);
    public static final DataKey<Integer> SELF_MARGIN = new DataKey<>("SELF_MARGIN", -1);

    // standard options
    private static final DataHolder OPTIONS = new MutableDataSet()
            .toImmutable();

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.put("self", new MutableDataSet().set(SELF, true));
        optionsMap.put("margin", new MutableDataSet().set(CUSTOM_OPTION, SampleTest::marginOption));
    }
    public SampleTest(@NotNull SpecExample example) {
        super(example, optionsMap, OPTIONS);
    }

    public static DataHolder marginOption(String option, String params) {
        int rightMargin = -1;
        if (params != null) {
            if (!params.matches("\\d*")) {
                throw new IllegalStateException("'margin' option requires a numeric or empty (for default margin) argument");
            }

            rightMargin = Integer.parseInt(params);
        }

        int finalRightMargin = rightMargin;
        return new MutableDataSet().set(SELF_MARGIN, finalRightMargin);
    }

    @Override
    @NotNull
    final public SpecExampleRenderer getSpecExampleRenderer(@NotNull SpecExample example, @Nullable DataHolder exampleOptions) {
        DataHolder OPTIONS = aggregate(myDefaultOptions, exampleOptions);
        return new FlexmarkSpecExampleRenderer(example, OPTIONS, Parser.builder(OPTIONS).build(), HtmlRenderer.builder(OPTIONS).build(), true);
    }
}
.
package com.vladsch.md.nav.flex.language.lineMarker;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.test.util.ComboSpecTestCase;
import com.vladsch.flexmark.test.util.FlexmarkSpecExampleRenderer;
import com.vladsch.flexmark.test.util.SpecExampleRenderer;
import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SampleTest extends ComboSpecTestCase {
    private static final String SPEC_RESOURCE = <lineMarker icon="MdIcons.Document.FLEXMARK_SPEC" descr="Navigate to flexmark Spec File" >"sample_spec_test.md"</lineMarker>;
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(SampleTest.class, SPEC_RESOURCE);
    public static final DataKey<Boolean> SELF = new DataKey<>("SELF", false);
    public static final DataKey<Integer> SELF_MARGIN = new DataKey<>("SELF_MARGIN", -1);

    // standard options
    private static final DataHolder OPTIONS = new MutableDataSet()
            .toImmutable();

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.put(<lineMarker icon="FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE" descr="Navigate to flexmark Spec Example" >"self"</lineMarker>, new MutableDataSet().set(SELF, true));
        optionsMap.put(<lineMarker icon="FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE" descr="Navigate to flexmark Spec Example" >"margin"</lineMarker>, new MutableDataSet().set(CUSTOM_OPTION, SampleTest::marginOption));
    }
    public SampleTest(@NotNull SpecExample example) {
        super(example, optionsMap, OPTIONS);
    }

    public static DataHolder marginOption(String option, String params) {
        int rightMargin = -1;
        if (params != null) {
            if (!params.matches("\\d*")) {
                throw new IllegalStateException("'margin' option requires a numeric or empty (for default margin) argument");
            }

            rightMargin = Integer.parseInt(params);
        }

        int finalRightMargin = rightMargin;
        return new MutableDataSet().set(SELF_MARGIN, finalRightMargin);
    }

    @Override
    @NotNull
    final public SpecExampleRenderer <lineMarker icon="/gutter/implementingMethod.svg" descr="<html><body><p>Implements method in <a href=&quot;#element/com.vladsch.flexmark.test.util.SpecExampleProcessor#getSpecExampleRenderer&quot;><code>SpecExampleProcessor</code></a> <font color='#787878'><code>(com.vladsch.flexmark.test.util)</code></font></p><p style='margin-top:8px;'><font size='2' color='#787878'>Press ⌘U to navigate</font></p></body></html>" >getSpecExampleRenderer</lineMarker>(@NotNull SpecExample example, @Nullable DataHolder exampleOptions) {
        DataHolder OPTIONS = aggregate(myDefaultOptions, exampleOptions);
        return new FlexmarkSpecExampleRenderer(example, OPTIONS, Parser.builder(OPTIONS).build(), HtmlRenderer.builder(OPTIONS).build(), true);
    }
}
````````````````````````````````


```````````````````````````````` example(Java Test Case: 2) options(add-spec-file, source-name[com/vladsch/md/nav/flex/language/lineMarker/SampleTest.java], disable-one[spec-file; spec-example-option])
package com.vladsch.md.nav.flex.language.lineMarker;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.test.util.ComboSpecTestCase;
import com.vladsch.flexmark.test.util.FlexmarkSpecExampleRenderer;
import com.vladsch.flexmark.test.util.SpecExampleRenderer;
import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SampleTest extends ComboSpecTestCase {
    private static final String SPEC_RESOURCE = "sample_spec_test.md";
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(SampleTest.class, SPEC_RESOURCE);
    public static final DataKey<Boolean> SELF = new DataKey<>("SELF", false);
    public static final DataKey<Integer> SELF_MARGIN = new DataKey<>("SELF_MARGIN", -1);

    // standard options
    private static final DataHolder OPTIONS = new MutableDataSet()
            .toImmutable();

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.put("self", new MutableDataSet().set(SELF, true));
        optionsMap.put("margin", new MutableDataSet().set(CUSTOM_OPTION, SampleTest::marginOption));
    }
    public SampleTest(@NotNull SpecExample example) {
        super(example, optionsMap, OPTIONS);
    }

    public static DataHolder marginOption(String option, String params) {
        int rightMargin = -1;
        if (params != null) {
            if (!params.matches("\\d*")) {
                throw new IllegalStateException("'margin' option requires a numeric or empty (for default margin) argument");
            }

            rightMargin = Integer.parseInt(params);
        }

        int finalRightMargin = rightMargin;
        return new MutableDataSet().set(SELF_MARGIN, finalRightMargin);
    }

    @Override
    @NotNull
    final public SpecExampleRenderer getSpecExampleRenderer(@NotNull SpecExample example, @Nullable DataHolder exampleOptions) {
        DataHolder OPTIONS = aggregate(myDefaultOptions, exampleOptions);
        return new FlexmarkSpecExampleRenderer(example, OPTIONS, Parser.builder(OPTIONS).build(), HtmlRenderer.builder(OPTIONS).build(), true);
    }
}
.
---- Disabled: spec-file -----------------------------------------------
package com.vladsch.md.nav.flex.language.lineMarker;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.test.util.ComboSpecTestCase;
import com.vladsch.flexmark.test.util.FlexmarkSpecExampleRenderer;
import com.vladsch.flexmark.test.util.SpecExampleRenderer;
import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SampleTest extends ComboSpecTestCase {
    private static final String SPEC_RESOURCE = "sample_spec_test.md";
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(SampleTest.class, SPEC_RESOURCE);
    public static final DataKey<Boolean> SELF = new DataKey<>("SELF", false);
    public static final DataKey<Integer> SELF_MARGIN = new DataKey<>("SELF_MARGIN", -1);

    // standard options
    private static final DataHolder OPTIONS = new MutableDataSet()
            .toImmutable();

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.put(<lineMarker icon="FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE" descr="Navigate to flexmark Spec Example" >"self"</lineMarker>, new MutableDataSet().set(SELF, true));
        optionsMap.put(<lineMarker icon="FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE" descr="Navigate to flexmark Spec Example" >"margin"</lineMarker>, new MutableDataSet().set(CUSTOM_OPTION, SampleTest::marginOption));
    }
    public SampleTest(@NotNull SpecExample example) {
        super(example, optionsMap, OPTIONS);
    }

    public static DataHolder marginOption(String option, String params) {
        int rightMargin = -1;
        if (params != null) {
            if (!params.matches("\\d*")) {
                throw new IllegalStateException("'margin' option requires a numeric or empty (for default margin) argument");
            }

            rightMargin = Integer.parseInt(params);
        }

        int finalRightMargin = rightMargin;
        return new MutableDataSet().set(SELF_MARGIN, finalRightMargin);
    }

    @Override
    @NotNull
    final public SpecExampleRenderer <lineMarker icon="/gutter/implementingMethod.svg" descr="<html><body><p>Implements method in <a href=&quot;#element/com.vladsch.flexmark.test.util.SpecExampleProcessor#getSpecExampleRenderer&quot;><code>SpecExampleProcessor</code></a> <font color='#787878'><code>(com.vladsch.flexmark.test.util)</code></font></p><p style='margin-top:8px;'><font size='2' color='#787878'>Press ⌘U to navigate</font></p></body></html>" >getSpecExampleRenderer</lineMarker>(@NotNull SpecExample example, @Nullable DataHolder exampleOptions) {
        DataHolder OPTIONS = aggregate(myDefaultOptions, exampleOptions);
        return new FlexmarkSpecExampleRenderer(example, OPTIONS, Parser.builder(OPTIONS).build(), HtmlRenderer.builder(OPTIONS).build(), true);
    }
}

---- Disabled: spec-example-option -------------------------------------
package com.vladsch.md.nav.flex.language.lineMarker;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.test.util.ComboSpecTestCase;
import com.vladsch.flexmark.test.util.FlexmarkSpecExampleRenderer;
import com.vladsch.flexmark.test.util.SpecExampleRenderer;
import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SampleTest extends ComboSpecTestCase {
    private static final String SPEC_RESOURCE = <lineMarker icon="MdIcons.Document.FLEXMARK_SPEC" descr="Navigate to flexmark Spec File" >"sample_spec_test.md"</lineMarker>;
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(SampleTest.class, SPEC_RESOURCE);
    public static final DataKey<Boolean> SELF = new DataKey<>("SELF", false);
    public static final DataKey<Integer> SELF_MARGIN = new DataKey<>("SELF_MARGIN", -1);

    // standard options
    private static final DataHolder OPTIONS = new MutableDataSet()
            .toImmutable();

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.put("self", new MutableDataSet().set(SELF, true));
        optionsMap.put("margin", new MutableDataSet().set(CUSTOM_OPTION, SampleTest::marginOption));
    }
    public SampleTest(@NotNull SpecExample example) {
        super(example, optionsMap, OPTIONS);
    }

    public static DataHolder marginOption(String option, String params) {
        int rightMargin = -1;
        if (params != null) {
            if (!params.matches("\\d*")) {
                throw new IllegalStateException("'margin' option requires a numeric or empty (for default margin) argument");
            }

            rightMargin = Integer.parseInt(params);
        }

        int finalRightMargin = rightMargin;
        return new MutableDataSet().set(SELF_MARGIN, finalRightMargin);
    }

    @Override
    @NotNull
    final public SpecExampleRenderer <lineMarker icon="/gutter/implementingMethod.svg" descr="<html><body><p>Implements method in <a href=&quot;#element/com.vladsch.flexmark.test.util.SpecExampleProcessor#getSpecExampleRenderer&quot;><code>SpecExampleProcessor</code></a> <font color='#787878'><code>(com.vladsch.flexmark.test.util)</code></font></p><p style='margin-top:8px;'><font size='2' color='#787878'>Press ⌘U to navigate</font></p></body></html>" >getSpecExampleRenderer</lineMarker>(@NotNull SpecExample example, @Nullable DataHolder exampleOptions) {
        DataHolder OPTIONS = aggregate(myDefaultOptions, exampleOptions);
        return new FlexmarkSpecExampleRenderer(example, OPTIONS, Parser.builder(OPTIONS).build(), HtmlRenderer.builder(OPTIONS).build(), true);
    }
}
````````````````````````````````


## Spec File

```````````````````````````````` example(Spec File: 1) options(add-test-case, source-name[com/vladsch/md/nav/language/lineMarker/sample_spec_test.md])
---
title: Line Marker Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Sample

```````````````` example Sample: 1
…
````````````````


```````````````` example(Sample: 2) options(IGNORE)
…
````````````````


```````````````` example(Sample: 2) options(FAIL)
…
````````````````


```````````````` example(Sample: 2) options(NO_FILE_EOL)
…
````````````````


```````````````` example(Sample: 2) options(FILE_EOL)
…
````````````````


```````````````` example(Sample: 2) options(TIMED)
…
````````````````


```````````````` example(Sample: 2) options(EMBED_TIMED)
…
````````````````


```````````````` example(Sample: 2) options(self)
…
````````````````


```````````````` example(Sample: 3) options(margin[10])
…
````````````````
.
---
title: Line Marker Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Sample

<lineMarker icon="MdIcons.Document.FLEXMARK_SPEC" descr="Navigate to test case or option definition" >````````````````</lineMarker> example Sample: 1
…
````````````````


<lineMarker icon="FlexmarkIcons.Element.HIDDEN_SPEC_EXAMPLE" descr="Navigate to test case or option definition" >````````````````</lineMarker> example(Sample: 2) options(IGNORE)
…
````````````````


<lineMarker icon="FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_FAIL" descr="Navigate to test case or option definition" >````````````````</lineMarker> example(Sample: 2) options(FAIL)
…
````````````````


<lineMarker icon="FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_NO_FILE_EOL" descr="Navigate to test case or option definition" >````````````````</lineMarker> example(Sample: 2) options(NO_FILE_EOL)
…
````````````````


<lineMarker icon="FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_FILE_EOL" descr="Navigate to test case or option definition" >````````````````</lineMarker> example(Sample: 2) options(FILE_EOL)
…
````````````````


<lineMarker icon="FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_TIMED" descr="Navigate to test case or option definition" >````````````````</lineMarker> example(Sample: 2) options(TIMED)
…
````````````````


<lineMarker icon="FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_TIMED" descr="Navigate to test case or option definition" >````````````````</lineMarker> example(Sample: 2) options(EMBED_TIMED)
…
````````````````


<lineMarker icon="FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE" descr="Navigate to test case or option definition" >````````````````</lineMarker> example(Sample: 2) options(self)
…
````````````````


<lineMarker icon="FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE" descr="Navigate to test case or option definition" >````````````````</lineMarker> example(Sample: 3) options(margin[10])
…
````````````````
````````````````````````````````


```````````````````````````````` example(Spec File: 2) options(add-test-case, source-name[com/vladsch/md/nav/flex/language/lineMarker/sample_spec_test.md], disable-one[spec-example])
---
title: Line Marker Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Sample

```````````````` example Sample: 1
…
````````````````


```````````````` example(Sample: 2) options(IGNORE)
…
````````````````


```````````````` example(Sample: 2) options(FAIL)
…
````````````````


```````````````` example(Sample: 2) options(NO_FILE_EOL)
…
````````````````


```````````````` example(Sample: 2) options(NO_FILE_EOL)
…
````````````````


```````````````` example(Sample: 2) options(TIMED)
…
````````````````


```````````````` example(Sample: 2) options(EMBED_TIMED)
…
````````````````


```````````````` example(Sample: 2) options(self)
…
````````````````


```````````````` example(Sample: 3) options(margin[10])
…
````````````````
.
---- Disabled: spec-example --------------------------------------------
---
title: Line Marker Spec
author:
version:
date: '2019-10-14'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Sample

```````````````` example Sample: 1
…
````````````````


```````````````` example(Sample: 2) options(IGNORE)
…
````````````````


```````````````` example(Sample: 2) options(FAIL)
…
````````````````


```````````````` example(Sample: 2) options(NO_FILE_EOL)
…
````````````````


```````````````` example(Sample: 2) options(NO_FILE_EOL)
…
````````````````


```````````````` example(Sample: 2) options(TIMED)
…
````````````````


```````````````` example(Sample: 2) options(EMBED_TIMED)
…
````````````````


```````````````` example(Sample: 2) options(self)
…
````````````````


```````````````` example(Sample: 3) options(margin[10])
…
````````````````
````````````````````````````````


