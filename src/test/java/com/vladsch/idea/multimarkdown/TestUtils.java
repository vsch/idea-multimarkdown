/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.idea.multimarkdown;

import com.vladsch.idea.multimarkdown.spellchecking.Suggestion;
import com.vladsch.idea.multimarkdown.spellchecking.SuggestionList;
import com.vladsch.idea.multimarkdown.util.InspectionResult;
import com.vladsch.idea.multimarkdown.util.PathInfo;
import org.junit.internal.ArrayComparisonFailure;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class TestUtils {
    public static void assertSuggestionHasParam(Suggestion.Param expected, Suggestion suggestion) {
        assertEquals(true, suggestion.hasParam(expected.key));
    }

    public static <T> void assertSuggestionParamValue(Suggestion.Param<T> expected, Suggestion suggestion) {
        assertEquals(expected.value, (T) suggestion.getParam(expected.key));
    }

    public static <T> void assertSuggestionParamValue(String key, T value, Suggestion suggestion) {
        assertEquals(value, (T) suggestion.getParam(key));
    }

    public static void assertSuggestionHasNoParams(Suggestion suggestion) {
        assertEquals(false, suggestion.hasParams());
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
        new OrderedPathInfoComparison().arrayEquals(null, expected.toArray(), actual.toArray());
    }

    public static void compareUnorderedLists(String message, ArrayList<String> expected, Set<String> actual) {
        new UnorderedPathInfoComparison().arrayEquals(null, expected.toArray(), actual.toArray());
    }

    public static void compareOrderedLists(String message, Suggestion[] expected, SuggestionList actual) {
        new OrderedSuggestionComparison().arrayEquals(null, expected, actual.getSuggestions().toArray(new Suggestion[actual.size()]));
    }

    public static void compareOrderedLists(String message, List<Suggestion> expected, SuggestionList actual) {
        new OrderedSuggestionComparison().arrayEquals(null, expected.toArray(new Suggestion[expected.size()]), actual.getSuggestions().toArray(new Suggestion[actual.size()]));
    }

    public static void compareOrderedLists(String message, String[] expected, String[] actual) {
        new OrderedStringComparison().arrayEquals(null, expected, actual);
    }

    public static void compareOrderedLists(String message, String[] expected, List<String> actual) {
        new OrderedStringComparison().arrayEquals(null, expected, actual.toArray(new String[actual.size()]));
    }

    public static void compareOrderedLists(String message, List<String> expected, List<String> actual) {
        new OrderedStringComparison().arrayEquals(message, expected.toArray(new String[expected.size()]), actual.toArray(new String[actual.size()]));
    }

    public static void compareOrderedLists(String message, SuggestionList expected, SuggestionList actual) {
        new OrderedSuggestionComparison().arrayEquals(null, expected.getSuggestions().toArray(new Suggestion[expected.size()]), actual.getSuggestions().toArray(new Suggestion[actual.size()]));
    }

    public static void compareUnorderedLists(String message, Suggestion[] expected, SuggestionList actual) {
        new UnorderedSuggestionComparison().arrayEquals(null, expected, actual.getSuggestions().toArray(new Suggestion[actual.size()]));
    }

    public static void compareUnorderedLists(String message, List<Suggestion> expected, SuggestionList actual) {
        new UnorderedSuggestionComparison().arrayEquals(null, expected.toArray(new Suggestion[expected.size()]), actual.getSuggestions().toArray(new Suggestion[actual.size()]));
    }

    public static void compareUnorderedLists(String message, SuggestionList expected, SuggestionList actual) {
        new UnorderedSuggestionComparison().arrayEquals(null, expected.getSuggestions().toArray(new Suggestion[expected.size()]), actual.getSuggestions().toArray(new Suggestion[actual.size()]));
    }


    public static void compareOrderedLists(String message, ArrayList<InspectionResult> expected, List<InspectionResult> actual) {
        new OrderedInspectionComparison().arrayEquals(message, expected.toArray(new InspectionResult[expected.size()]), actual.toArray(new InspectionResult[actual.size()]));
    }

    public static void compareUnorderedLists(String message, ArrayList<InspectionResult> expected, List<InspectionResult> actual) {
        new UnorderedInspectionComparison().arrayEquals(message, expected.toArray(new InspectionResult[expected.size()]), actual.toArray(new InspectionResult[actual.size()]));
    }


    public static void assertSuggestionListHasSuggestions(SuggestionList suggestionList, Suggestion... suggestions) {
        compareOrderedLists((String) null, suggestions, suggestionList);
    }

    public static void assertEqualsMessage(String message, Object expected, Object actual) {
        if ((expected == null && actual != null) || (expected != null && !expected.equals(actual))) {
            failNotEquals(message, expected, actual);
        }
    }

    public static void assertEqualsMessage(String message, boolean expected, boolean actual) {
        if (expected != actual) {
            failNotEquals(message, expected, actual);
        }
    }

    public static void assertEqualsMessage(String message, int expected, int actual) {
        if (expected != actual) {
            failNotEquals(message, expected, actual);
        }
    }

    public static void failNotEquals(String message, Object expected, Object actual) {
        fail(format(message, expected, actual));
    }

    public static void failNotEquals(String message, boolean expected, boolean actual) {
        fail(format(message, expected, actual));
    }

    public static void failNotEquals(String message, int expected, int actual) {
        fail(format(message, expected, actual));
    }

    public static String format(String message, Object expected, Object actual) {
        String formatted = "";
        if (message != null && !message.equals("")) {
            formatted = message + " ";
        }

        String expectedString = String.valueOf(expected);
        String actualString = String.valueOf(actual);
        return expectedString.equals(actualString) ? formatted + "expected: " + formatClassAndValue(expected, expectedString) + " but was: " + formatClassAndValue(actual, actualString) : formatted + "expected:<" + expectedString + "> but was:<" + actualString + ">";
    }

    public static String formatClassAndValue(Object value, String valueString) {
        String className = value == null ? "null" : value.getClass().getName();
        return className + "<" + valueString + ">";
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

    public static class OrderedStringComparison extends OrderedComparisonCriteria<String> {
        @Override
        protected boolean elementsAreEqual(String o1, String o2) {
            return o1.compareTo(o2) == 0;
        }

        @Override
        protected void assertElementsAreEqual(String o1, String o2) {
            if (o1.compareTo(o2) != 0) failNotEquals("Strings not equal", o1, o2);
        }
    }

    public static class UnorderedStringComparison extends UnorderedComparisonCriteria<String> {
        @Override
        protected boolean elementsAreEqual(String o1, String o2) {
            return o1.compareTo(o2) == 0;
        }

        @Override
        protected void assertElementsAreEqual(String o1, String o2) {
            if (o1.compareTo(o2) != 0) failNotEquals("Strings not equal", o1, o2);
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

    public static String arrayAsString(Object expecteds) {
        StringBuilder builder = new StringBuilder(100);

        builder.append(expecteds.toString());
        builder.append("[\n");
        int length = Array.getLength(expecteds);
        for (int i = 0; i < length; i++) {
            Object elem = Array.get(expecteds, i);
            builder.append("  ");
            if (elem == null) builder.append("null");
            else if (elem instanceof String) builder.append("\"").append(elem.toString().replace("\"", "\\\"")).append("\"");
            else builder.append(elem.toString());
            if (i+1 < length) builder.append(',');
            builder.append('\n');
        }
        builder.append("]\n");

        return builder.toString();
    }
}
