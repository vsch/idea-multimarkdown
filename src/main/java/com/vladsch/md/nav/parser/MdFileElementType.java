// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilderFactory;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.ILightStubFileElementType;
import com.intellij.util.diff.FlyweightCapableTreeStructure;
import com.vladsch.flexmark.util.misc.Utils;
import com.vladsch.md.nav.MdLanguage;
import com.vladsch.md.nav.psi.element.MdFileStub;
import com.vladsch.plugin.util.TestUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdFileElementType extends ILightStubFileElementType<MdFileStub> {
    public static final MdFileElementType INSTANCE = new MdFileElementType();

    public static final int MD_INDEX_VERSION = 34;

    private MdFileElementType() {
        super("psi.MdFile", MdLanguage.INSTANCE);
    }

    @Override
    public int getStubVersion() {
        return MD_INDEX_VERSION;
    }

    @Override
    public FlyweightCapableTreeStructure<LighterASTNode> parseContentsLight(final ASTNode chameleon) {
        PsiElement psi = chameleon.getPsi();
        assert (psi != null) : ("Bad chameleon: " + chameleon);
        assert (psi instanceof PsiFile) : ("Bad File chameleon: " + chameleon);
        boolean wantProfileTrace = false;

        if (MdParserDefinition.WANT_PROFILE_TRACE && chameleon.getText().indexOf(TestUtils.DUMMY_IDENTIFIER_CHAR) != -1) {
            wantProfileTrace = true;
        }

        Project project = psi.getProject();
        MdParserDefinition parserDefinition = MdParserDefinition.definitionFor((PsiFile) psi);
        Lexer lexer = parserDefinition.createLexer(project);

        if (wantProfileTrace && lexer instanceof MdLexer) {
            MdLexer mdLexer = (MdLexer) lexer;
            if (mdLexer.renderingProfile.getProfileName().isEmpty()) {
                int tmp = 0;
            }
            System.out.printf("Using profile: '%s' for injected element '%s'", mdLexer.renderingProfile.getProfileName(), Utils.escapeJavaString(chameleon.getText()));
        }

        PsiBuilderFactory factory = PsiBuilderFactory.getInstance();
        PsiBuilder builder = factory.createBuilder(parserDefinition, lexer, chameleon.getText());

        MdParserAdapter parser = new MdParserAdapter();
        return parser.parseLightStub(this, builder);
    }

    @Nullable
    @Override
    public ASTNode parseContents(@NotNull final ASTNode chameleon) {
        return super.parseContents(chameleon);
    }

    @Override
    protected ASTNode doParseContents(@NotNull final ASTNode chameleon, @NotNull final PsiElement psi) {
        Project project = psi.getProject();
        boolean wantProfileTrace = false;

        if (MdParserDefinition.WANT_PROFILE_TRACE && chameleon.getText().indexOf(TestUtils.DUMMY_IDENTIFIER_CHAR) != -1) {
            wantProfileTrace = true;
        }

        MdParserDefinition parserDefinition = MdParserDefinition.definitionFor(psi.getContainingFile());
        Lexer lexer = parserDefinition.createLexer(project);

        if (wantProfileTrace && lexer instanceof MdLexer) {
            MdLexer mdLexer = (MdLexer) lexer;
            if (mdLexer.renderingProfile.getProfileName().isEmpty()) {
                int tmp = 0;
            }
            System.out.printf("Using profile: '%s' for injected element '%s'", mdLexer.renderingProfile.getProfileName(), Utils.escapeJavaString(chameleon.getText()));
        }

        PsiBuilderFactory factory = PsiBuilderFactory.getInstance();
        PsiBuilder builder = factory.createBuilder(parserDefinition, lexer, chameleon.getText());

        MdParserAdapter parser = new MdParserAdapter();
        ASTNode node = parser.parse(this, builder);
        return node.getFirstChildNode();
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "psi.MdFile";
    }

//    @Override
//    public void serialize(@NotNull PsiJavaFileStub stub, @NotNull StubOutputStream dataStream) throws IOException {
//        dataStream.writeBoolean(stub.isCompiled());
//        LanguageLevel level = stub.getLanguageLevel();
//        dataStream.writeByte(level != null ? level.ordinal() : -1);
//        dataStream.writeName(stub.getPackageName());
//    }
//
//    @NotNull
//    @Override
//    public PsiJavaFileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
//        boolean compiled = dataStream.readBoolean();
//        int level = dataStream.readByte();
//        String packageName = dataStream.readNameString();
//        return new PsiJavaFileStubImpl(null, packageName, level >= 0 ? LanguageLevel.values()[level] : null, compiled);
//    }
//
//    @Override
//    public void indexStub(@NotNull MdFileStub stub, @NotNull IndexSink sink) { }
}
