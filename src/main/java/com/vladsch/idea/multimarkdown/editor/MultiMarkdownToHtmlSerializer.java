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
import org.pegdown.ToHtmlSerializer;
import org.pegdown.VerbatimSerializer;
import org.pegdown.ast.AnchorLinkNode;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

import java.util.List;
import java.util.Map;

public class MultiMarkdownToHtmlSerializer extends ToHtmlSerializer {
    public MultiMarkdownToHtmlSerializer(LinkRenderer linkRenderer) {
        super(linkRenderer);
    }

    public MultiMarkdownToHtmlSerializer(LinkRenderer linkRenderer, List<ToHtmlSerializerPlugin> plugins) {
        super(linkRenderer, plugins);
    }

    public MultiMarkdownToHtmlSerializer(LinkRenderer linkRenderer, Map<String, VerbatimSerializer> verbatimSerializers) {
        super(linkRenderer, verbatimSerializers);
    }

    public MultiMarkdownToHtmlSerializer(LinkRenderer linkRenderer, Map<String, VerbatimSerializer> verbatimSerializers, List<ToHtmlSerializerPlugin> plugins) {
        super(linkRenderer, verbatimSerializers, plugins);
    }

    @Override public void visit(AnchorLinkNode node) {
        printAnchorLink(linkRenderer.render(node));
    }

    protected void printAnchorLink(LinkRenderer.Rendering rendering) {
        printer.print('<').print('a');
        printAttribute("href", rendering.href);
        for (LinkRenderer.Attribute attr : rendering.attributes) {
            printAttribute(attr.name, attr.value);
        }
        printer.print('>').print("<span class=\"octicon octicon-link\"></span>").print("</a>").print(rendering.text);
    }
}
