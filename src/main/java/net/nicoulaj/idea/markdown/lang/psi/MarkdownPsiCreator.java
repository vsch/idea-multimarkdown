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
package net.nicoulaj.idea.markdown.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import net.nicoulaj.idea.markdown.lang.MarkdownElementTypes;
import net.nicoulaj.idea.markdown.lang.psi.impl.MarkdownPsiElementImpl;

/**
 * TODO Add Javadoc comment.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.1
 */
public class MarkdownPsiCreator implements MarkdownElementTypes {

    public static PsiElement createElement(ASTNode node) {

        // TODO PSI not implemented

        return new MarkdownPsiElementImpl(node);
    }
}
