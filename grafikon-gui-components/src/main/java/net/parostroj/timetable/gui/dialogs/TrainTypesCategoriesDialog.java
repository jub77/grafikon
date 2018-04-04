/*
 * TrainTypeCategoriesDialog.java
 *
 * Created on 8.10.2009, 20:00
 */
package net.parostroj.timetable.gui.dialogs;

import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.PenaltyTableRow;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainTypeCategory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * List of train type categories.
 *
 * @author jub
 */
public class TrainTypesCategoriesDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

	private class TrainTypeCategoriesListModel extends AbstractListModel<TrainTypeCategory> {

        private static final long serialVersionUID = 1L;

		@Override
        public int getSize() {
            if (diagram == null) {
                return 0;
            } else {
                return getCategories().size();
            }
        }

        @Override
        public TrainTypeCategory getElementAt(int index) {
            return getCategories().get(index);
        }

        public void addTrainTypeCategory(TrainTypeCategory category) {
            int size = getSize();
            diagram.getTrainTypeCategories().add(category);
            this.fireIntervalAdded(this, size, size);
        }

        public void removeTrainTypeCategory(int index) {
            TrainTypeCategory category = getElementAt(index);
            diagram.getTrainTypeCategories().remove(category);
            this.fireIntervalRemoved(this, index, index);
        }

        public void moveTrainTypeCategory(int index1, int index2) {
            diagram.getTrainTypeCategories().move(index1, index2);
            this.fireContentsChanged(this, index1, index1);
            this.fireContentsChanged(this, index2, index2);
        }

        private List<TrainTypeCategory> getCategories() {
            if (diagram != null)
                return diagram.getTrainTypeCategories();
            else
                return Collections.emptyList();
        }
    }

    private class PenaltyTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

		private TrainTypeCategory getCurrentTrainTypeCategory() {
            TrainTypeCategory category = trainTypeCategoriesList.getSelectedValue();
            return category;
        }

        private List<PenaltyTableRow> getCurrentRows() {
            if (diagram != null && getCurrentTrainTypeCategory() != null) {
                return getCurrentTrainTypeCategory().getPenaltyRows();
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Integer.class;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return ResourceLoader.getString("categories.speed");
                case 1:
                    return ResourceLoader.getString("categories.penalty.acceleration");
                case 2:
                    return ResourceLoader.getString("categories.penalty.deceleration");
                default:
                    return "";
            }
        }

        @Override
        public int getRowCount() {
            if (diagram != null && getCurrentRows() != null)
                return getCurrentRows().size();
            return 0;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return getCurrentRows().get(rowIndex).getSpeed();
                case 1:
                    return getCurrentRows().get(rowIndex).getAcceleration();
                case 2:
                    return getCurrentRows().get(rowIndex).getDeceleration();
                default:
                    return null;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex != 0;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            PenaltyTableRow row = getCurrentRows().get(rowIndex);
            switch (columnIndex) {
                case 1:
                    row.setAcceleration((Integer)aValue);
                    break;
                case 2:
                    row.setDeceleration((Integer)aValue);
            }
        }

        public void updateInfo() {
            this.fireTableStructureChanged();
        }

        public void addPenaltyTableRowForSpeed(int speed) {
            TrainTypeCategory category = getCurrentTrainTypeCategory();
            PenaltyTableRow row = category.createPenaltyTableRow(speed, 0, 0);
            category.addRow(row);
            int index = getCurrentRows().indexOf(row);
            this.fireTableRowsInserted(index, index);
        }

        public void removePenaltyTableRow(int index) {
            TrainTypeCategory category = getCurrentTrainTypeCategory();
            category.removeRow(index);
            this.fireTableRowsDeleted(index, index);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(TrainTypesCategoriesDialog.class);

    private TrainDiagram diagram;
    private TrainTypeCategoriesListModel listModel;
    private final PenaltyTableModel tableModel;

    /** Creates new form EngineClassesDialog */
    public TrainTypesCategoriesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        tableModel = new PenaltyTableModel();
        initComponents();
        weightTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    enableDisableDeleteRow();
                }
            }
        });
    }

    public void showDialog(TrainDiagram diagram) {
        this.diagram = diagram;
        listModel = new TrainTypeCategoriesListModel();
        trainTypeCategoriesList.setModel(listModel);
        this.updateValues();
        this.setVisible(true);
    }

    private void updateValues() {
        // update list of available classes ...
        trainTypeCategoriesList.setModel(listModel);
        tableModel.updateInfo();
        this.enableDisable();
    }

    private void enableDisable() {
        boolean enabled = !trainTypeCategoriesList.isSelectionEmpty();
        weightTable.setEnabled(enabled);
        speedTextField.setEnabled(enabled);
        newRowButton.setEnabled(enabled);
        upButton.setEnabled(enabled);
        downButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        this.enableDisableDeleteRow();
    }

    private void enableDisableDeleteRow() {
        boolean enabled = weightTable.getSelectedRow() != -1;
        deleteRowButton.setEnabled(enabled);
    }

    private boolean deleteAllowed(TrainTypeCategory category) {
        if (category == null)
            return false;
        for (TrainType type : diagram.getTrainTypes()) {
            if (type.getCategory() == category)
                return false;
        }
        return true;
    }

    private void initComponents() {
        javax.swing.JScrollPane scrollPane1 = new javax.swing.JScrollPane();
        trainTypeCategoriesList = new javax.swing.JList<>();
        newButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);
        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);
        upButton = GuiComponentUtils.createButton(GuiIcon.GO_UP, 2);
        downButton = GuiComponentUtils.createButton(GuiIcon.GO_DOWN, 2);
        javax.swing.JScrollPane scrollPane2 = new javax.swing.JScrollPane();
        weightTable = new javax.swing.JTable();
        newRowButton = GuiComponentUtils.createButton(GuiIcon.ADD, 0);
        deleteRowButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 0);
        speedTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();

        trainTypeCategoriesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        trainTypeCategoriesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                trainTypeCategoriesListValueChanged(evt);
            }
        });
        scrollPane1.setViewportView(trainTypeCategoriesList);

        newButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        upButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        downButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        scrollPane2.setPreferredSize(new java.awt.Dimension(0, 200));

        weightTable.setModel(tableModel);
        weightTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPane2.setViewportView(weightTable);

        newRowButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newRowButtonActionPerformed(evt);
            }
        });

        deleteRowButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteRowButtonActionPerformed(evt);
            }
        });

        speedTextField.setColumns(5);

        jLabel1.setText(ResourceLoader.getString("categories.speed") + ":");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                        .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                .addComponent(downButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(upButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(deleteButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(newButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(speedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(newRowButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(deleteRowButton)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(newButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(deleteButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(upButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(downButton))
                        .addComponent(scrollPane1, 0, 0, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(speedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(newRowButton)
                        .addComponent(deleteRowButton))
                    .addContainerGap())
        );
        getContentPane().setLayout(layout);

        pack();
    }

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {
        TrainTypesCategoriesNewDialog dialog = new TrainTypesCategoriesNewDialog(this);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(diagram);
        if (dialog.getNewCategory() != null) {
            TrainTypeCategory category = dialog.getNewCategory();
            listModel.addTrainTypeCategory(category);
            if (dialog.getTemplateCategory() != null) {
                TrainTypeCategory template = dialog.getTemplateCategory();
                // copy
                List<PenaltyTableRow> tRows = template.getPenaltyRows();
                for (PenaltyTableRow tRow : tRows) {
                    PenaltyTableRow row = category.createPenaltyTableRow(tRow.getSpeed(), tRow.getAcceleration(), tRow.getDeceleration());
                    category.addRow(row);
                }
            }
        }
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (!trainTypeCategoriesList.isSelectionEmpty()) {
            int selected = trainTypeCategoriesList.getSelectedIndex();
            if (!this.deleteAllowed(listModel.getElementAt(selected))) {
                GuiComponentUtils.showError(ResourceLoader.getString("dialog.error.delete.in.use"), this);
                return;
            }
            listModel.removeTrainTypeCategory(selected);
            if (selected >= listModel.getSize()) {
                selected--;
            }
            trainTypeCategoriesList.setSelectedIndex(selected);
        }
    }

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // move selected engine class up
        if (!trainTypeCategoriesList.isSelectionEmpty()) {
            int selected = trainTypeCategoriesList.getSelectedIndex();
            selected -= 1;
            if (selected < 0) {
                return;
            }
            listModel.moveTrainTypeCategory(selected + 1, selected);
            trainTypeCategoriesList.setSelectedIndex(selected);
        }
    }

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // move selected engine class down
        if (!trainTypeCategoriesList.isSelectionEmpty()) {
            int selected = trainTypeCategoriesList.getSelectedIndex();
            selected += 1;
            if (selected >= listModel.getSize()) {
                return;
            }
            listModel.moveTrainTypeCategory(selected - 1, selected);
            trainTypeCategoriesList.setSelectedIndex(selected);
        }
    }

    private void trainTypeCategoriesListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            if (!trainTypeCategoriesList.isSelectionEmpty()) {
                weightTable.removeEditor();
                tableModel.updateInfo();
            }
            this.enableDisable();
        }
    }

    private void newRowButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // check if there is a speed specified
        String speedStr = speedTextField.getText();
        try {
            int speed = Integer.parseInt(speedStr);
            if (speed == 0) {
                return;
            }
            tableModel.addPenaltyTableRowForSpeed(speed);
            speedTextField.setText("");

        } catch (NumberFormatException e) {
            log.debug("Cannot convert speed string to int: {}", speedStr);
        }
    }

    private void deleteRowButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = weightTable.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removePenaltyTableRow(selectedRow);
        }
    }

    private javax.swing.JButton deleteButton;
    private javax.swing.JButton deleteRowButton;
    private javax.swing.JButton downButton;
    private javax.swing.JButton newButton;
    private javax.swing.JButton newRowButton;
    private javax.swing.JTextField speedTextField;
    private javax.swing.JList<TrainTypeCategory> trainTypeCategoriesList;
    private javax.swing.JButton upButton;
    private javax.swing.JTable weightTable;
}
