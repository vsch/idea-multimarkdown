// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.util

import com.intellij.openapi.diagnostic.ControlFlowException
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.PsiReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Processor
import com.vladsch.md.nav.psi.element.*
import com.vladsch.md.nav.psi.index.MdLinkElementIndex
import com.vladsch.md.nav.psi.index.MdReferenceableTextIndex
import com.vladsch.md.nav.psi.text.MdPlainTextElementImpl
import com.vladsch.md.nav.psi.text.MdPlainTextElementPsiReference
import com.vladsch.md.nav.util.*
import com.vladsch.md.nav.vcs.GitHubLinkResolver
import java.util.regex.PatternSyntaxException

class MdIndexUtil {
    companion object {
        private val LOG = Logger.getInstance("com.vladsch.md.nav.language.reference-search")

        @JvmStatic
        fun processReferences(refElement: PsiFileSystemItem, effectiveSearchScope: GlobalSearchScope, consumer: Processor<in PsiReference>): Boolean {
            val project = refElement.project
            if (DumbService.isDumb(project)) return false

            val linkAsFileName: (String) -> String
            val matchLinkAnchor: Boolean
            val useWikiPageMatching: Boolean

            if (refElement is MdFile && refElement.isWikiPage) {
                linkAsFileName = fun(linkText: String): String = WikiLinkRef.linkAsFile(linkText)
                matchLinkAnchor = true
                useWikiPageMatching = true
            } else {
                linkAsFileName = fun(linkText: String): String = PathInfo(LinkRef.urlDecode(linkText)).fileNameNoExt
                matchLinkAnchor = false
                useWikiPageMatching = false
            }

            val pathInfo = PathInfo(refElement.name)
            val fileNameNoExt = pathInfo.fileNameNoExt
            val fileName = pathInfo.fileName
            val keys = ArrayList<String>()

            MdLinkElementIndex.getInstance().processAllKeys(project) { key ->
                //                System.out.println("Key: $key")
                val linkRefAddress: String
                var linkAnchor: String? = null
                val pos = key.indexOf('#')
                if (pos >= 0) {
                    // chop off at last # and use that
                    if (matchLinkAnchor) linkAnchor = "#" + key.substring(pos + 1)
                    linkRefAddress = linkAsFileName(key.substring(0, pos))
                } else {
                    linkRefAddress = linkAsFileName(key)
                }

                if (useWikiPageMatching) {
                    // have to match several characters for spaces
                    var handled = false
                    try {
                        val regex = WikiLinkRef.linkAsFileRegex(linkRefAddress).toRegex()
                        if (fileNameNoExt.matches(regex) || fileName.matches(regex)) {
                            // have possible match, we process this key now
                            keys.add(key)
                            handled = true
                        }
                    } catch (e: PatternSyntaxException) {
                        // invalid link address, happens when url is wrapped in ()
                        handled = true
                    }

                    if (linkAnchor != null && !handled) {
                        // wiki pages can have anchor looking text embedded in them, include matches just in case
                        try {
                            val regex1 = "$linkRefAddress\\Q$linkAnchor\\E".toRegex()
                            if (fileNameNoExt.matches(regex1) || fileName.matches(regex1)) {
                                // have possible match, we process this key now
                                keys.add(key)
                            }
                        } catch (e: PatternSyntaxException) {
                            // invalid link address, happens when url is wrapped in ()
                        }
                    }
                } else {
                    if (fileNameNoExt == linkRefAddress) {
                        // have possible match, we process this key now
                        keys.add(key)
                    }
                }

                true
            }

            for (key in keys) {
                try {
                    val links: MutableCollection<*> = MdLinkElementIndex.getInstance().get(key, project, effectiveSearchScope)
                    for (link in links) {
                        // diagnostic/3117, ClassCastException: cannot be cast to MdLinkElement
                        if (link !is MdLinkElement<*>) continue
                        assert(MdPsiImplUtil.inScope(effectiveSearchScope, link.containingFile))

                        val reference = link.linkRefElement?.reference ?: continue
                        val resolve = reference.resolve() ?: continue
                        if (resolve === refElement) {
                            if (!consumer.process(reference)) return false
                        }
                    }
                } catch (e: Throwable) {
                    if (e is ControlFlowException) {
                        throw e
                    } else {
                        LOG.error(e)
                        break
                    }
                }
            }

            keys.clear()

            // plain text file reference search with processing
            MdReferenceableTextIndex.getInstance().processAllKeys(project) { key ->
                val linkRefAddress: String
                var linkAnchor: String? = null
                val pos = key.indexOf('#')

                if (pos >= 0) {
                    // chop off at last # and use that
                    linkRefAddress = linkAsFileName(key.substring(0, pos))
                    if (matchLinkAnchor) linkAnchor = "#" + linkAsFileName(key.substring(pos + 1))
                } else {
                    linkRefAddress = linkAsFileName(key)
                }

                if (fileNameNoExt == linkRefAddress || (linkAnchor != null && fileNameNoExt == linkRefAddress + linkAnchor)) {
                    keys.add(key)
                }

                true
            }

            for (key in keys) {
                val elements = MdReferenceableTextIndex.getInstance().get(key, project, effectiveSearchScope)
                val fromList = listOf(ProjectFileRef(refElement.virtualFile, project))

                for (element in elements) {
                    assert(MdPsiImplUtil.inScope(effectiveSearchScope, element.containingFile))

                    // see if it resolves to the file
                    val resolver = GitHubLinkResolver(element.containingFile)
                    val fileRef = FileRef(element.containingFile)
                    val textMapMatches = element.textMapMatches
                    for (match in textMapMatches) {
                        if (match.replacedText != key) continue

                        val linkRef = LinkRef.parseLinkRef(fileRef, match.replacedText, null)
                        val targetRef = resolver.resolve(linkRef, Want.invoke(Local.REF, Remote.REF, Links.NONE), fromList) as? ProjectFileRef
                            ?: continue
                        if (targetRef.psiFile == refElement) {
                            // need to create a plain text element for the text range and create a reference to the file
                            val startOffset = match.replacedStart
                            val endOffset = match.replacedEnd
                            val plainTextElement = MdPlainTextElementImpl(element, startOffset, endOffset, element.referenceableOffsetInParent)
                            val reference = MdPlainTextElementPsiReference(plainTextElement, TextRange(0, plainTextElement.textLength), refElement)
                            if (!consumer.process(reference)) return false
                        }
                    }
                }
            }

            return true
        }

        @JvmStatic
        fun processReferences(refElement: MdAttributeIdValue, effectiveSearchScope: GlobalSearchScope, consumer: Processor<in PsiReference>): Boolean {
            val file = refElement.containingFile.originalFile as? MdFile ?: return false
            val anchorReferenceId = refElement.anchorReferenceId

            // add in file references via enum references
            if (MdPsiImplUtil.inScope(effectiveSearchScope, file)) {
                val referenceLinks = MdPsiImplUtil.listChildrenOfAnyType(file, false, true, true, MdEnumeratedReferenceIdImpl::class.java)

                for (childElement in referenceLinks) {
                    if (childElement.text == anchorReferenceId) {
                        val reference = childElement.reference ?: continue
                        if (!consumer.process(reference)) return true
                    }
                }

                val links = file.links
                for (link in links) {
                    if (link !is MdImageLink) {
                        val linkRefElement = link.linkRefElement
                        var isLocal = false

                        if (linkRefElement == null || linkRefElement.textLength == 0) {
                            isLocal = true
                        } else if (linkRefElement.reference?.resolve() == file) {
                            isLocal = true
                        }

                        if (isLocal) {
                            val linkAnchorElement = link.linkAnchorElement

                            if (linkAnchorElement != null && linkAnchorElement.text == anchorReferenceId) {
                                val reference = linkAnchorElement.reference ?: continue
                                if (!consumer.process(reference)) return true
                            }
                        }
                    }
                }
            }

            // look for links with anchor references from other files
            if (!processReferences(file, effectiveSearchScope, Processor { t ->
                    val element = t.element
                    if (element is MdLinkRefElement) {
                        val parent1 = element.parent
                        if (parent1 is MdLinkElement<*>) {
                            val linkAnchor = parent1.linkAnchorElement
                            if (linkAnchor != null && linkAnchor.text == anchorReferenceId) {
                                return@Processor consumer.process(linkAnchor.reference)
                            }
                        }
                    }
                    true
                })) return true
            return false
        }
    }
}
