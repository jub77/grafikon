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
            speedTextField.setText(Integer.toString(train.getTopSpeed()));
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
        okButton.addActionListener(evt -> okButtonActionPerformed(evt));

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(evt -> cancelButtonActionPerformed(evt));

        groupsComboBox = new GroupsComboBox(false);

        JLabel groupLabel = new JLabel(ResourceLoader.getString("create.train.group"));

        JPanel optionsPanel = new JPanel();

        JPanel routeEditPanel = new JPanel();

        JPanel techTimesPanel = new JPanel();
        techTimesPanel.setBorder(BorderFactory.createEmptyBorder());
        JPanel connectedTrainsPanel = new JPanel();
        connectedTrainsPanel.setBorder(BorderFactory.createEmptyBorder());
        FlowLayout fl_techTimesPanel = (FlowLayout) techTimesPanel.getLayout();
        fl_techTimesPanel.setHgap(0);
        fl_techTimesPanel.setAlignOnBaseline(true);
        fl_techTimesPanel.setAlignment(FlowLayout.LEFT);
        fl_techTimesPanel.setVgap(0);

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

        GridBagLayout gbl_connectedTrainsPanel = new GridBagLayout();
        connectedTrainsPanel.setLayout(gbl_connectedTrainsPanel);

        JLabel joinedTrainsLabel = new JLabel(ResourceLoader.getString("edit.train.joined"));
        GridBagConstraints gbc_joinedTrainsLabel = new GridBagConstraints();
        gbc_joinedTrainsLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_joinedTrainsLabel.anchor = GridBagConstraints.WEST;
        gbc_joinedTrainsLabel.gridwidth = 2;
        gbc_joinedTrainsLabel.insets = new Insets(0, 0, 5, 5);
        gbc_joinedTrainsLabel.gridx = 0;
        gbc_joinedTrainsLabel.gridy = 0;
        connectedTrainsPanel.add(joinedTrainsLabel, gbc_joinedTrainsLabel);

        JLabel previousTrainLabel = new JLabel(ResourceLoader.getString("edit.train.joined.previous") + ": ");
        GridBagConstraints gbc_previousTrainLabel = new GridBagConstraints();
        gbc_previousTrainLabel.anchor = GridBagConstraints.WEST;
        gbc_previousTrainLabel.insets = new Insets(0, 10, 5, 5);
        gbc_previousTrainLabel.gridx = 0;
        gbc_previousTrainLabel.gridy = 1;
        connectedTrainsPanel.add(previousTrainLabel, gbc_previousTrainLabel);

        JComboBox<Wrapper<Train>> previousTrainComboBox = new JComboBox<>();
        previousTrainModel = new WrapperListModel<>(true);
        previousTrainComboBox.setModel(previousTrainModel);
        GridBagConstraints gbc_previousTrainComboBox = new GridBagConstraints();
        gbc_previousTrainComboBox.insets = new Insets(0, 0, 5, 0);
        gbc_previousTrainComboBox.weightx = 1.0;
        gbc_previousTrainComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_previousTrainComboBox.anchor = GridBagConstraints.NORTHWEST;
        gbc_previousTrainComboBox.gridx = 1;
        gbc_previousTrainComboBox.gridy = 1;
        connectedTrainsPanel.add(previousTrainComboBox, gbc_previousTrainComboBox);

        JLabel nextTrainLabel = new JLabel(ResourceLoader.getString("edit.train.joined.next") + ": ");
        GridBagConstraints gbc_nextTrainLabel = new GridBagConstraints();
        gbc_nextTrainLabel.anchor = GridBagConstraints.WEST;
        gbc_nextTrainLabel.insets = new Insets(0, 10, 0, 5);
        gbc_nextTrainLabel.gridx = 0;
        gbc_nextTrainLabel.gridy = 2;
        connectedTrainsPanel.add(nextTrainLabel, gbc_nextTrainLabel);

        JComboBox<Wrapper<Train>> nextTrainComboBox = new JComboBox<>();
        nextTrainModel = new WrapperListModel<>(true);
        nextTrainComboBox.setModel(nextTrainModel);
        GridBagConstraints gbc_nextTrainComboBox = new GridBagConstraints();
        gbc_nextTrainComboBox.weightx = 1.0;
        gbc_nextTrainComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_nextTrainComboBox.anchor = GridBagConstraints.NORTHWEST;
        gbc_nextTrainComboBox.gridx = 1;
        gbc_nextTrainComboBox.gridy = 2;
        connectedTrainsPanel.add(nextTrainComboBox, gbc_nextTrainComboBox);

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {typeLabel, numberLabel, descLabel, speedLabel, weightLabel, routeLabel, groupLabel, weightLimitLabel});
        GridBagLayout gbl_weightLimitPanel = new GridBagLayout();
        gbl_weightLimitPanel.columnWeights = new double[] { 0.0, 0.0 };
        gbl_weightLimitPanel.rowWeights = new double[] { 0.0 };
        weightLimitPanel.setLayout(gbl_weightLimitPanel);

        weightLimitCheckBox = new JCheckBox();
        weightLimitCheckBox.addItemListener(e -> weightLimitEditBox.setEnabled(e.getStateChange() == ItemEvent.SELECTED));
        GridBagConstraints gbc_weightLimitCheckBox = new GridBagConstraints();
        gbc_weightLimitCheckBox.anchor = GridBagConstraints.WEST;
        gbc_weightLimitCheckBox.gridx = 1;
        gbc_weightLimitCheckBox.gridy = 0;
        weightLimitPanel.add(weightLimitCheckBox, gbc_weightLimitCheckBox);

        weightLimitEditBox = new ValueWithUnitEditBox();
        weightLimitEditBox.setUnits(Arrays.asList(WeightUnit.values()));
        weightLimitEditBox.setUnit(WeightUnit.T);
        GridBagConstraints gbc_weightLimitEditBox = new GridBagConstraints();
        gbc_weightLimitEditBox.weightx = 1.0;
        gbc_weightLimitEditBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_weightLimitEditBox.anchor = GridBagConstraints.NORTHWEST;
        gbc_weightLimitEditBox.gridx = 0;
        gbc_weightLimitEditBox.gridy = 0;
        weightLimitPanel.add(weightLimitEditBox, gbc_weightLimitEditBox);
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
        GridBagLayout gbl_routeEditPanel = new GridBagLayout();
        routeEditPanel.setLayout(gbl_routeEditPanel);
        javax.swing.JLabel routeInsertLabel = new javax.swing.JLabel();
        GridBagConstraints gbc_routeInsertLabel = new GridBagConstraints();
        gbc_routeInsertLabel.anchor = GridBagConstraints.WEST;
        gbc_routeInsertLabel.insets = new Insets(0, 0, 0, 5);
        gbc_routeInsertLabel.gridx = 0;
        gbc_routeInsertLabel.gridy = 0;
        routeEditPanel.add(routeInsertLabel, gbc_routeInsertLabel);

        routeInsertLabel.setText(ResourceLoader.getString("edit.train.insert.node"));
        fromNodeButton = new javax.swing.JButton();
        GridBagConstraints gbc_fromNodeButton = new GridBagConstraints();
        gbc_fromNodeButton.weightx = 1.0;
        gbc_fromNodeButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_fromNodeButton.anchor = GridBagConstraints.WEST;
        gbc_fromNodeButton.insets = new Insets(0, 0, 0, 5);
        gbc_fromNodeButton.gridx = 1;
        gbc_fromNodeButton.gridy = 0;
        routeEditPanel.add(fromNodeButton, gbc_fromNodeButton);

        fromNodeButton.addActionListener(evt -> fromNodeButtonActionPerformed(evt));
        toNodeButton = new javax.swing.JButton();
        GridBagConstraints gbc_toNodeButton = new GridBagConstraints();
        gbc_toNodeButton.weightx = 1.0;
        gbc_toNodeButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_toNodeButton.anchor = GridBagConstraints.WEST;
        gbc_toNodeButton.insets = new Insets(0, 0, 0, 5);
        gbc_toNodeButton.gridx = 2;
        gbc_toNodeButton.gridy = 0;
        routeEditPanel.add(toNodeButton, gbc_toNodeButton);

        toNodeButton.addActionListener(evt -> toNodeButtonActionPerformed(evt));
        stationsComboBox = new javax.swing.JComboBox<>();
        GridBagConstraints gbc_stationsComboBox = new GridBagConstraints();
        gbc_stationsComboBox.weightx = 1.0;
        gbc_stationsComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_stationsComboBox.anchor = GridBagConstraints.NORTHWEST;
        gbc_stationsComboBox.insets = new Insets(0, 0, 0, 5);
        gbc_stationsComboBox.gridx = 3;
        gbc_stationsComboBox.gridy = 0;
        routeEditPanel.add(stationsComboBox, gbc_stationsComboBox);
        insertButton = new javax.swing.JButton();
        GridBagConstraints gbc_insertButton = new GridBagConstraints();
        gbc_insertButton.anchor = GridBagConstraints.EAST;
        gbc_insertButton.gridx = 4;
        gbc_insertButton.gridy = 0;
        routeEditPanel.add(insertButton, gbc_insertButton);

        insertButton.setText("^");
        insertButton.addActionListener(evt -> insertButtonActionPerformed(evt));
        GridBagLayout gbl_optionsPanel = new GridBagLayout();
        gbl_optionsPanel.columnWeights = new double[]{0.0, 0.0};
        gbl_optionsPanel.rowWeights = new double[]{0.0, 0.0, 0.0};
        optionsPanel.setLayout(gbl_optionsPanel);
        dieselCheckBox = new javax.swing.JCheckBox();
        GridBagConstraints gbc_dieselCheckBox = new GridBagConstraints();
        gbc_dieselCheckBox.anchor = GridBagConstraints.WEST;
        gbc_dieselCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_dieselCheckBox.gridx = 0;
        gbc_dieselCheckBox.gridy = 0;
        optionsPanel.add(dieselCheckBox, gbc_dieselCheckBox);

        dieselCheckBox.setText(ResourceLoader.getString("create.train.diesel")); // NOI18N
        dieselCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        dieselCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        emptyCheckBox = new javax.swing.JCheckBox();
        GridBagConstraints gbc_emptyCheckBox = new GridBagConstraints();
        gbc_emptyCheckBox.weightx = 1.0;
        gbc_emptyCheckBox.anchor = GridBagConstraints.WEST;
        gbc_emptyCheckBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_emptyCheckBox.insets = new Insets(0, 0, 5, 0);
        gbc_emptyCheckBox.gridx = 1;
        gbc_emptyCheckBox.gridy = 0;
        optionsPanel.add(emptyCheckBox, gbc_emptyCheckBox);

        emptyCheckBox.setText(ResourceLoader.getString("create.train.empty")); // NOI18N
        emptyCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        emptyCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        electricCheckBox = new javax.swing.JCheckBox();
        GridBagConstraints gbc_electricCheckBox = new GridBagConstraints();
        gbc_electricCheckBox.anchor = GridBagConstraints.WEST;
        gbc_electricCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_electricCheckBox.gridx = 0;
        gbc_electricCheckBox.gridy = 1;
        optionsPanel.add(electricCheckBox, gbc_electricCheckBox);

        electricCheckBox.setText(ResourceLoader.getString("create.train.electric")); // NOI18N
        electricCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        electricCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        showLengthCheckBox = new javax.swing.JCheckBox();
        GridBagConstraints gbc_showLengthCheckBox = new GridBagConstraints();
        gbc_showLengthCheckBox.insets = new Insets(0, 0, 5, 0);
        gbc_showLengthCheckBox.weightx = 1.0;
        gbc_showLengthCheckBox.anchor = GridBagConstraints.WEST;
        gbc_showLengthCheckBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_showLengthCheckBox.gridx = 1;
        gbc_showLengthCheckBox.gridy = 1;
        optionsPanel.add(showLengthCheckBox, gbc_showLengthCheckBox);

        showLengthCheckBox.setText(ResourceLoader.getString("create.train.show.station.length")); // NOI18N
        showLengthCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        showLengthCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        managedFreightCheckBox = new javax.swing.JCheckBox(ResourceLoader.getString("edit.train.managed.freight")); // NOI18N
        managedFreightCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        managedFreightCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        GridBagConstraints gbc_managedFreightCheckBox = new GridBagConstraints();
        gbc_managedFreightCheckBox.weightx = 1.0;
        gbc_managedFreightCheckBox.anchor = GridBagConstraints.NORTHWEST;
        gbc_managedFreightCheckBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_managedFreightCheckBox.insets = new Insets(0, 0, 5, 5);
        gbc_managedFreightCheckBox.gridx = 0;
        gbc_managedFreightCheckBox.gridy = 2;
        optionsPanel.add(managedFreightCheckBox, gbc_managedFreightCheckBox);

        optionalCheckBox = new javax.swing.JCheckBox(ResourceLoader.getString("edit.train.optional.train")); //NOI18N
        GridBagConstraints gbc_checkBox = new GridBagConstraints();
        optionalCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        optionalCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gbc_checkBox.weightx = 1.0;
        gbc_checkBox.anchor = GridBagConstraints.WEST;
        gbc_checkBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_checkBox.gridx = 1;
        gbc_checkBox.gridy = 2;
        optionsPanel.add(optionalCheckBox, gbc_checkBox);

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
            int maxSpeed = Integer.parseInt(speedTextField.getText());
            if (maxSpeed > 0) {
                // modify top speed
                train.setTopSpeed(maxSpeed);
            } else {
                log.warn("Speed has to be positive number: {}", maxSpeed);
            }
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
    private javax.swing.JButton insertButton;
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
