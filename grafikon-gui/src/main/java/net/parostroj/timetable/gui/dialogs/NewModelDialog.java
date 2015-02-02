/*
 * SettingsDialog.java
 *
 * Created on 22. září 2007, 18:07
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.event.ActionListener;
import java.util.concurrent.Callable;

import javax.swing.JButton;

import net.parostroj.timetable.gui.pm.NewModelPM;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.ResourceLoader;

import org.beanfabrics.ModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnComboBox;

/**
 * Dialog for creation new GT.
 *
 * @author jub
 */
public class NewModelDialog extends javax.swing.JDialog {

    private final ModelProvider provider = new ModelProvider(NewModelPM.class);

    public NewModelDialog(java.awt.Window parent, boolean modal) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        provider.setPresentationModel(new NewModelPM());
        initComponents();
    }

    public Callable<TrainDiagram> showDialog() {
        NewModelPM model = (NewModelPM) this.provider.getPresentationModel();
        model.init();
        this.setVisible(true);
        return model.getResult();
    }

    private void initComponents() {
        ActionListener closeAction = e -> setVisible(false);

        java.awt.GridBagConstraints gridBagConstraints;

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        BnComboBox scaleComboBox = new BnComboBox();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        BnComboBox ratioComboBox = new BnComboBox();
        javax.swing.JPanel panel1 = new javax.swing.JPanel();
        BnButton okButton = new BnButton();
        okButton.addActionListener(closeAction);
        JButton cancelButton = new JButton();
        cancelButton.addActionListener(closeAction);
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        BnComboBox templatesComboBox = new BnComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(ResourceLoader.getString("newmodel")); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(ResourceLoader.getString("modelinfo.scales")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        getContentPane().add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 2, 10, 10);
        getContentPane().add(scaleComboBox, gridBagConstraints);

        jLabel2.setText(ResourceLoader.getString("modelinfo.ratio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        getContentPane().add(jLabel2, gridBagConstraints);

        ratioComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 10, 10);
        getContentPane().add(ratioComboBox, gridBagConstraints);

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        panel1.add(okButton);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        panel1.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 5);
        getContentPane().add(panel1, gridBagConstraints);

        jLabel3.setText(ResourceLoader.getString("newmodel.template")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        getContentPane().add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 10, 10);
        getContentPane().add(templatesComboBox, gridBagConstraints);

        // binding
        scaleComboBox.setModelProvider(provider);
        scaleComboBox.setPath(new Path("scale"));
        ratioComboBox.setModelProvider(provider);
        ratioComboBox.setPath(new Path("timeScale"));
        templatesComboBox.setModelProvider(provider);
        templatesComboBox.setPath(new Path("template"));
        okButton.setModelProvider(provider);
        okButton.setPath(new Path("ok"));

        pack();
    }
}
