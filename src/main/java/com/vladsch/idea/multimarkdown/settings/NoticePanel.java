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

import com.google.common.io.Resources;
import com.vladsch.idea.multimarkdown.editor.MultiMarkdownPreviewEditor;
import org.apache.commons.codec.Charsets;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class NoticePanel extends SettingsPanelImpl {
    private static final Logger logger = Logger.getLogger(NoticePanel.class);

    private JEditorPane noticesEditorPane;
    private JPanel mainPanel;

    public JComponent getComponent() {
        return mainPanel;
    }

    @Override
    public Object getComponent(@NotNull String componentName) {
        return null;
    }

    public NoticePanel() {
        String htmlText = "";
        try {
            htmlText = Resources.toString(getClass().getResource("/com/vladsch/idea/multimarkdown/NOTICE.html"), Charsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        MultiMarkdownPreviewEditor.setStyleSheet(noticesEditorPane);
        noticesEditorPane.setText(htmlText);

        //tippingJarEditorPane.addHyperlinkListener(listener);
        noticesEditorPane.addHyperlinkListener(getHyperLinkListenerBrowseUrl());
    }
}
