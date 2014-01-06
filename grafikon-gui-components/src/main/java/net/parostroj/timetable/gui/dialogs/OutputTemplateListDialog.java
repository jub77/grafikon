/*
 * TextTemplateListDialog.java
 *
 * Created on 14.4.2011, 18:17:18
 */
package net.parostroj.timetable.gui.dialogs;

import java.io.File;

import javax.swing.JFileChooser;

import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.utils.IdGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialog for editing list of output templates.
 *
 * @author jub
 */
public class OutputTemplateListDialog extends javax.swing.JDialog {

    public static class Settings {

        private final boolean title;
        private final boolean twoSided;
        private final boolean techTimes;

        public Settings(boolean title, boolean twoSided, boolean techTimes) {
            this.title = title;
            this.twoSided = twoSided;
            this.techTimes = techTimes;
        }

        public boolean isTitle() {
            return title;
        }

        public boolean isTwoSided() {
            return twoSided;
        }

        public boolean isTechTimes() {
            return techTimes;
        }

        public OutputParams createParams() {
            OutputParams params = new OutputParams();
            if (title) {
                params.setParam("title.page", true);
            }
            params.setParam("page.sort", twoSided ? "two_sides" : "one_side");
            if (techTimes) {
                params.setParam("tech.time", true);
            }
            return params;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(OutputTemplateListDialog.class);

    private TrainDiagram diagram;
    private final WrapperListModel<OutputTemplate> templatesModel;
    private File outputDirectory;
    private JFileChooser chooser;
    private Settings settings;

    /** Creates new form TextTemplateListDialog */
    public OutputTemplateListDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        templatesModel = new WrapperListModel<OutputTemplate>(false);
        templateList.setModel(templatesModel);
    }

    public void showDialog(TrainDiagram diagram, JFileChooser chooser, Settings settings) {
        this.diagram = diagram;
        this.chooser = chooser;
        this.settings = settings;
        this.outputDirectory = chooser.getSelectedFile() == null ? chooser.getCurrentDirectory() : chooser.getSelectedFile();
        this.locationTextField.setText(this.outputDirectory.getPath());
        this.fillList();
        this.setVisible(true);
    }

    @Override
    public void setVisible(boolean b) {
        if (b)
            updateButtons();
        super.setVisible(b);
    }

    private void fillList() {
        for (OutputTemplate template : diagram.getOutputTemplates()) {
            templatesModel.addWrapper(new Wrapper<OutputTemplate>(template));
        }
    }

    private void updateButtons() {
        boolean selected = templateList.getSelectedValue() != null;
        downButton.setEnabled(selected);
        upButton.setEnabled(selected);
        deleteButton.setEnabled(selected);
        editButton.setEnabled(selected);
        outputButton.setEnabled(selected);
        // create button
        newButton.setEnabled(!"".equals(nameTextField.getText().trim()));
    }

    private void initComponents() {
        buttonPanel = new javax.swing.JPanel();
        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        nameTextField = new javax.swing.JTextField();
        newButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        outputButton = new javax.swing.JButton();
        outputAllButton = new javax.swing.JButton();
        javax.swing.JPanel okPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        javax.swing.JPanel listPanel = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        templateList = new javax.swing.JList();
        javax.swing.JPanel locationPanel = new javax.swing.JPanel();
        javax.swing.JPanel locationPanel1 = new javax.swing.JPanel();
        locationTextField = new javax.swing.JTextField();
        javax.swing.JPanel locationPanel2 = new javax.swing.JPanel();
        locationButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(ResourceLoader.getString("ot.title")); // NOI18N

        buttonPanel.setLayout(new java.awt.BorderLayout());

        controlPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        controlPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 3));

        nameTextField.setColumns(10);
        nameTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                nameTextFieldCaretUpdate(evt);
            }
        });
        controlPanel.add(nameTextField);

        newButton.setText(ResourceLoader.getString("button.new")); // NOI18N
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        controlPanel.add(newButton);

        deleteButton.setText(ResourceLoader.getString("button.delete")); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        controlPanel.add(deleteButton);

        upButton.setText("^");
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });
        controlPanel.add(upButton);

        downButton.setText("v");
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });
        controlPanel.add(downButton);

        editButton.setText(ResourceLoader.getString("button.edit")); // NOI18N
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        controlPanel.add(editButton);

        outputButton.setText(ResourceLoader.getString("ot.button.output")); // NOI18N
        outputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputButtonActionPerformed(evt);
            }
        });
        controlPanel.add(outputButton);

        outputAllButton.setText(ResourceLoader.getString("ot.button.outputall")); // NOI18N
        outputAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputAllButtonActionPerformed(evt);
            }
        });
        controlPanel.add(outputAllButton);

        buttonPanel.add(controlPanel, java.awt.BorderLayout.NORTH);

        okPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        okPanel.setLayout(new java.awt.GridLayout(1, 0));

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        okPanel.add(okButton);

        buttonPanel.add(okPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.LINE_END);

        listPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 0));
        listPanel.setLayout(new java.awt.BorderLayout());

        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        templateList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        templateList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                templateListValueChanged(evt);
            }
        });
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

        locationButton.setText(ResourceLoader.getString("button.select") + "..."); // NOI18N
        locationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationButtonActionPerformed(evt);
            }
        });
        locationPanel2.add(locationButton, java.awt.BorderLayout.CENTER);

        locationPanel.add(locationPanel2, java.awt.BorderLayout.EAST);

        getContentPane().add(locationPanel, java.awt.BorderLayout.PAGE_START);

        pack();
    }

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int index = templateList.getSelectedIndex();
        if (index != -1 && index != (templatesModel.getSize() - 1)) {
            Wrapper<OutputTemplate> wrapper = templatesModel.removeIndex(index);
            diagram.moveOutputTemplate(index, index + 1);
            index++;
            templatesModel.addWrapper(wrapper, index);
            templateList.setSelectedIndex(index);
        }
    }

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int index = templateList.getSelectedIndex();
        if (index != -1 && index != 0) {
            Wrapper<OutputTemplate> wrapper = templatesModel.removeIndex(index);
            diagram.moveOutputTemplate(index, index - 1);
            index--;
            templatesModel.addWrapper(wrapper, index);
            templateList.setSelectedIndex(index);
        }
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        Wrapper<?> wrapper = (Wrapper<?>) templateList.getSelectedValue();
        if (wrapper != null) {
            templatesModel.removeObject((OutputTemplate) wrapper.getElement());
            diagram.removeOutputTemplate((OutputTemplate) wrapper.getElement());
        }
    }

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {
        OutputTemplate template = new OutputTemplate(IdGenerator.getInstance().getId(), diagram);
        template.setName(nameTextField.getText().trim());
        try {
            template.setTemplate(TextTemplate.createTextTemplate("", TextTemplate.Language.GROOVY));
        } catch (GrafikonException e) {
            LOG.error("Error creating template.", e);
        }
        template.setAttribute(OutputTemplate.ATTR_OUTPUT_TYPE, "diagram");
        diagram.addOutputTemplate(template);
        Wrapper<OutputTemplate> wrapper = new Wrapper<OutputTemplate>(template);
        templatesModel.addWrapper(wrapper);
        nameTextField.setText("");
        templateList.setSelectedValue(wrapper, true);
        this.updateButtons();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
    }

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {
        OutputTemplateDialog dialog = new OutputTemplateDialog(this, true);
        dialog.setLocationRelativeTo(this);
        // get template
        OutputTemplate template = (OutputTemplate) ((Wrapper<?>) templateList.getSelectedValue()).getElement();
        dialog.setTitle(template.getName());
        dialog.showDialog(this.copyTemplate(template));
        if (dialog.getTemplate() != null) {
            this.mergeTemplate(template, dialog.getTemplate());
        }
    }

    private void nameTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {
        this.updateButtons();
    }

    private void templateListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        this.updateButtons();
    }

    private void locationButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int returnValue = chooser.showOpenDialog(getParent());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            this.outputDirectory = chooser.getSelectedFile();
            // update text string
            locationTextField.setText(this.outputDirectory.getPath());
        }
    }

    private void outputButtonActionPerformed(java.awt.event.ActionEvent evt) {
        ActionContext c = new ActionContext();
        c.setLocationComponent(this);
        ModelAction action = new EventDispatchAfterModelAction(c) {

            private String errorMessage;
            private OutputTemplate errorTemplate;

            @Override
            protected void backgroundAction() {
                long time = System.currentTimeMillis();
                setWaitMessage(ResourceLoader.getString("ot.message.wait"));
                setWaitDialogVisible(true);
                try {
                    Wrapper<?> wrapper = (Wrapper<?>) templateList.getSelectedValue();
                    if (wrapper != null) {
                        try {
                            errorTemplate = (OutputTemplate) wrapper.getElement();
                            generateOutput((OutputTemplate) wrapper.getElement());
                        } catch (OutputException e) {
                            LOG.error(e.getMessage(), e);
                            errorMessage = e.getMessage();
                        }
                    }
                } catch (Exception e) {
                    LOG.warn(e.getMessage(), e);
                    errorMessage = ResourceLoader.getString("ot.message.error");
                } finally {
                    setWaitDialogVisible(false);
                }
                time = System.currentTimeMillis() - time;
                LOG.debug("Generated in {}ms", time);
            }

            @Override
            protected void eventDispatchActionAfter() {
                if (errorMessage != null) {
                    ActionUtils.showError(errorTemplate.getName() + ": " + errorMessage, OutputTemplateListDialog.this);
                }
            }
        };
        ActionHandler.getInstance().execute(action);
    }

    private void outputAllButtonActionPerformed(java.awt.event.ActionEvent evt) {
        ActionContext c = new ActionContext();
        c.setLocationComponent(this);
        ModelAction action = new EventDispatchAfterModelAction(c) {

            private String errorMessage;
            private OutputTemplate errorTemplate;

            @Override
            protected void backgroundAction() {
                long time = System.currentTimeMillis();
                setWaitMessage(ResourceLoader.getString("ot.message.wait"));
                setWaitDialogVisible(true);
                try {
                    try {
                        for (OutputTemplate template : diagram.getOutputTemplates()) {
                            errorTemplate = template;
                            generateOutput(template);
                        }
                    } catch (OutputException e) {
                        LOG.error(e.getMessage(), e);
                        errorMessage = e.getMessage();
                    }
                } catch (Exception e) {
                    LOG.warn(e.getMessage(), e);
                    errorMessage = ResourceLoader.getString("ot.message.error");
                } finally {
                    setWaitDialogVisible(false);
                }
                time = System.currentTimeMillis() - time;
                LOG.debug("Generated in {}ms", time);
            }

            @Override
            protected void eventDispatchActionAfter() {
                if (errorMessage != null) {
                    ActionUtils.showError(errorTemplate.getName() + ": " + errorMessage, OutputTemplateListDialog.this);
                }
            }
        };
        ActionHandler.getInstance().execute(action);
    }

    private void generateOutput(OutputTemplate template) throws OutputException {
        String type = (String) template.getAttribute(OutputTemplate.ATTR_OUTPUT_TYPE);
        OutputFactory factory = OutputFactory.newInstance("groovy");
        Output output = factory.createOutput(type);
        generateOutput(output, this.getFile(template.getName()), template.getTemplate(), type, null);
        if ("trains".equals(type)) {
            // for each driver circulation
            for (TrainsCycle cycle : diagram.getCycles(TrainsCycleType.DRIVER_CYCLE)) {
                generateOutput(output, this.getFile(template.getName() + "_" + cycle.getName()), template.getTemplate(), type, cycle);
            }
        }
    }

    private void generateOutput(Output output, File outpuFile, TextTemplate textTemplate, String type, Object param) throws OutputException {
        OutputParams params = settings.createParams();
        params.setParam(DefaultOutputParam.TEXT_TEMPLATE, textTemplate);
        params.setParam(DefaultOutputParam.TRAIN_DIAGRAM, diagram);
        params.setParam(DefaultOutputParam.OUTPUT_FILE, outpuFile);
        // nothing - starts, ends, stations, train_unit_cycles, engine_cycles
        if ("trains".equals(type) && param != null) {
            params.setParam("driver_cycle", param);
        }
        output.write(params);
    }

    private File getFile(String name) {
        name = name.replaceAll("[\\\\:/\"?<>|]", "");
        return new File(outputDirectory, name + ".html");
    }

    private OutputTemplate copyTemplate(OutputTemplate template) {
        OutputTemplate copy = new OutputTemplate(template.getId(), null);
        try {
            copy.setTemplate(TextTemplate.createTextTemplate(template.getTemplate().getTemplate(),
                    template.getTemplate().getLanguage()));
        } catch (GrafikonException e) {
            LOG.error("Error creating copy of template.", e);
        }
        copy.setName(template.getName());
        copy.setAttributes(new Attributes(template.getAttributes()));
        return copy;
    }

    private void mergeTemplate(OutputTemplate template, OutputTemplate fromTemplate) {
        if (!template.getName().equals(fromTemplate.getName())) {
            template.setName(fromTemplate.getName());
        }
        if (!template.getTemplate().equals(fromTemplate.getTemplate())) {
            template.setTemplate(fromTemplate.getTemplate());
        }
        template.getAttributes().merge(fromTemplate.getAttributes());
    }

    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton downButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton locationButton;
    private javax.swing.JTextField locationTextField;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton newButton;
    private javax.swing.JButton okButton;
    private javax.swing.JButton outputAllButton;
    private javax.swing.JButton outputButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JList templateList;
    private javax.swing.JButton upButton;
}
