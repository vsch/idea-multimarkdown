// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.util

import com.intellij.ide.macro.MacroManager
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.vladsch.md.nav.MdResourceResolverImpl
import com.vladsch.md.nav.editor.api.MdHtmlGeneratorExtension
import com.vladsch.md.nav.editor.util.HtmlPlacement.BODY_SCRIPT
import com.vladsch.md.nav.editor.util.HtmlPlacement.BODY_SCRIPT_INITIALIZATION
import com.vladsch.md.nav.editor.util.HtmlPlacement.HEAD_CSS_LAYOUT
import com.vladsch.md.nav.editor.util.HtmlPlacement.HEAD_CSS_SCHEME
import com.vladsch.md.nav.editor.util.HtmlPlacement.HEAD_SCRIPT
import com.vladsch.md.nav.editor.util.HtmlPlacement.HEAD_SCRIPT_INITIALIZATION
import com.vladsch.md.nav.language.FilePathType
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.util.PathInfo
import com.vladsch.plugin.util.prefixWith
import com.vladsch.plugin.util.startsWith
import com.vladsch.plugin.util.suffixWith
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.StringWriter
import java.util.*

abstract class HtmlResource {
    abstract val providerInfo: HtmlResourceProvider.Info
    abstract fun injectHtmlResource(
        project: Project,
        applicationSettings: MdApplicationSettings,
        renderingProfile: MdRenderingProfile,
        injections: ArrayList<InjectHtmlResource?>,
        forHtmlExport: Boolean,
        dataContext: DataContext
    )

    companion object {
        @JvmStatic
        fun getInjectionUrl(
            project: Project,
            resourceUrl: String?,
            resourcePath: String?,
            renderingProfile: MdRenderingProfile,
            forHtmlExport: Boolean,
            dataContext: DataContext?
        ): String? {
            resourceUrl ?: return null

            for (handler in MdHtmlGeneratorExtension.EXTENSIONS.value) {
                val useResourceUrl = handler.adjustUrl(resourcePath, resourceUrl, project, forHtmlExport, renderingProfile, dataContext)
                if (useResourceUrl != null) return useResourceUrl
            }

            return resourceUrl
        }

        private fun scriptPlacement(isHead: Boolean, isInitialization: Boolean) =
            if (isHead)
                if (isInitialization) HEAD_SCRIPT_INITIALIZATION
                else HEAD_SCRIPT
            else
                if (isInitialization) BODY_SCRIPT_INITIALIZATION
                else BODY_SCRIPT

        private fun cssPlacement(layoutPlacement: Boolean) =
            if (layoutPlacement) HEAD_CSS_LAYOUT
            else HEAD_CSS_SCHEME

        @JvmStatic
        fun injectCSSResourcePath(injections: ArrayList<InjectHtmlResource?>, enabled: Boolean, layoutPlacement: Boolean, isByScript: Boolean, resourcePath: String?, resourceClass: Class<Any>?) {
            injectHtmlCssUrl(injections, enabled, cssPlacement(layoutPlacement), isByScript, CSSResourcePathToUrl(resourcePath, resourceClass))
        }

        @JvmStatic
        fun injectCssUrl(injections: ArrayList<InjectHtmlResource?>, enabled: Boolean, layoutPlacement: Boolean, isByScript: Boolean, resourceURL: String?) {
            injectHtmlCssUrl(injections, enabled, cssPlacement(layoutPlacement), isByScript, resourceURL)
        }

        @JvmStatic
        fun injectCSSText(injections: ArrayList<InjectHtmlResource?>, enabled: Boolean, layoutPlacement: Boolean, isByScript: Boolean, resourceText: String?) {
            injectHtmlText(injections, enabled, cssPlacement(layoutPlacement), isByScript, { wrapCSSText(resourceText) })
        }

        @JvmStatic
        fun injectScriptResourcePath(injections: ArrayList<InjectHtmlResource?>, enabled: Boolean, isHead: Boolean, isInitialization: Boolean, resourcePath: String?, resourceClass: Class<Any>?) {
            injectHtmlScriptUrl(injections, enabled, scriptPlacement(isHead, isInitialization), true, ScriptResourcePathToUrl(resourcePath, resourceClass))
        }

        @JvmStatic
        fun injectScriptUrl(injections: ArrayList<InjectHtmlResource?>, enabled: Boolean, isHead: Boolean, isInitialization: Boolean, resourceUrl: String?) {
            injectHtmlScriptUrl(injections, enabled, scriptPlacement(isHead, isInitialization), true, resourceUrl)
        }

        @JvmStatic
        fun injectScriptText(injections: ArrayList<InjectHtmlResource?>, enabled: Boolean, isHead: Boolean, isInitialization: Boolean, resourceText: String?) {
            injectHtmlText(injections, enabled, scriptPlacement(isHead, isInitialization), true, { wrapScriptText(resourceText) })
        }

        @JvmStatic
        fun injectScriptInitializationHtml(injections: ArrayList<InjectHtmlResource?>, enabled: Boolean, isHead: Boolean, isInitialization: Boolean, resourceText: String?) {
            injectHtmlText(injections, enabled, scriptPlacement(isHead, isInitialization), true, resourceText)
        }

        @JvmStatic
        fun CSSResourcePathToUrl(resourcePath: String?, resourceClass: Class<Any>?): String? {
            if (resourcePath == null || resourcePath.isEmpty()) return null
            return resourceFileUrl(resourcePath, resourceClass)
        }

        //        fun wrapCSSResourcePath(resourcePath: String?, resourceClass: Class<Any>?): String? {
        //            if (resourcePath == null || resourcePath.isEmpty()) return null
        //            val resourceText = resourceFileUrl(resourcePath, resourceClass)
        //            return wrapCSSURL(resourceText)
        //        }
        //
        //        fun wrapCSSURL(resourceURL: String?): String? {
        //            if (resourceURL == null || resourceURL.isEmpty()) return null
        //            return "<link rel=\"stylesheet\" href=\"$resourceURL\">\n"
        //        }

        @JvmStatic
        fun wrapCSSText(resourceText: String?): String? {
            if (resourceText == null || resourceText.isEmpty()) return null
            val eol = if (resourceText.endsWith('\n')) "" else "\n"
            return "<style>\n$resourceText$eol</style>\n"
        }

        @JvmStatic
        fun ScriptResourcePathToUrl(resourcePath: String?, resourceClass: Class<Any>?): String? {
            if (resourcePath == null || resourcePath.isEmpty()) return null
            return resourceFileUrl(resourcePath, resourceClass)
        }

        //        fun wrapScriptResourcePath(resourcePath: String?, resourceClass: Class<Any>?): String? {
        //            if (resourcePath == null || resourcePath.isEmpty()) return null
        //            val resourceText = resourceFileUrl(resourcePath, resourceClass)
        //            return wrapScriptURL(resourceText)
        //        }
        //
        //        fun wrapScriptURL(resourceURL: String?): String? {
        //            if (resourceURL == null || resourceURL.isEmpty()) return null
        //            return "<script src=\"$resourceURL\"></script>\n"
        //        }

        @JvmStatic
        fun wrapScriptText(resourceText: String?): String? {
            if (resourceText == null || resourceText.isEmpty()) return null
            val eol = if (resourceText.endsWith('\n')) "" else "\n"
            return "<script>\n$resourceText$eol</script>\n"
        }

        @JvmStatic
        fun injectHtmlText(injections: ArrayList<InjectHtmlResource?>, enabled: Boolean, htmlPlacement: HtmlPlacement, isByScript: Boolean, htmlText: String?) {
            if (enabled && htmlText != null && !htmlText.isBlank()) injections.add(InjectHtmlText(htmlPlacement, isByScript, htmlText))
        }

        @JvmStatic
        fun injectHtmlCssUrl(injections: ArrayList<InjectHtmlResource?>, enabled: Boolean, htmlPlacement: HtmlPlacement, isByScript: Boolean, resourceURL: String?) {
            if (enabled && resourceURL != null && !resourceURL.isBlank()) injections.add(InjectCssUrl(htmlPlacement, isByScript, resourceURL))
        }

        @JvmStatic
        fun injectHtmlScriptUrl(injections: ArrayList<InjectHtmlResource?>, enabled: Boolean, htmlPlacement: HtmlPlacement, isByScript: Boolean, resourceURL: String?) {
            if (enabled && resourceURL != null && !resourceURL.isBlank()) injections.add(InjectScriptUrl(htmlPlacement, isByScript, resourceURL))
        }

        @JvmStatic
        fun injectHtmlText(injections: ArrayList<InjectHtmlResource?>, enabled: Boolean, htmlPlacement: HtmlPlacement, isByScript: Boolean, htmlTextEval: () -> String?) {
            if (enabled) {
                val htmlText = htmlTextEval()
                if (htmlText != null && !htmlText.isBlank()) injections.add(InjectHtmlText(htmlPlacement, isByScript, htmlText))
            }
        }

        @JvmStatic
        fun resourceFileUrl(resourcePath: String?, resourceClass: Class<Any>?): String {
            if (resourcePath == null) return ""
            return MdResourceResolverImpl.instance.resourceFileURL(resourcePath, resourceClass)
        }

        @JvmStatic
        fun resourceExportedFileUrl(resourcePath: String?, projectDir: String?, parentDir: String, targetDir: String, pathType: Int, newExtension: String?, dataContext: DataContext): String {
            if (resourcePath == null) return ""
            val parentDir1 = FileUtil.toSystemIndependentName(MacroManager.getInstance().expandMacrosInString(parentDir, false, dataContext)
                ?: parentDir).suffixWith('/')
            val targetDir1 = FileUtil.toSystemIndependentName(MacroManager.getInstance().expandMacrosInString(targetDir, false, dataContext)
                ?: targetDir).suffixWith('/')
            val projectDir1 = projectDir ?: parentDir1
            val systemIndependentPath = FileUtil.toSystemIndependentName(resourcePath)
            val fileInfo = PathInfo(systemIndependentPath)
            val fileName = if (newExtension != null) "${fileInfo.fileNameNoExt}$newExtension" else fileInfo.fileNameNoQuery
            val path =
                when (pathType) {
                    FilePathType.FILENAME_ONLY.intValue -> {
                        // add file name only
                        targetDir1
                    }
                    FilePathType.PATH_FROM_PARENT.intValue -> {
                        // add path relative to parent directory or projectDir if not under parent
                        val relativePath = targetDir1 + PathInfo.relativePath((if (systemIndependentPath.startsWith(parentDir1)) parentDir1 else projectDir1).prefixWith("/"), fileInfo.path.prefixWith("/"), false, false)
                        relativePath
                    }
                    else -> {
                        val relativePath = if (systemIndependentPath.startsWith(projectDir1)) targetDir1 + PathInfo.relativePath(projectDir1, fileInfo.path.prefixWith("/"), false, false) else targetDir1
                        relativePath
                    }
                }
            val toExportFile = File(FileUtil.toSystemDependentName(path + fileName)).toURI().toString()
            return toExportFile
        }

        @JvmStatic
        fun resourceUrlContent(resourceURL: String): String? {
            if (!resourceURL.startsWith("file:/")) return null

            try {
                val file = File(resourceURL.substring("file:".length))
                val inputStream = file.inputStream()
                val writer = StringWriter()
                IOUtils.copy(inputStream, writer, "UTF-8")
                inputStream.close()
                return writer.toString()
            } catch (e: Exception) {
                return ""
            }
        }
    }
}

