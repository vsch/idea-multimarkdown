// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.structure

import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModel.ElementInfoProvider
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.flex.psi.FlexmarkExample
import com.vladsch.md.nav.psi.element.*
import com.vladsch.md.nav.psi.util.MdTypes

class MdStructureViewModel(psiFile: MdFile, editor: Editor?, root: MdStructureViewFile)
    : StructureViewModelBase(psiFile, editor, root), ElementInfoProvider, StructureViewModel.ExpandInfoProvider {

    override fun getRoot(): MdStructureViewFile {
        return super.getRoot() as MdStructureViewFile
    }

    override fun isAutoExpand(element: StructureViewTreeElement): Boolean {
        return false
    }

    override fun isSmartExpand(): Boolean {
        return false
    }

    /**
     * Returns the list of PSI element classes which are shown as structure view elements. When determining the current editor element, the PSI tree is walked up until an element matching one of these classes is found.
     *
     * @return the list of classes
     */
    override fun getSuitableClasses(): Array<Class<*>> {
        return arrayOf(MdAtxHeader::class.java, MdAutoLink::class.java, MdMacro::class.java, MdDefinitionListImpl::class.java, MdDefinitionTermImpl::class.java, MdList::class.java, MdExplicitLink::class.java, MdReferenceImage::class.java, MdEnumeratedReferenceFormat::class.java, MdListItem::class.java, MdImageLink::class.java, MdHeaderElement::class.java, MdFootnote::class.java, FlexmarkExample::class.java, MdSetextHeader::class.java)
    }

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean {
        return false
    }

    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean {
        return false
    }

    override fun getCurrentEditorElement(): Any? {
        return super.getCurrentEditorElement()
    }

    override fun findAcceptableElement(element: PsiElement?): Any? {
        var useElement = element
        while (useElement?.node?.elementType == MdTypes.EOL) useElement = useElement?.prevSibling
        return super.findAcceptableElement(useElement)
    }
}
