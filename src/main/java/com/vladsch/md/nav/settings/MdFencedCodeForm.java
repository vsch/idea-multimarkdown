// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.intellij.openapi.project.Project;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.editors.JBComboBoxTableCellEditorComponent;
import com.intellij.ui.table.TableView;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.AbstractTableCellEditor;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.editor.util.HtmlPanelProvider;
import com.vladsch.md.nav.parser.api.MdFencedCodeImageConverter;
import com.vladsch.md.nav.parser.flexmark.MdFencedCodeImageConversionManager;
import com.vladsch.md.nav.settings.api.SettingsFormImpl;
import com.vladsch.plugin.util.ui.HeaderCenterRenderer;
import com.vladsch.plugin.util.ui.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MdFencedCodeForm extends SettingsFormImpl {
    public static final String[] EMPTY_STRINGS = new String[0];
    JPanel myMainPanel;
    private JPanel myViewPanel;
    private JButton myResetDefaults;
    private JPanel myTablesPanel;
    private ListTableModel<FencedCodeRendering> myTableModel;
    TableView<FencedCodeRendering> myDiagnosticTable;
    final Project myProject;
    final MdFencedCodeImageConversionManager myConversionManager;
    final HashMap<String, String> myDefaultVariants;

    public MdFencedCodeForm(RenderingProfileSynchronizer profileSynchronizer) {
        super(profileSynchronizer);

        myProject = profileSynchronizer.getProject();
        myConversionManager = MdFencedCodeImageConversionManager.getInstance(myProject);
        myResetDefaults.addActionListener(e -> resetToDefaults());

        myDefaultVariants = new HashMap<>();
        updateDefaultVariants();
    }

    public boolean selectInfo(String info) {
        if (myDefaultVariants.containsKey(info)) {
            List<FencedCodeRendering> renderings = myTableModel.getItems();
            for (FencedCodeRendering rendering : renderings) {
                if (rendering.info.equals(info)) {
                    myDiagnosticTable.setSelection(Collections.singleton(rendering));
                    return true;
                }
            }
        }
        myDiagnosticTable.setSelection(Collections.emptyList());
        return false;
    }

    void updateDefaultVariants() {
        HashMap<String, String> codeConversions = myProfileSynchronizer.getHtmlSettings().getFencedCodeConversions();

        for (String info : codeConversions.keySet()) {
            myDefaultVariants.put(info, codeConversions.get(info));

            List<MdFencedCodeImageConverter> converters = myConversionManager.getImageConverters(info);
            if (!converters.isEmpty()) {
                // set it to first available variant of first available converter
                String[] variants = converters.get(0).getConversionVariants(info);
                if (variants.length > 0) {
                    myDefaultVariants.put(info, variants[0]);
                }
            }
        }
    }

    void resetToDefaults() {
        List<FencedCodeRendering> renderings = myTableModel.getItems();
        ArrayList<FencedCodeRendering> defaultRenderings = new ArrayList<>();
        boolean isModified = false;

        for (FencedCodeRendering rendering : renderings) {
            if (myDefaultVariants.containsKey(rendering.info)) {
                defaultRenderings.add(new FencedCodeRendering(rendering.info, myDefaultVariants.get(rendering.info)));
                isModified = true;
            } else {
                defaultRenderings.add(rendering);
            }
        }

        if (isModified) {
            myTableModel.setItems(defaultRenderings);
            updateOptionalSettings();
        }
    }

    boolean canResetToDefaults() {
        List<FencedCodeRendering> renderings = myTableModel.getItems();
        return renderings.stream().anyMatch(rendering -> myDefaultVariants.containsKey(rendering.info) && !rendering.variant.equals(myDefaultVariants.get(rendering.info)));
    }

    @NotNull
    public JComponent getComponent() {
        return myMainPanel;
    }

    @Override
    protected void disposeResources() {

    }

    @Nullable
    @Override
    protected JPanel getMainFormPanel() {
        return myMainPanel;
    }

    @Override
    protected void updatePanelProviderDependentComponents(@NotNull HtmlPanelProvider fromProvider, @NotNull HtmlPanelProvider toProvider, boolean isInitialShow) {

    }

    @Override
    protected void updateFormOnReshow(boolean isInitialShow) {

    }

    @Override
    public void updateOptionalSettings() {
        myResetDefaults.setEnabled(canResetToDefaults());
        updateExtensionsOptionalSettings();
        RenderingProfileSynchronizer.updateCssScriptSettings(myProfileSynchronizer);
    }

    @Override
    public void reset(@NotNull MdRenderingProfileHolder settings) {
        ArrayList<FencedCodeRendering> renderings = getHtmlSettingsRenderings(settings);
        myTableModel.setItems(renderings);
        updateOptionalSettings();
    }

    @NotNull
    private ArrayList<FencedCodeRendering> getHtmlSettingsRenderings(@NotNull MdRenderingProfileHolder settings) {
        HashMap<String, String> conversions = settings.getRenderingProfile().getHtmlSettings().getFencedCodeConversions();
        ArrayList<FencedCodeRendering> renderings = new ArrayList<>();
        for (Map.Entry<String, String> entry : conversions.entrySet()) {
            renderings.add(new FencedCodeRendering(entry.getKey(), entry.getValue()));
        }

        renderings.sort(Comparator.comparing(rendering -> rendering.info));
        return renderings;
    }

    @Override
    public void apply(@NotNull MdRenderingProfileHolder settings) {
        HashMap<String, String> conversions = settings.getRenderingProfile().getHtmlSettings().getFencedCodeConversions();
        conversions.clear();
        List<FencedCodeRendering> renderings = myTableModel.getItems();
        for (FencedCodeRendering rendering : renderings) {
            conversions.put(rendering.info, rendering.variant);
        }
    }

    @Override
    public boolean isModified(@NotNull MdRenderingProfileHolder settings) {
        ArrayList<FencedCodeRendering> settingsRenderings = getHtmlSettingsRenderings(settings);
        List<FencedCodeRendering> renderings = myTableModel.getItems();
        if (settingsRenderings.size() != renderings.size()) return true;
        int iMax = renderings.size();
        for (int i = 0; i < iMax; i++) {
            if (!settingsRenderings.get(i).equals(renderings.get(i))) return true;
        }

        return false;
    }

    private static class FencedCodeRendering {
        final public @NotNull String info;
        public @NotNull String variant;

        public FencedCodeRendering(@NotNull String info, @NotNull String variant) {
            this.info = info;
            this.variant = variant;
        }

        public boolean isValid(@NotNull MdFencedCodeImageConversionManager manager) {
            return manager.getImageConverter(info, variant) != null;
        }

        public boolean isDefault(@NotNull MdFencedCodeForm form) {
            return variant.equals(form.myDefaultVariants.get(info));
        }

        @NotNull
        public String unavailableReason(@NotNull MdFencedCodeImageConversionManager manager, @NotNull MdRenderingProfile renderingProfile) {
            for (MdFencedCodeImageConverter converter : MdFencedCodeImageConverter.EXTENSIONS.getValue()) {
//                MdFencedCodeImageConverter converter = manager.getImageConverter(info, variant);
//                if (converter == null) return "";
                String unavailable = converter.isAvailable(info, variant, renderingProfile);
                if (unavailable != null) return unavailable;
            }
            return "";
        }

        public boolean isAvailable(@NotNull MdFencedCodeImageConversionManager manager, @NotNull MdRenderingProfile renderingProfile) {
            return unavailableReason(manager, renderingProfile).isEmpty();
        }

        public void setVariant(@NotNull String variant) {
            this.variant = variant;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FencedCodeRendering rendering = (FencedCodeRendering) o;

            if (!info.equals(rendering.info)) return false;
            return variant.equals(rendering.variant);
        }

        @Override
        public int hashCode() {
            int result = info.hashCode();
            result = 31 * result + variant.hashCode();
            return result;
        }
    }

    private void createUIComponents() {
//        ElementProducer<FencedCodeRendering> producer = new ElementProducer<FencedCodeRendering>() {
//            @Override
//            public FencedCodeRendering createElement() {
//                return new FencedCodeRendering();
//            }
//
//            @Override
//            public boolean canCreateElement() {
//                return false;
//            }
//        };

        // NOTE: these fields are not initialized until this method returns 
        Project myProject = myProfileSynchronizer.getProject();
        MdFencedCodeImageConversionManager myConversionManager = MdFencedCodeImageConversionManager.getInstance(myProject);
        MdRenderingProfile renderingProfile = myProfileSynchronizer.getRenderingProfile();

        GridConstraints constraints = new GridConstraints(0, 0, 1, 1
                , GridConstraints.ANCHOR_CENTER
                , GridConstraints.FILL_BOTH
                , GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK
                , GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK
                , null, null, null);

        //noinspection rawtypes
        ColumnInfo[] diagnosticColumns = {
                new InfoColumn(this, myConversionManager, renderingProfile),
                new VariantColumn(this, myConversionManager, renderingProfile),
                new DescriptionColumn(this, myConversionManager, renderingProfile),
        };

        myTableModel = new ListTableModel<>(diagnosticColumns, new ArrayList<>(), 0);
        myDiagnosticTable = new TableView<>(myTableModel);
        myDiagnosticTable.setRowSelectionAllowed(true);

        TableColumn infoColumn = myDiagnosticTable.getTableHeader().getColumnModel().getColumn(0);
        infoColumn.setPreferredWidth(200);
        infoColumn.setMaxWidth(200);

        TableColumn variantColumn = myDiagnosticTable.getTableHeader().getColumnModel().getColumn(1);
        variantColumn.setPreferredWidth(300);
        variantColumn.setMaxWidth(300);

        TableColumn descriptionColumn = myDiagnosticTable.getTableHeader().getColumnModel().getColumn(2);
        descriptionColumn.setPreferredWidth(500);

        myDiagnosticTable.getTableHeader().setDefaultRenderer(new HeaderCenterRenderer(myDiagnosticTable, 2, 3, 4));

        ToolbarDecorator tableDecorator = ToolbarDecorator.createDecorator(myDiagnosticTable, null /*producer*/);
        tableDecorator.disableAddAction();
        tableDecorator.disableRemoveAction();
        tableDecorator.disableUpDownActions();

        myTablesPanel = new JPanel(new GridLayoutManager(1, 1));
        myTablesPanel.add(tableDecorator.createPanel(), constraints);

        myViewPanel = new JPanel(new BorderLayout());
        myViewPanel.add(myTablesPanel, BorderLayout.CENTER);

        myTableModel.addTableModelListener(e -> updateOptionalSettings());
    }

    static class InfoColumn extends MyColumnInfo<String> {
        InfoColumn(MdFencedCodeForm form, MdFencedCodeImageConversionManager conversionManager, MdRenderingProfile renderingProfile) {
            super(MdBundle.message("settings.fenced-code.table-column.info"), form, conversionManager, renderingProfile);
        }

        @Override
        public boolean isCellEditable(final FencedCodeRendering item) {
            return false;
        }

        public TableCellRenderer getRenderer(final FencedCodeRendering rendering) {
            return new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    final Component rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if (rendering.isValid(myConversionManager)) {
                        if (!rendering.isAvailable(myConversionManager, myRenderingProfile)) {
//                            setForeground(Helpers.errorColor());
                        } else if (!rendering.isDefault(myForm)) {
                            setFont(getFont().deriveFont(Font.BOLD));
                        }
                    } else {
                        setForeground(Helpers.unusedColor());
                    }

                    setText(rendering.info);
                    return rendererComponent;
                }
            };
        }

//        public TableCellEditor getEditor(final FencedCodeRendering rendering) {
//            return new AbstractHookingTableEditor() {
//                private final JTextField myEditor = new JTextField();
//
//                public Object getCellEditorValue() {
//                    return myEditor.getText();
//                }
//
//                @Override
//                public JComponent getEditorComponent() {
//                    return myEditor;
//                }
//
//                public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//                    myEditor.setText((String) value);
//                    return myEditor;
//                }
//            };
//        }

        public String valueOf(final FencedCodeRendering rendering) {
            return rendering.info;
        }

//        public void setValue(final FencedCodeRendering rendering, final String value) {
//            if (rendering != null) {
//                rendering.setInfo(Integer.parseInt(value));
//            }
//        }
    }

    static class VariantColumn extends MyColumnInfo<String> {
        public static final int ROW_HEIGHT_OFFSET = 2;

        VariantColumn(MdFencedCodeForm form, MdFencedCodeImageConversionManager conversionManager, MdRenderingProfile renderingProfile) {
            super(MdBundle.message("settings.fenced-code.table-column.variant"), form, conversionManager, renderingProfile);
        }

        @Override
        public boolean isCellEditable(final FencedCodeRendering item) {
            return true;
        }

        String[] getVariants(FencedCodeRendering rendering) {
            String[] strings = myConversionManager.getConversionDisplayTexts(rendering.info, true, false).toArray(EMPTY_STRINGS);
            return strings;
        }

        public TableCellRenderer getRenderer(final FencedCodeRendering rendering) {
            return new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    final Component rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if (rendering.isValid(myConversionManager)) {
                        if (!rendering.isAvailable(myConversionManager, myRenderingProfile)) {
                            setForeground(Helpers.errorColor());
                        } else if (!rendering.isDefault(myForm)) {
                            setFont(getFont().deriveFont(Font.BOLD));
                        }
                    } else {
                        setForeground(Helpers.unusedColor());
                    }
                    setText(myConversionManager.getConversionVariantDisplayText(rendering.info, rendering.variant));
                    return rendererComponent;
                }

                @Override
                protected void setValue(Object value) {
                    super.setValue(value);
                }
            };
        }

        public TableCellEditor getEditor(final FencedCodeRendering rendering) {
            return new AbstractTableCellEditor() {
                private final JBComboBoxTableCellEditorComponent myChooser = new JBComboBoxTableCellEditorComponent();

                public Object getCellEditorValue() {
                    return myChooser.getEditorValue();
                }

                public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                    myChooser.setCell(table, row, column);
                    //noinspection RedundantCast
                    myChooser.setOptions((Object[]) getVariants(rendering));
                    myChooser.setDefaultValue(value);
                    myChooser.setToString(o -> (String) o);
                    return myChooser;
                }
            };
        }

        public String valueOf(final FencedCodeRendering rendering) {
            String text = myConversionManager.getConversionVariantDisplayText(rendering.info, rendering.variant);
            return text == null ? "" : text;
        }

        public void setValue(final FencedCodeRendering rendering, final String choice) {
            if (rendering != null) {
                String variant = myConversionManager.getConversionVariant(rendering.info, choice);
                rendering.setVariant(variant == null ? "" : variant);
            }
        }
    }

    static class DescriptionColumn extends MyColumnInfo<String> {
        DescriptionColumn(MdFencedCodeForm form, MdFencedCodeImageConversionManager conversionManager, MdRenderingProfile renderingProfile) {
            super(MdBundle.message("settings.fenced-code.table-column.description"), form, conversionManager, renderingProfile);
        }

        @Override
        public boolean isCellEditable(final FencedCodeRendering item) {
            return false;
        }

        public TableCellRenderer getRenderer(final FencedCodeRendering rendering) {
            return new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    final Component rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    String description = "";

                    if (rendering.isValid(myConversionManager)) {
                        if (!rendering.isAvailable(myConversionManager, myRenderingProfile)) {
                            description = rendering.unavailableReason(myConversionManager, myRenderingProfile);
                            setFont(getFont().deriveFont(Font.BOLD));
//                            setForeground(Helpers.errorColor());
                        } else if (!rendering.isDefault(myForm)) {
                            setFont(getFont().deriveFont(Font.BOLD));
                        }
                    } else {
                        setForeground(Helpers.unusedColor());
                    }

                    if (description.isEmpty()) {
                        description = myConversionManager.getConversionVariantDescription(rendering.info, rendering.variant);
                    }
                    setText(description);

                    return rendererComponent;
                }
            };
        }

        public TableCellEditor getEditor(final FencedCodeRendering rendering) {
            return new AbstractHookingTableEditor() {
                private final JTextField myEditor = new JTextField();

                public Object getCellEditorValue() {
                    return myEditor.getText();
                }

                @Override
                public JComponent getEditorComponent() {
                    return myEditor;
                }

                public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                    myEditor.setText((String) value);
                    return myEditor;
                }
            };
        }

        public String valueOf(final FencedCodeRendering rendering) {
            return myConversionManager.getConversionVariantDescription(rendering.info, rendering.variant);
        }

//        public void setValue(final FencedCodeRendering rendering, final String value) {
//            if (rendering != null) {
//                rendering.setDescription(value);
//            }
//        }
    }

    protected interface HookingCellEditor extends TableCellEditor {
        JComponent getEditorComponent();
    }

    static abstract class AbstractHookingTableEditor extends AbstractCellEditor implements HookingCellEditor {
    }

    private static abstract class MyColumnInfo<T> extends ColumnInfo<FencedCodeRendering, T> {
        final MdFencedCodeImageConversionManager myConversionManager;
        final MdRenderingProfile myRenderingProfile;
        final MdFencedCodeForm myForm;

        protected MyColumnInfo(final String name, MdFencedCodeForm form, MdFencedCodeImageConversionManager conversionManager, MdRenderingProfile renderingProfile) {
            super(name);
            myForm = form;
            myConversionManager = conversionManager;
            myRenderingProfile = renderingProfile;
        }

        @Override
        public boolean isCellEditable(final FencedCodeRendering item) {
            return true;
        }
    }
}
