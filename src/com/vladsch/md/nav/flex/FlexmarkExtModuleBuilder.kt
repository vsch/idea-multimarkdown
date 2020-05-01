// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex

import com.intellij.ide.util.projectWizard.JavaModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.SettingsStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.*
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.roots.*
import com.intellij.openapi.roots.libraries.LibraryUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.codeStyle.NameUtil
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.vladsch.flexmark.util.misc.DelimitedBuilder
import com.vladsch.md.nav.util.PathInfo
import org.jetbrains.jps.model.java.JavaResourceRootType
import org.jetbrains.jps.model.java.JavaSourceRootType
import org.jetbrains.jps.model.module.JpsModuleSourceRootType
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

class FlexmarkExtModuleBuilder : JavaModuleBuilder(), FlexmarkModuleOptions.Holder {
    private var myModel: ModifiableRootModel? = null

    override fun getOptions(): FlexmarkModuleOptions {
        return FlexmarkModuleLocalSettings.getInstance().options
    }

    override fun setOptions(options: FlexmarkModuleOptions) {
        FlexmarkModuleLocalSettings.getInstance().options = options
    }

    @Throws(ConfigurationException::class)
    override fun setupRootModel(model: ModifiableRootModel) {
        this.myModel = model
        val project = model.project

        var extModule: Module? = null
        var extModuleFile: VirtualFile? = null
        val fileIndex = ProjectRootManager.getInstance(project).fileIndex
        val flexmarkModuleExtensions = FilenameIndex.getFilesByName(project, "ZzzzzzExtension.java", GlobalSearchScope.projectScope(project))

        for (psiFile in flexmarkModuleExtensions) {
            val virtualFile = psiFile.virtualFile
            val module = fileIndex.getModuleForFile(virtualFile)

            if (module != null && module.name == "flexmark-ext-zzzzzz") {
                extModuleFile = virtualFile
                extModule = module
                break
            }
        }

        // need to add copies of sources from plugin module. But that is too freaking hard with the Api Model without knowing
        // how to do it
        // create the module tree from the flexmark-ext-module directory
        if (extModuleFile != null && extModule != null) {
            val moduleFile = extModule.moduleFile
            val moduleDir = moduleFile!!.parent

            // create the new module's directory tree
//            val newModuleFile = model.module.moduleFile
            val newModuleFilePath = model.module.moduleFilePath

            val pathInfo = PathInfo(newModuleFilePath)
//            val projectRelativePath = PathInfo.relativePath(project.basePath!!, pathInfo.path.removeSuffix("/"), false, false)

            var moduleRoot = PathInfo(pathInfo.path).virtualFile
            if (moduleRoot == null) {
                try {
                    moduleRoot = VfsUtil.createDirectoryIfMissing(pathInfo.path)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            if (moduleRoot != null) {
                //model.addContentEntry(moduleRoot)
                for (orderEntry in fileIndex.getOrderEntriesForFile(extModuleFile)) {
                    if (orderEntry is LibraryOrderEntry) {
//                        val library = orderEntry.library ?: continue
                        myModel!!.addOrderEntry(orderEntry)
                    } else if (orderEntry is ModuleOrderEntry) {
                        myModel!!.addOrderEntry(orderEntry)
                    } else {
                        myModel!!.addOrderEntry(orderEntry)
                    }
                }

                // now to copy the contents of moduleDir to moduleRoot
                val moduleRootDir = moduleRoot.path
                val moduleSrcRoot = "$moduleRootDir/src/main/java"
                val moduleTestRoot = "$moduleRootDir/src/test/java"
                val moduleTestResources = "$moduleRootDir/src/test/resources"
                val wantPaths = mapOf<String, JpsModuleSourceRootType<*>>(moduleSrcRoot to JavaSourceRootType.SOURCE, moduleTestRoot to JavaSourceRootType.TEST_SOURCE, moduleTestResources to JavaResourceRootType.TEST_RESOURCE)

                val replaceTemplate = Template("zzzoptionszzz(", ")", "zzzzzz")
                val fileMap = HashMap<String, String>()
                val optionSet = options.optionsSet
                replaceTemplate.optionSet.addAll(optionSet)

                fileMap.put("ZzzzzzBlockParser.java", FlexmarkModuleOptions.BLOCK_PARSER)
                fileMap.put("ZzzzzzBlockPreProcessor.java", FlexmarkModuleOptions.BLOCK_PRE_PROCESSOR)
                fileMap.put("ZzzzzzDelimiterProcessor.java", FlexmarkModuleOptions.DELIMITER_PROCESSOR)
                fileMap.put("ZzzzzzInlineParserExtension.java", FlexmarkModuleOptions.INLINE_PARSER_EXTENSION)
                fileMap.put("ZzzzzzLinkRefProcessor.java", FlexmarkModuleOptions.LINK_REF_PROCESSOR)
                fileMap.put("ZzzzzzLinkResolver.java", FlexmarkModuleOptions.LINK_RESOLVER)
                fileMap.put("ZzzzzzAttributeProvider.java", FlexmarkModuleOptions.ATTRIBUTE_PROVIDER)

                if (options.nodeRenderer) fileMap.put("ZzzzzzNodeRenderer.java", FlexmarkModuleOptions.NODE_RENDERER)
                else fileMap.put("ZzzzzzNodeRenderer.java", FlexmarkModuleOptions.PHASED_NODE_RENDERER)

                // JIRA rendering support
                fileMap.put("ZzzzzzJiraRenderer.java", FlexmarkModuleOptions.JIRA_RENDERER)
                fileMap.put("JiraZzzzzzSpecTest.java", FlexmarkModuleOptions.JIRA_RENDERER)
                fileMap.put("ext_zzzzzz_jira_spec.md", FlexmarkModuleOptions.JIRA_RENDERER)

                fileMap.put("ZzzzzzOptions.java", FlexmarkModuleOptions.CUSTOM_PROPERTIES)
                fileMap.put("ZzzzzzParagraphPreProcessor.java", FlexmarkModuleOptions.PARAGRAPH_PRE_PROCESSOR)
                fileMap.put("ZzzzzzNodePostProcessor.java", FlexmarkModuleOptions.NODE_POST_PROCESSOR)
                fileMap.put("ZzzzzzDocumentPostProcessor.java", FlexmarkModuleOptions.DOCUMENT_POST_PROCESSOR)
                fileMap.put("ZzzzzzRepository.java", FlexmarkModuleOptions.CUSTOM_NODE_REPOSITORY)
                fileMap.put("Zzzzzz.java", FlexmarkModuleOptions.CUSTOM_NODE)
                fileMap.put("ZzzzzzBlock.java", FlexmarkModuleOptions.CUSTOM_BLOCK_NODE)
                fileMap.put("ZzzzzzVisitor.java", if (options.customNode) FlexmarkModuleOptions.CUSTOM_NODE else FlexmarkModuleOptions.CUSTOM_BLOCK_NODE)
                fileMap.put("ZzzzzzVisitorExt.java", if (options.customNode) FlexmarkModuleOptions.CUSTOM_NODE else FlexmarkModuleOptions.CUSTOM_BLOCK_NODE)
                fileMap.put("flexmark-ext-zzzzzz.iml", "REMOVE")
                fileMap.put("AbstractZzzzzzVisitor", "REMOVE")

                replaceTemplate.replaceMap.put(moduleDir.path, moduleRootDir)
                replaceTemplate.replaceMap.put("com.vladsch.flexmark.ext.zzzzzz", options.extensionPackage.trim())
                replaceTemplate.replaceMap.put("com/vladsch/flexmark/ext/zzzzzz", options.extensionPackage.trim().replace(".", "/"))

                val moduleName = options.extensionName.trim()
                val words = NameUtil.nameToWords(moduleName)
                val name = DelimitedBuilder("")

                replaceTemplate.replaceMap.put("flexmark-ext-zzzzzz", name.appendAll("-", words).andClear.toLowerCase())
                replaceTemplate.replaceMap.put("ext_zzzzzz", name.appendAll("_", words).andClear.toLowerCase())
                replaceTemplate.replaceMap.put("zzzzzz", name.appendAll("_", words).andClear.toLowerCase())
                replaceTemplate.replaceMap.put("ZZZZZZ", name.appendAll("_", words).andClear.toUpperCase())
                replaceTemplate.replaceMap.put("Zzzzzz", options.extensionName.trim())

                val files = ArrayList<VirtualFile>()
                getDirContents(moduleDir, files)

                // now create files
                for (file in files) {
                    val fileOption = fileMap[file.name]
                    if (fileOption == null || optionSet.contains(fileOption)) {
                        val toPath = replaceTemplate.replaceText(file.path)
                        val toInfo = PathInfo(toPath)
                        val toDir: VirtualFile
                        try {
                            toDir = VfsUtil.createDirectoryIfMissing(toInfo.path.removeSuffix("/")) ?: continue
                        } catch (e: IOException) {
                            continue
                        }

                        val toFile = PathInfo(toPath).virtualFile ?: toDir.createChildData(this, toInfo.fileName)
                        if (!toFile.isDirectory) {
                            if (file.fileType.isBinary) {
                                toFile.setBinaryContent(file.contentsToByteArray())
                            } else {
                                // we filter and translate these
                                val content = file.contentsToByteArray().toString(Charset.defaultCharset())
                                val result = replaceTemplate.replaceText(content)
                                val filtered = replaceTemplate.filterLines(result)
                                toFile.setBinaryContent(filtered.toByteArray(Charset.defaultCharset()))
                            }
                        }
                    }
                }

                val content = model.addContentEntry(moduleRoot)
                for (entry in wantPaths) {
                    val root = PathInfo(entry.key).virtualFile
                    if (root != null) {
                        content.addSourceFolder(root, entry.value)
                    }
                }

                val modules = ModuleUtil.getModulesOfType(project, JavaModuleType.getModuleType())
                for (module in modules) {
                    if (module.name == "flexmark") {
                        val entry = model.addModuleOrderEntry(module)
                        entry.scope = DependencyScope.COMPILE
                    } else if (module.name == "flexmark-test-util") {
                        val entry = model.addModuleOrderEntry(module)
                        entry.scope = DependencyScope.TEST
                    }
                }

                // copy libraries from extModule
                val libRoots = LibraryUtil.getLibraryRoots(project)
                for (libRoot in libRoots) {
                    val libName = libRoot.name
                    if (libName.endsWith("junit-4.12.jar") || libName.endsWith("junit-4.12.jar") || libName.endsWith("hamcrest-core-1.3.jar")) {
                        val lib = LibraryUtil.findLibraryEntry(libRoot, project)
                        if (lib != null) {
                            if (lib is LibraryOrderEntry) {
                                val library = lib.library
                                if (library != null) {
                                    val libEntry = model.findLibraryOrderEntry(library)
                                    if (libEntry == null) {
                                        model.addOrderEntry(lib)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            model.inheritSdk()

            // if we have a test suite then add this module to its dependencies
            val flexmarkTestSuiteModules = FilenameIndex.getFilesByName(project, "FlexmarkTestSuite.java", GlobalSearchScope.projectScope(project))
            var syncNeeded = true

            for (psiFile in flexmarkTestSuiteModules) {
                val virtualFile = psiFile.virtualFile
                val module = fileIndex.getModuleForFile(virtualFile)
                if (module != null) {
                    if (module.name == "flexmark-test-suite") {
                        ApplicationManager.getApplication().invokeLater {
                            ApplicationManager.getApplication().runWriteAction {
                                val modifiableModel = ModuleRootManager.getInstance(module).modifiableModel
                                modifiableModel.addModuleOrderEntry(model.module)
                                modifiableModel.commit()
                                VirtualFileManager.getInstance().syncRefresh()
                            }
                        }
                        syncNeeded = false
                        break
                    }
                }
            }

            if (syncNeeded) {
                ApplicationManager.getApplication().invokeLater {
                    VirtualFileManager.getInstance().syncRefresh()
                }
            }

            // save module options, just in case we decide to add configuration based addition of new module files
            val buildConfiguration = FlexmarkExtModuleBuildConfiguration.getInstance(model.module)
                ?: FlexmarkExtModuleBuildConfiguration(model.module)
            buildConfiguration.setOptions(options)
        }
    }

    fun getDirContents(fromDir: VirtualFile, files: MutableList<VirtualFile>) {
        for (child in fromDir.children) {
            if (child.isDirectory) {
                getDirContents(child, files)
            } else {
                files.add(child)
            }
        }
    }

    override fun getParentGroup(): String {
        return JavaModuleType.JAVA_GROUP
    }

    override fun getModuleType(): ModuleType<JavaModuleBuilder> {
        return FlexmarkExtModuleType.getInstance()
    }

    override fun modifyProjectTypeStep(settingsStep: SettingsStep): ModuleWizardStep? {
        return StdModuleTypes.JAVA.modifyProjectTypeStep(settingsStep, this)
    }

    override fun getCustomOptionsStep(context: WizardContext?, parentDisposable: Disposable?): ModuleWizardStep? {
        return FlexmarkExtModuleSettingsForm(context, parentDisposable, this)
        //        return null;
    }
}
