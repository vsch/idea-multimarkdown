// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.util

import com.intellij.util.containers.Stack
import com.vladsch.plugin.util.splice
import com.vladsch.plugin.util.toRgbString
import javafx.scene.text.FontWeight
import java.awt.Color
import java.awt.Font
import java.io.Serializable

class HtmlStringBuilder(initialSize: Int = 1024) : Appendable, CharSequence, Serializable {
    private val out = StringBuilder(initialSize)
    private val openTags = Stack<String>()

    private fun tagStack(): String {
        return openTags.splice(", ")
    }

    private fun pushTag(tagName: String) {
        openTags.push(tagName)
    }

    private fun popTag(tagName: String) {
        if (openTags.isEmpty()) throw IllegalStateException("Close tag '$tagName' with no tags open")
        val openTag = openTags.peek()
        if (openTag != tagName) throw IllegalStateException("Close tag '$tagName' does not match '$openTag' in " + tagStack())
        openTags.pop()
    }

    fun toHtml(): String {
        return out.toString()
    }

    @JvmOverloads
    fun span(text: String, foreground: Color? = null, background: Color? = null, fontStyle: Int? = null, fontWeight: FontWeight? = null, font: Font? = null): HtmlStringBuilder {
        return span(foreground, background, fontStyle, fontWeight, font, { text })
    }

    @JvmOverloads
    fun span(foreground: Color? = null, background: Color? = null, fontStyle: Int? = null, fontWeight: FontWeight? = null, font: Font? = null, content: (() -> String)?): HtmlStringBuilder {
        @Suppress("NAME_SHADOWING")
        var fontStyle = fontStyle

        out.append("<span")
        if (font != null || foreground != null || background != null || fontStyle != null && fontStyle != 0 || fontWeight != null) {
            out.append(" style='")

            if (font != null) {
                out.append("font-family:").append(font.fontName).append(";").append("font-size:").append(font.size).append("pt;")
                if (fontStyle == null) fontStyle = font.style
            }

            if (foreground != null) out.append("color:").append(foreground.toRgbString()).append(";")
            if (background != null) out.append("background-color:").append(background.toRgbString()).append(";")
            if (fontStyle != null && fontStyle != 0) {
                out.append("font-style:")
                if (fontStyle and Font.ITALIC != 0) out.append("italic;")
            }
            if (fontWeight != null || (fontStyle != null && fontStyle and Font.BOLD != 0)) {
                out.append("font-weight:").append((fontWeight ?: FontWeight.BOLD).weight).append(";")
            }
            out.append("'")
        }

        out.append(">")

        if (content != null) {
            out.append(content())
            out.append("</span>")
        } else {
            pushTag("span")
        }

        return this
    }

    fun closeSpan(): HtmlStringBuilder {
        popTag("span")
        out.append("</span>")
        return this
    }

    private fun rawTag(tagName: String, attributes: String = ""): HtmlStringBuilder {
        out.append('<').append(tagName)
        if (attributes.isNotBlank()) out.append(' ').append(attributes.trim())
        out.append('>')
        return this
    }

    @JvmOverloads
    fun tag(tagName: String, attributes: String = ""): HtmlStringBuilder {
        if (tagName[0] == '/') throw IllegalStateException("Open tag called with $tagName")
        rawTag(tagName, attributes)
        pushTag(tagName)
        return this
    }

    fun closeTag(tagName: String): HtmlStringBuilder {
        if (tagName[0] == '/') {
            rawTag(tagName)
            popTag(tagName.substring(1))
        } else {
            rawTag("/$tagName")
            popTag(tagName)
        }
        return this
    }

    @JvmOverloads
    fun tag(tagName: String, attributes: String = "", content: (() -> String)?): HtmlStringBuilder {
        rawTag(tagName, attributes)
        if (content != null) {
            out.append(content())
            rawTag("/$tagName")
        } else {
            pushTag(tagName)
        }
        return this
    }

    // @formatter:off
    override fun append(c:Char):HtmlStringBuilder                                                        { out.append(c);                           return this }
    override fun append(s:CharSequence):HtmlStringBuilder                                                { out.append(s);                           return this }
    override fun append(s:CharSequence, start:Int, end:Int):HtmlStringBuilder                            { out.append(s, start, end);               return this }
    fun append(b:Boolean):HtmlStringBuilder                                                              { out.append(b);                           return this }
    fun append(str:CharArray):HtmlStringBuilder                                                          { out.append(str);                         return this }
    fun append(str:CharArray, offset:Int, len:Int):HtmlStringBuilder                                     { out.append(str, offset, len);            return this }
    fun append(d:Double):HtmlStringBuilder                                                               { out.append(d);                           return this }
    fun append(f:Float):HtmlStringBuilder                                                                { out.append(f);                           return this }
    fun append(i:Int):HtmlStringBuilder                                                                  { out.append(i);                           return this }
    fun append(lng:Long):HtmlStringBuilder                                                               { out.append(lng);                         return this }
    fun append(obj:Any):HtmlStringBuilder                                                                { out.append(obj);                         return this }
    fun append(str:String):HtmlStringBuilder                                                             { out.append(str);                         return this }
    fun append(sb:StringBuffer):HtmlStringBuilder                                                        { out.append(sb);                          return this }
    fun appendCodePoint(codePoint:Int):HtmlStringBuilder                                                 { out.appendCodePoint(codePoint);          return this }
    fun delete(start:Int, end:Int):HtmlStringBuilder                                                     { out.delete(start, end);                  return this }
    fun deleteCharAt(index:Int):HtmlStringBuilder                                                        { out.deleteCharAt(index);                 return this }
    fun insert(dstOffset:Int, s:CharSequence):HtmlStringBuilder                                          { out.insert(dstOffset, s);                return this }
    fun insert(dstOffset:Int, s:CharSequence, start:Int, end:Int):HtmlStringBuilder                      { out.insert(dstOffset, s, start, end);    return this }
    fun insert(index:Int, str:CharArray, offset:Int, len:Int):HtmlStringBuilder                          { out.insert(index, str, offset, len);     return this }
    fun insert(offset:Int, b:Boolean):HtmlStringBuilder                                                  { out.insert(offset, b);                   return this }
    fun insert(offset:Int, c:Char):HtmlStringBuilder                                                     { out.insert(offset, c);                   return this }
    fun insert(offset:Int, str:CharArray):HtmlStringBuilder                                              { out.insert(offset, str);                 return this }
    fun insert(offset:Int, d:Double):HtmlStringBuilder                                                   { out.insert(offset, d);                   return this }
    fun insert(offset:Int, f:Float):HtmlStringBuilder                                                    { out.insert(offset, f);                   return this }
    fun insert(offset:Int, i:Int):HtmlStringBuilder                                                      { out.insert(offset, i);                   return this }
    fun insert(offset:Int, l:Long):HtmlStringBuilder                                                     { out.insert(offset, l);                   return this }
    fun insert(offset:Int, obj:Any):HtmlStringBuilder                                                    { out.insert(offset, obj);                 return this }
    fun insert(offset:Int, str:String):HtmlStringBuilder                                                 { out.insert(offset, str);                 return this }
    fun replace(start:Int, end:Int, str:String):HtmlStringBuilder                                        { out.replace(start, end, str);            return this }
    fun reverse():HtmlStringBuilder                                                                      { out.reverse();                           return this }

    override val length: Int get()                                                                       { return out.length }
    override fun get(index: Int): Char                                                                   { return out[index] }
    override fun subSequence(startIndex:Int, endIndex:Int):CharSequence                                  { return out.subSequence(startIndex, endIndex) }
    fun capacity():Int                                                                                   { return out.capacity() }
    fun codePointAt(index:Int):Int                                                                       { return out.codePointAt(index) }
    fun codePointBefore(index:Int):Int                                                                   { return out.codePointBefore(index) }
    fun codePointCount(beginIndex:Int, endIndex:Int):Int                                                 { return out.codePointCount(beginIndex, endIndex) }
    fun indexOf(str:String):Int                                                                          { return out.indexOf(str) }
    fun indexOf(str:String, fromIndex:Int):Int                                                           { return out.indexOf(str, fromIndex) }
    fun lastIndexOf(str:String):Int                                                                      { return out.lastIndexOf(str) }
    fun lastIndexOf(str:String, fromIndex:Int):Int                                                       { return out.lastIndexOf(str, fromIndex) }
    fun offsetByCodePoints(index:Int, codePointOffset:Int):Int                                           { return out.offsetByCodePoints(index, codePointOffset) }
    fun substring(start:Int):String                                                                      { return out.substring(start) }
    fun substring(start:Int, end:Int):String                                                             { return out.substring(start, end) }
    fun ensureCapacity(minimumCapacity:Int)                                                              { out.ensureCapacity(minimumCapacity) }
    fun getChars(srcBegin:Int, srcEnd:Int, dst:CharArray, dstBegin:Int)                                  { out.getChars(srcBegin, srcEnd, dst, dstBegin) }
    fun setCharAt(index:Int, ch:Char)                                                                    { out.setCharAt(index, ch) }
    fun setLength(newLength:Int)                                                                         { out.setLength(newLength) }
    fun trimToSize()                                                                                     { out.trimToSize() }
 // @formatter:on

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HtmlStringBuilder) return false

        if (out != other.out) return false

        return true
    }

    override fun hashCode(): Int {
        return out.hashCode()
    }
}
