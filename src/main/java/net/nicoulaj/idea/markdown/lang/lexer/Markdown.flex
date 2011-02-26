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
package net.nicoulaj.idea.markdown.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import net.nicoulaj.idea.markdown.lang.MarkdownTokenTypes;

/**
 * Lexer for Markdown.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.1
 */
@SuppressWarnings({"ALL"})
%%

%class _MarkdownLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

%{
   public _MarkdownLexer() {
     this((java.io.Reader)null);
   }
%}

// TODO Lexer not implemented

EOL = \n|\r|\r\n
INPUT_CHAR = [^\r\n]
WHITE_SPACE_CHAR = [\ \n\r\t\f]
PLAIN_TEXT = ({INPUT_CHAR}|{EOL})*

%%

<YYINITIAL> {
  {PLAIN_TEXT}     { yybegin(YYINITIAL); return MarkdownTokenTypes.PLAIN_TEXT; }
  .                { yybegin(YYINITIAL); return MarkdownTokenTypes.BAD_CHARACTER; }
}
