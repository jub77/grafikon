package net.parostroj.timetable.gui.dialogs;

import java.awt.Color;
import java.util.Arrays;

import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JColorChooser;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.components.LocalizedStringField;
import net.parostroj.timetable.gui.pm.LocalizedStringPM;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperDelegate;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.LineType;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainTypeCategory;
import net.parostroj.timetable.utils.Conversions;
import net.parostroj.timetable.utils.ResourceLoader;
import javax.swing.JButton;

/**
 * Dialog for editing of the train type.
 *
 * @author jub
 */
public class TrainTypeDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(TrainTypeDialog.class);

    private static final Wrapper<TrainTypeCategory> NONE_CATEGORY = Wrapper.getEmptyWrapper("-");

    private transient TrainType trainType;

    public TrainTypeDialog(java.awt.Window parent, boolean modal) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        initComponents();
        nameTemplateEditBox.setLanguages(Arrays.asList(TextTemplate.Language.values()));
        cNameTemplateEditBox.setLanguages(Arrays.asList(TextTemplate.Language.values()));
        WrapperDelegate<LineType> lineTypeWrapperDelegate = new LineTypeWrapperDelegate();
        for (LineType type : LineType.values()) {
            lineTypeComboBox.addItem(Wrapper.getWrapper(type, lineTypeWrapperDelegate));
        }

        pack();
        setMinimumSize(getSize());
    }

    public void showDialog(TrainType trainType, TrainDiagram diagram) {
        this.trainType = trainType;
        this.updateValuesForTrainType(trainType, diagram);
        this.setVisible(true);
    }

    private void initComponents() {
        abbrTextField = new LocalizedStringField<>();
        LocalizedStringPM abbrPm = new LocalizedStringPM();
        abbrPm.init(LocalizedString.fromString(""));
        abbrTextField.setPresentationModel(abbrPm);
        brakeComboBox = new javax.swing.JComboBox<>();
        JButton editColorButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 2);
        descTextField = new LocalizedStringField<>();
        LocalizedStringPM descPm = new LocalizedStringPM();
        descPm.init(LocalizedString.fromString(""));
        descTextField.setPresentationModel(descPm);
        nameTemplateCheckBox = new javax.swing.JCheckBox();
        nameTemplateEditBox = new net.parostroj.timetable.gui.components.TextTemplateEditBox();
        completeNameTemplateCheckBox = new javax.swing.JCheckBox();
        cNameTemplateEditBox = new net.parostroj.timetable.gui.components.TextTemplateEditBox();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        colorLabel = new javax.swing.JLabel();

        setTitle(ResourceLoader.getString("edit.traintypes")); // NOI18N

        brakeComboBox.setMaximumRowCount(2);

        editColorButton.addActionListener(this::editColorButtonActionPerformed);

        nameTemplateCheckBox.setText(ResourceLoader.getString("edit.traintypes.nametemplate")); // NOI18N
        nameTemplateCheckBox.addActionListener(evt -> nameTemplateEditBox.setEnabled(nameTemplateCheckBox.isSelected()));

        completeNameTemplateCheckBox.setText(ResourceLoader.getString("edit.traintypes.completenametemplate")); // NOI18N
        completeNameTemplateCheckBox.addActionListener(evt -> cNameTemplateEditBox.setEnabled(completeNameTemplateCheckBox.isSelected()));

        jLabel1.setText(ResourceLoader.getString("edit.traintypes.abbr")); // NOI18N

        jLabel2.setText(ResourceLoader.getString("edit.traintypes.desc")); // NOI18N

        jLabel3.setText(ResourceLoader.getString("edit.traintypes.category")); // NOI18N

        jLabel4.setText(ResourceLoader.getString("edit.traintypes.color")); // NOI18N

        colorLabel.setText("0x000000");

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

        JButton cancelButton = new JButton(ResourceLoader.getString("button.cancel")); // NOI18N
        JButton okButton = new JButton(ResourceLoader.getString("button.ok")); // NOI18N

        cancelButton.addActionListener(evt -> setVisible(false));
        okButton.addActionListener(evt -> updateButtonActionPerformed());

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
                        .addComponent(cNameTemplateEditBox, GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                        .addComponent(completeNameTemplateCheckBox)
                        .addComponent(nameTemplateCheckBox)
                        .addComponent(descTextField, GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
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
                        .addComponent(nameTemplateEditBox, GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
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
                            .addComponent(percentLengthLabel))
                        .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(okButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(cancelButton)))
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
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(cancelButton)
                        .addComponent(okButton))
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

    private void updateValuesForTrainType(TrainType selected, TrainDiagram diagram) {
        // update categories
        brakeComboBox.removeAllItems();
        brakeComboBox.addItem(NONE_CATEGORY);
        for (TrainTypeCategory category : diagram.getTrainTypeCategories()) {
            brakeComboBox.addItem(Wrapper.getWrapper(category));
        }

        if (selected != null) {
            abbrTextField.getPresentationModel().init(selected.getAbbr(), diagram.getLocales());
            descTextField.getPresentationModel().init(selected.getDesc(), diagram.getLocales());
            colorLabel.setText(Conversions.convertColorToText(selected.getColor()));
            colorLabel.setForeground(selected.getColor());
            TrainTypeCategory category = selected.getCategory();
            brakeComboBox.setSelectedItem(category != null ? Wrapper.getWrapper(category) : NONE_CATEGORY);

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
        return ratio != null ? (int) (100 * ratio) : 100;
    }

    private void updateButtonActionPerformed() {
        // update values of a type
        TrainType type = trainType;
        if (type != null) {
            LocalizedString abbr = abbrTextField.getPresentationModel().getCurrentEdit().get();
            LocalizedString desc = descTextField.getPresentationModel().getCurrentEdit().get();
            // check values ....
            if (abbr == null || desc == null) {
                GuiComponentUtils.showError("dialog.error.missingvalues", this);
                return;
            }
            type.setAbbr(abbr);
            type.setDesc(desc);
            type.setPlatform(platformNeededCheckBox.isSelected());
            type.getAttributes().setBool(TrainType.ATTR_SHOW_WEIGHT_INFO, showWeightInfoCheckBox.isSelected());
            Color c = Conversions.convertTextToColor(colorLabel.getText());
            type.setColor(c);
            Wrapper<?> categoryWrapper = (Wrapper<?>) brakeComboBox.getSelectedItem();
            type.setCategory(categoryWrapper != NONE_CATEGORY ? (TrainTypeCategory) Objects.requireNonNull(categoryWrapper).getElement() : null);
            if (nameTemplateCheckBox.isSelected()) {
                try {
                    type.setTrainNameTemplate(nameTemplateEditBox.getTemplate());
                } catch (GrafikonException e) {
                    GuiComponentUtils.showWarning(e.getMessage(), this);
                    log.warn(e.getMessage(), e);
                    return;
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
                    return;
                }
            } else {
                type.setTrainCompleteNameTemplate(null);
            }
            type.getAttributes().setRemove(TrainType.ATTR_LINE_TYPE, extractLineType());
            type.getAttributes().setRemove(TrainType.ATTR_LINE_WIDTH, extractRatioFromPercentage(lineWidthTextField));
            type.getAttributes().setRemove(TrainType.ATTR_LINE_LENGTH, extractRatioFromPercentage(lineLengthTextField));
        }
        setVisible(false);
    }

    private Integer extractLineType() {
        Wrapper<?> selectedType = (Wrapper<?>) lineTypeComboBox.getSelectedItem();
        LineType type = (LineType) Objects.requireNonNull(selectedType).getElement();
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

    private LocalizedStringField<LocalizedStringPM> abbrTextField;
    private javax.swing.JComboBox<Wrapper<TrainTypeCategory>> brakeComboBox;
    private net.parostroj.timetable.gui.components.TextTemplateEditBox cNameTemplateEditBox;
    private javax.swing.JLabel colorLabel;
    private javax.swing.JCheckBox completeNameTemplateCheckBox;
    private javax.swing.JCheckBox platformNeededCheckBox;
    private LocalizedStringField<LocalizedStringPM> descTextField;
    private javax.swing.JCheckBox nameTemplateCheckBox;
    private net.parostroj.timetable.gui.components.TextTemplateEditBox nameTemplateEditBox;
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
    }
}
