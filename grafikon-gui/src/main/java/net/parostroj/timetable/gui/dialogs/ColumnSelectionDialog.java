package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.*;

import net.parostroj.timetable.gui.views.TrainTableColumn;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Dialog for column selection for table with train route.
 *
 * @author jub
 */
public class ColumnSelectionDialog extends JDialog {

    private static final int COUNT = 15;

    private int lines;
    private ItemListener itemListener;
    private Map<TrainTableColumn, SelectionCheckBox> map;

    public ColumnSelectionDialog(Window owner, boolean modal, Collection<? extends TrainTableColumn> columns) {
        super(owner, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);

        this.lines = columns.size();
        this.map = new EnumMap<>(TrainTableColumn.class);

        JScrollPane scrollPane = new JScrollPane() {
            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                size.height = size.height * COUNT / lines;
                return size;
            }
        };
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel list = new JPanel();
        scrollPane.setViewportView(list);

        this.initialize(list, columns);
        this.pack();
    }

    public void selectColumns(JTable table, Collection<? extends TrainTableColumn> selected) {
        // update selected columns
        this.updateColumns(selected);
        this.itemListener = e -> {
            SelectionCheckBox chb = (SelectionCheckBox) e.getItemSelectable();
            if (e.getStateChange() == ItemEvent.SELECTED) {
                table.addColumn(chb.column.createTableColumn());
            } else {
                int index = TrainTableColumn.getIndex(table.getColumnModel(), chb.column);
                table.removeColumn(table.getColumnModel().getColumn(index));
            }
        };
        try {
            this.setVisible(true);
        } finally {
            this.itemListener = null;
        }
    }

    private void updateColumns(Collection<? extends TrainTableColumn> columns) {
        for (SelectionCheckBox checkBox : map.values()) {
            checkBox.setSelected(false);
        }
        for (TrainTableColumn column : columns) {
            map.get(column).setSelected(true);
        }
    }

    private void initialize(JPanel list, Collection<? extends TrainTableColumn> columns) {
        ItemListener listener = e -> { if (itemListener != null) itemListener.itemStateChanged(e); };
        Color color = UIManager.getColor("List.background");
        BoxLayout layout = new BoxLayout(list, BoxLayout.Y_AXIS);
        list.setLayout(layout);
        list.setBackground(color);
        for (TrainTableColumn column : columns) {
            SelectionCheckBox checkBox = new SelectionCheckBox(ResourceLoader.getString(column.getKey()), column);
            checkBox.setBackground(color);
            checkBox.addItemListener(listener);
            list.add(checkBox);
            map.put(column, checkBox);
        }
        list.add(Box.createVerticalGlue());
    }

    private static class SelectionCheckBox extends JCheckBox {

        TrainTableColumn column;

        public SelectionCheckBox(String text, TrainTableColumn column) {
            super(text);
            this.column = column;
        }
    }
}
