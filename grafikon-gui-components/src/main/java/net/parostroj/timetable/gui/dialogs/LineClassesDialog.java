/*
 * LineClassesDialog.java
 *
 * Created on 2. červen 2008, 6:44
 */
package net.parostroj.timetable.gui.dialogs;

import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.gui.components.ChangeDocumentListener;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.gui.wrappers.WrapperListModel.ObjectListener;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;

/**
 * Dialog for editing classes of the lines.
 *
 * @author jub
 */
public class LineClassesDialog extends javax.swing.JDialog {

    private TrainDiagram diagram;
    private WrapperListModel<LineClass> listModel;

    /** Creates new form LineClassesDialog */
    public LineClassesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public void showDialog(TrainDiagram diagram) {
        this.diagram = diagram;
        this.updateValues();
        this.setVisible(true);
    }

    public void updateValues() {
        // update list of available classes ...
        listModel = new WrapperListModel<LineClass>(Wrapper.getWrapperList(diagram.getNet().getLineClasses()), null, false);
        listModel.setObjectListener(new ObjectListener<LineClass>() {
            @Override
            public void added(LineClass object, int index) {
                diagram.getNet().addLineClass(object, index);
            }

            @Override
            public void removed(LineClass object) {
                diagram.getNet().removeLineClass(object);
            }

            @Override
            public void moved(LineClass object, int fromIndex, int toIndex) {
                diagram.getNet().moveLineClass(fromIndex, toIndex);
            }});
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
        for (Line line : diagram.getNet().getLines()) {
            if (line.getLineClass(TimeIntervalDirection.FORWARD) == lineClass)
                return false;
            if (line.getLineClass(TimeIntervalDirection.BACKWARD) == lineClass)
                return false;
        }
        return true;
    }

    private void initComponents() {
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        lineClassesList = new javax.swing.JList();
        nameTextField = new javax.swing.JTextField();
        nameTextField.setColumns(6);
        nameTextField.getDocument().addDocumentListener(new ChangeDocumentListener() {
            @Override
            protected void change() {
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
            listModel.addWrapper(Wrapper.getWrapper(lineClass));
            nameTextField.setText("");
        }
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (!lineClassesList.isSelectionEmpty()) {
            int selected = lineClassesList.getSelectedIndex();
            if (!this.deleteAllowed(listModel.getIndex(selected).getElement())) {
                ActionUtils.showError(ResourceLoader.getString("dialog.error.delete.in.use"), this);
                return;
            }
            listModel.removeIndex(selected);
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
            listModel.moveIndexUp(selected + 1);
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
            listModel.moveIndexDown(selected - 1);
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
    private javax.swing.JButton upButton;
}
