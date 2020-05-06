// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.vladsch.flexmark.util.misc.Pair;
import com.vladsch.flexmark.util.misc.Ref;
import com.vladsch.flexmark.util.misc.Utils;
import com.vladsch.md.nav.parser.MdParserDefinition;
import com.vladsch.md.nav.parser.cache.data.CachedDataKey;
import com.vladsch.md.nav.parser.cache.data.transaction.CachedTransactionContext;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.psi.element.MdJekyllIncludeLinkRef;
import com.vladsch.md.nav.psi.element.MdPsiElement;
import com.vladsch.md.nav.psi.element.MdReferenceElement;
import com.vladsch.md.nav.psi.element.MdReferenceElementIdentifier;
import com.vladsch.md.nav.psi.element.MdReferencingElementReference;
import com.vladsch.md.nav.psi.util.MdIndexUtil;
import com.vladsch.md.nav.psi.util.MdNodeVisitor;
import com.vladsch.md.nav.psi.util.MdTypes;
import com.vladsch.md.nav.psi.util.MdVisitor;
import com.vladsch.md.nav.util.PsiSet;
import com.vladsch.md.nav.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.vladsch.md.nav.parser.MdParserDefinition.WANT_PROFILE_TRACE;

public class MdCachedFileElements {
    @SafeVarargs
    public static <T> Class<? extends T>[] arrayOf(Class<? extends T>... classes) {
        return classes;
    }

    final private static CachedDataKey<MdFile, MdCachedFileElements> CACHED_FILE_ELEMENTS = new CachedDataKey<MdFile, MdCachedFileElements>("CACHED_FILE_ELEMENTS") {
        @NotNull
        @Override
        public MdCachedFileElements compute(@NotNull CachedTransactionContext<MdFile> context) {
            MdFile file = context.getDataOwner();
            MdElementCollectingNodeVisitor visitor = new MdElementCollectingNodeVisitor();
            visitor.collect(file);
            context.addDependency(file);
            return new MdCachedFileElements(file, visitor.getElements(), visitor.getClassArrayIndexMap());
        }

        @Override
        public boolean isValid(@NotNull MdCachedFileElements value) {
            return value.isValid();
        }
    };

    final private static CachedDataKey<MdFile, Map<String, ? extends Set<MdReferenceElement>>> REFERENCED_ELEMENT_MAP =
            new CachedDataKey<MdFile, Map<String, ? extends Set<MdReferenceElement>>>("FILE:REFERENCING_ELEMENTS") {

                @Override
                public boolean isValid(@NotNull Map<String, ? extends Set<MdReferenceElement>> value) {
                    for (Set<MdReferenceElement> elements : value.values()) {
                        for (MdReferenceElement element : elements) {
                            if (!element.isValid()) return false;
                        }
                    }
                    return true;
                }

                @NotNull
                @Override
                public Map<String, ? extends Set<MdReferenceElement>> compute(@NotNull CachedTransactionContext<MdFile> context) {
                    MdFile file = context.getDataOwner();
                    HashMap<String, Set<MdReferenceElement>> referencesMap = new HashMap<>();
                    final PsiFile[] lastFile = { null };

                    // NOTE: add all referencing elements from included files and including files
                    MdCachedFileElements.findChildrenOfAnyType(file, false, true, true, arrayOf(MdReferencingElementReference.class), (childReference, source) -> {
                        PsiReference reference = childReference.getReference();
                        if (reference != null) {
                            MdReferenceElementIdentifier resolve = null;
                            if (reference instanceof PsiPolyVariantReference) {
                                ResolveResult[] results = ((PsiPolyVariantReference) reference).multiResolve(false);
                                if (results.length > 0) resolve = (MdReferenceElementIdentifier) results[0].getElement();
                            }
                            if (resolve != null) {
                                resolve = (MdReferenceElementIdentifier) reference.resolve();
                                if (resolve != null) {
                                    PsiElement referenceElement = resolve.getReferenceElement();
                                    if (referenceElement instanceof MdReferenceElement) {
                                        String id = ((MdReferenceElement) referenceElement).getReferenceId();
                                        PsiSet<MdReferenceElement> referenceElements = (PsiSet<MdReferenceElement>) referencesMap.computeIfAbsent(id, k -> new PsiSet<>(HashSet::new));
                                        referenceElements.add((MdReferenceElement) referenceElement);

                                        if (lastFile[0] != source.file) {
                                            lastFile[0] = source.file;
                                            context.addDependency(source.file);
                                        }
                                    }
                                }
                            }
                        }
                        return Result.CONTINUE();
                    });

                    return referencesMap;
                }
            };

    final private static CachedDataKey<MdFile, Map<IElementType, ? extends Map<String, Integer>>> REFERENCE_DEFINITION_COUNTS =
            new CachedDataKey<MdFile, Map<IElementType, ? extends Map<String, Integer>>>("FILE:REFERENCE_DEFINITION_COUNTS") {

                @Override
                public boolean isValid(@NotNull Map<IElementType, ? extends Map<String, Integer>> value) {
                    return true;
                }

                @NotNull
                @Override
                public Map<IElementType, ? extends Map<String, Integer>> compute(@NotNull CachedTransactionContext<MdFile> context) {
                    MdFile file = context.getDataOwner();
                    HashMap<IElementType, HashMap<String, Integer>> referenceElementsMap = new HashMap<>();
                    final PsiFile[] lastFile = { null };

                    // NOTE: count all elements from included files but not including files
                    MdCachedFileElements.findChildrenOfAnyType(file, false, true, false, arrayOf(MdReferenceElement.class), (referenceElement, source) -> {
                        IElementType referenceType = referenceElement.getReferenceType();
                        if (referenceType != MdTypes.DUMMY_REFERENCE) {
                            String id = referenceElement.getReferenceId();
                            HashMap<String, Integer> countMap = referenceElementsMap.computeIfAbsent(referenceType, k -> new HashMap<>());
                            countMap.put(id, countMap.computeIfAbsent(id, k -> 0) + 1);
                            if (lastFile[0] != source.file) {
                                lastFile[0] = source.file;
                                if (source.file != file) {
                                    context.addDependency(source.file);
                                }
                            }
                        }
                        return Result.CONTINUE();
                    });

                    return referenceElementsMap;
                }
            };

    @NotNull
    public static Map<String, ? extends Set<MdReferenceElement>> getReferencedElementMap(@NotNull MdFile mdFile) {
        return CachedData.get(mdFile, REFERENCED_ELEMENT_MAP);
    }

    @NotNull
    public static Map<IElementType, ? extends Map<String, Integer>> getReferenceDefinitionCounts(@NotNull MdFile mdFile) {
        return CachedData.get(mdFile, REFERENCE_DEFINITION_COUNTS);
    }

    /**
     * Visitor to build a list of all MdPsiElements in the file
     */
    static class MdElementCollectingNodeVisitor extends MdNodeVisitor {
        private final ArrayList<PsiElement> myElements = new ArrayList<>();
        private final HashMap<Class<?>, ArrayList<Integer>> myClassIndexMap = new HashMap<>();

        public MdElementCollectingNodeVisitor() {
            super(false); // all elements, not just block elements
        }

        void collect(MdFile file) {
            visit(file);
        }

        public ArrayList<PsiElement> getElements() {
            return myElements;
        }

        public HashMap<Class<?>, ArrayList<Integer>> getClassIndexMap() {
            return myClassIndexMap;
        }

        public HashMap<Class<?>, int[]> getClassArrayIndexMap() {
            HashMap<Class<?>, int[]> classIndexMap = new HashMap<>(myClassIndexMap.size());
            for (Map.Entry<Class<?>, ArrayList<Integer>> entry : myClassIndexMap.entrySet()) {
                ArrayList<Integer> value = entry.getValue();
                int iMax = value.size();
                int[] intArray = new int[iMax];
                for (int i = 0; i < iMax; i++) {
                    intArray[i] = value.get(i);
                }
                classIndexMap.put(entry.getKey(), intArray);
            }
            return classIndexMap;
        }

        @Override
        protected void processNode(@NotNull PsiElement node, boolean withChildren, @NotNull BiConsumer<PsiElement, MdVisitor<PsiElement>> processor) {
            if (WANT_PROFILE_TRACE) System.out.print(String.format("Processing node: %s:'%s'", node.getNode(), Utils.escapeJavaString(node.getText())));
            if (node instanceof MdPsiElement) {
                if (WANT_PROFILE_TRACE) System.out.println("  adding node");
                Class<?> nodeClass = node.getClass();
                ArrayList<Integer> indices = myClassIndexMap.computeIfAbsent(nodeClass, aClass -> new ArrayList<>());
                indices.add(myElements.size());
                myElements.add(node);
            } else {
                if (WANT_PROFILE_TRACE) System.out.println("  skipped node");
            }
            processChildren(node, processor);
        }
    }

    private final @NotNull MdFile myFile;
    private final @NotNull ArrayList<PsiElement> myElements;
    private final @NotNull HashMap<Class<?>, int[]> myClassIndexMap;

    public MdCachedFileElements(@NotNull MdFile file, @NotNull ArrayList<PsiElement> elements, @NotNull HashMap<Class<?>, int[]> classIndexMap) {
        myFile = file;
        myElements = elements;
        myClassIndexMap = classIndexMap;
    }

    /**
     * @return true if all elements in the stash are valid
     */
    public boolean isValid() {
        for (PsiElement element : myElements) {
            if (!element.isValid()) return false;
        }
        return true;
    }

    @NotNull
    public static <T> HashSet<Class<? extends T>> resolvedClassSet(@NotNull Class<? extends T>[] requestedClasses) {
        HashSet<Class<? extends T>> resolvedClassSet = new HashSet<>();
        for (Class<? extends T> requestedClass : requestedClasses) {
            if (requestedClass != null) {
                Set<? extends Class<? extends T>> resolvedClass = MdParserDefinition.getAllTypeFactoryElementsFor(requestedClass);
                resolvedClassSet.addAll(resolvedClass);
            }
        }
        return resolvedClassSet;
    }

    static public <T> Result<T> findChildrenOfAnyType(
            @NotNull MdFile file,
            boolean addOuterFile,
            boolean addIncludedFiles,
            boolean addIncludingFiles,
            @NotNull final Class<? extends T>[] classes,
            @NotNull final SourcedElementConsumer<T> consumer
    ) {
        MdCachedFileElements stash = CachedData.get(file, CACHED_FILE_ELEMENTS);
        return stash.invokeChildrenOfAnyType(addOuterFile, addIncludedFiles, addIncludingFiles, classes, consumer);
    }

    static public <T> Result<T> findChildrenOfAnyType(
            @NotNull MdFile file,
            boolean addOuterFile,
            boolean addIncludedFiles,
            boolean addIncludingFiles,
            @NotNull final Class<? extends T>[] classes,
            @NotNull final Consumer<T> consumer
    ) {
        MdCachedFileElements stash = CachedData.get(file, CACHED_FILE_ELEMENTS);
        return stash.invokeChildrenOfAnyType(addOuterFile, addIncludedFiles, addIncludingFiles, classes, (t, source) -> {
            consumer.accept(t);
            return Result.CONTINUE();
        });
    }

    public static <T> List<T> listChildrenOfAnyType(
            @NotNull MdFile file,
            boolean addOuterFile,
            boolean addIncludedFiles,
            boolean addIncludingFiles,
            @NotNull final Class<? extends T>[] classes
    ) {
        ArrayList<T> list = new ArrayList<>();
        MdCachedFileElements stash = CachedData.get(file, CACHED_FILE_ELEMENTS);

        stash.invokeChildrenOfAnyType(addOuterFile, addIncludedFiles, addIncludingFiles, classes, (t, source) -> {
            list.add(t);
            return Result.CONTINUE();
        });

        return list;
    }

    private <T> Result<T> invokeChildrenOfAnyType(
            boolean addOuterFile,
            boolean addIncludedFiles,
            boolean addIncludingFiles,
            @NotNull final Class<? extends T>[] classes,
            @NotNull final SourcedElementConsumer<T> consumer
    ) {
        @Nullable ArrayList<Pair<PsiElement, Integer>> requestedElements;
        boolean jekyllIsExtra = false;
        HashSet<Class<? extends T>> useClasses = resolvedClassSet(classes);

        if (addIncludedFiles) {
            //noinspection unchecked
            Class<? extends T> resolvedClass = (Class<? extends T>) MdParserDefinition.getCurrentFactoryClassOrNull(MdJekyllIncludeLinkRef.class);
            if (resolvedClass != null && !useClasses.contains(resolvedClass)) {
                // need to add this to the list
                jekyllIsExtra = true;
                useClasses.add(resolvedClass);
            }
        }

        int totalElements = 0;
        for (Class<? extends T> elementClass : useClasses) {
            int[] ints = myClassIndexMap.get(elementClass);
            if (ints != null) {
                totalElements += ints.length;
            }
        }

        requestedElements = new ArrayList<>(totalElements);
        for (Class<? extends T> elementClass : useClasses) {
            int[] ints = myClassIndexMap.get(elementClass);
            if (ints != null) {
                for (int anInt : ints) {
                    requestedElements.add(Pair.of(myElements.get(anInt), anInt));
                }
            }
        }

        requestedElements.sort(Comparator.comparing(Pair::getSecond));
        return findChildrenOfAnyType(addOuterFile, addIncludedFiles, addIncludingFiles, null, classes, requestedElements, jekyllIsExtra, consumer);
    }

    private <T> Result<T> findChildrenOfAnyType(
            boolean addOuterFile,
            boolean addIncludedFiles,
            boolean addIncludingFiles,
            @Nullable ElementSource forceSource,
            @NotNull final Class<? extends T>[] classes,
            @NotNull ArrayList<Pair<PsiElement, Integer>> requestedElements,
            boolean jekyllIsExtra,
            @NotNull final SourcedElementConsumer<T> consumer
    ) {
        Result<T> result = Result.CONTINUE();
        boolean skipFileElements = false;
        ElementSource fileSource = forceSource != null ? forceSource : ElementSource.FILE(myFile);

        for (Pair<PsiElement, Integer> pair : requestedElements) {
            PsiElement element = pair.getFirst();
            if (!skipFileElements && !(jekyllIsExtra && element instanceof MdJekyllIncludeLinkRef)) {
                //noinspection unchecked
                result = consumer.accept((T) element, fileSource);
                if (result.isStop()) {
                    return result;
                } else if (result.isSkip()) {
                    if (forceSource != null) return result;

                    skipFileElements = true;
                    result = Result.CONTINUE();
                }
            }

            if (addIncludedFiles && element instanceof MdJekyllIncludeLinkRef) {
                // load the file's elements
                final PsiReference reference = element.getReference();
                if (reference != null) {
                    final PsiElement resolved = reference.resolve();
                    if (resolved instanceof MdFile) {
                        MdFile includedFile = (MdFile) resolved;
                        MdCachedFileElements includedStash = CachedData.get(includedFile, CACHED_FILE_ELEMENTS);
                        result = includedStash.findChildrenOfAnyType(false, false, addIncludingFiles, ElementSource.INCLUDED_FILE(includedFile), classes, requestedElements, jekyllIsExtra, consumer);
                        if (result.isStop()) {
                            return result;
                        } else if (result.isSkip()) {
                            if (forceSource != null) return result;
                            addIncludedFiles = false;
                            result = Result.CONTINUE();
                        }
                    }
                }
            }

            if (skipFileElements && !addIncludedFiles) break;
        }

        if (result.isContinue() && addOuterFile) {
            MdFile outerFile = PsiTreeUtil.getContextOfType(myFile, MdFile.class);
            if (outerFile != null) {
                MdCachedFileElements outerStash = CachedData.get(outerFile, CACHED_FILE_ELEMENTS);
                result = outerStash.findChildrenOfAnyType(false, false, addIncludingFiles, forceSource != null ? forceSource : ElementSource.OUTER_FILE(outerFile), classes, requestedElements, jekyllIsExtra, consumer);
                if (result.isStop()) {
                    return result;
                } else if (result.isSkip()) {
                    if (forceSource != null) return result;
                    result = Result.CONTINUE();
                }
            }
        }

        if (result.isContinue() && addIncludingFiles) {
            Ref<Result<T>> results = new Ref<>(Result.CONTINUE());
            PsiFile containingFile = myFile.getContainingFile();
            if (containingFile instanceof MdFile && ((MdFile) containingFile).isIncludeFile()) {
                boolean finalAddIncludedFiles = addIncludedFiles;
                MdIndexUtil.processReferences(containingFile, GlobalSearchScope.projectScope(myFile.getProject()), reference -> {
                    PsiElement referencingElement = reference.getElement();
                    if (referencingElement instanceof MdJekyllIncludeLinkRef) {
                        MdFile includingFile = ((MdJekyllIncludeLinkRef) referencingElement).getMdFile();
                        MdCachedFileElements includingStash = CachedData.get(includingFile, CACHED_FILE_ELEMENTS);
                        results.value = includingStash.findChildrenOfAnyType(false, finalAddIncludedFiles, false, forceSource != null ? forceSource : ElementSource.INCLUDING_FILE(includingFile), classes, requestedElements, jekyllIsExtra, consumer);
                        return results.value.isContinue();
                    }
                    return true;
                });
            }

            result = results.value;
        }

        return result;
    }
}
