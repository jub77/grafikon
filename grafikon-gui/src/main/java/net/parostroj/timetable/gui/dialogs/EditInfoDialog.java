/*
 * EditInfoDialog.java
 *
 * Created on 13. říjen 2007, 13:03
 */
package net.parostroj.timetable.gui.dialogs;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelListener;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.ResourceLoader;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.UIManager;

/**
 * Dialog for editing additional information (temporary).
 *
 * @author jub
 */
public class EditInfoDialog extends javax.swing.JDialog implements ApplicationModelListener {

    private ApplicationModel model;

    /** Creates new form EditInfoDialog */
    public EditInfoDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public void setModel(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void modelChanged(ApplicationModelEvent event) {
        // do nothing
    }

    public void updateValues() {
        if (model.getDiagram() == null) {
            return;
        }
        String numbers = (String) model.getDiagram().getAttribute(TrainDiagram.ATTR_ROUTE_NUMBERS);
        String nodes = (String) model.getDiagram().getAttribute(TrainDiagram.ATTR_ROUTE_NODES);
        boolean info = !(nodes == null && numbers == null);
        routeInfoCheckBox.setSelected(info);
        setAreasEnabled(info);

        routeNumberTextArea.setText(numbers);
        routesTextArea.setText(nodes);
        validityTextField.setText((String) model.getDiagram().getAttribute(TrainDiagram.ATTR_ROUTE_VALIDITY));
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        javax.swing.JPanel dataPanel = new javax.swing.JPanel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JScrollPane scrollPane1 = new javax.swing.JScrollPane();
        routeNumberTextArea = new javax.swing.JTextArea();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JScrollPane scrollPane2 = new javax.swing.JScrollPane();
        routesTextArea = new javax.swing.JTextArea();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        javax.swing.JPanel buttonsPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        FormListener formListener = new FormListener();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(ResourceLoader.getString("info.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        dataPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(ResourceLoader.getString("info.route.number")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(3, 3, 5, 5);
        dataPanel.add(jLabel1, gridBagConstraints);

        routeNumberTextArea.setColumns(35);
        routeNumberTextArea.setRows(3);
        scrollPane1.setViewportView(routeNumberTextArea);

        java.awt.GridBagConstraints gridBagConstraints_2 = new java.awt.GridBagConstraints();
        gridBagConstraints_2.gridwidth = 2;
        gridBagConstraints_2.gridx = 0;
        gridBagConstraints_2.gridy = 1;
        gridBagConstraints_2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints_2.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints_2.weightx = 1.0;
        gridBagConstraints_2.weighty = 1.0;
        gridBagConstraints_2.insets = new Insets(3, 3, 5, 5);
        dataPanel.add(scrollPane1, gridBagConstraints_2);

        jLabel2.setText(ResourceLoader.getString("info.routes")); // NOI18N
        java.awt.GridBagConstraints gridBagConstraints_3 = new java.awt.GridBagConstraints();
        gridBagConstraints_3.gridwidth = 2;
        gridBagConstraints_3.gridx = 0;
        gridBagConstraints_3.gridy = 2;
        gridBagConstraints_3.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints_3.insets = new Insets(3, 3, 5, 5);
        dataPanel.add(jLabel2, gridBagConstraints_3);

        routesTextArea.setColumns(35);
        routesTextArea.setRows(5);
        scrollPane2.setViewportView(routesTextArea);

        java.awt.GridBagConstraints gridBagConstraints_1 = new java.awt.GridBagConstraints();
        gridBagConstraints_1.gridwidth = 2;
        gridBagConstraints_1.gridx = 0;
        gridBagConstraints_1.gridy = 3;
        gridBagConstraints_1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints_1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints_1.weightx = 1.0;
        gridBagConstraints_1.weighty = 1.0;
        gridBagConstraints_1.insets = new Insets(3, 3, 5, 5);
        dataPanel.add(scrollPane2, gridBagConstraints_1);

        routeInfoCheckBox = new javax.swing.JCheckBox(ResourceLoader.getString("info.route.enabled"));
        routeInfoCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean info = routeInfoCheckBox.isSelected();
                setAreasEnabled(info);
                routeNumberTextArea.setText("");
                routesTextArea.setText("");
            }
        });
        GridBagConstraints gbc_routeInfoCheckBox = new GridBagConstraints();
        gbc_routeInfoCheckBox.gridwidth = 2;
        gbc_routeInfoCheckBox.anchor = GridBagConstraints.WEST;
        gbc_routeInfoCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_routeInfoCheckBox.gridx = 0;
        gbc_routeInfoCheckBox.gridy = 4;
        dataPanel.add(routeInfoCheckBox, gbc_routeInfoCheckBox);

        jLabel3.setText(ResourceLoader.getString("info.validity") + ":"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(3, 3, 5, 5);
        dataPanel.add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(dataPanel, gridBagConstraints);
        validityTextField = new javax.swing.JTextField();
        routeNumberTextArea.setFont(validityTextField.getFont());
        routesTextArea.setFont(validityTextField.getFont());

        validityTextField.setColumns(25);
        java.awt.GridBagConstraints gridBagConstraints_4 = new java.awt.GridBagConstraints();
        gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_4.gridx = 1;
        gridBagConstraints_4.gridy = 5;
        gridBagConstraints_4.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints_4.insets = new Insets(3, 3, 5, 3);
        dataPanel.add(validityTextField, gridBagConstraints_4);

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(formListener);
        buttonsPanel.add(okButton);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(formListener);
        buttonsPanel.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(buttonsPanel, gridBagConstraints);

        pack();
    }

    private void setAreasEnabled(boolean enabled) {
        routeNumberTextArea.setEnabled(enabled);
        routesTextArea.setEnabled(enabled);
        Color back = UIManager.getColor(enabled ? "TextArea.background" : "TextArea.disabledBackground");
        routeNumberTextArea.setBackground(back);
        routesTextArea.setBackground(back);
    }

    private class FormListener implements java.awt.event.ActionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == okButton) {
                EditInfoDialog.this.okButtonActionPerformed(evt);
            }
            else if (evt.getSource() == cancelButton) {
                EditInfoDialog.this.cancelButtonActionPerformed(evt);
            }
        }
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // save values
        TrainDiagram diagram = model.getDiagram();
        String number = routeNumberTextArea.getText().trim(); if (number.equals("")) number = null;
        String nodes = routesTextArea.getText().trim(); if (nodes.equals("")) nodes = null;
        String validity = validityTextField.getText().trim(); if (validity.equals("")) validity = null;

        diagram.getAttributes().setRemove(TrainDiagram.ATTR_ROUTE_NUMBERS, number);
        diagram.getAttributes().setRemove(TrainDiagram.ATTR_ROUTE_NODES, nodes);
        diagram.getAttributes().setRemove(TrainDiagram.ATTR_ROUTE_VALIDITY, validity);

        this.setVisible(false);
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
    }

    private javax.swing.JButton cancelButton;
    private javax.swing.JButton okButton;
    private javax.swing.JTextArea routeNumberTextArea;
    private javax.swing.JTextArea routesTextArea;
    private javax.swing.JTextField validityTextField;
    private javax.swing.JCheckBox routeInfoCheckBox;
}
