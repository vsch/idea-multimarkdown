/*
 * Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
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

import com.intellij.psi.PsiElement;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.md.nav.parser.cache.SourcedElementConsumer;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.psi.element.MdHeaderElement;
import com.vladsch.md.nav.psi.element.MdListItem;
import com.vladsch.md.nav.psi.element.MdOrderedListItem;
import com.vladsch.md.nav.psi.element.MdReferenceElement;
import com.vladsch.md.nav.psi.element.MdUnorderedListItem;
import com.vladsch.md.nav.util.Result;
import com.vladsch.plugin.test.util.cases.LightFixtureActionSpecTest;
import com.vladsch.plugin.util.PsiTreeAstRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public interface MdCachedFileElementsTestCase extends LightFixtureActionSpecTest {
    Class<?>[] EMPTY_CLASSES = new Class<?>[0];
    DataKey<Class<?>[]> FIND_CLASSES = new DataKey<>("FIND_CLASSES", EMPTY_CLASSES);
    DataKey<Boolean> WANT_OUTER = new DataKey<>("WANT_OUTER", false);
    DataKey<Boolean> WANT_INCLUDED = new DataKey<>("WANT_INCLUDED", false);
    DataKey<Boolean> WANT_INCLUDING = new DataKey<>("WANT_INCLUDING", false);
    DataKey<Boolean> REFERENCED_ELEMENTS = new DataKey<>("REFERENCED_ELEMENTS", false);
    DataKey<Boolean> REFERENCE_DEFINITION_COUNTS = new DataKey<>("REFERENCE_DEFINITION_COUNTS", false);

    Map<String, DataHolder> optionsMap = new HashMap<>();

    static Map<String, DataHolder> getOptionsMap() {
        synchronized (optionsMap) {
            if (optionsMap.isEmpty()) {
                optionsMap.put("find-references", new MutableDataSet().set(FIND_CLASSES, new Class<?>[] { MdReferenceElement.class }));
                optionsMap.put("find-list-items-list", new MutableDataSet().set(FIND_CLASSES, new Class<?>[] { MdOrderedListItem.class, MdUnorderedListItem.class }));
                optionsMap.put("find-list-items", new MutableDataSet().set(FIND_CLASSES, new Class<?>[] { MdListItem.class }));
                optionsMap.put("find-headers", new MutableDataSet().set(FIND_CLASSES, new Class<?>[] { MdHeaderElement.class }));
                optionsMap.put("referenced-elements", new MutableDataSet().set(REFERENCED_ELEMENTS, true));
                optionsMap.put("reference-definition-counts", new MutableDataSet().set(REFERENCE_DEFINITION_COUNTS, true));
            }

            return optionsMap;
        }
    }

    default SourcedElementConsumer<Object> renderAstTextConsumer(@NotNull StringBuilder out) {
        MdFile file = (MdFile) getFile();
        BasedSequence fileChars = BasedSequence.of(file.getText());

        return (o, source) -> {
            PsiElement e = (PsiElement) o;
            PsiTreeAstRenderer.generateAst(fileChars, out, "", e);
            return Result.CONTINUE();
        };
    }
}
