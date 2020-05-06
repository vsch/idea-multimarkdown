// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.settings;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.textCompletion.DefaultTextCompletionValueDescriptor;
import com.intellij.util.textCompletion.TextCompletionValueDescriptor;
import com.intellij.util.textCompletion.TextFieldWithCompletion;
import com.intellij.util.textCompletion.ValuesCompletionProvider;
import com.vladsch.flexmark.util.misc.Pair;
import com.vladsch.md.nav.editor.util.HtmlPanelProvider;
import com.vladsch.md.nav.settings.MdRenderingProfileHolder;
import com.vladsch.md.nav.settings.MdSettableFormBase;
import com.vladsch.md.nav.settings.RenderingProfileSynchronizer;
import com.vladsch.md.nav.settings.api.SettingsFormImpl;
import com.vladsch.plugin.util.ui.Settable;
import com.vladsch.plugin.util.ui.SettableForm;
import com.vladsch.plugin.util.ui.SettingsComponents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FlexmarkHtmlSettingsForm extends SettingsFormImpl {
    public static final String FLEXMARK_AST = FlexmarkHtmlSettings.FLEXMARK_AST_LANGUAGE_NAME;

    private JPanel myMainPanel;
    JComboBox<String> myFlexmarkSpecExampleRendering;
    JBCheckBox myFlexmarkSpecExampleRenderHtml;

    TextFieldWithCompletion myFlexmarkSectionLanguageForSource;
    TextFieldWithCompletion myFlexmarkSectionLanguageForHtml;
    TextFieldWithCompletion myFlexmarkSectionLanguageForAst;

    JBCheckBox myFlexmarkSectionLanguageAstForSource;
    JBCheckBox myFlexmarkSectionLanguageAstForHtml;
    JBCheckBox myFlexmarkSectionLanguageAstForAst;

    JBCheckBox myFlexmarkAstOffsetsSourceForHtml;
    JBCheckBox myFlexmarkAstOffsetsSourceForAst;

    JBCheckBox myFlexmarkAstOffsetsHtmlForSource;
    JBCheckBox myFlexmarkAstOffsetsHtmlForAst;

    JBCheckBox myFlexmarkAstOffsetsAstForSource;
    JBCheckBox myFlexmarkAstOffsetsAstForHtml;

    private TextCompletionValueDescriptor<Language> myTextCompletionValueDescriptor = null;
    private ValuesCompletionProvider<Language> myValuesCompletionProvider = null;

    @NotNull
    public JComponent getComponent() {
        return myMainPanel;
    }

    static class SectionInfo {
        final int sectionIndex;
        final @NotNull TextFieldWithCompletion texField;
        final @NotNull JBCheckBox isAstCheckbox;
        final @Nullable JBCheckBox offsetsFor_1;
        final @Nullable JBCheckBox offsetsFor_2;
        final @Nullable JBCheckBox offsetsFor_3;

        public SectionInfo(int sectionIndex, @NotNull TextFieldWithCompletion texField, @NotNull JBCheckBox isAstCheckbox, @Nullable JBCheckBox offsetsFor_1, @Nullable JBCheckBox offsetsFor_2, @Nullable JBCheckBox offsetsFor_3) {
            this.sectionIndex = sectionIndex;
            this.texField = texField;
            this.isAstCheckbox = isAstCheckbox;
            this.offsetsFor_1 = offsetsFor_1;
            this.offsetsFor_2 = offsetsFor_2;
            this.offsetsFor_3 = offsetsFor_3;
        }
    }

    private final SettingsComponents<FlexmarkHtmlSettings> components;
    HashMap<Integer, SectionInfo> mySectionInfoMap = new HashMap<Integer, SectionInfo>();

    public FlexmarkHtmlSettingsForm(RenderingProfileSynchronizer profileSynchronizer) {
        super(profileSynchronizer);

        mySectionInfoMap.put(1, new SectionInfo(1, myFlexmarkSectionLanguageForSource, myFlexmarkSectionLanguageAstForSource, null, myFlexmarkAstOffsetsSourceForHtml, myFlexmarkAstOffsetsSourceForAst));
        mySectionInfoMap.put(2, new SectionInfo(2, myFlexmarkSectionLanguageForHtml, myFlexmarkSectionLanguageAstForHtml, myFlexmarkAstOffsetsHtmlForSource, null, myFlexmarkAstOffsetsHtmlForAst));
        mySectionInfoMap.put(3, new SectionInfo(3, myFlexmarkSectionLanguageForAst, myFlexmarkSectionLanguageAstForAst, myFlexmarkAstOffsetsAstForSource, myFlexmarkAstOffsetsAstForHtml, null));

        components = new SettingsComponents<FlexmarkHtmlSettings>() {
            @Override
            protected Settable<FlexmarkHtmlSettings>[] createComponents(@NotNull FlexmarkHtmlSettings i) {
                //noinspection unchecked
                return new Settable[] {
                        notrace("FlexmarkSpecExampleRenderHtml", component(myFlexmarkSpecExampleRenderHtml, i::getFlexmarkSpecExampleRenderHtml, i::setFlexmarkSpecExampleRenderHtml)),
                        notrace("SpecExampleRendering", component(FlexmarkSpecExampleRenderingType.ADAPTER, myFlexmarkSpecExampleRendering, i::getFlexmarkSpecExampleRendering, i::setFlexmarkSpecExampleRendering)),
                        component(new SettableForm<FlexmarkHtmlSettings>() {
                            @Override
                            public void reset(@NotNull final FlexmarkHtmlSettings settings) {
                                resetSectionLanguage(1, settings);
                                resetSectionLanguage(2, settings);
                                resetSectionLanguage(3, settings);
                            }

                            @Override
                            public void apply(@NotNull final FlexmarkHtmlSettings settings) {
                                applySectionLanguage(1, settings);
                                applySectionLanguage(2, settings);
                                applySectionLanguage(3, settings);
                            }

                            @Override
                            public boolean isModified(@NotNull final FlexmarkHtmlSettings settings) {
                                return isModifiedSectionLanguage(1, settings)
                                        || isModifiedSectionLanguage(2, settings)
                                        || isModifiedSectionLanguage(3, settings)
                                        ;
                            }
                        }, i),
                };
            }
        };

        myFlexmarkSectionLanguageAstForSource.addActionListener(e -> updateOptionalSettings());
        myFlexmarkSectionLanguageAstForHtml.addActionListener(e -> updateOptionalSettings());
        myFlexmarkSectionLanguageAstForAst.addActionListener(e -> updateOptionalSettings());
        myFlexmarkAstOffsetsSourceForHtml.addActionListener(e -> updateOptionalSettings());
        myFlexmarkAstOffsetsSourceForAst.addActionListener(e -> updateOptionalSettings());
        myFlexmarkAstOffsetsHtmlForSource.addActionListener(e -> updateOptionalSettings());
        myFlexmarkAstOffsetsHtmlForAst.addActionListener(e -> updateOptionalSettings());
        myFlexmarkAstOffsetsAstForSource.addActionListener(e -> updateOptionalSettings());
        myFlexmarkAstOffsetsAstForHtml.addActionListener(e -> updateOptionalSettings());

        onFormCreated();
    }

    Pair<String, Set<Integer>> getUiLanguageSections(int sectionIndex) {
        SectionInfo sectionInfo = mySectionInfoMap.get(sectionIndex);
        TextFieldWithCompletion sectionLanguageField = sectionInfo.texField;
        JBCheckBox isAstCheckbox = sectionInfo.isAstCheckbox;
        JBCheckBox offsetsFor_1 = sectionInfo.offsetsFor_1;
        JBCheckBox offsetsFor_2 = sectionInfo.offsetsFor_2;
        JBCheckBox offsetsFor_3 = sectionInfo.offsetsFor_3;

        HashSet<Integer> uiForSections = new HashSet<>();
        if (offsetsFor_1 != null && offsetsFor_1.isSelected()) uiForSections.add(1);
        if (offsetsFor_2 != null && offsetsFor_2.isSelected()) uiForSections.add(2);
        if (offsetsFor_3 != null && offsetsFor_3.isSelected()) uiForSections.add(3);

        return Pair.of(isAstCheckbox.isSelected() ? FLEXMARK_AST : sectionLanguageField.getText().trim(), uiForSections);
    }

    static String getLanguageSectionString(String language, Collection<Integer> sections) {
        if (!language.equals(FLEXMARK_AST)) return language;

        StringBuilder sb = new StringBuilder();
        sb.append(language);
        sb.append(':');
        ArrayList<Integer> indices = new ArrayList<>(sections);
        indices.sort(Comparator.comparing(o -> o));
        String sep = "";
        for (int index : indices) {
            sb.append(sep);
            sep = ",";
            sb.append(index);
        }
        return sb.toString();
    }

    boolean isModifiedSectionLanguage(int sectionIndex, @NotNull FlexmarkHtmlSettings settings) {
        Pair<String, Set<Integer>> languageSections = FlexmarkHtmlSettings.languageSections(settings.getFlexmarkSectionLanguages().get(sectionIndex));
        Pair<String, Set<Integer>> uiSections = getUiLanguageSections(sectionIndex);

        return !languageSections.equals(uiSections);
    }

    void applySectionLanguage(int sectionIndex, @NotNull FlexmarkHtmlSettings settings) {
        HashMap<Integer, String> sectionLanguages = settings.getFlexmarkSectionLanguages();
        Pair<String, Set<Integer>> uiSections = getUiLanguageSections(sectionIndex);
        sectionLanguages.put(sectionIndex, getLanguageSectionString(uiSections.getFirst(), uiSections.getSecond()));
    }

    void resetSectionLanguage(int sectionIndex, @NotNull FlexmarkHtmlSettings settings) {
        Pair<String, Set<Integer>> languageSections = FlexmarkHtmlSettings.languageSections(settings.getFlexmarkSectionLanguages().get(sectionIndex));
        boolean isAst = languageSections.getFirst().equals(FLEXMARK_AST);

        SectionInfo sectionInfo = mySectionInfoMap.get(sectionIndex);
        TextFieldWithCompletion sectionLanguageField = sectionInfo.texField;
        JBCheckBox isAstCheckbox = sectionInfo.isAstCheckbox;
        JBCheckBox offsetsFor_1 = sectionInfo.offsetsFor_1;
        JBCheckBox offsetsFor_2 = sectionInfo.offsetsFor_2;
        JBCheckBox offsetsFor_3 = sectionInfo.offsetsFor_3;

        sectionLanguageField.setText(isAst ? "" : languageSections.getFirst());
        MdSettableFormBase.updateEditorTextFieldEditable(sectionLanguageField, !isAst);

        isAstCheckbox.setSelected(isAst);
        if (offsetsFor_1 != null) offsetsFor_1.setSelected(languageSections.getSecond().contains(1));
        if (offsetsFor_2 != null) offsetsFor_2.setSelected(languageSections.getSecond().contains(2));
        if (offsetsFor_3 != null) offsetsFor_3.setSelected(languageSections.getSecond().contains(3));
    }

    @Override
    public void updateOptionalSettings() {
        //HtmlGeneratorProvider panelProvider = HtmlGeneratorProvider.Companion.getFromInfoOrDefault(myLastPanelProviderInfo);

        int iMax = 3;
        for (int i = 0; i < iMax; i++) {
            SectionInfo sectionInfo = mySectionInfoMap.get(i + 1);
            TextFieldWithCompletion sectionLanguageField = sectionInfo.texField;
            JBCheckBox isAstCheckbox = sectionInfo.isAstCheckbox;
            JBCheckBox offsetsFor_1 = sectionInfo.offsetsFor_1;
            JBCheckBox offsetsFor_2 = sectionInfo.offsetsFor_2;
            JBCheckBox offsetsFor_3 = sectionInfo.offsetsFor_3;

            MdSettableFormBase.updateEditorTextFieldEditable(sectionLanguageField, !isAstCheckbox.isSelected());

            if (offsetsFor_1 != null) offsetsFor_1.setEnabled(isAstCheckbox.isSelected());
            if (offsetsFor_2 != null) offsetsFor_2.setEnabled(isAstCheckbox.isSelected());
            if (offsetsFor_3 != null) offsetsFor_3.setEnabled(isAstCheckbox.isSelected());
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void updateValueProvider(String currentText) {
        if (myValuesCompletionProvider == null) {
            Collection<Language> languages = Language.getRegisteredLanguages();
            HashSet<Language> languageList = new HashSet<>();
            for (Language language : languages) {
                if (!language.getDisplayName().trim().isEmpty()) {
                    languageList.add(language);
                }
            }

            if (myTextCompletionValueDescriptor == null) {
                myTextCompletionValueDescriptor = new DefaultTextCompletionValueDescriptor<Language>() {
                    @NotNull
                    @Override
                    protected String getLookupString(@NotNull final Language item) {
                        return item.getDisplayName();
                    }

                    @Nullable
                    @Override
                    protected Icon getIcon(@NotNull final Language item) {
                        LanguageFileType fileType = item.getAssociatedFileType();
                        return fileType == null ? null : fileType.getIcon();
                    }
                };
            }

            myValuesCompletionProvider = new ValuesCompletionProvider<>(myTextCompletionValueDescriptor, Collections.emptyList(), languageList, false);
        }
    }

    private void createUIComponents() {
        updateValueProvider("");

        myFlexmarkSectionLanguageForSource = new TextFieldWithCompletion(
                ProjectManager.getInstance().getDefaultProject(),
                myValuesCompletionProvider, "", true, true, true, true);

        myFlexmarkSectionLanguageForHtml = new TextFieldWithCompletion(
                ProjectManager.getInstance().getDefaultProject(),
                myValuesCompletionProvider, "", true, true, true, true);

        myFlexmarkSectionLanguageForAst = new TextFieldWithCompletion(
                ProjectManager.getInstance().getDefaultProject(),
                myValuesCompletionProvider, "", true, true, true, true);

        ActionListener updateListener = e -> updateFormOnReshow(false);

        myFlexmarkSpecExampleRendering = FlexmarkSpecExampleRenderingType.ADAPTER.createComboBox();
        myFlexmarkSpecExampleRendering.addActionListener(updateListener);
    }

    @Override
    protected void updatePanelProviderDependentComponents(@NotNull HtmlPanelProvider fromProvider, @NotNull HtmlPanelProvider toProvider, boolean isInitialShow) {

    }

    public void updateFormOnReshow(boolean isInitialShow) {
        myFlexmarkSpecExampleRenderHtml.setEnabled(!FlexmarkSpecExampleRenderingType.ADAPTER.get(myFlexmarkSpecExampleRendering).isDefault());
    }

    @Override
    protected JPanel getMainFormPanel() {
        return myMainPanel;
    }

    @Override
    public void reset(@NotNull final MdRenderingProfileHolder settings) {
        components.reset(settings.getHtmlSettings().getExtension(FlexmarkHtmlSettings.KEY));
    }

    @Override
    public void apply(@NotNull final MdRenderingProfileHolder settings) {
        components.apply(settings.getHtmlSettings().getExtension(FlexmarkHtmlSettings.KEY));
    }

    @Override
    public boolean isModified(@NotNull final MdRenderingProfileHolder settings) {
        return components.isModified(settings.getHtmlSettings().getExtension(FlexmarkHtmlSettings.KEY));
    }

    @Override
    protected void disposeResources() {

    }
}
