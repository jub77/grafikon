/*
 * ColumnsDialog.java
 *
 * Created on 12.01.2009, 14:22:04
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import net.parostroj.timetable.gui.views.TrainTableColumn;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Dialog of displayed columns in train view.
 *
 * @author jub
 */
public class ColumnsDialog extends javax.swing.JDialog {

    private final Map<TrainTableColumn,JCheckBox> columnMap;
    private Map<TrainTableColumn, TableColumn> currentColumns;
    private JTable table;

    /** Creates new form TrainsFilterDialog */
    public ColumnsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        columnsPanel.setLayout(new GridLayout(TrainTableColumn.values().length, 1));
        columnMap = new EnumMap<TrainTableColumn, JCheckBox>(TrainTableColumn.class);
        for (TrainTableColumn column : TrainTableColumn.values()) {
            JCheckBox checkBox = new JCheckBox(ResourceLoader.getString(column.getKey()));
            columnMap.put(column, checkBox);
            checkBox.setSelected(false);
            columnsPanel.add(checkBox);
        }
        this.pack();
    }

    public void updateColumns(JTable table) {
        // clear
        for (JCheckBox ch : columnMap.values()) {
            ch.setSelected(false);
        }

        // collect current
        currentColumns = new EnumMap<TrainTableColumn, TableColumn>(TrainTableColumn.class);
        TableColumnModel tcm = table.getColumnModel();
        Enumeration<TableColumn> columns = tcm.getColumns();
        while (columns.hasMoreElements()) {
            TableColumn c = columns.nextElement();
            TrainTableColumn tc = TrainTableColumn.getColumn(c.getModelIndex());
            currentColumns.put(tc, c);
            columnMap.get(tc).setSelected(true);
        }

        this.table = table;
    }

    private void initComponents() {
        columnsPanel = new javax.swing.JPanel();
        okPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        columnsPanel.setLayout(new java.awt.GridLayout(1, 0));
        getContentPane().add(columnsPanel, java.awt.BorderLayout.CENTER);

        okPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        okPanel.add(okButton);

        getContentPane().add(okPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        for (Map.Entry<TrainTableColumn, JCheckBox> entry :  columnMap.entrySet()) {
            if (entry.getValue().isSelected()) {
                if (!currentColumns.keySet().contains(entry.getKey())) {
                    table.addColumn(entry.getKey().createTableColumn());
                }
            } else {
                if (currentColumns.keySet().contains(entry.getKey())) {
                    table.removeColumn(currentColumns.get(entry.getKey()));
                }
            }
        }

        this.setVisible(false);
    }

    private javax.swing.JPanel columnsPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel okPanel;
}
