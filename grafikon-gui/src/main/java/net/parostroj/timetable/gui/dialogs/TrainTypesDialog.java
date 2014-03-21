/*
 * TrainTypesDialog.java
 *
 * Created on 12. duben 2008, 23:32
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.gui.components.ChangeDocumentListener;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.gui.wrappers.WrapperListModel.ObjectListener;
import net.parostroj.timetable.utils.Conversions;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ResourceLoader;

import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentListener;

/**
 * Dialog for editation of the train types of the train diagram.
 *
 * @author jub
 */
public class TrainTypesDialog extends javax.swing.JDialog {

    private static final Logger LOG = LoggerFactory.getLogger(TrainTypesDialog.class);

    private static final TrainTypeCategory NONE_CATEGORY = new TrainTypeCategory(null, "-", "-");

    private ApplicationModel model;
    private WrapperListModel<TrainType> typesModel;

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
        typesModel = new WrapperListModel<TrainType>(Wrapper.getWrapperList(model.getDiagram().getTrainTypes()), null, false);
        typesModel.setObjectListener(new ObjectListener<TrainType>() {
            @Override
            public void added(TrainType object, int index) {
                model.getDiagram().addTrainType(object, index);
            }

            @Override
            public void removed(TrainType object) {
                model.getDiagram().removeTrainType(object);
            }

            @Override
            public void moved(TrainType object, int fromIndex, int toIndex) {
                model.getDiagram().moveTrainType(fromIndex, toIndex);
            }
        });
        trainTypesList.setModel(typesModel);
        brakeComboBox.removeAllItems();
        if (model.getDiagram() != null) {
            for (TrainTypeCategory cat : model.getDiagram().getPenaltyTable().getTrainTypeCategories()) {
                brakeComboBox.addItem(new Wrapper<TrainTypeCategory>(cat));
            }
            brakeComboBox.addItem(new Wrapper<TrainTypeCategory>(NONE_CATEGORY));
            brakeComboBox.setMaximumRowCount(brakeComboBox.getItemCount());
        }
        this.updateValuesForTrainType(null);
    }

    public void setComponentsEnabled(boolean enabled) {
        upButton.setEnabled(enabled);
        downButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        String abbr = abbrTextField.getText().trim();
        String desc = descTextField.getText().trim();
        boolean enabledValues = !(abbr.equals("") || desc.equals(""));
        newButton.setEnabled(enabledValues);
        updateButton.setEnabled(enabledValues && enabled);
    }

    private void initComponents() {
        abbrTextField = new javax.swing.JTextField();
        brakeComboBox = new javax.swing.JComboBox();
        editColorButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 0);
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
        newButton = GuiComponentUtils.createButton(GuiIcon.ADD, 0);
        updateButton = new javax.swing.JButton();
        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 0);
        upButton = GuiComponentUtils.createButton(GuiIcon.GO_UP, 0);
        downButton = GuiComponentUtils.createButton(GuiIcon.GO_DOWN, 0);

        setTitle(ResourceLoader.getString("edit.traintypes")); // NOI18N

        brakeComboBox.setMaximumRowCount(2);

        editColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editColorButtonActionPerformed(evt);
            }
        });

        nameTemplateCheckBox.setText(ResourceLoader.getString("edit.traintypes.nametemplate")); // NOI18N
        nameTemplateCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameTemplateEditBox.setEnabled(nameTemplateCheckBox.isSelected());
            }
        });

        completeNameTemplateCheckBox.setText(ResourceLoader.getString("edit.traintypes.completenametemplate")); // NOI18N
        completeNameTemplateCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cNameTemplateEditBox.setEnabled(completeNameTemplateCheckBox.isSelected());
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

        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        newButton.setEnabled(false);

        updateButton.setText(ResourceLoader.getString("button.update")); // NOI18N
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        platformNeededCheckBox = new javax.swing.JCheckBox(ResourceLoader.getString("edit.traintypes.platform.needed"));

        DocumentListener listener = new ChangeDocumentListener() {
            @Override
            protected void change() {
                setComponentsEnabled(trainTypesList.getSelectedIndex() != -1);
            }
        };
        descTextField.getDocument().addDocumentListener(listener);
        abbrTextField.getDocument().addDocumentListener(listener);

        showWeightInfoCheckBox = new javax.swing.JCheckBox(ResourceLoader.getString("edit.traintypes.show.weight.info"));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(platformNeededCheckBox)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(showWeightInfoCheckBox))
                        .addComponent(cNameTemplateEditBox, GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                        .addComponent(completeNameTemplateCheckBox)
                        .addComponent(nameTemplateCheckBox)
                        .addComponent(descTextField, GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jScrollPane, GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                .addComponent(updateButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(newButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(upButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(downButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(deleteButton, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                                .addComponent(abbrTextField, GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                                .addComponent(brakeComboBox, 0, 128, Short.MAX_VALUE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(colorLabel)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(editColorButton))
                                .addComponent(jLabel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jLabel2)
                        .addComponent(nameTemplateEditBox, GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(abbrTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(brakeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(colorLabel)
                        .addComponent(editColorButton))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(jLabel2)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(descTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(platformNeededCheckBox)
                        .addComponent(showWeightInfoCheckBox))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(nameTemplateCheckBox)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(nameTemplateEditBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(completeNameTemplateCheckBox)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(cNameTemplateEditBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(jScrollPane, GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(newButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(updateButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(deleteButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(upButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(downButton)))
                    .addContainerGap())
        );
        getContentPane().setLayout(layout);

        pack();
        setMinimumSize(getSize());
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
            int index = trainTypesList.getSelectedIndex();
            this.setComponentsEnabled(index != -1);
            if (index != -1) {
                TrainType selected = typesModel.getIndex(index).getElement();
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
            TrainTypeCategory category = selected.getCategory();
            brakeComboBox.setSelectedItem(new Wrapper<TrainTypeCategory>(category != null ? category : NONE_CATEGORY));

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
            platformNeededCheckBox.setSelected(selected.isPlatform());
            showWeightInfoCheckBox.setSelected(selected.getAttributes().getBool(TrainType.ATTR_SHOW_WEIGHT_INFO));
        } else {
            abbrTextField.setText("");
            descTextField.setText("");
            colorLabel.setText("0x000000");
            colorLabel.setForeground(Color.BLACK);
            brakeComboBox.setSelectedItem(NONE_CATEGORY);
            nameTemplateCheckBox.setSelected(false);
            nameTemplateEditBox.setTemplate(model.getDiagram().getTrainsData().getTrainNameTemplate());
            nameTemplateEditBox.setEnabled(false);
            completeNameTemplateCheckBox.setSelected(false);
            cNameTemplateEditBox.setTemplate(model.getDiagram().getTrainsData().getTrainCompleteNameTemplate());
            cNameTemplateEditBox.setEnabled(false);
            platformNeededCheckBox.setSelected(false);
            showWeightInfoCheckBox.setSelected(false);
        }
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // remove from types ...
        TrainType type = typesModel.getIndex(trainTypesList.getSelectedIndex()).getElement();
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

            typesModel.removeIndex(trainTypesList.getSelectedIndex());
            this.updateValuesForTrainType(null);
        }
    }

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // update values of a type
        TrainType type = typesModel.getIndex(trainTypesList.getSelectedIndex()).getElement();
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
            if (platformNeededCheckBox.isSelected() != type.isPlatform()) {
                type.setPlatform(platformNeededCheckBox.isSelected());
            }
            type.getAttributes().setBool(TrainType.ATTR_SHOW_WEIGHT_INFO, showWeightInfoCheckBox.isSelected());
            Color c = Conversions.convertTextToColor(colorLabel.getText());
            if (!c.equals(type.getColor()))
                type.setColor(c);
            TrainTypeCategory category = (TrainTypeCategory) ((Wrapper<?>) brakeComboBox.getSelectedItem()).getElement();
            type.setCategory(category != NONE_CATEGORY ? category : null);
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
            typesModel.updateIndex(trainTypesList.getSelectedIndex());
        }
    }

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int index = trainTypesList.getSelectedIndex();
        if (index > 0) {
            typesModel.moveIndexUp(index);
            trainTypesList.setSelectedIndex(index - 1);
            trainTypesList.ensureIndexIsVisible(index - 1);
        }
    }

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int index = trainTypesList.getSelectedIndex();
        if (index != -1 && index < typesModel.getSize() - 1) {
            typesModel.moveIndexDown(index);
            trainTypesList.setSelectedIndex(index + 1);
            trainTypesList.ensureIndexIsVisible(index + 1);
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
        type.setPlatform(platformNeededCheckBox.isSelected());
        type.setColor(Conversions.convertTextToColor(colorLabel.getText()));
        TrainTypeCategory category = (TrainTypeCategory) ((Wrapper<?>) brakeComboBox.getSelectedItem()).getElement();
        type.setCategory(category != NONE_CATEGORY ? category : null);
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
        type.getAttributes().setBool(TrainType.ATTR_SHOW_WEIGHT_INFO, showWeightInfoCheckBox.isSelected());
        int index = typesModel.getSize();
        typesModel.addWrapper(Wrapper.getWrapper(type), index);
        trainTypesList.setSelectedIndex(index);
        trainTypesList.ensureIndexIsVisible(index);
    }

    private boolean existsTrainWithType(TrainType type, List<Train> trains) {
        for (Train t : trains) {
            if (type.equals(t.getType()))
                return true;
        }
        return false;
    }

    private void showErrorDialog(String key) {
        JOptionPane.showMessageDialog(this, ResourceLoader.getString(key), ResourceLoader.getString("dialog.error.title"), JOptionPane.ERROR_MESSAGE);
    }

    private javax.swing.JTextField abbrTextField;
    private javax.swing.JComboBox brakeComboBox;
    private net.parostroj.timetable.gui.components.TextTemplateEditBox cNameTemplateEditBox;
    private javax.swing.JLabel colorLabel;
    private javax.swing.JCheckBox completeNameTemplateCheckBox;
    private javax.swing.JCheckBox platformNeededCheckBox;
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
    private javax.swing.JCheckBox showWeightInfoCheckBox;
}
