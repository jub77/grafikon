/*
 * TextTemplateListDialog.java
 *
 * Created on 14.4.2011, 18:17:18
 */
package net.parostroj.timetable.gui.dialogs;

import java.io.File;
import java.util.Collections;
import java.util.Locale;

import javax.swing.JFileChooser;

import net.parostroj.timetable.gui.GuiContext;
import net.parostroj.timetable.gui.GuiContextComponent;
import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.components.ChangeDocumentListener;
import net.parostroj.timetable.gui.components.JTextAreaGrey;
import net.parostroj.timetable.gui.utils.*;
import net.parostroj.timetable.gui.wrappers.OutputTemplateWrapperDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperDelegate;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.OutputWriter.Settings;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ObjectsUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ScrollPaneConstants;

/**
 * Dialog for editing list of output templates.
 *
 * @author jub
 */
public class OutputTemplateListDialog extends javax.swing.JDialog implements GuiContextComponent {

    private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(OutputTemplateListDialog.class);

    private TrainDiagram diagram;
    private WrapperListModel<OutputTemplate> templatesModel;
    private File outputDirectory;
    private JFileChooser attachmentsChooser;
    private Settings settings;

    private GuiContext context;

    private final WrapperDelegate<OutputTemplate> otWrapperDelegate;

    /** Creates new form TextTemplateListDialog */
    public OutputTemplateListDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        otWrapperDelegate = new OutputTemplateWrapperDelegate();
    }

    @Override
    public void registerContext(GuiContext context) {
        context.registerWindow("output.template.list", this);
        this.context = context;
    }

    public void showDialog(final TrainDiagram diagram, File outputDirectory, JFileChooser attachmentsChooser, Settings settings) {
        this.diagram = diagram;
        this.attachmentsChooser = attachmentsChooser;
        this.settings = settings;
        this.outputDirectory = outputDirectory;
        templatesModel = new WrapperListModel<>(Wrapper.getWrapperList(diagram.getOutputTemplates(), otWrapperDelegate));
        ItemSet<OutputTemplate> outputTemplates = diagram.getOutputTemplates();
        templatesModel.setObjectListener(new WrapperListModel.ObjectListener<OutputTemplate>() {
            @Override
            public void added(OutputTemplate object, int index) {
                outputTemplates.add(object);
            }

            @Override
            public void removed(OutputTemplate object) {
                outputTemplates.remove(object);
            }

            @Override
            public void moved(OutputTemplate object, int fromIndex, int toIndex) {
            }
        });
        templateList.setModel(templatesModel);
        this.setVisible(true);
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            updateButtons();
        }
        super.setVisible(b);
    }

    private void updateButtons() {
        String newName = ObjectsUtil.checkAndTrim(nameTextField.getText());
        int selectedCount = templateList.getSelectedIndices().length;
        deleteButton.setEnabled(selectedCount > 0);
        editButton.setEnabled(selectedCount == 1);
        copyButton.setEnabled(newName != null && selectedCount == 1);
        // create button
        newButton.setEnabled(newName != null);

        // description
        if (selectedCount == 1) {
            updateDescription(templateList.getSelectedValue().getElement().getDescription());
        } else {
            updateDescription(null);
        }
    }

    private void updateDescription(LocalizedString description) {
        if (description == null) {
            descriptionTextArea.setText("");
        } else {
            descriptionTextArea.setText(description.translate(Locale.getDefault()));
            descriptionTextArea.moveCaretPosition(0);
        }
    }

    private void initComponents() {
        buttonPanel = new javax.swing.JPanel();
        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        nameTextField = new javax.swing.JTextField();
        newButton = GuiComponentUtils.createButton(GuiIcon.ADD, 1);
        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 1);
        copyButton = GuiComponentUtils.createButton(GuiIcon.COPY, 1);
        editButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 1);
        javax.swing.JPanel listPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        templateList = new javax.swing.JList<>();
        templateList.setVisibleRowCount(10);

        templateList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !templateList.isSelectionEmpty()
                        && templateList.getSelectedIndices().length == 1) {
                    editButtonActionPerformed();
                }
            }
        });

        templateList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !templateList.isSelectionEmpty()
                        && templateList.getSelectedIndices().length == 1) {
                    editButtonActionPerformed();
                }
            }
        });

        setTitle(ResourceLoader.getString("ot.title")); // NOI18N

        buttonPanel.setLayout(new java.awt.BorderLayout());

        controlPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        controlPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 3));

        nameTextField.setColumns(10);
        nameTextField.getDocument().addDocumentListener(new ChangeDocumentListener() {
            @Override
            protected void change() {
                updateButtons();
            }
        });
        controlPanel.add(nameTextField);

        newButton.addActionListener(evt -> newButtonActionPerformed(evt));
        controlPanel.add(newButton);

        deleteButton.addActionListener(evt ->deleteButtonActionPerformed(evt));
        controlPanel.add(deleteButton);

        copyButton.addActionListener(evt -> copyButtonActionPerformed());
        controlPanel.add(copyButton);

        editButton.addActionListener(evt -> editButtonActionPerformed());
        controlPanel.add(editButton);

        buttonPanel.add(controlPanel, java.awt.BorderLayout.NORTH);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.LINE_END);

        listPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 0));
        listPanel.setLayout(new java.awt.BorderLayout());

        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        templateList.addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                updateButtons();
            }
        });
        scrollPane.setViewportView(templateList);

        listPanel.add(scrollPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(listPanel, java.awt.BorderLayout.CENTER);

        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setBorder(new EmptyBorder(0, 5, 5, 5));
        getContentPane().add(descriptionPanel, BorderLayout.SOUTH);
        descriptionPanel.setLayout(new BorderLayout(0, 0));

        javax.swing.JScrollPane descriptionScrollPane = new javax.swing.JScrollPane();
        descriptionScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        descriptionPanel.add(descriptionScrollPane, BorderLayout.CENTER);
        descriptionTextArea = new JTextAreaGrey();
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setRows(4);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        // same font as list
        descriptionTextArea.setFont(templateList.getFont());
        descriptionScrollPane.setViewportView(descriptionTextArea);

        pack();
        this.setMinimumSize(this.getSize());
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int[] indices = templateList.getSelectedIndices();
        if (indices.length > 0) {
            int cnt = 0;
            for (int index : indices) {
                templatesModel.removeIndex(index - (cnt++));
            }
            int index = indices[0];
            if (index > templatesModel.getSize() - 1) {
                templateList.setSelectedIndex(templatesModel.getSize() - 1);
            } else {
                templateList.setSelectedIndex(index);
            }
        }
    }

    private void copyButtonActionPerformed() {
        OutputTemplate selectedTemplate = templatesModel.getIndex(templateList.getSelectedIndex()).getElement();
        OutputTemplate template = this.copyTemplate(selectedTemplate);
        template.setKey(ObjectsUtil.checkAndTrim(nameTextField.getText()));
        Wrapper<OutputTemplate> wrapper = Wrapper.getWrapper(template, otWrapperDelegate);
        templatesModel.addWrapper(wrapper);
        nameTextField.setText("");
        templateList.setSelectedValue(wrapper, true);
        this.updateButtons();
    }

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {
        OutputTemplate template = diagram.getPartFactory().createOutputTemplate(IdGenerator.getInstance().getId());
        template.setKey(nameTextField.getText().trim());
        try {
            template.setTemplate(TextTemplate.create("", TextTemplate.Language.GROOVY));
        } catch (GrafikonException e) {
            log.error("Error creating template.", e);
        }
        template.setAttribute(OutputTemplate.ATTR_OUTPUT_TYPE, "diagram");
        Wrapper<OutputTemplate> wrapper = Wrapper.getWrapper(template, otWrapperDelegate);
        templatesModel.addWrapper(wrapper);
        nameTextField.setText("");
        templateList.setSelectedValue(wrapper, true);
        this.updateButtons();
    }

    private void editButtonActionPerformed() {
        OutputTemplateDialog dialog = new OutputTemplateDialog(this, true, attachmentsChooser, this::outputButtonAction);
        dialog.setLocationRelativeTo(this);
        dialog.registerContext(context);
        // get template
        OutputTemplate template = templatesModel.getIndex(templateList.getSelectedIndex()).getElement();
        dialog.setTitle(template.getKey());
        dialog.showDialog(template);
        // update description
        updateDescription(template.getDescription());
        Wrapper<OutputTemplate> selectedValue = templateList.getSelectedValue();
        templatesModel.refreshAll();
        templateList.setSelectedValue(selectedValue, true);
    }

    private void outputButtonAction(OutputTemplate outputTemplate) {
        ActionContext c = new ActionContext();
        c.setLocationComponent(this);
        Output testOutput = diagram.getPartFactory().createOutput(IdGenerator.getInstance().getId());
        testOutput.setName(LocalizedString.fromString(outputTemplate.getKey()));
        testOutput.setTemplate(outputTemplate);
        log.debug("Writing template {} to {}", testOutput.getName().getDefaultString(), outputDirectory);
        OutputTemplateAction action = new OutputTemplateAction(c, diagram, settings, outputDirectory,
                Collections.singletonList(testOutput));
        ActionHandler.getInstance().execute(action);
    }

    private OutputTemplate copyTemplate(OutputTemplate template) {
        return new CopyFactory(template.getDiagram().getPartFactory()).copy(template, IdGenerator.getInstance().getId());
    }

    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton copyButton;
    private javax.swing.JButton editButton;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton newButton;
    private javax.swing.JList<Wrapper<OutputTemplate>> templateList;
    private javax.swing.JTextArea descriptionTextArea;
}
