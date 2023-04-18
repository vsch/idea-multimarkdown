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
package com.vladsch.md.nav.testUtil;

import com.vladsch.md.nav.util.DataPrinterAware;

import java.lang.reflect.Array;
import java.util.List;

import static org.junit.Assert.fail;

public class SmTestCaseUtils {
    public static void compareOrderedLists(String message, String[] expected, String[] actual) {
        new OrderedStringComparison().arrayEquals(message, expected, actual);
    }

    public static <T> void compareOrderedLists(String message, T[] expected, T[] actual) {
        new OrderedComparableComparison<T>().arrayEquals(message, expected, actual);
    }

    public static <T> void compareUnorderedLists(String message, T[] expected, T[] actual) {
        new UnorderedComparableComparison<T>().arrayEquals(message, expected, actual);
    }

    public static void compareOrderedLists(String message, String[] expected, List<String> actual) {
        new OrderedStringComparison().arrayEquals(message, expected, actual.toArray(new String[actual.size()]));
    }

    public static void compareOrderedLists(String message, List<String> expected, List<String> actual) {
        new OrderedStringComparison().arrayEquals(message, expected.toArray(new String[expected.size()]), actual.toArray(new String[actual.size()]));
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

    public static class OrderedComparableComparison<T> extends OrderedComparisonCriteria<Comparable<T>> {
        @Override
        protected void assertElementsAreEqual(Comparable<T> o1, Comparable<T> o2) {
            if (o1.compareTo((T) o2) != 0) failNotEquals("Elements not equal", o1, o2);
        }

        @Override
        protected boolean elementsAreEqual(Comparable<T> o1, Comparable<T> o2) {
            return o1.compareTo((T) o2) == 0;
        }
    }

    public static class UnorderedComparableComparison<T> extends UnorderedComparisonCriteria<Comparable<T>> {
        @Override
        protected void assertElementsAreEqual(Comparable<T> o1, Comparable<T> o2) {
            if (o1.compareTo((T) o2) != 0) failNotEquals("Elements not equal", o1, o2);
        }

        @Override
        protected boolean elementsAreEqual(Comparable<T> o1, Comparable<T> o2) {
            return o1.compareTo((T) o2) == 0;
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
            else if (elem instanceof DataPrinterAware) builder.append(((DataPrinterAware) elem).testData());
            else builder.append(elem.toString());
            if (i + 1 < length) builder.append(',');
            builder.append('\n');
        }
        builder.append("]\n");

        return builder.toString();
    }
}
