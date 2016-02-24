package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

import javax.swing.*;

import net.parostroj.timetable.gui.components.ElementSelectionCheckBoxPanel;
import net.parostroj.timetable.gui.views.TrainTableColumn;
import net.parostroj.timetable.gui.wrappers.BasicWrapperDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
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

    public static JPopupMenu createPopupMenu(JTable table, Collection<? extends TrainTableColumn> columns) {
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
        return menu;
    }
}
