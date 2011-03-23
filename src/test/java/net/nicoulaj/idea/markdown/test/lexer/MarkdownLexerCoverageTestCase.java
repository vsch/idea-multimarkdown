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
import net.nicoulaj.idea.markdown.lang.lexer.MarkdownLexer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;

/**
 * Coverage test for {@link MarkdownLexer}.
 * <p/>
 * Lexes some input files and compares tokens lengths sum with input document length.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.4
 */
@RunWith(value = Parameterized.class)
public class MarkdownLexerCoverageTestCase extends AbstractMarkdownLexerTestCase {

    /**
     * Build a new instance of {@link MarkdownLexerCoverageTestCase}.
     *
     * @param testFile the file to test
     */
    public MarkdownLexerCoverageTestCase(File testFile) {
        super(testFile);
    }

    /**
     * Test the {@link #testFile} lexing coverage.
     *
     * @throws java.io.IOException
     */
    @Test
    public void doLexerTest() throws IOException {

        // Load the test file data.
        final String text = getTestFileData();

        // Process the data with a lexer.
        final Lexer lexer = new MarkdownLexer();
        int count = 0;
        lexer.start(text);
        while (lexer.getTokenType() != null) {
            count += lexer.getBufferSequence().subSequence(lexer.getTokenStart(), lexer.getTokenEnd()).toString().length();
            lexer.advance();
        }

        // Compare results with the expected ones.
        assertEquals(text.length(), count);
    }
}
