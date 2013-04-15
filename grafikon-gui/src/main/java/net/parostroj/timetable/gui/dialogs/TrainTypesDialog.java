/*
 * TrainTypesDialog.java
 *
 * Created on 12. duben 2008, 23:32
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.utils.Conversions;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Dialog for editation of the train types of the train diagram.
 *
 * @author jub
 */
public class TrainTypesDialog extends javax.swing.JDialog {

    private static final Logger LOG = LoggerFactory.getLogger(TrainTypesDialog.class);

    private ApplicationModel model;
    private TrainTypesModel typesModel;

    /** Creates new form TrainTypesDialog */
    public TrainTypesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setComponentsEnabled(false);
        nameTemplateEditBox.setLanguages(Arrays.asList(TextTemplate.Language.values()));
        cNameTemplateEditBox.setLanguages(Arrays.asList(TextTemplate.Language.values()));
    }

    public void setModel(ApplicationModel model) {
        this.model = model;
    }

    public void updateValues() {
        // fill train type jlist
        typesModel = new TrainTypesModel(model.getDiagram());
        trainTypesList.setModel(typesModel);
        brakeComboBox.removeAllItems();
        if (model.getDiagram() != null) {
            for (TrainTypeCategory cat : model.getDiagram().getPenaltyTable().getTrainTypeCategories()) {
                brakeComboBox.addItem(new Wrapper<TrainTypeCategory>(cat));
            }
        }
        brakeComboBox.setMaximumRowCount(Math.min(10, brakeComboBox.getItemCount()));
        this.updateValuesForTrainType(null);
    }

    public void setComponentsEnabled(boolean enabled) {
        upButton.setEnabled(enabled);
        downButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        updateButton.setEnabled(enabled);
    }

    private void initComponents() {
        abbrTextField = new javax.swing.JTextField();
        brakeComboBox = new javax.swing.JComboBox();
        editColorButton = new javax.swing.JButton();
        descTextField = new javax.swing.JTextField();
        nameTemplateCheckBox = new javax.swing.JCheckBox();
        nameTemplateEditBox = new net.parostroj.timetable.gui.components.TextTemplateEditBox();
        completeNameTemplateCheckBox = new javax.swing.JCheckBox();
        cNameTemplateEditBox = new net.parostroj.timetable.gui.components.TextTemplateEditBox();
        javax.swing.JScrollPane jScrollPane = new javax.swing.JScrollPane();
        trainTypesList = new javax.swing.JList();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        colorLabel = new javax.swing.JLabel();
        newButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();

        setTitle(ResourceLoader.getString("edit.traintypes")); // NOI18N

        brakeComboBox.setMaximumRowCount(2);

        editColorButton.setText(ResourceLoader.getString("button.edit") + "..."); // NOI18N
        editColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editColorButtonActionPerformed(evt);
            }
        });

        nameTemplateCheckBox.setText(ResourceLoader.getString("edit.traintypes.nametemplate")); // NOI18N
        nameTemplateCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameTemplateCheckBoxActionPerformed(evt);
            }
        });

        completeNameTemplateCheckBox.setText(ResourceLoader.getString("edit.traintypes.completenametemplate")); // NOI18N
        completeNameTemplateCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                completeNameTemplateCheckBoxActionPerformed(evt);
            }
        });

        trainTypesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        trainTypesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                trainTypesListValueChanged(evt);
            }
        });
        jScrollPane.setViewportView(trainTypesList);

        jLabel1.setText(ResourceLoader.getString("edit.traintypes.abbr")); // NOI18N

        jLabel2.setText(ResourceLoader.getString("edit.traintypes.desc")); // NOI18N

        jLabel3.setText(ResourceLoader.getString("edit.traintypes.category")); // NOI18N

        jLabel4.setText(ResourceLoader.getString("edit.traintypes.color")); // NOI18N

        colorLabel.setText("0x000000");

        newButton.setText(ResourceLoader.getString("button.new")); // NOI18N
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

        updateButton.setText(ResourceLoader.getString("button.update")); // NOI18N
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        deleteButton.setText(ResourceLoader.getString("button.delete")); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        upButton.setText("^");
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        downButton.setText("v");
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cNameTemplateEditBox, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                    .addComponent(completeNameTemplateCheckBox)
                    .addComponent(nameTemplateCheckBox)
                    .addComponent(descTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(updateButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(newButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(upButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(downButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                            .addComponent(abbrTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                            .addComponent(brakeComboBox, 0, 108, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(colorLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(editColorButton))
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel2)
                    .addComponent(nameTemplateEditBox, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(abbrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(brakeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(colorLabel)
                    .addComponent(editColorButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(descTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameTemplateCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameTemplateEditBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(completeNameTemplateCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cNameTemplateEditBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(newButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(updateButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(upButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(downButton)))
                .addContainerGap())
        );

        pack();
    }

    private void editColorButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // open color chooser
        Color chosen = JColorChooser.showDialog(this, null, Conversions.convertTextToColor(colorLabel.getText()));
        if (chosen != null) {
            colorLabel.setText(Conversions.convertColorToText(chosen));
            colorLabel.setForeground(chosen);
        }
    }

    private void trainTypesListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            this.setComponentsEnabled(trainTypesList.getSelectedIndex() != -1);
            if (trainTypesList.getSelectedIndex() != -1) {
                TrainType selected = (TrainType)trainTypesList.getSelectedValue();
                this.updateValuesForTrainType(selected);
            }
        }
    }

    private void updateValuesForTrainType(TrainType selected) {
        if (selected != null) {
            abbrTextField.setText(selected.getAbbr());
            descTextField.setText(selected.getDesc());
            colorLabel.setText(Conversions.convertColorToText(selected.getColor()));
            colorLabel.setForeground(selected.getColor());
            brakeComboBox.setSelectedItem(new Wrapper<TrainTypeCategory>(selected.getCategory()));

            nameTemplateCheckBox.setSelected(selected.getTrainNameTemplate() != null);
            nameTemplateEditBox.setEnabled(selected.getTrainNameTemplate() != null);
            nameTemplateEditBox.setTemplate(selected.getTrainNameTemplate() == null ?
                selected.getTrainDiagram().getTrainsData().getTrainNameTemplate() :
                selected.getTrainNameTemplate());

            completeNameTemplateCheckBox.setSelected(selected.getTrainCompleteNameTemplate() != null);
            cNameTemplateEditBox.setEnabled(selected.getTrainCompleteNameTemplate() != null);
            cNameTemplateEditBox.setTemplate(selected.getTrainCompleteNameTemplate() == null ?
                selected.getTrainDiagram().getTrainsData().getTrainCompleteNameTemplate() :
                selected.getTrainCompleteNameTemplate());
        } else {
            abbrTextField.setText("");
            descTextField.setText("");
            colorLabel.setText("0x000000");
            colorLabel.setForeground(Color.BLACK);
            brakeComboBox.setSelectedItem(model.getDiagram().getPenaltyTable().getTrainTypeCategories().get(0));
            nameTemplateCheckBox.setSelected(false);
            nameTemplateEditBox.setTemplate(model.getDiagram().getTrainsData().getTrainNameTemplate());
            nameTemplateEditBox.setEnabled(false);
            completeNameTemplateCheckBox.setSelected(false);
            cNameTemplateEditBox.setTemplate(model.getDiagram().getTrainsData().getTrainCompleteNameTemplate());
            cNameTemplateEditBox.setEnabled(false);
        }
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // remove from types ...
        TrainType type = (TrainType)trainTypesList.getSelectedValue();
        if (type != null) {
            // check if there is no train with this type ...
            if (this.existsTrainWithType(type, model.getDiagram().getTrains())) {
                this.showErrorDialog("dialog.error.trainwithtraintype");
                return;
            }

            // check if there is at least one type ...
            if (typesModel.getSize() == 1) {
                showErrorDialog("dialog.error.onetraintype");
                return;
            }

            typesModel.remove(trainTypesList.getSelectedIndex());
            this.updateValuesForTrainType(null);
            // fire event
            this.fireEvent();
        }
    }

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // update values of a type
        TrainType type = (TrainType)trainTypesList.getSelectedValue();
        if (type != null) {
            String abbr = abbrTextField.getText().trim();
            String desc = descTextField.getText().trim();
            // check values ....
            if ("".equals(abbr) || "".equals(desc)) {
                this.showErrorDialog("dialog.error.missingvalues");
                return;
            }
            if (!abbr.equals(type.getAbbr()))
                type.setAbbr(abbr);
            if (!desc.equals(type.getDesc()))
                type.setDesc(desc);
            Color c = Conversions.convertTextToColor(colorLabel.getText());
            if (!c.equals(type.getColor()))
                type.setColor(c);
            TrainTypeCategory category = (TrainTypeCategory) ((Wrapper<?>)brakeComboBox.getSelectedItem()).getElement();
            if (!category.equals(type.getCategory()))
                type.setCategory(category);
            if (nameTemplateCheckBox.isSelected()) {
                try {
                    if (type.getTrainNameTemplate() == null ||
                            !nameTemplateEditBox.getTemplate().equals(type.getTrainNameTemplate()))
                        type.setTrainNameTemplate(nameTemplateEditBox.getTemplate());
                } catch (GrafikonException e) {
                    ActionUtils.showWarning(e.getMessage(), this);
                    LOG.warn(e.getMessage(), e);
                }
            } else {
                if (type.getTrainNameTemplate() != null)
                    type.setTrainNameTemplate(null);
            }
            if (completeNameTemplateCheckBox.isSelected()) {
                try {
                    if (type.getTrainCompleteNameTemplate() == null ||
                            !cNameTemplateEditBox.getTemplate().equals(type.getTrainCompleteNameTemplate()))
                        type.setTrainCompleteNameTemplate(cNameTemplateEditBox.getTemplate());
                } catch (GrafikonException e) {
                    ActionUtils.showWarning(e.getMessage(), this);
                    LOG.warn(e.getMessage(), e);
                }
            } else {
                if (type.getTrainCompleteNameTemplate() != null)
                    type.setTrainCompleteNameTemplate(null);
            }
            typesModel.updated(trainTypesList.getSelectedIndex());

            // clear data for templates
            for (Train train : model.getDiagram().getTrains())
                if (train.getType() == type)
                    train.clearCachedData();

            this.fireEvent();
        }
    }

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int index = trainTypesList.getSelectedIndex();
        if (index != -1) {
            int dest = typesModel.moveFromTo(index, index - 1);
            if (dest != index) {
                trainTypesList.setSelectedIndex(dest);
                trainTypesList.ensureIndexIsVisible(dest);
                this.fireEvent();
            }
        }
    }

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int index = trainTypesList.getSelectedIndex();
        if (index != -1) {
            int dest = typesModel.moveFromTo(index, index + 1);
            if (dest != index) {
                trainTypesList.setSelectedIndex(dest);
                trainTypesList.ensureIndexIsVisible(dest);
                this.fireEvent();
            }
        }
    }

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // test values
        String abbr = abbrTextField.getText().trim();
        String desc = descTextField.getText().trim();
        // check values ....
        if ("".equals(abbr) || "".equals(desc)) {
            this.showErrorDialog("dialog.error.missingvalues");
            return;
        }
        TrainType type = model.getDiagram().createTrainType(IdGenerator.getInstance().getId());
        type.setAbbr(abbr);
        type.setDesc(desc);
        type.setColor(Conversions.convertTextToColor(colorLabel.getText()));
        type.setCategory((TrainTypeCategory) ((Wrapper<?>)brakeComboBox.getSelectedItem()).getElement());
        try {
            if (nameTemplateCheckBox.isSelected()) {
                type.setTrainNameTemplate(nameTemplateEditBox.getTemplate());
            }
            if (completeNameTemplateCheckBox.isSelected()) {
                type.setTrainCompleteNameTemplate(cNameTemplateEditBox.getTemplate());
            }
        } catch (GrafikonException e) {
            ActionUtils.showError(e.getMessage(), this);
            LOG.warn(e.getMessage(), e);
            return;
        }
        int index = typesModel.add(type);
        trainTypesList.setSelectedIndex(index);
        trainTypesList.ensureIndexIsVisible(index);
        this.fireEvent();
    }

    private void nameTemplateCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
        nameTemplateEditBox.setEnabled(nameTemplateCheckBox.isSelected());
    }

    private void completeNameTemplateCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
        cNameTemplateEditBox.setEnabled(completeNameTemplateCheckBox.isSelected());
    }

    private boolean existsTrainWithType(TrainType type, List<Train> trains) {
        for (Train t : trains) {
            if (t.getType().equals(type))
                return true;
        }
        return false;
    }

    private void showErrorDialog(String key) {
        JOptionPane.showMessageDialog(this, ResourceLoader.getString(key), ResourceLoader.getString("dialog.error.title"), JOptionPane.ERROR_MESSAGE);
    }

    private void fireEvent() {
        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.TRAIN_TYPES_CHANGED, model));
    }

    private javax.swing.JTextField abbrTextField;
    private javax.swing.JComboBox brakeComboBox;
    private net.parostroj.timetable.gui.components.TextTemplateEditBox cNameTemplateEditBox;
    private javax.swing.JLabel colorLabel;
    private javax.swing.JCheckBox completeNameTemplateCheckBox;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField descTextField;
    private javax.swing.JButton downButton;
    private javax.swing.JButton editColorButton;
    private javax.swing.JCheckBox nameTemplateCheckBox;
    private net.parostroj.timetable.gui.components.TextTemplateEditBox nameTemplateEditBox;
    private javax.swing.JButton newButton;
    private javax.swing.JList trainTypesList;
    private javax.swing.JButton upButton;
    private javax.swing.JButton updateButton;
}

class TrainTypesModel extends AbstractListModel {

    private final TrainDiagram diagram;

    public TrainTypesModel(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    public void remove(int index) {
        TrainType type = diagram.getTrainTypes().get(index);
        diagram.removeTrainType(type);
        this.fireIntervalRemoved(this, index, index);
    }

    @Override
    public int getSize() {
        return diagram.getTrainTypes().size();
    }

    @Override
    public Object getElementAt(int index) {
        return diagram.getTrainTypes().get(index);
    }

    public void updated(int index) {
        this.fireContentsChanged(this, index, index);
    }

    public int add(TrainType type) {
        diagram.addTrainType(type);
        int index = diagram.getTrainTypes().size() - 1;
        this.fireIntervalAdded(this, index, index);
        return index;
    }

    public int moveFromTo(int index1, int index2) {
        // check limits
        if (index1 < 0 || index1 >= diagram.getTrainTypes().size() || index2 < 0 || index2 >= diagram.getTrainTypes().size())
            return index1;
        diagram.moveTrainType(index1, index2);
        // inform listeners
        this.fireContentsChanged(this, index1, index1);
        this.fireContentsChanged(this, index2, index2);
        return index2;
    }
}