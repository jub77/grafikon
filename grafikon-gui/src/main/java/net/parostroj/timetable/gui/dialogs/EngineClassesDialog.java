/*
 * EngineClassesDialog.java
 *
 * Created on 2. červen 2008, 16:08
 */
package net.parostroj.timetable.gui.dialogs;

import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;

import net.parostroj.timetable.gui.components.ChangeDocumentListener;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.gui.wrappers.WrapperListModel.ObjectListener;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.ObjectsUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * List of engine class.
 *
 * @author jub
 */
public class EngineClassesDialog extends javax.swing.JDialog {

    private class WeightTableModel extends AbstractTableModel {

        private EngineClass getCurrentEngineClass() {
            int selected = engineClassesList.getSelectedIndex();
            EngineClass clazz = selected != -1 ? listModel.getIndex(selected).getElement() : null;
            return clazz;
        }

        private LineClass getLineClass(int index) {
            return diagram.getNet().getLineClasses().get(index - 1);
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
            if (diagram != null) {
                return diagram.getNet().getLineClasses().get(column - 1).getName();
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
            if (diagram != null) {
                return diagram.getNet().getLineClasses().size() + 1;
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
        }

        public void removeWeightTableRow(int index) {
            EngineClass clazz = getCurrentEngineClass();
            clazz.removeWeightTableRow(index);
            this.fireTableRowsDeleted(index, index);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(EngineClassesDialog.class);

    private TrainDiagram diagram;
    private WrapperListModel<EngineClass> listModel;
    private final WeightTableModel tableModel;

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

    public void showDialog(TrainDiagram diagram) {
        this.diagram = diagram;
        this.updateValues();
        this.setVisible(true);
    }

    public void updateValues() {
        // update list of available classes ...
        listModel = new WrapperListModel<>(Wrapper.getWrapperList(diagram.getEngineClasses()), null, true);
        listModel.setObjectListener(new ObjectListener<EngineClass>() {
            @Override
            public void added(EngineClass object, int index) {
                diagram.getEngineClasses().add(object);
            }

            @Override
            public void removed(EngineClass object) {
                diagram.getEngineClasses().remove(object);
            }

            @Override
            public void moved(EngineClass object, int fromIndex, int toIndex) {
                // set is without move
            }
        });
        engineClassesList.setModel(listModel);
        tableModel.updateInfo();
        this.enableDisable();
    }

    private void enableDisable() {
        boolean enabled = !engineClassesList.isSelectionEmpty();
        weightTable.setEnabled(enabled);
        speedTextField.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        copyEnable(ObjectsUtil.checkAndTrim(nameTextField.getText()), enabled);
        speedTextField.setText("");
        this.enableDisableDeleteRow();
    }

    private void copyEnable(String txt, boolean selected) {
        copyButton.setEnabled(txt != null && selected);
    }

    private void enableDisableDeleteRow() {
        boolean enabled = weightTable.getSelectedRow() != -1;
        deleteRowButton.setEnabled(enabled);
    }

    private void initComponents() {
        javax.swing.JScrollPane scrollPane1 = new javax.swing.JScrollPane();
        engineClassesList = new javax.swing.JList<>();
        nameTextField = new javax.swing.JTextField();
        nameTextField.setColumns(8);
        nameTextField.getDocument().addDocumentListener(new ChangeDocumentListener() {
            @Override
            protected void change() {
                String text = ObjectsUtil.checkAndTrim(nameTextField.getText());
                newButton.setEnabled(text != null);
                copyEnable(text, !engineClassesList.isSelectionEmpty());
            }
        });
        newButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);
        newButton.setEnabled(false);
        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);
        copyButton = GuiComponentUtils.createButton(GuiIcon.COPY, 2);
        copyButton.setEnabled(false);
        javax.swing.JScrollPane scrollPane2 = new javax.swing.JScrollPane();
        weightTable = new javax.swing.JTable();
        newRowButton = GuiComponentUtils.createButton(GuiIcon.ADD, 0);
        newRowButton.setEnabled(false);
        deleteRowButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 0);
        speedTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();

        engineClassesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        engineClassesList.addListSelectionListener(evt -> engineClassesListValueChanged(evt));
        scrollPane1.setViewportView(engineClassesList);

        newButton.addActionListener(evt -> newButtonActionPerformed(evt));

        deleteButton.addActionListener(evt -> deleteButtonActionPerformed(evt));

        copyButton.addActionListener(evt -> copyButtonActionPerformed(evt));

        scrollPane2.setPreferredSize(new java.awt.Dimension(0, 200));

        weightTable.setModel(tableModel);
        weightTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPane2.setViewportView(weightTable);

        newRowButton.addActionListener(evt -> newRowButtonActionPerformed(evt));

        deleteRowButton.addActionListener(evt -> deleteRowButtonActionPerformed(evt));

        speedTextField.setColumns(5);
        speedTextField.addCaretListener(e -> newRowButton.setEnabled(!ObjectsUtil.isEmpty(speedTextField.getText())));

        jLabel1.setText(ResourceLoader.getString("editengingeclasses.speed") + ":"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(copyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(newButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nameTextField, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(copyButton))
                    .addComponent(scrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
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
        String newName = ObjectsUtil.checkAndTrim(nameTextField.getText());
        if (newName != null) {
            // create new LineClass
            EngineClass clazz = new EngineClass(IdGenerator.getInstance().getId(), newName);
            listModel.addWrapper(Wrapper.getWrapper(clazz));
            nameTextField.setText("");
            int index = listModel.getIndexOfObject(clazz);
            engineClassesList.setSelectedIndex(index);
            engineClassesList.ensureIndexIsVisible(index);
        }
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (!engineClassesList.isSelectionEmpty()) {
            int selected = engineClassesList.getSelectedIndex();
            listModel.removeIndex(selected);
            if (selected >= listModel.getSize()) {
                selected--;
            }
            engineClassesList.setSelectedIndex(selected);
        }
    }

    private void engineClassesListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            if (!engineClassesList.isSelectionEmpty()) {
                weightTable.removeEditor();
                tableModel.updateInfo();
            }
            this.enableDisable();
        }
    }

    private void newRowButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // check if there is a speed specified
        try {
            int speed = Integer.parseInt(speedTextField.getText());
            if (speed == 0) {
                return;
            }
            tableModel.addWeightTableRowForSpeed(speed);
            speedTextField.setText("");

        } catch (NumberFormatException e) {
            log.trace("Cannot convert speed string to int.", e);
        }
    }

    private void deleteRowButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = weightTable.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeWeightTableRow(selectedRow);
        }
    }

    private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (!engineClassesList.isSelectionEmpty()) {
            int selected = engineClassesList.getSelectedIndex();
            EngineClass copiedClazz = listModel.getIndex(selected).getElement();

            String newName = ObjectsUtil.checkAndTrim(nameTextField.getText());
            if (newName != null) {
                // create new LineClass
                EngineClass clazz = new CopyFactory(diagram.getPartFactory()).copy(copiedClazz,
                        IdGenerator.getInstance().getId());
                listModel.addWrapper(Wrapper.getWrapper(clazz));
                nameTextField.setText("");
                int index = listModel.getIndexOfObject(clazz);
                engineClassesList.setSelectedIndex(index);
                engineClassesList.ensureIndexIsVisible(index);
            }
        }
    }

    private javax.swing.JButton copyButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton deleteRowButton;
    private javax.swing.JList<Wrapper<EngineClass>> engineClassesList;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton newButton;
    private javax.swing.JButton newRowButton;
    private javax.swing.JTextField speedTextField;
    private javax.swing.JTable weightTable;
}
