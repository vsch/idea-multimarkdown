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
 *
 * This file is based on the IntelliJ SimplePlugin tutorial
 *
 */
package com.vladsch.idea.multimarkdown.spellchecking;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static com.vladsch.idea.multimarkdown.TestUtils.assertSuggestionEquals;

@RunWith(value = Parameterized.class)
public class TestSuggestionFixersBasic extends TestCase {
    private final String suggestion;
    private final String SuggestCleanSpacedWords;
    private final String SuggestCapSpacedWords;
    private final String SuggestLowerSpacedWords;
    private final String SuggestUpperSpacedWords;
    private final String SuggestCleanSplicedWords;
    private final String SuggestCapSplicedWords;
    private final String SuggestLowerSplicedWords;
    private final String SuggestUpperSplicedWords;
    private final String SuggestCleanDashedWords;
    private final String SuggestCapDashedWords;
    private final String SuggestLowerDashedWords;
    private final String SuggestUpperDashedWords;
    private final String SuggestCleanSnakedWords;
    private final String SuggestCapSnakedWords;
    private final String SuggestLowerSnakedWords;
    private final String SuggestUpperSnakedWords;
    private final String SuggestWikiRefAsFilNameWithExt;
    private final String SuggestFileNameWithExt;

    public TestSuggestionFixersBasic(String suggestion, String suggestCleanSpacedWords, String suggestCapSpacedWords, String suggestLowerSpacedWords, String suggestUpperSpacedWords, String suggestCleanSplicedWords, String suggestCapSplicedWords, String suggestLowerSplicedWords, String suggestUpperSplicedWords, String suggestCleanDashedWords, String suggestCapDashedWords, String suggestLowerDashedWords, String suggestUpperDashedWords, String suggestCleanSnakedWords, String suggestCapSnakedWords, String suggestLowerSnakedWords, String suggestUpperSnakedWords, String suggestWikiRefAsFilNameWithExt, String suggestFileNameWithExt
    ) {
        this.suggestion = suggestion;
        SuggestCleanSpacedWords = suggestCleanSpacedWords;
        SuggestCapSpacedWords = suggestCapSpacedWords;
        SuggestLowerSpacedWords = suggestLowerSpacedWords;
        SuggestUpperSpacedWords = suggestUpperSpacedWords;
        SuggestCleanSplicedWords = suggestCleanSplicedWords;
        SuggestCapSplicedWords = suggestCapSplicedWords;
        SuggestLowerSplicedWords = suggestLowerSplicedWords;
        SuggestUpperSplicedWords = suggestUpperSplicedWords;
        SuggestCleanDashedWords = suggestCleanDashedWords;
        SuggestCapDashedWords = suggestCapDashedWords;
        SuggestLowerDashedWords = suggestLowerDashedWords;
        SuggestUpperDashedWords = suggestUpperDashedWords;
        SuggestCleanSnakedWords = suggestCleanSnakedWords;
        SuggestCapSnakedWords = suggestCapSnakedWords;
        SuggestLowerSnakedWords = suggestLowerSnakedWords;
        SuggestUpperSnakedWords = suggestUpperSnakedWords;
        SuggestWikiRefAsFilNameWithExt = suggestWikiRefAsFilNameWithExt;
        SuggestFileNameWithExt = suggestFileNameWithExt;
    }

    /* @formatter:off */
    @Test public void testSuggestCleanSpacedWords() { assertSuggestionEquals(suggestion, SuggestCleanSpacedWords, SuggestionFixers.SuggestCleanSpacedWords); }
    @Test public void testSuggestCapSpacedWords() { assertSuggestionEquals(suggestion, SuggestCapSpacedWords, SuggestionFixers.SuggestCapSpacedWords); }
    @Test public void testSuggestLowerSpacedWords() { assertSuggestionEquals(suggestion, SuggestLowerSpacedWords, SuggestionFixers.SuggestLowerSpacedWords); }
    @Test public void testSuggestUpperSpacedWords() { assertSuggestionEquals(suggestion, SuggestUpperSpacedWords, SuggestionFixers.SuggestUpperSpacedWords); }
    @Test public void testSuggestCleanSplicedWords() { assertSuggestionEquals(suggestion, SuggestCleanSplicedWords, SuggestionFixers.SuggestCleanSplicedWords); }
    @Test public void testSuggestCapSplicedWords() { assertSuggestionEquals(suggestion, SuggestCapSplicedWords, SuggestionFixers.SuggestCapSplicedWords); }
    @Test public void testSuggestLowerSplicedWords() { assertSuggestionEquals(suggestion, SuggestLowerSplicedWords, SuggestionFixers.SuggestLowerSplicedWords); }
    @Test public void testSuggestUpperSplicedWords() { assertSuggestionEquals(suggestion, SuggestUpperSplicedWords, SuggestionFixers.SuggestUpperSplicedWords); }
    @Test public void testSuggestCleanDashedWords() { assertSuggestionEquals(suggestion, SuggestCleanDashedWords, SuggestionFixers.SuggestCleanDashedWords); }
    @Test public void testSuggestCapDashedWords() { assertSuggestionEquals(suggestion, SuggestCapDashedWords, SuggestionFixers.SuggestCapDashedWords); }
    @Test public void testSuggestLowerDashedWords() { assertSuggestionEquals(suggestion, SuggestLowerDashedWords, SuggestionFixers.SuggestLowerDashedWords); }
    @Test public void testSuggestUpperDashedWords() { assertSuggestionEquals(suggestion, SuggestUpperDashedWords, SuggestionFixers.SuggestUpperDashedWords); }
    @Test public void testSuggestCleanSnakedWords() { assertSuggestionEquals(suggestion, SuggestCleanSnakedWords, SuggestionFixers.SuggestCleanSnakedWords); }
    @Test public void testSuggestCapSnakedWords() { assertSuggestionEquals(suggestion, SuggestCapSnakedWords, SuggestionFixers.SuggestCapSnakedWords); }
    @Test public void testSuggestLowerSnakedWords() { assertSuggestionEquals(suggestion, SuggestLowerSnakedWords, SuggestionFixers.SuggestLowerSnakedWords); }
    @Test public void testSuggestUpperSnakedWords() { assertSuggestionEquals(suggestion, SuggestUpperSnakedWords, SuggestionFixers.SuggestUpperSnakedWords); }
    @Test public void testSuggestWikiRefAsFilNameWithExt() { assertSuggestionEquals(suggestion, SuggestWikiRefAsFilNameWithExt, SuggestionFixers.SuggestWikiRefAsFilNameWithExt); }
    @Test public void testSuggestFileNameWithExt() { assertSuggestionEquals(suggestion, SuggestFileNameWithExt, SuggestionFixers.SuggestFileNameWithExt); }
    /* @formatter:on */

    @Parameterized.Parameters(name = "{index}: suggest({0})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                /* @formatter:off */
                {
                        "simpledocumentsuggestion",
                        "simpledocumentsuggestion", // SuggestCleanSpacedWords
                        "Simpledocumentsuggestion", // SuggestCapSpacedWords
                        "simpledocumentsuggestion", // SuggestLowerSpacedWords
                        "SIMPLEDOCUMENTSUGGESTION", // SuggestUpperSpacedWords

                        "simpledocumentsuggestion", // SuggestCleanSplicedWords
                        "Simpledocumentsuggestion", // SuggestCapSplicedWords
                        "simpledocumentsuggestion", // SuggestLowerSplicedWords
                        "SIMPLEDOCUMENTSUGGESTION", // SuggestUpperSplicedWords

                        "simpledocumentsuggestion", // SuggestCleanDashedWords
                        "Simpledocumentsuggestion", // SuggestCapDashedWords
                        "simpledocumentsuggestion", // SuggestLowerDashedWords
                        "SIMPLEDOCUMENTSUGGESTION", // SuggestUpperDashedWords

                        "simpledocumentsuggestion", // SuggestCleanSnakedWords
                        "Simpledocumentsuggestion", // SuggestCapSnakedWords
                        "simpledocumentsuggestion", // SuggestLowerSnakedWords
                        "SIMPLEDOCUMENTSUGGESTION", // SuggestUpperSnakedWords

                        "simpledocumentsuggestion.md", // SuggestWikiRefAsFilNameWithExt
                        "simpledocumentsuggestion.md", // SuggestFileNameWithExt
                },
                {
                        "simple/document/suggestion",
                        "simple document suggestion", // SuggestCleanSpacedWords
                        "Simple Document Suggestion", // SuggestCapSpacedWords
                        "simple document suggestion", // SuggestLowerSpacedWords
                        "SIMPLE DOCUMENT SUGGESTION", // SuggestUpperSpacedWords

                        "simpledocumentsuggestion", // SuggestCleanSplicedWords
                        "SimpleDocumentSuggestion", // SuggestCapSplicedWords
                        "simpledocumentsuggestion", // SuggestLowerSplicedWords
                        "SIMPLEDOCUMENTSUGGESTION", // SuggestUpperSplicedWords

                        "simple-document-suggestion", // SuggestCleanDashedWords
                        "Simple-Document-Suggestion", // SuggestCapDashedWords
                        "simple-document-suggestion", // SuggestLowerDashedWords
                        "SIMPLE-DOCUMENT-SUGGESTION", // SuggestUpperDashedWords

                        "simple_document_suggestion", // SuggestCleanSnakedWords
                        "Simple_Document_Suggestion", // SuggestCapSnakedWords
                        "simple_document_suggestion", // SuggestLowerSnakedWords
                        "SIMPLE_DOCUMENT_SUGGESTION", // SuggestUpperSnakedWords

                        "suggestion.md", // SuggestWikiRefAsFilNameWithExt
                        "suggestion.md", // SuggestFileNameWithExt

                },
                {
                        "simple document suggestion",
                        "simple document suggestion", // SuggestCleanSpacedWords
                        "Simple Document Suggestion", // SuggestCapSpacedWords
                        "simple document suggestion", // SuggestLowerSpacedWords
                        "SIMPLE DOCUMENT SUGGESTION", // SuggestUpperSpacedWords

                        "simpledocumentsuggestion", // SuggestCleanSplicedWords
                        "SimpleDocumentSuggestion", // SuggestCapSplicedWords
                        "simpledocumentsuggestion", // SuggestLowerSplicedWords
                        "SIMPLEDOCUMENTSUGGESTION", // SuggestUpperSplicedWords

                        "simple-document-suggestion", // SuggestCleanDashedWords
                        "Simple-Document-Suggestion", // SuggestCapDashedWords
                        "simple-document-suggestion", // SuggestLowerDashedWords
                        "SIMPLE-DOCUMENT-SUGGESTION", // SuggestUpperDashedWords

                        "simple_document_suggestion", // SuggestCleanSnakedWords
                        "Simple_Document_Suggestion", // SuggestCapSnakedWords
                        "simple_document_suggestion", // SuggestLowerSnakedWords
                        "SIMPLE_DOCUMENT_SUGGESTION", // SuggestUpperSnakedWords

                        "simple-document-suggestion.md", // SuggestWikiRefAsFilNameWithExt
                        "simple document suggestion.md", // SuggestFileNameWithExt
                },
                {
                        "simple-document-suggestion",
                        "simple document suggestion", // SuggestCleanSpacedWords
                        "Simple Document Suggestion", // SuggestCapSpacedWords
                        "simple document suggestion", // SuggestLowerSpacedWords
                        "SIMPLE DOCUMENT SUGGESTION", // SuggestUpperSpacedWords

                        "simpledocumentsuggestion", // SuggestCleanSplicedWords
                        "SimpleDocumentSuggestion", // SuggestCapSplicedWords
                        "simpledocumentsuggestion", // SuggestLowerSplicedWords
                        "SIMPLEDOCUMENTSUGGESTION", // SuggestUpperSplicedWords

                        "simple-document-suggestion", // SuggestCleanDashedWords
                        "Simple-Document-Suggestion", // SuggestCapDashedWords
                        "simple-document-suggestion", // SuggestLowerDashedWords
                        "SIMPLE-DOCUMENT-SUGGESTION", // SuggestUpperDashedWords

                        "simple_document_suggestion", // SuggestCleanSnakedWords
                        "Simple_Document_Suggestion", // SuggestCapSnakedWords
                        "simple_document_suggestion", // SuggestLowerSnakedWords
                        "SIMPLE_DOCUMENT_SUGGESTION", // SuggestUpperSnakedWords

                        "simple-document-suggestion.md", // SuggestWikiRefAsFilNameWithExt
                        "simple-document-suggestion.md", // SuggestFileNameWithExt
                },
                {
                        "simple#document#suggestion",
                        "simple document suggestion", // SuggestCleanSpacedWords
                        "Simple Document Suggestion", // SuggestCapSpacedWords
                        "simple document suggestion", // SuggestLowerSpacedWords
                        "SIMPLE DOCUMENT SUGGESTION", // SuggestUpperSpacedWords

                        "simpledocumentsuggestion", // SuggestCleanSplicedWords
                        "SimpleDocumentSuggestion", // SuggestCapSplicedWords
                        "simpledocumentsuggestion", // SuggestLowerSplicedWords
                        "SIMPLEDOCUMENTSUGGESTION", // SuggestUpperSplicedWords

                        "simple-document-suggestion", // SuggestCleanDashedWords
                        "Simple-Document-Suggestion", // SuggestCapDashedWords
                        "simple-document-suggestion", // SuggestLowerDashedWords
                        "SIMPLE-DOCUMENT-SUGGESTION", // SuggestUpperDashedWords

                        "simple_document_suggestion", // SuggestCleanSnakedWords
                        "Simple_Document_Suggestion", // SuggestCapSnakedWords
                        "simple_document_suggestion", // SuggestLowerSnakedWords
                        "SIMPLE_DOCUMENT_SUGGESTION", // SuggestUpperSnakedWords

                        "simple#document#suggestion.md", // SuggestWikiRefAsFilNameWithExt
                        "simple#document#suggestion.md", // SuggestFileNameWithExt
                },
                {
                        "SimpleDocumentSuggestion",
                        "Simple Document Suggestion", // SuggestCleanSpacedWords
                        "Simple Document Suggestion", // SuggestCapSpacedWords
                        "simple document suggestion", // SuggestLowerSpacedWords
                        "SIMPLE DOCUMENT SUGGESTION", // SuggestUpperSpacedWords

                        "SimpleDocumentSuggestion", // SuggestCleanSplicedWords
                        "SimpleDocumentSuggestion", // SuggestCapSplicedWords
                        "simpledocumentsuggestion", // SuggestLowerSplicedWords
                        "SIMPLEDOCUMENTSUGGESTION", // SuggestUpperSplicedWords

                        "Simple-Document-Suggestion", // SuggestCleanDashedWords
                        "Simple-Document-Suggestion", // SuggestCapDashedWords
                        "simple-document-suggestion", // SuggestLowerDashedWords
                        "SIMPLE-DOCUMENT-SUGGESTION", // SuggestUpperDashedWords

                        "Simple_Document_Suggestion", // SuggestCleanSnakedWords
                        "Simple_Document_Suggestion", // SuggestCapSnakedWords
                        "simple_document_suggestion", // SuggestLowerSnakedWords
                        "SIMPLE_DOCUMENT_SUGGESTION", // SuggestUpperSnakedWords

                        "SimpleDocumentSuggestion.md", // SuggestWikiRefAsFilNameWithExt
                        "SimpleDocumentSuggestion.md", // SuggestFileNameWithExt
                },
                /* @formatter:on */
        });
    }
}
