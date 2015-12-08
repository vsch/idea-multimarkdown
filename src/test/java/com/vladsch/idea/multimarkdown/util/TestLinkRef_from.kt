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
package com.vladsch.idea.multimarkdown.util

import com.vladsch.idea.multimarkdown.printData
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals

class TestLinkRef_from {

    @Test
    fun test_explicitFromWiki_1() {
        val containingFile = FileRef("/Users/vlad/src/MarkdownTest/MardownTest.wiki/Home.md")
        val wikiLinkRef = WikiLinkRef(containingFile, "Home", null, null)
        val linkRef = LinkRef.from(wikiLinkRef)

        assertEquals("#", linkRef.filePathWithAnchor)
    }

    @Test
    fun test_explicitFromWiki_2() {
        val containingFile = FileRef("/Users/vlad/src/MarkdownTest/MardownTest.wiki/Home.md")
        val wikiLinkRef = WikiLinkRef(containingFile, "Home", "#anchor", null)
        val linkRef = LinkRef.from(wikiLinkRef)

        assertEquals("#anchor", linkRef.filePathWithAnchor)
    }

    @Test
    fun test_explicitFromWiki_3() {
        val containingFile = FileRef("/Users/vlad/src/MarkdownTest/MardownTest.wiki/single-line-test.md")
        val wikiLinkRef = WikiLinkRef(containingFile, "Home", null, null)
        val linkRef = LinkRef.from(wikiLinkRef)

        assertEquals("Home", linkRef.filePathWithAnchor)
    }

    @Test
    fun test_explicitFromWiki_4() {
        val containingFile = FileRef("/Users/vlad/src/MarkdownTest/MardownTest.wiki/single-line-test.md")
        val wikiLinkRef = WikiLinkRef(containingFile, "Home", "#anchor", null)
        val linkRef = LinkRef.from(wikiLinkRef)

        assertEquals("Home#anchor", linkRef.filePathWithAnchor)
    }
}

