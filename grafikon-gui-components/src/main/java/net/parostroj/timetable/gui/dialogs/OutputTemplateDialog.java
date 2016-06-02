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
import java.util.function.Consumer;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.TextTemplate.Language;
import net.parostroj.timetable.output2.OutputFactory;
import net.parostroj.timetable.utils.AttributeReference;
import net.parostroj.timetable.utils.ObjectsUtil;

import org.beanfabrics.ModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.model.PresentationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.GuiContext;
import net.parostroj.timetable.gui.GuiContextComponent;
import net.parostroj.timetable.gui.components.AttributesPanel;
import net.parostroj.timetable.gui.components.EditLocalizedStringListAddRemovePanel;
import net.parostroj.timetable.gui.components.EditLocalizedStringMultilinePanel;
import net.parostroj.timetable.gui.components.ScriptEditBox;
import net.parostroj.timetable.gui.pm.ARLocalizedStringListPM;
import net.parostroj.timetable.gui.pm.LocalizationTypeFactory;
import net.parostroj.timetable.gui.pm.LocalizedStringPM;

import java.awt.Font;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

/**
 * Dialog for editing text template.
 *
 * @author jub
 */
public class OutputTemplateDialog extends javax.swing.JDialog implements GuiContextComponent {

    private static final String DEFAULT_OUTPUT_SCRIPT = "outputs.add(\"output.html\",[:],\"utf-8\")";
    private static final List<String> OUTPUTS = Collections.unmodifiableList(Arrays.asList("groovy", "draw",
            "pdf.groovy", "xml"));
    private static final Collection<String> OUTPUTS_WITH_TEMPLATE = Collections.unmodifiableCollection(Arrays.asList(
            "groovy", "pdf.groovy"));

    private static final Logger log = LoggerFactory.getLogger(OutputTemplateDialog.class);

    private OutputTemplate template;
    private OutputTemplate resultTemplate;

    private final ModelProvider i18nProvider;

    private final JFileChooser attachmentChooser;
    private final Consumer<Collection<OutputTemplate>> templateWriter;

    public OutputTemplateDialog(Window parent, boolean modal, JFileChooser attachmentChooser,
            Consumer<Collection<OutputTemplate>> templateWriter) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        this.attachmentChooser = attachmentChooser;
        this.templateWriter = templateWriter;
        this.i18nProvider = new ModelProvider();
        initComponents();
        init();
    }

    @Override
    public void registerContext(GuiContext context) {
        context.registerWindow("output.template", this);
    }

    public void showDialog(OutputTemplate template) {
        this.template = template;
        this.resultTemplate = null;
        this.updateValues(false);
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
        return resultTemplate;
    }

    private void updateValues(boolean noTemplate) {
        boolean isScript = this.template.getScript() != null;
        textTemplateEditBox.setEnabled(!noTemplate);
        textTemplateEditBox.setTemplate(noTemplate ? null : this.template.getTemplate());
        extensionTextField.setText(!isScript ? this.template.getAttributes().get(OutputTemplate.ATTR_OUTPUT_EXTENSION,
                String.class) : null);
        nameTextField.setText(this.template.getName());
        outputComboBox.setSelectedItem(this.template.getOutput());
        outputTypeComboBox.setSelectedItem(this.template.getAttribute(OutputTemplate.ATTR_OUTPUT_TYPE, Object.class));
        scriptCheckBox.setSelected(isScript);
        extensionTextField.setEnabled(!isScript);
        scriptEditBox.setScript(template.getScript());
        scriptEditBox.setEnabled(isScript);
        LocalizedString description = template.getDescription();
        LocalizedStringPM pm = new LocalizedStringPM();
        pm.init(description != null ? description : LocalizedString.fromString(""), template.getDiagram().getLocales());
        descriptionTextArea.setPresentationModel(pm);
        // localization
        i18nProvider.setPresentationModel(this.createPM(template.getDiagram(), template));
        // setup
        setupPanel.startEditing(template.getAttributes(), OutputTemplate.CATEGORY_SETTINGS);
    }

    private ARLocalizedStringListPM<AttributeReference<LocalizedString>> createPM(TrainDiagram diagram, AttributesHolder holder) {
        ARLocalizedStringListPM<AttributeReference<LocalizedString>> pm = new ARLocalizedStringListPM<>();
        pm.setSorted(true);
        pm.init(LocalizationTypeFactory.createInstance().createEditFromAttributeHolder(diagram, holder, OutputTemplate.CATEGORY_I18N));
        return pm;
    }

    private void initComponents() {
        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        ((FlowLayout) buttonPanel.getLayout()).setAlignment(FlowLayout.RIGHT);
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

        controlPanel.setLayout(new java.awt.BorderLayout());

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(okButton);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        controlPanel.add(buttonPanel, BorderLayout.EAST);

        getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

        JPanel leftPanel = new JPanel();
        controlPanel.add(leftPanel, BorderLayout.WEST);

        JButton attachmentsButton = new JButton(ResourceLoader.getString("ot.button.attachments")); // NOI18N
        leftPanel.add(attachmentsButton);
        attachmentsButton.addActionListener(evt -> {
            EditAttachmentsDialog dialog = new EditAttachmentsDialog(GuiComponentUtils.getWindow(this), true, attachmentChooser);
            dialog.setLocationRelativeTo(this);
            dialog.showDialog(template);
        });

        JButton writeTemplateOutputButton = new JButton(ResourceLoader.getString("ot.button.output")); // NOI18N
        leftPanel.add(writeTemplateOutputButton);
        writeTemplateOutputButton.setEnabled(this.templateWriter != null);
        writeTemplateOutputButton.addActionListener(evt -> {
            OutputTemplate tempTemplate = this.createTempOutputTemplate();
            if (tempTemplate != null) {
                templateWriter.accept(Collections.singletonList(tempTemplate));
            }
        });

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
        nameTextField = new javax.swing.JTextField(15);
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
                    if (template != null) {
                        if (OUTPUTS_WITH_TEMPLATE.contains(output)) {
                            updateValues(false);
                        } else {
                            updateValues(true);
                        }
                    }
                }
            }
        });

        verifyPanel.add(nameTextField);
        verifyPanel.add(outputComboBox);
        verifyPanel.add(outputTypeComboBox);

        verifyButton = new javax.swing.JButton();

        verifyButton.setText(ResourceLoader.getString("ot.button.verify")); // NOI18N
        verifyButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
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

        javax.swing.JPanel scriptPanel = new javax.swing.JPanel();
        tabbedPane.addTab(ResourceLoader.getString("ot.tab.script"), null, scriptPanel, null); // NOI18N
        scriptPanel.setLayout(new BorderLayout(0, 0));

        scriptEditBox = new ScriptEditBox();
        scriptPanel.add(scriptEditBox);
        scriptEditBox.setScriptFont(new Font("Monospaced", Font.PLAIN, 12));
        scriptEditBox.setRows(15);
        scriptEditBox.setColumns(60);

        EditLocalizedStringListAddRemovePanel localizePanel = new EditLocalizedStringListAddRemovePanel(
                new Path("this"), i18nProvider, 5, false);
        localizePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tabbedPane.addTab(ResourceLoader.getString("ot.tab.localization"), null, localizePanel, null); // NOI18N

        setupPanel = new AttributesPanel();
        setupPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        tabbedPane.addTab(ResourceLoader.getString("ot.tab.setup"), null, setupPanel, null);

        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        descriptionPanel.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab(ResourceLoader.getString("ot.tab.description"), null, descriptionPanel, null); // NOI18N

        JScrollPane descriptionScrollPane = new JScrollPane();
        descriptionPanel.add(descriptionScrollPane);

        descriptionTextArea = new EditLocalizedStringMultilinePanel(5, 5);
        // same font as script area
        descriptionTextArea.setFont(scriptEditBox.getScriptFont());
        descriptionScrollPane.setViewportView(descriptionTextArea);

        scriptCheckBox = new javax.swing.JCheckBox();
        scriptCheckBox.addActionListener(new ActionListener() {
            @Override
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
            this.template.setName(nameTextField.getText().trim());
            this.template.setTemplate(textTemplate);
            this.template.setAttribute(OutputTemplate.ATTR_OUTPUT_TYPE, outputTypeComboBox.getSelectedItem());
            this.template.setAttribute(OutputTemplate.ATTR_OUTPUT, outputComboBox.getSelectedItem());
            this.template.getAttributes().setRemove(OutputTemplate.ATTR_OUTPUT_EXTENSION,
                    ObjectsUtil.checkAndTrim(extensionTextField.getText()));
            this.template.setScript(scriptCheckBox.isSelected() ? scriptEditBox.getScript() : null);
            LocalizedString description = descriptionTextArea.getPresentationModel().getCurrentEdit().get();
            description = description.getDefaultString().trim().isEmpty() ? null : description;
            this.template.getAttributes().setRemove(OutputTemplate.ATTR_DESCRIPTION, description);
            this.setVisible(false);

            // localization
            writeBackLocalization();

            // setup
            setupPanel.stopEditing();

            this.resultTemplate = this.template;
        } catch (GrafikonException e) {
            log.error(e.getMessage(), e);
            GuiComponentUtils.showError(e.getMessage(), this);
        }
    }

    /**
     * Creates output template for testing generation of an output.
     *
     * @return template
     */
    private OutputTemplate createTempOutputTemplate() {
        try {
            TextTemplate textTemplate = this.convertToTemplate();
            OutputTemplate outputTemplate = this.template.getDiagram().getPartFactory().createOutputTemplate("temp_id");
            outputTemplate.setName(this.template.getName());
            outputTemplate.setTemplate(textTemplate);
            outputTemplate.setAttribute(OutputTemplate.ATTR_OUTPUT_TYPE, outputTypeComboBox.getSelectedItem());
            outputTemplate.setAttribute(OutputTemplate.ATTR_OUTPUT, outputComboBox.getSelectedItem());
            outputTemplate.getAttributes().setRemove(OutputTemplate.ATTR_OUTPUT_EXTENSION,
                    ObjectsUtil.checkAndTrim(extensionTextField.getText()));
            outputTemplate.setScript(scriptCheckBox.isSelected() ? scriptEditBox.getScript() : null);
            writeBackLocalization();
            outputTemplate.getAttributes().merge(template.getAttributes(), OutputTemplate.CATEGORY_I18N);
            outputTemplate.getAttributes().merge(template.getAttributes(), OutputTemplate.CATEGORY_SETTINGS);
            outputTemplate.getAttachments().addAll(template.getAttachments());
            return outputTemplate;
        } catch (GrafikonException e) {
            log.error(e.getMessage(), e);
            GuiComponentUtils.showError(e.getMessage(), this);
            return null;
        }
    }

    private void writeBackLocalization() {
        PresentationModel pm = this.i18nProvider.getPresentationModel();
        if (pm != null) {
            ((ARLocalizedStringListPM<?>) pm).ok();
        }
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setupPanel.stopEditing();
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
        return textTemplateEditBox.isEnabled() ? textTemplateEditBox.getTemplate() : null;
    }

    private javax.swing.JButton cancelButton;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox<String> outputTypeComboBox;
    private net.parostroj.timetable.gui.components.TextTemplateEditBox2 textTemplateEditBox;
    private javax.swing.JButton verifyButton;
    private javax.swing.JTextField extensionTextField;
    private javax.swing.JCheckBox scriptCheckBox;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JComboBox<String> outputComboBox;
    private javax.swing.JTabbedPane tabbedPane;
    private ScriptEditBox scriptEditBox;
    private EditLocalizedStringMultilinePanel descriptionTextArea;
    private AttributesPanel setupPanel;
}
