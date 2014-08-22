/*
 * OutputTemplateDialog.java
 *
 * Created on 15.4.2011, 13:22:54
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.Arrays;

import javax.swing.JPanel;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TextTemplate.Language;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialog for editing text template.
 *
 * @author jub
 */
public class EditTextTemplateDialog extends javax.swing.JDialog {

    private static final Logger log = LoggerFactory.getLogger(EditTextTemplateDialog.class);

    private TextTemplate template;

    public EditTextTemplateDialog(Window parent, boolean modal) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        initComponents();
        init();
    }

    public void showDialog(TextTemplate template) {
        this.template = template;
        this.updateValues();
        this.setVisible(true);
    }

    private void init() {
        textTemplateEditBox.setTemplateLanguages(Arrays.asList(Language.PLAIN, Language.GROOVY));
    }

    public TextTemplate getTemplate() {
        return template;
    }

    private void updateValues() {
        textTemplateEditBox.setEnabled(true);
        textTemplateEditBox.setTemplate(this.template);
    }

    private void initComponents() {
        textTemplateEditBox = new net.parostroj.timetable.gui.components.TextTemplateEditBox2();
        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        javax.swing.JPanel verifyPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        textTemplateEditBox.setTemplateFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        textTemplateEditBox.setColumns(80);
        textTemplateEditBox.setRows(25);
        getContentPane().add(textTemplateEditBox, java.awt.BorderLayout.CENTER);

        controlPanel.setLayout(new java.awt.BorderLayout());

        controlPanel.add(verifyPanel, BorderLayout.WEST);
        verifyButton = new javax.swing.JButton();
        verifyPanel.add(verifyButton);

        verifyButton.setText(ResourceLoader.getString("ot.button.verify")); // NOI18N
        verifyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verifyButtonActionPerformed(evt);
            }
        });

        getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        controlPanel.add(buttonPanel, BorderLayout.EAST);
        okButton = new javax.swing.JButton();
        buttonPanel.add(okButton);

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        cancelButton = new javax.swing.JButton();
        buttonPanel.add(cancelButton);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            this.template = this.convertToTemplate();
            this.setVisible(false);
        } catch (GrafikonException e) {
            log.error(e.getMessage(), e);
            GuiComponentUtils.showError(e.getMessage(), this);
        }
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.template = null;
        this.setVisible(false);
    }

    private void verifyButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            this.convertToTemplate();
            GuiComponentUtils.showInformation(ResourceLoader.getString("ot.verification.ok"), this);
        } catch (GrafikonException e) {
            log.error(e.getMessage(), e);
            GuiComponentUtils.showError(e.getMessage(), this);
        }
    }

    private TextTemplate convertToTemplate() throws GrafikonException {
        return textTemplateEditBox.getTemplate();
    }

    private javax.swing.JButton cancelButton;
    private javax.swing.JButton okButton;
    private net.parostroj.timetable.gui.components.TextTemplateEditBox2 textTemplateEditBox;
    private javax.swing.JButton verifyButton;
}
