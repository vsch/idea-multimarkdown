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

import javax.swing.*;

/**
 * UI form for {@link MarkdownGlobalSettings} edition.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @see MarkdownGlobalSettingsConfigurable
 * @see MarkdownGlobalSettings
 * @since 0.6
 */
public class MarkdownSettingsPanel {

    /**
     * The parent panel for the form.
     */
    public JPanel panel;

    /**
     * The "extensions" form container.
     */
    public JPanel extensionsPanel;

    /**
     * Description label for {@link #extensionsPanel}.
     */
    private JLabel extensionsPanelDescription;

    /**
     * Form element for {@link MarkdownGlobalSettings#smarts}.
     */
    public JCheckBox smartsCheckBox;

    /**
     * Form element for {@link MarkdownGlobalSettings#quotes}.
     */
    public JCheckBox quotesCheckBox;

    /**
     * Form element for {@link MarkdownGlobalSettings#abbreviations}.
     */
    public JCheckBox abbreviationsCheckBox;

    /**
     * Form element for {@link MarkdownGlobalSettings#hardWraps}.
     */
    public JCheckBox hardWrapsCheckBox;

    /**
     * Form element for {@link MarkdownGlobalSettings#autoLinks}.
     */
    public JCheckBox autoLinksCheckBox;

    /**
     * Form element for {@link MarkdownGlobalSettings#tables}.
     */
    public JCheckBox tablesCheckBox;

    /**
     * Form element for {@link MarkdownGlobalSettings#definitions}.
     */
    public JCheckBox definitionsCheckBox;

    /**
     * Form element for {@link MarkdownGlobalSettings#fencedCodeBlocks}.
     */
    public JCheckBox fencedCodeBlocksCheckBox;

    /**
     * Form element for {@link MarkdownGlobalSettings#suppressHTMLBlocks}.
     */
    public JCheckBox suppressHTMLBlocksCheckBox;

    /**
     * Form element for {@link MarkdownGlobalSettings#suppressInlineHTML}.
     */
    public JCheckBox suppressInlineHTMLCheckBox;

    /**
     * Form element for {@link MarkdownGlobalSettings#noFollowLinks}.
     */
    public JCheckBox noFollowLinksCheckBox;

    /**
     * Description label for {@link #suppressInlineHTMLCheckBox}.
     */
    private JLabel suppressInlineHTMLDescriptionLabel;

    /**
     * Description label for {@link #noFollowLinksCheckBox}.
     */
    private JLabel noFollowLinksDescriptionLabel;

    /**
     * Description label for {@link #suppressHTMLBlocksCheckBox}.
     */
    private JLabel suppressHTMLBlocksDescriptionLabel;

    /**
     * Description label for {@link #fencedCodeBlocksCheckBox}.
     */
    private JLabel fencedCodeBlocksDescriptionLabel;

    /**
     * Description label for {@link #definitionsCheckBox}.
     */
    private JLabel definitionsDescriptionLabel;

    /**
     * Description label for {@link #tablesCheckBox}.
     */
    private JLabel tablesDescriptionLabel;

    /**
     * Description label for {@link #autoLinksCheckBox}.
     */
    private JLabel autoLinksDescriptionLabel;

    /**
     * Description label for {@link #hardWrapsCheckBox}.
     */
    private JLabel hardWarpsDescriptionLabel;

    /**
     * Description label for {@link #abbreviationsCheckBox}.
     */
    private JLabel abbreviationsDescriptionLabel;

    /**
     * Description label for {@link #quotesCheckBox}.
     */
    private JLabel quotesDescriptionLabel;

    /**
     * Description label for {@link #smartsCheckBox}.
     */
    private JLabel smartsDescriptionLabel;
}
