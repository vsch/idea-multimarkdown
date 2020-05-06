// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.ex.FileChooserDialogImpl;
import com.intellij.openapi.fileChooser.ex.FileSaverDialogImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.ui.components.JBCheckBox;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.MdImageCache;
import com.vladsch.md.nav.MdPlugin;
import com.vladsch.md.nav.actions.ide.CopyReferenceUtils;
import com.vladsch.md.nav.settings.api.ApplicationSettingsContainer;
import com.vladsch.md.nav.settings.api.MdProjectSettingsExtensionHandler;
import com.vladsch.md.nav.util.MiscUtils;
import com.vladsch.plugin.util.ui.Settable;
import com.vladsch.plugin.util.ui.SettingsComponents;
import icons.MdIcons;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

public class MdApplicationSettingsForm extends ApplicationSettingsContainer {
    private static final Logger LOG = Logger.getInstance(MdApplicationSettingsForm.class);
    protected static final String EXPORTED_SETTINGS_STATE_ELEMENT_NAME = "MarkdownNavigator.ExportedProjectSettings";

    private JPanel myMainPanel;
    @SuppressWarnings("unused") private JPanel myExtensionsPanel;
    private JLabel myVersionNumberLabel;
    private JComboBox<String> myUpdateStream;
    JSpinner myPreviewScrollDelay;
    JBCheckBox myDisableGifImages;
    JBCheckBox myCopyPathFileName;
    JBCheckBox myCopyPathBareFileName;
    JBCheckBox myCopyPathWithLineNumbers;
    JBCheckBox myCopyUpsourcePathWithLineNumbers;
    JBCheckBox myUseUpsourceURL;
    JSpinner myZoomFactor;
    private JButton myActions;
    private JBPopupMenu myPopupMenuActions;
    JComboBox<String> myDocumentIcon;
    JComboBox<String> myWikiIcon;
    private JLabel myImageCacheUse;
    private JButton myClearImageCache;
    private JPanel myAddToCopyPathPanel;

    private RenderingProfileSynchronizer myProjectSynchronizer = null;

    private final SettingsComponents<MdDocumentSettings> components;

    public MdApplicationSettingsForm(MdApplicationSettings applicationSettings, RenderingProfileSynchronizer renderingProfile) {
        super(applicationSettings, renderingProfile);

        components = new SettingsComponents<MdDocumentSettings>() {
            @Override
            protected Settable<MdDocumentSettings>[] createComponents(@NotNull MdDocumentSettings i) {
                //noinspection unchecked
                return new Settable[] {
                        component(myPreviewScrollDelay, i::getPreviewScrollDelay, i::setPreviewScrollDelay),
                        component(myDisableGifImages, i::getDisableGifImages, i::setDisableGifImages),
                        component(myCopyPathFileName, i::getCopyPathFileName, i::setCopyPathFileName),
                        component(myCopyPathBareFileName, i::getCopyPathBareFileName, i::setCopyPathBareFileName),
                        component(myCopyPathWithLineNumbers, i::getCopyPathWithLineNumbers, i::setCopyPathWithLineNumbers),
                        component(myCopyUpsourcePathWithLineNumbers, i::getCopyUpsourcePathWithLineNumbers, i::setCopyUpsourcePathWithLineNumbers),
                        component(myUseUpsourceURL, i::getUseUpsourceURL, i::setUseUpsourceURL),
                        component(myZoomFactor, i::getZoomFactor, i::setZoomFactor),
                        component(DocumentIconTypes.ADAPTER, myDocumentIcon, i::getDocumentIcon, i::setDocumentIcon),
                        component(DocumentIconTypes.ADAPTER, myWikiIcon, i::getWikiIcon, i::setWikiIcon),
                };
            }
        };

        myCopyUpsourcePathWithLineNumbers.setVisible(CopyReferenceUtils.isUpsourceCopyReferenceAvailable());
        myUseUpsourceURL.setVisible(CopyReferenceUtils.isUpsourceCopyReferenceAvailable());

        myVersionNumberLabel.setText(MdPlugin.getFullProductVersion());

        myActions.setComponentPopupMenu(myPopupMenuActions);

        myActions.addActionListener(e -> myPopupMenuActions.show(myActions, myActions.getWidth() / 10, myActions.getHeight() * 85 / 100));

        onFormCreated();

        myClearImageCache.addActionListener(e -> clearImageCache());

        String imageCacheSizeType;
        long imageCacheUse = MdImageCache.getInstance().fileSize();
        int cachedFiles = MdImageCache.getInstance().fileCount();
        if (imageCacheUse < 1024000) {
            imageCacheSizeType = "B";
        } else if (imageCacheUse < 1024 * 1024 * 1000) {
            imageCacheSizeType = "KB";
            imageCacheUse /= 1024;
        } else {
            imageCacheSizeType = "MB";
            imageCacheUse /= 1024 * 1024;
        }
        myImageCacheUse.setText(String.format("%,d: %,d %s", cachedFiles, imageCacheUse, imageCacheSizeType));
    }

    void clearImageCache() {
        MdImageCache.getInstance().clearCache();
        myImageCacheUse.setText("0: 0 B");
    }

    private void createUIComponents() {
        myExtensionsPanel = getExtensionsPanel();

        myUpdateStream = UpdateStreamType.ADAPTER.createComboBox();

        myDocumentIcon = DocumentIconTypes.ADAPTER.createComboBox();
        DocumentIconTypes.ADAPTER.addComboBoxIcons(myDocumentIcon, MdIcons::getFileIcon);

        myWikiIcon = DocumentIconTypes.ADAPTER.createComboBox();
        DocumentIconTypes.ADAPTER.addComboBoxIcons(myWikiIcon, MdIcons::getFileIcon);

        final SpinnerNumberModel delayModel = new SpinnerNumberModel(MdDocumentSettings.DEFAULT_PREVIEW_DELAY, MdDocumentSettings.MIN_PREVIEW_DELAY, MdDocumentSettings.MAX_PREVIEW_DELAY, 50);
        myPreviewScrollDelay = new JSpinner(delayModel);

        final SpinnerNumberModel zoomModel = new SpinnerNumberModel(1.0, MdPreviewSettings.MIN_ZOOM_FACTOR, MdPreviewSettings.MAX_ZOOM_FACTOR, 0.01);
        myZoomFactor = new JSpinner(zoomModel);
        JSpinner.NumberEditor decimalFormat = new JSpinner.NumberEditor(myZoomFactor, "0.00");
        myZoomFactor.setEditor(decimalFormat);

        myPopupMenuActions = new JBPopupMenu("Actions");
        final JBMenuItem copyToProjectDefaults = new JBMenuItem(MdBundle.message("settings.actions.popup.copy-to-project-defaults.label"));
        final JBMenuItem copyFromProjectDefaults = new JBMenuItem(MdBundle.message("settings.actions.popup.copy-from-project-defaults.label"));
        final JBMenuItem exportSettings = new JBMenuItem(MdBundle.message("settings.actions.popup.export-settings.label"));
        final JBMenuItem importSettings = new JBMenuItem(MdBundle.message("settings.actions.popup.import-settings.label"));
        final JBMenuItem resetSettings = new JBMenuItem(MdBundle.message("settings.actions.popup.reset-settings.label"));

        myPopupMenuActions.add(copyToProjectDefaults);
        myPopupMenuActions.add(copyFromProjectDefaults);
        myPopupMenuActions.add(exportSettings);
        myPopupMenuActions.add(importSettings);
        myPopupMenuActions.addSeparator();
        myPopupMenuActions.add(resetSettings);

        final Project project = ProjectUtil.guessCurrentProject(getMainFormPanel());

        copyToProjectDefaults.addActionListener(e -> {
            //final MarkdownApplicationSettings settings = MarkdownApplicationSettings.getInstance();
            final MdRenderingProfile profileHolder = RenderingProfileSynchronizer.getInstance(project).getRenderingProfile();
            final Project defaultProject = ProjectManager.getInstance().getDefaultProject();
            final MdRenderingProfile defaultProfileHolder = RenderingProfileSynchronizer.getInstance(defaultProject).getRenderingProfile();
            defaultProfileHolder.setRenderingProfile(new MdRenderingProfile(profileHolder.getRenderingProfile()));

            MiscUtils.forEach(MdProjectSettingsExtensionHandler.EXTENSIONS.getValue(), provider -> provider.copyFromProject(defaultProject, project));
        });

        copyFromProjectDefaults.addActionListener(e -> {
            //final MarkdownApplicationSettings settings = MarkdownApplicationSettings.getInstance();
            myProjectSynchronizer = RenderingProfileSynchronizer.getInstance(project);

            final Project defaultProject = ProjectManager.getInstance().getDefaultProject();
            final MdRenderingProfile defaultProfileHolder = RenderingProfileSynchronizer.getInstance(defaultProject).getRenderingProfile();
            myProjectSynchronizer.setRenderingProfileAndReset(defaultProfileHolder);

            MiscUtils.forEach(MdProjectSettingsExtensionHandler.EXTENSIONS.getValue(), provider -> provider.copyFromProject(project, defaultProject));
        });

        // reset to initial defaults
        resetSettings.addActionListener(e -> {
            // get default application settings
            final MdApplicationSettings defaultSettings = new MdApplicationSettings();

            // reset project settings
            myProjectSynchronizer = RenderingProfileSynchronizer.getInstance(project);
            MdRenderingProfile defaultRenderingProfile = MdRenderingProfileManager.getInstance(null).getDefaultRenderingProfile();
            myProjectSynchronizer.setRenderingProfileAndReset(defaultRenderingProfile);

            reset(defaultSettings);

            // allow extensions to reset project settings to default
            MiscUtils.forEach(MdProjectSettingsExtensionHandler.EXTENSIONS.getValue(), provider -> provider.resetToDefault(project));
        });

        final VirtualFile projectFile = project.getProjectFile();
        final VirtualFile projectDir = projectFile == null ? null : projectFile.getParent();
        final VirtualFile projectBaseDir = projectDir == null ? LocalFileSystem.getInstance().findFileByPath(System.getProperty("user.home")) : projectDir.getName().equals(".idea") ? projectDir.getParent() : projectDir;
        assert projectBaseDir != null;

        exportSettings.addActionListener(e -> {
            JPanel mainFormPanel = getMainFormPanel();
            if (mainFormPanel == null) return;

            FileSaverDescriptor fileSaverDescriptor = new FileSaverDescriptor(MdBundle.message("export-project-settings.title"), MdBundle.message("export-project-settings.description"), "xml");
            FileSaverDialogImpl saveDialog = new FileSaverDialogImpl(fileSaverDescriptor, mainFormPanel);

            VirtualFileWrapper file = saveDialog.save(projectBaseDir, "markdown-navigator-settings");

            if (file != null) {
                Element root = new Element(EXPORTED_SETTINGS_STATE_ELEMENT_NAME);
                final MdRenderingProfile profileHolder = RenderingProfileSynchronizer.getInstance(project).getRenderingProfile();
                final Element profileHolderState = profileHolder.getStateHolder().saveState(null);
                root.addContent(profileHolderState);

                // allow extensions to add project settings to exported
                MiscUtils.forEach(MdProjectSettingsExtensionHandler.EXTENSIONS.getValue(), provider -> provider.exportSettings(project, root));

                MdApplicationSettings.getInstance().getUpdatedDocumentSettings().getDocumentSettings().getStateHolder().saveState(root);

                try {
                    // make it 2016.3 compatible
                    FileUtil.createParentDirs(file.getFile());
                    FileWriter fileWriter = new FileWriter(file.getFile());
                    JDOMUtil.writeElement(root, fileWriter, "\n");
                } catch (IOException e1) {
                    LOG.info(e1);
                    Messages.showErrorDialog(e1.getMessage(), MdBundle.message("export-project-settings.failure.title"));
                }
            }
        });

        importSettings.addActionListener(e -> {
            FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);
            JPanel mainFormPanel = getMainFormPanel();
            if (mainFormPanel == null) return;

            fileChooserDescriptor.setTitle(MdBundle.message("import.project.settings.title"));
            fileChooserDescriptor.setDescription(MdBundle.message("import.project.settings.description"));
            FileChooserDialogImpl fileChooserDialog = new FileChooserDialogImpl(fileChooserDescriptor, mainFormPanel, project);
            String lastImport = MdApplicationSettings.getInstance().getDebugSettings().getLastSettingsImport();
            VirtualFile lastImportFile = projectBaseDir;
            if (!lastImport.isEmpty()) {
                File file = new File(lastImport);
                try {
                    lastImportFile = VirtualFileManager.getInstance().findFileByUrl(file.toURI().toURL().toString());
                } catch (MalformedURLException ignore) {
                }
            }

            VirtualFile[] files = fileChooserDialog.choose(project, lastImportFile);
            if (files.length > 0) {
                try {
                    Element root = JDOMUtil.load(files[0].getInputStream());
                    if (root.getName().equals(EXPORTED_SETTINGS_STATE_ELEMENT_NAME)) {
                        final Element profileHolderState = root.getChild(MdRenderingProfile.STATE_ELEMENT_NAME);
                        if (profileHolderState != null) {
                            myProjectSynchronizer = RenderingProfileSynchronizer.getInstance(project);
                            final MdRenderingProfile renderingProfile = new MdRenderingProfile();

                            // kludge: if component name is not "component" then state holder will look for child element "option" by itself
                            renderingProfile.getStateHolder().loadState(root);
                            myProjectSynchronizer.setRenderingProfileAndReset(renderingProfile);
                        }

                        // allow extensions to add project settings to imported settings
                        MiscUtils.forEach(MdProjectSettingsExtensionHandler.EXTENSIONS.getValue(), provider -> provider.importSettings(project, root));

                        final Element documentHolderState = root.getChild(MdDocumentSettings.STATE_ELEMENT_NAME);
                        if (documentHolderState != null) {
                            myApplicationSettings.groupNotifications(() -> {
                                MdDocumentSettings documentSettings = new MdDocumentSettings(myApplicationSettings.getDocumentSettings());
                                documentSettings.getStateHolder().loadState(root);
                                myApplicationSettings.setDocumentSettings(documentSettings);
                                MdApplicationSettings.getInstance().resetDocumentSettings(myApplicationSettings);
                            });
                        }

                        MdApplicationSettings.getInstance().getDebugSettings().setLastSettingsImport(files[0].getPath());
                    } else {
                        Messages.showErrorDialog(MdBundle.message("import-project-settings.failure.not-markdown-navigator.description"), MdBundle.message("import-project-settings.failure.title"));
                    }
                } catch (Exception e1) {
                    LOG.info(e1);
                    Messages.showErrorDialog(e1.getMessage(), MdBundle.message("import-project-settings.failure.title"));
                }
            }
        });
    }

    public void updateOptionalSettings() {
        updateExtensionsOptionalSettings();
    }

    @NotNull
    public JComponent getComponent() {
        return myMainPanel;
    }

    @Override
    protected void updateFormOnReshow(boolean isInitialShow) {
        updateOptionalSettings();
    }

    @Override
    protected JPanel getMainFormPanel() {
        return myMainPanel;
    }

    @Override
    public void reset(@NotNull MdApplicationSettingsHolder settings) {
        components.reset(settings.getDocumentSettings());

        // reset update stream
        resetUpdateStream(myUpdateStream, UpdateStreamType.STABLE);
        if (myProjectSynchronizer != null) {
            myProjectSynchronizer.setRenderingProfileAndReset(myProjectSynchronizer.getRenderingProfileHolder().getRenderingProfile());
        }

        resetExtensions(settings);

        updateOptionalSettings();
    }

    public boolean isModified(@NotNull MdApplicationSettingsHolder settings) {
        return components.isModified(settings.getDocumentSettings())
                || isModifiedUpdateStream(myUpdateStream, UpdateStreamType.STABLE)
                || isModifiedExtensions(settings) || myProjectSynchronizer != null && myProjectSynchronizer.isModified();
    }

    @Override
    public void apply(@NotNull MdApplicationSettingsHolder settings) {
        components.apply(settings.getDocumentSettings());
        applyUpdateStream(myUpdateStream, UpdateStreamType.STABLE);
        if (myProjectSynchronizer != null) {
            myProjectSynchronizer.apply();
        }
        applyExtensions(settings);
    }

    @SuppressWarnings("SameParameterValue")
    private void resetUpdateStream(JComboBox<String> comboBox, UpdateStreamType anEnum) {
        comboBox.setSelectedItem(UpdateStreamType.ADAPTER.findEnum(MiscUtils.getReleaseStream()).getDisplayName());
    }

    @SuppressWarnings("SameParameterValue")
    private void applyUpdateStream(JComboBox<String> comboBox, UpdateStreamType anEnum) {
        MiscUtils.setReleaseStream(UpdateStreamType.ADAPTER.findEnum((String) comboBox.getSelectedItem()).getIntValue());
    }

    @SuppressWarnings("SameParameterValue")
    private boolean isModifiedUpdateStream(JComboBox<String> comboBox, UpdateStreamType anEnum) {
        return MiscUtils.getReleaseStream() != UpdateStreamType.ADAPTER.findEnum((String) comboBox.getSelectedItem()).getIntValue();
    }
}
