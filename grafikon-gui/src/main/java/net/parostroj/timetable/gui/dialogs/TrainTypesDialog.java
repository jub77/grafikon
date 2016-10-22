/*
 * TrainTypesDialog.java
 *
 * Created on 12. duben 2008, 23:32
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.components.LocalizedStringField;
import net.parostroj.timetable.gui.pm.LocalizedStringDefaultPM;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperDelegate;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.gui.wrappers.WrapperListModel.ObjectListener;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.LineType;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainDiagramPartFactory;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainTypeCategory;
import net.parostroj.timetable.utils.Conversions;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Dialog for editation of the train types of the train diagram.
 *
 * @author jub
 */
public class TrainTypesDialog extends javax.swing.JDialog {

    private static final Logger log = LoggerFactory.getLogger(TrainTypesDialog.class);

    private static final TrainTypeCategory NONE_CATEGORY;

    static {
        NONE_CATEGORY = new TrainTypeCategory(null);
        NONE_CATEGORY.setKey("-");
        NONE_CATEGORY.setName(LocalizedString.fromString("-"));
    }

    private TrainDiagram diagram;
    private WrapperListModel<TrainType> typesModel;
    private final WrapperDelegate<LineType> lineTypeWrapperDelegate;

    /** Creates new form TrainTypesDialog */
    public TrainTypesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setComponentsEnabled(false);
        nameTemplateEditBox.setLanguages(Arrays.asList(TextTemplate.Language.values()));
        cNameTemplateEditBox.setLanguages(Arrays.asList(TextTemplate.Language.values()));
        this.lineTypeWrapperDelegate = new LineTypeWrapperDelegate();
        for (LineType type : LineType.values()) {
            lineTypeComboBox.addItem(Wrapper.getWrapper(type, lineTypeWrapperDelegate));
        }

        pack();
        setMinimumSize(getSize());
    }

    public void showDialog(TrainDiagram diagram) {
        this.diagram = diagram;
        this.updateValues();
        this.setVisible(true);
    }

    public void updateValues() {
        // fill train type jlist
        typesModel = new WrapperListModel<>(Wrapper.getWrapperList(diagram.getTrainTypes()), null, false);
        typesModel.setObjectListener(new ObjectListener<TrainType>() {
            @Override
            public void added(TrainType object, int index) {
                diagram.getTrainTypes().add(index, object);
            }

            @Override
            public void removed(TrainType object) {
                diagram.getTrainTypes().remove(object);
            }

            @Override
            public void moved(TrainType object, int fromIndex, int toIndex) {
                diagram.getTrainTypes().move(fromIndex, toIndex);
            }
        });
        trainTypesList.setModel(typesModel);
        brakeComboBox.removeAllItems();
        if (diagram != null) {
            for (TrainTypeCategory cat : diagram.getTrainTypeCategories()) {
                brakeComboBox.addItem(new Wrapper<>(cat));
            }
            brakeComboBox.addItem(new Wrapper<>(NONE_CATEGORY));
            brakeComboBox.setMaximumRowCount(brakeComboBox.getItemCount());
        }
        this.updateValuesForTrainType(null);
    }

    private void setComponentsEnabled(boolean enabled) {
        upButton.setEnabled(enabled);
        downButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        LocalizedStringDefaultPM abbrModel = abbrTextField.getPresentationModel();
        LocalizedStringDefaultPM descModel = descTextField.getPresentationModel();
        String abbr = abbrModel != null ? abbrModel.getDefaultString().trim() : "";
        String desc = descModel != null ? descModel.getDefaultString().trim() : "";
        boolean enabledValues = !(abbr.equals("") || desc.equals(""));
        newButton.setEnabled(enabledValues);
        updateButton.setEnabled(enabledValues && enabled);
    }

    private void initComponents() {
        PropertyChangeListener propListener = evt -> {
            setComponentsEnabled(trainTypesList.getSelectedIndex() != -1);
        };
        abbrTextField = new LocalizedStringField();
        LocalizedStringDefaultPM abbrPm = new LocalizedStringDefaultPM();
        abbrPm.init(LocalizedString.fromString(""));
        abbrTextField.setPresentationModel(abbrPm);
        abbrPm.getDefault().addPropertyChangeListener("text", propListener);
        brakeComboBox = new javax.swing.JComboBox<>();
        editColorButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 2);
        descTextField = new LocalizedStringField();
        LocalizedStringDefaultPM descPm = new LocalizedStringDefaultPM();
        descPm.init(LocalizedString.fromString(""));
        descTextField.setPresentationModel(descPm);
        descPm.getDefault().addPropertyChangeListener("text", propListener);
        nameTemplateCheckBox = new javax.swing.JCheckBox();
        nameTemplateEditBox = new net.parostroj.timetable.gui.components.TextTemplateEditBox();
        completeNameTemplateCheckBox = new javax.swing.JCheckBox();
        cNameTemplateEditBox = new net.parostroj.timetable.gui.components.TextTemplateEditBox();
        javax.swing.JScrollPane jScrollPane = new javax.swing.JScrollPane();
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        trainTypesList = new javax.swing.JList<>();
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
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editColorButtonActionPerformed(evt);
            }
        });

        nameTemplateCheckBox.setText(ResourceLoader.getString("edit.traintypes.nametemplate")); // NOI18N
        nameTemplateCheckBox.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameTemplateEditBox.setEnabled(nameTemplateCheckBox.isSelected());
            }
        });

        completeNameTemplateCheckBox.setText(ResourceLoader.getString("edit.traintypes.completenametemplate")); // NOI18N
        completeNameTemplateCheckBox.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cNameTemplateEditBox.setEnabled(completeNameTemplateCheckBox.isSelected());
            }
        });

        trainTypesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        trainTypesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
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
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        newButton.setEnabled(false);

        updateButton.setText(ResourceLoader.getString("button.update")); // NOI18N
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        upButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        downButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        platformNeededCheckBox = new javax.swing.JCheckBox(ResourceLoader.getString("edit.traintypes.platform.needed"));

        showWeightInfoCheckBox = new javax.swing.JCheckBox(ResourceLoader.getString("edit.traintypes.show.weight.info"));

        lineTypeComboBox = new javax.swing.JComboBox<>();

        lineWidthTextField = new javax.swing.JTextField();
        lineWidthTextField.setColumns(4);
        lineWidthTextField.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.JLabel lineTypeLabel = new javax.swing.JLabel(ResourceLoader.getString("edit.traintypes.line.type") + ":"); // NOI18N

        javax.swing.JLabel lineWidthLabel = new javax.swing.JLabel(ResourceLoader.getString("edit.traintypes.line.width") + ":"); // NOI18N

        javax.swing.JLabel percentWidthLabel = new javax.swing.JLabel("%");

        javax.swing.JLabel lineLengthLabel = new javax.swing.JLabel(ResourceLoader.getString("edit.traintypes.line.length") + ":"); // NOI18N

        lineLengthTextField = new javax.swing.JTextField();
        lineLengthTextField.setColumns(4);
        lineLengthTextField.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.JLabel percentLengthLabel = new javax.swing.JLabel("%");

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
                        .addComponent(cNameTemplateEditBox, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(completeNameTemplateCheckBox)
                        .addComponent(nameTemplateCheckBox)
                        .addComponent(descTextField, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jScrollPane)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                .addComponent(updateButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(newButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(upButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(downButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(deleteButton, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                .addComponent(abbrTextField, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 124, Short.MAX_VALUE)
                                .addComponent(brakeComboBox, GroupLayout.PREFERRED_SIZE, 124, Short.MAX_VALUE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(colorLabel)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(editColorButton))
                                .addComponent(jLabel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jLabel2)
                        .addComponent(nameTemplateEditBox, GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lineTypeLabel)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lineTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(lineWidthLabel)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lineWidthTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(percentWidthLabel)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(lineLengthLabel)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lineLengthTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(percentLengthLabel)))
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
                    .addGroup(layout.createParallelGroup(Alignment.CENTER)
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
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lineTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lineWidthTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lineTypeLabel)
                        .addComponent(lineWidthLabel)
                        .addComponent(percentWidthLabel)
                        .addComponent(lineLengthLabel)
                        .addComponent(lineLengthTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(percentLengthLabel))
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
                        .addComponent(jScrollPane)
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
            abbrTextField.getPresentationModel().init(selected.getLocalizedAbbr(), diagram.getLocales());
            descTextField.getPresentationModel().init(selected.getDesc(), diagram.getLocales());
            colorLabel.setText(Conversions.convertColorToText(selected.getColor()));
            colorLabel.setForeground(selected.getColor());
            TrainTypeCategory category = selected.getCategory();
            brakeComboBox.setSelectedItem(new Wrapper<>(category != null ? category : NONE_CATEGORY));

            nameTemplateCheckBox.setSelected(selected.getTrainNameTemplate() != null);
            nameTemplateEditBox.setEnabled(selected.getTrainNameTemplate() != null);
            nameTemplateEditBox.setTemplate(selected.getTrainNameTemplate() == null ?
                selected.getDiagram().getTrainsData().getTrainNameTemplate() :
                selected.getTrainNameTemplate());

            completeNameTemplateCheckBox.setSelected(selected.getTrainCompleteNameTemplate() != null);
            cNameTemplateEditBox.setEnabled(selected.getTrainCompleteNameTemplate() != null);
            cNameTemplateEditBox.setTemplate(selected.getTrainCompleteNameTemplate() == null ?
                selected.getDiagram().getTrainsData().getTrainCompleteNameTemplate() :
                selected.getTrainCompleteNameTemplate());
            platformNeededCheckBox.setSelected(selected.isPlatform());
            showWeightInfoCheckBox.setSelected(selected.getAttributes().getBool(TrainType.ATTR_SHOW_WEIGHT_INFO));
            // line information
            Integer lineTypeInt = selected.getAttributes().get(TrainType.ATTR_LINE_TYPE, Integer.class);
            LineType lineType = LineType.valueOf(lineTypeInt);
            lineTypeComboBox.setSelectedIndex(lineType.ordinal());
            lineWidthTextField.setText(Integer.toString(this.convertDoubleValueToPercent(selected, TrainType.ATTR_LINE_WIDTH)));
            lineLengthTextField.setText(Integer.toString(this.convertDoubleValueToPercent(selected, TrainType.ATTR_LINE_LENGTH)));
        } else {
            abbrTextField.getPresentationModel().init(LocalizedString.fromString(""), diagram.getLocales());
            descTextField.getPresentationModel().init(LocalizedString.fromString(""), diagram.getLocales());
            colorLabel.setText("0x000000");
            colorLabel.setForeground(Color.BLACK);
            brakeComboBox.setSelectedItem(NONE_CATEGORY);
            nameTemplateCheckBox.setSelected(false);
            nameTemplateEditBox.setTemplate(diagram.getTrainsData().getTrainNameTemplate());
            nameTemplateEditBox.setEnabled(false);
            completeNameTemplateCheckBox.setSelected(false);
            cNameTemplateEditBox.setTemplate(diagram.getTrainsData().getTrainCompleteNameTemplate());
            cNameTemplateEditBox.setEnabled(false);
            platformNeededCheckBox.setSelected(false);
            showWeightInfoCheckBox.setSelected(false);
            lineTypeComboBox.setSelectedIndex(0);
            lineWidthTextField.setText("100");
            lineLengthTextField.setText("100");
        }
    }

    private int convertDoubleValueToPercent(TrainType selected, String attribute) {
        Double ratio = selected.getAttributes().get(attribute, Double.class);
        int percentage = ratio != null ? (int) (100 * ratio) : 100;
        return percentage;
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // remove from types ...
        TrainType type = typesModel.getIndex(trainTypesList.getSelectedIndex()).getElement();
        if (type != null) {
            // check if there is no train with this type ...
            if (this.existsTrainWithType(type, diagram.getTrains())) {
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
            LocalizedString abbr = abbrTextField.getPresentationModel().getCurrentEdit().get();
            LocalizedString desc = descTextField.getPresentationModel().getCurrentEdit().get();
            // check values ....
            if (abbr == null || desc == null) {
                this.showErrorDialog("dialog.error.missingvalues");
                return;
            }
            type.setLocalizedAbbr(abbr);
            type.setDesc(desc);
            type.setPlatform(platformNeededCheckBox.isSelected());
            type.getAttributes().setBool(TrainType.ATTR_SHOW_WEIGHT_INFO, showWeightInfoCheckBox.isSelected());
            Color c = Conversions.convertTextToColor(colorLabel.getText());
            type.setColor(c);
            TrainTypeCategory category = (TrainTypeCategory) ((Wrapper<?>) brakeComboBox.getSelectedItem()).getElement();
            type.setCategory(category != NONE_CATEGORY ? category : null);
            if (nameTemplateCheckBox.isSelected()) {
                try {
                    type.setTrainNameTemplate(nameTemplateEditBox.getTemplate());
                } catch (GrafikonException e) {
                    GuiComponentUtils.showWarning(e.getMessage(), this);
                    log.warn(e.getMessage(), e);
                }
            } else {
                type.setTrainNameTemplate(null);
            }
            if (completeNameTemplateCheckBox.isSelected()) {
                try {
                    type.setTrainCompleteNameTemplate(cNameTemplateEditBox.getTemplate());
                } catch (GrafikonException e) {
                    GuiComponentUtils.showWarning(e.getMessage(), this);
                    log.warn(e.getMessage(), e);
                }
            } else {
                type.setTrainCompleteNameTemplate(null);
            }
            type.getAttributes().setRemove(TrainType.ATTR_LINE_TYPE, extractLineType());
            type.getAttributes().setRemove(TrainType.ATTR_LINE_WIDTH, extractRatioFromPercentage(lineWidthTextField));
            type.getAttributes().setRemove(TrainType.ATTR_LINE_LENGTH, extractRatioFromPercentage(lineLengthTextField));
            typesModel.refreshIndex(trainTypesList.getSelectedIndex());
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
        LocalizedString abbr = abbrTextField.getPresentationModel().getCurrentEdit().get();
        LocalizedString desc = descTextField.getPresentationModel().getCurrentEdit().get();
        // check values ....
        if (abbr == null || desc == null) {
            this.showErrorDialog("dialog.error.missingvalues");
            return;
        }
        TrainDiagramPartFactory factory = diagram.getPartFactory();
        TrainType type = factory.createTrainType(factory.createId());
        type.setLocalizedAbbr(abbr);
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
            GuiComponentUtils.showError(e.getMessage(), this);
            log.warn(e.getMessage(), e);
            return;
        }
        type.getAttributes().setBool(TrainType.ATTR_SHOW_WEIGHT_INFO, showWeightInfoCheckBox.isSelected());
        type.getAttributes().setRemove(TrainType.ATTR_LINE_TYPE, extractLineType());
        type.getAttributes().setRemove(TrainType.ATTR_LINE_WIDTH, extractRatioFromPercentage(lineWidthTextField));
        type.getAttributes().setRemove(TrainType.ATTR_LINE_LENGTH, extractRatioFromPercentage(lineLengthTextField));

        int index = typesModel.getSize();
        typesModel.addWrapper(Wrapper.getWrapper(type), index);
        trainTypesList.setSelectedIndex(index);
        trainTypesList.ensureIndexIsVisible(index);
    }

    private Integer extractLineType() {
        Wrapper<?> selectedType = (Wrapper<?>) lineTypeComboBox.getSelectedItem();
        LineType type = (LineType) selectedType.getElement();
        // solid is default value -> null
        return type == LineType.SOLID ? null : type.getValue();
    }

    private Double extractRatioFromPercentage(JTextField field) {
        String widthText = field.getText();
        Double width = null;
        try {
            int percentWidth = Integer.parseInt(widthText);
            // limit 10 - 1000%
            if (percentWidth < 10) {
                percentWidth = 10;
            } else if (percentWidth > 1000) {
                percentWidth = 1000;
            }
            // 100 is default value -> null
            if (percentWidth != 100) {
                width = percentWidth / 100d;
            }
        } catch (NumberFormatException e) {
            log.warn("Cannot parse value {} to int", widthText);
        }
        return width;
    }

    private boolean existsTrainWithType(TrainType type, Iterable<Train> trains) {
        for (Train t : trains) {
            if (type.equals(t.getType()))
                return true;
        }
        return false;
    }

    private void showErrorDialog(String key) {
        JOptionPane.showMessageDialog(this, ResourceLoader.getString(key), ResourceLoader.getString("dialog.error.title"), JOptionPane.ERROR_MESSAGE);
    }

    private LocalizedStringField abbrTextField;
    private javax.swing.JComboBox<Wrapper<TrainTypeCategory>> brakeComboBox;
    private net.parostroj.timetable.gui.components.TextTemplateEditBox cNameTemplateEditBox;
    private javax.swing.JLabel colorLabel;
    private javax.swing.JCheckBox completeNameTemplateCheckBox;
    private javax.swing.JCheckBox platformNeededCheckBox;
    private javax.swing.JButton deleteButton;
    private LocalizedStringField descTextField;
    private javax.swing.JButton downButton;
    private javax.swing.JButton editColorButton;
    private javax.swing.JCheckBox nameTemplateCheckBox;
    private net.parostroj.timetable.gui.components.TextTemplateEditBox nameTemplateEditBox;
    private javax.swing.JButton newButton;
    private javax.swing.JList<Wrapper<TrainType>> trainTypesList;
    private javax.swing.JButton upButton;
    private javax.swing.JButton updateButton;
    private javax.swing.JCheckBox showWeightInfoCheckBox;
    private javax.swing.JTextField lineWidthTextField;
    private javax.swing.JComboBox<Wrapper<LineType>> lineTypeComboBox;
    private javax.swing.JTextField lineLengthTextField;

    private static class LineTypeWrapperDelegate implements WrapperDelegate<LineType> {
        @Override
        public String toString(LineType element) {
            String key = element.getKey();
            return ResourceLoader.getString("edit.traintypes.line.type." + key);
        }

        @Override
        public int compare(LineType o1, LineType o2) {
            return 0;
        }
    }
}
