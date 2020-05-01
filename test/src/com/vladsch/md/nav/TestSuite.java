/*
 * Copyright (c) 2015-2019 Vladimir Schneider <vladimir.schneider@gmail.com>
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
package com.vladsch.md.nav;

import com.vladsch.md.nav.editor.util.HtmlCompatibilityTest;
import com.vladsch.md.nav.language.completion.CompletionTestSuite;
import com.vladsch.md.nav.parser.MdParserTest;
import com.vladsch.md.nav.parser.MdPlainTextLexerTest;
import com.vladsch.md.nav.parser.cache.MdCachedFileElementsTest;
import com.vladsch.md.nav.settings.SerializersTest;
import com.vladsch.md.nav.spellchecking.SpellcheckingTestSuite;
import com.vladsch.md.nav.util.UtilTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UtilTestSuite.class,
        HtmlCompatibilityTest.class,
        MdPlainTextLexerTest.class,
        SpellcheckingTestSuite.class,
//        TypingBasicHandlerSpecTest.class,
        SerializersTest.class,
        MdCachedFileElementsTest.class,
        CompletionTestSuite.class,
        MdParserTest.class,
})
public class TestSuite {
}
