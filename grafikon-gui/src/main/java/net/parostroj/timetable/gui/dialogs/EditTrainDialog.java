/*
 * EditTrainDialog.java
 *
 * Created on 18. září 2007, 14:47
 */
package net.parostroj.timetable.gui.dialogs;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;

import net.parostroj.timetable.gui.components.GroupsComboBox;
import net.parostroj.timetable.gui.views.CreateTrainView;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.units.WeightUnit;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;

import net.parostroj.timetable.gui.components.ValueWithUnitEditBox;

import javax.swing.SwingConstants;

import java.awt.Component;
import java.awt.event.*;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.Box;

/**
 * Dialog for editation of train properties.
 *
 * @author jub
 */
public class EditTrainDialog extends javax.swing.JDialog {

    private static final Logger log = LoggerFactory.getLogger(EditTrainDialog.class);

    private static final String FROM_STATION = "${stations.first}";
    private static final String TO_STATION = "${stations.last}";
    private static final String STATION_X = "${stations.get(%d)}";

    private Train train;

    /**
     * Creates new form EditTrainDialog.
     *
     * @param parent
     * @param modal
     */
    public EditTrainDialog(java.awt.Window parent, boolean modal) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        initComponents();
        routeEditBox.setLanguages(Arrays.asList(TextTemplate.Language.values()));
    }

    /**
     * fills the dialog with train data.
     */
    public void showDialog(Train train) {
        this.train = train;
        if (train != null)  {
            // model for train types
            typeComboBox.setModel(new DefaultComboBoxModel<>(train.getDiagram().getTrainTypes().toArray(new TrainType[0])));
            typeComboBox.addItem(CreateTrainView.NO_TYPE);
            typeComboBox.setSelectedItem(train.getType() != null ? train.getType() : CreateTrainView.NO_TYPE);
            dieselCheckBox.setSelected(train.getAttributes().getBool(Train.ATTR_DIESEL));
            electricCheckBox.setSelected(train.getAttributes().getBool(Train.ATTR_ELECTRIC));
            managedFreightCheckBox.setSelected(train.getAttributes().getBool(Train.ATTR_MANAGED_FREIGHT));
            showLengthCheckBox.setSelected(train.getAttributes().getBool(Train.ATTR_SHOW_STATION_LENGTH));
            emptyCheckBox.setSelected(train.getAttributes().getBool(Train.ATTR_EMPTY));
            optionalCheckBox.setSelected(train.getAttributes().getBool(Train.ATTR_OPTIONAL));

            numberTextField.setText(train.getNumber());

            descriptionTextField.setText(train.getDescription());
            Integer topSpeed = train.getTopSpeed();
            speedTextField.setText(topSpeed == null ? "" : Integer.toString(topSpeed));
            Integer weight = train.getAttribute(Train.ATTR_WEIGHT, Integer.class);
            weightTextField.setText(weight != null ? weight.toString() : "");
            routeEditBox.setTemplate(train.getAttribute(Train.ATTR_ROUTE, TextTemplate.class));

            fromNodeButton.setText(train.getFirstInterval().getOwnerAsNode().getName());
            toNodeButton.setText(train.getLastInterval().getOwnerAsNode().getName());

            stationsComboBox.removeAllItems();
            for (TimeInterval i : train.getNodeIntervals()) {
                stationsComboBox.addItem(i.getOwnerAsNode());
            }

            timeBeforeTextField.setText(Integer.toString(train.getTimeBefore() / 60));
            timeAfterTextField.setText(Integer.toString(train.getTimeAfter() / 60));

            groupsComboBox.updateGroups(train.getDiagram(), train.getAttributes().get(Train.ATTR_GROUP, Group.class));

            Integer weightLimit = train.getAttributes().get(Train.ATTR_WEIGHT_LIMIT, Integer.class);
            weightLimitCheckBox.setSelected(weightLimit != null);
            weightLimitEditBox.setEnabled(weightLimit != null);
            if (weightLimit == null) {
                weightLimitEditBox.setValue(BigDecimal.ZERO);
            } else {
                weightLimitEditBox.setValueInUnit(BigDecimal.valueOf(weightLimit), WeightUnit.T);
            }

            // next and previous train
            TrainDiagram diagram = train.getDiagram();

            Node from = train.getFirstInterval().getOwnerAsNode();
            Node to = train.getLastInterval().getOwnerAsNode();

            nextTrainModel.clear();
            nextTrainModel.addWrapper(Wrapper.getEmptyWrapper("-"));
            previousTrainModel.clear();
            previousTrainModel.addWrapper(Wrapper.getEmptyWrapper("-"));

            diagram.getTrains().stream().forEach(t -> {
                if (t != train) {
                    if (t.getFirstInterval().getOwner() == to) {
                        nextTrainModel.addWrapper(Wrapper.getWrapper(t));
                    }
                    if (t.getLastInterval().getOwner() == from) {
                        previousTrainModel.addWrapper(Wrapper.getWrapper(t));
                    }
                }
            });

            nextTrainModel.setSelectedObject(train.getNextJoinedTrain());
            previousTrainModel.setSelectedObject(train.getPreviousJoinedTrain());
        }
        pack();
        setMinimumSize(getSize());
        this.setVisible(true);
    }

    private void initComponents() {
        javax.swing.JLabel typeLabel = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox<>();
        numberTextField = new javax.swing.JTextField();
        javax.swing.JLabel numberLabel = new javax.swing.JLabel();
        descriptionTextField = new javax.swing.JTextField();
        javax.swing.JLabel descLabel = new javax.swing.JLabel();
        speedTextField = new javax.swing.JTextField();
        javax.swing.JLabel speedLabel = new javax.swing.JLabel();
        weightTextField = new javax.swing.JTextField();
        routeEditBox = new net.parostroj.timetable.gui.components.TextTemplateEditBox();
        javax.swing.JLabel weightLabel = new javax.swing.JLabel();
        javax.swing.JLabel routeLabel = new javax.swing.JLabel();
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();

        setTitle(ResourceLoader.getString("edit.train")); // NOI18N

        typeLabel.setText(ResourceLoader.getString("create.train.type")); // NOI18N

        numberTextField.setColumns(20);

        numberLabel.setText(ResourceLoader.getString("create.train.number")); // NOI18N

        descriptionTextField.setColumns(30);

        descLabel.setText(ResourceLoader.getString("create.train.description")); // NOI18N

        speedTextField.setColumns(10);

        speedLabel.setText(ResourceLoader.getString("create.train.speed")); // NOI18N

        weightLabel.setText(ResourceLoader.getString("edit.train.weight")); // NOI18N

        routeLabel.setText(ResourceLoader.getString("edit.train.route")); // NOI18N

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(this::okButtonActionPerformed);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(this::cancelButtonActionPerformed);

        groupsComboBox = new GroupsComboBox(false);

        JLabel groupLabel = new JLabel(ResourceLoader.getString("create.train.group"));

        JPanel optionsPanel = new JPanel();

        JPanel routeEditPanel = new JPanel();

        JPanel techTimesPanel = new JPanel();
        techTimesPanel.setBorder(BorderFactory.createEmptyBorder());
        JPanel connectedTrainsPanel = new JPanel();
        connectedTrainsPanel.setBorder(BorderFactory.createEmptyBorder());
        FlowLayout flTechTimesPanel = (FlowLayout) techTimesPanel.getLayout();
        flTechTimesPanel.setHgap(0);
        flTechTimesPanel.setAlignOnBaseline(true);
        flTechTimesPanel.setAlignment(FlowLayout.LEFT);
        flTechTimesPanel.setVgap(0);

        JPanel weightLimitPanel = new JPanel();

        JLabel weightLimitLabel = new JLabel(ResourceLoader.getString("edit.train.weight.limit") + ":");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                        .addComponent(techTimesPanel, GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                        .addComponent(connectedTrainsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(okButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(cancelButton))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(routeLabel)
                                .addComponent(weightLabel)
                                .addComponent(weightLimitLabel)
                                .addComponent(speedLabel)
                                .addComponent(descLabel)
                                .addComponent(numberLabel)
                                .addComponent(groupLabel)
                                .addComponent(typeLabel))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(routeEditPanel, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                                .addComponent(optionsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(descriptionTextField, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                                .addComponent(weightTextField, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                                .addComponent(numberTextField, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                                .addComponent(typeComboBox, 0, 301, Short.MAX_VALUE)
                                .addComponent(routeEditBox, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                                .addComponent(groupsComboBox, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                                .addComponent(weightLimitPanel, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                                .addComponent(speedTextField, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE))))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(typeLabel)
                        .addComponent(typeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(optionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(groupsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(groupLabel))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(numberLabel)
                        .addComponent(numberTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(descriptionTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(descLabel))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(speedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(speedLabel))
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(weightLimitPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(10)
                            .addComponent(weightLimitLabel)))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(weightTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(weightLabel))
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(routeEditBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(routeEditPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(10)
                            .addComponent(routeLabel)))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(techTimesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(connectedTrainsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(cancelButton)
                        .addComponent(okButton))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GridBagLayout gblConnectedTrainsPanel = new GridBagLayout();
        connectedTrainsPanel.setLayout(gblConnectedTrainsPanel);

        JLabel joinedTrainsLabel = new JLabel(ResourceLoader.getString("edit.train.joined"));
        GridBagConstraints gbcJoinedTrainsLabel = new GridBagConstraints();
        gbcJoinedTrainsLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcJoinedTrainsLabel.anchor = GridBagConstraints.WEST;
        gbcJoinedTrainsLabel.gridwidth = 2;
        gbcJoinedTrainsLabel.insets = new Insets(0, 0, 5, 5);
        gbcJoinedTrainsLabel.gridx = 0;
        gbcJoinedTrainsLabel.gridy = 0;
        connectedTrainsPanel.add(joinedTrainsLabel, gbcJoinedTrainsLabel);

        JLabel previousTrainLabel = new JLabel(ResourceLoader.getString("edit.train.joined.previous") + ": ");
        GridBagConstraints gbcPreviousTrainLabel = new GridBagConstraints();
        gbcPreviousTrainLabel.anchor = GridBagConstraints.WEST;
        gbcPreviousTrainLabel.insets = new Insets(0, 10, 5, 5);
        gbcPreviousTrainLabel.gridx = 0;
        gbcPreviousTrainLabel.gridy = 1;
        connectedTrainsPanel.add(previousTrainLabel, gbcPreviousTrainLabel);

        JComboBox<Wrapper<Train>> previousTrainComboBox = new JComboBox<>();
        previousTrainModel = new WrapperListModel<>(true);
        previousTrainComboBox.setModel(previousTrainModel);
        GridBagConstraints gbcPreviousTrainComboBox = new GridBagConstraints();
        gbcPreviousTrainComboBox.insets = new Insets(0, 0, 5, 0);
        gbcPreviousTrainComboBox.weightx = 1.0;
        gbcPreviousTrainComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbcPreviousTrainComboBox.anchor = GridBagConstraints.NORTHWEST;
        gbcPreviousTrainComboBox.gridx = 1;
        gbcPreviousTrainComboBox.gridy = 1;
        connectedTrainsPanel.add(previousTrainComboBox, gbcPreviousTrainComboBox);

        JLabel nextTrainLabel = new JLabel(ResourceLoader.getString("edit.train.joined.next") + ": ");
        GridBagConstraints gbcNextTrainLabel = new GridBagConstraints();
        gbcNextTrainLabel.anchor = GridBagConstraints.WEST;
        gbcNextTrainLabel.insets = new Insets(0, 10, 0, 5);
        gbcNextTrainLabel.gridx = 0;
        gbcNextTrainLabel.gridy = 2;
        connectedTrainsPanel.add(nextTrainLabel, gbcNextTrainLabel);

        JComboBox<Wrapper<Train>> nextTrainComboBox = new JComboBox<>();
        nextTrainModel = new WrapperListModel<>(true);
        nextTrainComboBox.setModel(nextTrainModel);
        GridBagConstraints gbcNextTrainComboBox = new GridBagConstraints();
        gbcNextTrainComboBox.weightx = 1.0;
        gbcNextTrainComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbcNextTrainComboBox.anchor = GridBagConstraints.NORTHWEST;
        gbcNextTrainComboBox.gridx = 1;
        gbcNextTrainComboBox.gridy = 2;
        connectedTrainsPanel.add(nextTrainComboBox, gbcNextTrainComboBox);

        layout.linkSize(SwingConstants.HORIZONTAL, typeLabel, numberLabel, descLabel, speedLabel, weightLabel,
                routeLabel, groupLabel, weightLimitLabel);
        GridBagLayout gblWeightLimitPanel = new GridBagLayout();
        gblWeightLimitPanel.columnWeights = new double[] { 0.0, 0.0 };
        gblWeightLimitPanel.rowWeights = new double[] { 0.0 };
        weightLimitPanel.setLayout(gblWeightLimitPanel);

        weightLimitCheckBox = new JCheckBox();
        weightLimitCheckBox.addItemListener(e -> weightLimitEditBox.setEnabled(e.getStateChange() == ItemEvent.SELECTED));
        GridBagConstraints gbcWeightLimitCheckBox = new GridBagConstraints();
        gbcWeightLimitCheckBox.anchor = GridBagConstraints.WEST;
        gbcWeightLimitCheckBox.gridx = 1;
        gbcWeightLimitCheckBox.gridy = 0;
        weightLimitPanel.add(weightLimitCheckBox, gbcWeightLimitCheckBox);

        weightLimitEditBox = new ValueWithUnitEditBox();
        weightLimitEditBox.setUnits(Arrays.asList(WeightUnit.values()));
        weightLimitEditBox.setUnit(WeightUnit.T);
        GridBagConstraints gbcWeightLimitEditBox = new GridBagConstraints();
        gbcWeightLimitEditBox.weightx = 1.0;
        gbcWeightLimitEditBox.fill = GridBagConstraints.HORIZONTAL;
        gbcWeightLimitEditBox.anchor = GridBagConstraints.NORTHWEST;
        gbcWeightLimitEditBox.gridx = 0;
        gbcWeightLimitEditBox.gridy = 0;
        weightLimitPanel.add(weightLimitEditBox, gbcWeightLimitEditBox);
        javax.swing.JLabel techTimesLabel = new javax.swing.JLabel();
        techTimesPanel.add(techTimesLabel);

        techTimesLabel.setText(ResourceLoader.getString("create.train.technological.time"));

        Component delimiterStrut1 = Box.createHorizontalStrut(5);
        techTimesPanel.add(delimiterStrut1);
        javax.swing.JLabel beforeLabel = new javax.swing.JLabel();
        techTimesPanel.add(beforeLabel);

        beforeLabel.setText(ResourceLoader.getString("create.train.time.before"));

        Component delimiterStrut2 = Box.createHorizontalStrut(5);
        techTimesPanel.add(delimiterStrut2);
        timeBeforeTextField = new javax.swing.JTextField();
        techTimesPanel.add(timeBeforeTextField);

        timeBeforeTextField.setColumns(6);

        Component delimiterStrut3 = Box.createHorizontalStrut(5);
        techTimesPanel.add(delimiterStrut3);
        javax.swing.JLabel afterLabel = new javax.swing.JLabel();
        techTimesPanel.add(afterLabel);

        afterLabel.setText(ResourceLoader.getString("create.train.time.after"));

        Component delimiterStrut4 = Box.createHorizontalStrut(5);
        techTimesPanel.add(delimiterStrut4);
        timeAfterTextField = new javax.swing.JTextField();
        techTimesPanel.add(timeAfterTextField);

        timeAfterTextField.setColumns(6);
        GridBagLayout gblRouteEditPanel = new GridBagLayout();
        routeEditPanel.setLayout(gblRouteEditPanel);
        javax.swing.JLabel routeInsertLabel = new javax.swing.JLabel();
        GridBagConstraints gbcRouteInsertLabel = new GridBagConstraints();
        gbcRouteInsertLabel.anchor = GridBagConstraints.WEST;
        gbcRouteInsertLabel.insets = new Insets(0, 0, 0, 5);
        gbcRouteInsertLabel.gridx = 0;
        gbcRouteInsertLabel.gridy = 0;
        routeEditPanel.add(routeInsertLabel, gbcRouteInsertLabel);

        routeInsertLabel.setText(ResourceLoader.getString("edit.train.insert.node"));
        fromNodeButton = new javax.swing.JButton();
        GridBagConstraints gbcFromNodeButton = new GridBagConstraints();
        gbcFromNodeButton.weightx = 1.0;
        gbcFromNodeButton.fill = GridBagConstraints.HORIZONTAL;
        gbcFromNodeButton.anchor = GridBagConstraints.WEST;
        gbcFromNodeButton.insets = new Insets(0, 0, 0, 5);
        gbcFromNodeButton.gridx = 1;
        gbcFromNodeButton.gridy = 0;
        routeEditPanel.add(fromNodeButton, gbcFromNodeButton);

        fromNodeButton.addActionListener(this::fromNodeButtonActionPerformed);
        toNodeButton = new javax.swing.JButton();
        GridBagConstraints gbcToNodeButton = new GridBagConstraints();
        gbcToNodeButton.weightx = 1.0;
        gbcToNodeButton.fill = GridBagConstraints.HORIZONTAL;
        gbcToNodeButton.anchor = GridBagConstraints.WEST;
        gbcToNodeButton.insets = new Insets(0, 0, 0, 5);
        gbcToNodeButton.gridx = 2;
        gbcToNodeButton.gridy = 0;
        routeEditPanel.add(toNodeButton, gbcToNodeButton);

        toNodeButton.addActionListener(this::toNodeButtonActionPerformed);
        stationsComboBox = new javax.swing.JComboBox<>();
        GridBagConstraints gbcStationsComboBox = new GridBagConstraints();
        gbcStationsComboBox.weightx = 1.0;
        gbcStationsComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbcStationsComboBox.anchor = GridBagConstraints.NORTHWEST;
        gbcStationsComboBox.insets = new Insets(0, 0, 0, 5);
        gbcStationsComboBox.gridx = 3;
        gbcStationsComboBox.gridy = 0;
        routeEditPanel.add(stationsComboBox, gbcStationsComboBox);
        javax.swing.JButton insertButton = new javax.swing.JButton();
        GridBagConstraints gbcInsertButton = new GridBagConstraints();
        gbcInsertButton.anchor = GridBagConstraints.EAST;
        gbcInsertButton.gridx = 4;
        gbcInsertButton.gridy = 0;
        routeEditPanel.add(insertButton, gbcInsertButton);

        insertButton.setText("^");
        insertButton.addActionListener(this::insertButtonActionPerformed);
        GridBagLayout gblOptionsPanel = new GridBagLayout();
        gblOptionsPanel.columnWeights = new double[]{0.0, 0.0};
        gblOptionsPanel.rowWeights = new double[]{0.0, 0.0, 0.0};
        optionsPanel.setLayout(gblOptionsPanel);
        dieselCheckBox = new javax.swing.JCheckBox();
        GridBagConstraints gbcDieselCheckBox = new GridBagConstraints();
        gbcDieselCheckBox.anchor = GridBagConstraints.WEST;
        gbcDieselCheckBox.insets = new Insets(0, 0, 5, 5);
        gbcDieselCheckBox.gridx = 0;
        gbcDieselCheckBox.gridy = 0;
        optionsPanel.add(dieselCheckBox, gbcDieselCheckBox);

        dieselCheckBox.setText(ResourceLoader.getString("create.train.diesel")); // NOI18N
        dieselCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        dieselCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        emptyCheckBox = new javax.swing.JCheckBox();
        GridBagConstraints gbcEmptyCheckBox = new GridBagConstraints();
        gbcEmptyCheckBox.weightx = 1.0;
        gbcEmptyCheckBox.anchor = GridBagConstraints.WEST;
        gbcEmptyCheckBox.fill = GridBagConstraints.HORIZONTAL;
        gbcEmptyCheckBox.insets = new Insets(0, 0, 5, 0);
        gbcEmptyCheckBox.gridx = 1;
        gbcEmptyCheckBox.gridy = 0;
        optionsPanel.add(emptyCheckBox, gbcEmptyCheckBox);

        emptyCheckBox.setText(ResourceLoader.getString("create.train.empty")); // NOI18N
        emptyCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        emptyCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        electricCheckBox = new javax.swing.JCheckBox();
        GridBagConstraints gbcElectricCheckBox = new GridBagConstraints();
        gbcElectricCheckBox.anchor = GridBagConstraints.WEST;
        gbcElectricCheckBox.insets = new Insets(0, 0, 5, 5);
        gbcElectricCheckBox.gridx = 0;
        gbcElectricCheckBox.gridy = 1;
        optionsPanel.add(electricCheckBox, gbcElectricCheckBox);

        electricCheckBox.setText(ResourceLoader.getString("create.train.electric")); // NOI18N
        electricCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        electricCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        showLengthCheckBox = new javax.swing.JCheckBox();
        GridBagConstraints gbcShowLengthCheckBox = new GridBagConstraints();
        gbcShowLengthCheckBox.insets = new Insets(0, 0, 5, 0);
        gbcShowLengthCheckBox.weightx = 1.0;
        gbcShowLengthCheckBox.anchor = GridBagConstraints.WEST;
        gbcShowLengthCheckBox.fill = GridBagConstraints.HORIZONTAL;
        gbcShowLengthCheckBox.gridx = 1;
        gbcShowLengthCheckBox.gridy = 1;
        optionsPanel.add(showLengthCheckBox, gbcShowLengthCheckBox);

        showLengthCheckBox.setText(ResourceLoader.getString("create.train.show.station.length")); // NOI18N
        showLengthCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        showLengthCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        managedFreightCheckBox = new javax.swing.JCheckBox(ResourceLoader.getString("edit.train.managed.freight")); // NOI18N
        managedFreightCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        managedFreightCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        GridBagConstraints gbcManagedFreightCheckBox = new GridBagConstraints();
        gbcManagedFreightCheckBox.weightx = 1.0;
        gbcManagedFreightCheckBox.anchor = GridBagConstraints.NORTHWEST;
        gbcManagedFreightCheckBox.fill = GridBagConstraints.HORIZONTAL;
        gbcManagedFreightCheckBox.insets = new Insets(0, 0, 5, 5);
        gbcManagedFreightCheckBox.gridx = 0;
        gbcManagedFreightCheckBox.gridy = 2;
        optionsPanel.add(managedFreightCheckBox, gbcManagedFreightCheckBox);

        optionalCheckBox = new javax.swing.JCheckBox(ResourceLoader.getString("edit.train.optional.train")); //NOI18N
        GridBagConstraints gbcCheckBox = new GridBagConstraints();
        optionalCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        optionalCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gbcCheckBox.weightx = 1.0;
        gbcCheckBox.anchor = GridBagConstraints.WEST;
        gbcCheckBox.fill = GridBagConstraints.HORIZONTAL;
        gbcCheckBox.gridx = 1;
        gbcCheckBox.gridy = 2;
        optionsPanel.add(optionalCheckBox, gbcCheckBox);

        getContentPane().setLayout(layout);

        pack();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // set values to train ...
        TrainType type = (TrainType) typeComboBox.getSelectedItem();
        train.setType(type != CreateTrainView.NO_TYPE ? type : null);
        train.setAttribute(Train.ATTR_DIESEL, dieselCheckBox.isSelected());
        train.setAttribute(Train.ATTR_ELECTRIC, electricCheckBox.isSelected());
        train.getAttributes().setBool(Train.ATTR_SHOW_STATION_LENGTH, showLengthCheckBox.isSelected());
        train.getAttributes().setBool(Train.ATTR_EMPTY, emptyCheckBox.isSelected());
        train.getAttributes().setBool(Train.ATTR_OPTIONAL, optionalCheckBox.isSelected());
        String newNumber = ObjectsUtil.checkAndTrim(numberTextField.getText());
        if (newNumber != null) {
            train.setNumber(newNumber);
        }
        train.setDescription(ObjectsUtil.checkAndTrim(descriptionTextField.getText()));
        Group sGroup = groupsComboBox.getGroupSelection().getGroup();
        train.getAttributes().setRemove(Train.ATTR_GROUP, sGroup);

        // weight
        Integer oldWI = train.getAttribute(Train.ATTR_WEIGHT, Integer.class);
        Integer newWI = null;
        try {
            String weightStr = ObjectsUtil.checkAndTrim(weightTextField.getText());
            if (weightStr != null) {
                newWI = Integer.valueOf(weightStr);
            }
        } catch (NumberFormatException e) {
            log.warn("Couldn't convert weight to int.");
            newWI = oldWI;
        }
        train.getAttributes().setRemove(Train.ATTR_WEIGHT, newWI);

        // weight limit
        Integer weightLimit = null;
        if (weightLimitCheckBox.isSelected()) {
            weightLimit = weightLimitEditBox.getValueInUnit(WeightUnit.T).intValue();
        }
        train.getAttributes().setRemove(Train.ATTR_WEIGHT_LIMIT, weightLimit);

        // route
        try {
            TextTemplate newRI = routeEditBox.getTemplateEmpty();
            train.getAttributes().setRemove(Train.ATTR_ROUTE, newRI);
        } catch (GrafikonException e) {
            log.warn("Error creating template: {}", e.getMessage());
        }

        // check max speed - modify if changed
        try {
            String speedText = ObjectsUtil.checkAndTrim(speedTextField.getText());
            Integer maxSpeed = null;
            if (speedText != null) {
                maxSpeed = Integer.parseInt(speedText);
                if (maxSpeed <= 0) {
                    log.warn("Speed has to be positive number: {}", maxSpeed);
                    maxSpeed = null;
                }
            }
            train.setTopSpeed(maxSpeed);
        } catch (NumberFormatException e) {
            log.warn("Cannot convert speed to number: {}", speedTextField.getText());
        }

        // technological times
        try {
            int timeBefore = Integer.parseInt(timeBeforeTextField.getText()) * 60;
            int timeAfter = Integer.parseInt(timeAfterTextField.getText()) * 60;
            if (timeBefore != train.getTimeBefore()) {
                train.setTimeBefore(timeBefore);
            }
            if (timeAfter != train.getTimeAfter()) {
                train.setTimeAfter(timeAfter);
            }
        } catch (NumberFormatException e) {
            log.warn("Cannot convert technological time: {}, {}", timeBeforeTextField.getText(), timeAfterTextField.getText());
        }

        // managed freight
        train.getAttributes().setBool(Train.ATTR_MANAGED_FREIGHT, managedFreightCheckBox.isSelected());

        // next and previous trains
        train.setNextJoinedTrain(nextTrainModel.getSelectedObject());
        Train previousTrain = train.getPreviousJoinedTrain();
        Train newPreviousTrain = previousTrainModel.getSelectedObject();
        if (previousTrain != newPreviousTrain) {
            if (newPreviousTrain != null) {
                newPreviousTrain.setNextJoinedTrain(train);
            } else {
                previousTrain.setNextJoinedTrain(null);
            }
        }

        this.setVisible(false);
    }

    private void fromNodeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        routeEditBox.insertText(FROM_STATION);
        routeEditBox.requestFocusForTemplateField();
    }

    private void toNodeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        routeEditBox.insertText(TO_STATION);
        routeEditBox.requestFocusForTemplateField();
    }

    private void insertButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String stationX = String.format(STATION_X, stationsComboBox.getSelectedIndex());
        routeEditBox.insertText(stationX);
        routeEditBox.requestFocusForTemplateField();
    }

    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JCheckBox dieselCheckBox;
    private javax.swing.JCheckBox electricCheckBox;
    private javax.swing.JCheckBox emptyCheckBox;
    private javax.swing.JCheckBox optionalCheckBox;
    private javax.swing.JButton fromNodeButton;
    private javax.swing.JTextField numberTextField;
    private net.parostroj.timetable.gui.components.TextTemplateEditBox routeEditBox;
    private javax.swing.JCheckBox showLengthCheckBox;
    private javax.swing.JTextField speedTextField;
    private javax.swing.JComboBox<Node> stationsComboBox;
    private javax.swing.JTextField timeAfterTextField;
    private javax.swing.JTextField timeBeforeTextField;
    private javax.swing.JButton toNodeButton;
    private javax.swing.JComboBox<TrainType> typeComboBox;
    private javax.swing.JTextField weightTextField;
    private GroupsComboBox groupsComboBox;
    private JCheckBox weightLimitCheckBox;
    private ValueWithUnitEditBox weightLimitEditBox;
    private javax.swing.JCheckBox managedFreightCheckBox;

    private WrapperListModel<Train> previousTrainModel;
    private WrapperListModel<Train> nextTrainModel;
}
