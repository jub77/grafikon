/*
 * TextTemplateListDialog.java
 *
 * Created on 14.4.2011, 18:17:18
 */
package net.parostroj.timetable.gui.dialogs;

import java.io.File;
import java.util.Collection;
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

import com.google.common.base.Objects;
import com.google.common.collect.Collections2;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.ScrollPaneConstants;

/**
 * Dialog for editing list of output templates.
 *
 * @author jub
 */
public class OutputTemplateListDialog extends javax.swing.JDialog implements GuiContextComponent {

    private static final Logger log = LoggerFactory.getLogger(OutputTemplateListDialog.class);

    private TrainDiagram diagram;
    private WrapperListModel<OutputTemplate> templatesModel;
    private File outputDirectory;
    private JFileChooser chooser;
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

    public void showDialog(final TrainDiagram diagram, JFileChooser chooser, JFileChooser attachmentsChooser, Settings settings) {
        this.diagram = diagram;
        this.chooser = chooser;
        this.attachmentsChooser = attachmentsChooser;
        this.settings = settings;
        this.outputDirectory = chooser.getSelectedFile() == null ? chooser.getCurrentDirectory() : chooser.getSelectedFile();
        this.locationTextField.setText(this.outputDirectory.getPath());
        templatesModel = new WrapperListModel<OutputTemplate>(
                Wrapper.getWrapperList(diagram.getOutputTemplates(), otWrapperDelegate),
                null,
                false);
        ItemList<OutputTemplate> outputTemplates = diagram.getOutputTemplates();
        templatesModel.setObjectListener(new WrapperListModel.ObjectListener<OutputTemplate>() {
            @Override
            public void added(OutputTemplate object, int index) {
                outputTemplates.add(index, object);
            }

            @Override
            public void removed(OutputTemplate object) {
                outputTemplates.remove(object);
            }

            @Override
            public void moved(OutputTemplate object, int fromIndex, int toIndex) {
                outputTemplates.move(fromIndex, toIndex);
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
        downButton.setEnabled(selectedCount == 1);
        upButton.setEnabled(selectedCount == 1);
        deleteButton.setEnabled(selectedCount > 0);
        editButton.setEnabled(selectedCount == 1);
        outputButton.setEnabled(selectedCount > 0);
        copyButton.setEnabled(newName != null && selectedCount == 1);
        // create button
        newButton.setEnabled(newName != null);

        // description
        if (selectedCount == 1) {
            updateDescription(templatesModel.getIndex(templateList.getSelectedIndex()).getElement().getDescription());
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
        upButton = GuiComponentUtils.createButton(GuiIcon.GO_UP, 1);
        downButton = GuiComponentUtils.createButton(GuiIcon.GO_DOWN, 1);
        editButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 1);
        outputButton = new javax.swing.JButton();
        outputAllButton = new javax.swing.JButton();
        javax.swing.JPanel listPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        templateList = new javax.swing.JList<Wrapper<OutputTemplate>>();
        templateList.setVisibleRowCount(10);
        javax.swing.JPanel locationPanel = new javax.swing.JPanel();
        javax.swing.JPanel locationPanel1 = new javax.swing.JPanel();
        locationTextField = new javax.swing.JTextField();
        javax.swing.JPanel locationPanel2 = new javax.swing.JPanel();
        locationButton = new javax.swing.JButton();

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

        upButton.addActionListener(evt -> upButtonActionPerformed(evt));
        controlPanel.add(upButton);

        downButton.addActionListener(evt -> downButtonActionPerformed(evt));
        controlPanel.add(downButton);

        editButton.addActionListener(evt -> editButtonActionPerformed());
        controlPanel.add(editButton);

        outputButton.setText(ResourceLoader.getString("ot.button.output")); // NOI18N
        outputButton.addActionListener(
                evt -> outputButtonAction(
                        templateList.isSelectionEmpty() ? null :
                            Collections2.transform(templatesModel.getIndices(templateList.getSelectedIndices()),
                        item -> item.getElement())));
        controlPanel.add(outputButton);

        outputAllButton.setText(ResourceLoader.getString("ot.button.outputall")); // NOI18N
        outputAllButton.addActionListener(evt -> outputAllButtonActionPerformed(evt));
        controlPanel.add(outputAllButton);

        buttonPanel.add(controlPanel, java.awt.BorderLayout.NORTH);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.LINE_END);

        listPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 0));
        listPanel.setLayout(new java.awt.BorderLayout());

        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        templateList.addListSelectionListener(evt -> updateButtons());
        scrollPane.setViewportView(templateList);

        listPanel.add(scrollPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(listPanel, java.awt.BorderLayout.CENTER);

        locationPanel.setLayout(new java.awt.BorderLayout());

        locationPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 0));
        locationPanel1.setLayout(new java.awt.BorderLayout());

        locationTextField.setEditable(false);
        locationPanel1.add(locationTextField, java.awt.BorderLayout.CENTER);

        locationPanel.add(locationPanel1, java.awt.BorderLayout.CENTER);

        locationPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 5));
        locationPanel2.setLayout(new java.awt.BorderLayout());

        locationButton.setText(ResourceLoader.getString("button.select")); // NOI18N
        locationButton.addActionListener(evt -> locationButtonActionPerformed(evt));
        locationPanel2.add(locationButton, java.awt.BorderLayout.CENTER);

        locationPanel.add(locationPanel2, java.awt.BorderLayout.EAST);

        getContentPane().add(locationPanel, java.awt.BorderLayout.PAGE_START);

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

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int index = templateList.getSelectedIndex();
        if (index != -1 && index != (templatesModel.getSize() - 1)) {
            templatesModel.moveIndexDown(index);
            templateList.setSelectedIndex(index + 1);
        }
    }

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int index = templateList.getSelectedIndex();
        if (index != -1 && index != 0) {
            templatesModel.moveIndexUp(index);
            templateList.setSelectedIndex(index - 1);
        }
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
        template.setName(ObjectsUtil.checkAndTrim(nameTextField.getText()));
        Wrapper<OutputTemplate> wrapper = Wrapper.getWrapper(template, otWrapperDelegate);
        templatesModel.addWrapper(wrapper);
        nameTextField.setText("");
        templateList.setSelectedValue(wrapper, true);
        this.updateButtons();
    }

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {
        OutputTemplate template = diagram.getPartFactory().createOutputTemplate(IdGenerator.getInstance().getId());
        template.setName(nameTextField.getText().trim());
        try {
            template.setTemplate(TextTemplate.createTextTemplate("", TextTemplate.Language.GROOVY));
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
        dialog.setTitle(template.getName());
        dialog.showDialog(this.copyTemplate(template));
        if (dialog.getTemplate() != null) {
            this.mergeTemplate(template, dialog.getTemplate());
            // update description
            updateDescription(template.getDescription());
        }
    }

    private void locationButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int returnValue = chooser.showOpenDialog(getParent());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            this.outputDirectory = chooser.getSelectedFile();
            // update text string
            locationTextField.setText(this.outputDirectory.getPath());
        }
    }

    private void outputButtonAction(Collection<OutputTemplate> outputTemplates) {
        ActionContext c = new ActionContext();
        c.setLocationComponent(this);
        OutputTemplateAction action = new OutputTemplateAction(c, diagram, settings, outputDirectory,
                outputTemplates);
        ActionHandler.getInstance().execute(action);
    }

    private void outputAllButtonActionPerformed(java.awt.event.ActionEvent evt) {
        ActionContext c = new ActionContext();
        c.setLocationComponent(this);
        OutputTemplateAction action = new OutputTemplateAction(c, diagram, settings, outputDirectory, diagram.getOutputTemplates());
        ActionHandler.getInstance().execute(action);
    }

    private OutputTemplate copyTemplate(OutputTemplate template) {
        return CopyFactory.getInstance(template.getDiagram()).copy(template, IdGenerator.getInstance().getId());
    }

    private void mergeTemplate(OutputTemplate template, OutputTemplate fromTemplate) {
        if (!template.getName().equals(fromTemplate.getName())) {
            template.setName(fromTemplate.getName());
        }
        if (!Objects.equal(template.getTemplate(), fromTemplate.getTemplate())) {
            template.setTemplate(fromTemplate.getTemplate());
        }
        template.getAttributes().merge(fromTemplate.getAttributes());
        template.setScript(fromTemplate.getScript());
        template.getAttachments().replaceAll(fromTemplate.getAttachments());
    }

    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton copyButton;
    private javax.swing.JButton downButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton locationButton;
    private javax.swing.JTextField locationTextField;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton newButton;
    private javax.swing.JButton outputAllButton;
    private javax.swing.JButton outputButton;
    private javax.swing.JList<Wrapper<OutputTemplate>> templateList;
    private javax.swing.JButton upButton;
    private javax.swing.JTextArea descriptionTextArea;
}
