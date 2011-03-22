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
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.testFramework.UsefulTestCase;
import junit.framework.AssertionFailedError;
import net.nicoulaj.idea.markdown.lang.lexer.MarkdownLexer;
import net.nicoulaj.idea.markdown.test.TestUtils;
import org.jetbrains.annotations.NonNls;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Black box test for {@link MarkdownLexer}.
 * <p/>
 * Lexes some input files and compares result with expected output.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.4
 */
@RunWith(value = Parameterized.class)
public class MarkdownLexerTestCase extends UsefulTestCase {

    /**
     * The path to the data used for this test.
     */
    @NonNls
    public static final String LEXER_TEST_RESOURCES_DIR = TestUtils.TEST_RESOURCES_DIR + "/net/nicoulaj/idea/markdown/test/lexer";

    /**
     * The pattern used for catching input test data files.
     */
    public static final Pattern TEST_FILES_PATTERN = Pattern.compile(".+.md");

    /**
     * The files containing the expected lexer results are named after the test file file name + this suffix.
     */
    @NonNls
    public static final String TEST_FILES_TOKENS_FILE_EXT = ".tokens.csv";

    /**
     * The file to test.
     */
    private File testFile;

    /**
     * Build a new instance of {@link MarkdownLexerTestCase}.
     *
     * @param testFile the file to test
     */
    public MarkdownLexerTestCase(File testFile) {
        this.testFile = testFile;
    }

    /**
     * Generate the data to use to instantiate this test.
     *
     * @return a {@link Collection} of parameters to pass to {@link #MarkdownLexerTestCase(File testFile)}
     */
    @Parameterized.Parameters
    public static Collection<Object[]> getTestFiles() {
        final List<File> dataFiles = FileUtil.findFilesByMask(TEST_FILES_PATTERN, new File(LEXER_TEST_RESOURCES_DIR));
        final Collection<Object[]> res = new ArrayList<Object[]>(dataFiles.size());
        for (File file : dataFiles) {
            res.add(new Object[]{file});
        }
        return res;
    }

    /**
     * Test the {@link #testFile} lexing.
     */
    @Test
    public void testFileLexing() {

        // Load the test file data.
        String text;
        try {
            text = new String(FileUtil.loadFileText(testFile)).trim();
        } catch (IOException e) {
            throw new AssertionFailedError("Failed loading test file '" + testFile + "': " + e.getMessage());
        }

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
        assertSameLinesWithFile(testFile.getPath() + TEST_FILES_TOKENS_FILE_EXT, result);
    }
}
