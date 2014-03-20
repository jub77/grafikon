/*
 * LineClassesDialog.java
 *
 * Created on 2. červen 2008, 6:44
 */
package net.parostroj.timetable.gui.dialogs;

import javax.swing.AbstractListModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Dialog for editing classes of the lines.
 *
 * @author jub
 */
public class LineClassesDialog extends javax.swing.JDialog {

    private ApplicationModel model;
    private LineClassesListModel listModel;

    private class LineClassesListModel extends AbstractListModel {

        @Override
        public int getSize() {
            if (model.getDiagram() == null) {
                return 0;
            } else {
                return model.getDiagram().getNet().getLineClasses().size();
            }
        }

        @Override
        public Object getElementAt(int index) {
            if (model.getDiagram() == null) {
                return null;
            } else {
                return model.getDiagram().getNet().getLineClasses().get(index);
            }
        }

        public void addLineClass(LineClass clazz) {
            int size = getSize();
            model.getDiagram().getNet().addLineClass(clazz);
            this.fireIntervalAdded(this, size, size);
        }

        public void removeLineClass(int index) {
            LineClass clazz = (LineClass) getElementAt(index);
            // remove item with this line class from weight tables
            for (EngineClass eClass : model.getDiagram().getEngineClasses()) {
                for (WeightTableRow row : eClass.getWeightTable()) {
                    row.removeWeightInfo(clazz);
                }
            }
            // remove line class from lines
            for (Line line : model.getDiagram().getNet().getLines()) {
                line.removeAttribute(Line.ATTR_CLASS);
                line.removeAttribute(Line.ATTR_CLASS_BACK);
            }
            // remove line class
            model.getDiagram().getNet().removeLineClass(clazz);
            this.fireIntervalRemoved(model, index, index);
        }

        public void moveLineClass(int index1, int index2) {
            model.getDiagram().getNet().moveLineClass(index1, index2);
            this.fireContentsChanged(this, index1, index1);
            this.fireContentsChanged(this, index2, index2);
        }
    }

    /** Creates new form LineClassesDialog */
    public LineClassesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public void setModel(ApplicationModel model) {
        this.model = model;
        listModel = new LineClassesListModel();
        lineClassesList.setModel(listModel);
    }

    public void updateValues() {
        // update list of available classes ...
        lineClassesList.setModel(listModel);
        this.updateEnabled();
    }

    public void updateEnabled() {
        boolean enabled = !lineClassesList.isSelectionEmpty();
        upButton.setEnabled(enabled);
        downButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
    }

    private boolean deleteAllowed(LineClass lineClass) {
        if (lineClass == null)
            return false;
        for (Line line : model.getDiagram().getNet().getLines()) {
            if (line.getLineClass(TimeIntervalDirection.FORWARD) == lineClass)
                return false;
            if (line.getLineClass(TimeIntervalDirection.BACKWARD) == lineClass)
                return false;
        }
        return true;
    }

    private void initComponents() {
        scrollPane = new javax.swing.JScrollPane();
        lineClassesList = new javax.swing.JList();
        nameTextField = new javax.swing.JTextField();
        nameTextField.setColumns(6);
        nameTextField.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                String text = nameTextField.getText();
                newButton.setEnabled(text != null && !"".equals(text.trim()));
            }
        });
        newButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);
        newButton.setEnabled(false);
        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);
        upButton = GuiComponentUtils.createButton(GuiIcon.GO_UP, 2);
        downButton = GuiComponentUtils.createButton(GuiIcon.GO_DOWN, 2);

        lineClassesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lineClassesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lineClassesListValueChanged(evt);
            }
        });
        scrollPane.setViewportView(lineClassesList);

        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(newButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(upButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(downButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(nameTextField))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(upButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(downButton)))
                .addContainerGap())
        );

        pack();
    }

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (nameTextField != null && !"".equals(nameTextField.getText())) {
            // create new LineClass
            LineClass lineClass = new LineClass(IdGenerator.getInstance().getId(), nameTextField.getText());
            listModel.addLineClass(lineClass);
            nameTextField.setText("");
        }
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (!lineClassesList.isSelectionEmpty()) {
            int selected = lineClassesList.getSelectedIndex();
            if (!this.deleteAllowed((LineClass)listModel.getElementAt(selected))) {
                ActionUtils.showError(ResourceLoader.getString("dialog.error.delete.in.use"), this);
                return;
            }
            listModel.removeLineClass(selected);
            if (selected >= listModel.getSize()) {
                selected--;
            }
            lineClassesList.setSelectedIndex(selected);
        }
    }

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // move selected line class up
        if (!lineClassesList.isSelectionEmpty()) {
            int selected = lineClassesList.getSelectedIndex();
            selected -= 1;
            if (selected < 0)
                return;
            listModel.moveLineClass(selected + 1, selected);
            lineClassesList.setSelectedIndex(selected);
        }
    }

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // move selected line class down
        if (!lineClassesList.isSelectionEmpty()) {
            int selected = lineClassesList.getSelectedIndex();
            selected += 1;
            if (selected >= listModel.getSize())
                return;
            listModel.moveLineClass(selected - 1, selected);
            lineClassesList.setSelectedIndex(selected);
        }
    }

    private void lineClassesListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting())
            this.updateEnabled();
    }

    private javax.swing.JButton deleteButton;
    private javax.swing.JButton downButton;
    private javax.swing.JList lineClassesList;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton newButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JButton upButton;
}
