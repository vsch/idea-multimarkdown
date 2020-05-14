// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser;

public interface Extensions {

    /**
     * The default, standard markup mode without any extensions.
     */
    int NONE = 0x00;

    /**
     * Pretty ellipses, dashes and apostrophes.
     */
    int SMARTS = 0x01;

    /**
     * Pretty single and double quotes.
     */
    int QUOTES = 0x02;

    /**
     * PHP Markdown Extra style abbreviations.
     *
     * @see <a href="http://michelf.com/projects/php-markdown/extra/#abbr">PHP Markdown Extra</a>
     */
    int ABBREVIATIONS = 0x04;

    /**
     * Enables the parsing of hard wraps as HTML linebreaks. Similar to what github does.
     *
     * @see <a href="http://github.github.com/github-flavored-markdown">Github-flavored-Markdown</a>
     */
    int HARDWRAPS = 0x08;

    /**
     * Enables plain autolinks the way github flavoured markdown implements them. With this extension enabled pegdown will intelligently recognize URLs and email addresses without any further delimiters and mark them as the respective link type.
     *
     * @see <a href="http://github.github.com/github-flavored-markdown">Github-flavored-Markdown</a>
     */
    int AUTOLINKS = 0x10;

    /**
     * Table support similar to what Multimarkdown offers.
     *
     * @see <a href="http://fletcherpenney.net/multimarkdown/users_guide/">MultiMarkdown</a>
     */
    int TABLES = 0x20;

    /**
     * PHP Markdown Extra style definition lists. Additionally supports the small extension proposed in the article referenced below.
     *
     * @see <a href="http://michelf.com/projects/php-markdown/extra/#def-list">PHP Markdown Extra</a>
     * @see <a href="http://www.justatheory.com/computers/markup/modest-markdown-proposal.html">Extension proposal</a>
     */
    int DEFINITIONS = 0x40;

    /**
     * PHP Markdown Extra style fenced code blocks.
     *
     * @see <a href="http://michelf.com/projects/php-markdown/extra/#fenced-code-blocks">PHP Markdown Extra</a>
     */
    int FENCED_CODE_BLOCKS = 0x80;

    /**
     * Support [[Wiki-style links]].
     *
     * @see <a href="http://github.github.com/github-flavored-markdown">Github-flavored-Markdown</a>
     */
    int WIKILINKS = 0x0100;

    /**
     * Support ~~strikethroughs~~ as supported in Pandoc and Github.
     */
    int STRIKETHROUGH = 0x0200;

    /**
     * Enables anchor links in headers.
     * ANCHORLINKS
     */
    int ANCHORLINKS = 0x0400;

    int NOT_USED_1 = 0x0800;
    int NOT_USED_2 = 0x1000;
    int NOT_USED_3 = 0x2000;

    /**
     *
     */
    int HTML_DEEP_PARSER = 0x4000;

    /**
     *  double break starts new definition list
     */
    int DEFINITION_BREAK_DOUBLE_BLANK_LINE = 0x8000;

    /**
     * All available extensions excluding the SUPPRESS_... options, ATXHEADERSPACE. FORCELISTITEMPARA
     */
    int ALL = 0x0000FFFF & ~(DEFINITION_BREAK_DOUBLE_BLANK_LINE | HTML_DEEP_PARSER | AUTOLINKS | NOT_USED_1 | NOT_USED_2 | NOT_USED_3);

    /**
     * Suppresses HTML blocks. They will be accepted in the input but not be contained in the output.
     */
    int SUPPRESS_HTML_BLOCKS = 0x00010000;

    /**
     * Suppresses inline HTML tags. They will be accepted in the input but not be contained in the output.
     */
    int SUPPRESS_INLINE_HTML = 0x00020000;

    /**
     * Suppresses HTML blocks as well as inline HTML tags. Both will be accepted in the input but not be contained in the output.
     */
    int SUPPRESS_ALL_HTML = 0x00030000;

    /**
     * Requires a space char after Atx # header prefixes, so that #dasdsdaf is not a header.
     */
    int ATXHEADERSPACE = 0x00040000;

    /**
     * Force List and Definition Paragraph wrapping if it includes more than just a single paragraph
     */
    int SUBSCRIPT = 0x00080000;

    /**
     * Allow horizontal rules without a blank line following them.
     */
    int RELAXEDHRULES = 0x00100000;

    /**
     * GitHub style task list items: - [ ] and - [x]
     */
    int TASKLISTITEMS = 0x00200000;

    /**
     * Generate anchor links for headers using complete contents of the header. Spaces and non-alphanumerics replaced by `-`, multiple dashes trimmed to one. Anchor link is added as first element inside the header with empty content: `<h1><a name="header-a"></a>header a</h1>`
     * EXTANCHORLINKS
     * 
     */
    int EXTANCHORLINKS = 0x00400000;

    /**
     * should wrap header content instead of creating an empty anchor: `<h1><a name="header-a">header a</a></h1>`
     * EXTANCHORLINKS_WRAP
     * 
     */
    int NOT_USED_5 = 0x00800000;

    /**
     * Enables footnote processing [^1]: Text Paragraph with continuations and footnote reference [^1]
     */
    int FOOTNOTES = 0x01000000;

    /**
     * Enables TOC extension
     */
    int TOC = 0x02000000;

    /**
     * ![alt](.....?
     * <p>
     * )
     * <p>
     * ![alt](.....?
     * <p>
     * "title")
     * <p>
     * Enables MULTI_LINE_IMAGE_URLS extension which allows image urls of the form above. any text at all until ) or "title") at the begining of a line. Used for displaying UML diagrams with gravizo.com
     */
    int MULTI_LINE_IMAGE_URLS = 0x04000000;

    /**
     * trace parsing elements to console
     */
    int SUPERSCRIPT = 0x08000000;

    /**
     * allow aside syntax
     */
    int ASIDE = 0x10000000;

    /**
     * Include \u001F in Letters() so that IntelliJ Code Completion Marker will be parsed as part of Identifier and all elements of markdown that allow Alphanumerics. Only needed by idea-multimarkdown.
     * <p>
     * Also allows zerowidth labels for [^] for the same reason, so completions can work without typing any characters between the brackets
     */
    int INTELLIJ_DUMMY_IDENTIFIER = 0x20000000;

    /**
     * Enables adding a dummy reference key node to RefLink and RefImage so that the AST differs between [ ][] and plain [ ] for refLink and ![ ][] and plain ![ ] for RefImage
     */
    int INSERTED = 0x40000000;

    int UNUSABLE = 0x80000000;

    /**
     * These are GitHub main repo document processing compatibility flags
     */
    int GITHUB_DOCUMENT_COMPATIBLE = (FENCED_CODE_BLOCKS | TABLES | AUTOLINKS | TASKLISTITEMS | STRIKETHROUGH | ATXHEADERSPACE | RELAXEDHRULES | INTELLIJ_DUMMY_IDENTIFIER);

    /**
     * These are GitHub wiki page processing compatibility flags
     */
    int GITHUB_WIKI_COMPATIBLE = (GITHUB_DOCUMENT_COMPATIBLE | WIKILINKS);

    /**
     * These are GitHub comment (issues, pull requests and comments) processing compatibility flags
     */
    int GITHUB_COMMENT_COMPATIBLE = (GITHUB_DOCUMENT_COMPATIBLE | HARDWRAPS);
}
