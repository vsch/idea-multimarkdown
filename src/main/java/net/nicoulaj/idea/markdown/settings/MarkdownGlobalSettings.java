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
package net.nicoulaj.idea.markdown.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.pegdown.Extensions;

/**
 * Persistent global settings object for the Markdown plugin.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.6
 */
@State(
name = "MarkdownSettings",
storages = @Storage(id = "other", file = "$APP_CONFIG$/markdown.xml")
)
public class MarkdownGlobalSettings implements PersistentStateComponent<Element> {

    /**
     * Whether the "SmartyPants style pretty ellipsises, dashes and apostrophes" extension should be enabled.
     */
    public boolean smarts = false;

    /**
     * Whether the "SmartyPants style pretty single and double quotes" extension should be enabled.
     */
    public boolean quotes = false;

    /**
     * Whether the "PHP Markdown Extra style abbreviations" extension should be enabled.
     */
    public boolean abbreviations = false;

    /**
     * Whether the "PHP Markdown Extra style definition lists" extension should be enabled.
     */
    public boolean definitions = false;

    /**
     * Whether the "PHP Markdown Extra style fenced code blocks" extension should be enabled.
     */
    public boolean fencedCodeBlocks = false;

    /**
     * Whether the "Github style hard wraps parsing as HTML linebreaks" extension should be enabled.
     */
    public boolean hardWraps = false;

    /**
     * Whether the "Github style plain auto-links" extension should be enabled.
     */
    public boolean autoLinks = false;

    /**
     * Whether the "MultiMarkdown style tables support" extension should be enabled.
     */
    public boolean tables = false;

    /**
     * Whether the "No follow links" extension should be enabled.
     */
    public boolean noFollowLinks = false;

    /**
     * Whether the "Suppress HTML blocks" extension should be enabled.
     */
    public boolean suppressHTMLBlocks = false;

    /**
     * Whether the "Suppress inline HTML tags" extension should be enabled.
     */
    public boolean suppressInlineHTML = false;

    /**
     * Get the instance of this service.
     *
     * @return the unique {@link MarkdownGlobalSettings} instance.
     */
    public static MarkdownGlobalSettings getInstance() {
        return ServiceManager.getService(MarkdownGlobalSettings.class);
    }

    /**
     * Get the settings state as a DOM element.
     *
     * @return an ready to serialize DOM {@link Element}.
     * @see {@link #loadState(org.jdom.Element)}
     */
    public Element getState() {
        final Element element = new Element("MarkdownSettings");
        element.setAttribute("smarts", Boolean.toString(smarts));
        element.setAttribute("quotes", Boolean.toString(quotes));
        element.setAttribute("abbreviations", Boolean.toString(abbreviations));
        element.setAttribute("hardWraps", Boolean.toString(hardWraps));
        element.setAttribute("autoLinks", Boolean.toString(autoLinks));
        element.setAttribute("tables", Boolean.toString(tables));
        element.setAttribute("definitions", Boolean.toString(definitions));
        element.setAttribute("fencedCodeBlocks", Boolean.toString(fencedCodeBlocks));
        element.setAttribute("suppressHTMLBlocks", Boolean.toString(suppressHTMLBlocks));
        element.setAttribute("suppressInlineHTML", Boolean.toString(suppressInlineHTML));
        element.setAttribute("noFollowLinks", Boolean.toString(noFollowLinks));
        return element;
    }

    /**
     * Load the settings state from the DOM {@link Element}.
     *
     * @param element the {@link Element} to load values from.
     * @see {@link #getState()}
     */
    public void loadState(@NotNull Element element) {
        smarts = Boolean.parseBoolean(element.getAttributeValue("smarts"));
        quotes = Boolean.parseBoolean(element.getAttributeValue("quotes"));
        abbreviations = Boolean.parseBoolean(element.getAttributeValue("abbreviations"));
        hardWraps = Boolean.parseBoolean(element.getAttributeValue("hardWraps"));
        autoLinks = Boolean.parseBoolean(element.getAttributeValue("autoLinks"));
        tables = Boolean.parseBoolean(element.getAttributeValue("tables"));
        definitions = Boolean.parseBoolean(element.getAttributeValue("definitions"));
        fencedCodeBlocks = Boolean.parseBoolean(element.getAttributeValue("fencedCodeBlocks"));
        suppressHTMLBlocks = Boolean.parseBoolean(element.getAttributeValue("suppressHTMLBlocks"));
        suppressInlineHTML = Boolean.parseBoolean(element.getAttributeValue("suppressInlineHTML"));
        noFollowLinks = Boolean.parseBoolean(element.getAttributeValue("noFollowLinks"));
    }

    /**
     * Get the extensions value to setup PegDown parser with.
     *
     * @return the value to use with {@link org.pegdown.PegDownProcessor(int)}
     */
    public int getExtensionsValue() {
        return
        (smarts ? Extensions.SMARTS : 0) +
        (quotes ? Extensions.QUOTES : 0) +
        (abbreviations ? Extensions.ABBREVIATIONS : 0) +
        (hardWraps ? Extensions.HARDWRAPS : 0) +
        (autoLinks ? Extensions.AUTOLINKS : 0) +
        (tables ? Extensions.TABLES : 0) +
        (definitions ? Extensions.DEFINITIONS : 0) +
        (fencedCodeBlocks ? Extensions.FENCED_CODE_BLOCKS : 0) +
        (suppressHTMLBlocks ? Extensions.SUPPRESS_HTML_BLOCKS : 0) +
        (suppressInlineHTML ? Extensions.SUPPRESS_INLINE_HTML : 0) +
        (noFollowLinks ? Extensions.NO_FOLLOW_LINKS : 0);
    }
}
