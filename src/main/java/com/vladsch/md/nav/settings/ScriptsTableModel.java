// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.editor.util.HtmlScriptResourceProvider;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ScriptsTableModel extends AbstractTableModel {
    static String[] columnNames = {
            "settings.css.table-column.enable",
            "settings.css.table-column.script",
    };

    private List<ScriptResource> scriptResources = new ArrayList<ScriptResource>();

    public final static int SELECTED_COL = 0;
    public final static int NAME_COL = 1;
    public final static int ENABLED_COL = 2;

    public ScriptsTableModel(List<ScriptResource> scriptResources) {
        this.scriptResources = scriptResources;
    }

    public void addScripts(List<ScriptResource> scriptResources) {
        for (ScriptResource scriptResource : scriptResources) {
            this.scriptResources.add((ScriptResource) scriptResource.clone());
        }
        fireTableDataChanged();
    }

    public void replaceScripts(List<ScriptResource> scriptResources) {
        HashMap<String, Boolean> scriptIdMap = new HashMap<String, Boolean>();
        for (ScriptResource script : this.scriptResources) {
            scriptIdMap.put(script.getProviderId(), script.isSelected());
        }

        this.scriptResources.clear();

        for (ScriptResource scriptResource : scriptResources) {
            if (scriptIdMap.containsKey(scriptResource.getProviderId())) {
                scriptResource.setSelected(scriptIdMap.get(scriptResource.getProviderId()));
                this.scriptResources.add(scriptResource);
            }
        }

        fireTableDataChanged();
    }

    public void setSelected(List<HtmlScriptResourceProvider.Info> scriptProviderInfo) {
        HashSet<String> scripts = new HashSet<String>();
        for (HtmlScriptResourceProvider.Info info : scriptProviderInfo) {
            scripts.add(info.getProviderId());
        }

        for (ScriptResource scriptResource : scriptResources) {
            scriptResource.setSelected(scriptResource.isEnabled() && scripts.contains(scriptResource.getProviderId()));
        }

        fireTableDataChanged();
    }

    public void addScript(ScriptResource scriptResource) {
        this.scriptResources.add((ScriptResource) scriptResource.clone());
        fireTableDataChanged();
    }

    public void updateScript(ScriptResource script) {
        for (ScriptResource scriptResource : scriptResources) {
            if (scriptResource.getProviderId().equals(script.getProviderId())) {
                scriptResource.setSelected(script.isSelected());
                break;
            }
        }
        fireTableDataChanged();
    }

    public ArrayList<HtmlScriptResourceProvider.Info> getEnabledScripts() {
        ArrayList<HtmlScriptResourceProvider.Info> infoArrayList = new ArrayList<HtmlScriptResourceProvider.Info>();
        for (ScriptResource scriptResource : scriptResources) {
            if (scriptResource.isSelected()) infoArrayList.add(scriptResource.getProviderInfo());
        }
        return infoArrayList;
    }

    public void removeScript(int rowIndex) {
        this.scriptResources.remove(rowIndex);
        fireTableDataChanged();
    }

    public void setScripts(ArrayList<ScriptResource> resources) {
        this.scriptResources.clear();
        this.scriptResources.addAll(resources);
        fireTableDataChanged();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return scriptResources.size();
    }

    public String getColumnName(int col) {
        return MdBundle.message(columnNames[col]);
    }

    public Object getValueAt(int row, int col) {
        switch (col) {
            case SELECTED_COL:
                return scriptResources.get(row).isSelected();
            case NAME_COL:
                return scriptResources.get(row).getDisplayName();

            case ENABLED_COL:
                return scriptResources.get(row).isEnabled();
        }
        return null;
    }

    public boolean getPerformanceWarningAt(int row) {
        HtmlScriptResourceProvider provider = HtmlScriptResourceProvider.getFromId(scriptResources.get(row).getProviderId());
        return provider != null && provider.isSupportedSetting(MdPreviewSettings.PERFORMANCE_WARNING);
    }

    public boolean getExperimentalWarningAt(int row) {
        HtmlScriptResourceProvider provider = HtmlScriptResourceProvider.getFromId(scriptResources.get(row).getProviderId());
        return provider != null && provider.isSupportedSetting(MdPreviewSettings.EXPERIMENTAL_WARNING);
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        return scriptResources.get(row).isEnabled() && col == SELECTED_COL;
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        switch (col) {
            case SELECTED_COL:
                ScriptResource scriptResource = scriptResources.get(row);
                scriptResource.setSelected(scriptResource.isEnabled() && Boolean.parseBoolean(value.toString()));
                break;
            case NAME_COL:
                //scriptResources.get(row).setDisplayName(value.toString());
                break;
        }
        fireTableCellUpdated(row, col);
    }

    public List<ScriptResource> getScriptResources() {
        return scriptResources;
    }
}
