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

package com.vladsch.md.nav.testUtil.renderers;

import com.intellij.psi.tree.IElementType;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.md.nav.parser.cache.MdCachedFileElements;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.psi.element.MdReferenceElement;
import com.vladsch.md.nav.testUtil.cases.MdCachedFileElementsTestCase;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.cases.LightFixtureActionSpecTest;
import com.vladsch.plugin.test.util.renderers.ActionSpecRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class FileElementStashSpecRenderer<T extends MdCachedFileElementsTestCase> extends ActionSpecRenderer<T> {
    public FileElementStashSpecRenderer(@NotNull T specTestBase, @NotNull SpecExample example, @Nullable DataHolder options) {
        super(specTestBase, example, options);
    }

    protected boolean wantAstByDefault() {
        return false;
    }

    @Override
    protected void renderAst(StringBuilder out) {
        MdFile file = (MdFile) getFile();

        if (MdCachedFileElementsTestCase.REFERENCED_ELEMENTS.get(myOptions)) {
            Map<String, ? extends Set<MdReferenceElement>> referencedElements = MdCachedFileElements.getReferencedElementMap(file);
            ArrayList<String> types = new ArrayList<>(referencedElements.keySet());
            types.sort(Comparator.comparing(it -> it));
            for (String type : types) {
                out.append(type).append(": { ");
                String sep = "";
                ArrayList<MdReferenceElement> strings = new ArrayList<>(referencedElements.get(type));
                strings.sort(Comparator.comparing(MdReferenceElement::getReferenceId));
                for (MdReferenceElement key : strings) {
                    out.append(sep).append(key.getReferenceId()).append(String.format(":[%d, %d, type: %s]", key.getTextOffset(), key.getTextOffset() + key.getTextLength(), key.getReferenceType().toString()));
                    sep = ", ";
                }
                out.append(" }\n");
            }
        }

        if (MdCachedFileElementsTestCase.REFERENCE_DEFINITION_COUNTS.get(myOptions)) {
            Map<IElementType, ? extends Map<String, Integer>> referenceCounts = MdCachedFileElements.getReferenceDefinitionCounts(file);
            ArrayList<IElementType> types = new ArrayList<>(referenceCounts.keySet());
            types.sort(Comparator.comparing(IElementType::toString));
            for (IElementType type : types) {
                out.append(type).append(": { ");
                String sep = "";
                Map<String, Integer> integerMap = referenceCounts.get(type);
                ArrayList<String> strings = new ArrayList<>(integerMap.keySet());
                strings.sort(Comparator.comparing(it -> it));
                for (String key : strings) {
                    out.append(sep).append(key).append(": ").append(integerMap.get(key));
                    sep = ", ";
                }
                out.append(" }\n");
            }
        }

        Class<?>[] classes = MdCachedFileElementsTestCase.FIND_CLASSES.get(myOptions);
        if (classes.length > 0) {
            MdCachedFileElements.findChildrenOfAnyType(file,
                    MdCachedFileElementsTestCase.WANT_OUTER.get(myOptions),
                    MdCachedFileElementsTestCase.WANT_INCLUDED.get(myOptions),
                    MdCachedFileElementsTestCase.WANT_INCLUDING.get(myOptions),
                    classes,
                    mySpecTest.renderAstTextConsumer(out));
        }

        super.renderAst(out);
    }

    @NotNull
    @Override
    public String renderHtml() {
        String action = LightFixtureActionSpecTest.ACTION_NAME.get(myOptions);
        if (!action.equals(LightFixtureActionSpecTest.SKIP_ACTION)) {
            CodeInsightFixtureSpecTestCase.appendBanner(ast, CodeInsightFixtureSpecTestCase.BANNER_BEFORE_ACTION);
            renderAst(ast);
            CodeInsightFixtureSpecTestCase.appendBanner(ast, CodeInsightFixtureSpecTestCase.BANNER_AFTER_ACTION);
            return super.renderHtml();
        }
        return "";
    }
}
