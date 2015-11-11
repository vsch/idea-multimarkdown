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
package com.vladsch.idea.multimarkdown.settings;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.license.LicenseAgent;
import com.vladsch.idea.multimarkdown.license.LicenseRequest;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GeneralOptionsPanel extends SettingsPanelImpl {
    private static final Logger logger = Logger.getLogger(GeneralOptionsPanel.class);
    private final MultiMarkdownGlobalSettingsListener globalSettingsListener;

    private JButton buyLicenseButton;
    private JButton clearLicenseButton;
    private JButton fetchLicenseButton;
    private JButton trialLicenseButton;
    private JCheckBox enableFirebugCheckBox;
    private JCheckBox enableTrimSpacesCheckBox;
    private JCheckBox showHtmlTextAsModifiedCheckBox;
    private JCheckBox showHtmlTextCheckBox;
    private JCheckBox useOldPreviewCheckBox;
    private JEditorPane licenseInfoEditorPane;
    private JLabel enableFirebugLabel;
    private JLabel maxImgWidthLabel;
    private JLabel pageZoomLabel;
    private JLabel parsingTimeoutDescriptionLabel;
    private JPanel mainPanel;
    private JPanel settingsPanel;
    private JSpinner maxImgWidthSpinner;
    private JSpinner pageZoomSpinner;
    private JSpinner parsingTimeoutSpinner;
    private JSpinner updateDelaySpinner;
    private JTextArea licenseTextArea;

    @Nullable
    @Override
    public Object getComponent(@NotNull String persistName) {
        if (persistName.equals("enableFirebugCheckBox")) return enableFirebugCheckBox;
        if (persistName.equals("enableTrimSpacesCheckBox")) return enableTrimSpacesCheckBox;
        if (persistName.equals("licenseTextArea")) return licenseTextArea;
        if (persistName.equals("maxImgWidthSpinner")) return maxImgWidthSpinner;
        if (persistName.equals("pageZoomSpinner")) return pageZoomSpinner;
        if (persistName.equals("parsingTimeoutSpinner")) return parsingTimeoutSpinner;
        if (persistName.equals("showHtmlTextAsModifiedCheckBox")) return showHtmlTextAsModifiedCheckBox;
        if (persistName.equals("showHtmlTextCheckBox")) return showHtmlTextCheckBox;
        if (persistName.equals("updateDelaySpinner")) return updateDelaySpinner;
        if (persistName.equals("useOldPreviewCheckBox")) return useOldPreviewCheckBox;
        if (persistName.equals(IS_LICENSED_BOOLEAN)) return isLicensed;
        if (persistName.equals(USE_OLD_PREVIEW_CHECK_BOX)) return useOldPreviewCheckBox;

        return null;
    }

    @Override
    public void updateShowHtmlTextControls(boolean isShowHtmlText) {
        if (showHtmlTextAsModifiedCheckBox != null) {
            showHtmlTextAsModifiedCheckBox.setEnabled(isShowHtmlText);
        }
    }

    @Override
    public void updateUseOldPreviewControls(boolean useNewPreview) {
        enableFirebugCheckBox.setEnabled(useNewPreview);
        enableFirebugLabel.setEnabled(useNewPreview);
        pageZoomSpinner.setEnabled(useNewPreview);
        pageZoomLabel.setEnabled(useNewPreview);
        maxImgWidthSpinner.setEnabled(!useNewPreview);
        maxImgWidthLabel.setEnabled(!useNewPreview);
    }

    final private LicenseAgent agent;
    final MultiMarkdownGlobalSettings settings;
    private boolean isLicensed;

    public GeneralOptionsPanel() {
        this.agent = new LicenseAgent(MultiMarkdownPlugin.getInstance().getAgent());
        settings = MultiMarkdownGlobalSettings.getInstance();

        showHtmlTextCheckBox.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                notifyUpdateShowHtmlTextControls(showHtmlTextCheckBox.isSelected());
            }
        });

        showHtmlTextCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyUpdateShowHtmlTextControls(showHtmlTextCheckBox.isSelected());
            }
        });

        if (MultiMarkdownGlobalSettings.getInstance().fxPreviewFailedBuild.isFailedBuild()) {
            // set it and disable
            useOldPreviewCheckBox.setSelected(true);
            useOldPreviewCheckBox.setEnabled(false);
        }

        notifyUpdateUseOldPreviewControls(useOldPreviewCheckBox.isSelected());
        ItemListener itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                notifyUpdateUseOldPreviewControls(useOldPreviewCheckBox.isSelected());
            }
        };

        useOldPreviewCheckBox.addItemListener(itemListener);
        fetchLicenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FetchLicenseDialog.showDialog(mainPanel, agent);
                licenseTextArea.setText(agent.licenseCode());
                updateLicenseInfo(false);
            }
        });

        if (Desktop.isDesktopSupported()) {
            trialLicenseButton.addActionListener(getActionListenerBrowseUrl(LicenseAgent.getTrialLicenseURL()));
            buyLicenseButton.addActionListener(getActionListenerBrowseUrl(LicenseAgent.getLicenseURL()));
        } else {
            trialLicenseButton.setVisible(false);
            buyLicenseButton.setVisible(false);
        }

        clearLicenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //FetchLicenseDialog.showDialog(panel);
                licenseTextArea.setText("");
                //updateLicenseInfo(false);
            }
        });

        licenseTextArea.setVisible(false);
        updateLicenseInfo(true);
        MultiMarkdownGlobalSettings.getInstance().addListener(globalSettingsListener = new MultiMarkdownGlobalSettingsListener() {
            public void handleSettingsChanged(@NotNull final MultiMarkdownGlobalSettings newSettings) {
                agent.updateFrom(MultiMarkdownPlugin.getInstance().getAgent());
                licenseTextArea.setText(MultiMarkdownGlobalSettings.getInstance().licenseCode.getValue());
                updateLicenseInfo(false);
            }
        });

        licenseTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                if (!agent.licenseCode().equals(licenseTextArea.getText().trim())) {
                    updateLicenseInfo(true);
                }
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                if (!agent.licenseCode().equals(licenseTextArea.getText().trim())) {
                    updateLicenseInfo(true);
                }
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                if (!agent.licenseCode().equals(licenseTextArea.getText().trim())) {
                    updateLicenseInfo(true);
                }
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        final SpinnerNumberModel model = new SpinnerNumberModel(1.0, 0.2, 5.0, 0.01);
        pageZoomSpinner = new JSpinner(model);
        JSpinner.NumberEditor decimalFormat = new JSpinner.NumberEditor(pageZoomSpinner, "0.00");
    }

    private void updateLicenseInfo(boolean delayed) {
        if (!delayed) {
            String licenseInfoText = "";
            boolean isValidLicense = true;
            isLicensed = false;

            // reset to defaults
            licenseTextArea.setVisible(true);
            trialLicenseButton.setVisible(true);
            fetchLicenseButton.setVisible(true);
            buyLicenseButton.setVisible(true);

            agent.setLicenseCode(licenseTextArea.getText());
            if (agent.isValidLicense()) {
                if (!agent.isValidActivation()) {
                    agent.setActivationCode(null);

                    // request activation
                    LicenseRequest request = new LicenseRequest(MultiMarkdownPlugin.getProductName(), MultiMarkdownPlugin.getProductVersion());
                    request.license_code = agent.licenseCode();
                    if (!agent.getLicenseCode(request)) {
                        // license is not valid
                        isValidLicense = false;
                    }
                }
            }

            if (agent.isValidLicense() && isValidLicense) {
                trialLicenseButton.setVisible(false);
                fetchLicenseButton.setVisible(false);
                clearLicenseButton.setVisible(true);

                if (agent.isValidActivation()) {
                    licenseTextArea.setVisible(false);
                    int expiresIn = agent.getLicenseExpiringIn();
                    String licenseType = MultiMarkdownBundle.message("settings.license-type-" + agent.getLicenseType());

                    String days = (expiresIn < 0) ? MultiMarkdownBundle.message("settings.license-has-expired-" + agent.getLicenseType())
                            : (expiresIn == 1 ? MultiMarkdownBundle.message("settings.license-expires-tomorrow-" + agent.getLicenseType())
                            : MultiMarkdownBundle.message("settings.license-ends-" + agent.getLicenseType()) + " " + MultiMarkdownBundle.message("settings.license-expires-in-days", expiresIn));

                    licenseInfoText += "\n\n" + MultiMarkdownBundle.message("settings.license-activated-on") + " " + agent.getActivatedOn();
                    licenseInfoText += "\n\n" + MultiMarkdownBundle.message("settings.license-expires-" + agent.getLicenseType()) + " " + agent.getLicenseExpiration() + ",\n       " + days;
                    if (expiresIn > 90) {
                        buyLicenseButton.setVisible(false);
                    }

                    isLicensed = expiresIn >= 0;
                }
            } else {
                clearLicenseButton.setVisible(!licenseTextArea.getText().trim().isEmpty());
                if (!licenseTextArea.getText().trim().isEmpty()) {
                    licenseInfoText += MultiMarkdownBundle.message("settings.license-invalid");
                }
            }

            licenseInfoEditorPane.setText(licenseInfoText.trim());
            notifyUpdateLicensedControls(isLicensed);
        } else {
            final Application application = ApplicationManager.getApplication();
            application.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updateLicenseInfo(false);
                }
            }, application.getCurrentModalityState());
        }
    }
}
