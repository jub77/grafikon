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

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.TextTemplate.Language;
import net.parostroj.timetable.output2.OutputFactory;
import net.parostroj.timetable.utils.ObjectsUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.components.ScriptEditBox;

import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * Dialog for editing text template.
 *
 * @author jub
 */
public class OutputTemplateDialog extends javax.swing.JDialog {

    private static final String DEFAULT_OUTPUT_SCRIPT = "outputs.add(\"output.html\",[:],\"utf-8\")";
    private static final List<String> OUTPUTS = Collections.unmodifiableList(Arrays.asList("groovy", "draw"));
    private static final Set<String> OUTPUTS_WITH_TEMPLATE = Collections.unmodifiableSet(Collections
            .singleton("groovy"));

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
        extensionTextField.setText(!isScript ? this.template.getAttributes().get(OutputTemplate.ATTR_OUTPUT_EXTENSION,
                String.class) : null);
        outputComboBox.setSelectedItem(this.template.getOutput());
        outputTypeComboBox.setSelectedItem(this.template.getAttribute(OutputTemplate.ATTR_OUTPUT_TYPE, Object.class));
        scriptCheckBox.setSelected(isScript);
        extensionTextField.setEnabled(!isScript);
        scriptEditBox.setScript(template.getScript());
        scriptEditBox.setEnabled(isScript);
        descriptionTextArea.setText(template.getAttribute(OutputTemplate.ATTR_DESCRIPTION, String.class));
    }

    private void initComponents() {
        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        ((FlowLayout) buttonPanel.getLayout()).setAlignment(FlowLayout.RIGHT);
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

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

        getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

        tabbedPane = new javax.swing.JTabbedPane(javax.swing.JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        javax.swing.JPanel templatePanel = new javax.swing.JPanel();
        tabbedPane.addTab(ResourceLoader.getString("ot.tab.template"), null, templatePanel, null); // NOI18N
        templatePanel.setLayout(new BorderLayout(0, 0));
        textTemplateEditBox = new net.parostroj.timetable.gui.components.TextTemplateEditBox2();
        templatePanel.add(textTemplateEditBox);

        textTemplateEditBox.setTemplateFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        textTemplateEditBox.setColumns(80);
        textTemplateEditBox.setRows(25);
        textTemplateEditBox.setVisibleTemplateLanguageChange(false);
        javax.swing.JPanel verifyPanel = new javax.swing.JPanel();
        templatePanel.add(verifyPanel, BorderLayout.SOUTH);
        FlowLayout flowLayout1 = (FlowLayout) verifyPanel.getLayout();
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

        verifyPanel.add(outputComboBox);
        verifyPanel.add(outputTypeComboBox);

        defaultTemplateCheckbox = new javax.swing.JCheckBox(ResourceLoader.getString("ot.checkbox.default.template"));
        verifyPanel.add(defaultTemplateCheckbox);
        verifyButton = new javax.swing.JButton();

        verifyButton.setText(ResourceLoader.getString("ot.button.verify")); // NOI18N
        verifyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verifyButtonActionPerformed(evt);
            }
        });
        verifyPanel.add(verifyButton);

        javax.swing.JLabel suffixLabel = new javax.swing.JLabel(ResourceLoader.getString("ot.extension") + ":");
        verifyPanel.add(suffixLabel);

        extensionTextField = new javax.swing.JTextField();
        verifyPanel.add(extensionTextField);
        extensionTextField.setColumns(10);
        defaultTemplateCheckbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateValues(defaultTemplateCheckbox.isSelected());
            }
        });

        javax.swing.JPanel scriptPanel = new javax.swing.JPanel();
        tabbedPane.addTab(ResourceLoader.getString("ot.tab.script"), null, scriptPanel, null); // NOI18N
        scriptPanel.setLayout(new BorderLayout(0, 0));

        scriptEditBox = new ScriptEditBox();
        scriptPanel.add(scriptEditBox);
        scriptEditBox.setScriptFont(new Font("Monospaced", Font.PLAIN, 12));
        scriptEditBox.setRows(15);
        scriptEditBox.setColumns(60);

        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        tabbedPane.addTab(ResourceLoader.getString("ot.tab.description"), null, descriptionPanel, null); // NOI18N
        descriptionPanel.setLayout(new BorderLayout(0, 0));

        JScrollPane descriptionScrollPane = new JScrollPane();
        descriptionPanel.add(descriptionScrollPane);

        descriptionTextArea = new JTextArea();
        descriptionTextArea.setLineWrap(true);
        // same font as script area
        descriptionTextArea.setFont(scriptEditBox.getScriptFont());
        descriptionScrollPane.setViewportView(descriptionTextArea);

        scriptCheckBox = new javax.swing.JCheckBox();
        scriptCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isScript = scriptCheckBox.isSelected();
                scriptEditBox.setEnabled(isScript);
                extensionTextField.setEnabled(!isScript);
                if (isScript) {
                    extensionTextField.setText(null);
                    try {
                        scriptEditBox.setScript(Script.createScript(DEFAULT_OUTPUT_SCRIPT, Script.Language.GROOVY));
                    } catch (GrafikonException e1) {
                        log.error("Error creating script.", e);
                    }
                } else {
                    scriptEditBox.setScript(null);
                }
            }
        });
        scriptEditBox.addComponentToEditBox(scriptCheckBox);

        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            TextTemplate textTemplate = this.convertToTemplate();
            this.template.setTemplate(textTemplate);
            this.template.setAttribute(OutputTemplate.ATTR_OUTPUT_TYPE, outputTypeComboBox.getSelectedItem());
            this.template.setAttribute(OutputTemplate.ATTR_OUTPUT, outputComboBox.getSelectedItem());
            this.template.getAttributes().setRemove(OutputTemplate.ATTR_OUTPUT_EXTENSION,
                    ObjectsUtil.checkAndTrim(extensionTextField.getText()));
            this.template.getAttributes().setBool(OutputTemplate.ATTR_DEFAULT_TEMPLATE,
                    defaultTemplateCheckbox.isSelected());
            this.template.setScript(scriptEditBox.getScript());
            this.template.getAttributes().setRemove(OutputTemplate.ATTR_DESCRIPTION,
                    ObjectsUtil.checkAndTrim(descriptionTextArea.getText()));
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
    private javax.swing.JCheckBox defaultTemplateCheckbox;
    private javax.swing.JTextField extensionTextField;
    private javax.swing.JCheckBox scriptCheckBox;
    private javax.swing.JComboBox<String> outputComboBox;
    private javax.swing.JTabbedPane tabbedPane;
    private ScriptEditBox scriptEditBox;
    private JTextArea descriptionTextArea;
}
