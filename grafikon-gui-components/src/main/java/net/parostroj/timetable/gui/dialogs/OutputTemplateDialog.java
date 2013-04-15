/*
 * OutputTemplateDialog.java
 *
 * Created on 15.4.2011, 13:22:54
 */
package net.parostroj.timetable.gui.dialogs;

import java.util.Collections;
import javax.swing.JDialog;
import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TextTemplate.Language;
import net.parostroj.timetable.output2.OutputFactory;
import org.slf4j.LoggerFactory;

/**
 * Dialog for editing text template.
 *
 * @author jub
 */
public class OutputTemplateDialog extends javax.swing.JDialog {

    private OutputTemplate template;

    /** Creates new form OutputTemplateDialog */
    public OutputTemplateDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
    }

    public OutputTemplateDialog(JDialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
    }

    public void showDialog(OutputTemplate template) {
        this.template = template;
        this.updateValues();
        this.setVisible(true);
    }

    private void init() {
        textTemplateEditBox.setTemplateLanguages(Collections.singleton(Language.GROOVY));
        for (String type : OutputFactory.newInstance("groovy").getOutputTypes()) {
            outputTypeComboBox.addItem(type);
        }
    }

    public OutputTemplate getTemplate() {
        return template;
    }

    private void updateValues() {
        textTemplateEditBox.setTemplate(this.template.getTemplate());
        outputTypeComboBox.setSelectedItem(this.template.getAttribute(OutputTemplate.ATTR_OUTPUT_TYPE));
    }

    private void initComponents() {
        textTemplateEditBox = new net.parostroj.timetable.gui.components.TextTemplateEditBox2();
        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        javax.swing.JPanel verifyPanel = new javax.swing.JPanel();
        outputTypeComboBox = new javax.swing.JComboBox();
        verifyButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        textTemplateEditBox.setTemplateFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        textTemplateEditBox.setColumns(80);
        textTemplateEditBox.setRows(25);
        getContentPane().add(textTemplateEditBox, java.awt.BorderLayout.CENTER);

        controlPanel.setLayout(new java.awt.BorderLayout());

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

        controlPanel.add(buttonPanel, java.awt.BorderLayout.EAST);

        verifyPanel.add(outputTypeComboBox);

        verifyButton.setText(ResourceLoader.getString("ot.button.verify")); // NOI18N
        verifyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verifyButtonActionPerformed(evt);
            }
        });
        verifyPanel.add(verifyButton);

        controlPanel.add(verifyPanel, java.awt.BorderLayout.WEST);

        getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            TextTemplate textTemplate = this.convertToTemplate();
            this.template.setTemplate(textTemplate);
            this.template.setAttribute(OutputTemplate.ATTR_OUTPUT_TYPE, outputTypeComboBox.getSelectedItem());
            this.setVisible(false);
        } catch (GrafikonException e) {
            LoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            ActionUtils.showError(e.getMessage(), this);
        }
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.template = null;
        this.setVisible(false);
    }

    private void verifyButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            this.convertToTemplate();
            ActionUtils.showInformation(ResourceLoader.getString("ot.verification.ok"), this);
        } catch (GrafikonException e) {
            LoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            ActionUtils.showError(e.getMessage(), this);
        }
    }

    private TextTemplate convertToTemplate() throws GrafikonException {
        return textTemplateEditBox.getTemplate();
    }

    private javax.swing.JButton cancelButton;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox outputTypeComboBox;
    private net.parostroj.timetable.gui.components.TextTemplateEditBox2 textTemplateEditBox;
    private javax.swing.JButton verifyButton;
}
