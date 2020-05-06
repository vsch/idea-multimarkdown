// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.language

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.openapi.util.Iconable
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.NullableFunction
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.language.api.MdLineMarkerExtension
import com.vladsch.md.nav.psi.element.*
import com.vladsch.md.nav.psi.reference.MdPsiReference
import com.vladsch.md.nav.psi.text.MdLineSelectionFakePsiElement
import com.vladsch.md.nav.psi.text.MdUrlFakePsiElement
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdPsiImplUtil.getLeafPsiElement
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.md.nav.settings.PegdownExtensions
import com.vladsch.md.nav.util.FileRef
import com.vladsch.md.nav.util.PathInfo
import com.vladsch.md.nav.vcs.GitHubLinkResolver
import com.vladsch.plugin.util.ifElse
import com.vladsch.plugin.util.psi.isIn
import com.vladsch.plugin.util.psi.isTypeOf
import com.vladsch.plugin.util.suffixWith
import icons.MdIcons
import javax.swing.Icon

class MdLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun getIcon(): Icon? {
        return MdIcons.Document.FILE
    }

    override fun isEnabledByDefault(): Boolean {
        return true
    }

    override fun getName(): String? {
        return "Markdown Navigator"
    }

    companion object {
        @JvmField
        val ID_PREFIX: String = MdLineMarkerProvider::class.java.name

        // @formatter:off
        @JvmField val LINK_REF_MARKDOWN             : Option = Option("$ID_PREFIX.link.ref.markdown"                , "Link: markdown file reference"       , MdIcons.getDocumentIcon())
        @JvmField val LINK_REF_FILE                 : Option = Option("$ID_PREFIX.link.ref.file"                    , "Link: file reference"                , MdIcons.EditorActions.Menu_copy)
        @JvmField val LINK_REF_GITHUB               : Option = Option("$ID_PREFIX.link.ref.gitHub"                  , "Link: GitHub URL"                    , MdIcons.LinkTypes.GitHub)
        @JvmField val LINK_REF_IMAGE                : Option = Option("$ID_PREFIX.link.ref.image"                   , "Link: image file reference"          , MdIcons.EditorActions.Menu_copy)
        @JvmField val LINK_REF_WEB                  : Option = Option("$ID_PREFIX.link.ref.web"                     , "Link: external URL"                  , MdIcons.LinkTypes.Web)
        @JvmField val LINK_REF_FTP                  : Option = Option("$ID_PREFIX.link.ref.ftp"                     , "Link: FTP"                           , MdIcons.LinkTypes.Ftp)
        @JvmField val LINK_REF_MAIL                 : Option = Option("$ID_PREFIX.link.ref.mail"                    , "Link: mail"                          , MdIcons.LinkTypes.Mail)
        @JvmField val LINK_REF_JET_BRAINS           : Option = Option("$ID_PREFIX.link.ref.jetbrains"               , "Link: Toolbox URL"                   , MdIcons.LinkTypes.JetBrains)
        @JvmField val LINK_REF_UPSOURCE             : Option = Option("$ID_PREFIX.link.ref.upsource"                , "Link: Upsource URL"                  , MdIcons.LinkTypes.Upsource)
        @JvmField val LINK_REF_CUSTOM_URI           : Option = Option("$ID_PREFIX.link.ref.custom"                  , "Link: custom protocol URL"           , MdIcons.LinkTypes.CustomUri)

        @JvmField val LINK_ANCHOR_HEADING           : Option = Option("$ID_PREFIX.link.anchor.heading"              , "Link Anchor: heading"                , MdIcons.Element.HEADER)
        @JvmField val LINK_ANCHOR_A_TAG             : Option = Option("$ID_PREFIX.link.anchor.a.tag"                , "Link Anchor: HTML 'a' tag"           , MdIcons.Element.ANCHOR)
        @JvmField val LINK_ANCHOR_ID_ATTRIBUTE      : Option = Option("$ID_PREFIX.link.anchor.id.attribute"         , "Link Anchor: name attribute"         , MdIcons.Element.ATTRIBUTE_ID_VALUE)
        @JvmField val LINK_ANCHOR_LINE_SELECTION    : Option = Option("$ID_PREFIX.link.anchor.line.selection"       , "Link Anchor: line selection"         , MdIcons.Element.ANCHOR)

        @JvmField val REFERENCE_REF_LINK            : Option = Option("$ID_PREFIX.reference.ref.link"               , "Reference: ref link"                 , MdIcons.Element.REFERENCE)
        @JvmField val REFERENCE_REF_IMAGE           : Option = Option("$ID_PREFIX.reference.ref.image"              , "Reference: ref image"                , MdIcons.Element.REFERENCE)

// @formatter:on

        private val ourOptions = arrayOf(
            LINK_REF_MARKDOWN,
            LINK_REF_FILE,
            LINK_REF_GITHUB,
            LINK_REF_IMAGE,
            LINK_REF_WEB,
            LINK_REF_FTP,
            LINK_REF_MAIL,
            LINK_REF_JET_BRAINS,
            LINK_REF_UPSOURCE,
            LINK_REF_CUSTOM_URI,

            LINK_ANCHOR_HEADING,
            LINK_ANCHOR_A_TAG,
            LINK_ANCHOR_ID_ATTRIBUTE,
            LINK_ANCHOR_LINE_SELECTION,

            REFERENCE_REF_LINK,
            REFERENCE_REF_IMAGE
        )

        private val REFERENCING_ELEMENT_NAMER = NullableFunction<PsiElement, String> {
            when (it) {
                is MdHeaderText -> it.containingFile.name + "#" + (it.parent as MdHeaderElement).anchorReferenceId
                is MdNamedElementImpl -> it.displayName
                is MdFile -> it.name
                is PsiFile -> it.name
                else -> MdPsiImplUtil.truncateStringForDisplay(it.text, 500, false, true, false)
            }
        }
    }

    override fun getOptions(): Array<Option> {
        val options = ArrayList<Option>();
        options.addAll(ourOptions)

        for (extension in MdLineMarkerExtension.EXTENSIONS.value) {
            val extOptions = extension.options
            options.addAll(extOptions)
        }

        return options.toTypedArray()
    }

    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        if (!MdApplicationSettings.instance.documentSettings.enableLineMarkers) return

        if (element is LeafPsiElement || element.isTypeOf(MdTokenSets.LINK_REF_OR_ANCHOR_TYPES)) {
            var handled = false;
            val provider = object : MdLineMarkerExtension.LineMarkerProvider {
                override fun collectFileRefMarkers(leafElement: PsiElement, element: PsiElement, resolveResults: Array<out ResolveResult>?) {
                    collectFileRefMarkers(leafElement, element, resolveResults, result);
                }

                override fun collectReferencingMarkers(leafElement: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>, navigationText: String?): Boolean {
                    return collectReferencingMarkersImpl(leafElement, result, navigationText)
                }
            }

            for (extension in MdLineMarkerExtension.EXTENSIONS.value) {

                if (extension.collectNavigationMarkers(element, result, provider)) {
                    handled = true;
                    break
                }
            }

            if (!handled) {
                when {
                    collectLinkMarkers(element, result) -> return
                    collectReferencingLinkMarkers(element, result) -> return

                    // Not very useful since it always creates a reference to containing file an collectFileRefMarkers excludes these from line markers
                    // because they don't add any information for navigation
                    // collectPlainTextMarkers(element, result) -> return
                }
            }
        }

        return
    }

    private fun collectLinkMarkers(leafElement: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>): Boolean {
        if (leafElement !is LeafPsiElement || !leafElement.isTypeOf(MdTokenSets.LINK_REF_OR_ANCHOR_TYPES)) return false

        val element: MdLinkElement<*> = MdPsiImplUtil.findAncestorOfType(leafElement, MdLinkElement::class.java) ?: return false

        if (element is MdAutoLink) {
            if ((MdRenderingProfileManager.getProfile(element.containingFile).parserSettings.pegdownFlags and PegdownExtensions.AUTOLINKS.flags) == 0) {
                if (element.node.firstChildNode.elementType == MdTypes.AUTO_LINK_REF) {
                    // an unwrapped link with parser option not enabled
                    return true
                }
            }
        }

        val anchorElement = element.linkAnchorElement
        val linkRefElement = element.linkRefElement

        // use anchor if it is available
        if ((anchorElement == null || anchorElement.nameIdentifier == null) && linkRefElement == null) {
            return false
        }

        if (PathInfo.isOnlyUri(linkRefElement?.text)) {
            return true
        }

        val psiAnchorReference = if (anchorElement != null && anchorElement.reference != null && anchorElement.reference!!.resolve() != null) anchorElement.reference else null
        val anchorResults = (psiAnchorReference as? MdPsiReference)?.multiResolve(false)
        val filteredAnchorResults = anchorResults?.filter {
            val psiElement = it.element
            if (!it.isValidResult || psiElement == null) false
            else when (psiElement) {
                null -> false
                is MdHeaderText -> LINK_ANCHOR_HEADING.isEnabled && LINK_REF_MARKDOWN.isEnabled
                is MdRefAnchorId -> LINK_ANCHOR_A_TAG.isEnabled && LINK_REF_MARKDOWN.isEnabled
                is MdAttributeIdValue -> LINK_ANCHOR_ID_ATTRIBUTE.isEnabled && LINK_REF_MARKDOWN.isEnabled
                is MdLineSelectionFakePsiElement -> LINK_ANCHOR_LINE_SELECTION.isEnabled && when (psiElement.parent.containingFile) {
                    is MdFile -> LINK_REF_MARKDOWN.isEnabled
                    else -> LINK_REF_FILE.isEnabled
                }
                is MdUrlFakePsiElement -> false // let link handle it
                else -> true
            }
        }?.toTypedArray()

        if (leafElement.isTypeOf(MdTokenSets.LINK_REF_TYPES)) {
            if (filteredAnchorResults?.isNotEmpty() == true) return true

            val psiLinkReference = linkRefElement?.reference
            val linkResults: Array<out ResolveResult> = if (psiLinkReference is MdPsiReference) {
                if (psiLinkReference.element is MdWikiLinkRef) {
                    psiLinkReference.multiResolve(false)
                } else {
                    val resolved = psiLinkReference.resolve()
                    if (resolved != null) {
                        if (resolved is MdFile && resolved.isWikiPage) {
                            // need to use multi-resolve because even explicit links can have multi-resolve wiki targets
                            psiLinkReference.multiResolve(false)
                        } else {
                            arrayOf(PsiElementResolveResult(resolved))
                        }
                    } else {
                        arrayOf()
                    }
                }
            } else arrayOf()

            if (linkResults.isNotEmpty()) {
                val filtered = linkResults.filter {
                    val psiElement = it.element
                    if (!it.isValidResult || psiElement == null) false
                    else when (psiElement) {
                        null -> false
                        is MdFile -> LINK_REF_MARKDOWN.isEnabled
                        is PsiFile -> PathInfo.isImageExt(psiElement.virtualFile.extension ?: "").ifElse(LINK_REF_IMAGE.isEnabled, LINK_REF_FILE.isEnabled)
                        is MdLineSelectionFakePsiElement -> when (psiElement.parent.containingFile) {
                            is MdFile -> LINK_REF_MARKDOWN.isEnabled
                            else -> LINK_REF_FILE.isEnabled
                        }
                        is MdUrlFakePsiElement -> when (psiElement.getIcon(false)) {
                            MdIcons.LinkTypes.Web -> LINK_REF_WEB.isEnabled
                            MdIcons.LinkTypes.Ftp -> LINK_REF_FTP.isEnabled
                            MdIcons.LinkTypes.Mail -> LINK_REF_MAIL.isEnabled
                            MdIcons.LinkTypes.GitHub -> LINK_REF_GITHUB.isEnabled
                            MdIcons.LinkTypes.JetBrains -> LINK_REF_JET_BRAINS.isEnabled
                            MdIcons.LinkTypes.Upsource -> LINK_REF_UPSOURCE.isEnabled
                            MdIcons.LinkTypes.CustomUri -> LINK_REF_CUSTOM_URI.isEnabled
                            else -> true
                        }
                        else -> true
                    }
                }.toTypedArray()

                collectFileRefMarkers(leafElement, element, filtered, result)
            }
        } else if (filteredAnchorResults?.isNotEmpty() == true) {
            collectFileRefMarkers(leafElement, element, filteredAnchorResults, result)
        }
        return true
    }

    private fun collectFileRefMarkers(leafElement: PsiElement, element: PsiElement, resolveResults: Array<out ResolveResult>?, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        if (resolveResults != null && resolveResults.isNotEmpty()) {
            val containingFile = element.containingFile
            val project = element.project
            val basePath = if (project.basePath == null) "/" else project.basePath.suffixWith('/')
            val isAnchor = leafElement.isTypeOf(MdTokenSets.LINK_ANCHOR_TYPES)

            if (resolveResults.isNotEmpty()) {
                val linkTargets = ArrayList<PsiElement>()
                var icon: Icon? = null

                for (resolveResult in resolveResults) {
                    val resolveResultElement = resolveResult.element ?: continue
                    if (!isAnchor) {
                        if (resolveResultElement is PsiFileSystemItem && !((resolveResults.size == 1 || resolveResults.size != 2) && resolveResultElement === containingFile)) {
                            if (icon == null) icon = resolveResultElement.getIcon(0)
                            linkTargets.add(resolveResultElement)
                        } else if (resolveResultElement is MdNamedElementImpl) {
                            if (resolveResultElement is MdHeaderText) {
                                if (icon == null) icon = (resolveResultElement as PsiElement).containingFile.getIcon(0)
                                linkTargets.add((resolveResultElement as PsiElement).containingFile)
                            }
                        } else {
                            if (icon == null) icon = resolveResultElement.getIcon(0)
                            linkTargets.add(resolveResultElement)
                        }
                    } else {
                        if (icon == null) icon = resolveResultElement.getIcon(0)
                        linkTargets.add(resolveResultElement)
                    }
                }

                if (linkTargets.size > 0) {
                    val useIcon = if (linkTargets.size > 1) MdIcons.getMultiIcon(icon ?: MdIcons.getDocumentIcon()) else icon ?: MdIcons.getDocumentIcon()
                    val cellRenderer = FileReferenceCellRenderer(resolveResults.size > 1, containingFile, linkTargets[0], basePath)

                    val builder = NavigationGutterIconBuilder.create(useIcon)
                        .setCellRenderer(cellRenderer)
                        .setTargets(linkTargets)
                        .setNamer(REFERENCING_ELEMENT_NAMER)
                        .setTooltipText(MdBundle.message("linemarker.navigate-to-target", REFERENCING_ELEMENT_NAMER.`fun`(linkTargets[0])) ?: "")

                    val useLeafPsiElement = getLeafPsiElement(leafElement)
                    val markerInfo = builder.createLineMarkerInfo(useLeafPsiElement)
                    result.add(markerInfo)
                }
            }
        }
    }

    internal class FileReferenceCellRenderer(
        private val multiResolve: Boolean,
        private val containingFile: PsiFile,
        private val firstTarget: PsiElement,
        private val basePath: String
    ) : PsiElementListCellRenderer<PsiElement>() {

        override fun getElementText(fileElement: PsiElement): String {
            return if (fileElement is PsiFile) {
                val fileRef = FileRef(fileElement)
                if (fileRef.isUnderWikiDir && multiResolve) {
                    // need subdirectory and extension, there is more than one match
                    PathInfo.relativePath(fileRef.wikiDir.suffixWith('/'), fileRef.filePath, false, false)
                } else {
                    GitHubLinkResolver(containingFile).linkAddress(fileRef, null, null, null)
                }
            } else if (fileElement is MdHeaderText) {
                fileElement.containingFile.name + "#" + (fileElement.parent as MdHeaderElement).anchorReferenceId!!
            } else if (fileElement is MdNamedElementImpl) {
                fileElement.displayName
            } else "<unknown>"
        }

        override fun getIcon(element: PsiElement): Icon {
            val firstItem = element === firstTarget
            return if (firstItem) element.getIcon(0) else MdIcons.getHiddenIcon(element.getIcon(0))
        }

        override fun getContainerText(element: PsiElement, name: String): String? {
            val fileRef = FileRef(element as? PsiFile ?: element.containingFile)
            val repoDir: String = (fileRef.isUnderWikiDir).ifElse(fileRef.wikiDir, fileRef.path)
            return PathInfo.relativePath(basePath, repoDir, withPrefix = false, blobRawEqual = false)
        }

        override fun getIconFlags(): Int {
            return Iconable.ICON_FLAG_READ_STATUS
        }
    }

    private fun collectReferencingLinkMarkers(leafElement: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>): Boolean {
        val elementType = leafElement.node.elementType
        if (!elementType.isIn(MdTokenSets.LINE_MARKER_REFERENCING_ID_SET)) {
            return false
        }

        val element = leafElement.nextSibling as? MdReferencingElementReferenceImpl ?: return false
        val enabled = when (element) {
            is MdReferenceLinkReference -> REFERENCE_REF_LINK.isEnabled
            is MdReferenceImageReference -> REFERENCE_REF_IMAGE.isEnabled
            else -> false
        }

        if (!enabled) return true
        return collectReferencingMarkersImpl(leafElement, result, null)
    }

    private fun collectReferencingMarkersImpl(leafElement: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>, navigationText: String?): Boolean {
        val element = leafElement.nextSibling as? MdReferencingElementReferenceImpl ?: return false
        val psiReference = element.reference

        // set incomplete code so that incorrect image ext for ref images will still be resolved
        val results = if (psiReference != null) (psiReference as MdPsiReference).multiResolve(true) else null

        if (results != null && results.isNotEmpty()) {
            var useResults: Array<ResolveResult> = results

            if (useResults.size == 1) {
                val reference = results[0].element
                if (reference is MdReferenceIdentifier) {
                    val mdReference = reference.parent as? MdReference
                    if (mdReference != null) {
                        val linkAnchor = mdReference.linkAnchorElement
                        useResults = if (linkAnchor != null) {
                            (linkAnchor.reference as? MdPsiReference)?.multiResolve(false) ?: results
                        } else {
                            (mdReference.linkRefElement?.reference as? MdPsiReference)?.multiResolve(false) ?: results
                        }
                    }
                }
            }

            if (useResults.isNotEmpty()) {
                val linkTargets = ArrayList<PsiElement>()
                var icon: Icon? = null
                var useNavigationText: String? = null

                for (resolveResult in useResults) {
                    val resolveResultElement = resolveResult.element
                    if (resolveResultElement != null) {
                        if (icon == null) icon = resolveResultElement.getIcon(0)
                        linkTargets.add(resolveResultElement)
                        if (useNavigationText == null && navigationText != null)
                            useNavigationText = navigationText + " " + resolveResultElement.text
                    }
                }

                if (linkTargets.size > 0) {
                    val useIcon = if (linkTargets.size > 1) MdIcons.getMultiIcon(icon ?: MdIcons.getDocumentIcon()) else icon ?: MdIcons.getDocumentIcon()
                    val cellRenderer = ReferencingCellRenderer(linkTargets[0])

                    val builder = NavigationGutterIconBuilder.create(useIcon)
                        .setCellRenderer(cellRenderer)
                        .setTargets(linkTargets)
                        .setNamer(REFERENCING_ELEMENT_NAMER)
                        .setTooltipText(useNavigationText ?: MdBundle.message("linemarker.navigate-to-reference-page", REFERENCING_ELEMENT_NAMER.`fun`(linkTargets[0])))

                    val useLeafPsiElement = getLeafPsiElement(leafElement)
                    result.add(builder.createLineMarkerInfo(useLeafPsiElement))
                }
            }
        }
        return true
    }

    internal class ReferencingCellRenderer(private val firstTarget: PsiElement) : PsiElementListCellRenderer<PsiElement>() {
        override fun getElementText(fileElement: PsiElement): String {
            return when (fileElement) {
                is MdNamedElementImpl -> fileElement.displayName
                is MdFile -> fileElement.name
                else -> "<unknown>"
            }
        }

        override fun getIcon(element: PsiElement): Icon {
            val firstItem = element === firstTarget
            return if (firstItem) element.getIcon(0) else MdIcons.getHiddenIcon(element.getIcon(0))
        }

        override fun getContainerText(element: PsiElement, name: String): String? {
            return ""
        }

        override fun getIconFlags(): Int {
            return Iconable.ICON_FLAG_READ_STATUS
        }
    }

//    /**
//     * Not very useful since it always creates a reference to containing file an collectFileRefMarkers excludes these from line markers
//     * because they don't add any information for navigation
//     */
//    private fun collectPlainTextMarkers(leafElement: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>): Boolean {
//        val element: MdPlainText<*> = MdPsiImplUtil.findAncestorOfType(leafElement, MdPlainText::class.java) ?: return false
//
//        // now walk through the matches and get References from Plain Text
//        // see if it resolves to the file
//        val resolver = GitHubLinkResolver(element.containingFile)
//        val fileRef = FileRef(element.containingFile)
//        val textMapMatches = element.textMapMatches
//        for (match in textMapMatches) {
//            val linkRef = LinkRef.parseLinkRef(fileRef, match.replacedText, null)
//            val targetRef = resolver.resolve(linkRef, Want.invoke(Local.REF, Remote.REF, Links.NONE), null) as? ProjectFileRef ?: continue
//            if (targetRef.psiFile != null) {
//                val startOffset = match.replacedStart
//                val endOffset = match.replacedEnd
//                val plainTextElement = MdPlainTextElementImpl(element, startOffset, endOffset, element.referenceableOffsetInParent)
//                //                resolveResults.add(PsiElementResolveResult(targetRef.psiFile!!))
//                collectFileRefMarkers(leafElement, plainTextElement, arrayOf(PsiElementResolveResult(targetRef.psiFile!!)), result)
//            }
//        }
//        return true
//    }
}
