// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.openapi.Disposable;
import com.vladsch.md.nav.MdResourceResolverImpl;
import com.vladsch.md.nav.editor.swing.SwingHtmlPanel;
import com.vladsch.md.nav.settings.api.SettingsFormImpl;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import java.io.IOException;

public class MdNoticesSettingsForm implements Disposable {
    private JEditorPane noticesEditorPane;
    private JPanel mainPanel;

    public JComponent getComponent() {
        return mainPanel;
    }

    @Override
    public void dispose() {

    }

    public MdNoticesSettingsForm() {
        String htmlText = "";
        try {
            MdResourceResolverImpl resourceResolver = MdResourceResolverImpl.getInstance();
            htmlText = resourceResolver.getResourceFileContent("/com/vladsch/md/nav/NOTICE.html", MdNoticesSettingsForm.class);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        SwingHtmlPanel.Companion.setStyleSheet(noticesEditorPane, null);
        noticesEditorPane.setText(htmlText);

        //tippingJarEditorPane.addHyperlinkListener(listener);
        noticesEditorPane.addHyperlinkListener(SettingsFormImpl.getHyperLinkListenerBrowseUrl());
    }
}
