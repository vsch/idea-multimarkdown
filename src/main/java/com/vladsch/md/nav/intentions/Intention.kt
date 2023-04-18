// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.intentions

import com.intellij.codeInsight.FileModificationService
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.IntentionManager
import com.intellij.codeInsight.intention.impl.config.IntentionManagerSettings
import com.intellij.lang.ASTNode
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import com.intellij.util.ReflectionUtil
import com.vladsch.md.nav.MdLanguage
import com.vladsch.md.nav.actions.handlers.util.PsiEditAdjustment
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.psi.element.MdLinkRefElement
import com.vladsch.md.nav.psi.element.MdWikiLink
import com.vladsch.md.nav.psi.reference.MdPsiReferenceWikiLinkRef
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.md.nav.util.*
import com.vladsch.md.nav.vcs.GitHubLinkResolver
import com.vladsch.plugin.util.psi.isTypeIn
import icons.MdIcons
import java.util.*
import javax.swing.Icon

/**
 * @noinspection AbstractMethodCallInConstructor, OverridableMethodCallInConstructor
 */
abstract class Intention protected constructor() : IntentionAction, Iconable {

    var allInFileAdded: Boolean = false

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        if (!ensureFileWritable(project, file)) {
            return
        }

        val element = findMatchingElement(file, editor) ?: return
        if (element.isValid) processIntention(element, project, editor, PsiEditAdjustment(file, editor.document.immutableCharSequence))
        else {
            LOG.warn("Intention.invoke called on $element, with isValid() == false")
        }
    }

    protected open fun addAllInFileAction(project: Project) {
        if (!allInFileAdded) {
            allInFileAdded = true
            val options = getOptions()
            if (options.isNotEmpty()) {
                val manager = IntentionManager.getInstance()
                for (option in options) {
                    manager.addAction(option)
                }
            }
        }
    }

    @Throws(IncorrectOperationException::class)
    abstract fun processIntention(element: PsiElement, project: Project, editor: Editor, editContext: PsiEditContext)

    abstract fun getElementPredicate(): PsiElementPredicate

    open fun getOptions(): MutableList<IntentionAction> {
        return Collections.emptyList()
    }

    open fun isAvailableIn(file: PsiFile): Boolean {
        return file.viewProvider.languages.contains(MdLanguage.INSTANCE)
    }

    protected open fun findMatchingElement(file: PsiFile, editor: Editor): PsiElement? {
        if (!isAvailableIn(file)) {
            return null
        }

        val selectionModel = editor.selectionModel
        val elementPredicate = getElementPredicate()

        if (selectionModel.hasSelection()) {
            val start = selectionModel.selectionStart
            val end = selectionModel.selectionEnd

            if (elementPredicate is PsiElementPredicateWithEditor && !elementPredicate.satisfiedBy(editor, start, end)) return null

            if (start in 0 .. end) {
                val selectionRange = TextRange(start, end)
                var element = findElementInRange(file, start, end, PsiElement::class.java)
                while (element != null && element.textRange != null && selectionRange.contains(element.textRange)) {
                    if (elementPredicate.satisfiedBy(element)) {
                        return element
                    }
                    element = element.parent
                }
            }
        }

        val position = editor.caretModel.offset
        if (elementPredicate is PsiElementPredicateWithEditor && !elementPredicate.satisfiedBy(editor, position, position)) return null

        var element = file.findElementAt(position)
        while (element != null) {
            if (elementPredicate.satisfiedBy(element)) return element
            if (isStopElement(element)) break
            element = element.parent
        }

        element = file.findElementAt(position - 1)
        while (element != null) {
            if (elementPredicate.satisfiedBy(element)) return element
            if (isStopElement(element)) return null
            element = element.parent
        }

        return null
    }

    open fun isStopElement(element: PsiElement): Boolean = element is PsiFile

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        if (isAvailableIn(file)) {
            addAllInFileAction(project)
            return findMatchingElement(file, editor) != null
        }
        return false
    }

    override fun startInWriteAction(): Boolean = true

    open val prefix: String get() = computePrefix(this)

    // re-enable lazy if using different icons for intentions
    //val icon: Icon? get() = MarkdownIcons.IntentionActions.getCategoryIcon(MarkdownIcons.MULTI_MARKDOWN_CATEGORY)

    open val icon: Icon? by lazy {
        val intentionMetaData = IntentionManagerSettings.getInstance().metaData
        val familyName = familyName
        var icon: Icon? = null
        for (metaData in intentionMetaData) {
            if (metaData.action.familyName == familyName) {
                icon = MdIcons.IntentionActions.getCategoryIcon(metaData.myCategory)
                break
            }
        }
        icon ?: MdIcons.IntentionActions.getCategoryIcon(MdIcons.MARKDOWN_CATEGORY)
    }

    override fun getIcon(flags: Int): Icon? = icon

    override fun getText(): String = IntentionsBundle.message(prefix)

    override fun getFamilyName(): String = IntentionsBundle.message(prefix)

    open class IntentionCompanion(val intentionClass: Class<out Intention>) {
        val isEnabled: Boolean get() = isIntentionEnabled(intentionClass)
    }

    companion object {
        @JvmStatic
        val LOG: Logger = Logger.getInstance("com.vladsch.md.nav.intentions")

        @JvmStatic
        fun isIntentionEnabled(intentionClass: Class<out Intention>): Boolean {
            val intentionActionFamilyName = computePrefix(intentionClass)
            for (availableIntention in IntentionManager.getInstance().availableIntentions) {
                if (availableIntention.familyName == intentionActionFamilyName) return true
            }
            return false
        }

        @JvmStatic
        fun computePrefix(intention: Intention): String = computePrefix(intention.javaClass)

        @JvmStatic
        fun computePrefix(intentionClass: Class<out Intention>): String {
            val name = intentionClass.simpleName
            val buffer = StringBuilder(name.length + 10)
            buffer.append(Character.toLowerCase(name[0]))
            for (i in 1 until name.length) {
                val c = name[i]
                if (Character.isUpperCase(c)) {
                    buffer.append('.')
                    buffer.append(Character.toLowerCase(c))
                } else {
                    buffer.append(c)
                }
            }
            return buffer.toString()
        }

        @JvmStatic
        fun isWhiteSpaceOrNls(sibling: PsiElement?): Boolean = sibling != null && isWhiteSpaceOrNls(sibling.node)

        @JvmStatic
        fun isWhiteSpaceOrNls(node: ASTNode?): Boolean =
            node != null && node.isTypeIn(MdTokenSets.WHITESPACE_SET)

        @Suppress("UNUSED_PARAMETER")
        private fun checkRanges(element: PsiElement, startOffset: Int, endOffset: Int): Boolean {
            if (element.textRange.startOffset == startOffset) {
                return true
            }

            return false
        }

        inline fun <reified T : PsiElement> resolveElement(element: MdLinkRefElement?): T? {
            val reference: PsiReference = element?.reference ?: return null
            val resolved: PsiElement = reference.resolve() ?: return null
            return resolved as? T?
        }

        @Suppress("NAME_SHADOWING")
        @JvmStatic
        fun <T : PsiElement> findElementInRange(file: PsiFile, startOffset: Int, endOffset: Int, klass: Class<T>): T? {
            var startOffset = startOffset
            var endOffset = endOffset
            var element1 = file.viewProvider.findElementAt(startOffset, file.language)
            var element2 = file.viewProvider.findElementAt(endOffset - 1, file.language)
            if (element1 == null || element2 == null) return null

            if (isWhiteSpaceOrNls(element1)) {
                startOffset = element1.textRange.endOffset
                element1 = file.viewProvider.findElementAt(startOffset, file.language)
            }
            if (isWhiteSpaceOrNls(element2)) {
                endOffset = element2.textRange.startOffset
                element2 = file.viewProvider.findElementAt(endOffset - 1, file.language)
            }

            if (element2 == null || element1 == null) return null
            val commonParent = PsiTreeUtil.findCommonParent(element1, element2)!!

            @Suppress("UNCHECKED_CAST")
            val element = (if (ReflectionUtil.isAssignable(klass, commonParent.javaClass)) commonParent as T? else PsiTreeUtil.getParentOfType(commonParent, klass))
                ?: return null

            if (!checkRanges(element, startOffset, endOffset)) {
                return null
            }

            return element
        }

        @JvmStatic
        @Suppress("UNUSED_PARAMETER")
        fun ensureFileWritable(project: Project, file: PsiFile): Boolean =
            FileModificationService.getInstance().preparePsiElementsForWrite(file)

        @JvmStatic
        fun isWikiLinkTextResolvable(element: MdWikiLink): Boolean {
            // see if need to swap link ref and link text
            val elementTypes = MdPsiImplUtil.getNamedElementTypes(element)

            if (elementTypes == null || elementTypes !== MdPsiImplUtil.WIKI_LINK_ELEMENT) return false

            val textType = elementTypes.textType ?: return false
            val textElement = MdPsiImplUtil.findChildByType(element, textType) as PsiNamedElement? ?: return false
            val linkRefElement = MdPsiImplUtil.findChildByType(element, elementTypes.linkRefType) as MdLinkRefElement?
                ?: return false
            val reference = linkRefElement.reference ?: return false

            val wikiPageTextName = textElement.name
            if (wikiPageTextName != null) {
                // see if the link text resolves to a page
                if (wikiPageTextName != linkRefElement.nameWithAnchor) {
                    val containingFile = ProjectFileRef(element.containingFile.originalFile)
                    val resolver = GitHubLinkResolver(containingFile)
                    val linkRefInfo = LinkRef.parseWikiLinkRef(containingFile, wikiPageTextName, null)
                    val targetRefs = resolver.multiResolve(linkRefInfo, Want(), null)
                    val targetInfo = if (targetRefs.isNotEmpty()) targetRefs[0] else null

                    if (targetRefs.isNotEmpty() && targetInfo != null) {
                        // have a resolve target
                        if ((reference as MdPsiReferenceWikiLinkRef).isResolveRefMissing) {
                            return false
                        } else if (WikiLinkRef.fileAsLink(targetInfo.fileNameNoExt) == wikiPageTextName) {
                            return true
                        }
                    } else if (wikiPageTextName.length > 1 && wikiPageTextName.startsWith("#")) {
                        // see if we have an anchor on this page matching what follows the #
                        val anchorRef = wikiPageTextName.substring(1)
                        if (containingFile.psiFile is MdFile) {
                            val markdownFile = containingFile.psiFile as MdFile? ?: return false
                            val headerElements = markdownFile.anchorTargets
                            for (headerElement in headerElements) {
                                if (headerElement.isReferenceFor(anchorRef)) {
                                    return true
                                }
                            }
                        }
                    }
                }
            }
            return false
        }
    }
}
