// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.psi.element.MdFileStub;
import com.vladsch.md.nav.psi.util.MdTokenSets;
import com.vladsch.md.nav.settings.MdParserSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.MdRenderingProfileManager;
import com.vladsch.plugin.util.LazyComputable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static com.vladsch.md.nav.psi.util.MdTypes.*;

public class MdParserDefinition implements ParserDefinition {
    final public static boolean WANT_PROFILE_TRACE = false;
    public static final IStubFileElementType<MdFileStub> MARKDOWN_FILE = MdFileElementType.INSTANCE;

    private final @Nullable VirtualFile myVirtualFile;

    public MdParserDefinition() {
        this((VirtualFile) null);
    }

    private MdParserDefinition(@Nullable PsiFile psiFile) {
        VirtualFile virtualFile = null;

        if (psiFile != null) {
            virtualFile = psiFile.getOriginalFile().getVirtualFile();

            if (virtualFile == null) {
                // parse contents light will have virtual file null but view provider virtual file will be light virtual which profile manager can handle
                virtualFile = psiFile.getViewProvider().getVirtualFile();
            }
        }

        myVirtualFile = virtualFile;
    }

    private MdParserDefinition(@Nullable VirtualFile virtualFile) {
        myVirtualFile = virtualFile;
    }

    public static MdParserDefinition definitionFor(@Nullable PsiFile psiFile) {
        return new MdParserDefinition(psiFile);
    }

    public static MdParserDefinition definitionFor(@Nullable VirtualFile virtualFile) {
        return new MdParserDefinition(virtualFile);
    }

    public static final TokenSet LINE_BREAK_BEFORE = TokenSet.create(
            REFERENCE_TEXT_OPEN,
            FOOTNOTE_OPEN,
            ABBREVIATION,
            HEADER_ATX_MARKER,
            TOC_OPEN,
            ORDERED_LIST,
            BULLET_LIST,
            HRULE_TEXT
    );

    public static final TokenSet LINE_BREAK_AFTER = TokenSet.create(
            SETEXT_HEADER,
            HEADER_SETEXT_MARKER,
            HEADER_TEXT,
            TOC_CLOSE,
            //FOOTNOTE_END,
            REFERENCE_LINK_REF,
            DEFINITION_TERM,
            DEFINITION_MARKER,
            ABBREVIATION,
            BULLET_LIST_ITEM,
            ORDERED_LIST_ITEM,
            HRULE_TEXT
    );

    public static final TokenSet HAS_OWN_BREAK_AFTER = TokenSet.create(
            REFERENCE_END
    );

    @Override
    public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        MdRenderingProfile renderingProfile;
        if (myVirtualFile == null) {
            renderingProfile = MdRenderingProfileManager.getInstance(project).getDefaultRenderingProfile();
        } else {
            if (WANT_PROFILE_TRACE && myVirtualFile.getName().equals("README.md")) {
                int tmp = 0;
            }

            renderingProfile = MdRenderingProfileManager.getProfile(project, myVirtualFile);

            if (WANT_PROFILE_TRACE && myVirtualFile.getName().equals("README.md") && renderingProfile.getProfileName().isEmpty()) {
                int tmp = 0;
            }
        }

        MdParserSettings parserSettings = renderingProfile.getParserSettings();
        int pegdownExtensionFlags = parserSettings.getPegdownFlags();
        long parserOptionsFlags = parserSettings.getOptionsFlags();
        return new MdLexer(renderingProfile, pegdownExtensionFlags, parserOptionsFlags);
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        // none, we need all of them in the tree
        return TokenSet.EMPTY;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return MdTokenSets.COMMENT_FOR_SYNTAX_SET;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new MdParserAdapter();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return MARKDOWN_FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new MdFile(viewProvider);
    }

    final private static LazyComputable<MdTypeFactoryRegistryImpl> ourFactory = new LazyComputable<>(MdTypeFactoryRegistryImpl::new);

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return ourFactory.getValue().createElement(node);
    }

    public static boolean isClassFactoryRegistered(Class<? extends PsiElement> klass) {
        return ourFactory.getValue().isTypeFactoryDefined(klass);
    }

    @NotNull
    public static <K extends PsiElement> Class<K> getCurrentFactoryClass(@NotNull Class<? extends K> klass) {
        return ourFactory.getValue().getCurrentFactoryClass(klass);
    }

    @Nullable
    public static Class<?> getCurrentFactoryClassOrNull(@NotNull Class<?> klass) {
        return ourFactory.getValue().getCurrentFactoryClassOrNull(klass);
    }

    @NotNull
    public static <T> Set<Class<T>> getAllTypeFactoryElementsFor(@NotNull Class<T> klass) {
        return ourFactory.getValue().getAllTypeFactoryElementsFor(klass);
    }
}
