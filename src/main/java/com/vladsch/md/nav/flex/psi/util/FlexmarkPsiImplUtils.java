// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.index.JavaFieldNameIndex;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.vladsch.flexmark.util.misc.Pair;
import com.vladsch.flexmark.util.misc.Ref;
import com.vladsch.md.nav.flex.parser.FlexmarkProjectCachedData;
import com.vladsch.md.nav.flex.parser.FlexmarkSpecTestCaseCachedData;
import com.vladsch.md.nav.flex.psi.FlexmarkExampleOption;
import com.vladsch.md.nav.flex.psi.FlexmarkExampleOptionDefinition;
import com.vladsch.md.nav.flex.psi.index.FlexmarkExampleOptionIndex;
import com.vladsch.md.nav.flex.psi.index.FlexmarkFileIndex;
import com.vladsch.md.nav.parser.cache.MdCachedFileElements;
import com.vladsch.md.nav.parser.cache.PsiClassProcessor;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.util.NotNullBiConsumer;
import com.vladsch.md.nav.util.NotNullBiFunction;
import com.vladsch.md.nav.util.NotNullBiPredicate;
import com.vladsch.md.nav.util.PathInfo;
import com.vladsch.md.nav.util.PsiMap;
import com.vladsch.md.nav.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaResourceRootType;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.jetbrains.jps.model.module.JpsModuleSourceRootType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.vladsch.flexmark.util.misc.Utils.prefixWith;
import static com.vladsch.flexmark.util.misc.Utils.suffixWith;

public class FlexmarkPsiImplUtils {
    public static final String SPEC_RESOURCE = "SPEC_RESOURCE";
    public static final Set<JpsModuleSourceRootType<?>> SOURCE_RESOURCE_ROOT_TYPES = new HashSet<>(Arrays.asList(JavaResourceRootType.RESOURCE, JavaSourceRootType.SOURCE));
    public static final Set<JpsModuleSourceRootType<?>> TEST_SOURCE_RESOURCE_ROOT_TYPES = new HashSet<>(Arrays.asList(JavaResourceRootType.TEST_RESOURCE, JavaSourceRootType.TEST_SOURCE));
    public static final Set<JpsModuleSourceRootType<?>> SOURCE_TEST_SOURCE_ROOT_TYPES = new HashSet<>(Arrays.asList(JavaSourceRootType.SOURCE, JavaSourceRootType.TEST_SOURCE));
    public static final Set<JpsModuleSourceRootType<?>> RESOURCE_TEST_RESOURCE_ROOT_TYPES = new HashSet<>(Arrays.asList(JavaResourceRootType.TEST_RESOURCE, JavaResourceRootType.RESOURCE));

    /**
     * Find specific spec example option used in the project
     *
     * @param project    project
     * @param optionName option name to search
     * @param filePath   spec resource file's absolute path or null if options in all files are desired
     *
     * @return collection of all spec example options
     */
    @NotNull
    public static Collection<FlexmarkExampleOption> findSpecExampleOptions(
            @NotNull Project project,
            @Nullable String optionName,
            @Nullable String filePath
    ) {
        GlobalSearchScope searchScope;

        if (filePath != null) {
            VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(PathInfo.prefixWithFileURI(filePath));
            if (virtualFile == null) return Collections.emptyList();
            searchScope = GlobalSearchScope.fileScope(project, virtualFile);
        } else {
            searchScope = GlobalSearchScope.projectScope(project);
        }

        if (optionName == null) {
            Collection<String> keys = FlexmarkExampleOptionIndex.getInstance().getAllKeys(project);
            ArrayList<FlexmarkExampleOption> list = new ArrayList<>();
            for (String key : keys) {
                list.addAll(FlexmarkExampleOptionIndex.getInstance().get(key, project, searchScope));
            }
            return list;
        } else {
            return FlexmarkExampleOptionIndex.getInstance().get(optionName, project, searchScope);
        }
    }

    @NotNull
    private static <T> Result<T> processSpecResourceModuleRoots(@NotNull SpecFileInfo specInfo, @NotNull Function<VirtualFile, Result<T>> processor) {
        // NOTE: find file in one of the content roots of the module where it is located
        Module resourceFileModule = ModuleUtilCore.findModuleForFile(specInfo.mdFile);
        if (resourceFileModule == null || !resourceFileModule.isLoaded()) return Result.CONTINUE();

        // if it is the same module then it is already tested by class path in caller, if it is in another module, then only source roots or source resource roots apply
        VirtualFile fileContentRoot = specInfo.info.projectFileIndex.getContentRootForFile(specInfo.mdVirtualFile);
        if (fileContentRoot == null) return Result.CONTINUE();

        List<VirtualFile> roots = ModuleRootManager.getInstance(resourceFileModule).getSourceRoots(resourceFileModule != specInfo.info.psiClassModule ? SOURCE_RESOURCE_ROOT_TYPES : TEST_SOURCE_RESOURCE_ROOT_TYPES);

        for (VirtualFile root : roots) {
            String rootPath = suffixWith(root.getPath(), '/');
            if (specInfo.mdVirtualFile.getPath().startsWith(rootPath)) {
                Result<T> result = processor.apply(root);
                if (result.isStop()) return result;
            }
        }
        return Result.CONTINUE();
    }

    /**
     * Executes processor for every spec file which can be loaded as resource using given text as resource path for the given psiClass
     * <p>
     * FIX: should use ResourceLocation() to figure out which class loader is used to load the resource for determining accessibility of the resource
     *
     * @param psiClass         psi class where spec resource is defined
     * @param specResourceText text for the spec resource path, null or empty for all files
     * @param processor        processor taking MdFileSpecResourceInfo, VirtualFile root directory containing the spec resource, return null to continue, non-null result to terminate search and return result
     *
     * @return first non-null result from predicate/consumer
     */
    @NotNull
    private static <T> Result<T> processSpecFilesInfo(@Nullable PsiClass psiClass, @Nullable String specResourceText, @NotNull SpecInfoProcessor<T> processor) {
        if (psiClass == null) return Result.CONTINUE();

        // look for MdFiles with the name that class can resolve using ResourceLocation and having subType == FLEXMARK_SUBTYPE
        PsiClassInfo info = PsiClassInfo.getOrNull(psiClass, specResourceText == null ? "" : specResourceText);
        if (info == null) return Result.CONTINUE();

        List<MdFile> mdFiles = FlexmarkFileIndex.findFlexmarkFiles(info.project, info.specResourceText.isEmpty() ? null : info.specResourceText);

        for (MdFile mdFile : mdFiles) {
            VirtualFile virtualFile = mdFile.getVirtualFile();

            if (info.psiClassScope.contains(virtualFile)) {
                SpecFileInfo specInfo = SpecFileInfo.get(info, mdFile);

                // NOTE: file can resolve with fully qualified resource path on class content root or file's content root
                //   try class first
                String fileClassPath = specResourceText == null ? specInfo.info.psiClassContentRootRelativePath : specInfo.info.psiClassContentRootRelativePath + specInfo.info.resolvedSpecResourcePath;

                if (specResourceText == null ? specInfo.mdVirtualFile.getPath().startsWith(suffixWith(fileClassPath, '/'))
                        : fileClassPath.equals(specInfo.mdVirtualFile.getPath())
                ) {
                    Result<T> result = processor.apply(specInfo, info.psiClassContentRoot);
                    if (result.isStop()) return result;
                }

                // NOTE: test file as being in one of the content roots of the module where it is located
                Result<T> result = processSpecResourceModuleRoots(specInfo, root -> {
                    // NOTE: if no spec resource text then any markdown resource is matched, otherwise resolved spec resource path must match
                    if (specResourceText == null || (root.getPath() + specInfo.info.resolvedSpecResourcePath).equals(specInfo.mdVirtualFile.getPath())) {
                        return processor.apply(specInfo, root);
                    }

                    return Result.CONTINUE();
                });

                if (result.isStop()) return result;
            }
        }

        return Result.CONTINUE();
    }

    /**
     * Invoke processor for all spec files reachable from the class and provide MdFile spec file and specResource path string for the resource which the class can use to load as resource
     *
     * @param psiClass         psi class where spec resource is defined
     * @param specResourceText spec resource text or empty or null for all spec files
     * @param processor        processor taking MdFile and spec resource path, return null to continue looking for match, non-null result to terminate search and return result
     *
     * @return first non-null result returned by processor or null if never called or all returned null
     */
    @NotNull
    private static <T> Result<T> processSpecFiles(@Nullable PsiClass psiClass, @Nullable String specResourceText, @NotNull SpecFileProcessor<T> processor) {
        // look for MdFiles with the name that class can resolve using ResourceLocation and having subType == FLEXMARK_SUBTYPE
        return processSpecFilesInfo(psiClass, specResourceText, (specInfo, root) -> {
            PsiClassInfo info = specInfo.info;
            String virtualFilePath = specInfo.mdVirtualFile.getPath();
            String resourcePath;

            if (virtualFilePath.startsWith(suffixWith(info.psiClassContentRootRelativePath, '/'))) {
                // can be relative to class path
                resourcePath = prefixWith(virtualFilePath.substring(info.psiClassContentRootRelativePath.length()), '/');
            } else {
                // need to get the directory in the fileContentRoot which is marked as test resources root or resources root, the effing content root is on the parent
                String contentDirPath = suffixWith(root.getPath(), '/');
                assert specInfo.mdVirtualFile.getPath().startsWith(contentDirPath);
                resourcePath = prefixWith(virtualFilePath.substring(contentDirPath.length()), '/');
            }

            String specResourcePath = (resourcePath.startsWith(info.psiQualifiedSuffixedPath)) ? resourcePath.substring(info.psiQualifiedSuffixedPath.length()) : resourcePath;
            return processor.apply(specInfo.mdFile, specResourcePath);
        });
    }

    /**
     * Invoke processor for all spec files reachable from the class and provide MdFile spec file and specResource path string for the resource which the class can use to load as resource
     *
     * @param psiClass         psi class where spec resource is defined
     * @param specResourceText spec resource text or empty or null for all spec files
     * @param filter           predicate returning true for entries to include in the map, null for all
     *
     * @return a map of all spec files in the project reachable with specResource text, PsiKeyMap is returned
     */
    @NotNull
    public static PsiMap<MdFile, String> getSpecFiles(@Nullable PsiClass psiClass, @Nullable String specResourceText, @Nullable NotNullBiPredicate<MdFile, String> filter) {
        PsiMap<MdFile, String> specFileMap = new PsiMap<>(HashMap::new);

        processSpecFiles(psiClass, specResourceText, (mdFile, specResourcePath) -> {
            if (!specFileMap.containsKey(mdFile) && (filter == null || filter.test(mdFile, specResourcePath))) {
                specFileMap.put(mdFile, specResourcePath);
            }

            return Result.CONTINUE();
        });

        return specFileMap;
    }

    /**
     * Invoke processor for all spec files reachable from the class and provide MdFile spec file and specResource path string for the resource which the class can use to load as resource
     *
     * @param psiClass         psi class where spec resource is defined
     * @param specResourceText spec resource text or empty or null for all spec files
     * @param filter           predicate returning true for entries to include in the map
     *
     * @return first pair of spec file and specResourcePath for which predicate tested true or null if none were found
     */
    @Nullable
    public static Pair<MdFile, String> getFirstSpecFile(@Nullable PsiClass psiClass, @Nullable String specResourceText, @Nullable NotNullBiPredicate<MdFile, String> filter) {
        Result<Pair<MdFile, String>> result = processSpecFiles(psiClass, specResourceText, (mdFile, specResourcePath) -> {
            if (filter == null || filter.test(mdFile, specResourcePath)) {
                return Result.RETURN(Pair.of(mdFile, specResourcePath));
            }
            return Result.CONTINUE();
        });

        return result.getOrNull();
    }

    private static int getSuperOrInterface(@NotNull PsiClass element, @NotNull Predicate<PsiClass> predicate) {
        int level = -1;
        for (PsiClass superClass : element.getSupers()) {
            if (predicate.test(superClass)) {
                level = Math.max(level, 0);
            } else {
                int superLevel = getSuperOrInterface(superClass, predicate);
                if (superLevel >= 0) level = Math.max(superLevel + 1, level);
            }
        }
        return level;
    }

    /**
     * Test whether a psiClass is a rendering test case class
     *
     * @param psiClass class to test
     *
     * @return true if it is a spec test case class
     */
    public static int renderingTestCaseSuperLevel(@NotNull PsiClass psiClass) {
        return getSuperOrInterface(psiClass, FlexmarkPsiImplUtils::isRenderingTestCaseSuper);
    }

    /**
     * Test whether given psiClass is named RenderingTestCase or SpecExampleProcessor, marking it as a test case using spec file resource and defining spec example options
     *
     * @param psiClass PsiClass to test
     *
     * @return true if it is a rendering test case super
     */
    private static boolean isRenderingTestCaseSuper(@NotNull PsiClass psiClass) {
        String qualifiedName = psiClass.getQualifiedName();
        String name = psiClass.getName();
        if (qualifiedName == null || name == null) return false;
        return qualifiedName.startsWith("com.vladsch.flexmark.") && (name.equals("RenderingTestCase") || name.equals("SpecExampleProcessor"));
    }

    /**
     * Generate to consumer all PsiClasses which are subclasses of RenderingTestCase or SpecExampleProcessor
     * <p>
     * NOTE: rendering super classes will be generated first with the class itself last, iff any of its supers were rendering test case classes
     *
     * @param psiClass  PsiClass to use as a starting point
     * @param processor return true if had rendering test cases and should continue, false aborts the collection of classes
     */
    @SuppressWarnings("UnusedReturnValue")
    private static boolean processRenderingTestCaseSupers(@NotNull PsiClass psiClass, NotNullBiFunction<PsiClass, Integer, Boolean> processor) {
        HashMap<String, Integer> handled = new HashMap<>();
        return forAllRenderingTestCaseSuperInner(handled, psiClass, processor);
    }

    /**
     * Generate to consumer all PsiClasses which are subclasses of RenderingTestCase or SpecExampleProcessor
     * <p>
     * NOTE: rendering super classes will be generated first with the class itself last, iff any of its supers were rendering test case classes
     *
     * @param psiClass PsiClass to use as a starting point
     * @param consumer return true if had rendering test cases and should continue, false aborts the collection of classes
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean collectRenderingTestCaseSupers(@NotNull PsiClass psiClass, NotNullBiConsumer<PsiClass, Integer> consumer) {
        HashMap<String, Integer> handled = new HashMap<>();
        return forAllRenderingTestCaseSuperInner(handled, psiClass, (psiClass1, level) -> {
            consumer.accept(psiClass1, level);
            return true;
        });
    }

    private static boolean isNotRenderingCandidate(@Nullable String qualifiedName) {
        return qualifiedName == null || qualifiedName.equals("java.lang.Object");
    }

    private static boolean forAllRenderingTestCaseSuperInner(@NotNull HashMap<String, Integer> handled, @NotNull PsiClass psiClass, @NotNull NotNullBiFunction<PsiClass, Integer, Boolean> consumer) {
        String qualifiedName = psiClass.getQualifiedName();
        if (isNotRenderingCandidate(qualifiedName)) return false;
        if (handled.containsKey(qualifiedName)) return handled.get(qualifiedName) >= 0;

        handled.put(qualifiedName, -1);

        PsiClass[] psiSupers = psiClass.getSupers();

        for (PsiClass psiSuper : psiSupers) {
            String qualifiedNameSuper = psiSuper.getQualifiedName();
            if (isNotRenderingCandidate(qualifiedNameSuper)) continue;
            if (handled.containsKey(qualifiedNameSuper)) continue;

            if (isRenderingTestCaseSuper(psiSuper)) {
                // has the right super as direct superclass
                handled.put(qualifiedName, 0);
                if (!consumer.apply(psiSuper, 0)) break;
            } else {
                forAllRenderingTestCaseSuperInner(handled, psiSuper, consumer);
            }
        }

        // Update level for this class after all supers are resolved
        int maxLevel = handled.get(qualifiedName);
        for (PsiClass psiSuper : psiSupers) {
            String qualifiedNameSuper = psiSuper.getQualifiedName();
            if (isNotRenderingCandidate(qualifiedNameSuper)) continue;
            maxLevel = Math.max(maxLevel, handled.getOrDefault(qualifiedNameSuper, -1));
        }

        if (maxLevel >= 0) {
            handled.put(qualifiedName, maxLevel + 1);
            consumer.apply(psiClass, handled.get(qualifiedName));
        }

        return maxLevel >= 0;
    }

    @Nullable
    public static PsiClass getElementPsiClass(@Nullable PsiElement element) {
        if (element == null || !element.isValid()) return null;

        PsiElement psiClass = element.getOriginalElement();
        while (!(psiClass instanceof PsiClass || psiClass instanceof PsiFile)) psiClass = psiClass.getParent();
        if (psiClass instanceof PsiClass) return (PsiClass) psiClass;
        return null;
    }

    /**
     * Return resource spec text and psiClass for the element if it is PsiLiteralExpression and defined SPEC_RESOURCE in the class
     *
     * @param element element which is potentially defining spec resource
     *
     * @return null or pair of string of the SPEC_RESOURCE text and psiClass of the element
     */
    @Nullable
    public static SpecResourceDefinition getSpecResourceDefinitionOrNull(@Nullable PsiElement element) {
        if (element instanceof PsiLiteralExpression) {
            PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
            String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
            if (value != null) {
                PsiElement parent = literalExpression.getParent();

                if (parent instanceof PsiField) {
                    PsiField psiField = (PsiField) parent;
                    if (psiField.getName().equals(SPEC_RESOURCE)) {
                        PsiClass psiClass = getElementPsiClass(element);
                        if (psiClass != null) {
                            if (renderingTestCaseSuperLevel(psiClass) >= 0) {
                                return new SpecResourceDefinition(psiClass, literalExpression, value, new TextRange(1, literalExpression.getTextLength() - 1));
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public static PsiLiteralExpression getSpecResourceLiteralOrNull(@Nullable PsiElement element) {
        return getSpecResourceDefinitionOrNull(element) == null ? null : (PsiLiteralExpression) element;
    }

    @NotNull
    @SafeVarargs
    public static <T extends PsiElement> List<T> findChildrenOfAnyType(@Nullable final PsiElement element, @Nullable Predicate<T> filter, @NotNull final Class<? extends T>... classes) {
        if (element == null) return ContainerUtil.emptyList();
        final ArrayList<T> elements = new ArrayList<>();
        PsiElementProcessor.CollectElements<T> processor = new PsiElementProcessor.CollectElements<T>(elements) {
            @Override
            public boolean execute(@NotNull PsiElement each) {
                if (each == element) return true;
                if (PsiTreeUtil.instanceOf(each, classes)) {
                    //noinspection unchecked
                    if (filter == null || filter.test((T) each)) {
                        //noinspection unchecked
                        return super.execute((T) each);
                    }
                }

                return true;
            }
        };

        //noinspection unchecked
        PsiTreeUtil.processElements(element, (PsiElementProcessor<PsiElement>) processor);
        return elements;
    }

    /**
     * Execute processor for all literal string expressions in class
     *
     * @param psiClass   class where to look
     * @param withSupers true if literal string expressions from supers are to be added
     * @param processor  processor results
     * @param <T>        returned result
     *
     * @return first non-null result from processor or null
     */
    @Nullable
    public static <T> T processTestCaseStringLiterals(@NotNull PsiClass psiClass, boolean withSupers, @NotNull BiFunction<PsiClass, Pair<Integer, List<PsiLiteralExpression>>, T> processor) {
        Object[] result = new Object[] { null };

        if (withSupers) {
            processRenderingTestCaseSupers(psiClass, (specTestClass, level) -> {
                List<PsiLiteralExpression> literalExpressionList = findChildrenOfAnyType(specTestClass, it -> it.getValue() instanceof String, PsiLiteralExpression.class);
                result[0] = processor.apply(specTestClass, Pair.of(level, literalExpressionList));
                return result[0] == null;
            });
        } else {
            int level = renderingTestCaseSuperLevel(psiClass);
            if (level >= 0) {
                List<PsiLiteralExpression> literalExpressionList = findChildrenOfAnyType(psiClass, it -> it.getValue() instanceof String, PsiLiteralExpression.class);
                result[0] = processor.apply(psiClass, Pair.of(level, literalExpressionList));
            }
        }

        //noinspection unchecked
        return (T) result[0];
    }

    @Nullable
    public static PsiLiteralExpression getResourceSpecLiteralOrNull(@NotNull PsiClass psiClass) {
        Collection<PsiField> psiFields = JavaFieldNameIndex.getInstance().get(SPEC_RESOURCE, psiClass.getProject(), GlobalSearchScope.fileScope(psiClass.getContainingFile()));
        for (PsiField psiField : psiFields) {
            PsiLiteralExpression specResource = getSpecResourceLiteralOrNull(psiField.getInitializer());
            if (specResource != null) {
                Object resourceValue = specResource.getValue();
                if (resourceValue != null) {
                    return specResource;
                }
            }
        }
        return null;
    }

    private static boolean isQualifiedNameOfParent(@Nullable PsiElement element, boolean wantSupers, @NotNull String name) {
        return element != null && isQualifiedName(element.getParent(), wantSupers, name);
    }

    private static boolean isQualifiedName(@Nullable PsiElement element, boolean wantSupers, @NotNull String name) {
        if (element instanceof PsiClass) {
            String qualifiedName = ((PsiClass) element).getQualifiedName();
            if (name.equals(qualifiedName)) return true;
            if (wantSupers) return getSuperOrInterface((PsiClass) element, (psiClass) -> name.equals(psiClass.getQualifiedName())) >= 0;
        }
        return false;
    }

    @Nullable
    private static <T extends PsiElement> T getResolvedReference(@Nullable PsiElement element, @NotNull Class<T> psiClass) {
        if (element != null) {
            PsiReference reference = element.getReference();
            if (reference != null) {
                PsiElement resolved = reference.resolve();
                if (resolved != null && psiClass.isAssignableFrom(resolved.getClass())) {
                    //noinspection unchecked
                    return (T) resolved;
                }
            }
        }
        return null;
    }

    @Nullable
    private static <T extends PsiElement> T getResolvedReferenceOfType(@Nullable PsiElement element, @NotNull Class<T> psiClass) {
        if (element instanceof PsiTypeElement) {
            PsiType type = ((PsiTypeElement) element).getType();
            if (type instanceof PsiClassReferenceType) {
                PsiReference reference = ((PsiClassReferenceType) type).getReference();
                PsiElement resolved = reference.resolve();
                if (resolved != null && psiClass.isAssignableFrom(resolved.getClass())) {
                    //noinspection unchecked
                    return (T) resolved;
                }
            }
        }
        return null;
    }

    @Nullable
    private static <T extends PsiElement> T getChildOfType(@Nullable PsiElement element, @NotNull Class<T> psiClass) {
        if (element != null) {
            PsiElement type = element.getFirstChild();
            while (type != null && !(psiClass.isAssignableFrom(type.getClass()))) type = type.getNextSibling();
            //noinspection unchecked
            return (T) type;
        }
        return null;
    }

    private static boolean isOptionsMapDeclaration(@NotNull PsiElement element) {
        if (isQualifiedNameOfParent(getResolvedReference(element, PsiElement.class), true, "java.util.Map")) {
            PsiElement firstChild = element.getFirstChild();
            PsiElement lastChild = firstChild.getLastChild();
            if (firstChild instanceof PsiReferenceExpression && lastChild instanceof PsiIdentifier) {
                // figure out if this is a map of <String, DataHolder>
                PsiElement resolvedReference = getResolvedReference(firstChild, PsiElement.class);
                PsiTypeElement type = getChildOfType(resolvedReference, PsiTypeElement.class);
                if (type != null) {
                    PsiElement child = type.getFirstChild();
                    if (child instanceof PsiJavaCodeReferenceElement) {
                        PsiReferenceParameterList params = getChildOfType(child, PsiReferenceParameterList.class);
                        if (params != null) {
                            int paramCount = 0;
                            PsiElement param = params.getFirstChild();
                            int isOptionsMap = 0;

                            while (param != null) {
                                if (param instanceof PsiTypeElement) {
                                    if (paramCount == 0) {
                                        if (!isQualifiedName(getResolvedReferenceOfType(param, PsiClass.class), false, "java.lang.String")) {
                                            break;
                                        }
                                        isOptionsMap = 1;
                                    } else if (paramCount == 1) {
                                        if (!isQualifiedName(getResolvedReferenceOfType(param, PsiClass.class), true, "com.vladsch.flexmark.util.data.DataHolder")) {
                                            break;
                                        }
                                        isOptionsMap = 2;
                                    } else {
                                        isOptionsMap = 3;
                                        break;
                                    }
                                    paramCount++;
                                }
                                param = param.getNextSibling();
                            }

                            return isOptionsMap == 2;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Nullable
    public static FlexmarkExampleOptionDefinition getFlexmarkExampleOptionDefinition(@NotNull PsiElement element, @Nullable PsiClass originalClass) {
        PsiClass psiClass = getElementPsiClass(element);
        if (psiClass != null && element instanceof PsiLiteralExpression) {
            PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
            String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
            if (value != null) {
                PsiElement parent = literalExpression.getParent();
                if (parent instanceof PsiExpressionList) {
                    parent = parent.getParent();
                    if (parent instanceof PsiMethodCallExpression) {
                        PsiElement firstChild = parent.getFirstChild();
                        PsiElement lastChild = firstChild.getLastChild();
                        if (firstChild instanceof PsiReferenceExpression && lastChild instanceof PsiIdentifier && lastChild.getText().equals("put")) {
                            if (isOptionsMapDeclaration(firstChild)) {
                                int endOffset = element.getTextLength() - 1;
                                return new FlexmarkExampleOptionDefinition(psiClass, element, new TextRange(1, endOffset), originalClass == null ? psiClass : originalClass);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public static List<String> getFlexmarkExampleOptionDataKeys(PsiElement element) {
        List<String> optionDataKeys = null;
        if (element instanceof PsiLiteralExpression) {
            PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
            PsiElement parent = literalExpression.getParent();
            if (parent instanceof PsiExpressionList) {
                Collection<PsiIdentifier> identifiers = findChildrenOfAnyType(parent, it -> it.getParent() instanceof PsiReferenceExpression, PsiIdentifier.class);
                // now if these identifiers have DataKey inherited class then we have them
                for (PsiIdentifier identifier : identifiers) {
                    PsiElement setRefExp = identifier.getParent();
                    PsiElement setRefExpParent = setRefExp.getParent();
                    if (setRefExpParent instanceof PsiMethodCallExpression) {
                        PsiReference reference = identifier.getParent().getReference();
                        if (reference != null) {
                            PsiElement methodElement = reference.resolve();
                            if (methodElement instanceof PsiMethod) {
                                PsiMethod method = (PsiMethod) methodElement;
                                if ("set".equals(method.getName())) {
                                    Collection<PsiExpressionList> dataKeyExprParams = PsiTreeUtil.findChildrenOfType(setRefExpParent, PsiExpressionList.class);
                                    for (PsiElement dataKeyExpParam : dataKeyExprParams) {
                                        String text = dataKeyExpParam.getText();
                                        String params = text.substring(1, text.length() - 1).trim();
                                        if (!params.isEmpty()) {
                                            // have our params
                                            if (optionDataKeys == null) {
                                                optionDataKeys = new ArrayList<String>();
                                            }
                                            if (!optionDataKeys.contains(text)) {
                                                optionDataKeys.add(text);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return optionDataKeys;
    }

    // ********************************************************
    // Cached Data Access
    // ********************************************************

    @NotNull
    public static <T> Result<T> forAllRenderingTestCaseClasses(@NotNull MdFile mdFile, @NotNull SpecTestDataProcessor<T> processor) {
        return FlexmarkProjectCachedData.forAllRenderingTestCaseFiles(mdFile.getProject(), psiFile -> {
            FlexmarkSpecTestCaseCachedData.Companion.Data testCaseData = FlexmarkSpecTestCaseCachedData.Companion.getData(psiFile);
            //noinspection SuspiciousMethodCalls
            if (testCaseData.getSpecFiles().contains(mdFile.getOriginalFile())) {
                return processor.apply(testCaseData);
            }
            return Result.CONTINUE();
        });
    }

    @SafeVarargs
    static public <T> Class<T>[] arrayOf(Class<T>... args) {
        return args;
    }

    @NotNull
    static public <T> Result<T> forAllOptions(@NotNull MdFile file, @NotNull final FlexmarkExampleOptionProcessor<T> processor) {
        Ref<Result<T>> result = new Ref<>(Result.CONTINUE());

        MdCachedFileElements.findChildrenOfAnyType(file, false, false, false, arrayOf(FlexmarkExampleOption.class), (option, source) -> {
            result.value = processor.apply(option);
            if (result.value.isStop()) return Result.STOP();
            return Result.CONTINUE();
        });

        return result.value;
    }

    /**
     * invoke processor for every test case file whi
     *
     * @param mdFile               markdown file for which to get option definitions
     * @param optionName           option name, null or empty for all options
     * @param wantInheritedOptions true if options defined in the test class or its supers, false for only options defined in the test class
     * @param processor            invoked with (PsiClass psiTestClass, {@link FlexmarkExampleOptionDefinition}[] definitions)
     *                             NOTE: psiTestClass is always a test class which defines this mdFile as its spec resource
     *                             definitions[0] is the definition used for resolving the option, [1...] if present are super class overridden definitions
     *                             definition[0].getPsiClass() will not be equal to psiTestClass if the test class inherits the option
     * @param <T>                  result type
     *
     * @return final result which terminated the processing, Result.CONTINUE() if terminated because there were no more definitions
     */
    @NotNull
    static public <T> Result<T> forOptionDefinitions(@NotNull MdFile mdFile, @Nullable String optionName, boolean wantInheritedOptions, @NotNull final FlexmarkExampleOptionDefinitionProcessor<T> processor) {
        FlexmarkProjectCachedData.Companion.Data projectData = FlexmarkProjectCachedData.getData(mdFile.getProject());
        HashMap<String, Pair<PsiClass, FlexmarkExampleOptionDefinition[]>> options = new HashMap<>();

        // collect all classes which use this spec file
        for (PsiFile psiFile : projectData.getRenderingTestCaseFiles()) {
            FlexmarkSpecTestCaseCachedData.Companion.Data psiFileData = FlexmarkSpecTestCaseCachedData.getData(psiFile);

            for (PsiClass psiClass : psiFileData.getPsiClassData().keySet()) {
                FlexmarkSpecTestCaseCachedData.Companion.PsiClassData psiClassData = psiFileData.getPsiClassData().get(psiClass);
                if (psiClassData == null || !psiClassData.getSpecFiles().containsKey(mdFile)) continue;

                LinkedHashMap<String, FlexmarkExampleOptionDefinition[]> definitions = psiClassData.getResolvedOptionDefinitions(wantInheritedOptions);
                boolean hadCallback = false;

                if (optionName != null && !optionName.isEmpty()) {
                    FlexmarkExampleOptionDefinition[] optionDefinitions = definitions.get(optionName);
                    if (optionDefinitions != null && optionDefinitions.length > 0) {
                        hadCallback = true;
                        Result<T> result = processor.apply(psiClass, optionDefinitions);
                        if (result.isStop()) return result;
                    }
                } else {
                    for (FlexmarkExampleOptionDefinition[] optionDefinitions : definitions.values()) {
                        if (optionDefinitions.length > 0) {
                            hadCallback = true;
                            Result<T> result = processor.apply(psiClass, optionDefinitions);
                            if (result.isStop()) return result;
                        }
                    }
                }
            }
        }

        return Result.CONTINUE();
    }

    public static <T> int indexOf(@NotNull T[] list, @NotNull T element) {
        int i = 0;
        for (T item : list) {
            if (item.equals(element)) return i;
            i++;
        }
        return -1;
    }

    @NotNull
    public static <T> Result<T> forAllRenderingSubClasses(@NotNull PsiClass psiClass, PsiClassProcessor<T> processor) {
        HashSet<PsiClass> informed = new HashSet<>();
        return FlexmarkProjectCachedData.forAllRenderingTestCaseClasses(psiClass.getProject(), (psiSubClass, level) -> {
            if (psiSubClass != psiClass) {
                boolean[] isSubClass = { false };

                if (!informed.contains(psiSubClass)) {
                    informed.add(psiSubClass);

                    processRenderingTestCaseSupers(psiSubClass, (psiSuper, superLevel) -> {
                        if (psiSuper == psiClass) {
                            isSubClass[0] = true;
                            return false;
                        }
                        return true;
                    });

                    if (isSubClass[0]) {
                        return processor.apply(psiSubClass, level);
                    }
                }
            }
            return Result.CONTINUE();
        });
    }

    @NotNull
    static public Set<String> getOptionDefinitionStrings(@NotNull MdFile mdFile, boolean wantInheritedOptions) {
        HashSet<String> definitions = new HashSet<>();

        FlexmarkPsiImplUtils.forOptionDefinitions(mdFile, null, wantInheritedOptions, (psiClass, definitions1) -> {
            definitions.add(definitions1[0].getOptionName());
            return Result.CONTINUE();
        });

        return definitions;
    }

    @NotNull
    static public Set<FlexmarkExampleOptionDefinition> getOptionDefinitions(@NotNull MdFile mdFile, boolean wantInheritedOptions) {
        HashSet<FlexmarkExampleOptionDefinition> definitions = new HashSet<>();

        FlexmarkPsiImplUtils.forOptionDefinitions(mdFile, null, wantInheritedOptions, (psiClass, definitions1) -> {
            definitions.add(definitions1[0]);
            return Result.CONTINUE();
        });

        return definitions;
    }
}
