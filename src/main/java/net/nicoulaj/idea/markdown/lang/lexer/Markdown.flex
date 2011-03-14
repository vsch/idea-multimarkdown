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
%state IN_ITALIC, IN_BOLD, IN_CODE_BLOCK

%{
   public _MarkdownLexer() {
     this((java.io.Reader)null);
   }
%}

EOL = \n|\r|\r\n
INPUT_CHAR = [^\r\n]
WHITE_SPACE_CHAR = [\ \n\r\t\f]

%%

<YYINITIAL> {
  "*"[^"*"]                   { yybegin(IN_ITALIC); return MarkdownTokenTypes.ITALIC_TEXT; }
  "**"[^"*"]                  { yybegin(IN_BOLD); return MarkdownTokenTypes.BOLD_TEXT; }
  "!["[^"]"]*"]("[^")"]*")"   { return MarkdownTokenTypes.IMAGE; }
  "["[^"]"]*"]("[^")"]*")"    { return MarkdownTokenTypes.LINK; }
  ^"#"[^"#"].*{EOL}           { return MarkdownTokenTypes.ATX_HEADER_LEVEL_1; }
  ^"##"[^"#"].*{EOL}          { return MarkdownTokenTypes.ATX_HEADER_LEVEL_2; }
  ^"###"[^"#"].*{EOL}         { return MarkdownTokenTypes.ATX_HEADER_LEVEL_3; }
  ^"####"[^"#"].*{EOL}        { return MarkdownTokenTypes.ATX_HEADER_LEVEL_4; }
  ^"#####"[^"#"].*{EOL}       { return MarkdownTokenTypes.ATX_HEADER_LEVEL_5; }
  ^"######"[^"#"].*{EOL}      { return MarkdownTokenTypes.ATX_HEADER_LEVEL_6; }
  ^.*{EOL}"="+{EOL}           { return MarkdownTokenTypes.SETEXT_HEADER_LEVEL_1; }
  ^.*{EOL}"-"+{EOL}           { return MarkdownTokenTypes.SETEXT_HEADER_LEVEL_2; }
  ^{EOL}(\ \ \ \ |\t)         { yybegin(IN_CODE_BLOCK); return MarkdownTokenTypes.PLAIN_TEXT; }
  [^]                         { return MarkdownTokenTypes.PLAIN_TEXT; }
}

<IN_ITALIC> {
  "*"                         { yybegin(YYINITIAL); return MarkdownTokenTypes.ITALIC_TEXT; }
  [^]                         { return MarkdownTokenTypes.ITALIC_TEXT; }
}

<IN_BOLD> {
  "**"                        { yybegin(YYINITIAL); return MarkdownTokenTypes.BOLD_TEXT; }
  [^]                         { return MarkdownTokenTypes.BOLD_TEXT; }
}

<IN_CODE_BLOCK> {
  {EOL}(\ {0,3}[^\ ]|[^\ \t]) { yybegin(YYINITIAL); return MarkdownTokenTypes.PLAIN_TEXT; }
  [^]                         { return MarkdownTokenTypes.CODE_BLOCK; }
}
