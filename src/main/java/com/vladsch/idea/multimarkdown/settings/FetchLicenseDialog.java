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

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.license.LicenseAgent;
import com.vladsch.idea.multimarkdown.license.LicenseRequest;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class FetchLicenseDialog extends DialogWrapper {
    private static final Logger logger = Logger.getLogger(FetchLicenseDialog.class);

    private JPanel contentPane;
    private JTextField emailTextField;
    private JTextField nameTextField;
    private JEditorPane descriptionEditorPane;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private LicenseAgent agent;
    private String lastLicenseError;

    public FetchLicenseDialog(JComponent parent, LicenseAgent agent) {
        super(parent, false);
        init();
        setTitle(MultiMarkdownBundle.message("settings.license-trial.title"));
        setModal(true);
        this.agent = agent;

        descriptionEditorPane.setText("" +
                "<html>\n" +
                "  <head>\n" +
                "  <style>\n" +
                "     body, div { margin: 0 !important; padding: 0 !important; }\n" +
                "     p { margin: 10px !important; padding: 0 !important; }\n" +
                "     p.error { color: #FF2080; }\n" +
                "  </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <p>\n" +
                "      " + MultiMarkdownBundle.message("settings.license-fetch.description") + "\n" +
                "    </p>\n" +
                "  </body>\n" +
                "</html>\n" +
                "");
    }

    @Nullable
    @Override
    protected String getDimensionServiceKey() {
        return "MultiMarkdownGetTrialLicenseDialog";
    }

    protected class MyOkAction extends OkAction {
        protected MyOkAction() {
            super();
            putValue(Action.NAME, MultiMarkdownBundle.message("settings.license-fetch"));
        }

        @Override
        protected void doAction(ActionEvent e) {
            if (doValidate(true) == null) {
               getOKAction().setEnabled(true);
            }
            super.doAction(e);
        }
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        super.createDefaultActions();
        return new Action[] { new MyOkAction(), getCancelAction() };
    }

    public static void showDialog(JComponent parent, LicenseAgent agent) {
        FetchLicenseDialog dialog = new FetchLicenseDialog(parent, agent);
        dialog.show();
    }

    protected ValidationInfo doValidate(boolean loadLicense) {
        String email = emailTextField.getText();
        if (!EmailValidator.getInstance().isValid(email)) {
            return new ValidationInfo(MultiMarkdownBundle.message("settings.license-email-invalid", emailTextField));
        }

        String name = nameTextField.getText();
        if (name.trim().isEmpty()) {
            return new ValidationInfo(MultiMarkdownBundle.message("settings.license-name-required", nameTextField));
        }

        if (loadLicense) {
            // get the license.
            LicenseRequest request = new LicenseRequest(MultiMarkdownPlugin.getProductName(), MultiMarkdownPlugin.getProductVersion());
            request.email = email;
            request.name = name;
            char[] password = passwordField.getPassword();
            request.password = new String(password);

            if (agent.getLicenseCode(request)) {
                // see if we can extract the information from activation
                if (agent.isValidActivation()) {
                    lastLicenseError = "";
                    return super.doValidate();
                }
            }

            //noinspection ConstantConditions
            lastLicenseError = agent.getMessage();
        }

        if (!lastLicenseError.isEmpty()) {
            return new ValidationInfo(lastLicenseError);
        }
        return super.doValidate();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return doValidate(false);
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return emailTextField;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }
}
