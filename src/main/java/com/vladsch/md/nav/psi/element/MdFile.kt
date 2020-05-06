// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.Key
import com.intellij.psi.FileViewProvider
import com.intellij.psi.tree.IElementType
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.md.nav.MdFileType
import com.vladsch.md.nav.MdLanguage
import com.vladsch.md.nav.flex.psi.FlexmarkFrontMatterBlock
import com.vladsch.md.nav.parser.cache.MdCachedFileElements
import com.vladsch.md.nav.parser.cache.data.CachedDataOwner
import com.vladsch.md.nav.parser.cache.data.CachedDataSet
import com.vladsch.md.nav.parser.cache.data.ProjectCachedData
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.md.nav.settings.ParserOptions
import com.vladsch.md.nav.util.FileRef
import com.vladsch.plugin.util.suffixWith
import icons.MdIcons
import javax.swing.Icon

class MdFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, MdLanguage.INSTANCE),
    MdTaskItemContainer,
    MdStructureViewPresentableElement,
    MdStructureViewPresentableItem,
    MdBlockElementWithChildren,
    CachedDataOwner {

    override fun getFileType(): FileType {
        return MdFileType.INSTANCE
    }

    override fun getIcon(flags: Int): Icon? {
        //return super.getIcon(flags);
        return when (subType) {
            WIKI_PAGE_SUBTYPE -> MdIcons.getWikiPageIcon()
            FLEXMARK_SUBTYPE -> MdIcons.Document.FLEXMARK_SPEC
            else -> MdIcons.getDocumentIcon()
        }
    }

    val isWikiPage: Boolean
        get() {
            val file = super.getVirtualFile()
            return file != null && FileRef(file).isWikiPage
        }

    private val myCachedData: CachedDataSet by lazy { ProjectCachedData.fileCachedData(this) }

    override fun getCachedData(): CachedDataSet {
        return myCachedData
    }

    val referencedElementMap: Map<String, Set<MdReferenceElement>> get() = MdCachedFileElements.getReferencedElementMap(this)
    val referenceDefinitionCounts: Map<IElementType, Map<String, Int>> get() = MdCachedFileElements.getReferenceDefinitionCounts(this)

    val links: List<MdLinkElement<*>> get() = MdCachedFileElements.listChildrenOfAnyType(this, false, true, false, arrayOf(MdLinkElement::class.java, MdExplicitLink::class.java, MdImageLink::class.java, MdReference::class.java))
    val referenceElements: List<MdReferenceElement> get() = MdCachedFileElements.listChildrenOfAnyType(this, false, true, true, arrayOf(MdReferenceElement::class.java))
    val headerElements: List<MdHeaderElement> get() = MdCachedFileElements.listChildrenOfAnyType(this, false, true, false, arrayOf(MdHeaderElement::class.java))
    val anchorTargets: List<MdAnchorTarget> get() = MdCachedFileElements.listChildrenOfAnyType(this, false, true, false, arrayOf(MdAnchorTarget::class.java))
    val references: List<MdReference> get() = MdCachedFileElements.listChildrenOfAnyType(this, false, true, true, arrayOf(MdReference::class.java))
    val referencingElements: List<MdReferencingElement> get() = MdCachedFileElements.listChildrenOfAnyType(this, false, true, true, arrayOf(MdReferencingElement::class.java))
    val referenceLinks: List<MdReferenceLink> get() = MdCachedFileElements.listChildrenOfAnyType(this, false, true, true, arrayOf(MdReferenceLink::class.java))

    fun getReferences(referenceId: String?): List<MdReferenceElement> {
        return referenceElements.filter { referenceId == null || it.isReferenceFor(referenceId) }.reversed()
    }

    val isIncludeFile: Boolean
        get() {
            val basePath = project.basePath
            if (basePath != null) {
                val fixedIncludePath = MdRenderingProfileManager.getProfile(this).parserSettings.optionsFlags and ParserOptions.GITBOOK_URL_ENCODING.flags == 0L
                if (!fixedIncludePath) {
                    // any file can be an include file
                    return true
                }
                val filePath = virtualFile?.path?.removePrefix(basePath.suffixWith('/'))
                if (filePath != null && filePath.startsWith("_includes/")) {
                    return fixedIncludePath
                }
            }
            return false
        }

    override fun getStructureViewPresentation(): ItemPresentation {
        return MdElementItemPresentation(this)
    }

    class MarkdownFilePresentation : ItemPresentation {
        override fun getLocationString(): String? {
            return null
        }

        override fun getIcon(unused: Boolean): Icon? {
            return null
        }

        override fun getPresentableText(): String? {
            return null
        }
    }

    override fun getLocationString(): String? {
        return null
    }

    override fun getPresentableText(): String? {
        return name
    }

    val subType: String
        get() {
            // diagnostic: 1032, sometimes an exception is thrown "AssertionError: Unexpected content storage modification"
            return if (isWikiPage) {
                WIKI_PAGE_SUBTYPE
            } else {
                if (DumbService.isDumb(project)) {
                    MARKDOWN_SUBTYPE
                } else {
                    // diagnostic: 1047
                    try {
                        // see if first child is jekyll or flexmark front matter block
                        when (firstChild) {
                            is FlexmarkFrontMatterBlock -> FLEXMARK_SUBTYPE
                            is MdJekyllFrontMatterBlock -> JEKYLL_SUBTYPE
                            else -> MARKDOWN_SUBTYPE
                        }
                    } catch (e: Throwable) {
                        MARKDOWN_SUBTYPE
                    }
                }
            }
        }

    override fun toString(): String {
        return when {
            virtualFile == null -> super.toString()
            context != null -> super.toString() + " : injected in " + virtualFile.name
            else -> super.toString() + " : " + virtualFile.name
        }
    }

    companion object {
        const val MARKDOWN_SUBTYPE: String = "Markdown"
        const val WIKI_PAGE_SUBTYPE: String = "WikiPage"
        const val JEKYLL_SUBTYPE: String = "Jekyll"
        const val FLEXMARK_SUBTYPE: String = "FlexmarkSpec"

        @JvmField
        // add BasedSequence.NULL if want result
        val FORMAT_RESULT: Key<BasedSequence> = Key.create("FORMAT_RESULT")

        @JvmField
        val TEST_PARAM: Key<Any> = Key.create("TEST_PARAM")

        @JvmStatic
        fun frontMatterOffset(chars: CharSequence, jekyllFrontMatter: Boolean, flexmarkFrontMatter: Boolean): Int {
            // if it starts with --- followed by white space
            var pos: Int
            val length = chars.length
            if (length > 9 && chars[0] == '-' && chars[1] == '-' && chars[2] == '-') {
                pos = 3
                while (pos < length && chars[pos] != '\n' && Character.isWhitespace(chars[pos])) pos++

                if (pos < length && chars[pos] == '\n') {
                    // have first line, need to find the last line
                    while (pos < length) {
                        if (pos + 2 < length && ((jekyllFrontMatter && chars[pos + 0] == '-' && chars[pos + 1] == '-') || (flexmarkFrontMatter && chars[pos + 0] == '.' && chars[pos + 1] == '.' && chars[pos + 2] == '.'))) {
                            pos += 3
                            while (pos < length && chars[pos] != '\n' && Character.isWhitespace(chars[pos])) pos++

                            // file ends on the marker
                            if (pos == length) return pos

                            if (pos < length && chars[pos] == '\n') {
                                // have the last line
                                pos++
                                return pos
                            } else {
                                // skip to end of line and try again
                                while (pos < length && chars[pos] != '\n') pos++
                                if (pos < length && chars[pos] == '\n') {
                                    pos++
                                } else {
                                    break
                                }
                            }
                        } else {
                            while (pos < length && chars[pos] != '\n') pos++
                            if (pos < length && chars[pos] == '\n') {
                                pos++
                            } else {
                                break
                            }
                        }
                    }
                }
            }
            return 0
        }
    }
}

