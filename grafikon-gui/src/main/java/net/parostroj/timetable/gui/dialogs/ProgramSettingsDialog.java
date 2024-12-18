/*
 * ProgramSettingsDialog.java
 *
 * Created on 13.5.2010, 11:28:40
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.*;

import javax.swing.JLabel;

import org.beanfabrics.ModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnCheckBox;
import org.beanfabrics.swing.BnComboBox;
import org.beanfabrics.swing.BnTextField;

import net.parostroj.timetable.gui.data.ProgramSettings;
import net.parostroj.timetable.gui.pm.ProgramSettingsPM;
import net.parostroj.timetable.utils.ResourceLoader;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

/**
 * Dialog for program settings.
 *
 * @author jub
 */
public class ProgramSettingsDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

	private final ModelProvider provider = new ModelProvider(ProgramSettingsPM.class);

    /** Creates new form ProgramSettingsDialog */
    public ProgramSettingsDialog(Window parent, boolean modal) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        provider.setPresentationModel(new ProgramSettingsPM());
        initComponents();

        pack();
    }

    private void initComponents() {
        ActionListener closeAction = e -> setVisible(false);

        java.awt.GridBagConstraints gridBagConstraints;

        javax.swing.JPanel dataPanel = new javax.swing.JPanel();
        javax.swing.JLabel nameLabel = new javax.swing.JLabel();
        BnTextField nameTextField = new BnTextField();
        javax.swing.JLabel unitLabel = new javax.swing.JLabel();
        javax.swing.JLabel diagramTypeLabel = new javax.swing.JLabel();
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        BnButton okButton = new BnButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();
        BnCheckBox debugLoggingCheckBox = new BnCheckBox();

        setTitle(ResourceLoader.getString("program.settings.title")); // NOI18N
        setResizable(false);

        GridBagLayout gblDataPanel = new GridBagLayout();
        gblDataPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0 };
        dataPanel.setLayout(gblDataPanel);

        nameLabel.setText(ResourceLoader.getString("program.settings.username")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        dataPanel.add(nameLabel, gridBagConstraints);

        nameTextField.setColumns(25);
        GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 4;
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.insets = new Insets(5, 0, 5, 5);
        dataPanel.add(nameTextField, gridBagConstraints1);

        FlowLayout flUnitsPanel = new FlowLayout(FlowLayout.LEFT);
        javax.swing.JPanel unitsPanel = new javax.swing.JPanel(flUnitsPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        dataPanel.add(unitsPanel, gridBagConstraints);

        unitLabel.setText(ResourceLoader.getString("modelinfo.unit")); // NOI18N
        unitsPanel.add(unitLabel);
        BnComboBox lengthUnitComboBox = new BnComboBox();
        unitsPanel.add(lengthUnitComboBox);
        JLabel speedUnitLabel = new JLabel(ResourceLoader.getString("modelinfo.speed.unit")); //$NON-NLS-1$
        unitsPanel.add(speedUnitLabel);
        BnComboBox speedUnitComboBox = new BnComboBox();
        unitsPanel.add(speedUnitComboBox);

        debugLoggingCheckBox.setText(ResourceLoader.getString("program.settings.debug.logging")); // NOI18N
        GridBagConstraints gridBagConstraintsLog = new java.awt.GridBagConstraints();
        gridBagConstraintsLog.gridwidth = 4;
        gridBagConstraintsLog.gridx = 0;
        gridBagConstraintsLog.gridy = 2;
        gridBagConstraintsLog.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraintsLog.weightx = 1.0;
        gridBagConstraintsLog.insets = new Insets(0, 5, 0, 5);
        dataPanel.add(debugLoggingCheckBox, gridBagConstraintsLog);

        FlowLayout fDiagramTypePanel = new FlowLayout(FlowLayout.LEFT);
        javax.swing.JPanel diagramTypePanel = new javax.swing.JPanel(fDiagramTypePanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        dataPanel.add(diagramTypePanel, gridBagConstraints);

        diagramTypeLabel.setText(ResourceLoader.getString("program.settings.diagram.type"));
        diagramTypePanel.add(diagramTypeLabel);
        BnComboBox diagramTypeComboBox = new BnComboBox();
        diagramTypePanel.add(diagramTypeComboBox);

        getContentPane().add(dataPanel, java.awt.BorderLayout.CENTER);

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(closeAction);
        buttonPanel.add(okButton);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(closeAction);
        buttonPanel.add(cancelButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.PAGE_END);

        nameTextField.setModelProvider(provider);
        nameTextField.setPath(new Path("user"));
        speedUnitComboBox.setModelProvider(provider);
        speedUnitComboBox.setPath(new Path("speed"));
        lengthUnitComboBox.setModelProvider(provider);
        lengthUnitComboBox.setPath(new Path("length"));
        debugLoggingCheckBox.setModelProvider(provider);
        debugLoggingCheckBox.setPath(new Path("debugLogging"));
        diagramTypeComboBox.setModelProvider(provider);
        diagramTypeComboBox.setPath(new Path("diagramType"));
        okButton.setModelProvider(provider);
        okButton.setPath(new Path("ok"));
    }

    public void showDialog(ProgramSettings settings) {
        ((ProgramSettingsPM) provider.getPresentationModel()).init(settings);
        this.setVisible(true);
    }
}
