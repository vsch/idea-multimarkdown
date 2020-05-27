// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.stubs.IStubElementType
import com.intellij.util.IncorrectOperationException
import com.vladsch.flexmark.test.util.ExampleOption
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.element.MdElementItemPresentation
import com.vladsch.md.nav.psi.element.MdNamedElement
import com.vladsch.md.nav.psi.element.MdStructureViewPresentableElement
import com.vladsch.md.nav.psi.element.MdStructureViewPresentableItem
import com.vladsch.md.nav.psi.util.MdElementFactory
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.plugin.util.nullIf
import icons.FlexmarkIcons
import java.util.*
import javax.swing.Icon

class FlexmarkExampleOptionImpl(stub: FlexmarkExampleOptionStub?, nodeType: IStubElementType<FlexmarkExampleOptionStub, FlexmarkExampleOption>?, node: ASTNode?) :
    FlexmarkStubElementImpl<FlexmarkExampleOptionStub>(stub, nodeType, node),
    FlexmarkExampleOption,
    MdStructureViewPresentableElement,
    MdStructureViewPresentableItem {

    constructor(stub: FlexmarkExampleOptionStub, nodeType: IStubElementType<FlexmarkExampleOptionStub, FlexmarkExampleOption>) : this(stub, nodeType, null)

    constructor(node: ASTNode) : this(null, null, node)

    override fun getNameIdentifier(): PsiElement? {
        val nameElement = findChildByType<PsiElement>(MdTokenSets.FLEXMARK_EXAMPLE_OPTION_NAME_OR_DISABLED_SET)
        return nameElement?.nullIf(nameElement.node.findChildByType(MdTokenSets.FLEXMARK_EXAMPLE_OPTION_NAME_OR_DISABLED_SET) == null)
    }

    override fun isRenameAvailable(): Boolean {
        return nameIdentifier != null
    }

    override fun setName(newName: String): FlexmarkExampleOption {
        // preserve params and disabled status
        return setName(newName, MdNamedElement.REASON_FILE_RENAMED)
    }

    override fun handleContentChange(newContent: String): FlexmarkExampleOption {
        val parent = parent as FlexmarkExampleOptions
        val optionElements = parent.optionElements
        val options = ArrayList<String>()
        var self = 0
        for ((i, element) in optionElements.withIndex()) {
            if (element === this) {
                val useContent = optionInfo.getOptionText(newContent)
                options.add(useContent)
                self = i
            } else {
                val name = element.optionName
                options.add(name)
            }
        }

        val example: FlexmarkExample = parent.parent as FlexmarkExample
        val factoryContext = MdFactoryContext(this)
        val newExample = MdElementFactory.createFlexmarkExample(factoryContext, FlexmarkExampleParams(example).withOptions(options))
            ?: return this
        val contentNodes = newExample.optionsList?.optionElements ?: return this
        if (self < contentNodes.size) {
            replace(contentNodes[self])
            return contentNodes[self]
        }
        return this
    }

    override fun isInplaceRenameAvailable(context: PsiElement?): Boolean {
        return true
        //        val elementType = context?.node?.elementType
        //        return isRenameAvailable && (context is FlexmarkExampleOption)
    }

    override fun isMemberInplaceRenameAvailable(context: PsiElement?): Boolean {
        return true
        //        val elementType = context?.node?.elementType
        //        return isRenameAvailable && (context is FlexmarkExampleOption)
    }

    @Throws(IncorrectOperationException::class)
    override fun handleContentChange(range: TextRange, newContent: String): FlexmarkExampleOption {
        val optionRange = optionInfo.optionNameRange
        if (!range.equalsToRange(optionRange.startOffset, optionRange.endOffset)) {
            throw IncorrectOperationException()
        }
        return handleContentChange(newContent)
    }

    override fun createReference(textRange: TextRange, exactReference: Boolean): FlexmarkPsiReferenceExampleOption {
        return FlexmarkPsiReferenceExampleOption(this, textRange, exactReference)
    }

    override fun getReference(): FlexmarkPsiReferenceExampleOption {
        val optionRange = optionInfo.optionNameRange
        return createReference(optionRange, false)
    }

    override fun getExactReference(): FlexmarkPsiReferenceExampleOption {
        val optionRange = optionInfo.optionNameRange
        return createReference(optionRange, true)
    }

    override fun getOptionParams(): String? {
        val exampleOption = ExampleOption.of(text)
        return exampleOption.getCustomParams()
    }

    override fun getOptionName(): String {
        val stub = stub
        if (stub != null) {
            return stub.optionName
        }

        val exampleOption = ExampleOption.of(text)
        return exampleOption.getOptionName()
    }

    override fun getDisplayName(): String {
        return optionName
    }

    override fun getLocationString(): String? {
        return null
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        val grandParent = parent.parent
        if (grandParent is FlexmarkExampleImpl) {
            return MdElementItemPresentation(this, grandParent.getIcon(0) ?: FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE)
        }
        return MdElementItemPresentation(this)
    }

    override fun getPresentation(): ItemPresentation {
        return structureViewPresentation
    }

    override fun getPresentableText(): String? {
        val grandParent = parent.parent
        if (grandParent is FlexmarkExampleImpl) {
            return "options(" + (parent as FlexmarkExampleOptions).optionsString + ") " + containingFile.name + " " + grandParent.presentableText
        }
        return null
    }

    override fun getIcon(flags: Int): Icon? {
        val grandParent = parent.parent
        if (grandParent is FlexmarkExampleImpl) {
            return grandParent.getIcon(flags)
        }
        val flexmarkOption = FlexmarkPsi.getBuiltInFlexmarkOption(optionName)
        return if (flexmarkOption != null) flexmarkOption.icon
        else FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE
    }

    override fun isIgnore(): Boolean {
        return optionInfo.isIgnore
    }

    override fun isFail(): Boolean {
        return optionInfo.isFail
    }

    override fun isBuiltIn(): Boolean {
        return optionInfo.isBuiltIn
    }

    override fun isDisabled(): Boolean {
        return optionInfo.isDisabled
    }

    override fun getOptionInfo(): FlexmarkOptionInfo {
        return FlexmarkPsi.getFlexmarkOptionInfo(text)
    }

    override fun getName(): String {
        return optionName
    }

    override fun setName(newName: String, reason: Int): FlexmarkExampleOption {
        return handleContentChange(newName)
    }

    override fun toString(): String {
        return "FLEXMARK_EXAMPLE_OPTION '" + optionName + "' " + super.hashCode()
    }

    override fun accept(visitor: PsiElementVisitor) {
        //        if (visitor is MdPsiVisitor)
        //            visitor.visitNamedElement(this)
        //        else
        super.accept(visitor)
    }
}
