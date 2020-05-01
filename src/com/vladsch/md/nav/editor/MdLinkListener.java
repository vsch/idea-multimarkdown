// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor;

import org.jetbrains.annotations.NotNull;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.net.URL;

public class MdLinkListener implements HyperlinkListener {

    private final JEditorPane editor;
    private final ExternalLinkLauncher linkLauncher;

    public MdLinkListener(@NotNull JEditorPane editor, @NotNull ExternalLinkLauncher linkLauncher) {
        this.editor = editor;
        this.linkLauncher = linkLauncher;
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (HyperlinkEvent.EventType.ACTIVATED == e.getEventType()) {
            // Try to get a URL from the event.
            URL target = e.getURL();
            if (target == null) {
                if (e.getDescription().startsWith("#")) {
                    editor.scrollToReference(e.getDescription().substring(1));
                } else {
                    linkLauncher.launchExternalLink(e.getDescription());
                }
            } else {
                linkLauncher.launchExternalLink(e.getDescription());
            }
        }
    }
}
