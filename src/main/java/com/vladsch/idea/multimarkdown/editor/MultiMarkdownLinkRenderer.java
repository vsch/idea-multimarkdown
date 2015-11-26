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
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.util.GitHubLinkResolver;
import com.vladsch.idea.multimarkdown.util.LinkRef;
import com.vladsch.idea.multimarkdown.util.LinkResolver;
import com.vladsch.idea.multimarkdown.util.PathInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.parboiled.common.StringUtils;
import org.pegdown.LinkRenderer;
import org.pegdown.ast.*;

import static org.pegdown.FastEncoder.encode;
import static org.pegdown.FastEncoder.obfuscate;

public class MultiMarkdownLinkRenderer extends LinkRenderer {
    final public static int GITHUB_WIKI_LINK_FORMAT = 1;
    final public static int VALIDATE_LINKS = 2;

    @NotNull final protected String missingTargetClass;
    final protected int options;
    final protected GitHubLinkResolver resolver;

    public MultiMarkdownLinkRenderer() {
        this(0);
    }

    public MultiMarkdownLinkRenderer(int options) {
        this(null, null, null, options);
    }

    public MultiMarkdownLinkRenderer(@Nullable Project project, @Nullable Document document, @Nullable String missingTargetClass, int options) {
        super();
        this.missingTargetClass = missingTargetClass == null ? "absent" : missingTargetClass;

        if ((options & VALIDATE_LINKS) != 0) {
            VirtualFile file = document == null ? null : FileDocumentManager.getInstance().getFile(document);
            this.resolver = file == null || project == null ? null : new GitHubLinkResolver(file, project);
            if (this.resolver == null) options &= ~VALIDATE_LINKS;
        } else {
            this.resolver = null;
        }

        this.options = options;
    }

    @Nullable
    public String getLinkTarget(@NotNull String url, boolean isWikiLink) {
        // return null if does not resolved, but only if validating links
        if ((options & VALIDATE_LINKS) != 0 && (isWikiLink || MultiMarkdownPlugin.isLicensed())) {
            assert resolver != null;

            LinkRef targetRef = isWikiLink ? LinkRef.parseWikiLinkRef(resolver.getContainingFile(), url) : LinkRef.parseLinkRef(resolver.getContainingFile(), url);
            PathInfo resolvedTarget = resolver.resolve(targetRef, LinkResolver.ONLY_URI, null);
            if (resolvedTarget != null) {
                assert resolvedTarget.isURI() : "Expected URI only target, got " + resolvedTarget;
                return resolvedTarget.getFilePath();
            }
            return null;
        }
        return url;
    }

    public Rendering checkTarget(Rendering rendering) {
        return rendering;
    }

    public Rendering checkTargetImage(Rendering rendering) {
        return rendering;
    }

    @Nullable
    public String getImageLinkTarget(@NotNull String url) {
        // return null if does not resolved, but only if validating links
        if ((options & VALIDATE_LINKS) != 0 && MultiMarkdownPlugin.isLicensed()) {
            assert resolver != null;

            LinkRef targetRef = LinkRef.parseImageLinkRef(resolver.getContainingFile(), url);
            PathInfo resolvedTarget = resolver.resolve(targetRef, LinkResolver.ONLY_URI, null);
            if (resolvedTarget != null) {
                assert resolvedTarget.isURI() : "Expected URI only target, got " + resolvedTarget;
                return resolvedTarget.getFilePath();
            }
            return null;
        }
        return url;
    }

    @Override
    public Rendering render(AnchorLinkNode node) {
        return super.render(node);
    }

    @Override
    public Rendering render(AutoLinkNode node) {
        return checkTarget(super.render(node));
    }

    @Override
    public Rendering render(ExpLinkNode node, String text) {
        String href = getLinkTarget(node.url, false);
        Rendering rendering = new Rendering(href == null ? node.url : href, text);
        if (href == null && MultiMarkdownPlugin.isLicensed()) rendering.withAttribute("class", missingTargetClass);
        if (!StringUtils.isEmpty(node.title)) rendering.withAttribute("title", encode(node.title));
        return rendering;
    }

    @Override
    public Rendering render(ExpImageNode node, String text) {
        String href = getImageLinkTarget(node.url);
        Rendering rendering = new Rendering(href == null ? node.url : href, text);
        if (href == null && MultiMarkdownPlugin.isLicensed()) rendering.withAttribute("class", missingTargetClass);
        if (!StringUtils.isEmpty(node.title)) rendering.withAttribute("title", encode(node.title));
        return rendering;
    }

    @Override
    public Rendering render(RefLinkNode node, String url, String title, String text) {
        //return checkTarget(super.render(node, url, title, text));
        String href = getLinkTarget(url, false);
        Rendering rendering = new Rendering(href == null ? url : href, text);
        if (href == null && MultiMarkdownPlugin.isLicensed()) rendering.withAttribute("class", missingTargetClass);
        if (!StringUtils.isEmpty(title)) rendering.withAttribute("title", encode(title));
        return rendering;
    }

    @Override
    public Rendering render(RefImageNode node, String url, String title, String alt) {
        //return checkTargetImage(super.render(node, url, title, alt));
        String href = getImageLinkTarget(url);
        Rendering rendering = new Rendering(href == null ? url : href, alt);
        if (href == null && MultiMarkdownPlugin.isLicensed()) rendering.withAttribute("class", missingTargetClass);
        if (!StringUtils.isEmpty(title)) rendering.withAttribute("title", encode(title));
        return rendering;
    }

    @Override
    public Rendering render(MailLinkNode node) {
        String obfuscated = obfuscate(node.getText());
        return (new Rendering(obfuscate("mailto:") + obfuscated, obfuscated)).withAttribute("class", obfuscate("mail-link"));
    }

    @Override
    public Rendering render(WikiLinkNode node) {
        int pos;
        String text = node.getText();
        String url = text;

        if ((options & GITHUB_WIKI_LINK_FORMAT) != 0) {
            // vsch: #202 handle WikiLinks alternative format à la GitHub [[text|page]]
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

        // vsch: #200 WikiLinks can have anchor # refs, these are now handled by link resolution engine
        String href = getLinkTarget(url, true);
        Rendering rendering = new Rendering(href == null ? url : href, text);
        if (href == null) rendering.withAttribute("class", missingTargetClass);
        return rendering;
    }
}
