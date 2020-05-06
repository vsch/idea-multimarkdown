// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex;

import com.intellij.ide.projectWizard.NewProjectWizard;
import com.intellij.ide.projectWizard.ProjectSettingsStep;
import com.intellij.ide.util.newProjectWizard.StepSequence;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBCheckBox;
import com.vladsch.flexmark.util.misc.DelimitedBuilder;
import com.vladsch.plugin.util.HelpersKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlexmarkExtModuleSettingsForm extends FlexmarkExtModuleWizardStep implements FlexmarkModuleOptions.Holder {
    public static final String PROFILE_NONE = "None";
    public static final String PROFILE_BLOCK_PARSER = "BlockParser";
    public static final String PROFILE_ALL = "All";
    private JPanel myMainPanel;
    private JTextField myExtensionName;
    JTextField myExtensionPackage;

    private JBCheckBox myDelimiterProcessor;
    private JBCheckBox myInlineParserExtension;
    private JBCheckBox myBlockParser;
    private JBCheckBox myBlockPreProcessor;
    private JBCheckBox myLinkRefProcessor;
    private JBCheckBox myParagraphPreProcessor;
    private JBCheckBox myNodePostProcessor;
    private JBCheckBox myDocumentPostProcessor;
    private JBCheckBox myNodeRenderer;
    private JBCheckBox myCustomBlockNode;
    private JBCheckBox myCustomNode;
    private JBCheckBox myCustomNodeRepository;
    private JBCheckBox myCustomProperties;
    private JBCheckBox myPhasedNodeRenderer;

    private JButton mySelectNoneButton;
    private JButton mySelectAllButton;
    private JButton mySelectBlockParserButton;
    private JBCheckBox myLinkResolver;
    private JBCheckBox myAttributeProvider;
    private JBCheckBox myJiraRenderer;

    final Map<String, Set<JBCheckBox>> profileSelectedCheckBoxMap = new HashMap<String, Set<JBCheckBox>>();
    final Map<JButton, String> profileButtonMap = new HashMap<JButton, String>();
    final List<JBCheckBox> myCheckBoxes;

    final protected FlexmarkModuleOptions.Holder myOptionsHolder;

    final String myProjectName;
    final String myProjectFileDirectory;

    String myExtensionPackagePrefix;
    boolean myInExtensionNameUpdate = false;

    public FlexmarkExtModuleSettingsForm(@Nullable WizardContext context, @Nullable Disposable parentDisposable, @NotNull FlexmarkModuleOptions.Holder optionsHolder) {
        super(context, parentDisposable, optionsHolder);

        myProjectName = context != null ? context.getProjectName() : null;
        myProjectFileDirectory = context != null ? context.getProjectFileDirectory() : null;

        //super(optionsHolder);
        //noinspection unchecked
        profileSelectedCheckBoxMap.put(PROFILE_NONE, (Set<JBCheckBox>) Collections.EMPTY_SET);
        myCheckBoxes = Arrays.asList(
                myDelimiterProcessor,
                myInlineParserExtension,
                myBlockParser,
                myBlockPreProcessor,
                myLinkRefProcessor,
                myParagraphPreProcessor,
                myNodePostProcessor,
                myDocumentPostProcessor,
                myNodeRenderer,
                myJiraRenderer,
                myCustomBlockNode,
                myCustomNode,
                myCustomNodeRepository,
                myCustomProperties,
                myPhasedNodeRenderer,
                myLinkResolver,
                myAttributeProvider
        );

        profileSelectedCheckBoxMap.put(PROFILE_ALL, new HashSet<JBCheckBox>(myCheckBoxes));
        profileSelectedCheckBoxMap.put(PROFILE_BLOCK_PARSER, new HashSet<JBCheckBox>(Arrays.asList(
                //myDelimiterProcessor,
                myBlockParser,
                //myBlockPreProcessor,
                //myLinkRefProcessor,
                //myParagraphPreProcessor,
                //myNodePostProcessor,
                //myDocumentPostProcessor,
                myNodeRenderer,
                myCustomBlockNode,
                //myCustomNode,
                //myCustomNodeRepository,
                myCustomProperties
                //myPhasedNodeRenderer,
                //myLinkResolver,
                //myAttributeProvider,
        )));

        profileButtonMap.put(mySelectNoneButton, PROFILE_NONE);
        profileButtonMap.put(mySelectAllButton, PROFILE_ALL);
        profileButtonMap.put(mySelectBlockParserButton, PROFILE_BLOCK_PARSER);

        myOptionsHolder = optionsHolder;

        // copy model defaults
        FlexmarkModuleOptions options = myOptionsHolder.getOptions();

        myExtensionName.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                if (!myInExtensionNameUpdate) {
                    myInExtensionNameUpdate = true;
                    updateExtensionPackage();
                    myInExtensionNameUpdate = false;
                }
            }
        });

        myExtensionPackage.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                if (!myInExtensionNameUpdate) {
                    updateExtensionPackagePrefix();
                }
            }
        });

        myNodeRenderer.addActionListener(event -> updateOptions());

        ActionListener selectActionListener = event -> {
            //noinspection SuspiciousMethodCalls
            String profileName = profileButtonMap.get(event.getSource());
            if (profileName != null) {
                Set<JBCheckBox> profileCheckBoxes = profileSelectedCheckBoxMap.get(profileName);
                if (profileCheckBoxes != null) {
                    for (JBCheckBox checkBox : myCheckBoxes) {
                        checkBox.setSelected(profileCheckBoxes.contains(checkBox));
                    }
                }

                updateOptions();
            }
        };

        for (JButton button : profileButtonMap.keySet()) {
            button.addActionListener(selectActionListener);
        }

        setOptions(options);
    }

    protected void updateOptions() {
        myPhasedNodeRenderer.setEnabled(myNodeRenderer.isSelected());
    }

    protected void updateExtensionPackagePrefix() {
        String[] words = NameUtil.nameToWords(myExtensionName.getText());
        com.vladsch.flexmark.util.misc.DelimitedBuilder name = new com.vladsch.flexmark.util.misc.DelimitedBuilder("");

        String extensionName = name.appendAll(".", words).getAndClear().toLowerCase();
        String extensionPackage = myExtensionPackage.getText();
        if (extensionPackage.endsWith("." + extensionName)) {
            myExtensionPackagePrefix = extensionPackage.substring(0, extensionPackage.length() - extensionName.length() - 1);
        } else {
            myExtensionPackagePrefix = extensionPackage;
        }
    }

    protected void updateExtensionPackage() {
        String[] words = NameUtil.nameToWords(myExtensionName.getText());
        com.vladsch.flexmark.util.misc.DelimitedBuilder name = new DelimitedBuilder("");

        String extensionName = name.appendAll(".", words).getAndClear().toLowerCase();
        String extensionPackage = myExtensionPackagePrefix == null ? "" : myExtensionPackagePrefix;
        myExtensionPackage.setText(HelpersKt.suffixWith(extensionPackage, ".") + extensionName);

        // now set - module name
        // now set - content root
        // now set - module file location
        if (myContext != null/* && !myContext.isProjectFileDirectorySetExplicitly()*/) {
            String dirName = name.appendAll("-", words).getAndClear().toLowerCase();
            //myContext.setProjectName(dirName);
            myContext.setProjectFileDirectory(myProjectFileDirectory + "/flexmark-ext-" + dirName);

            try {
                if (myContext.getWizard() instanceof NewProjectWizard) {
                    NewProjectWizard wizard = (NewProjectWizard) myContext.getWizard();
                    StepSequence sequence = wizard.getSequence();
                    // need to find NamePathComponent
                    for (ModuleWizardStep step : sequence.getSelectedSteps()) {
                        if (step instanceof ProjectSettingsStep) {
                            ProjectSettingsStep projectSettingsStep = (ProjectSettingsStep) step;
                            projectSettingsStep.setModuleName(dirName);
                            break;
                        }
                    }
                }
            } catch (NoSuchMethodError ignored) {

            }
        }
    }

    public JComponent getComponent() {
        return myMainPanel;
    }

    @Override
    public void setOptions(@NotNull FlexmarkModuleOptions options) {
        myExtensionName.setText(options.extensionName);
        myExtensionPackage.setText(options.extensionPackage);
        myExtensionPackagePrefix = options.extensionPackagePrefix;
        myDelimiterProcessor.setSelected(options.delimiterProcessor);
        myInlineParserExtension.setSelected(options.inlineParserExtension);
        myBlockParser.setSelected(options.blockParser);
        myBlockPreProcessor.setSelected(options.blockPreProcessor);
        myLinkRefProcessor.setSelected(options.linkRefProcessor);
        myParagraphPreProcessor.setSelected(options.paragraphPreProcessor);
        myNodePostProcessor.setSelected(options.nodePostProcessor);
        myDocumentPostProcessor.setSelected(options.documentPostProcessor);
        myNodeRenderer.setSelected(options.nodeRenderer || options.phasedNodeRenderer);
        myJiraRenderer.setSelected(options.jiraRenderer);
        myPhasedNodeRenderer.setSelected(options.phasedNodeRenderer);
        myLinkResolver.setSelected(options.linkResolver);
        myAttributeProvider.setSelected(options.attributeProvider);
        myCustomBlockNode.setSelected(options.customBlockNode);
        myCustomNode.setSelected(options.customNode);
        myCustomNodeRepository.setSelected(options.customNodeRepository);
        myCustomProperties.setSelected(options.customProperties);
        updateOptions();
        updateExtensionPackagePrefix();
    }

    @Override
    @NotNull
    public FlexmarkModuleOptions getOptions() {
        return new FlexmarkModuleOptions(
                myExtensionName.getText(),
                myExtensionPackage.getText(),
                myExtensionPackagePrefix,
                myDelimiterProcessor.isSelected(),
                myInlineParserExtension.isSelected(),
                myBlockParser.isSelected(),
                myBlockPreProcessor.isSelected(),
                myLinkRefProcessor.isSelected(),
                myParagraphPreProcessor.isSelected(),
                myNodePostProcessor.isSelected(),
                myDocumentPostProcessor.isSelected(),
                myNodeRenderer.isSelected() && !myPhasedNodeRenderer.isSelected(),
                myNodeRenderer.isSelected() && myPhasedNodeRenderer.isSelected(),
                myLinkResolver.isSelected(),
                myAttributeProvider.isSelected(),
                myCustomBlockNode.isSelected(),
                myCustomNode.isSelected(),
                myCustomNodeRepository.isSelected(),
                myCustomProperties.isSelected(),
                myJiraRenderer.isSelected()
        );
    }

    public void updateDataModel() {
        myOptionsHolder.setOptions(getOptions());
    }

    private void createUIComponents() {
    }
}
