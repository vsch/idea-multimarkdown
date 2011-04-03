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

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.testFramework.UsefulTestCase;
import net.nicoulaj.idea.markdown.test.TestUtils;
import org.jetbrains.annotations.NonNls;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Base class for testing {@link net.nicoulaj.idea.markdown.lang.lexer.MarkdownLexer} against all available test input documents.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.4
 */
@RunWith(value = Parameterized.class)
public abstract class AbstractMarkdownLexerTestCase extends UsefulTestCase {

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
     * The file to test.
     */
    private File testFile;

    /**
     * Build a new instance of {@link AbstractMarkdownLexerTestCase}.
     *
     * @param testFile the file to test
     */
    protected AbstractMarkdownLexerTestCase(File testFile) {
        this.testFile = testFile;
    }

    /**
     * Generate the data to use to instantiate this test.
     *
     * @return a {@link Collection} of parameters to pass to {@link #AbstractMarkdownLexerTestCase(java.io.File)} implementations constructor.
     */
    @Parameterized.Parameters
    public static Collection<Object[]> getTestFiles() {
        final List<File> dataFiles = FileUtil.findFilesByMask(TEST_FILES_PATTERN, new File(LEXER_TEST_RESOURCES_DIR));
        Collections.sort(dataFiles);
        final Collection<Object[]> res = new ArrayList<Object[]>(dataFiles.size());
        for (File file : dataFiles) {
            res.add(new Object[]{file});
        }
        return res;
    }

    /**
     * Get the file to run the test against.
     *
     * @return a {@link File} from {@link #LEXER_TEST_RESOURCES_DIR}
     */
    protected File getTestFile() {
        return testFile;
    }

    /**
     * Get the test file data as a {@link String}.
     *
     * @return the test file data in a single {@link String}
     * @throws IOException if the data could not be loaded
     */
    protected String getTestFileData() throws IOException {
        return new String(FileUtil.loadFileText(testFile));
    }

    /**
     * Actual implementation of the test to run against {@link #testFile}.
     *
     * @throws Exception if an error occured while running the test
     */
    abstract public void doLexerTest() throws Exception;
}
