// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.containers.ContainerUtil;
import com.vladsch.md.nav.editor.resources.TextHtmlGeneratorProvider;
import com.vladsch.md.nav.editor.util.HtmlGeneratorProvider;
import com.vladsch.md.nav.editor.util.HtmlPanelProvider;
import com.vladsch.md.nav.settings.api.SettingsFormImpl;
import com.vladsch.plugin.util.ui.Settable;
import com.vladsch.plugin.util.ui.SettingsComponents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MdHtmlSettingsForm extends SettingsFormImpl {
    private JPanel myMainPanel;
    ComboBox<HtmlGeneratorProvider.Info> myHtmlProviders;
    JBCheckBox myHeadTopEnabled;
    JBCheckBox myHeadBottomEnabled;
    JBCheckBox myBodyTopEnabled;
    JBCheckBox myBodyBottomEnabled;
    JBCheckBox myAddDocTypeHtml;
    CustomizableEditorTextField myHeadTop;
    CustomizableEditorTextField myHeadBottom;
    CustomizableEditorTextField myBodyTop;
    CustomizableEditorTextField myBodyBottom;
    JBCheckBox myAddPageHeader;
    JBCheckBox myAddAnchorLinks;
    JBCheckBox myAnchorLinksWrapText;
    JBCheckBox myImageUriSerials;
    JBCheckBox myNoParaTags;
    JBCheckBox myDefaultUrlTitle;
    @SuppressWarnings("unused") private JPanel myExtensionsPanel;

    @Nullable private ActionListener myUpdateListener;
    @NotNull CollectionComboBoxModel<HtmlGeneratorProvider.Info> myHtmlProvidersModel;
    @NotNull HtmlGeneratorProvider.Info myHtmlProviderLastItem;
    @Nullable private Map<JBCheckBox, CustomizableEditorTextField> checkBoxEditorMap;
    HtmlPanelProvider.Info myLastPanelProviderInfo;
    final boolean myAllowPlainTextHtmlGenerator;

    @NotNull
    public JComponent getComponent() {
        return myMainPanel;
    }

    private final SettingsComponents<MdHtmlSettings> components;

    public MdHtmlSettingsForm(RenderingProfileSynchronizer profileSynchronizer, boolean allowPlainTextHtmlGenerator) {
        super(profileSynchronizer);

        components = new SettingsComponents<MdHtmlSettings>() {
            @Override
            protected Settable<MdHtmlSettings>[] createComponents(@NotNull MdHtmlSettings i) {
                JComponentGetter<CustomizableEditorTextField, String> textEditorGetter = component -> getEditorTextFieldText(component);
                JComponentSetter<CustomizableEditorTextField, String> textEditorSetter = (component, value) -> updateEditorTextFieldText(component, value);

                //noinspection unchecked
                return new Settable[] {
                        notrace("myHeadTopEnabled", component(myHeadTopEnabled, i::getHeadTopEnabled, i::setHeadTopEnabled)),
                        notrace("myHeadBottomEnabled", component(myHeadBottomEnabled, i::getHeadBottomEnabled, i::setHeadBottomEnabled)),
                        notrace("myBodyTopEnabled", component(myBodyTopEnabled, i::getBodyTopEnabled, i::setBodyTopEnabled)),
                        notrace("myBodyBottomEnabled", component(myBodyBottomEnabled, i::getBodyBottomEnabled, i::setBodyBottomEnabled)),
                        notrace("myAddDocTypeHtml", component(myAddDocTypeHtml, i::getAddDocTypeHtml, i::setAddDocTypeHtml)),
                        notrace("myAddPageHeader", component(myAddPageHeader, i::getAddPageHeader, i::setAddPageHeader)),
                        notrace("myAddAnchorLinks", component(myAddAnchorLinks, i::getAddAnchorLinks, i::setAddAnchorLinks)),
                        notrace("myAnchorLinksWrapText", component(myAnchorLinksWrapText, i::getAnchorLinksWrapText, i::setAnchorLinksWrapText)),
                        notrace("myImageUriSerials", component(myImageUriSerials, i::getImageUriSerials, i::setImageUriSerials)),
                        notrace("myNoParaTags", component(myNoParaTags, i::getNoParaTags, i::setNoParaTags)),
                        trace("myDefaultUrlTitle", component(myDefaultUrlTitle, i::getDefaultUrlTitle, i::setDefaultUrlTitle)),

                        new Settable<ComboBox<HtmlGeneratorProvider.Info>>() {
                            @Override
                            public void reset() {
                                myHtmlProviderLastItem = i.getHtmlGeneratorProviderInfo();
                                myHtmlProvidersModel.setSelectedItem(myHtmlProviderLastItem);
                            }

                            @Override
                            public void apply() {
                                i.setHtmlGeneratorProviderInfo(myHtmlProviderLastItem);
                            }

                            @Override
                            public boolean isModified() {
                                return !Objects.equals(i.getHtmlGeneratorProviderInfo(), myHtmlProviderLastItem);
                            }

                            @Override
                            public ComboBox<HtmlGeneratorProvider.Info> getComponent() {
                                return myHtmlProviders;
                            }
                        },

                        notrace("myHeadTop", component(myHeadTop, textEditorGetter, textEditorSetter, i::getHeadTop, i::setHeadTop)),
                        notrace("myHeadBottom", component(myHeadBottom, textEditorGetter, textEditorSetter, i::getHeadBottom, i::setHeadBottom)),
                        notrace("myBodyTop", component(myBodyTop, textEditorGetter, textEditorSetter, i::getBodyTop, i::setBodyTop)),
                        notrace("myBodyBottom", component(myBodyBottom, textEditorGetter, textEditorSetter, i::getBodyBottom, i::setBodyBottom)),
                };
            }
        };

        myAllowPlainTextHtmlGenerator = allowPlainTextHtmlGenerator;
        myLastPanelProviderInfo = getPanelProviderInfo();
        myHtmlProvidersModel = getHtmlProvidersModel(getPanelProvider());
        myHtmlProviderLastItem = MdHtmlSettings.Companion.getDefaultSettings(myLastPanelProviderInfo).getHtmlGeneratorProviderInfo();

        onFormCreated();
    }

    private void createUIComponents() {
        myUpdateListener = e -> updateFormOnReshow(false);

        myHeadTopEnabled = new JBCheckBox();
        myHeadBottomEnabled = new JBCheckBox();
        myBodyTopEnabled = new JBCheckBox();
        myBodyBottomEnabled = new JBCheckBox();
        myAddDocTypeHtml = new JBCheckBox();
        myNoParaTags = new JBCheckBox();

        myHeadTopEnabled.addActionListener(myUpdateListener);
        myHeadBottomEnabled.addActionListener(myUpdateListener);
        myBodyTopEnabled.addActionListener(myUpdateListener);
        myBodyBottomEnabled.addActionListener(myUpdateListener);
        myAddDocTypeHtml.addActionListener(myUpdateListener);
        myNoParaTags.addActionListener(myUpdateListener);

        CustomizableEditorTextField.EditorCustomizationListener editorCustomizationListener = new CustomizableEditorTextField.EditorCustomizationListener() {
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
        };

        myHeadTop = createCustomizableTextFieldEditor(editorCustomizationListener, "html");
        myHeadBottom = createCustomizableTextFieldEditor(editorCustomizationListener, "html");
        myBodyTop = createCustomizableTextFieldEditor(editorCustomizationListener, "html");
        myBodyBottom = createCustomizableTextFieldEditor(editorCustomizationListener, "html");

        checkBoxEditorMap = new HashMap<>();
        checkBoxEditorMap.put(myHeadTopEnabled, myHeadTop);
        checkBoxEditorMap.put(myHeadBottomEnabled, myHeadBottom);
        checkBoxEditorMap.put(myBodyTopEnabled, myBodyTop);
        checkBoxEditorMap.put(myBodyBottomEnabled, myBodyBottom);

        myHtmlProvidersModel = getHtmlProvidersModel(getPanelProvider());
        myHtmlProviders = new ComboBox<>(myHtmlProvidersModel);
        myHtmlProviders.addItemListener(e -> {
            final Object item = e.getItem();
            if (e.getStateChange() != ItemEvent.SELECTED || !(item instanceof HtmlGeneratorProvider.Info)) {
                return;
            }

            myHtmlProviderLastItem = (HtmlGeneratorProvider.Info) item;
            updateOptionalSettings();
        });

        myExtensionsPanel = getExtensionsPanel();
    }

    @Override
    protected void updatePanelProviderDependentComponents(@NotNull HtmlPanelProvider fromProvider, @NotNull HtmlPanelProvider toProvider, boolean isInitialShow) {
        MdHtmlSettings htmlSettings = new MdHtmlSettings();
        components.apply(htmlSettings);

        htmlSettings.changeToProvider(fromProvider.getINFO(), toProvider.getINFO());
        myLastPanelProviderInfo = toProvider.getINFO();
        components.reset(htmlSettings);

        myHtmlProviderLastItem = htmlSettings.getHtmlGeneratorProvider().getINFO();
        myHtmlProvidersModel = getHtmlProvidersModel(toProvider);

        myHtmlProviders.setModel(myHtmlProvidersModel);
        myHtmlProvidersModel.setSelectedItem(myHtmlProviderLastItem);
        myHtmlProviders.setSelectedItem(myHtmlProviderLastItem);
    }

    public void updateFormOnReshow(boolean isInitialShow) {
        if (checkBoxEditorMap == null) return;

        int enabledEditors = 0;
        for (JBCheckBox checkBox : checkBoxEditorMap.keySet()) {
            CustomizableEditorTextField editorTextField = checkBoxEditorMap.get(checkBox);
            MdSettableFormBase.updateEditorTextFieldEditable(editorTextField, checkBox.isSelected());
            if (checkBox.isSelected()) enabledEditors++;
        }

        Dimension unconstrainedSize = new Dimension(-1, -1);
        Dimension constrainedSize = new Dimension(-1, 0);

        for (JBCheckBox checkBox : checkBoxEditorMap.keySet()) {
            CustomizableEditorTextField editorTextField = checkBoxEditorMap.get(checkBox);
            Container parent = editorTextField.getParent();
            GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getParent().getLayout();
            GridConstraints constraints = gridLayoutManager.getConstraintsForComponent(parent);

            if (checkBox.isSelected() || enabledEditors == 0 || enabledEditors == 4) {
                parent.setPreferredSize(unconstrainedSize);
                parent.setMaximumSize(unconstrainedSize);
                constraints.setFill(GridConstraints.FILL_BOTH);
                constraints.setVSizePolicy(GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK);
            } else {
                parent.setPreferredSize(constrainedSize);
                parent.setMaximumSize(constrainedSize);
                constraints.setFill(GridConstraints.FILL_HORIZONTAL);
                constraints.setAnchor(GridConstraints.ANCHOR_NORTH);
                constraints.setVSizePolicy(GridConstraints.SIZEPOLICY_FIXED);
            }

            gridLayoutManager.invalidateLayout(parent);
        }

        myMainPanel.invalidate();
    }

    @Override
    protected JPanel getMainFormPanel() {
        return myMainPanel;
    }

    @Override
    public void updateOptionalSettings() {
        HtmlGeneratorProvider panelProvider = HtmlGeneratorProvider.Companion.getFromInfoOrDefault(myHtmlProviderLastItem);

        myAddPageHeader.setEnabled(panelProvider.isSupportedSetting(MdHtmlSettings.ADD_PAGE_HEADER));
        myAddDocTypeHtml.setEnabled(panelProvider.isSupportedSetting(MdHtmlSettings.ADD_DOC_TYPE_HTML));

        updateExtensionsOptionalSettings();
    }

    @NotNull
    private CollectionComboBoxModel<HtmlGeneratorProvider.Info> getHtmlProvidersModel(@NotNull HtmlPanelProvider panelProvider) {
        List<HtmlGeneratorProvider.Info> providersInfo = getCompatibleGeneratorProvidersInfo(panelProvider);
        return new CollectionComboBoxModel<>(providersInfo, providersInfo.get(0));
    }

    @NotNull
    private List<HtmlGeneratorProvider.Info> getCompatibleGeneratorProvidersInfo(@NotNull HtmlPanelProvider panelProvider) {
        HtmlGeneratorProvider[] extensions = HtmlGeneratorProvider.Companion.getEP_NAME().getExtensions();
        return ContainerUtil.mapNotNull(extensions, provider -> {
            if (provider.getHAS_PARENT()) {
                // dedicated CSS to another provider, not for generic panel use
                return null;
            }

            if (provider.getINFO().getProviderId().equals(TextHtmlGeneratorProvider.INSTANCE.getID())) {
                // unmodified HTML generator, this is selected by preview type settings
                if (!myAllowPlainTextHtmlGenerator) return null;
            } else if (!provider.getCOMPATIBILITY().isForAvailable(panelProvider.getCOMPATIBILITY())) {
                // not compatible with current browser
                return null;
            }

            return provider.getINFO();
        });
    }

    @Override
    public void reset(@NotNull final MdRenderingProfileHolder settings) {
        components.reset(settings.getHtmlSettings());
        resetExtensions(settings);

        updateOptionalSettings();

        if (!myInitialShow) {
            // if already initialized then update, otherwise the first show will do it
            ApplicationManager.getApplication().invokeLater(() -> updateFormOnReshow(false), ModalityState.any());
        }

        if (myUpdateListener != null) myUpdateListener.actionPerformed(null);
    }

    @Override
    public void apply(@NotNull final MdRenderingProfileHolder settings) {
        components.apply(settings.getHtmlSettings());
        applyExtensions(settings);
    }

    @Override
    public boolean isModified(@NotNull final MdRenderingProfileHolder settings) {
        return components.isModified(settings.getHtmlSettings()) || isModifiedExtensions(settings);
    }

    @Override
    protected void disposeResources() {
        myHeadTop = null;
        myHeadBottom = null;
        myBodyTop = null;
        myBodyBottom = null;
    }
}
