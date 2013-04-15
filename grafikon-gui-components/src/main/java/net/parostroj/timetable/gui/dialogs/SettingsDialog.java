/*
 * SettingsDialog.java
 *
 * Created on 22. září 2007, 18:07
 */
package net.parostroj.timetable.gui.dialogs;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.UnitUtil;
import net.parostroj.timetable.model.units.WeightUnit;
import net.parostroj.timetable.utils.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;

/**
 * Dialog for settings modification of the train diagram.
 *
 * @author jub
 */
public class SettingsDialog extends javax.swing.JDialog {

    private static final Logger LOG = LoggerFactory.getLogger(SettingsDialog.class.getName());

    private static final String NO_UNIT = "-";

    private boolean diagramChanged;
    private boolean recalculate;
    private TrainDiagram diagram;

    /** Creates new form SettingsDialog */
    public SettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        diagramChanged = false;
        recalculate = false;

        sortComboBox.addItem(ResourceLoader.getString("modelinfo.sort.number"));
        sortComboBox.addItem(ResourceLoader.getString("modelinfo.sort.string"));
        sortComboBox.setPrototypeDisplayValue("nnnnnnnnnnnnn");

        for (TimeConverter.Rounding r : TimeConverter.Rounding.values()) {
        	roundingComboBox.addItem(ResourceLoader.getString("modelinfo.rounding." + r.getKey()));
        }
        roundingComboBox.setPrototypeDisplayValue("nnnnnnnnnnnnn");

        nameTemplateEditBox.setLanguages(Arrays.asList(TextTemplate.Language.values()));
        cNameTemplateEditBox.setLanguages(Arrays.asList(TextTemplate.Language.values()));

        emptyWeightEditBox.setUnits(Arrays.asList(WeightUnit.values()));
        loadedWeightEditBox.setUnits(Arrays.asList(WeightUnit.values()));
        emptyWeightEditBox.setUnit(WeightUnit.T);
        loadedWeightEditBox.setUnit(WeightUnit.T);
        lengthPerAxleEditBox.setUnits(LengthUnit.getScaleDependent());
        lengthPerAxleEditBox.setUnit(LengthUnit.M);

        for (LengthUnit unit : LengthUnit.values()) {
            lengthUnitComboBox.addItem(unit);
        }

        unitComboBox.addItem(NO_UNIT);
        speedUnitComboBox.addItem(NO_UNIT);
        for (LengthUnit unit : LengthUnit.values()) {
            if (unit.isScaleDependent()) {
                unitComboBox.addItem(unit);
            }
        }
        speedUnitComboBox.addItem(LengthUnit.KM);
        speedUnitComboBox.addItem(LengthUnit.MILE);

        pack();
    }

    public void setTrainDiagram(TrainDiagram diagram) {
        this.diagram = diagram;
        this.diagramChanged = false;
        this.recalculate = false;

        for (Scale scale : Scale.getPredefined()) {
            scaleComboBox.addItem(scale);
        }

        // set some values for speed
        for (double d = 4.0; d <= 6.0 ;) {
            ratioComboBox.addItem(Double.toString(d));
            d += 0.5;
        }

        this.updateValues();
    }

    private void updateValues() {
        if (diagram != null) {
            // set original values ...
            scaleComboBox.setSelectedItem(diagram.getAttribute(TrainDiagram.ATTR_SCALE));
            ratioComboBox.setSelectedItem(((Double)diagram.getAttribute(TrainDiagram.ATTR_TIME_SCALE)).toString());

            // sorting
            TrainsData trainsData = diagram.getTrainsData();
            SortPatternGroup firstGroup = trainsData.getTrainSortPattern().getGroups().get(0);
            sortComboBox.setSelectedIndex(firstGroup.getType() == SortPatternGroup.Type.NUMBER ? 0 : 1);
            roundingComboBox.setSelectedIndex(diagram.getTimeConverter().getRounding().ordinal());
            cNameTemplateEditBox.setTemplate(trainsData.getTrainCompleteNameTemplate());
            nameTemplateEditBox.setTemplate(trainsData.getTrainNameTemplate());

            // set crossing time in minutes
            Integer transferTime = (Integer)diagram.getAttribute(TrainDiagram.ATTR_STATION_TRANSFER_TIME);
            if (transferTime != null) {
                stationTransferTextField.setText(transferTime.toString());
            } else {
                LOG.warn("Station transfer time information missing.");
                stationTransferTextField.setText("");
            }

            // changes tracking
            changesTrackingCheckBox.setSelected(diagram.getChangesTracker().isTrackingEnabled());

            // script
            scriptEditBox.setScript(trainsData.getRunningTimeScript());

            // route length
            Double routeLengthRatio = (Double)diagram.getAttribute(TrainDiagram.ATTR_ROUTE_LENGTH_RATIO);
            rlRatioTextField.setText(routeLengthRatio != null ? routeLengthRatio.toString() : "");
            String routeLengthUnit = (String)diagram.getAttribute(TrainDiagram.ATTR_ROUTE_LENGTH_UNIT);
            rlUnitTextField.setText(routeLengthUnit != null ? routeLengthUnit : "");

            // weight -> length conversion
            loadedWeightEditBox.setValueInUnit(new BigDecimal((Integer) diagram.getAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE)), WeightUnit.KG);
            emptyWeightEditBox.setValueInUnit(new BigDecimal((Integer) diagram.getAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE_EMPTY)), WeightUnit.KG);
            lengthPerAxleEditBox.setValueInUnit(new BigDecimal((Integer) diagram.getAttribute(TrainDiagram.ATTR_LENGTH_PER_AXLE)), LengthUnit.MM);
            lengthUnitComboBox.setSelectedItem(diagram.getAttribute(TrainDiagram.ATTR_LENGTH_UNIT));

            // time range
            Integer fromTime = (Integer) diagram.getAttribute(TrainDiagram.ATTR_FROM_TIME);
            Integer toTime = (Integer) diagram.getAttribute(TrainDiagram.ATTR_TO_TIME);
            this.setTimeRange(fromTime, toTime);

            LengthUnit lUnit = diagram.getAttributes().get(TrainDiagram.ATTR_EDIT_LENGTH_UNIT, LengthUnit.class);
            LengthUnit sUnit = diagram.getAttributes().get(TrainDiagram.ATTR_EDIT_SPEED_UNIT, LengthUnit.class);
            unitComboBox.setSelectedItem(lUnit != null ? lUnit : NO_UNIT);
            speedUnitComboBox.setSelectedItem(sUnit != null ? sUnit : NO_UNIT);
        }
    }

    private void setTimeRange(Integer from, Integer to) {
        fromTimeTextField.setText(diagram.getTimeConverter().convertIntToText(from != null ? from : 0));
        toTimeTextField.setText(diagram.getTimeConverter().convertIntToTextNNFull(to != null ? to : TimeInterval.DAY));
    }

    private Tuple<Integer> getTimeRange() {
        int from = diagram.getTimeConverter().convertTextToInt(fromTimeTextField.getText());
        int to = diagram.getTimeConverter().convertTextToInt(toTimeTextField.getText());
        Integer fromTime = from == -1 ? 0 : from;
        Integer toTime = to == -1 ? TimeInterval.DAY : to;
        if (toTime == 0)
            toTime = TimeInterval.DAY;
        // check range (a least an hour)
        if (fromTime > toTime) {
            // swap
            Integer swap = fromTime;
            fromTime = toTime;
            toTime = swap;
        }
        // check minimal distance
        if ((toTime - fromTime) < 3600) {
            toTime = fromTime + 3600;
            if (toTime > TimeInterval.DAY) {
                toTime = TimeInterval.DAY;
                fromTime = toTime - 3600;
            }
        }
        if (fromTime == 0)
            fromTime = null;
        if (toTime == TimeInterval.DAY || toTime == 0)
            toTime = null;
        return new Tuple<Integer>(fromTime, toTime);
    }

    public boolean isDiagramChanged() {
        return diagramChanged;
    }

    public boolean isRecalculate() {
        return recalculate;
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        scaleComboBox = new javax.swing.JComboBox();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        ratioComboBox = new javax.swing.JComboBox();
        roundingComboBox = new javax.swing.JComboBox();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        nameTemplateEditBox = new net.parostroj.timetable.gui.components.TextTemplateEditBox();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        cNameTemplateEditBox = new net.parostroj.timetable.gui.components.TextTemplateEditBox();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        sortComboBox = new javax.swing.JComboBox();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        stationTransferTextField = new javax.swing.JTextField();
        changesTrackingCheckBox = new javax.swing.JCheckBox();
        javax.swing.JLabel jLabel11 = new javax.swing.JLabel();
        javax.swing.JPanel routeLengthPanel = new javax.swing.JPanel();
        javax.swing.JLabel jLabel12 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel14 = new javax.swing.JLabel();
        rlRatioTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel13 = new javax.swing.JLabel();
        rlUnitTextField = new javax.swing.JTextField();
        javax.swing.JPanel weightPerAxlePanel = new javax.swing.JPanel();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        loadedWeightEditBox = new net.parostroj.timetable.gui.components.ValueWithUnitEditBox();
        javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
        emptyWeightEditBox = new net.parostroj.timetable.gui.components.ValueWithUnitEditBox();
        javax.swing.JPanel lengthPanel = new javax.swing.JPanel();
        javax.swing.JLabel jLabel10 = new javax.swing.JLabel();
        lengthPerAxleEditBox = new net.parostroj.timetable.gui.components.ValueWithUnitEditBox();
        javax.swing.JLabel jLabel15 = new javax.swing.JLabel();
        lengthUnitComboBox = new javax.swing.JComboBox();
        javax.swing.JPanel timeRangePanel = new javax.swing.JPanel();
        javax.swing.JLabel jLabel16 = new javax.swing.JLabel();
        fromTimeTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel17 = new javax.swing.JLabel();
        toTimeTextField = new javax.swing.JTextField();
        scriptEditBox = new net.parostroj.timetable.gui.components.ScriptEditBox();
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(ResourceLoader.getString("modelinfo")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(ResourceLoader.getString("modelinfo.scales")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(jLabel1, gridBagConstraints);

        GridBagConstraints gridBagConstraints_2 = new java.awt.GridBagConstraints();
        gridBagConstraints_2.gridx = 1;
        gridBagConstraints_2.gridy = 0;
        gridBagConstraints_2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints_2.insets = new Insets(5, 2, 5, 10);
        getContentPane().add(scaleComboBox, gridBagConstraints_2);

        jLabel2.setText(ResourceLoader.getString("modelinfo.ratio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 5, 5);
        getContentPane().add(jLabel2, gridBagConstraints);

        ratioComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 5, 10);
        getContentPane().add(ratioComboBox, gridBagConstraints);

        JLabel label = new JLabel(ResourceLoader.getString("modelinfo.rounding"));
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.anchor = GridBagConstraints.WEST;
        gbc_label.insets = new Insets(0, 5, 5, 5);
        gbc_label.gridx = 0;
        gbc_label.gridy = 2;
        getContentPane().add(label, gbc_label);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 5, 10);
        getContentPane().add(roundingComboBox, gridBagConstraints);

        jLabel3.setText(ResourceLoader.getString("edit.traintypes.nametemplate")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 5, 5);
        getContentPane().add(jLabel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 5, 0);
        getContentPane().add(nameTemplateEditBox, gridBagConstraints);

        jLabel4.setText(ResourceLoader.getString("edit.traintypes.completenametemplate")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 10, 5, 5);
        getContentPane().add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 5, 0);
        getContentPane().add(cNameTemplateEditBox, gridBagConstraints);

        jLabel5.setText(ResourceLoader.getString("modelinfo.sort")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 5, 5);
        getContentPane().add(jLabel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 5, 10);
        getContentPane().add(sortComboBox, gridBagConstraints);

        jLabel6.setText(ResourceLoader.getString("modelinfo.crossing")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 5, 5);
        getContentPane().add(jLabel6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 5, 10);
        getContentPane().add(stationTransferTextField, gridBagConstraints);

        changesTrackingCheckBox.setText(ResourceLoader.getString("modelinfo.tracking.changes")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 2, 5, 10);
        getContentPane().add(changesTrackingCheckBox, gridBagConstraints);

        jLabel11.setText(ResourceLoader.getString("modelinfo.running.time.script")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        getContentPane().add(jLabel11, gridBagConstraints);

        routeLengthPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel12.setText(ResourceLoader.getString("modelinfo.route.length") + " -"); // NOI18N
        routeLengthPanel.add(jLabel12);

        jLabel14.setText(ResourceLoader.getString("modelinfo.route.length.ratio") + ":"); // NOI18N
        routeLengthPanel.add(jLabel14);

        rlRatioTextField.setColumns(7);
        routeLengthPanel.add(rlRatioTextField);

        jLabel13.setText(ResourceLoader.getString("modelinfo.route.length.unit") + ":"); // NOI18N
        routeLengthPanel.add(jLabel13);

        rlUnitTextField.setColumns(5);
        routeLengthPanel.add(rlUnitTextField);

        GridBagConstraints gridBagConstraints_3 = new java.awt.GridBagConstraints();
        gridBagConstraints_3.insets = new Insets(0, 0, 5, 0);
        gridBagConstraints_3.gridx = 0;
        gridBagConstraints_3.gridy = 13;
        gridBagConstraints_3.gridwidth = 3;
        gridBagConstraints_3.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints_3.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(routeLengthPanel, gridBagConstraints_3);

        weightPerAxlePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel7.setText(ResourceLoader.getString("modelinfo.weight.per.axle") + " - "); // NOI18N
        weightPerAxlePanel.add(jLabel7);

        jLabel8.setText(ResourceLoader.getString("modelinfo.weight.per.axle.loaded") + ":"); // NOI18N
        weightPerAxlePanel.add(jLabel8);

        loadedWeightEditBox.setValueColumns(5);
        weightPerAxlePanel.add(loadedWeightEditBox);

        jLabel9.setText(ResourceLoader.getString("modelinfo.weight.per.axle.empty") + ":"); // NOI18N
        weightPerAxlePanel.add(jLabel9);

        emptyWeightEditBox.setValueColumns(5);
        weightPerAxlePanel.add(emptyWeightEditBox);

        GridBagConstraints gridBagConstraints_1 = new java.awt.GridBagConstraints();
        gridBagConstraints_1.insets = new Insets(0, 0, 5, 0);
        gridBagConstraints_1.gridx = 0;
        gridBagConstraints_1.gridy = 11;
        gridBagConstraints_1.gridwidth = 3;
        gridBagConstraints_1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints_1.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(weightPerAxlePanel, gridBagConstraints_1);

        lengthPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel10.setText(ResourceLoader.getString("modelinfo.axle.length") + ":"); // NOI18N
        lengthPanel.add(jLabel10);

        lengthPerAxleEditBox.setValueColumns(5);
        lengthPanel.add(lengthPerAxleEditBox);

        jLabel15.setText(ResourceLoader.getString("modelinfo.length.unit") + ":"); // NOI18N
        lengthPanel.add(jLabel15);

        lengthPanel.add(lengthUnitComboBox);

        GridBagConstraints gridBagConstraints_4 = new java.awt.GridBagConstraints();
        gridBagConstraints_4.insets = new Insets(0, 0, 5, 0);
        gridBagConstraints_4.gridx = 0;
        gridBagConstraints_4.gridy = 12;
        gridBagConstraints_4.gridwidth = 3;
        gridBagConstraints_4.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints_4.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(lengthPanel, gridBagConstraints_4);

        timeRangePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel16.setText(ResourceLoader.getString("modelinfo.from.time")); // NOI18N
        timeRangePanel.add(jLabel16);

        fromTimeTextField.setColumns(7);
        fromTimeTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                timeTextFieldFocusLost(evt);
            }
        });
        timeRangePanel.add(fromTimeTextField);

        jLabel17.setText(ResourceLoader.getString("modelinfo.to.time")); // NOI18N
        timeRangePanel.add(jLabel17);

        toTimeTextField.setColumns(7);
        toTimeTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                timeTextFieldFocusLost(evt);
            }
        });
        timeRangePanel.add(toTimeTextField);

        GridBagConstraints gridBagConstraints_5 = new java.awt.GridBagConstraints();
        gridBagConstraints_5.insets = new Insets(0, 0, 5, 0);
        gridBagConstraints_5.gridx = 0;
        gridBagConstraints_5.gridy = 14;
        gridBagConstraints_5.gridwidth = 3;
        gridBagConstraints_5.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints_5.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(timeRangePanel, gridBagConstraints_5);

        javax.swing.JPanel unitsPanel = new javax.swing.JPanel();
        unitsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        gridBagConstraints_5 = new java.awt.GridBagConstraints();
        gridBagConstraints_5.insets = new Insets(0, 0, 5, 0);
        gridBagConstraints_5.gridx = 0;
        gridBagConstraints_5.gridy = 15;
        gridBagConstraints_5.gridwidth = 3;
        gridBagConstraints_5.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints_5.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(unitsPanel, gridBagConstraints_5);

        unitsPanel.add(new javax.swing.JLabel(ResourceLoader.getString("modelinfo.unit")));
        unitComboBox = new JComboBox();
        unitsPanel.add(unitComboBox);
        unitsPanel.add(new javax.swing.JLabel(ResourceLoader.getString("modelinfo.speed.unit")));
        speedUnitComboBox = new JComboBox();
        unitsPanel.add(speedUnitComboBox);
        unitsPanel.add(new javax.swing.JLabel("/h"));


        scriptEditBox.setColumns(80);
        scriptEditBox.setRows(8);
        scriptEditBox.setScriptFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        GridBagConstraints gridBagConstraints_6 = new java.awt.GridBagConstraints();
        gridBagConstraints_6.insets = new Insets(0, 0, 5, 0);
        gridBagConstraints_6.gridx = 0;
        gridBagConstraints_6.gridy = 17;
        gridBagConstraints_6.gridwidth = 3;
        gridBagConstraints_6.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints_6.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints_6.weightx = 1.0;
        gridBagConstraints_6.weighty = 1.0;
        getContentPane().add(scriptEditBox, gridBagConstraints_6);

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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(buttonPanel, gridBagConstraints);

        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        boolean recalculateUpate = false;
        boolean clear = false;

        // get templates values
        TrainsData trainsData = diagram.getTrainsData();
        TextTemplate completeName = null;
        TextTemplate name = null;
        try {
            completeName = cNameTemplateEditBox.getTemplate();
            name = nameTemplateEditBox.getTemplate();
        } catch (GrafikonException e) {
            JOptionPane.showMessageDialog(this.getParent(), ResourceLoader.getString("dialog.error.emptytemplates"),
                    ResourceLoader.getString("dialog.error.title"), JOptionPane.ERROR_MESSAGE);
            LOG.debug("Error setting templates.", e);
            return;
        }

        // set scale
        Scale s = (Scale)scaleComboBox.getSelectedItem();
        // set ratio
        double sp = 1.0;
        try {
            sp = Double.parseDouble((String)ratioComboBox.getSelectedItem());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this.getParent(), ResourceLoader.getString("dialog.error.badratio"),
                    ResourceLoader.getString("dialog.error.title"), JOptionPane.ERROR_MESSAGE);
            LOG.debug("Cannot covert ratio.", ex);
            return;
        }
        if (s != null && !s.equals(diagram.getAttribute(TrainDiagram.ATTR_SCALE))) {
            diagram.setAttribute(TrainDiagram.ATTR_SCALE, s);
            recalculateUpate = true;
        }
        if (sp != ((Double)diagram.getAttribute(TrainDiagram.ATTR_TIME_SCALE)).doubleValue()) {
            diagram.setAttribute(TrainDiagram.ATTR_TIME_SCALE, sp);
            recalculateUpate = true;
        }

        // set templates
        if (!completeName.equals(trainsData.getTrainCompleteNameTemplate())) {
            trainsData.setTrainCompleteNameTemplate(completeName);
            clear = true;
        }
        if (!name.equals(trainsData.getTrainNameTemplate())) {
            trainsData.setTrainNameTemplate(name);
            clear = true;
        }

        // set sorting
        SortPattern sPattern = null;
        if (sortComboBox.getSelectedIndex() == 0) {
            sPattern = new SortPattern("(\\d*)(.*)");
            sPattern.getGroups().add(new SortPatternGroup(1, SortPatternGroup.Type.NUMBER));
            sPattern.getGroups().add(new SortPatternGroup(2, SortPatternGroup.Type.STRING));
        } else {
            sPattern = new SortPattern("(.*)");
            sPattern.getGroups().add(new SortPatternGroup(1, SortPatternGroup.Type.STRING));
        }
        if (!sPattern.getPattern().equals(trainsData.getTrainSortPattern().getPattern()))
            trainsData.setTrainSortPattern(sPattern);

        TimeConverter.Rounding rounding = TimeConverter.Rounding.values()[roundingComboBox.getSelectedIndex()];
        if (diagram.getTimeConverter().getRounding() != rounding) {
        	diagram.setTimeConverter(new TimeConverter(rounding));
        	recalculateUpate = true;
        }

        // set transfer time
        try {
            Integer difference = Integer.valueOf(stationTransferTextField.getText());
            if (difference != null && !difference.equals(diagram.getAttribute(TrainDiagram.ATTR_STATION_TRANSFER_TIME)))
                diagram.setAttribute(TrainDiagram.ATTR_STATION_TRANSFER_TIME, difference);
        } catch (NumberFormatException e) {
            LOG.warn("Cannot parse station transfer time: {}", stationTransferTextField.getText());
        }

        // changes tracking
        if (changesTrackingCheckBox.isSelected() != diagram.getChangesTracker().isTrackingEnabled()) {
            if (changesTrackingCheckBox.isSelected() && !diagram.getChangesTracker().isTrackingEnabled() &&
                    diagram.getChangesTracker().getCurrentChangeSet() == null) {
                diagram.getChangesTracker().addVersion(null, null, null);
                diagram.getChangesTracker().setLastAsCurrent();
            }
            diagram.getChangesTracker().setTrackingEnabled(changesTrackingCheckBox.isSelected());
        }

        // set running time script
        try {
            Script newScript = scriptEditBox.getScript();
            if (!diagram.getTrainsData().getRunningTimeScript().equals(newScript) && newScript != null) {
                diagram.getTrainsData().setRunningTimeScript(newScript);
                recalculateUpate = true;
            }
        } catch (GrafikonException e) {
            JOptionPane.showMessageDialog(this.getParent(), String.format(ResourceLoader.getString("dialog.error.script"), e.getCause().getMessage()),
                    ResourceLoader.getString("dialog.error.title"), JOptionPane.ERROR_MESSAGE);
            LOG.debug("Error setting script.", e);
            return;
        }

        // weight -> length conversion
        BigDecimal wpa = loadedWeightEditBox.getValueInUnit(WeightUnit.KG);
        BigDecimal wpae = emptyWeightEditBox.getValueInUnit(WeightUnit.KG);
        BigDecimal lpa = lengthPerAxleEditBox.getValueInUnit(LengthUnit.MM);
        LengthUnit lu = (LengthUnit) lengthUnitComboBox.getSelectedItem();
        if (lu != diagram.getAttribute(TrainDiagram.ATTR_LENGTH_UNIT))
            diagram.setAttribute(TrainDiagram.ATTR_LENGTH_UNIT, lu);
        this.setAttributeBDtoInt(TrainDiagram.ATTR_WEIGHT_PER_AXLE, wpa);
        this.setAttributeBDtoInt(TrainDiagram.ATTR_WEIGHT_PER_AXLE_EMPTY, wpae);
        this.setAttributeBDtoInt(TrainDiagram.ATTR_LENGTH_PER_AXLE, lpa);

        // route length
        try {
            if (rlRatioTextField.getText() != null && !"".equals(rlRatioTextField.getText())) {
                Double rlRatio = Double.valueOf(rlRatioTextField.getText());
                if (!rlRatio.equals(diagram.getAttribute(TrainDiagram.ATTR_ROUTE_LENGTH_RATIO)))
                    diagram.setAttribute(TrainDiagram.ATTR_ROUTE_LENGTH_RATIO, rlRatio);
            } else {
                if (diagram.getAttribute(TrainDiagram.ATTR_ROUTE_LENGTH_RATIO) != null)
                    diagram.removeAttribute(TrainDiagram.ATTR_ROUTE_LENGTH_RATIO);
            }
        } catch (NumberFormatException e) {
            LOG.warn("Cannot convert route length ratio to double.", e);
        }
        if (rlUnitTextField.getText() != null && !rlUnitTextField.getText().equals("")) {
            if (!rlUnitTextField.getText().trim().equals(diagram.getAttribute(TrainDiagram.ATTR_ROUTE_LENGTH_UNIT)))
                diagram.setAttribute(TrainDiagram.ATTR_ROUTE_LENGTH_UNIT, rlUnitTextField.getText().trim());
        } else {
            if (diagram.getAttribute(TrainDiagram.ATTR_ROUTE_LENGTH_UNIT) != null)
                diagram.removeAttribute(TrainDiagram.ATTR_ROUTE_LENGTH_UNIT);
        }

        // time range
        Tuple<Integer> timeRange = this.getTimeRange();
        if ((timeRange.first != null && !timeRange.first.equals(diagram.getAttribute(TrainDiagram.ATTR_FROM_TIME)))
                || (timeRange.first == null && diagram.getAttribute(TrainDiagram.ATTR_FROM_TIME) != null)) {
            if (timeRange.first == null)
                diagram.removeAttribute(TrainDiagram.ATTR_FROM_TIME);
            else
                diagram.setAttribute(TrainDiagram.ATTR_FROM_TIME, timeRange.first);
        }
        if ((timeRange.second != null && !timeRange.second.equals(diagram.getAttribute(TrainDiagram.ATTR_TO_TIME)))
                || (timeRange.second == null && diagram.getAttribute(TrainDiagram.ATTR_TO_TIME) != null)) {
            if (timeRange.second == null)
                diagram.removeAttribute(TrainDiagram.ATTR_TO_TIME);
            else
                diagram.setAttribute(TrainDiagram.ATTR_TO_TIME, timeRange.second);
        }

        Object unitObject = unitComboBox.getSelectedItem();
        Object speedUnitObject = speedUnitComboBox.getSelectedItem();
        LengthUnit lUnit = diagram.getAttributes().get(TrainDiagram.ATTR_EDIT_LENGTH_UNIT, LengthUnit.class);
        LengthUnit sUnit = diagram.getAttributes().get(TrainDiagram.ATTR_EDIT_SPEED_UNIT, LengthUnit.class);
        if (unitObject != lUnit) {
            if (unitObject == NO_UNIT)
                diagram.removeAttribute(TrainDiagram.ATTR_EDIT_LENGTH_UNIT);
            else
                diagram.setAttribute(TrainDiagram.ATTR_EDIT_LENGTH_UNIT, unitObject);
        }
        if (speedUnitObject != sUnit) {
            if (speedUnitObject == NO_UNIT)
                diagram.removeAttribute(TrainDiagram.ATTR_EDIT_SPEED_UNIT);
            else
                diagram.setAttribute(TrainDiagram.ATTR_EDIT_SPEED_UNIT, speedUnitObject);
        }

        // clear cached information for train names
        if (clear)
            for (Train train : diagram.getTrains())
                train.clearCachedData();

        this.updateValues();

        this.setVisible(false);
        this.recalculate = recalculateUpate;
        this.diagramChanged = true;
    }

    private void setAttributeBDtoInt(String attr, BigDecimal value) {
        try {
            Integer cValue = UnitUtil.convert(value);
            if (!cValue.equals(diagram.getAttribute(attr)))
                diagram.setAttribute(attr, cValue);
        } catch (ArithmeticException e) {
            LOG.warn("Cannot convert {} attribute to int: {}", attr, e.getMessage());
        }
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.updateValues();
        this.setVisible(false);
        this.diagramChanged = false;
    }

    private void timeTextFieldFocusLost(java.awt.event.FocusEvent evt) {
        // focus lost - check time information
        Tuple<Integer> timeRange = this.getTimeRange();
        this.setTimeRange(timeRange.first, timeRange.second);
    }

    private net.parostroj.timetable.gui.components.TextTemplateEditBox cNameTemplateEditBox;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox changesTrackingCheckBox;
    private net.parostroj.timetable.gui.components.ValueWithUnitEditBox emptyWeightEditBox;
    private javax.swing.JTextField fromTimeTextField;
    private net.parostroj.timetable.gui.components.ValueWithUnitEditBox lengthPerAxleEditBox;
    private javax.swing.JComboBox lengthUnitComboBox;
    private net.parostroj.timetable.gui.components.ValueWithUnitEditBox loadedWeightEditBox;
    private net.parostroj.timetable.gui.components.TextTemplateEditBox nameTemplateEditBox;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox ratioComboBox;
    private javax.swing.JComboBox roundingComboBox;
    private javax.swing.JTextField rlRatioTextField;
    private javax.swing.JTextField rlUnitTextField;
    private javax.swing.JComboBox scaleComboBox;
    private net.parostroj.timetable.gui.components.ScriptEditBox scriptEditBox;
    private javax.swing.JComboBox sortComboBox;
    private javax.swing.JTextField stationTransferTextField;
    private javax.swing.JTextField toTimeTextField;
    private javax.swing.JComboBox unitComboBox;
    private javax.swing.JComboBox speedUnitComboBox;
}
