// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.flexmark;

import com.vladsch.flexmark.ast.Code;
import com.vladsch.flexmark.ast.CodeBlock;
import com.vladsch.flexmark.ast.IndentedCodeBlock;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.html.renderer.PhasedNodeRenderer;
import com.vladsch.flexmark.html.renderer.RenderingPhase;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.misc.Utils;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.Escaping;
import com.vladsch.flexmark.util.sequence.Range;
import com.vladsch.flexmark.util.sequence.ReplacedTextMapper;
import com.vladsch.flexmark.util.sequence.ReplacedTextRegion;
import com.vladsch.flexmark.util.sequence.SegmentedSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The node renderer that renders all the core nodes (comes last in the order of node renderers).
 */
@SuppressWarnings("WeakerAccess")
public class MdNavigatorNodeRenderer implements PhasedNodeRenderer {
    final private List<Range> highlightRangeList;
    final private Map<Range, String> highlightRangeMap;
    private int rangeIndex;

    public MdNavigatorNodeRenderer(DataHolder options) {
        highlightRangeMap = MdNavigatorExtension.HIGHLIGHT_RANGES.get(options);
        highlightRangeList = highlightRangeMap != null ? new ArrayList<>(highlightRangeMap.keySet()) : null;
        rangeIndex = 0;
    }

    @Override
    public Set<RenderingPhase> getRenderingPhases() {
        return new HashSet<>(Arrays.asList(
                RenderingPhase.BODY_BOTTOM,
                RenderingPhase.BODY_TOP,
                RenderingPhase.BODY,
                RenderingPhase.BODY_BOTTOM,
                RenderingPhase.BODY_LOAD_SCRIPTS,
                RenderingPhase.BODY_SCRIPTS
        ));
    }

    @Override
    public void renderDocument(@NotNull final NodeRendererContext context, @NotNull final HtmlWriter html, @NotNull final Document document, @NotNull final RenderingPhase phase) {
        rangeIndex = 0;
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        return new HashSet<>(Arrays.asList(
                new NodeRenderingHandler<>(Code.class, this::render),
                new NodeRenderingHandler<>(CodeBlock.class, this::render),
                new NodeRenderingHandler<>(Text.class, this::render)
        ));
    }

    void render(CodeBlock node, NodeRendererContext context, HtmlWriter html) {
        if (highlightRangeList == null) {
            if (node.getParent() instanceof IndentedCodeBlock) {
                html.text(node.getContentChars().trimTailBlankLines().normalizeEndWithEOL());
            } else {
                html.text(node.getContentChars().normalizeEOL());
            }
        } else {
            BasedSequence original;
            if (node.getParent() instanceof IndentedCodeBlock) {
                original = node.getContentChars().trimTailBlankLines();
            } else {
                original = node.getContentChars();
            }

            ReplacedTextMapper textMapper = new ReplacedTextMapper(original);
            BasedSequence mapped = original;
            highlight(context, html, original, mapped, textMapper, false);
        }
    }

    void render(Text node, NodeRendererContext context, HtmlWriter html) {
        if (highlightRangeList == null) {
            html.text(Escaping.normalizeEOL(node.getChars().unescape()));
        } else {
            BasedSequence original = node.getChars();
            ReplacedTextMapper textMapper = new ReplacedTextMapper(original);
            BasedSequence mapped = original.unescape(textMapper);
            highlight(context, html, original, mapped, textMapper, true);
        }
    }

    void render(Code node, NodeRendererContext context, HtmlWriter html) {
        html.srcPos(node.getText()).withAttr().tag("code");
        if (highlightRangeList == null) {
            html.text(Escaping.collapseWhitespace(node.getText(), true));
        } else {
            BasedSequence original = node.getText();
            ReplacedTextMapper textMapper = new ReplacedTextMapper(original);
            BasedSequence mapped = Escaping.collapseWhitespace(original, textMapper);
            highlight(context, html, original, mapped, textMapper, true);
        }
        html.tag("/code");
    }

    @Nullable
    private Range next() {
        if (rangeIndex < highlightRangeList.size()) {
            return highlightRangeList.get(rangeIndex++);
        }
        return null;
    }

    @Nullable
    private Range peek(int startOffset, int endOffset) {
        Range range = peek();

        if (range != null && range.getStart() < endOffset) {
            while (range != null && (range.getEnd() < startOffset)) {
                range = skip();
            }
        }
        return range;
    }

    @Nullable
    private Range peek() {
        if (rangeIndex < highlightRangeList.size()) {
            return highlightRangeList.get(rangeIndex);
        }
        return null;
    }

    private Range skip() {
        if (rangeIndex < highlightRangeList.size()) {
            rangeIndex++;
            return peek();
        }
        return null;
    }

    public void highlight(
            final NodeRendererContext context,
            final HtmlWriter html,
            final BasedSequence original,
            final BasedSequence mapped,
            final ReplacedTextMapper textMapper,
            boolean asText
    ) {
        final int originalStart = textMapper.originalOffset(0);
        final int originalEnd = textMapper.originalOffset(mapped.length());
        // diagnostic/3923
        int startOffset = original.isEmpty() || originalStart > original.length() ? originalStart : original.getIndexOffset(originalStart);
        int endOffset = original.isEmpty() || originalEnd <= originalStart ? originalStart : original.getIndexOffset(originalEnd);
        Range range = peek(startOffset, endOffset);

        if (range == null || range.getStart() >= endOffset) {
            // nothing in this range is highlighted
            if (asText) {
                html.text(mapped);
            } else {
                html.raw(mapped);
            }
        } else {
            int lastOut = 0;
            ArrayList<ReplacedTextRegion> replacedRegions = textMapper.getRegions();
            for (ReplacedTextRegion region : replacedRegions) {
                int start = region.getReplacedRange().getStart();
                int end = region.getReplacedRange().getEnd();

                if (lastOut < start) {
                    // need to output part of range
                    lastOut = outputText(html, asText, original, mapped, lastOut, start, textMapper);
                }

                lastOut = outputText(html, asText, original, mapped, lastOut, end, textMapper);
            }

            if (lastOut < mapped.length()) {
                lastOut = outputText(html, asText, original, mapped, lastOut, mapped.length(), textMapper);
            }
        }
    }

    Range mappedChunk(final Range chunk, final BasedSequence original, final BasedSequence mapped, int startIndex, int endIndex, final ReplacedTextMapper textMapper) {
        if (original instanceof SegmentedSequence) {
            // here we need to find the segment which has the chunk start/end
            int start = Integer.MIN_VALUE;
            int end = Integer.MIN_VALUE;
            for (int i = startIndex; i < endIndex; i++) {
                int index = original.getIndexOffset(textMapper.originalOffset(i));
                if (index == chunk.getStart()) {
                    start = i;
                }
                if (index == chunk.getEnd()) {
                    end = i;
                }
                if (start != Integer.MIN_VALUE && end != Integer.MIN_VALUE) break;
            }

            if (start < 0) start = startIndex;
            if (end < 0) end = endIndex;

            if (end < start) end = start;
            if (start > end) start = end;
            return Range.of(start, end);
        } else {
            int originalIndexOffset = original.getStartOffset();
            return Range.of(chunk.getStart() - originalIndexOffset, chunk.getEnd() - originalIndexOffset);
        }
    }

    int outputText(
            final HtmlWriter html,
            final boolean asText,
            BasedSequence original,
            final BasedSequence mapped,
            final int startIndex,
            final int endIndex,
            final ReplacedTextMapper textMapper
    ) {
        Range range;
        int originalStart = original.getIndexOffset(textMapper.originalOffset(startIndex));
        int originalEnd = original.getIndexOffset(textMapper.originalOffset(endIndex));
        int lastIndex = startIndex;
        int length = mapped.length();
        while (lastIndex < endIndex) {
            int originalLastIndex = textMapper.originalOffset(lastIndex);
            int originalOffset = original.getIndexOffset(originalLastIndex);
            range = peek(originalOffset, originalEnd);
            if (range != null && range.getStart() < originalEnd) {
                Range mappedRange = mappedChunk(range, original, mapped, lastIndex, endIndex, textMapper);
                int chunkStart = mappedRange.getStart();
                int chunkEnd = Utils.maxLimit(mappedRange.getEnd(), length);

                if (lastIndex < chunkStart) {
                    // output not highlighted
                    if (asText) {
                        html.text(mapped.subSequence(lastIndex, chunkStart));
                    } else {
                        html.raw(mapped.subSequence(lastIndex, chunkStart));
                    }
                    lastIndex = chunkStart;
                }

                if (lastIndex < chunkEnd) {
                    // output highlighted
                    html.raw("<span class=\"" + highlightRangeMap.getOrDefault(range, "selection-highlight") + "\">");
                    if (asText) {
                        html.text(mapped.subSequence(lastIndex, chunkEnd));
                    } else {
                        html.raw(mapped.subSequence(lastIndex, chunkEnd));
                    }
                    html.raw("</span>");
                    lastIndex = chunkEnd;
                }

                if (range.getEnd() <= originalEnd) skip();
            } else {
                // output not highlighted
                if (asText) {
                    html.text(mapped.subSequence(lastIndex, endIndex));
                } else {
                    html.raw(mapped.subSequence(lastIndex, endIndex));
                }
                lastIndex = endIndex;
            }
        }
        return lastIndex;
    }

    public static class Factory implements NodeRendererFactory {
        @NotNull
        @Override
        public NodeRenderer apply(@NotNull final com.vladsch.flexmark.util.data.DataHolder options) {
            return new MdNavigatorNodeRenderer(options);
        }
    }
}
