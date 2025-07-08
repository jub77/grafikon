/*
 * OutputTemplateDialog.java
 *
 * Created on 15.4.2011, 13:22:54
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.util.Arrays;

import javax.swing.JPanel;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TextTemplate.Language;

import net.parostroj.timetable.model.TrainDiagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialog for editing text template.
 *
 * @author jub
 */
public class TextItemDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

	public static class TextItemModel {
        TextTemplate template;

        public TextItemModel(TextTemplate template) {
            this.template = template;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(TextItemDialog.class);

    private TextItemModel itemModel;
    private TextItemModel resultItemModel;

    public TextItemDialog(Window parent, boolean modal) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        initComponents();
        init();
    }

    public void showDialog(TrainDiagram diagram, TextItemModel template) {
        this.itemModel = template;
        this.resultItemModel = null;
        this.updateValues(diagram);
        this.setVisible(true);
    }

    private void init() {
        textTemplateEditBox.setTemplateLanguages(Arrays.asList(Language.PLAIN, Language.GROOVY, Language.SIMPLE));
    }

    public TextItemModel getResultModel() {
        return resultItemModel;
    }

    private void updateValues(TrainDiagram diagram) {
        textTemplateEditBox.setTemplateLanguages(diagram.getRuntimeInfo().getPermissions().getAllowedTemplate());
        textTemplateEditBox.setEnabled(true);
        textTemplateEditBox.setTemplate(this.itemModel.template);
    }

    private void initComponents() {
        textTemplateEditBox = new net.parostroj.timetable.gui.components.TextTemplateEditBox2();
        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        javax.swing.JPanel verifyPanel = new javax.swing.JPanel();

        textTemplateEditBox.setTemplateFont(new java.awt.Font("Monospaced", Font.PLAIN, 12)); // NOI18N
        textTemplateEditBox.setColumns(80);
        textTemplateEditBox.setRows(25);
        getContentPane().add(textTemplateEditBox, java.awt.BorderLayout.CENTER);

        controlPanel.setLayout(new java.awt.BorderLayout());

        controlPanel.add(verifyPanel, BorderLayout.WEST);
        javax.swing.JButton verifyButton = new javax.swing.JButton();
        verifyPanel.add(verifyButton);

        verifyButton.setText(ResourceLoader.getString("ot.button.verify")); // NOI18N

        verifyButton.addActionListener(this::verifyButtonActionPerformed);

        getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        controlPanel.add(buttonPanel, BorderLayout.EAST);
        javax.swing.JButton okButton = new javax.swing.JButton();
        buttonPanel.add(okButton);

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        javax.swing.JButton cancelButton = new javax.swing.JButton();
        buttonPanel.add(cancelButton);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(this::cancelButtonActionPerformed);
        okButton.addActionListener(this::okButtonActionPerformed);

        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            TextTemplate template = this.convertToTemplate();
            this.resultItemModel = new TextItemModel(template);
            this.setVisible(false);
        } catch (GrafikonException e) {
            log.error(e.getMessage(), e);
            GuiComponentUtils.showError(e.getMessage(), this);
        }
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
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

    private net.parostroj.timetable.gui.components.TextTemplateEditBox2 textTemplateEditBox;
}
