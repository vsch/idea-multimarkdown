/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.idea.multimarkdown.annotator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.AnnotationSession;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.vladsch.idea.multimarkdown.util.Severity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;

public class AnnotationState {
    final public static String TYPE_CHANGE_LINK_REF_QUICK_FIX = "ChangeLinkRefQuickFix";
    final public static String TYPE_CREATE_FILE_QUICK_FIX = "CreateFileQuickFix";
    final public static String TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX = "DeleteWikiPageRefQuickFix";
    final public static String TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX = "DeleteWikiPageTitleQuickFix";
    final public static String TYPE_RENAME_FILE_AND_RE_TARGET_QUICK_FIX = "RenameFileAndReTargetQuickFix";
    final public static String TYPE_RENAME_FILE_QUICK_FIX = "RenameFileQuickFix";
    final public static String TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX = "SwapWikiPageRefTitleQuickFix";
    final public static String TYPE_CHANGE_EXPLICIT_LINK_TO_WIKI_LINK_QUICK_FIX = "ChangeExplicitLinkToWikiLinkQuickFix";
    final public static String TYPE_CHANGE_WIKI_LINK_QUICK_FIX_TO_EXPLICIT_LINK = "ChangeWikiLinkToExplicitLinkQuickFix";

    final public static HashMap<String, Integer> typeParams = new HashMap<String, Integer>(10);
    static {
        typeParams.put(TYPE_CHANGE_LINK_REF_QUICK_FIX, 1);
        typeParams.put(TYPE_CREATE_FILE_QUICK_FIX, 1);
        typeParams.put(TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX, 0);
        typeParams.put(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX, 0);
        typeParams.put(TYPE_RENAME_FILE_AND_RE_TARGET_QUICK_FIX, 2);
        typeParams.put(TYPE_RENAME_FILE_QUICK_FIX, 2);
        typeParams.put(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX, 0);
        typeParams.put(TYPE_CHANGE_EXPLICIT_LINK_TO_WIKI_LINK_QUICK_FIX, 0);
        typeParams.put(TYPE_CHANGE_WIKI_LINK_QUICK_FIX_TO_EXPLICIT_LINK, 0);
    }

    public final AnnotationHolder holder;
    public Annotation annotator = null;
    public boolean warningsOnly = true;
    public boolean unresolved = false;
    public boolean canCreateFile = true;
    public boolean needTargetList = true;
    protected HashMap<String, HashSet<String>> alreadyOffered = new HashMap<String, HashSet<String>>();

    public AnnotationState(AnnotationHolder holder) {
        this.holder = holder;
    }

    public boolean hadAnnotation() {
        return annotator != null;
    }

    public boolean alreadyOfferedIds(@NotNull String type, @NotNull String... ids) {
        if (!alreadyOffered.containsKey(type)) return false;
        HashSet<String> idSet = alreadyOffered.get(type);
        for (String id : ids) {
            if (!idSet.contains(id)) return false;
        }
        return true;
    }

    public boolean alreadyOfferedTypes(@NotNull String... types) {
        for (String type : types) {
            if (!alreadyOffered.containsKey(type)) return false;
        }
        return true;
    }

    public static int sumLength(@NotNull String[] array) {
        int sum = 0;
        for (String s : array) {
            if (s != null) {
                sum += s.length();
            }
        }
        return sum;
    }

    public static String implode(@NotNull String separator, String... array) {
        StringBuilder result = new StringBuilder(array.length * separator.length() + sumLength(array));
        for (String s : array) {
            if (s != null) {
                if (result.length() > 0) {
                    result.append(separator);
                }
                result.append(s);
            }
        }
        return result.toString();
    }

    public boolean addingAlreadyOffered(@NotNull String type, String... idList) {
        assert typeParams.containsKey(type) : "quickFix type " + type + " is not defined in parameter map";
        assert typeParams.get(type) == idList.length : "quickFix type " + type + " should have " + typeParams.get(type) + ", given " + idList.length;

        String id;
        if (idList.length == 0) {
            if (!alreadyOfferedTypes(type)) {
                addAlreadyOffered(type);
                return true;
            }
            return false;
        } else if (idList.length == 1) id = idList[0];
        else id = implode("|", idList);

        if (!alreadyOfferedIds(type, id)) {
            addAlreadyOffered(type, id);
            return true;
        }
        return false;
    }

    public boolean alreadyOfferedId(String type, String... idList) {
        assert typeParams.containsKey(type) : "quickFix type " + type + " is not defined in parameter map";
        assert typeParams.get(type) == idList.length : "quickFix type " + type + " should have " + typeParams.get(type) + ", given " + idList.length;

        String id;
        if (idList.length == 0) return !alreadyOfferedTypes(type);
        else if (idList.length == 1) id = idList[0];
        else id = implode("|", idList);

        return alreadyOfferedIds(type, id);
    }

    public void addAlreadyOffered(@NotNull String type, @NotNull String id) {
        if (!alreadyOffered.containsKey(type)) {
            alreadyOffered.put(type, new HashSet<String>(1));
        }

        alreadyOffered.get(type).add(id);
    }

    public void addAlreadyOffered(@NotNull String type) {
        if (!alreadyOffered.containsKey(type)) {
            alreadyOffered.put(type, new HashSet<String>(1));
        }
    }

    public HashSet<String> getAlreadyOffered(String type) {
        addAlreadyOffered(type);
        return alreadyOffered.get(type);
    }

    public int getAlreadyOfferedSize(String type) {
        return !alreadyOffered.containsKey(type) ? 0 : alreadyOffered.get(type).size();
    }

    // delegated to holder
    public Annotation createErrorAnnotation(@NotNull PsiElement element, @Nullable String s) {
        return createErrorAnnotation(element.getNode(), s);
    }
    public Annotation createErrorAnnotation(@NotNull ASTNode node, @Nullable String s) {
        return createErrorAnnotation(node.getTextRange(), s);
    }
    public Annotation createErrorAnnotation(@NotNull TextRange range, @Nullable String s) {
        annotator = holder.createErrorAnnotation(range, s);
        annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
        annotator.setNeedsUpdateOnTyping(true);
        warningsOnly = false;
        return annotator;
    }
    public Annotation createWarningAnnotation(@NotNull PsiElement element, @Nullable String s) {
        annotator = holder.createWarningAnnotation(element, s);
        annotator.setNeedsUpdateOnTyping(true);
        return annotator;
    }
    public Annotation createWarningAnnotation(@NotNull ASTNode node, @Nullable String s) {
        annotator = holder.createWarningAnnotation(node, s);
        annotator.setNeedsUpdateOnTyping(true);
        return annotator;
    }
    public Annotation createWarningAnnotation(@NotNull TextRange range, @Nullable String s) {
        annotator = holder.createWarningAnnotation(range, s);
        annotator.setNeedsUpdateOnTyping(true);
        return annotator;
    }
    public Annotation createWeakWarningAnnotation(@NotNull PsiElement element, @Nullable String s) {
        annotator = holder.createWeakWarningAnnotation(element, s);
        annotator.setNeedsUpdateOnTyping(true);
        return annotator;
    }
    public Annotation createWeakWarningAnnotation(@NotNull ASTNode node, @Nullable String s) {
        annotator = holder.createWeakWarningAnnotation(node, s);
        annotator.setNeedsUpdateOnTyping(true);
        return annotator;
    }
    public Annotation createWeakWarningAnnotation(@NotNull TextRange range, @Nullable String s) {
        annotator = holder.createWeakWarningAnnotation(range, s);
        annotator.setNeedsUpdateOnTyping(true);
        return annotator;
    }
    public Annotation createInfoAnnotation(@NotNull PsiElement element, @Nullable String s) {
        annotator = holder.createInfoAnnotation(element, s);
        annotator.setNeedsUpdateOnTyping(true);
        return annotator;
    }
    public Annotation createInfoAnnotation(@NotNull ASTNode node, @Nullable String s) {
        annotator = holder.createInfoAnnotation(node, s);
        annotator.setNeedsUpdateOnTyping(true);
        return annotator;
    }
    public Annotation createInfoAnnotation(@NotNull TextRange range, @Nullable String s) {
        annotator = holder.createInfoAnnotation(range, s);
        annotator.setNeedsUpdateOnTyping(true);
        return annotator;
    }

    public Annotation createAnnotation(@NotNull HighlightSeverity severity, @NotNull TextRange range, @Nullable String s) {
        annotator = holder.createAnnotation(severity, range, s);
        if (severity == HighlightSeverity.ERROR) {
            annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
            warningsOnly = false;
        }
        annotator.setNeedsUpdateOnTyping(true);
        return annotator;
    }

    public Annotation createAnnotation(@NotNull HighlightSeverity severity, @NotNull TextRange range, @Nullable String s, @Nullable String s1) {
        annotator = holder.createAnnotation(severity, range, s, s1);
        if (severity == HighlightSeverity.ERROR) {
            annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
            warningsOnly = false;
        }
        annotator.setNeedsUpdateOnTyping(true);
        return annotator;
    }

    public static HighlightSeverity mapSeverity(@NotNull Severity severity) {
        HighlightSeverity highlightSeverity;
        switch (severity) {
            case INFO:
                highlightSeverity = HighlightSeverity.INFORMATION;
                break;
            case WEAK_WARNING:
                highlightSeverity = HighlightSeverity.WEAK_WARNING;
                break;
            case WARNING:
                highlightSeverity = HighlightSeverity.WARNING;
                break;
            case ERROR:
                highlightSeverity = HighlightSeverity.ERROR;
                break;
            default:
                highlightSeverity = HighlightSeverity.ERROR;
                break;
        }

        return highlightSeverity;
    }

    public Annotation createAnnotation(@NotNull Severity severity, @NotNull TextRange range, @Nullable String s) {
        return createAnnotation(mapSeverity(severity), range, s);
    }

    public Annotation createAnnotation(@NotNull Severity severity, @NotNull TextRange range, @Nullable String s, @Nullable String s1) {
        return createAnnotation(mapSeverity(severity), range, s, s1);
    }

    @NotNull
    public AnnotationSession getCurrentAnnotationSession() {return holder.getCurrentAnnotationSession();}
    public boolean isBatchMode() {return holder.isBatchMode();}
}
