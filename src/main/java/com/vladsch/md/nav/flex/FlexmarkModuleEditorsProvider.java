// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;

import java.util.ArrayList;
import java.util.List;

public class FlexmarkModuleEditorsProvider implements ModuleConfigurationEditorProvider {
    public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState state) {
        final Module module = state.getRootModel().getModule();
        if (ModuleType.get(module) != FlexmarkExtModuleType.getInstance()) return ModuleConfigurationEditor.EMPTY;

        //final DefaultModuleConfigurationEditorFactory editorFactory = DefaultModuleConfigurationEditorFactory.getInstance();
        List<ModuleConfigurationEditor> editors = new ArrayList<ModuleConfigurationEditor>();
        //editors.add(editorFactory.createModuleContentRootsEditor(state));
        //editors.add(editorFactory.createOutputEditor(state));
        //editors.add(editorFactory.createClasspathEditor(state));
        // FIX: add any module specific configuration information here
        //editors.add(new FlexmarkExtModuleBuildConfEditor(state));
        return editors.toArray(new ModuleConfigurationEditor[editors.size()]);
    }
}
