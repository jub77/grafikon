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

import net.parostroj.timetable.gui.ProgramSettings;
import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.utils.ResourceLoader;
import java.awt.FlowLayout;

/**
 * Dialog for program settings.
 *
 * @author jub
 */
public class ProgramSettingsDialog extends javax.swing.JDialog {

    private ProgramSettings settings;

    /** Creates new form ProgramSettingsDialog */
    public ProgramSettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        for (LengthUnit unit : LengthUnit.values()) {
            if (unit.isScaleDependent()) {
                unitComboBox.addItem(unit);
            }
        }
        speedUnitComboBox.addItem(LengthUnit.KM);
        speedUnitComboBox.addItem(LengthUnit.MILE);
    }

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
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        dataPanel.add(warningAutoECCorrectionCheckBox, gridBagConstraints);

        FlowLayout fl_unitsPanel = new FlowLayout(FlowLayout.LEFT);
        javax.swing.JPanel unitsPanel = new javax.swing.JPanel(fl_unitsPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        dataPanel.add(unitsPanel, gridBagConstraints);

        unitLabel.setText(ResourceLoader.getString("modelinfo.unit")); // NOI18N
        unitsPanel.add(unitLabel);
        unitComboBox = new javax.swing.JComboBox();
        unitsPanel.add(unitComboBox);
        JLabel speedUnitLabel = new JLabel(ResourceLoader.getString("modelinfo.speed.unit")); //$NON-NLS-1$
        unitsPanel.add(speedUnitLabel);
        speedUnitComboBox = new javax.swing.JComboBox();
        unitsPanel.add(speedUnitComboBox);
        unitsPanel.add(new javax.swing.JLabel("/h"));

        getContentPane().add(dataPanel, java.awt.BorderLayout.CENTER);

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
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (writeBackValues())
            this.setVisible(false);
        else
            ActionUtils.showError("Error", this);
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
    }

    public void showDialog(ProgramSettings settings) {
        // set model
        this.settings = settings;
        this.updateValues();
        // show dialog
        this.setVisible(true);
    }

    private void updateValues() {
        this.nameTextField.setText(settings.getUserNameOrSystemUser());
        this.warningAutoECCorrectionCheckBox.setSelected(settings.isWarningAutoECCorrection());
        this.unitComboBox.setSelectedItem(settings.getLengthUnit());
        this.speedUnitComboBox.setSelectedItem(settings.getSpeedLengthUnit());
    }

    private boolean writeBackValues() {
        String name = nameTextField.getText().trim();
        if (name.equals(settings.getSystemUser()) || "".equals(name))
            settings.setUserName(null);
        else
            settings.setUserName(name);
        settings.setWarningAutoECCorrection(warningAutoECCorrectionCheckBox.isSelected());
        settings.setLengthUnit((LengthUnit) unitComboBox.getSelectedItem());
        settings.setSpeedLengthUnit((LengthUnit) speedUnitComboBox.getSelectedItem());
        return true;
    }

    private javax.swing.JTextField nameTextField;
    private javax.swing.JComboBox unitComboBox;
    private javax.swing.JCheckBox warningAutoECCorrectionCheckBox;
    private javax.swing.JComboBox speedUnitComboBox;
}
