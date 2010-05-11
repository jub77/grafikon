/*
 * EngineClassesDialog.java
 *
 * Created on 2. červen 2008, 16:08
 */
package net.parostroj.timetable.gui.dialogs;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.WeightTableRow;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * List of engine class.
 * 
 * @author jub
 */
public class EngineClassesDialog extends javax.swing.JDialog {

    private class EngineClassesListModel extends AbstractListModel {

        @Override
        public int getSize() {
            if (model.getDiagram() == null) {
                return 0;
            } else {
                return model.getDiagram().getEngineClasses().size();
            }
        }

        @Override
        public Object getElementAt(int index) {
            return model.getDiagram().getEngineClasses().get(index);
        }

        public void updateInfo() {
            this.fireContentsChanged(this, 0, getSize());
        }

        public void addEngineClass(EngineClass clazz) {
            int size = getSize();
            model.getDiagram().addEngineClass(clazz);
            this.fireIntervalAdded(this, size, size);
        }

        public void removeEngineClass(int index) {
            EngineClass clazz = (EngineClass) getElementAt(index);
            model.getDiagram().removeEngineClass(clazz);
            this.fireIntervalRemoved(model, index, index);
        }

        public void moveEngineClass(int index1, int index2) {
            model.getDiagram().moveEngineClass(index1, index2);
            this.fireContentsChanged(this, index1, index1);
            this.fireContentsChanged(this, index2, index2);
        }
    }

    private class WeightTableModel extends AbstractTableModel {

        private EngineClass getCurrentEngineClass() {
            EngineClass clazz = (EngineClass) engineClassesList.getSelectedValue();
            return clazz;
        }

        private LineClass getLineClass(int index) {
            return model.getDiagram().getNet().getLineClasses().get(index - 1);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Integer.class;
        }

        @Override
        public String getColumnName(int column) {
            if (column == 0) {
                return ResourceLoader.getString("editengingeclasses.speed");
            }
            if (model != null && model.getDiagram() != null) {
                return model.getDiagram().getNet().getLineClasses().get(column - 1).getName();
            } else {
                return null;
            }
        }

        @Override
        public int getRowCount() {
            if (getCurrentEngineClass() == null) {
                return 0;
            } else {
                return getCurrentEngineClass().getWeightTable().size();
            }
        }

        @Override
        public int getColumnCount() {
            if (model != null && model.getDiagram() != null) {
                return model.getDiagram().getNet().getLineClasses().size() + 1;
            } else {
                return 0;
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return getCurrentEngineClass().getWeightTable().get(rowIndex).getSpeed();
            } else {
                return getCurrentEngineClass().getWeightTable().get(rowIndex).getWeights().get(getLineClass(columnIndex));
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex != 0;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (Integer.valueOf(0).equals(aValue)) {
                aValue = null;
            }
            WeightTableRow row = getCurrentEngineClass().getWeightTable().get(rowIndex);
            LineClass clazz = getLineClass(columnIndex);
            if (aValue == null) {
                row.removeWeightInfo(clazz);
            } else {
                row.setWeightInfo(clazz, (Integer) aValue);
            }
            model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.ENGINE_CLASSES_CHANGED, model));
        }

        public void updateInfo() {
            this.fireTableStructureChanged();
        }

        public void addWeightTableRowForSpeed(int speed) {
            EngineClass clazz = getCurrentEngineClass();
            WeightTableRow row = clazz.createWeightTableRow(speed);
            clazz.addWeightTableRow(row);
            int index = clazz.getWeightTable().indexOf(row);
            this.fireTableRowsInserted(index, index);
            model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.ENGINE_CLASSES_CHANGED, model));
        }

        public void removeWeightTableRow(int index) {
            EngineClass clazz = getCurrentEngineClass();
            clazz.removeWeightTableRow(index);
            this.fireTableRowsDeleted(index, index);
            model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.ENGINE_CLASSES_CHANGED, model));
        }
    }
    private ApplicationModel model;
    private EngineClassesListModel listModel;
    private WeightTableModel tableModel;
    private static final Logger LOG = Logger.getLogger(EngineClassesDialog.class.getName());

    /** Creates new form EngineClassesDialog */
    public EngineClassesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        tableModel = new WeightTableModel();
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

    public void setModel(ApplicationModel model) {
        this.model = model;
        listModel = new EngineClassesListModel();
        engineClassesList.setModel(listModel);
    }

    public void updateValues() {
        // update list of available classes ...
        listModel.updateInfo();
        tableModel.updateInfo();
        this.enableDisable();
    }

    private void enableDisable() {
        boolean enabled = !engineClassesList.isSelectionEmpty();
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane1 = new javax.swing.JScrollPane();
        engineClassesList = new javax.swing.JList();
        nameTextField = new javax.swing.JTextField();
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

        engineClassesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        engineClassesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                engineClassesListValueChanged(evt);
            }
        });
        scrollPane1.setViewportView(engineClassesList);

        newButton.setText(ResourceLoader.getString("button.new")); // NOI18N
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

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

        jLabel1.setText(ResourceLoader.getString("editengingeclasses.speed") + ":"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
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
                    .addComponent(scrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(upButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(downButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newRowButton)
                    .addComponent(deleteRowButton)
                    .addComponent(speedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
    if (nameTextField != null && !"".equals(nameTextField.getText())) {
        // create new LineClass
        EngineClass clazz = new EngineClass(IdGenerator.getInstance().getId(), nameTextField.getText());
        listModel.addEngineClass(clazz);
        nameTextField.setText("");
        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.ENGINE_CLASSES_CHANGED, model));
    }
}//GEN-LAST:event_newButtonActionPerformed

private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
    if (!engineClassesList.isSelectionEmpty()) {
        int selected = engineClassesList.getSelectedIndex();
        listModel.removeEngineClass(selected);
        if (selected >= listModel.getSize()) {
            selected--;
        }
        engineClassesList.setSelectedIndex(selected);
        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.ENGINE_CLASSES_CHANGED, model));
    }
}//GEN-LAST:event_deleteButtonActionPerformed

private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
    // move selected engine class up
    if (!engineClassesList.isSelectionEmpty()) {
        int selected = engineClassesList.getSelectedIndex();
        selected -= 1;
        if (selected < 0) {
            return;
        }
        listModel.moveEngineClass(selected + 1, selected);
        engineClassesList.setSelectedIndex(selected);
        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.ENGINE_CLASSES_CHANGED, model));
    }
}//GEN-LAST:event_upButtonActionPerformed

private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
    // move selected engine class down
    if (!engineClassesList.isSelectionEmpty()) {
        int selected = engineClassesList.getSelectedIndex();
        selected += 1;
        if (selected >= listModel.getSize()) {
            return;
        }
        listModel.moveEngineClass(selected - 1, selected);
        engineClassesList.setSelectedIndex(selected);
        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.ENGINE_CLASSES_CHANGED, model));
    }
}//GEN-LAST:event_downButtonActionPerformed

private void engineClassesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_engineClassesListValueChanged
    if (!evt.getValueIsAdjusting()) {
        if (!engineClassesList.isSelectionEmpty()) {
            weightTable.removeEditor();
            tableModel.updateInfo();
        }
        this.enableDisable();
    }
}//GEN-LAST:event_engineClassesListValueChanged

private void newRowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newRowButtonActionPerformed
    // check if there is a speed specified
    try {
        int speed = Integer.parseInt(speedTextField.getText());
        if (speed == 0) {
            return;
        }
        tableModel.addWeightTableRowForSpeed(speed);
        speedTextField.setText("");

    } catch (NumberFormatException e) {
        LOG.log(Level.FINEST, "Cannot convert speed string to int.", e);
    }
}//GEN-LAST:event_newRowButtonActionPerformed

private void deleteRowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteRowButtonActionPerformed
    int selectedRow = weightTable.getSelectedRow();
    if (selectedRow != -1) {
        tableModel.removeWeightTableRow(selectedRow);
    }
}//GEN-LAST:event_deleteRowButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton deleteRowButton;
    private javax.swing.JButton downButton;
    private javax.swing.JList engineClassesList;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton newButton;
    private javax.swing.JButton newRowButton;
    private javax.swing.JScrollPane scrollPane1;
    private javax.swing.JScrollPane scrollPane2;
    private javax.swing.JTextField speedTextField;
    private javax.swing.JButton upButton;
    private javax.swing.JTable weightTable;
    // End of variables declaration//GEN-END:variables
}
