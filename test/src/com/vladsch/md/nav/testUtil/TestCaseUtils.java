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

package com.vladsch.md.nav.testUtil;

import com.vladsch.md.nav.spellchecking.Suggestion;
import com.vladsch.md.nav.spellchecking.SuggestionList;
import com.vladsch.md.nav.util.InspectionResult;
import com.vladsch.md.nav.util.PathInfo;


import org.junit.internal.ArrayComparisonFailure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestCaseUtils extends SmTestCaseUtils {
    public static void assertSuggestionHasParam(Suggestion.Param expected, Suggestion suggestion) {
        assertTrue(suggestion.hasParam(expected.key));
    }

    public static <T> void assertSuggestionParamValue(Suggestion.Param<T> expected, Suggestion suggestion) {
        //noinspection unchecked
        assertEquals(expected.value, suggestion.getParam(expected.key));
    }

    public static <T> void assertSuggestionParamValue(String key, T value, Suggestion suggestion) {
        //noinspection unchecked
        assertEquals(value, suggestion.getParam(key));
    }

    public static void assertSuggestionHasNoParams(Suggestion suggestion) {
        assertFalse(suggestion.hasParams());
    }

    public static void assertSuggestionEquals(String text, String expected, Suggestion.Fixer fixer) {
        Suggestion suggestion = new Suggestion(text);
        SuggestionList result = fixer.fix(suggestion, null);
        if (result != null && result.getSuggestions().size() > 0) {
            assertEquals(expected, result.getSuggestions().get(0).getText());
            assertEquals(1, result.getSuggestions().size());
        } else {
            assertNotNull(result);
            assertEquals(1, result.getSuggestions().size());
        }
    }

    public static void compareOrderedLists(String message, ArrayList<String> expected, Set<String> actual) {
        new OrderedPathInfoComparison().arrayEquals(message, expected.toArray(), actual.toArray());
    }

    public static void compareUnorderedLists(String message, ArrayList<String> expected, Set<String> actual) {
        new UnorderedPathInfoComparison().arrayEquals(message, expected.toArray(), actual.toArray());
    }

    public static void compareOrderedLists(String message, Suggestion[] expected, SuggestionList actual) {
        new OrderedSuggestionComparison().arrayEquals(message, expected, actual.getSuggestions().toArray(new Suggestion[actual.size()]));
    }

    public static void compareOrderedLists(String message, List<Suggestion> expected, SuggestionList actual) {
        new OrderedSuggestionComparison().arrayEquals(message, expected.toArray(new Suggestion[0]), actual.getSuggestions().toArray(new Suggestion[actual.size()]));
    }

    public static void compareOrderedLists(String message, SuggestionList expected, SuggestionList actual) {
        new OrderedSuggestionComparison().arrayEquals(message, expected.getSuggestions().toArray(new Suggestion[expected.size()]), actual.getSuggestions().toArray(new Suggestion[actual.size()]));
    }

    public static void compareUnorderedLists(String message, Suggestion[] expected, SuggestionList actual) {
        new UnorderedSuggestionComparison().arrayEquals(message, expected, actual.getSuggestions().toArray(new Suggestion[actual.size()]));
    }

    public static void compareUnorderedLists(String message, List<Suggestion> expected, SuggestionList actual) {
        new UnorderedSuggestionComparison().arrayEquals(message, expected.toArray(new Suggestion[0]), actual.getSuggestions().toArray(new Suggestion[actual.size()]));
    }

    public static void compareUnorderedLists(String message, SuggestionList expected, SuggestionList actual) {
        new UnorderedSuggestionComparison().arrayEquals(message, expected.getSuggestions().toArray(new Suggestion[expected.size()]), actual.getSuggestions().toArray(new Suggestion[actual.size()]));
    }

    private static InspectionResult[] inspectionResults(List<InspectionResult> results) {
        int iMax = results.size();
        InspectionResult[] array = new InspectionResult[iMax];
        for (int i = 0; i < iMax; i++) {
            InspectionResult result = results.get(i);
            if (result.getReferenceId() == null) array[i] = result;
            else array[i] = new InspectionResult(null, result.getId(), result.getSeverity(), result.getFixedLink(), result.getFixedFilePath());
        }
        return array;
    }

    public static void compareOrderedLists(String message, ArrayList<InspectionResult> expected, List<InspectionResult> actual) {
        new OrderedInspectionComparison().arrayEquals(message, inspectionResults(expected), inspectionResults(actual));
    }

    public static void compareUnorderedLists(String message, ArrayList<InspectionResult> expected, List<InspectionResult> actual) {
        new UnorderedInspectionComparison().arrayEquals(message, inspectionResults(expected), inspectionResults(actual));
    }

    public static void assertSuggestionListHasSuggestions(SuggestionList suggestionList, Suggestion... suggestions) {
        compareOrderedLists(null, suggestions, suggestionList);
    }

    public static class OrderedPathInfoComparison extends OrderedComparisonCriteria<PathInfo> {
        @Override
        protected boolean elementsAreEqual(PathInfo o1, PathInfo o2) {
            return o1.compareTo(o2) == 0;
        }

        @Override
        protected void assertElementsAreEqual(PathInfo o1, PathInfo o2) {
            if (o1.compareTo(o2) != 0) failNotEquals("PathInfo not equal", o1, o2);
        }
    }

    public static class UnorderedPathInfoComparison extends UnorderedComparisonCriteria<PathInfo> {
        @Override
        protected boolean elementsAreEqual(PathInfo o1, PathInfo o2) {
            return o1.compareTo(o2) == 0;
        }

        @Override
        protected void assertElementsAreEqual(PathInfo o1, PathInfo o2) {
            if (o1.compareTo(o2) != 0) failNotEquals("PathInfo not equal", o1, o2);
        }
    }

    public static class OrderedInspectionComparison extends OrderedComparisonCriteria<InspectionResult> {
        @Override
        protected boolean elementsAreEqual(InspectionResult o1, InspectionResult o2) {
            return o1.compareTo(o2) == 0;
        }

        @Override
        protected void assertElementsAreEqual(InspectionResult o1, InspectionResult o2) {
            if (o1.compareTo(o2) != 0) failNotEquals("PathInfo not equal", o1, o2);
        }
    }

    public static class UnorderedInspectionComparison extends UnorderedComparisonCriteria<InspectionResult> {
        @Override
        protected boolean elementsAreEqual(InspectionResult o1, InspectionResult o2) {
            return o1.compareTo(o2) == 0;
        }

        @Override
        protected void assertElementsAreEqual(InspectionResult o1, InspectionResult o2) {
            if (o1.compareTo(o2) != 0) failNotEquals("PathInfo not equal", o1, o2);
        }
    }

    public static class OrderedSuggestionComparison extends OrderedComparisonCriteria<Suggestion> {
        @Override
        protected boolean elementsAreEqual(Suggestion o1, Suggestion o2) {
            if (!o1.getText().equals(o2.getText())) return false;
            try {
                new UnorderedSuggestionParamComparison().arrayEquals("Suggestion.params not equal", o1.paramsArray(), o2.paramsArray());
            } catch (ArrayComparisonFailure ignored) {
                return false;
            }
            return true;
        }

        @Override
        protected void assertElementsAreEqual(Suggestion o1, Suggestion o2) {
            if (!elementsAreEqual(o1, o2)) {
                assertEquals(o1.getText(), o2.getText());
                new UnorderedSuggestionParamComparison().arrayEquals("Suggestion.params not equal", o1.paramsArray(), o2.paramsArray());
            }
        }
    }

    public static class UnorderedSuggestionComparison extends UnorderedComparisonCriteria<Suggestion> {
        @Override
        protected boolean elementsAreEqual(Suggestion o1, Suggestion o2) {
            if (!o1.getText().equals(o2.getText())) return false;
            try {
                new UnorderedSuggestionParamComparison().arrayEquals("Suggestion.params not equal", o1.paramsArray(), o2.paramsArray());
            } catch (ArrayComparisonFailure ignored) {
                return false;
            }
            return true;
        }

        @Override
        protected void assertElementsAreEqual(Suggestion o1, Suggestion o2) {
            if (!elementsAreEqual(o1, o2)) {
                assertEquals(o1.getText(), o2.getText());
                new UnorderedSuggestionParamComparison().arrayEquals("Suggestion.params not equal", o1.paramsArray(), o2.paramsArray());
            }
        }
    }

    public static class UnorderedSuggestionParamComparison extends UnorderedComparisonCriteria<Suggestion.Param> {
        @Override
        protected boolean elementsAreEqual(Suggestion.Param o1, Suggestion.Param o2) {
            return o1.key.equals(o2.key) && o1.value.equals(o2.value);
        }

        @Override
        protected void assertElementsAreEqual(Suggestion.Param o1, Suggestion.Param o2) {
            if (!elementsAreEqual(o1, o2)) failNotEquals("Suggestion.Param not equal", o1, o2);
        }
    }

    public static class OrderedSuggestionParamComparison extends OrderedComparisonCriteria<Suggestion.Param> {
        @Override
        protected boolean elementsAreEqual(Suggestion.Param o1, Suggestion.Param o2) {
            return o1.key.equals(o2.key) && o1.value.equals(o2.value);
        }

        @Override
        protected void assertElementsAreEqual(Suggestion.Param o1, Suggestion.Param o2) {
            if (!elementsAreEqual(o1, o2)) failNotEquals("Suggestion.Param not equal", o1, o2);
        }
    }
}
