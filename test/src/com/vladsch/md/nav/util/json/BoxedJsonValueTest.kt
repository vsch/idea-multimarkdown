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

package com.vladsch.md.nav.util.json

import com.vladsch.boxed.json.BoxedJsValue.*
import com.vladsch.boxed.json.BoxedJson
import com.vladsch.boxed.json.MutableJsArray
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.StringWriter
import javax.json.Json
import javax.json.JsonValue

class BoxedJsonValueTest {

    @Test
    fun test_Basic() {

        val s = """{
  "null":null,
  "int0":0,
  "int1":1,
  "name":"value",
  "option":"optValue",
  "array": [
    "0", "1", "2", "3"
  ],
  "object": {
      "param0":"paramValue0",
      "param1":"paramValue1",
      "param2":"paramValue2",
      "param3":"paramValue3",
      "param4":"paramValue4",
      "param5":"paramValue5",
      "param6":"paramValue6",
      "param7":"paramValue7",
      "param8":"paramValue8",
      "0":"indexValue0",
      "1":"indexValue1",
      "2":"indexValue2",
      "3":"indexValue3",
      "4":"indexValue4",
      "5":"indexValue5",
      "6":"indexValue6",
      "7":"indexValue7",
      "8":"indexValue8",
       "array": [
         "0", "1", "2", "3", 4, 5, 6
       ]
  }
}"""

        val jsonObject = BoxedJson.boxedFrom(s.trimIndent())

        assertEquals(0, jsonObject.getInt("int0"))
        assertEquals(1, jsonObject.getInt("int1"))
        assertEquals(false, jsonObject.isNull("int1"))
        assertEquals(true, jsonObject.isNull("null"))
        assertEquals("value", jsonObject.getString("name"))
        assertEquals(HAD_MISSING_LITERAL, jsonObject.get("names"))

        assertEquals("\"paramValue2\"", jsonObject.evalJsString("object.param2").toString())
        assertEquals("\"indexValue4\"", jsonObject.evalJsString("object.4").toString())
        assertEquals("indexValue4", jsonObject.evalString("object.4"))
        assertEquals(HAD_INVALID_NUMBER, jsonObject.evalJsNumber("object.4"))
        assertEquals(0, jsonObject.evalInt("object.4"))
        assertEquals(HAD_MISSING_STRING, jsonObject.evalJsString("object.10"))
        assertEquals(HAD_INVALID_LITERAL, jsonObject.evalJsString("object.array.1"))
        assertEquals("1", jsonObject.evalString("object.array[1]"))
        assertEquals(HAD_INVALID_STRING, jsonObject.evalJsString("object.array[5]"))
        assertEquals(5, jsonObject.evalInt("object.array[5]"))
        assertEquals(HAD_INVALID_NUMBER, jsonObject.evalJsNumber("object.array[1]"))
        assertEquals(HAD_MISSING_NUMBER, jsonObject.evalJsNumber("object.array[10]"))
        assertEquals(HAD_NULL_NUMBER, jsonObject.evalJsNumber("null.array[10]"))
        assertEquals(HAD_NULL_STRING, jsonObject.evalJsString("null.array[10]"))
    }

    fun asString(value: JsonValue): String {
        val sw = StringWriter()
        val arr = MutableJsArray()
        arr.add(value)
        val jw = Json.createWriter(sw)
        jw.writeArray(arr)
        jw.close()
        return sw.toString()
    }

    // @formatter:off
    @Test fun test_Literal_NULL() = assertEquals("[null]", asString(JsonValue.NULL))
    @Test fun test_Literal_TRUE() = assertEquals("[true]", asString(JsonValue.TRUE))
    @Test fun test_Literal_FALSE() = assertEquals("[false]", asString(JsonValue.FALSE))
    @Test fun test_Literal_HAD_NULL_LITERAL() = assertEquals("[null]", asString(HAD_NULL_LITERAL))
    @Test fun test_Literal_HAD_MISSING_LITERAL() = assertEquals("[null]", asString(HAD_MISSING_LITERAL))
    @Test fun test_Literal_HAD_INVALID_LITERAL() = assertEquals("[null]", asString(HAD_INVALID_LITERAL))
    @Test fun test_Literal_HAD_NULL_NUMBER() = assertEquals("[null]", asString(HAD_NULL_NUMBER))
    @Test fun test_Literal_HAD_MISSING_NUMBER() = assertEquals("[null]", asString(HAD_MISSING_NUMBER))
    @Test fun test_Literal_HAD_INVALID_NUMBER() = assertEquals("[null]", asString(HAD_INVALID_NUMBER))
    @Test fun test_Literal_HAD_NULL_STRING() = assertEquals("[null]", asString(HAD_NULL_STRING))
    @Test fun test_Literal_HAD_MISSING_STRING() = assertEquals("[null]", asString(HAD_MISSING_STRING))
    @Test fun test_Literal_HAD_INVALID_STRING() = assertEquals("[null]", asString(HAD_INVALID_STRING))
    @Test fun test_Literal_HAD_NULL_ARRAY() = assertEquals("[null]", asString(HAD_NULL_ARRAY))
    @Test fun test_Literal_HAD_MISSING_ARRAY() = assertEquals("[null]", asString(HAD_MISSING_ARRAY))
    @Test fun test_Literal_HAD_INVALID_ARRAY() = assertEquals("[null]", asString(HAD_INVALID_ARRAY))
    @Test fun test_Literal_HAD_NULL_OBJECT() = assertEquals("[null]", asString(HAD_NULL_OBJECT))
    @Test fun test_Literal_HAD_MISSING_OBJECT() = assertEquals("[null]", asString(HAD_MISSING_OBJECT))
    @Test fun test_Literal_HAD_INVALID_OBJECT() = assertEquals("[null]", asString(HAD_INVALID_OBJECT))
    // @formatter:on

}
