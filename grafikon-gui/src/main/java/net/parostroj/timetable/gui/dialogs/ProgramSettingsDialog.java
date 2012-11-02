/*
 * ProgramSettingsDialog.java
 *
 * Created on 13.5.2010, 11:28:40
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ProgramSettings;
import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Dialog for program settings.
 *
 * @author jub
 */
public class ProgramSettingsDialog extends javax.swing.JDialog {

    private ApplicationModel model;

    /** Creates new form ProgramSettingsDialog */
    public ProgramSettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        for (LengthUnit unit : LengthUnit.values()) {
            if (unit.isScaleDependent()) {
                unitComboBox.addItem(unit);
                speedUnitComboBox.addItem(unit);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed"
    // desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        javax.swing.JPanel dataPanel = new javax.swing.JPanel();
        javax.swing.JLabel nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        warningAutoECCorrectionCheckBox = new javax.swing.JCheckBox();
        javax.swing.JLabel unitLabel = new javax.swing.JLabel();
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(ResourceLoader.getString("program.settings.title")); // NOI18N
        setResizable(false);

        GridBagLayout gbl_dataPanel = new GridBagLayout();
        gbl_dataPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0 };
        dataPanel.setLayout(gbl_dataPanel);

        nameLabel.setText(ResourceLoader.getString("program.settings.username")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        dataPanel.add(nameLabel, gridBagConstraints);

        nameTextField.setColumns(25);
        GridBagConstraints gridBagConstraints_1 = new java.awt.GridBagConstraints();
        gridBagConstraints_1.gridwidth = 4;
        gridBagConstraints_1.gridx = 1;
        gridBagConstraints_1.gridy = 0;
        gridBagConstraints_1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints_1.weightx = 1.0;
        gridBagConstraints_1.insets = new Insets(5, 0, 5, 5);
        dataPanel.add(nameTextField, gridBagConstraints_1);

        warningAutoECCorrectionCheckBox.setText(ResourceLoader.getString("program.settings.speedautochange.warning")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(0, 5, 5, 5);
        dataPanel.add(warningAutoECCorrectionCheckBox, gridBagConstraints);

        unitLabel.setText(ResourceLoader.getString("program.settings.unit")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        dataPanel.add(unitLabel, gridBagConstraints);
        unitComboBox = new javax.swing.JComboBox();

        GridBagConstraints gridBagConstraints_2 = new java.awt.GridBagConstraints();
        gridBagConstraints_2.insets = new Insets(0, 0, 0, 5);
        gridBagConstraints_2.gridx = 1;
        gridBagConstraints_2.gridy = 2;
        gridBagConstraints_2.anchor = java.awt.GridBagConstraints.WEST;
        dataPanel.add(unitComboBox, gridBagConstraints_2);

        JLabel label2 = new JLabel(ResourceLoader.getString("program.settings.unit")); //$NON-NLS-1$
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 5);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 2;
        gbc.gridy = 2;
        dataPanel.add(label2, gbc);

        getContentPane().add(dataPanel, java.awt.BorderLayout.CENTER);

        speedUnitComboBox = new javax.swing.JComboBox();
        GridBagConstraints gbc_speedUnitComboBox = new GridBagConstraints();
        gbc_speedUnitComboBox.anchor = GridBagConstraints.WEST;
        gbc_speedUnitComboBox.insets = new Insets(0, 0, 0, 5);
        gbc_speedUnitComboBox.gridx = 3;
        gbc_speedUnitComboBox.gridy = 2;
        dataPanel.add(speedUnitComboBox, gbc_speedUnitComboBox);

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(okButton);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okButtonActionPerformed
        if (writeBackValues())
            this.setVisible(false);
        else
            ActionUtils.showError("Error", this);
    }// GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }// GEN-LAST:event_cancelButtonActionPerformed

    public void showDialog(ApplicationModel model) {
        // set model
        this.setModel(model);
        // show dialog
        this.setVisible(true);
    }

    private void setModel(ApplicationModel model) {
        this.model = model;
        this.updateValues();
    }

    private void updateValues() {
        this.nameTextField.setText(model.getProgramSettings().getUserNameOrSystemUser());
        this.warningAutoECCorrectionCheckBox.setSelected(model.getProgramSettings().isWarningAutoECCorrection());
        this.unitComboBox.setSelectedItem(model.getProgramSettings().getLengthUnit());
        this.speedUnitComboBox.setSelectedItem(model.getProgramSettings().getSpeedLengthUnit());
    }

    private boolean writeBackValues() {
        ProgramSettings ps = model.getProgramSettings();
        String name = nameTextField.getText().trim();
        if (name.equals(ps.getSystemUser()) || "".equals(name))
            ps.setUserName(null);
        else
            ps.setUserName(name);
        ps.setWarningAutoECCorrection(warningAutoECCorrectionCheckBox.isSelected());
        ps.setLengthUnit((LengthUnit) unitComboBox.getSelectedItem());
        ps.setSpeedLengthUnit((LengthUnit) speedUnitComboBox.getSelectedItem());
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField nameTextField;
    private javax.swing.JComboBox unitComboBox;
    private javax.swing.JCheckBox warningAutoECCorrectionCheckBox;
    private javax.swing.JComboBox speedUnitComboBox;
    // End of variables declaration//GEN-END:variables
}
