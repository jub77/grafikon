/*
 * SettingsDialog.java
 *
 * Created on 22. září 2007, 18:07
 */
package net.parostroj.timetable.gui.dialogs;

import java.util.List;
import javax.swing.JOptionPane;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.templates.Template;
import net.parostroj.timetable.model.templates.TemplatesLoader;
import net.parostroj.timetable.utils.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialog for creation new GT.
 *
 * @author jub
 */
public class NewModelDialog extends javax.swing.JDialog {

    private static final Logger LOG = LoggerFactory.getLogger(NewModelDialog.class.getName());

    public static class NewModelValues {

        public String template;
        public double timeScale;
        public Scale scale;

        public NewModelValues(String template, double timeScale, Scale scale) {
            super();
            this.template = template;
            this.timeScale = timeScale;
            this.scale = scale;
        }
    }

    private NewModelValues values;

    /** Creates new form SettingsDialog */
    public NewModelDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        for (Scale scale : Scale.getPredefined()) {
            scaleComboBox.addItem(scale);
        }

        // set some values for speed
        for (double d = 1.0; d <= 10.0 ;) {
            ratioComboBox.addItem(Double.toString(d));
            d += 0.5;
        }

        ratioComboBox.setSelectedItem("5.0");

        // create combo box with templates
        List<Template> list = TemplatesLoader.getTemplates();
        for (Template template : list) {
            templatesComboBox.addItem(template.getName());
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (b)
            values = null;
        super.setVisible(b);
    }

    public NewModelValues getNewModelValues() {
        return values;
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        scaleComboBox = new javax.swing.JComboBox();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        ratioComboBox = new javax.swing.JComboBox();
        javax.swing.JPanel panel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        templatesComboBox = new javax.swing.JComboBox();

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
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        panel1.add(okButton);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
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

        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // set scale
        Scale s = (Scale)scaleComboBox.getSelectedItem();
        // set ratio
        double sp = 1.0;
        try {
            sp = Double.parseDouble((String)ratioComboBox.getSelectedItem());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this.getParent(), ex.getMessage(),
                    ResourceLoader.getString("dialog.error.title"), JOptionPane.ERROR_MESSAGE);
            LOG.warn("Cannot convert ratio value.", ex);
            return;
        }

        // load template
        String templateName = (String)templatesComboBox.getSelectedItem();
        values = new NewModelValues(templateName, sp, s);
        this.setVisible(false);
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
    }

    private javax.swing.JButton cancelButton;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox ratioComboBox;
    private javax.swing.JComboBox scaleComboBox;
    private javax.swing.JComboBox templatesComboBox;
}
