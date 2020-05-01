// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;

import javax.swing.JComponent;

/**
 * User: anna Date: Nov 24, 2004
 */
public class FlexmarkExtModuleBuildConfEditor implements ModuleConfigurationEditor {
    private final FlexmarkExtModuleBuildConfiguration myBuildProperties;
    private FlexmarkExtModuleSettingsForm myForm = null;
    private final Module myModule;

    public FlexmarkExtModuleBuildConfEditor(ModuleConfigurationState state) {
        myModule = state.getRootModel().getModule();
        FlexmarkExtModuleBuildConfiguration buildProperties = FlexmarkExtModuleBuildConfiguration.getInstance(myModule);
        if (buildProperties == null) buildProperties = new FlexmarkExtModuleBuildConfiguration(myModule);
        myBuildProperties = buildProperties;
    }

    public JComponent createComponent() {
        myForm = new FlexmarkExtModuleSettingsForm(null, null, myBuildProperties);
        return myForm.getComponent();
    }

    public boolean isModified() {
        //final String pluginXmlPath = new File(myBuildProperties.getPluginXmlPath()).getParentFile().getParent(); //parent for meta-inf
        //boolean modified = !Comparing.strEqual(myPluginXML.getText(), pluginXmlPath);
        //final boolean selected = myUseUserManifest.isSelected();
        //modified |= myBuildProperties.isUseUserManifest() != selected;
        //if (selected) {
        //    modified |= !Comparing.strEqual(myManifest.getText(), myBuildProperties.getManifestPath());
        //}
        FlexmarkModuleOptions options = myForm.getOptions();

        return !myBuildProperties.getOptions().equals(options);
    }

    public void apply() throws ConfigurationException {
        myBuildProperties.setOptions(myForm.getOptions());
    }

    public void reset() {
        myForm.setOptions(myBuildProperties.getOptions());
    }

    public void disposeUIResources() {

    }

    public void saveData() {

    }

    public String getDisplayName() {
        return PluginBundle.message("module.configuration.title");
    }

    public String getHelpTopic() {
        return null; //"plugin.configuring";
    }

    public void moduleStateChanged() {
    }
}
