// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util

import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.io.FileInputStream
import java.security.DigestInputStream
import java.security.MessageDigest

class Md5Utils {
    private val md5 = MessageDigest.getInstance("MD5")

    fun add(virtualFile: VirtualFile) {
        val buffer = ByteArray(16384)
        val md5IS = DigestInputStream(virtualFile.inputStream, md5)
        while (md5IS.read(buffer) > 0);
    }

    fun addAll(vararg virtualFiles: VirtualFile) {
        val buffer = ByteArray(16384)
        for (virtualFile in virtualFiles) {
            val md5IS = DigestInputStream(virtualFile.inputStream, md5)
            while (md5IS.read(buffer) > 0);
        }
    }

    fun add(file: File) {
        val buffer = ByteArray(16384)
        val inputStream = FileInputStream(file)
        val md5IS = DigestInputStream(inputStream, md5)
        while (md5IS.read(buffer) > 0);
    }

    fun addAll(vararg files: File) {
        val buffer = ByteArray(16384)
        for (file in files) {
            val inputStream = FileInputStream(file)
            val md5IS = DigestInputStream(inputStream, md5)
            while (md5IS.read(buffer) > 0);
        }
    }

    fun add(text: CharSequence) {
        val buffer = text.toString().toByteArray()
        md5.update(buffer)
    }

    fun addAll(vararg texts: CharSequence) {
        for (text in texts) {
            val buffer = text.toString().toByteArray()
            md5.update(buffer)
        }
    }

    fun addAll(contents: Collection<Any>) {
        val buffer = ByteArray(16384)

        for (content in contents) {
            when (content) {
                is CharSequence -> {
                    val bytes = content.toString().toByteArray()
                    md5.update(bytes)
                }
                is VirtualFile -> {
                    val md5IS = DigestInputStream(content.inputStream, md5)
                    while (md5IS.read(buffer) > 0);
                }
                is File -> {
                    val inputStream = FileInputStream(content)
                    val md5IS = DigestInputStream(inputStream, md5)
                    while (md5IS.read(buffer) > 0);
                }
            }
        }
    }

    fun getMd5(): String {
        val sb = StringBuffer()
        for (byte in md5.digest()) {
            val toInt = (256 + byte.toInt()) and 255
            val hexString = Integer.toHexString(toInt)
            if (hexString.length < 2) sb.append("0")
            sb.append(hexString)
        }
        return sb.toString().toUpperCase()
    }

    companion object {
        @JvmStatic
        fun md5(vararg virtualFiles: VirtualFile): String {
            val md5 = Md5Utils()
            md5.addAll(*virtualFiles)
            return md5.getMd5()
        }

        @JvmStatic
        fun md5(vararg texts: CharSequence): String {
            val md5 = Md5Utils()
            md5.addAll(*texts)
            return md5.getMd5()
        }

        @JvmStatic
        fun md5(vararg files: File): String {
            val md5 = Md5Utils()
            md5.addAll(*files)
            return md5.getMd5()
        }

        @JvmStatic
        fun md5(texts: Collection<CharSequence>): String {
            val md5 = Md5Utils()
            md5.addAll(texts)
            return md5.getMd5()
        }
    }
}
