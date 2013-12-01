/*
 * Copyright (c) 2011-2013 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

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

    /** A set of listeners to this object state changes. */
    protected Set<WeakReference<MarkdownGlobalSettingsListener>> listeners;

    /** Parsing timeout (milliseconds). */
    private int parsingTimeout = 10000;

    /** Whether the "SmartyPants style pretty ellipsises, dashes and apostrophes" extension should be enabled. */
    private boolean smarts = false;

    /** Whether the "SmartyPants style pretty single and double quotes" extension should be enabled. */
    private boolean quotes = false;

    /** Whether the "PHP Markdown Extra style abbreviations" extension should be enabled. */
    private boolean abbreviations = false;

    /** Whether the "PHP Markdown Extra style definition lists" extension should be enabled. */
    private boolean definitions = false;

    /** Whether the "PHP Markdown Extra style fenced code blocks" extension should be enabled. */
    private boolean fencedCodeBlocks = false;

    /** Whether the "Github style hard wraps parsing as HTML linebreaks" extension should be enabled. */
    private boolean hardWraps = false;

    /** Whether the "Github style plain auto-links" extension should be enabled. */
    private boolean autoLinks = false;

    /** Whether the "Wiki-style links" extension should be enabled. */
    private boolean wikiLinks = false;

    /** Whether the "MultiMarkdown style tables support" extension should be enabled. */
    private boolean tables = false;

    /** Whether the "Suppress HTML blocks" extension should be enabled. */
    private boolean suppressHTMLBlocks = false;

    /** Whether the "Suppress inline HTML tags" extension should be enabled. */
    private boolean suppressInlineHTML = false;

    /**
     * Get the instance of this service.
     *
     * @return the unique {@link MarkdownGlobalSettings} instance.
     */
    public static MarkdownGlobalSettings getInstance() {
        return ServiceManager.getService(MarkdownGlobalSettings.class);
    }

    /**
     * Get the parsing timeout (milliseconds).
     *
     * @return parsing timeout (milliseconds)
     */
    public int getParsingTimeout() {
        return parsingTimeout;
    }

    /**
     * Set parsing timeout (milliseconds).
     *
     * @param parsingTimeout parsing timeout (milliseconds)
     */
    public void setParsingTimeout(int parsingTimeout) {
        if (this.parsingTimeout != parsingTimeout) {
            this.parsingTimeout = parsingTimeout;
            notifyListeners();
        }
    }

    /**
     * Whether the "Suppress inline HTML tags" extension should be enabled.
     *
     * @return {@link #suppressInlineHTML}
     */
    public boolean isSuppressInlineHTML() {
        return suppressInlineHTML;
    }

    /**
     * Whether the "Suppress inline HTML tags" extension should be enabled.
     *
     * @param suppressInlineHTML whether the "Suppress inline HTML tags" extension should be enabled.
     */
    public void setSuppressInlineHTML(boolean suppressInlineHTML) {
        if (this.suppressInlineHTML != suppressInlineHTML) {
            this.suppressInlineHTML = suppressInlineHTML;
            notifyListeners();
        }
    }

    /**
     * Whether the "Suppress HTML blocks" extension should be enabled.
     *
     * @return {@link #suppressHTMLBlocks}
     */
    public boolean isSuppressHTMLBlocks() {
        return suppressHTMLBlocks;
    }

    /**
     * Whether the "Suppress HTML blocks" extension should be enabled.
     *
     * @param suppressHTMLBlocks whether the "Suppress HTML blocks" extension should be enabled.
     */
    public void setSuppressHTMLBlocks(boolean suppressHTMLBlocks) {
        if (this.suppressHTMLBlocks != suppressHTMLBlocks) {
            this.suppressHTMLBlocks = suppressHTMLBlocks;
            notifyListeners();
        }
    }

    /**
     * Whether the "MultiMarkdown style tables support" extension should be enabled.
     *
     * @return {@link #tables}
     */
    public boolean isTables() {
        return tables;
    }

    /**
     * Whether the "MultiMarkdown style tables support" extension should be enabled.
     *
     * @param tables whether the "MultiMarkdown style tables support" extension should be enabled.
     */
    public void setTables(boolean tables) {
        if (this.tables != tables) {
            this.tables = tables;
            notifyListeners();
        }
    }

    /**
     * Whether the "Wiki-style links" extension should be enabled.
     *
     * @return {@link #wikiLinks}
     */
    public boolean isWikiLinks() {
        return wikiLinks;
    }

    /**
     * Whether the "Wiki-style links" extension should be enabled.
     *
     * @param wikiLinks whether the "Wiki-style links" extension should be enabled.
     */
    public void setWikiLinks(boolean wikiLinks) {
        if (this.wikiLinks != wikiLinks) {
            this.wikiLinks = wikiLinks;
            notifyListeners();
        }
    }

    /**
     * Whether the "Github style plain auto-links" extension should be enabled.
     *
     * @return {@link #autoLinks}
     */
    public boolean isAutoLinks() {
        return autoLinks;
    }

    /**
     * Whether the "Github style plain auto-links" extension should be enabled.
     *
     * @param autoLinks whether the "Github style plain auto-links" extension should be enabled.
     */
    public void setAutoLinks(boolean autoLinks) {
        if (this.autoLinks != autoLinks) {
            this.autoLinks = autoLinks;
            notifyListeners();
        }
    }

    /**
     * Whether the "Github style hard wraps parsing as HTML linebreaks" extension should be enabled.
     *
     * @return {@link #hardWraps}
     */
    public boolean isHardWraps() {
        return hardWraps;
    }

    /**
     * Whether the "Github style hard wraps parsing as HTML linebreaks" extension should be enabled.
     *
     * @param hardWraps whether the "Github style hard wraps parsing as HTML linebreaks" extension should be enabled.
     */
    public void setHardWraps(boolean hardWraps) {
        if (this.hardWraps != hardWraps) {
            this.hardWraps = hardWraps;
            notifyListeners();
        }
    }

    /**
     * Whether the "PHP Markdown Extra style fenced code blocks" extension should be enabled.
     *
     * @return {@link #fencedCodeBlocks}
     */
    public boolean isFencedCodeBlocks() {
        return fencedCodeBlocks;
    }

    /**
     * Whether the "PHP Markdown Extra style fenced code blocks" extension should be enabled.
     *
     * @param fencedCodeBlocks whether the "PHP Markdown Extra style fenced code blocks" extension should be enabled.
     */
    public void setFencedCodeBlocks(boolean fencedCodeBlocks) {
        if (this.fencedCodeBlocks != fencedCodeBlocks) {
            this.fencedCodeBlocks = fencedCodeBlocks;
            notifyListeners();
        }
    }

    /**
     * Whether the "PHP Markdown Extra style definition lists" extension should be enabled.
     *
     * @return {@link #definitions}
     */
    public boolean isDefinitions() {
        return definitions;
    }

    /**
     * Whether the "PHP Markdown Extra style definition lists" extension should be enabled.
     *
     * @param definitions whether the "PHP Markdown Extra style definition lists" extension should be enabled.
     */
    public void setDefinitions(boolean definitions) {
        if (this.definitions != definitions) {
            this.definitions = definitions;
            notifyListeners();
        }
    }

    /**
     * Whether the "PHP Markdown Extra style abbreviations" extension should be enabled.
     *
     * @return {@link #abbreviations}
     */
    public boolean isAbbreviations() {
        return abbreviations;
    }

    /**
     * Whether the "PHP Markdown Extra style abbreviations" extension should be enabled.
     *
     * @param abbreviations whether the "PHP Markdown Extra style abbreviations" extension should be enabled.
     */
    public void setAbbreviations(boolean abbreviations) {
        if (this.abbreviations != abbreviations) {
            this.abbreviations = abbreviations;
            notifyListeners();
        }
    }

    /**
     * Whether the "SmartyPants style pretty single and double quotes" extension should be enabled.
     *
     * @return {@link #quotes}
     */
    public boolean isQuotes() {
        return quotes;
    }

    /**
     * Whether the "SmartyPants style pretty single and double quotes" extension should be enabled.
     *
     * @param quotes whether the "SmartyPants style pretty single and double quotes" extension should be enabled.
     */
    public void setQuotes(boolean quotes) {
        if (this.quotes != quotes) {
            this.quotes = quotes;
            notifyListeners();
        }
    }

    /**
     * Whether the "SmartyPants style pretty ellipsises, dashes and apostrophes" extension should be enabled.
     *
     * @return {@link #smarts}
     */
    public boolean isSmarts() {
        return smarts;
    }

    /**
     * Whether the "SmartyPants style pretty ellipsises, dashes and apostrophes" extension should be enabled.
     *
     * @param smarts whether the "SmartyPants style pretty ellipsises, dashes and apostrophes" extension should be enabled.
     */
    public void setSmarts(boolean smarts) {
        if (this.smarts != smarts) {
            this.smarts = smarts;
            notifyListeners();
        }
    }

    /**
     * Get the settings state as a DOM element.
     *
     * @return an ready to serialize DOM {@link Element}.
     * @see {@link #loadState(org.jdom.Element)}
     */
    public Element getState() {
        final Element element = new Element("MarkdownSettings");
        element.setAttribute("parsingTimeout", Integer.toString(parsingTimeout));
        element.setAttribute("smarts", Boolean.toString(smarts));
        element.setAttribute("quotes", Boolean.toString(quotes));
        element.setAttribute("abbreviations", Boolean.toString(abbreviations));
        element.setAttribute("hardWraps", Boolean.toString(hardWraps));
        element.setAttribute("autoLinks", Boolean.toString(autoLinks));
        element.setAttribute("wikiLinks", Boolean.toString(wikiLinks));
        element.setAttribute("tables", Boolean.toString(tables));
        element.setAttribute("definitions", Boolean.toString(definitions));
        element.setAttribute("fencedCodeBlocks", Boolean.toString(fencedCodeBlocks));
        element.setAttribute("suppressHTMLBlocks", Boolean.toString(suppressHTMLBlocks));
        element.setAttribute("suppressInlineHTML", Boolean.toString(suppressInlineHTML));
        return element;
    }

    /**
     * Load the settings state from the DOM {@link Element}.
     *
     * @param element the {@link Element} to load values from.
     * @see {@link #getState()}
     */
    public void loadState(@NotNull Element element) {
        String value = element.getAttributeValue("parsingTimeout");
        if (value != null) parsingTimeout = Integer.parseInt(value);
        value = element.getAttributeValue("smarts");
        if (value != null) smarts = Boolean.parseBoolean(value);
        value = element.getAttributeValue("quotes");
        if (value != null) quotes = Boolean.parseBoolean(value);
        value = element.getAttributeValue("abbreviations");
        if (value != null) abbreviations = Boolean.parseBoolean(value);
        value = element.getAttributeValue("hardWraps");
        if (value != null) hardWraps = Boolean.parseBoolean(value);
        value = element.getAttributeValue("autoLinks");
        if (value != null) autoLinks = Boolean.parseBoolean(value);
        value = element.getAttributeValue("wikiLinks");
        if (value != null) wikiLinks = Boolean.parseBoolean(value);
        value = element.getAttributeValue("tables");
        if (value != null) tables = Boolean.parseBoolean(value);
        value = element.getAttributeValue("definitions");
        if (value != null) definitions = Boolean.parseBoolean(value);
        value = element.getAttributeValue("fencedCodeBlocks");
        if (value != null) fencedCodeBlocks = Boolean.parseBoolean(value);
        value = element.getAttributeValue("suppressHTMLBlocks");
        if (value != null) suppressHTMLBlocks = Boolean.parseBoolean(value);
        value = element.getAttributeValue("suppressInlineHTML");
        if (value != null) suppressInlineHTML = Boolean.parseBoolean(value);
        notifyListeners();
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
                (wikiLinks ? Extensions.WIKILINKS : 0) +
                (tables ? Extensions.TABLES : 0) +
                (definitions ? Extensions.DEFINITIONS : 0) +
                (fencedCodeBlocks ? Extensions.FENCED_CODE_BLOCKS : 0) +
                (suppressHTMLBlocks ? Extensions.SUPPRESS_HTML_BLOCKS : 0) +
                (suppressInlineHTML ? Extensions.SUPPRESS_INLINE_HTML : 0);
    }

    /**
     * Add a listener to this settings object changes.
     *
     * @param listener the {@link MarkdownGlobalSettingsListener}.
     */
    public void addListener(@NotNull final MarkdownGlobalSettingsListener listener) {
        if (listeners == null) listeners = new HashSet<WeakReference<MarkdownGlobalSettingsListener>>();
        listeners.add(new WeakReference<MarkdownGlobalSettingsListener>(listener));
    }

    /** Notify event listeners of changes. */
    protected void notifyListeners() {
        if (listeners != null)
            for (final WeakReference<MarkdownGlobalSettingsListener> listenerRef : listeners)
                if (listenerRef.get() != null) listenerRef.get().handleSettingsChanged(this);
    }
}
