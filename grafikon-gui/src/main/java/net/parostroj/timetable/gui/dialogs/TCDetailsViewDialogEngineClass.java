/*
 * TCDetailsViewDialogEngineClass.java
 *
 * Created on 4. červen 2008, 12:30
 */
package net.parostroj.timetable.gui.dialogs;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.gui.views.TCDelegate;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Dialog for editing
 *
 * @author jub
 */
public class TCDetailsViewDialogEngineClass extends javax.swing.JDialog {

    private TCDelegate delegate;
    private ApplicationModel model;
    private static final EngineClass noneEngineClass = new EngineClass(null, ResourceLoader.getString("ec.details.engineclass.none"));

    /** Creates new form TCDetailsViewDialog */
    public TCDetailsViewDialogEngineClass(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        attributesPanel.setCategory(TCDetailsViewDialog.USER_ATTR_CATEGORY);
    }

    public void updateValues(TCDelegate delegate, ApplicationModel model) {
        this.delegate = delegate;
        this.model = model;
        TrainsCycle cycle = delegate.getSelectedCycle();
        EngineClass clazz = (EngineClass)cycle.getAttribute("engine.class");
        this.nameTextField.setText(cycle.getName());
        this.descTextField.setText(cycle.getDescription());
        this.engineClassComboBox.removeAllItems();
        this.engineClassComboBox.addItem(noneEngineClass);
        for (EngineClass c : model.getDiagram().getEngineClasses()) {
            this.engineClassComboBox.addItem(c);
        }
        attributesPanel.startEditing(new Attributes(cycle.getAttributes()));
        this.engineClassComboBox.setSelectedItem(clazz != null ? clazz : noneEngineClass);
    }

    private void initComponents() {
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        descTextField = new javax.swing.JTextField();
        engineClassComboBox = new javax.swing.JComboBox();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        attributesPanel = new net.parostroj.timetable.gui.components.AttributesPanel();
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setModal(true);
        setResizable(false);

        jLabel1.setText(ResourceLoader.getString("ec.details.name") + ": "); // NOI18N

        nameTextField.setColumns(15);

        jLabel2.setText(ResourceLoader.getString("ec.details.description") + ": "); // NOI18N

        descTextField.setColumns(15);

        jLabel3.setText(ResourceLoader.getString("ec.details.engineclass") + ": "); // NOI18N

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(attributesPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                            .addComponent(descTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                            .addComponent(engineClassComboBox, 0, 286, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(descTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(engineClassComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attributesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // write values back and close
        TrainsCycle cycle = delegate.getSelectedCycle();
        if (nameTextField.getText() != null && !"".equals(nameTextField.getText())
                && !nameTextField.getText().equals(cycle.getName()))
            cycle.setName(nameTextField.getText().trim());
        if (descTextField.getText() == null || "".equals(descTextField.getText().trim())) {
            if (cycle.getDescription() != null)
                cycle.setDescription(null);
        } else {
            if (!descTextField.getText().equals(cycle.getDescription()))
                cycle.setDescription(descTextField.getText().trim());
        }

        // write back engine class
        if (engineClassComboBox.getSelectedItem() == noneEngineClass) {
            // another check is not needed because remove attribute doesn't send
            // event for removing non-existent attribute
            cycle.removeAttribute("engine.class");
        } else {
            EngineClass eClass = (EngineClass) engineClassComboBox.getSelectedItem();
            if (eClass != cycle.getAttribute("engine.class")) {
                cycle.setAttribute("engine.class", eClass);
                boolean warning = model.getProgramSettings().isWarningAutoECCorrection();
                StringBuilder trainsStr = null;
                for (TrainsCycleItem item : cycle.getItems()) {
                    Train train = item.getTrain();
                    if (train.checkNeedSpeedRecalculate()) {
                        train.recalculate();
                        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_TRAIN, model, train));
                        if (warning) {
                            if (trainsStr == null)
                                trainsStr = new StringBuilder();
                            else
                                trainsStr.append(',');
                            trainsStr.append(train.getName());
                        }
                    }
                }
                if (warning && trainsStr != null) {
                    ActionUtils.showWarning(
                            String.format(ResourceLoader.getString("dialog.warning.trains.recalculated"), trainsStr),
                            ActionUtils.getTopLevelComponent(this.getParent()));
                }
            }
        }

        // event
        delegate.fireEvent(TCDelegate.Action.MODIFIED_CYCLE, cycle);

        this.setVisible(false);
        cycle.getAttributes().merge(attributesPanel.stopEditing(), TCDetailsViewDialog.USER_ATTR_CATEGORY);
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // do nothing
        this.setVisible(false);
        attributesPanel.stopEditing();
    }

    private net.parostroj.timetable.gui.components.AttributesPanel attributesPanel;
    private javax.swing.JTextField descTextField;
    private javax.swing.JComboBox engineClassComboBox;
    private javax.swing.JTextField nameTextField;
}
