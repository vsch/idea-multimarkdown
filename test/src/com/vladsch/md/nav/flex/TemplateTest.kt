/*
 * Copyright (c) 2015-2019 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
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

package com.vladsch.md.nav.flex

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

@RunWith(value = Parameterized::class)
class TemplateTest(
    val number: Int,
    val text: String,
    val expected: String
) {

    @Test
    fun replaceText() {
    }

    companion object {
        @Parameterized.Parameters(name = "{0}")
        @JvmStatic
        fun data(): List<Array<Any>> {
            val data = ArrayList<Array<Any>>()
            var count = 0
            data.add(arrayOf<Any>(++count, """prefix
        someCommands;//zzzoptionszzz(REMOVE, OPT2)
suffix""", """prefix
suffix"""))

            data.add(arrayOf<Any>(++count, """prefix
        // zzzoptionszzz(REMOVE, OPT1)
suffix""", """prefix
suffix"""))

            data.add(arrayOf<Any>(++count, """prefix
        someCommands;//zzzoptionszzz(REMOVE, OPT2)
suffix""", """prefix
suffix"""))

            data.add(arrayOf<Any>(++count, """prefix
        someCommands1;//zzzoptionszzz(REMOVE, OPT2)
        someCommands2;//zzzoptionszzz(REMOVE, OPT2)
        someCommands3;//zzzoptionszzz(REMOVE, OPT2)
        someCommands4;//zzzoptionszzz(REMOVE, OPT2)
suffix""", """prefix
suffix"""))

            data.add(arrayOf<Any>(++count, """prefix
        someCommands;//zzzoptionszzz(OPT2)
suffix""", """prefix
        // someCommands;
suffix"""))

            data.add(arrayOf<Any>(++count, """prefix
        someCommands1;//zzzoptionszzz(OPT2)
        someCommands2;//zzzoptionszzz(OPT2)
        someCommands3;//zzzoptionszzz(OPT2)
        someCommands4;//zzzoptionszzz(OPT2)
suffix""", """prefix
        // someCommands1;
        // someCommands2;
        // someCommands3;
        // someCommands4;
suffix"""))

            data.add(arrayOf<Any>(++count, """prefix
import com.vladsch.flexmark.internal.util.KeepType;//zzzoptionszzz(OPT2)
suffix""", """prefix
// import com.vladsch.flexmark.internal.util.KeepType;
suffix"""))

            data.add(arrayOf<Any>(++count, """prefix
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customBlockParserFactory(new ZzzzzzBlockParser.Factory());//zzzoptionszzz(REMOVE, OPT1)
        parserBuilder.paragraphPreProcessorFactory(ZzzzzzParagraphPreProcessor.Factory());//zzzoptionszzz(REMOVE, OPT2)
        parserBuilder.blockPreProcessorFactory(new ZzzzzzBlockPreProcessorFactory());//zzzoptionszzz(REMOVE, OPT3)
        parserBuilder.customDelimiterProcessor(new ZzzzzzDelimiterProcessor());//zzzoptionszzz(REMOVE, OPT1, OPT2)
        parserBuilder.linkRefProcessor(new ZzzzzzLinkRefProcessor(parserBuilder));//zzzoptionszzz(REMOVE, OPT2, OPT3)
        parserBuilder.postProcessor(new ZzzzzzPostProcessor());//zzzoptionszzz(REMOVE, OPT2)
    }
suffix""", """prefix
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customBlockParserFactory(new ZzzzzzBlockParser.Factory());
        parserBuilder.blockPreProcessorFactory(new ZzzzzzBlockPreProcessorFactory());
        parserBuilder.customDelimiterProcessor(new ZzzzzzDelimiterProcessor());
        parserBuilder.linkRefProcessor(new ZzzzzzLinkRefProcessor(parserBuilder));
    }
suffix"""))

            data.add(arrayOf<Any>(++count, """prefix
    public void extend(Parser.Builder parserBuilder) {
        //zzzoptionszzz(REMOVE, OPT2)
        //zzzoptionszzz(OPT2)
        //zzzoptionszzz(REMOVE, OPT2)
        //zzzoptionszzz(REMOVE, OPT2)
        //zzzoptionszzz(OPT1)
        //zzzoptionszzz(OPT2)
        //zzzoptionszzz(OPT3)
        //zzzoptionszzz(OPT2)
        //zzzoptionszzz(OPT1)
        //zzzoptionszzz(OPT2)
        //zzzoptionszzz(OPT3)
        parserBuilder.customBlockParserFactory(new ZzzzzzBlockParser.Factory());//zzzoptionszzz(REMOVE, OPT1)
        parserBuilder.paragraphPreProcessorFactory(ZzzzzzParagraphPreProcessor.Factory());//zzzoptionszzz(REMOVE, OPT2)
        parserBuilder.blockPreProcessorFactory(new ZzzzzzBlockPreProcessorFactory());//zzzoptionszzz(REMOVE, OPT3)
        parserBuilder.customDelimiterProcessor(new ZzzzzzDelimiterProcessor());//zzzoptionszzz(REMOVE, OPT1, OPT2)
        parserBuilder.linkRefProcessor(new ZzzzzzLinkRefProcessor(parserBuilder));//zzzoptionszzz(REMOVE, OPT2, OPT3)
        parserBuilder.postProcessor(new ZzzzzzPostProcessor());//zzzoptionszzz(REMOVE, OPT2)
    }
suffix""", """prefix
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customBlockParserFactory(new ZzzzzzBlockParser.Factory());
        parserBuilder.blockPreProcessorFactory(new ZzzzzzBlockPreProcessorFactory());
        parserBuilder.customDelimiterProcessor(new ZzzzzzDelimiterProcessor());
        parserBuilder.linkRefProcessor(new ZzzzzzLinkRefProcessor(parserBuilder));
    }
suffix"""))

            return data
        }
    }

    @Test
    fun filterLines() {
        val template = Template("zzzoptionszzz(", ")", "zzzzzz")
        template.optionSet.addAll(listOf("OPT1", "OPT3"))

        assertEquals(expected, template.filterLines(text))
    }
}
