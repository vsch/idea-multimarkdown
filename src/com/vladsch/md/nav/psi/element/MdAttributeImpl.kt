// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes

class MdAttributeImpl(node: ASTNode) : MdCompositeImpl(node), MdAttribute {
    override fun getAttributeName(): String? {
        val nameElement = MdPsiImplUtil.findChildByType(this, MdTypes.ATTRIBUTE_NAME) as MdAttributeName?
            ?: MdPsiImplUtil.findChildByType(this, MdTypes.ATTRIBUTE_NAME_ID) as MdAttributeName?
            ?: MdPsiImplUtil.findChildByType(this, MdTypes.ATTRIBUTE_NAME_CLASS) as MdAttributeName?

        return when (nameElement) {
            is MdAttributeNameImplicitClass -> "class"
            is MdAttributeNameImplicitId -> "id"
            else -> nameElement?.text
        }
    }

    override fun getAttributeValue(): String? {
        return attributeValueElement?.text
    }

    override fun getAttributeNameElement(): MdAttributeName? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.ATTRIBUTE_NAME) as MdAttributeName?
            ?: MdPsiImplUtil.findChildByType(this, MdTypes.ATTRIBUTE_NAME_ID) as MdAttributeName?
            ?: MdPsiImplUtil.findChildByType(this, MdTypes.ATTRIBUTE_NAME_CLASS) as MdAttributeName?
    }

    override fun getAttributeValueElement(): MdAttributeValue? {
        return MdPsiImplUtil.findChildByTypeNoCheck(this, MdTypes.ATTRIBUTE_ID_VALUE) as MdAttributeIdValue?
            ?: MdPsiImplUtil.findChildByTypeNoCheck(this, MdTypes.ATTRIBUTE_VALUE) as MdAttributeValue?
    }

    companion object {
        fun isLegalAttributeName(attributeName: String): Boolean {
            val result = "^(?:[a-zA-Z" + "\u001f" + "_:][a-zA-Z0-9" + "\u001f" + ":._-]*)$"
            return result.toRegex().matches(attributeName)
        }

        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, attributeName: String, attributeValue: String?): String {
            if (attributeValue == null) {
                return attributeName
            } else {
                if (attributeName == "class" && isLegalAttributeName(attributeValue)) {
                    return ".$attributeValue"
                } else if (attributeName == "id" && isLegalAttributeName(attributeValue)) {
                    return "#$attributeValue"
                } else {
                    if (!attributeValue.contains('\"')) {
                        return "$attributeName=\"$attributeValue\""
                    } else if (!attributeValue.contains('\'')) {
                        return "$attributeName='$attributeValue'"
                    } else {
                        // contains both " and ', use " and escape "
                        return "$attributeName=\"${attributeValue.replace("\"", "\\\"")}\""
                    }
                }
            }
        }
    }
}
