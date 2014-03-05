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

import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.PenaltyTableRow;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainTypeCategory;
import net.parostroj.timetable.utils.IdGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * List of train type categories.
 *
 * @author jub
 */
public class TrainTypesCategoriesDialog extends javax.swing.JDialog {

    private class TrainTypeCategoriesListModel extends AbstractListModel {

        @Override
        public int getSize() {
            if (diagram == null) {
                return 0;
            } else {
                return getCategories().size();
            }
        }

        @Override
        public Object getElementAt(int index) {
            return getCategories().get(index);
        }

        public void addTrainTypeCategory(TrainTypeCategory category) {
            int size = getSize();
            diagram.getPenaltyTable().addTrainTypeCategory(category);
            this.fireIntervalAdded(this, size, size);
        }

        public void removeTrainTypeCategory(int index) {
            TrainTypeCategory category = (TrainTypeCategory) getElementAt(index);
            diagram.getPenaltyTable().removeTrainTypeCategory(category);
            this.fireIntervalRemoved(this, index, index);
        }

        public void moveTrainTypeCategory(int index1, int index2) {
            TrainTypeCategory category = getCategories().get(index1);
            diagram.getPenaltyTable().moveTrainTypeCategory(category, index2);
            this.fireContentsChanged(this, index1, index1);
            this.fireContentsChanged(this, index2, index2);
        }

        private List<TrainTypeCategory> getCategories() {
            if (diagram != null)
                return diagram.getPenaltyTable().getTrainTypeCategories();
            else
                return Collections.emptyList();
        }
    }

    private class PenaltyTableModel extends AbstractTableModel {

        private TrainTypeCategory getCurrentTrainTypeCategory() {
            TrainTypeCategory category = (TrainTypeCategory) trainTypeCategoriesList.getSelectedValue();
            return category;
        }

        private List<PenaltyTableRow> getCurrentRows() {
            if (diagram != null)
                return diagram.getPenaltyTable().getPenaltyTableRowsForCategory(getCurrentTrainTypeCategory());
            else
                return Collections.emptyList();
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
            PenaltyTableRow row = new PenaltyTableRow(speed, 0, 0);
            TrainTypeCategory category = getCurrentTrainTypeCategory();
            diagram.getPenaltyTable().addRowForCategory(category, row);
            int index = getCurrentRows().indexOf(row);
            this.fireTableRowsInserted(index, index);
        }

        public void removePenaltyTableRow(int index) {
            TrainTypeCategory category = getCurrentTrainTypeCategory();
            diagram.getPenaltyTable().removeRowForCategory(category, index);
            this.fireTableRowsDeleted(index, index);
        }
    }

    private TrainDiagram diagram;
    private TrainTypeCategoriesListModel listModel;
    private final PenaltyTableModel tableModel;
    private static final Logger LOG = LoggerFactory.getLogger(TrainTypesCategoriesDialog.class.getName());

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

    public void setTrainDiagram(TrainDiagram diagram) {
        this.diagram = diagram;
        listModel = new TrainTypeCategoriesListModel();
        trainTypeCategoriesList.setModel(listModel);
    }

    public void updateValues() {
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
        scrollPane1 = new javax.swing.JScrollPane();
        trainTypeCategoriesList = new javax.swing.JList();
        nameTextField = new javax.swing.JTextField();
        keyTextField = new javax.swing.JTextField();
        newButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        scrollPane2 = new javax.swing.JScrollPane();
        weightTable = new javax.swing.JTable();
        newRowButton = new javax.swing.JButton();
        deleteRowButton = new javax.swing.JButton();
        speedTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();

        trainTypeCategoriesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        trainTypeCategoriesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                trainTypeCategoriesListValueChanged(evt);
            }
        });
        scrollPane1.setViewportView(trainTypeCategoriesList);

        newButton.setText(ResourceLoader.getString("button.new")); // NOI18N
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        newButton.setEnabled(false);

        deleteButton.setText(ResourceLoader.getString("button.delete")); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        upButton.setText("^");
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        downButton.setText("v");
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        scrollPane2.setPreferredSize(new java.awt.Dimension(0, 200));

        weightTable.setModel(tableModel);
        weightTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPane2.setViewportView(weightTable);

        newRowButton.setText(ResourceLoader.getString("button.new")); // NOI18N
        newRowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newRowButtonActionPerformed(evt);
            }
        });

        deleteRowButton.setText(ResourceLoader.getString("button.delete")); // NOI18N
        deleteRowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteRowButtonActionPerformed(evt);
            }
        });

        speedTextField.setColumns(5);

        jLabel1.setText(ResourceLoader.getString("categories.speed") + ":"); // NOI18N

        jLabel2.setText(ResourceLoader.getString("categories.key") + ":"); // NOI18N

        DocumentListener valuesChangedListener = new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                enableNew();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                enableNew();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                enableNew();
            }

            private void enableNew() {
                newButton.setEnabled(!nameTextField.getText().trim().equals("") && !keyTextField.getText().trim().equals(""));
            }
        };
        keyTextField.getDocument().addDocumentListener(valuesChangedListener);
        nameTextField.getDocument().addDocumentListener(valuesChangedListener);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(scrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(keyTextField)
                            .addComponent(downButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(upButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(newButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nameTextField, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(speedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newRowButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteRowButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(keyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(upButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(downButton))
                    .addComponent(scrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newRowButton)
                    .addComponent(deleteRowButton)
                    .addComponent(speedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap())
        );

        pack();
    }

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (nameTextField != null && !"".equals(nameTextField.getText())) {
            // create new LineClass
            TrainTypeCategory category = new TrainTypeCategory(IdGenerator.getInstance().getId(),
                    nameTextField.getText(),
                    keyTextField.getText());
            listModel.addTrainTypeCategory(category);
            nameTextField.setText("");
            keyTextField.setText("");
        }
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (!trainTypeCategoriesList.isSelectionEmpty()) {
            int selected = trainTypeCategoriesList.getSelectedIndex();
            if (!this.deleteAllowed((TrainTypeCategory)listModel.getElementAt(selected))) {
                ActionUtils.showError(ResourceLoader.getString("dialog.error.delete.in.use"), this);
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
            LOG.debug("Cannot convert speed string to int: {}", speedStr);
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
    private javax.swing.JTextField keyTextField;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton newButton;
    private javax.swing.JButton newRowButton;
    private javax.swing.JScrollPane scrollPane1;
    private javax.swing.JScrollPane scrollPane2;
    private javax.swing.JTextField speedTextField;
    private javax.swing.JList trainTypeCategoriesList;
    private javax.swing.JButton upButton;
    private javax.swing.JTable weightTable;
}
