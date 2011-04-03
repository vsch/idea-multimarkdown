/*
 * Copyright (c) 2011 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
package net.nicoulaj.idea.markdown.test.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.tree.IElementType;
import net.nicoulaj.idea.markdown.lang.lexer.MarkdownLexer;
import org.jetbrains.annotations.NonNls;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Black box test for {@link net.nicoulaj.idea.markdown.lang.lexer.MarkdownLexer}.
 * <p/>
 * Lexes some input files and compares result with expected output.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.4
 */
public class MarkdownLexerTest extends AbstractMarkdownLexerTestCase {

    /**
     * The files containing the expected lexer results are named after the test file file name + this suffix.
     */
    @NonNls
    public static final String TEST_FILES_TOKENS_FILE_EXT = ".tokens.csv";

    /**
     * Build a new instance of {@link MarkdownLexerTest}.
     *
     * @param testFile the file to test
     */
    public MarkdownLexerTest(File testFile) {
        super(testFile);
    }

    /**
     * Test the {@link #testFile} lexing.
     *
     * @throws java.io.IOException
     */
    @Test
    public void doLexerTest() throws IOException {

        // Load the test file data.
        final String text = getTestFileData();

        // Process the data with a lexer.
        final Lexer lexer = new MarkdownLexer();
        String result = "";
        String chunk = "";
        IElementType tokenType;
        String tokenText;
        String tokenTypeName = "";
        lexer.start(text);
        while (true) {
            tokenType = lexer.getTokenType();
            tokenText = StringUtil.replace(lexer.getBufferSequence().subSequence(lexer.getTokenStart(), lexer.getTokenEnd()).toString(), "\n", "\\n");
            if (tokenType != null && tokenTypeName.equals(tokenType.toString())) {
                chunk += tokenText;
            } else {
                if (tokenTypeName.length() > 0) {
                    result += tokenTypeName + ";\"" + chunk + "\"\n";
                }
                if (tokenType != null) {
                    tokenTypeName = tokenType.toString();
                    chunk = tokenText;
                }
            }
            if (tokenType == null) {
                break;
            }
            lexer.advance();
        }

        // Compare results with the expected ones.
        assertSameLinesWithFile(getTestFile().getPath() + TEST_FILES_TOKENS_FILE_EXT, result);
    }
}
