// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ColoredItemPresentation
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.html.renderer.HeaderIdGenerator
import com.vladsch.flexmark.html.renderer.HtmlIdGenerator
import com.vladsch.flexmark.util.data.DataKey
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.highlighter.MdHighlighterColors
import com.vladsch.md.nav.psi.reference.MdPsiReference
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.md.nav.settings.ParserOptions
import com.vladsch.md.nav.util.looping.MdPsiIterator
import com.vladsch.plugin.util.psi.isTypeOf
import icons.MdIcons
import javax.swing.Icon
import kotlin.math.max

abstract class MdHeaderElementImpl(node: ASTNode) : MdNamedElementImpl(node), MdHeaderElement, MdStructureViewPresentableItem, MdBreadcrumbElement {
    override fun getAnchorReferenceId(htmlIdGenerator: HtmlIdGenerator?): String? {
        //String data = getNode().getUserData(MultiMarkdownTypes.HEADER_REF_ID_DATA_KEY);
        val headerTextElement = headerTextElement
        if (headerTextElement != null) {
            val renderingProfile = MdRenderingProfileManager.getInstance(project).getRenderingProfile(containingFile)
            val headerText = MdPsiImplUtil.getNodeText(headerTextElement, renderingProfile.parserSettings.anyOptions(ParserOptions.HEADER_ID_REF_TEXT_TRIM_TRAILING_SPACES), true)
            val parserSettings = renderingProfile.parserSettings
            if (htmlIdGenerator == null) {
                return HeaderIdGenerator.generateId(headerText, " -", "_",
                    parserSettings.anyOptions(ParserOptions.HEADER_ID_NO_DUPED_DASHES),
                    parserSettings.anyOptions(ParserOptions.HEADER_ID_NON_ASCII_TO_LOWERCASE)
                )
            } else {
                return htmlIdGenerator.getId(headerText)
            }
        }
        return null
    }

    override fun getAnchorReferenceId(): String? {
        return getAnchorReferenceId(null)
    }

    override fun getAttributedAnchorReferenceId(): String? {
        return getAttributedAnchorReferenceId(null)
    }

    override fun getAttributedAnchorReferenceId(htmlIdGenerator: HtmlIdGenerator?): String? {
        val id = node.findChildByType(MdTypes.HEADER_TEXT)
            ?.findChildByType(MdTypes.ATTRIBUTES)
            ?.findChildByType(MdTypes.ATTRIBUTE)
            ?.findChildByType(MdTypes.ATTRIBUTE_ID_VALUE)
            ?.text

        return if (!id.isNullOrEmpty()) {
            id
        } else {
            getAnchorReferenceId(htmlIdGenerator)
        }
    }

    override fun getAttributesElement(): MdAttributes? {
        return findChildByType<MdHeaderTextImpl>(MdTypes.HEADER_TEXT)?.attributesElement
    }

    override fun getAnchorReferenceElement(): PsiElement? {
        return nameIdentifier
    }

    override fun getTrailingAttributesLength(): Int {
        return findChildByType<MdHeaderTextImpl>(MdTypes.HEADER_TEXT)?.trailingAttributesLength ?: 0
    }

    override fun getIdValueAttribute(): MdAttributeIdValue? {
        return attributesElement?.idValueAttribute
    }

    override fun isReferenceFor(referenceId: String?): Boolean {
        return referenceId != null && referenceId.removePrefix("#").equals(getAnchorReferenceId(null), ignoreCase = true)
    }

    override fun isReferenceFor(refElement: MdLinkAnchor?): Boolean {
        if (refElement == null) return false
        val refElementName = refElement.name
        return refElementName.equals(getAnchorReferenceId(null), ignoreCase = true)
    }

    override fun createReference(textRange: TextRange, exactReference: Boolean): MdPsiReference {
        return MdPsiReference(this, textRange, exactReference)
    }

    override fun getDisplayName(): String {
        return name
    }

    override fun getNameIdentifier(): PsiElement? {
        return (node.findChildByType(MdTypes.HEADER_TEXT)?.findChildByType(MdTypes.ATTRIBUTES)?.psi as? MdAttributes)?.idValueAttribute
            ?: MdPsiImplUtil.findChildByType(this, MdTypes.HEADER_TEXT)
    }

    override fun getCompletionTypeText(): String {
        return "######".substring(0, headerLevel) + " " + headerText
    }

    override fun toString(): String {
        return """${this.javaClass.simpleName}{level: $headerLevel text: '${name.trim()}'}"""
    }

    override fun getLocationString(): String? {
        val anchorId = getAnchorReferenceId(null)
        return if (anchorId != null) "{#$anchorId}" else null
    }

    override fun getPresentableText(): String {
        //        return PsiBundle.message("header", headerLevel.toString())
        val headerTextElement = headerTextElement ?: return ""
        val plainText = MdPsiImplUtil.getNodeText(headerTextElement, true, false).trim()
        if (plainText.isNotEmpty()) return plainText
        return headerTextElement.text
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.HEADER
    }

    override fun nestedHeadingSectionLooping(wantNestedSubHeadings: Boolean): MdPsiIterator<PsiElement> {
        val psiFile = containingFile as? MdFile
        if (psiFile != null) {
            return siblingsLooping()
                .filterOutLeafPsi()
                .filterOutWhitespace()
                .nonRecursive()
                .acceptFilter { element, loop ->
                    if (element is MdHeaderElement) {
                        if (element.headerLevel <= this@MdHeaderElementImpl.headerLevel) {
                            loop.doBreak()
                        } else if (!wantNestedSubHeadings) {
                            // the following is for skipping descendant sub-headings which will be contained by their parents
                            //                                if (element.headerLevel > subHeadLevel) continue
                            //                                subHeadLevel = element.headerLevel

                            if (element.headerLevel > SUB_HEAD_KEY[loop.data]) loop.doContinue()
                            else loop.data[SUB_HEAD_KEY] = element.headerLevel
                        }
                    }
                    loop.isIncomplete
                }
        }
        @Suppress("UNCHECKED_CAST")
        return childLooping().filterFalse()
    }

    final override fun hasTaskItemDescendants(wantEmptyItems: Boolean, wantCompleteItems: Boolean, emptiesCombined: Boolean): Boolean {
        return headingSectionLooping()
            .filterCanContainTasksOrHeaders()
            .doLoop(false) { it, loop ->
                if (it is MdPsiElement && it.isTypeOf(MdTokenSets.CAN_CONTAIN_TASKS_OR_HEADERS)) {
                    it.childLooping()
                        .recurseCanContainTasksOrHeaders()
                        .filter(MdListItem::class.java) { it.isWantedTaskItem(wantEmptyItems, wantCompleteItems, emptiesCombined) }
                        .doLoop { _, itemLoop ->
                            loop.Return(true)
                            itemLoop.doReturn()
                        }
                }
            }
    }

    final override fun getTaskItemDescendantPriority(editContext: PsiEditContext, wantEmptyItems: Boolean, wantCompleteItems: Boolean, emptiesCombined: Boolean): Int {
        return headingSectionLooping()
            .filterCanContainTasksOrHeaders()
            .doLoop(-1) { it, loop ->
                if (it is MdPsiElement && it.isTypeOf(MdTokenSets.CAN_CONTAIN_TASKS_OR_HEADERS)) {
                    it.childLooping()
                        .recurseCanContainTasksOrHeaders()
                        .filter(MdListItem::class.java) { it.isWantedTaskItem(wantEmptyItems, wantCompleteItems, emptiesCombined) }
                        .doLoop { item, _ ->
                            val priority = max(item.getTaskItemPriority(editContext), MdTaskItemContainer.MAX_TASK_PRIORITY[loop.data])
                            MdTaskItemContainer.MAX_TASK_PRIORITY[loop.data] = priority
                            loop.setResult(priority)
                        }
                }
            }
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return object : ColoredItemPresentation {
            override fun getPresentableText(): String? {
                return this@MdHeaderElementImpl.presentableText
            }

            override fun getLocationString(): String? {
                return null
            }

            override fun getIcon(open: Boolean): Icon? {
                return null
            }

            override fun getTextAttributesKey(): TextAttributesKey? {
                return if (headerLevel == 1) MdHighlighterColors.getInstance().BOLD_ATTR_KEY else null
            }
        }
    }

    override fun getBreadcrumbInfo(): String {
        val settings = MdApplicationSettings.instance.documentSettings
        if (settings.showBreadcrumbText && node.text.isNotEmpty()) {
            val truncateStringForDisplay = MdPsiImplUtil.truncateStringForDisplay(node.text, settings.maxBreadcrumbText, false, true, true)
            if (truncateStringForDisplay.isNotEmpty()) return truncateStringForDisplay
        }
        return MdPsiBundle.message("header", headerLevel.toString())
    }

    override fun getBreadcrumbTooltip(): String? {
        return node.text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }

    override fun getPresentation(): ItemPresentation {
        return super.getPresentation()
    }

    companion object {
        private val SUB_HEAD_KEY = DataKey("SUB_HEAD_KEY", 7)
    }
}
