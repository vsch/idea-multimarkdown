// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

@State(name = "Markdown Navigator",
        storages = {
                @Storage(value = MdApplicationSharedSettings.MARKDOWN_NAVIGATOR_SHARED_XML, roamingType = RoamingType.DEFAULT)
        }
)
public class MdApplicationSharedSettings implements PersistentStateComponent<Element> {

    public static final String MARKDOWN_NAVIGATOR_SHARED_XML = "markdown_navigator.shared.xml";

    public static MdApplicationSharedSettings getInstance() {
        return ServiceManager.getService(MdApplicationSharedSettings.class);
    }

    @Nullable
    @Override
    public Element getState() {
        MdApplicationSettings applicationSettings = ServiceManager.getService(MdApplicationSettings.class);
        return applicationSettings.getSharedState().saveState(null);
    }

    @Override
    public void loadState(Element state) {
        if (state.getChildren().size() > 0) {
            MdApplicationSettings applicationSettings = ServiceManager.getService(MdApplicationSettings.class);
            applicationSettings.getSharedState().loadState(state);
        }
    }
}
