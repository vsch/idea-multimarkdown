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
package com.vladsch.idea.multimarkdown.editor;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.pegdown.LinkRenderer;
import org.pegdown.ast.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.pegdown.FastEncoder.obfuscate;

public class MultiMarkdownLinkRenderer extends LinkRenderer {
    final public static int GITHUB_WIKI_LINK_FORMAT = 1;

    final protected Project project;
    final protected Document document;
    final protected String missingTargetClass;
    final protected int options;

    public MultiMarkdownLinkRenderer() {
        super();
        project = null;
        document = null;
        missingTargetClass = null;
        options = 0;
    }

    public MultiMarkdownLinkRenderer(int options) {
        super();
        project = null;
        document = null;
        missingTargetClass = null;
        this.options = options;
    }

    public MultiMarkdownLinkRenderer(Project project, Document document, String missingTargetClass, int options) {
        super();
        this.project = project;
        this.document = document;
        this.missingTargetClass = missingTargetClass;
        this.options = options;
    }

    // TODO: need to implement this using ProjectComponent methods so that we don't need
    // to go into areas that may have threading issues.
    public Rendering checkTarget(Rendering rendering) {
        if (project != null && document != null && missingTargetClass != null) {
            if (!rendering.href.startsWith("#") && !MultiMarkdownPathResolver.canResolveLink(project, document, rendering.href)) {
                rendering.withAttribute("class", missingTargetClass);
            }
        }
        return rendering;
    }

    @Override
    public Rendering render(AnchorLinkNode node) {
        return checkTarget(super.render(node));
    }

    @Override
    public Rendering render(AutoLinkNode node) {
        return checkTarget(super.render(node));
    }

    @Override
    public Rendering render(ExpLinkNode node, String text) {
        return checkTarget(super.render(node, text));
    }

    @Override
    public Rendering render(ExpImageNode node, String text) {
        return checkTarget(super.render(node, text));
    }

    @Override
    public Rendering render(RefLinkNode node, String url, String title, String text) {
        return checkTarget(super.render(node, url, title, text));
    }

    @Override
    public Rendering render(RefImageNode node, String url, String title, String alt) {
        return checkTarget(super.render(node, url, title, alt));
    }

    @Override
    public Rendering render(MailLinkNode node) {
        String obfuscated = obfuscate(node.getText());
        return (new Rendering(obfuscate("mailto:") + obfuscated, obfuscated)).withAttribute("class", obfuscate("mail-link"));
    }

    @Override
    public Rendering render(WikiLinkNode node) {
        if (project == null || document == null || missingTargetClass == null) {
            return checkTarget(super.render(node));
        }
        try {
            int pos;
            String text = node.getText();
            String url = text;

            if ((options & GITHUB_WIKI_LINK_FORMAT) != 0) {
                // vsch: #202 handle WikiLinks a la GitHub alternative format [[text|page]]
                if ((pos = text.indexOf("|")) >= 0) {
                    url = text.substring(pos + 1);
                    text = text.substring(0, pos);
                }
            } else {
                // vsch: #182 handle WikiLinks alternative format [[page|text]]
                if ((pos = text.indexOf("|")) >= 0) {
                    url = text.substring(0, pos);
                    text = text.substring(pos + 1);
                }
            }

            // vsch: #200 WikiLinks can have anchor # refs
            String suffix = "";
            if ((pos = url.lastIndexOf("#")) >= 0) {
                suffix = url.substring(pos);
                url = url.substring(0, pos);
            }

            // vsch: need our own extension for the file
            url = ((url.isEmpty()) ? "" : ("./" + URLEncoder.encode(url.replace(' ', '-'), "UTF-8") + ".md")) + suffix;
            return checkTarget(new Rendering(url, text));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException();
        }
    }
}
