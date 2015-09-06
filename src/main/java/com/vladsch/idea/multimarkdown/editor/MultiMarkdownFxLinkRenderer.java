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
import org.pegdown.LinkRenderer;
import org.pegdown.ast.*;

import static org.pegdown.FastEncoder.obfuscate;

public class MultiMarkdownFxLinkRenderer extends LinkRenderer {
    public MultiMarkdownFxLinkRenderer() {
        super();
    }

    @Override public Rendering render(AnchorLinkNode node) {
        String name = node.getName();
        return new Rendering('#' + name, node.getText()).withAttribute("name", name).withAttribute("id", name).withAttribute("class", "anchor");
    }

    @Override public Rendering render(AutoLinkNode node) {
        return super.render(node);
    }

    @Override public Rendering render(ExpLinkNode node, String text) {
        return super.render(node, text);
    }

    @Override public Rendering render(ExpImageNode node, String text) {
        return super.render(node, text);
    }

    @Override public Rendering render(RefLinkNode node, String url, String title, String text) {
        return super.render(node, url, title, text);
    }

    @Override public Rendering render(RefImageNode node, String url, String title, String alt) {
        return super.render(node, url, title, alt);
    }

    @Override public Rendering render(WikiLinkNode node) {
        return super.render(node);
    }

    @Override
    public Rendering render(MailLinkNode node) {
        String obfuscated = obfuscate(node.getText());
        return (new Rendering(obfuscate("mailto:") + obfuscated, obfuscated)).withAttribute("class", obfuscate("mail-link"));
    }

}
