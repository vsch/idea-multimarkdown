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
package com.vladsch.md.nav.editor.util

import com.vladsch.md.nav.editor.jbcef.JBCefHtmlPanelProvider
import com.vladsch.md.nav.editor.resources.GitHubCollapseInCommentScriptProvider
import com.vladsch.md.nav.editor.resources.GitHubCollapseMarkdownScriptProvider
import com.vladsch.md.nav.editor.resources.HljsScriptProvider
import com.vladsch.md.nav.editor.resources.PrismScriptProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertTrue

class HtmlCompatibilityTest {

    private val panel = "com.vladsch.md.nav.html.test.panel.provider"
    private val resource = "com.vladsch.md.nav.html.test.css.provider"
    val available = HtmlCompatibility(panel, 3f, 1f, 0f, arrayOf(), arrayOf())

    @Test
    fun test_Basic_Same() {
        assertEquals("Same level or any failed", true, HtmlCompatibility.isCompatibleWith(available, HtmlCompatibility(resource, 3f, 1f, 0f, arrayOf(), arrayOf())))
        assertEquals("Same level or any failed", true, HtmlCompatibility.isCompatibleWith(available, HtmlCompatibility(resource, 3f, 1f, null, arrayOf(), arrayOf())))
        assertEquals("Same level or any failed", true, HtmlCompatibility.isCompatibleWith(available, HtmlCompatibility(resource, 3f, null, 0f, arrayOf(), arrayOf())))
        assertEquals("Same level or any failed", true, HtmlCompatibility.isCompatibleWith(available, HtmlCompatibility(resource, null, 1f, 0f, arrayOf(), arrayOf())))
        assertEquals("Same level or any failed", true, HtmlCompatibility.isCompatibleWith(available, HtmlCompatibility(resource, 3f, null, 0f, arrayOf(), arrayOf())))
        assertEquals("Same level or any failed", true, HtmlCompatibility.isCompatibleWith(available, HtmlCompatibility(resource, null, null, null, arrayOf(), arrayOf())))

        assertEquals("Same level or any failed", true, available.isForRequired(HtmlCompatibility(resource, 3f, 1f, 0f, arrayOf(), arrayOf())))
        assertEquals("Same level or any failed", true, available.isForRequired(HtmlCompatibility(resource, 3f, 1f, null, arrayOf(), arrayOf())))
        assertEquals("Same level or any failed", true, available.isForRequired(HtmlCompatibility(resource, 3f, null, 0f, arrayOf(), arrayOf())))
        assertEquals("Same level or any failed", true, available.isForRequired(HtmlCompatibility(resource, null, 1f, 0f, arrayOf(), arrayOf())))
        assertEquals("Same level or any failed", true, available.isForRequired(HtmlCompatibility(resource, 3f, null, 0f, arrayOf(), arrayOf())))
        assertEquals("Same level or any failed", true, available.isForRequired(HtmlCompatibility(resource, null, null, null, arrayOf(), arrayOf())))

        assertEquals("Same level or any failed", true, HtmlCompatibility(resource, 3f, 1f, 0f, arrayOf(), arrayOf()).isForAvailable(available))
        assertEquals("Same level or any failed", true, HtmlCompatibility(resource, 3f, 1f, null, arrayOf(), arrayOf()).isForAvailable(available))
        assertEquals("Same level or any failed", true, HtmlCompatibility(resource, 3f, null, 0f, arrayOf(), arrayOf()).isForAvailable(available))
        assertEquals("Same level or any failed", true, HtmlCompatibility(resource, null, 1f, 0f, arrayOf(), arrayOf()).isForAvailable(available))
        assertEquals("Same level or any failed", true, HtmlCompatibility(resource, 3f, null, 0f, arrayOf(), arrayOf()).isForAvailable(available))
        assertEquals("Same level or any failed", true, HtmlCompatibility(resource, null, null, null, arrayOf(), arrayOf()).isForAvailable(available))
    }

    @Test
    fun test_Basic_NotCompatible() {
        assertEquals("Not Compatible failed", false, HtmlCompatibility.isCompatibleWith(available, HtmlCompatibility(resource, 3f, 3f, 0f, arrayOf(), arrayOf())))
        assertEquals("Not Compatible failed", false, HtmlCompatibility.isCompatibleWith(available, HtmlCompatibility(resource, 3f, 1f, 0.1f, arrayOf(), arrayOf())))
        assertEquals("Not Compatible failed", false, HtmlCompatibility.isCompatibleWith(available, HtmlCompatibility(resource, 3.1f, 1f, 0f, arrayOf(), arrayOf())))
        assertEquals("Not Compatible failed", false, HtmlCompatibility.isCompatibleWith(available, HtmlCompatibility(resource, 3.1f, null, null, arrayOf(), arrayOf())))

        assertEquals("Not Compatible failed", false, available.isForRequired(HtmlCompatibility(resource, 3f, 3f, 0f, arrayOf(), arrayOf())))
        assertEquals("Not Compatible failed", false, available.isForRequired(HtmlCompatibility(resource, 3f, 1f, 0.1f, arrayOf(), arrayOf())))
        assertEquals("Not Compatible failed", false, available.isForRequired(HtmlCompatibility(resource, 3.1f, 1f, 0f, arrayOf(), arrayOf())))
        assertEquals("Not Compatible failed", false, available.isForRequired(HtmlCompatibility(resource, 3.1f, null, null, arrayOf(), arrayOf())))

        assertEquals("Not Compatible failed", false, HtmlCompatibility(resource, 3f, 3f, 0f, arrayOf(), arrayOf()).isForAvailable(available))
        assertEquals("Not Compatible failed", false, HtmlCompatibility(resource, 3f, 1f, 0.1f, arrayOf(), arrayOf()).isForAvailable(available))
        assertEquals("Not Compatible failed", false, HtmlCompatibility(resource, 3.1f, 1f, 0f, arrayOf(), arrayOf()).isForAvailable(available))
        assertEquals("Not Compatible failed", false, HtmlCompatibility(resource, 3.1f, null, null, arrayOf(), arrayOf()).isForAvailable(available))
    }

    @Test
    fun test_Basic_Compatible() {
        assertEquals("Compatible failed", true, HtmlCompatibility.isCompatibleWith(available, HtmlCompatibility(resource, 3f, 0.9f, 0f, arrayOf(), arrayOf())))
        assertEquals("Compatible failed", true, HtmlCompatibility.isCompatibleWith(available, HtmlCompatibility(resource, 3f, 1f, 0f, arrayOf(), arrayOf())))
        assertEquals("Compatible failed", true, HtmlCompatibility.isCompatibleWith(available, HtmlCompatibility(resource, 2.9f, 1f, 0f, arrayOf(), arrayOf())))
        assertEquals("Compatible failed", true, HtmlCompatibility.isCompatibleWith(available, HtmlCompatibility(resource, 3f, 1f, null, arrayOf(), arrayOf())))

        assertEquals("Compatible failed", true, available.isForRequired(HtmlCompatibility(resource, 3f, 0.9f, 0f, arrayOf(), arrayOf())))
        assertEquals("Compatible failed", true, available.isForRequired(HtmlCompatibility(resource, 3f, 1f, 0f, arrayOf(), arrayOf())))
        assertEquals("Compatible failed", true, available.isForRequired(HtmlCompatibility(resource, 2.9f, 1f, 0f, arrayOf(), arrayOf())))
        assertEquals("Compatible failed", true, available.isForRequired(HtmlCompatibility(resource, 3f, 1f, null, arrayOf(), arrayOf())))

        assertEquals("Compatible failed", true, HtmlCompatibility(resource, 3f, 0.9f, 0f, arrayOf(), arrayOf()).isForAvailable(available))
        assertEquals("Compatible failed", true, HtmlCompatibility(resource, 3f, 1f, 0f, arrayOf(), arrayOf()).isForAvailable(available))
        assertEquals("Compatible failed", true, HtmlCompatibility(resource, 2.9f, 1f, 0f, arrayOf(), arrayOf()).isForAvailable(available))
        assertEquals("Compatible failed", true, HtmlCompatibility(resource, 3f, 1f, null, arrayOf(), arrayOf()).isForAvailable(available))
    }

    @Test
    fun test_JavaFxJbCef_Compatible() {
        val panel = JBCefHtmlPanelProvider.COMPATIBILITY
        
        assertTrue(GitHubCollapseMarkdownScriptProvider.COMPATIBILITY.isForAvailable(panel))
        assertTrue(GitHubCollapseInCommentScriptProvider.COMPATIBILITY.isForAvailable(panel))
        assertTrue(HljsScriptProvider.COMPATIBILITY.isForAvailable(panel))
        assertTrue(PrismScriptProvider.COMPATIBILITY.isForAvailable(panel))
    }
}

