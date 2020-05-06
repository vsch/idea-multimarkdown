// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdElementFactory

class MdAttributesImpl(node: ASTNode) : MdCompositeImpl(node), MdAttributes {
    override fun getIdValueAttribute(): MdAttributeIdValue? {
        for (attribute in children) {
            if (attribute is MdAttribute) {
                val attributeName = attribute.attributeName ?: continue
                if (attributeName == "id") {
                    return attribute.attributeValueElement as MdAttributeIdValue?
                }
            }
        }

        return null
    }

    override fun setAttributes(attributes: Map<String, String?>): MdAttributes {
        val attributesText = getAttributesText(project, attributes)
        val newAttributes = MdElementFactory.createAttributes(MdFactoryContext(this), attributesText)
        if (newAttributes != null) {
            return replace(newAttributes) as MdAttributes
        }
        return this
    }

    override fun getAttributes(): Map<String, String?> {
        val attributes = HashMap<String, String?>()

        for (attribute in children) {
            if (attribute is MdAttribute) {
                val attributeName = attribute.attributeName ?: continue
                attributes[attributeName] = attribute.attributeValue
            }
        }

        return attributes
    }

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, attributesText: String): String {
            return "{$attributesText}"
        }

        @Suppress("UNUSED_PARAMETER")
        fun getAttributesText(project: Project, attributes: Map<String, String?>): String {
            val attributesText = StringBuilder()
            for (entry in attributes) {
                if (attributesText.isNotEmpty()) attributesText.append(' ')
                var isBuiltIn = false
                when (entry.key) {
                    "id" -> {
                        attributesText.append('#')
                        isBuiltIn = true
                    }
                    "class" -> {
                        attributesText.append('.')
                        isBuiltIn = true
                    }
                    else -> attributesText.append(entry.key)
                }

                if (entry.value != null) {
                    if (!isBuiltIn) {
                        attributesText.append('=')
                        if (!entry.value!!.contains('"')) {
                            attributesText.append('"').append(entry.value).append('"')
                        } else if (!entry.value!!.contains('\'')) {
                            attributesText.append('\'').append(entry.value).append('\'')
                        } else {
                            attributesText.append('"').append(entry.value!!.replace("\"", "\\\"")).append('"')
                        }
                    } else {
                        attributesText.append(entry.value)
                    }
                }
            }
            return attributesText.toString()
        }
    }
}
