/*
 * OutputTemplateDialog.java
 *
 * Created on 15.4.2011, 13:22:54
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.TextTemplate.Language;
import net.parostroj.timetable.output2.OutputFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialog for editing text template.
 *
 * @author jub
 */
public class OutputTemplateDialog extends javax.swing.JDialog {

    private static final String DEFAULT_OUTPUT_SCRIPT = "outputs.add(\"output.html\",[:],\"utf-8\")";
    private static final List<String> OUTPUTS = Collections.unmodifiableList(Arrays.asList("groovy", "draw"));
    private static final Set<String> OUTPUTS_WITH_TEMPLATE = Collections.unmodifiableSet(Collections.singleton("groovy"));

    private static final Logger log = LoggerFactory.getLogger(OutputTemplateDialog.class);

    private OutputTemplate template;

    public OutputTemplateDialog(Window parent, boolean modal) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        initComponents();
        init();
    }

    public void showDialog(OutputTemplate template) {
        this.template = template;
        this.updateValues(null);
        this.setVisible(true);
    }

    private void init() {
        textTemplateEditBox.setTemplateLanguages(Collections.singleton(Language.GROOVY));
        for (String output : OUTPUTS) {
            outputComboBox.addItem(output);
        }
        outputComboBox.setSelectedItem(OutputTemplate.DEFAULT_OUTPUT);
    }

    public OutputTemplate getTemplate() {
        return template;
    }

    private void updateValues(Boolean defaultTemplate) {
        if (defaultTemplate == null) {
            defaultTemplate = this.template.getAttributes().get(OutputTemplate.ATTR_DEFAULT_TEMPLATE, Boolean.class);
            this.defaultTemplateCheckbox.setSelected(defaultTemplate == Boolean.TRUE);
        }
        boolean isScript = this.template.getScript() != null;
        textTemplateEditBox.setEnabled(defaultTemplate != Boolean.TRUE);
        textTemplateEditBox.setTemplate(defaultTemplate == Boolean.TRUE ? null : this.template.getTemplate());
        extensionTextField.setText(!isScript ? this.template.getAttributes().get(OutputTemplate.ATTR_OUTPUT_EXTENSION, String.class) : null);
        outputComboBox.setSelectedItem(this.template.getOutput());
        outputTypeComboBox.setSelectedItem(this.template.getAttribute(OutputTemplate.ATTR_OUTPUT_TYPE, Object.class));
        scriptCheckBox.setSelected(isScript);
        scriptButton.setEnabled(isScript);
        extensionTextField.setEnabled(!isScript);
    }

    private void initComponents() {
        textTemplateEditBox = new net.parostroj.timetable.gui.components.TextTemplateEditBox2();
        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        ((FlowLayout) buttonPanel.getLayout()).setAlignment(FlowLayout.RIGHT);
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        javax.swing.JPanel verifyPanel = new javax.swing.JPanel();
        FlowLayout flowLayout1 = (FlowLayout) verifyPanel.getLayout();
        flowLayout1.setVgap(2);
        flowLayout1.setAlignment(FlowLayout.LEFT);
        outputComboBox = new javax.swing.JComboBox<String>();
        outputComboBox.setPrototypeDisplayValue("MMMMMM");
        outputTypeComboBox = new javax.swing.JComboBox<String>();
        outputTypeComboBox.setPrototypeDisplayValue("MMMMMMMMMMM");

        outputComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String output = (String) e.getItem();
                    if (template != null) {
                        template.setAttribute(OutputTemplate.ATTR_OUTPUT, output);
                    }
                    outputTypeComboBox.removeAllItems();
                    for (String type : OutputFactory.newInstance(output).getOutputTypes()) {
                        outputTypeComboBox.addItem(type);
                    }
                    if (OUTPUTS_WITH_TEMPLATE.contains(output)) {
                        defaultTemplateCheckbox.setEnabled(true);
                    } else {
                        defaultTemplateCheckbox.setSelected(true);
                        defaultTemplateCheckbox.setEnabled(false);
                    }
                }
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        textTemplateEditBox.setTemplateFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        textTemplateEditBox.setColumns(80);
        textTemplateEditBox.setRows(25);
        textTemplateEditBox.setVisibleTemplateLanguageChange(false);
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

        controlPanel.add(buttonPanel, BorderLayout.SOUTH);

        verifyPanel.add(outputComboBox);
        verifyPanel.add(outputTypeComboBox);

        controlPanel.add(verifyPanel, BorderLayout.WEST);

        defaultTemplateCheckbox = new JCheckBox(ResourceLoader.getString("ot.checkbox.default.template"));
        verifyPanel.add(defaultTemplateCheckbox);
        verifyButton = new javax.swing.JButton();

                verifyButton.setText(ResourceLoader.getString("ot.button.verify")); // NOI18N
                verifyButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        verifyButtonActionPerformed(evt);
                    }
                });
                verifyPanel.add(verifyButton);

        JLabel suffixLabel = new JLabel(ResourceLoader.getString("ot.extension") + ":");
        verifyPanel.add(suffixLabel);

        extensionTextField = new JTextField();
        verifyPanel.add(extensionTextField);
        extensionTextField.setColumns(10);
        defaultTemplateCheckbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateValues(defaultTemplateCheckbox.isSelected());
            }
        });

        getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

        JPanel scriptPanel = new JPanel();
        FlowLayout flowLayout2 = (FlowLayout) scriptPanel.getLayout();
        flowLayout2.setVgap(2);
        controlPanel.add(scriptPanel, BorderLayout.EAST);

        scriptButton = new JButton(ResourceLoader.getString("ot.script") + "...");
        scriptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ScriptDialog dialog = new ScriptDialog(OutputTemplateDialog.this, true);
                dialog.setScript(template.getScript());
                dialog.setLocationRelativeTo(OutputTemplateDialog.this);
                dialog.setVisible(true);
                Script selectedScript = dialog.getSelectedScript();
                if (selectedScript != null) {
                    template.setScript(selectedScript);
                }
            }
        });
        scriptPanel.add(scriptButton);

        scriptCheckBox = new JCheckBox();
        scriptCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isScript = scriptCheckBox.isSelected();
                scriptButton.setEnabled(isScript);
                extensionTextField.setEnabled(!isScript);
                if (isScript) {
                    extensionTextField.setText(null);
                    try {
                        template.setScript(Script.createScript(DEFAULT_OUTPUT_SCRIPT, Script.Language.GROOVY));
                    } catch (GrafikonException e1) {
                        log.error("Error creating script.", e);
                    }
                } else {
                    template.setScript(null);
                }
            }
        });
        scriptPanel.add(scriptCheckBox);

        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            TextTemplate textTemplate = this.convertToTemplate();
            this.template.setTemplate(textTemplate);
            this.template.setAttribute(OutputTemplate.ATTR_OUTPUT_TYPE, outputTypeComboBox.getSelectedItem());
            this.template.setAttribute(OutputTemplate.ATTR_OUTPUT, outputComboBox.getSelectedItem());
            String ext = extensionTextField.getText().trim();
            this.template.getAttributes().setRemove(OutputTemplate.ATTR_OUTPUT_EXTENSION, "".equals(ext) ? null : ext);
            this.template.getAttributes().setBool(OutputTemplate.ATTR_DEFAULT_TEMPLATE, defaultTemplateCheckbox.isSelected());
            this.setVisible(false);
        } catch (GrafikonException e) {
            LoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
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
            LoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            GuiComponentUtils.showError(e.getMessage(), this);
        }
    }

    private TextTemplate convertToTemplate() throws GrafikonException {
        return textTemplateEditBox.getTemplate();
    }

    private javax.swing.JButton cancelButton;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox<String> outputTypeComboBox;
    private net.parostroj.timetable.gui.components.TextTemplateEditBox2 textTemplateEditBox;
    private javax.swing.JButton verifyButton;
    private JCheckBox defaultTemplateCheckbox;
    private JTextField extensionTextField;
    private JCheckBox scriptCheckBox;
    private JButton scriptButton;
    private JComboBox<String> outputComboBox;
}
