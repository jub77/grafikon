package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.Collator;
import java.util.*;

import javax.swing.*;

import net.parostroj.timetable.gui.components.ElementSelectionCheckBoxPanel;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.views.TrainTableColumn;
import net.parostroj.timetable.gui.views.TrainViewColumns;
import net.parostroj.timetable.gui.wrappers.BasicWrapperDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Dialog for column selection for table with train route.
 *
 * @author jub
 */
public class ColumnSelectionDialog extends JDialog {

    private ElementSelectionCheckBoxPanel<TrainTableColumn> panel;

    public ColumnSelectionDialog(Window owner, boolean modal) {
        super(owner, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);

        panel = new ElementSelectionCheckBoxPanel<>(false);
        panel.setListForSelection(Wrapper.getWrapperList(Arrays.asList(TrainTableColumn.values()),
                new BasicWrapperDelegate<TrainTableColumn>() {
                    @Override
                    public String toString(TrainTableColumn element) {
                        return ResourceLoader.getString(element.getKey());
                    }
                }));
        getContentPane().add(panel, BorderLayout.CENTER);

        this.pack();
    }

    public void selectColumns(JTable table, Collection<? extends TrainTableColumn> selected) {
        // update selected columns
        panel.setSelected(selected);
        panel.setListener((column, sel) -> {
            if (sel) {
                table.addColumn(column.createTableColumn());
            } else {
                int index = TrainTableColumn.getIndex(table.getColumnModel(), column);
                table.removeColumn(table.getColumnModel().getColumn(index));
            }
        });
        try {
            this.setVisible(true);
        } finally {
            panel.setListener(null);
        }
    }

    private static class SelectionCheckBoxMenuItem extends JCheckBoxMenuItem {

        TrainTableColumn column;

        public SelectionCheckBoxMenuItem(String text, TrainTableColumn column) {
            super(text);
            this.column = column;
        }
    }

    public static JPopupMenu createPopupMenu(JTable table, Collection<? extends TrainTableColumn> columns,
            TrainViewColumns viewColumns) {
        JPopupMenu menu = new JPopupMenu();
        ItemListener listener = e -> {
            SelectionCheckBoxMenuItem chb = (SelectionCheckBoxMenuItem) e.getItemSelectable();
            if (e.getStateChange() == ItemEvent.SELECTED) {
                table.addColumn(chb.column.createTableColumn());
            } else {
                int index = TrainTableColumn.getIndex(table.getColumnModel(), chb.column);
                table.removeColumn(table.getColumnModel().getColumn(index));
            }
        };
        for (TrainTableColumn column : TrainTableColumn.values()) {
            JCheckBoxMenuItem item = new SelectionCheckBoxMenuItem(ResourceLoader.getString(column.getKey()), column);
            item.setSelected(columns.contains(column));
            item.addItemListener(listener);
            menu.add(item);
        }
        menu.addSeparator();
        addColunmsMenuItems(viewColumns, menu);
        return menu;
    }

    public static void addColunmsMenuItems(TrainViewColumns viewColumns, JPopupMenu menu) {
        JMenu configs = new JMenu(ResourceLoader.getString("train.view.columns.set"));
        JMenu removeConfigs = new JMenu(ResourceLoader.getString("train.view.columns.remove"));
        JMenuItem saveConfig = new JMenuItem(ResourceLoader.getString("train.view.columns.save") + "...");
        viewColumns.getColumnConfigurationKeys().stream()
                .sorted(Collator.getInstance())
                .forEach(key -> {
                    JMenuItem menuItem = new JMenuItem(key);
                    menuItem.addActionListener(event -> viewColumns.loadColumnConfiguration(key));
                    configs.add(menuItem);
                    JMenuItem removeMenuItem = new JMenuItem(key);
                    removeMenuItem.addActionListener(event -> viewColumns.removeColumnConfiguration(key));
                    removeConfigs.add(removeMenuItem);
                });
        saveConfig.addActionListener(event -> {
            String value = (String) JOptionPane.showInputDialog(
                    GuiComponentUtils.getTopLevelComponent(event.getSource()),
                    "", null, JOptionPane.QUESTION_MESSAGE, null, null, "");
            value = ObjectsUtil.checkAndTrim(value);
            if (value != null) {
                viewColumns.storeColumnConfiguration(value);
            }
        });
        menu.add(configs);
        menu.add(saveConfig);
        menu.add(removeConfigs);
    }
}
