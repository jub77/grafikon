package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.Collator;
import java.util.*;
import java.util.function.Supplier;

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
public class ColumnSelectionDialog extends EditItemsDialog<String, TrainViewColumns> {

    private ElementSelectionCheckBoxPanel<TrainTableColumn> panel;
    private Supplier<Collection<? extends TrainTableColumn>> selected;

    public ColumnSelectionDialog(Window owner, boolean modal) {
        super(owner, modal, false, true, true, false, false);

        panel = new ElementSelectionCheckBoxPanel<>(false);
        panel.setListForSelection(Wrapper.getWrapperList(Arrays.asList(TrainTableColumn.values()),
                new BasicWrapperDelegate<TrainTableColumn>() {
                    @Override
                    public String toString(TrainTableColumn element) {
                        return ResourceLoader.getString(element.getKey());
                    }
                }));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        panel.setVisibleRows(15);
        getContentPane().add(panel, BorderLayout.SOUTH);

        this.pack();
    }

    public void selectColumns(JTable table, Supplier<Collection<? extends TrainTableColumn>> selected, TrainViewColumns columns) {
        // update selected columns
        this.selected = selected;
        panel.setSelected(selected.get());
        panel.setListener((column, sel) -> {
            if (sel) {
                table.addColumn(column.createTableColumn());
            } else {
                int index = TrainTableColumn.getIndex(table.getColumnModel(), column);
                table.removeColumn(table.getColumnModel().getColumn(index));
            }
        });
        try {
            this.showDialog(columns);
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

    @Override
    protected Collection<String> getList() {
        return element.getColumnConfigurationKeys();
    }

    @Override
    protected void add(String item, int index) {
        element.storeColumnConfiguration(item);
    }

    @Override
    protected void remove(String item) {
        element.removeColumnConfiguration(item);
    }

    @Override
    protected void move(String item, int oldIndex, int newIndex) {
        throw new UnsupportedOperationException("move");
    }

    @Override
    protected boolean deleteAllowed(String item) {
        return true;
    }

    @Override
    protected String createNew(String name) {
        return name;
    }

    @Override
    protected void edit(String item) {
        element.loadColumnConfiguration(item);
        panel.setSelected(selected.get());
    }
}
