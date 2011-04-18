/*
 * TextTemplateListDialog.java
 *
 * Created on 14.4.2011, 18:17:18
 */
package net.parostroj.timetable.gui.dialogs;

import java.io.File;
import javax.swing.JFileChooser;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.IdGenerator;
import org.slf4j.LoggerFactory;

/**
 * Dialog for editing list of output templates.
 *
 * @author jub
 */
public class OutputTemplateListDialog extends javax.swing.JDialog {

    private TrainDiagram diagram;
    private WrapperListModel<OutputTemplate> templatesModel;
    private File templateLocation;
    private JFileChooser chooser;

    /** Creates new form TextTemplateListDialog */
    public OutputTemplateListDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        templatesModel = new WrapperListModel<OutputTemplate>(false);
        templateList.setModel(templatesModel);
    }

    public void showDialog(TrainDiagram diagram, JFileChooser chooser) {
        this.diagram = diagram;
        this.chooser = chooser;
        this.templateLocation = chooser.getSelectedFile() == null ? chooser.getCurrentDirectory() : chooser.getSelectedFile();
        this.locationTextField.setText(this.templateLocation.getPath());
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
    }// </editor-fold>//GEN-END:initComponents

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
        int index = templateList.getSelectedIndex();
        if (index != -1 && index != (templatesModel.getSize() - 1)) {
            Wrapper<OutputTemplate> wrapper = templatesModel.removeIndex(index);
            diagram.moveOutputTemplate(index, index + 1);
            index++;
            templatesModel.addWrapper(wrapper, index);
            templateList.setSelectedIndex(index);
        }
    }//GEN-LAST:event_downButtonActionPerformed

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        int index = templateList.getSelectedIndex();
        if (index != -1 && index != 0) {
            Wrapper<OutputTemplate> wrapper = templatesModel.removeIndex(index);
            diagram.moveOutputTemplate(index, index - 1);
            index--;
            templatesModel.addWrapper(wrapper, index);
            templateList.setSelectedIndex(index);
        }
    }//GEN-LAST:event_upButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        Wrapper<?> wrapper = (Wrapper<?>) templateList.getSelectedValue();
        if (wrapper != null) {
            templatesModel.removeObject((OutputTemplate) wrapper.getElement());
            diagram.removeOutputTemplate((OutputTemplate) wrapper.getElement());
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        OutputTemplate template = new OutputTemplate(IdGenerator.getInstance().getId(), diagram);
        template.setName(nameTextField.getText().trim());
        try {
            template.setTemplate(TextTemplate.createTextTemplate("", TextTemplate.Language.GROOVY));
        } catch (GrafikonException e) {
            LoggerFactory.getLogger(this.getClass()).error("Error creating template.", e);
        }
        diagram.addOutputTemplate(template);
        Wrapper<OutputTemplate> wrapper = new Wrapper<OutputTemplate>(template);
        templatesModel.addWrapper(wrapper);
        nameTextField.setText("");
        templateList.setSelectedValue(wrapper, true);
        this.updateButtons();
    }//GEN-LAST:event_newButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        OutputTemplateDialog dialog = new OutputTemplateDialog(this, true);
        dialog.setLocationRelativeTo(this);
        // get template
        OutputTemplate template = (OutputTemplate) ((Wrapper<?>) templateList.getSelectedValue()).getElement();
        dialog.showDialog(this.copyTemplate(template));
        if (dialog.getTemplate() != null) {
            this.mergeTemplate(template, dialog.getTemplate());
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void nameTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_nameTextFieldCaretUpdate
        this.updateButtons();
    }//GEN-LAST:event_nameTextFieldCaretUpdate

    private void templateListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_templateListValueChanged
        this.updateButtons();
    }//GEN-LAST:event_templateListValueChanged

    private void locationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locationButtonActionPerformed
        int returnValue = chooser.showOpenDialog(getParent());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            this.templateLocation = chooser.getSelectedFile();
            // update text string
            locationTextField.setText(this.templateLocation.getPath());
        }
    }//GEN-LAST:event_locationButtonActionPerformed

    private void outputButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_outputButtonActionPerformed

    private void outputAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputAllButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_outputAllButtonActionPerformed

    private OutputTemplate copyTemplate(OutputTemplate template) {
        OutputTemplate copy = new OutputTemplate(template.getId(), null);
        try {
            copy.setTemplate(TextTemplate.createTextTemplate(template.getTemplate().getTemplate(),
                    template.getTemplate().getLanguage()));
        } catch (GrafikonException e) {
            LoggerFactory.getLogger(this.getClass()).error("Error creating copy of template.", e);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    // End of variables declaration//GEN-END:variables
}
