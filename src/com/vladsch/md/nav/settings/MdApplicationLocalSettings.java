// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "ApplicationLocal",
        storages = {
                @Storage(value = MdApplicationLocalSettings.MARKDOWN_NAVIGATOR_LOCAL_XML, roamingType = RoamingType.DISABLED)
        }
)
public class MdApplicationLocalSettings implements PersistentStateComponent<Element> {
    private static final Logger LOG = Logger.getInstance("com.vladsch.md.nav.settings");
    public static final String MARKDOWN_NAVIGATOR_LOCAL_XML = "markdown_navigator.local.xml";

    public static MdApplicationLocalSettings getInstance() {
        return ServiceManager.getService(MdApplicationLocalSettings.class);
    }

    @Nullable
    @Override
    public Element getState() {
        MdApplicationSettings applicationSettings = ServiceManager.getService(MdApplicationSettings.class);
        MdApplicationSettings.LocalState localState = applicationSettings.getLocalState();
        LOG.debug("Saving Local State: " + localState + " license state: " + localState.getExtensions());
        //noinspection UnnecessaryLocalVariable
        Element element = localState.saveState(null);
        return element;
    }

    @Override
    public void loadState(@NotNull Element state) {
        MdApplicationSettings applicationSettings = ServiceManager.getService(MdApplicationSettings.class);
        MdApplicationSettings.LocalState localState = applicationSettings.getLocalState();
        LOG.debug("Loading Local State: " + localState + " license state: " + localState.getExtensions());
        localState.loadState(state);
    }
}
