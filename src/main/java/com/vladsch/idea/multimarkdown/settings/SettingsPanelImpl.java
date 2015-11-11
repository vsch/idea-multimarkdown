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
import com.intellij.openapi.components.ServiceManager;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.util.ListenerNotifier;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class SettingsPanelImpl implements SettingsPanel {
    private static final Logger logger = Logger.getLogger(SettingsPanelImpl.class);

    public static final String USE_OLD_PREVIEW_CHECK_BOX = "useOldPreviewCheckBox";
    public static final String IS_LICENSED_BOOLEAN = "isLicensedBoolean";
    public static final String HAVE_CUSTOMIZABLE_EDITOR = "haveCustomizableEditor";
    public static final String HAVE_CUSTOM_CSS_BOOLEAN = "haveCustomCss";
    protected final ListenerNotifier<SettingsPanel> componentProvider = new ListenerNotifier<SettingsPanel>();

    public static SettingsPanelImpl getInstance() {
        return ServiceManager.getService(SettingsPanelImpl.class);
    }

    public SettingsPanelImpl() {
        SettingsPanelImpl globalSettingsProvider = SettingsPanelImpl.getInstance();
        if (this != globalSettingsProvider) {
            componentProvider.addListener(this);
        }
    }

    public Object notifyGetComponent(@NotNull final String persistName) {
        final Object[] component = new Object[1];
        componentProvider.notifyListeners(new ListenerNotifier.RunnableNotifier<SettingsPanel>() {
            @Override
            public boolean notify(SettingsPanel listener) {
                component[0] = listener.getComponent(persistName);
                return component[0] != null;
            }
        });
        return component[0];
    }

    public void notifyUpdateCustomCssControls() {
        CustomizableEditorTextField textCustomCss = haveCustomizableEditor();
        if (textCustomCss != null && textCustomCss.isPendingTextUpdate()) {
            updateCustomCssControls(haveCustomCss());
        } else {
            Application application = ApplicationManager.getApplication();
            application.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updateCustomCssControls(haveCustomCss());
                }
            }, application.getCurrentModalityState());
        }
    }

    public void notifyUpdateUseOldPreviewControls(final boolean useNewPreview) {
        componentProvider.notifyListeners(new ListenerNotifier.RunnableNotifier<SettingsPanel>() {
            @Override
            public boolean notify(SettingsPanel listener) {
                listener.updateUseOldPreviewControls(useNewPreview);
                return false;
            }
        });
    }

    public void notifyUpdateLicensedControls(final boolean isLicensed) {
        componentProvider.notifyListeners(new ListenerNotifier.RunnableNotifier<SettingsPanel>() {
            @Override
            public boolean notify(SettingsPanel listener) {
                listener.updateLicensedControls(isLicensed);
                return false;
            }
        });
    }

    public void notifyUpdateShowHtmlTextControls(final boolean isShowHtmlText) {
        componentProvider.notifyListeners(new ListenerNotifier.RunnableNotifier<SettingsPanel>() {
            @Override
            public boolean notify(SettingsPanel listener) {
                listener.updateShowHtmlTextControls(isShowHtmlText);
                return false;
            }
        });
    }

    public boolean isLicensed() {
        Boolean isLicensed = (Boolean) getComponent(IS_LICENSED_BOOLEAN);
        return isLicensed != null ? isLicensed : MultiMarkdownPlugin.isLicensed();
    }

    public boolean haveCustomCss() {
        Boolean haveCustomCss = (Boolean) getComponent(HAVE_CUSTOM_CSS_BOOLEAN);
        return haveCustomCss != null ? haveCustomCss : !MultiMarkdownGlobalSettings.getInstance().customCss.getValue().trim().isEmpty();
    }

    @Nullable
    public CustomizableEditorTextField haveCustomizableEditor() {
        return (CustomizableEditorTextField) getComponent(HAVE_CUSTOMIZABLE_EDITOR);
    }

    public boolean isUseOldPreview() {
        JCheckBox useOldPreviewCheckBox = (JCheckBox) getComponent(USE_OLD_PREVIEW_CHECK_BOX);
        return useOldPreviewCheckBox != null ? useOldPreviewCheckBox.isSelected() : MultiMarkdownGlobalSettings.getInstance().useOldPreview.getValue();
    }

    @Override
    public void updateCustomCssControls(boolean haveCustomCss) {

    }

    @Override
    public void updateUseOldPreviewControls(boolean useNewPreview) {

    }

    @Override
    public void updateLicensedControls(boolean isLicensed) {

    }

    @Override
    public void updateShowHtmlTextControls(boolean isShowHtmlText) {

    }

    @Nullable
    @Override
    public Object getComponent(@NotNull String persistName) {
        return null;
    }

    @NotNull
    public static ActionListener getActionListenerBrowseUrl(final String url) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse((URI) new URI(url));
                    } catch (URISyntaxException ex) {
                        // invalid URI, just log
                        logger.info("URISyntaxException on '" + url + "'" + ex.toString());
                    } catch (IOException ex) {
                        logger.info("IOException on '" + url + "'" + ex.toString());
                    }
                }
            }
        };
    }

    @NotNull
    public static HyperlinkListener getHyperLinkListenerBrowseUrl() {
        return new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    URL href = e.getURL();
                    if (href != null) {
                        if (Desktop.isDesktopSupported()) {
                            try {
                                Desktop.getDesktop().browse(href.toURI());
                            } catch (URISyntaxException ex) {
                                // invalid URI, just log
                                logger.error("URISyntaxException on '" + href.toString() + "'" + ex.toString());
                            } catch (IOException ex) {
                                logger.error("IOException on '" + href.toString() + "'" + ex.toString());
                            }
                        }
                    }
                }
            }
        };
    }
}
