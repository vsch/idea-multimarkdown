// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.folding

import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.language.api.MdFoldingBuilderProvider
import com.vladsch.md.nav.language.api.MdFoldingVisitorHandler
import com.vladsch.md.nav.psi.element.*
import com.vladsch.md.nav.psi.util.MdNodeVisitor
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdVisitHandler
import com.vladsch.md.nav.psi.util.MdVisitor
import com.vladsch.md.nav.util.*
import com.vladsch.md.nav.vcs.GitHubLinkResolver
import com.vladsch.plugin.util.image.ImageUtils
import com.vladsch.plugin.util.maxLimit
import java.util.*

class MdFoldingVisitor(
    private val root: PsiElement,
    document: Document,
    private val descriptors: ArrayList<FoldingDescriptor>,
    quick: Boolean,
    private val defaultPlaceHolderText: String
) : MdFoldingVisitorHandler {

    private val myResolver = GitHubLinkResolver(root)
    private val myHeadingRanges = HashMap<MdHeaderElement, TextRange>()
    private val myOpenHeadingRanges = Array<TextRange?>(6) { null }
    private val myOpenHeadings = Array<MdHeaderElement?>(6) { null }
    private val myRootText = document.charsSequence
    private val myVisitor: MdNodeVisitor = MdNodeVisitor()

    init {
        addHandlers(

//            // does not do anything in basic
//            MdVisitHandler(MdSimToc::class.java, this::fold),

            MdVisitHandler(MdJekyllFrontMatterBlock::class.java, this::fold),
            MdVisitHandler(MdUnorderedListItem::class.java, this::fold),
            MdVisitHandler(MdOrderedListItem::class.java, this::fold),
            MdVisitHandler(MdAtxHeader::class.java, this::fold),
            MdVisitHandler(MdSetextHeader::class.java, this::fold),
            MdVisitHandler(MdBlockComment::class.java, this::fold),
            MdVisitHandler(MdInlineComment::class.java, this::fold),
            MdVisitHandler(MdVerbatim::class.java, this::fold)
        )

        if (!quick) {
            addHandlers(
                MdVisitHandler(MdAutoLinkRef::class.java, this::fold),
                MdVisitHandler(MdImageLinkRef::class.java, this::fold),
                MdVisitHandler(MdJekyllIncludeLinkRef::class.java, this::fold),
                MdVisitHandler(MdLinkRef::class.java, this::fold),
                MdVisitHandler(MdReferenceLinkRef::class.java, this::fold),
                MdVisitHandler(MdWikiLinkRef::class.java, this::fold)
            )
        }

        // allow extensions to add theirs
        MdFoldingBuilderProvider.EXTENSIONS.value.forEach { it.extendFoldingHandler(this, quick) }
    }

    override fun addHandlers(handlers: MutableCollection<MdVisitHandler<PsiElement>>): MdNodeVisitor = myVisitor.addHandlers(handlers)
    override fun addHandlers(vararg handlers: MdVisitHandler<*>): MdNodeVisitor = myVisitor.addHandlers(handlers)
    override fun addHandlers(vararg handlers: Array<out MdVisitHandler<PsiElement>>): MdNodeVisitor = myVisitor.addHandlers(*handlers)
    override fun addHandler(handler: MdVisitHandler<*>): MdNodeVisitor = myVisitor.addHandler(handler)
    override fun visitNodeOnly(element: PsiElement): Unit = myVisitor.visitNodeOnly(element)
    override fun visit(element: PsiElement): Unit = myVisitor.visit(element)
    override fun visitChildren(element: PsiElement): Unit = myVisitor.visitChildren(element)

    override fun getRootText(): CharSequence = myRootText
    override fun getDefaultPlaceHolderText(): String = defaultPlaceHolderText
    override fun addDescriptor(descriptor: FoldingDescriptor): Boolean = descriptors.add(descriptor)
    override fun getFoldingHandler(klass: Class<out PsiElement>): MdVisitor<PsiElement>? = myVisitor.getAction(klass)

    override fun <T : PsiElement> delegateFoldingHandler(forClass: Class<T>, toClass: Class<out T>): Boolean {
        val handler = myVisitor.getAction(forClass) ?: return false
        myVisitor.addHandler(MdVisitHandler(toClass) { handler.visit(it) })
        return true
    }

    fun buildFoldingRegions(root: PsiElement) {
        myVisitor.visit(root)

        // close any open headings at end of file
        closeOpenHeadings()
    }

    private fun fold(element: MdVerbatim) {
        val content = element.contentElement ?: return
        val range = content.textRange
        if (!range.isEmpty) {
            addDescriptor(object : FoldingDescriptor(element.node, TextRange(if (range.startOffset > 0) range.startOffset - 1 else range.startOffset, range.endOffset), null) {
                override fun getPlaceholderText(): String? {
                    return null
                }
            })
        }
    }

    private fun fold(element: MdLinkRefElement) {
        if (!element.textRange.isEmpty) {
            val linkRef = MdPsiImplUtil.getLinkRef(element.parent)

            if (linkRef != null) {
                val filePath = linkRef.filePath
                if (ImageUtils.isPossiblyEncodedImage(filePath)) {
                    val collapsedText = "data:image/$defaultPlaceHolderText"
                    addDescriptor(object : FoldingDescriptor(element, element.textRange) {
                        override fun getPlaceholderText(): String? {
                            return collapsedText
                        }
                    })
                } else {
                    if ((linkRef.isURL || linkRef.isFileURI) && !myResolver.isExternalUnchecked(linkRef)) {
                        val reference = element.reference
                        if (reference != null) {
                            val targetElement = reference.resolve()

                            if (targetElement != null) {
                                // get the repo relative if available, if not then page relative
                                var collapsedForm = myResolver.resolve(linkRef, Want.invoke(Local.ABS, Remote.ABS, Links.NONE), null)
                                if (collapsedForm == null) {
                                    collapsedForm = myResolver.resolve(linkRef, Want.invoke(Local.REL, Remote.REL, Links.NONE), null)
                                }

                                if (collapsedForm != null && collapsedForm is LinkRef) {
                                    val collapsedText = collapsedForm.filePath
                                    addDescriptor(object : FoldingDescriptor(element, element.textRange) {
                                        override fun getPlaceholderText(): String? {
                                            return collapsedText
                                        }
                                    })
                                }
                            }
                        }
                    }
                }
            }

            visitChildren(element)
        }
    }

    private fun trimLastBlankLine(text: CharSequence, range: TextRange): TextRange {
        if (range.endOffset > 1 && range.endOffset <= text.length && text[range.endOffset - 1] == '\n') {
            val lastEOL = range.endOffset - 1
            val prevEOL = text.lastIndexOf('\n', lastEOL - 1)
            if (prevEOL >= range.startOffset && prevEOL < lastEOL) {
                if (text.subSequence(prevEOL + 1, lastEOL).isBlank()) {
                    return TextRange(range.startOffset, prevEOL)
                }
            }
        }
        return range
    }

    private fun fold(element: MdHeaderElement) {
        val headingRange = element.textRange
        val headingIndex = element.headerLevel - 1

        // extend range of all headings with lower level and close all equal or lower levels
        for (i in 0 until 6) {
            val openHeadingRange = myOpenHeadingRanges[i]
            val openHeading = myOpenHeadings[i]
            if (i < headingIndex) {
                // extend its range to include this heading
                if (openHeadingRange != null && openHeading != null) {
                    val textRange = openHeadingRange.union(headingRange)
                    myOpenHeadingRanges[i] = textRange
                }
            } else {
                // end it before our start
                if (openHeadingRange != null && openHeading != null) {
                    updateHeadingRanges(openHeading, openHeadingRange, headingRange.startOffset)
                }

                // close this level
                myOpenHeadingRanges[i] = null
                myOpenHeadings[i] = null

                if (i == headingIndex) {
                    // this is now this heading's spot
                    myOpenHeadings[i] = element
                    myOpenHeadingRanges[i] = headingRange
                    if (element is MdSetextHeader) {
                        // need to use the end of text node
                        val headingTextRange = element.headerTextElement?.textRange
                        if (headingTextRange != null) {
                            myOpenHeadingRanges[headingIndex] = headingTextRange
                        }
                    } else {
                        myOpenHeadingRanges[headingIndex] = TextRange(headingRange.endOffset - 1, headingRange.endOffset - 1)
                    }
                }
            }
        }

        visitChildren(element)
    }

    private fun updateHeadingRanges(openHeading: MdHeaderElement, openHeadingRange: TextRange, endOffset: Int) {
        val finalOpenHeadingRange = trimLastBlankLine(myRootText, TextRange(openHeadingRange.startOffset, endOffset))
        if (!finalOpenHeadingRange.isEmpty && myRootText.subSequence(finalOpenHeadingRange.startOffset, finalOpenHeadingRange.endOffset).contains('\n')) {
            myHeadingRanges[openHeading] = finalOpenHeadingRange
        }
    }

    private fun closeOpenHeadings() {
        val endOffset = root.textRange.endOffset
        for (i in 0 until 6) {
            val openHeadingRange = myOpenHeadingRanges[i]
            val openHeading = myOpenHeadings[i]
            if (openHeadingRange != null && openHeading != null) {
                // diagnostic/4985
                updateHeadingRanges(openHeading, openHeadingRange, endOffset.maxLimit(myRootText.length))
            }
        }

        for (heading in myHeadingRanges.keys) {
            val range = myHeadingRanges[heading]
            if (range != null && !range.isEmpty) {
                addDescriptor(object : FoldingDescriptor(heading.node, range, null) {
                    override fun getPlaceholderText(): String? {
                        return defaultPlaceHolderText
                    }
                })
            }
        }
    }

    private fun fold(element: MdJekyllFrontMatterBlock) {
        val range = element.textRange
        if (!range.isEmpty && range.startOffset + 3 < range.endOffset) {
            val text = element.text
            val lastEOL = (text as String).lastIndexOf("\n", text.length - 1) + 1
            addDescriptor(object : FoldingDescriptor(element.node, TextRange(range.startOffset + 3, range.startOffset + lastEOL), null) {
                override fun getPlaceholderText(): String? {
                    return defaultPlaceHolderText
                }
            })
            visitChildren(element)
        }
    }

    private fun fold(element: MdListItem) {
        val firstLineEnd = element.text.indexOf("\n")
        if (firstLineEnd >= 0) {
            val textRange = element.textRange
            val range = TextRange.create(textRange.startOffset + firstLineEnd, textRange.endOffset - 1)
            if (!range.isEmpty) {
                addDescriptor(object : FoldingDescriptor(element.node, TextRange(range.startOffset, range.endOffset), null) {
                    override fun getPlaceholderText(): String? {
                        return defaultPlaceHolderText
                    }
                })
            }
        }
        visitChildren(element)
    }

    private fun fold(comment: MdComment) {
        val commentText = comment.commentTextNode ?: return

        var text = commentText.text
        val pos = text.indexOf('\n')
        if (pos > 0) {
            text = text.substring(0, pos).trim() + defaultPlaceHolderText
            addDescriptor(object : FoldingDescriptor(comment.node, comment.node.textRange, null) {
                override fun getPlaceholderText(): String? {
                    return text
                }
            })
        }
    }
}
