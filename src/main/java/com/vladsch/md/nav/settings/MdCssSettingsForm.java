// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.util.SystemInfoRt;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.containers.ContainerUtil;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.editor.util.HtmlCssResourceProvider;
import com.vladsch.md.nav.editor.util.HtmlPanelProvider;
import com.vladsch.md.nav.editor.util.HtmlScriptResourceProvider;
import com.vladsch.md.nav.settings.MdCssSettings.PreviewScheme;
import com.vladsch.md.nav.settings.api.SettingsFormImpl;
import com.vladsch.md.nav.util.MdCancelableJobScheduler;
import com.vladsch.md.nav.util.PathInfo;
import com.vladsch.plugin.util.AwtRunnable;
import com.vladsch.plugin.util.CancellableRunnable;
import com.vladsch.plugin.util.ui.Helpers;
import com.vladsch.plugin.util.ui.Settable;
import com.vladsch.plugin.util.ui.SettingsComponents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.table.TableCellEditor;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MdCssSettingsForm extends SettingsFormImpl implements SearchableForm {
    private JPanel myMainPanel;
    JBCheckBox myCssUriEnabled;
    JBCheckBox myCssUriSerial;
    TextFieldWithHistoryWithBrowseButton myCssUri;
    JBCheckBox myCssTextEnabled;
    ComboBox<PreviewScheme> myPreviewScheme;
    ComboBox<HtmlCssResourceProvider.Info> myCssProviders;
    CustomizableEditorTextField myCssText;

    private JPanel myTableJPanel;
    private JLabel myDynamicPageWidthLabel;
    JBCheckBox myDynamicPageWidth;
    private HyperlinkLabel myCssUriErrorLabel;
    @SuppressWarnings("unused") private JPanel myExtensionsPanel;

    ScriptsTable myScriptsTable;
    ScriptsTableModel myScriptsTableModel;
    CancellableRunnable myCssUriValidator = CancellableRunnable.NULL;

    @NotNull private final ActionListener myUpdateListener;

    @NotNull CollectionComboBoxModel<PreviewScheme> myPreviewSchemeModel = getPreviewSchemeModel();
    @NotNull CollectionComboBoxModel<HtmlCssResourceProvider.Info> myCssProvidersModel = getCssProvidersModel(getPanelProvider());
    @NotNull CollectionListModel<HtmlScriptResourceProvider.Info> myScriptProvidersModel = getScriptProvidersModel();

    @NotNull MdCssSettings.PreviewScheme myPreviewSchemeLastItem = PreviewScheme.UI_SCHEME;
    @NotNull HtmlCssResourceProvider.Info myCssProviderLastItem = MdCssSettings.DEFAULT.getCssProviderInfo();

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @NotNull
    public JComponent getComponent() {
        return myMainPanel;
    }

    private final SettingsComponents<MdCssSettings> components;

    public MdCssSettingsForm(RenderingProfileSynchronizer profileSynchronizer) {
        super(profileSynchronizer);

        components = new SettingsComponents<MdCssSettings>() {
            @Override
            protected Settable<MdCssSettings>[] createComponents(@NotNull MdCssSettings i) {
                JComponentGetter<CustomizableEditorTextField, String> textEditorGetter = component -> getEditorTextFieldText(component);
                JComponentSetter<CustomizableEditorTextField, String> textEditorSetter = (component, value) -> updateEditorTextFieldText(component, value);

                //noinspection unchecked
                return new Settable[] {
                        component(myCssUriEnabled, i::isCssUriEnabled, i::setCssUriEnabled),
                        component(myCssUriSerial, i::isCssUriSerial, i::setCssUriSerial),
                        component(myCssTextEnabled, i::isCssTextEnabled, i::setCssTextEnabled),
                        component(myDynamicPageWidth, i::isDynamicPageWidth, i::setDynamicPageWidth),

                        component(myCssUri, i::getCssUriHistoryList, i::setCssUriHistoryList, i::getCssUri, i::setCssUri),

                        new Settable<ComboBox<HtmlCssResourceProvider.Info>>() {
                            @Override
                            public void reset() {
                                myCssProviderLastItem = i.getCssProviderInfo();
                                myCssProviders.setSelectedItem(myCssProviderLastItem);
                            }

                            @Override
                            public void apply() {
                                i.setCssProviderInfo(myCssProviderLastItem);
                            }

                            @Override
                            public boolean isModified() {
                                return !Objects.equals(i.getCssProviderInfo(), myCssProviderLastItem);
                            }

                            @Override
                            public ComboBox<HtmlCssResourceProvider.Info> getComponent() {
                                return myCssProviders;
                            }
                        },

                        new Settable<ScriptsTable>() {
                            @Override
                            public void reset() {
                                myScriptsTableModel.setSelected(i.getHtmlScriptProvidersInfo());
                            }

                            @Override
                            public void apply() {
                                i.setHtmlScriptProvidersInfo(myScriptsTableModel.getEnabledScripts());
                            }

                            @Override
                            public boolean isModified() {
                                return !i.scriptProvidersEquals(myScriptsTableModel.getEnabledScripts());
                            }

                            @Override
                            public ScriptsTable getComponent() {
                                return myScriptsTable;
                            }
                        },

                        new Settable<ComboBox<PreviewScheme>>() {
                            @Override
                            public void reset() {
                                myPreviewSchemeLastItem = i.getPreviewScheme();
                                myPreviewScheme.setSelectedItem(myPreviewSchemeLastItem);
                            }

                            @Override
                            public void apply() {
                                i.setPreviewScheme(myPreviewSchemeLastItem);
                            }

                            @Override
                            public boolean isModified() {
                                return !Objects.equals(i.getPreviewScheme(), myPreviewSchemeLastItem);
                            }

                            @Override
                            public ComboBox<PreviewScheme> getComponent() {
                                return myPreviewScheme;
                            }
                        },

                        component(myCssText, textEditorGetter, textEditorSetter, i::getCssText, i::setCssText)
                };
            }
        };

        myCssProvidersModel = getCssProvidersModel(getPanelProvider());

        myUpdateListener = e -> {
            myCssUri.setEnabled(myCssUriEnabled.isSelected());
            myCssUriSerial.setEnabled(myCssUriEnabled.isSelected() && PathInfo.isLocal(myCssUri.getText()));
            updateFormOnReshow(false);
            updateCssUriError();
        };

        myCssUriEnabled.addActionListener(myUpdateListener);

        myCssUri.addBrowseFolderListener(MdBundle.message("debug.test-file-directory.title"), null, myProject,
                new FileChooserDescriptor(true, false, false, false, false, false),
                new TextComponentAccessor<TextFieldWithHistory>() {
                    @Override
                    public String getText(final TextFieldWithHistory component) {
                        return myCssUri.getText();
                    }

                    @Override
                    public void setText(final TextFieldWithHistory component, @NotNull final String text) {
                        myCssUri.setText(PathInfo.prefixWithFileURI(PathInfo.removeFileUriPrefix(text)));
                    }
                }
        );

        myCssUri.getChildComponent().getTextEditor().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull final DocumentEvent event) {
                myCssUriSerial.setEnabled(myCssUriEnabled.isSelected() && PathInfo.isLocal(myCssUri.getText()));
                updateFormOnReshow(false);
                updateCssUriError();
            }
        });

        // add prefix
        myCssUriErrorLabel.addHyperlinkListener(e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                myCssUri.setText(PathInfo.prefixWithFileURI(myCssUri.getText()));
            }
        });

        myCssUriErrorLabel.setVisible(false);

        myCssTextEnabled.addActionListener(myUpdateListener);

        onFormCreated();
        updateOptionalSettings();
    }

    @Nullable
    public static String validateCssUri(String cssUri) {
        if (!cssUri.trim().isEmpty()) {
            PathInfo cssUriInfo = new PathInfo(cssUri);

            boolean customURI = cssUriInfo.isCustomURI() && !(SystemInfoRt.isWindows && PathInfo.isWindowsPathURI(cssUri));
            if (cssUriInfo.isFileURI() || !customURI) {
                File cssUriFile = new File(PathInfo.removeFileUriPrefix(cssUriInfo.getFilePathNoQuery()));
                if (!cssUriFile.exists()) {
                    return MdBundle.message("settings.markdown.css.uri.error.does-not-exist");
                } else if (!cssUriFile.isFile()) {
                    return MdBundle.message("settings.markdown.css.uri.error.not-file");
                } else if (!customURI) {
                    return MdBundle.message("settings.markdown.css.uri.error.0.missing-protocol", PathInfo.fileURIPrefix(cssUri));
                }
            }
        }
        return null;
    }

    void updateCssUriError() {
        myCssUriValidator.cancel();
        String cssUri = myCssUri.getText();
        if (cssUri.trim().isEmpty()) {
            myCssUriErrorLabel.setVisible(false);
        } else {
            myCssUriValidator = AwtRunnable.schedule(MdCancelableJobScheduler.getInstance(), "Css URI Validator", 250, ModalityState.any(), () -> {
                boolean errorVisible = false;
                if (myCssUriEnabled.isSelected()) {
                    String error = validateCssUri(cssUri);
                    errorVisible = error != null;
                    if (errorVisible) {
                        String fixMessage = MdBundle.message("settings.markdown.css.uri.error.missing-protocol.0.fix", PathInfo.fileURIPrefix(cssUri));
                        int pos = error.indexOf(fixMessage);
                        if (pos != -1) {
                            myCssUriErrorLabel.setHyperlinkText(error.substring(0, pos), fixMessage, error.substring(pos + fixMessage.length()));
                        } else {
                            myCssUriErrorLabel.setText(error);
                        }
                        myCssUriErrorLabel.setForeground(Helpers.errorColor());
                    }
                }
                myCssUriErrorLabel.setVisible(errorVisible);
            });
        }
    }

    private void createUIComponents() {
        myExtensionsPanel = getExtensionsPanel();

        myCssText = MdSettableFormBase.createCustomizableTextFieldEditor(new CustomizableEditorTextField.EditorCustomizationListener() {
            @Override
            public boolean editorCreated(@NotNull EditorEx editor, @Nullable Project project) {
                updateFormOnReshow(false);
                return true;
            }

            @Nullable
            @Override
            public EditorHighlighter getHighlighter(Project project, @NotNull FileType fileType, @NotNull EditorColorsScheme settings) {
                return null;
            }
        }, "css", myProject);

        myPreviewSchemeModel = getPreviewSchemeModel();
        assert myPreviewSchemeModel.getSelected() != null;

        myPreviewScheme = new ComboBox<>(myPreviewSchemeModel);
        myPreviewSchemeLastItem = myPreviewSchemeModel.getSelected();
        myPreviewScheme.addItemListener(e -> {
            final Object item = e.getItem();
            if (e.getStateChange() != ItemEvent.SELECTED || !(item instanceof PreviewScheme)) {
                return;
            }

            myPreviewSchemeLastItem = (PreviewScheme) item;
            updateOptionalSettings();
        });

        myCssProvidersModel = getCssProvidersModel(getPanelProvider());
        assert myCssProvidersModel.getSelected() != null;

        myCssProviders = new ComboBox<>(myCssProvidersModel);
        myCssProviderLastItem = myCssProvidersModel.getSelected();
        myCssProviders.addItemListener(e -> {
            final Object item = e.getItem();
            if (e.getStateChange() != ItemEvent.SELECTED || !(item instanceof HtmlCssResourceProvider.Info)) {
                return;
            }

            myCssProviderLastItem = (HtmlCssResourceProvider.Info) item;
            updateOptionalSettings();
        });

        myScriptProvidersModel = getScriptProvidersModel();
        myScriptsTableModel = new ScriptsTableModel(getScriptTableResources());
        myScriptsTable = new ScriptsTable(myScriptsTableModel);
        myScriptsTable.setModel(myScriptsTableModel);

        myTableJPanel = new JPanel(new BorderLayout());
        TableCellEditor cellEditor = myScriptsTable.getDefaultEditor(Boolean.class);
        cellEditor.addCellEditorListener(new CellEditorListener() {
            public void editingStopped(ChangeEvent e) {
                myScriptsTableModel.fireTableDataChanged();
            }

            public void editingCanceled(ChangeEvent e) {
                myScriptsTableModel.fireTableDataChanged();
            }
        });
        //myTableJPanel.add(myScriptsJBTable.getTableHeader(), BorderLayout.PAGE_START);
        myTableJPanel.add(myScriptsTable, BorderLayout.CENTER);
        //myTableJPanel.setMinimumSize(myScriptsJBTable.getMinimumSize());

        myExtensionsPanel = getExtensionsPanel();
    }

    @NotNull
    private ArrayList<ScriptResource> getScriptTableResources() {
        ArrayList<ScriptResource> scripts = new ArrayList<>();
        for (HtmlScriptResourceProvider.Info info : myScriptProvidersModel.getItems()) {
            HtmlScriptResourceProvider scriptResourceProvider = HtmlScriptResourceProvider.getFromId(info.getProviderId());
            if (scriptResourceProvider != null) {
                boolean forAvailable = scriptResourceProvider.getCOMPATIBILITY().isForAvailable(getPanelProvider().getCOMPATIBILITY());
                scripts.add(new ScriptResource(forAvailable, false, info));
            }
        }
        return scripts;
    }

    @Override
    protected void updatePanelProviderDependentComponents(@NotNull HtmlPanelProvider fromProvider, @NotNull HtmlPanelProvider toProvider, boolean isInitialShow) {
        MdCssSettings cssSettings = new MdCssSettings();
        components.apply(cssSettings);

        cssSettings.changeToProvider(fromProvider.getINFO(), toProvider.getINFO());
        myLastPanelProviderInfo = toProvider.getINFO();
        myScriptsTableModel.setScripts(getScriptTableResources());
        myScriptsTable.setModel(myScriptsTableModel);

        components.reset(cssSettings);
    }

    public void updateFormOnReshow(boolean isInitialShow) {
        final boolean canEdit = myCssTextEnabled.isSelected();
        MdSettableFormBase.updateEditorTextFieldEditable(myCssText, canEdit);
    }

    @Override
    protected JPanel getMainFormPanel() {
        return myMainPanel;
    }

    @NotNull
    private CollectionComboBoxModel<HtmlCssResourceProvider.Info> getCssProvidersModel(@NotNull HtmlPanelProvider provider) {
        List<HtmlCssResourceProvider.Info> cssProviderInfos = getCompatibleCssProvidersInfo(provider);
        return new CollectionComboBoxModel<>(cssProviderInfos, cssProviderInfos.get(0));
    }

    @Override
    public void updateOptionalSettings() {
        final HtmlCssResourceProvider provider = HtmlCssResourceProvider.Companion.getFromInfoOrDefault(myCssProviderLastItem);
        myDynamicPageWidth.setEnabled(provider.isSupportedSetting(MdCssSettings.DYNAMIC_PAGE_WIDTH));
        myDynamicPageWidthLabel.setEnabled(provider.isSupportedSetting(MdCssSettings.DYNAMIC_PAGE_WIDTH));
        updateCssUriError();
        updateExtensionsOptionalSettings();
    }

    @NotNull
    private CollectionComboBoxModel<PreviewScheme> getPreviewSchemeModel() {
        return new CollectionComboBoxModel<>(Arrays.asList(PreviewScheme.values()));
    }

    @NotNull
    private List<HtmlCssResourceProvider.Info> getCompatibleCssProvidersInfo(@NotNull HtmlPanelProvider provider) {
        HtmlCssResourceProvider[] extensions = HtmlCssResourceProvider.Companion.getEP_NAME().getExtensions();
        return ContainerUtil.mapNotNull(extensions,
                provider1 -> {
                    if (provider1.getHAS_PARENT()) {
                        // dedicated CSS to another provider, not for generic panel use
                        return null;
                    }
                    if (!provider1.getCOMPATIBILITY().isForAvailable(provider.getCOMPATIBILITY())) {
                        // not compatible with current browser
                        return null;
                    }
                    return provider1.getINFO();
                });
    }

    @NotNull
    private CollectionListModel<HtmlScriptResourceProvider.Info> getScriptProvidersModel() {
        List<HtmlScriptResourceProvider.Info> scriptProvidersInfo = getScriptProvidersInfo();
        return new CollectionListModel<>(scriptProvidersInfo);
    }

    @NotNull
    private List<HtmlScriptResourceProvider.Info> getScriptProvidersInfo() {
        HtmlScriptResourceProvider[] extensions = HtmlScriptResourceProvider.Companion.getEP_NAME().getExtensions();
        return ContainerUtil.mapNotNull(extensions, provider -> {
            if (provider.getHAS_PARENT()) {
                // dedicated Script to another provider, not for generic panel use
                return null;
            }
            return provider.getINFO();
        });
    }

    @Override
    public void reset(@NotNull final MdRenderingProfileHolder settings) {
        components.reset(settings.getCssSettings());
        resetExtensions(settings);

        if (!myInitialShow) {
            // if already initialized then we update, otherwise the first show will do it
            ApplicationManager.getApplication().invokeLater(() -> updateFormOnReshow(false));
        }

        updateOptionalSettings();
        myUpdateListener.actionPerformed(null);
    }

    @Override
    public void apply(@NotNull final MdRenderingProfileHolder settings) {
        components.apply(settings.getCssSettings());
        applyExtensions(settings);
    }

    @Override
    public boolean isModified(@NotNull final MdRenderingProfileHolder settings) {
        return components.isModified(settings.getCssSettings()) || isModifiedExtensions(settings);
    }

    @Override
    protected void onCssSettingsChanged(@NotNull final MdRenderingProfileHolder settings) {
        components.reset(settings.getCssSettings());
        resetExtensions(settings);
    }

    @Override
    protected void disposeResources() {
        myCssText = null;
    }
}
