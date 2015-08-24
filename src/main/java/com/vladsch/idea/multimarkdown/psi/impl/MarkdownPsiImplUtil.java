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
package com.vladsch.idea.multimarkdown.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.vladsch.idea.multimarkdown.MarkdownIcons;
import com.vladsch.idea.multimarkdown.psi.MarkdownElementFactory;
import com.vladsch.idea.multimarkdown.psi.MarkdownProperty;
import com.vladsch.idea.multimarkdown.psi.MarkdownTypes;
import org.jetbrains.annotations.Nullable;
import java.lang.String;

import javax.swing.*;

public class MarkdownPsiImplUtil {
    public static String getKey(MarkdownProperty element) {
        ASTNode keyNode = element.getNode().findChildByType(MarkdownTypes.KEY);
        if (keyNode != null) {
            // IMPORTANT: Convert embedded escaped spaces to Markdown spaces
            return keyNode.getText().replaceAll("\\\\ "," ");
        } else {
            return null;
        }
    }

    public static String getValue(MarkdownProperty element) {
        ASTNode valueNode = element.getNode().findChildByType(MarkdownTypes.VALUE);
        if (valueNode != null) {
            return valueNode.getText();
        } else {
            return null;
        }
    }

    public static String getName(MarkdownProperty element) {
        return getKey(element);
    }

    public static PsiElement setName(MarkdownProperty element, String newName) {
        ASTNode keyNode = element.getNode().findChildByType(MarkdownTypes.KEY);
        if (keyNode != null) {
            MarkdownProperty property = MarkdownElementFactory.createProperty(element.getProject(), newName);
            ASTNode newKeyNode = property.getFirstChild().getNode();
            element.getNode().replaceChild(keyNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(MarkdownProperty element) {
        ASTNode keyNode = element.getNode().findChildByType(MarkdownTypes.KEY);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return null;
        }
    }

    public static ItemPresentation getPresentation(final MarkdownProperty element) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return element.getKey();
            }

            @Nullable
            @Override
            public String getLocationString() {
                PsiFile containingFile = element.getContainingFile();
                return containingFile == null ? null : containingFile.getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return MarkdownIcons.FILE;
            }
        };
    }
}

