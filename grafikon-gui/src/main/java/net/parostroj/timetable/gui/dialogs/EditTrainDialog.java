/*
 * EditTrainDialog.java
 *
 * Created on 18. září 2007, 14:47
 */
package net.parostroj.timetable.gui.dialogs;

import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.components.GroupsComboBox;
import net.parostroj.timetable.gui.views.CreateTrainView;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.GroupLayout;
import javax.swing.JLabel;

/**
 * Dialog for editation of train properties.
 *
 * @author jub
 */
public class EditTrainDialog extends javax.swing.JDialog {

    private static final Logger LOG = LoggerFactory.getLogger(EditTrainDialog.class.getName());
    private static final String FROM_STATION = "${stations.first}";
    private static final String TO_STATION = "${stations.last}";
    private static final String STATION_X = "${stations.get(%d)}";

    public ApplicationModel model;

    /**
     * Creates new form EditTrainDialog.
     *
     * @param parent
     * @param modal
     */
    public EditTrainDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        routeEditBox.setLanguages(Arrays.asList(TextTemplate.Language.values()));
    }

    /**
     * fills the dialog with train data.
     */
    public void getSelectedTrainData() {
        if (model != null && model.getSelectedTrain() != null)  {
            Train train = model.getSelectedTrain();
            // model for train types
            typeComboBox.setModel(new DefaultComboBoxModel(model.getDiagram().getTrainTypes().toArray()));
            typeComboBox.addItem(CreateTrainView.NO_TYPE);
            typeComboBox.setSelectedItem(train.getType() != null ? train.getType() : CreateTrainView.NO_TYPE);
            dieselCheckBox.setSelected((Boolean) train.getAttribute(Train.ATTR_DIESEL));
            electricCheckBox.setSelected((Boolean) train.getAttribute(Train.ATTR_ELECTRIC));
            showLengthCheckBox.setSelected(Boolean.TRUE.equals(train.getAttribute(Train.ATTR_SHOW_STATION_LENGTH)));
            emptyCheckBox.setSelected(Boolean.TRUE.equals(train.getAttribute(Train.ATTR_EMPTY)));

            numberTextField.setText(train.getNumber());

            descriptionTextField.setText(train.getDescription());
            speedTextField.setText(Integer.toString(train.getTopSpeed()));
            Integer weight = (Integer) train.getAttribute(Train.ATTR_WEIGHT);
            weightTextField.setText(weight != null ? weight.toString() : "");
            routeEditBox.setTemplate((TextTemplate) train.getAttribute(Train.ATTR_ROUTE));

            fromNodeButton.setText(((Node) train.getFirstInterval().getOwner()).getName());
            toNodeButton.setText(((Node) train.getLastInterval().getOwner()).getName());

            stationsComboBox.removeAllItems();
            for (TimeInterval i : train.getTimeIntervalList()) {
                if (i.isNodeOwner())
                    stationsComboBox.addItem(i.getOwner());
            }

            timeBeforeTextField.setText(Integer.toString(train.getTimeBefore() / 60));
            timeAfterTextField.setText(Integer.toString(train.getTimeAfter() / 60));

            groupsComboBox.updateGroups(model.getDiagram(), train.getAttributes().get(Train.ATTR_GROUP, Group.class));
        }
        pack();
        setMinimumSize(getSize());
    }

    public void setModel(ApplicationModel model) {
        this.model = model;
    }

    private void initComponents() {
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox();
        dieselCheckBox = new javax.swing.JCheckBox();
        electricCheckBox = new javax.swing.JCheckBox();
        showLengthCheckBox = new javax.swing.JCheckBox();
        emptyCheckBox = new javax.swing.JCheckBox();
        numberTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        descriptionTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        speedTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        weightTextField = new javax.swing.JTextField();
        routeEditBox = new net.parostroj.timetable.gui.components.TextTemplateEditBox();
        fromNodeButton = new javax.swing.JButton();
        toNodeButton = new javax.swing.JButton();
        stationsComboBox = new javax.swing.JComboBox();
        insertButton = new javax.swing.JButton();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        timeBeforeTextField = new javax.swing.JTextField();
        timeAfterTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();
        javax.swing.JLabel jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(ResourceLoader.getString("edit.train")); // NOI18N

        jLabel1.setText(ResourceLoader.getString("create.train.type")); // NOI18N

        dieselCheckBox.setText(ResourceLoader.getString("create.train.diesel")); // NOI18N
        dieselCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        dieselCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        electricCheckBox.setText(ResourceLoader.getString("create.train.electric")); // NOI18N
        electricCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        electricCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        showLengthCheckBox.setText(ResourceLoader.getString("create.train.show.station.length")); // NOI18N
        showLengthCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        showLengthCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        emptyCheckBox.setText(ResourceLoader.getString("create.train.empty")); // NOI18N
        emptyCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        emptyCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        numberTextField.setColumns(20);

        jLabel2.setText(ResourceLoader.getString("create.train.number")); // NOI18N

        descriptionTextField.setColumns(30);

        jLabel3.setText(ResourceLoader.getString("create.train.description")); // NOI18N

        speedTextField.setColumns(10);

        jLabel4.setText(ResourceLoader.getString("create.train.speed")); // NOI18N

        fromNodeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromNodeButtonActionPerformed(evt);
            }
        });

        toNodeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toNodeButtonActionPerformed(evt);
            }
        });

        insertButton.setText("^");
        insertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertButtonActionPerformed(evt);
            }
        });

        jLabel5.setText(ResourceLoader.getString("edit.train.weight")); // NOI18N

        jLabel6.setText(ResourceLoader.getString("edit.train.route")); // NOI18N

        timeBeforeTextField.setColumns(6);

        timeAfterTextField.setColumns(6);

        jLabel7.setText(ResourceLoader.getString("create.train.technological.time")); // NOI18N

        jLabel8.setText(ResourceLoader.getString("create.train.time.before")); // NOI18N

        jLabel9.setText(ResourceLoader.getString("create.train.time.after")); // NOI18N

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel10.setText(ResourceLoader.getString("edit.train.insert.node")); // NOI18N

        groupsComboBox = new GroupsComboBox(false);

        JLabel lblNewLabel = new JLabel(ResourceLoader.getString("create.train.group"));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(okButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(cancelButton))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                                    .addComponent(jLabel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(lblNewLabel))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel10)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(fromNodeButton)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(toNodeButton)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(stationsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(insertButton))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(dieselCheckBox)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(electricCheckBox))
                                .addComponent(emptyCheckBox)
                                .addComponent(descriptionTextField)
                                .addComponent(speedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(weightTextField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(showLengthCheckBox)
                                .addComponent(numberTextField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(typeComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(routeEditBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(groupsComboBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(jLabel8)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(timeBeforeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(jLabel9)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(timeAfterTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(typeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(dieselCheckBox)
                        .addComponent(electricCheckBox))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(showLengthCheckBox)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(emptyCheckBox)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(groupsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblNewLabel))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(numberTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(descriptionTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(speedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(weightTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(jLabel6)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(routeEditBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(toNodeButton)
                                    .addComponent(stationsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(insertButton))
                                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(fromNodeButton)
                                    .addComponent(jLabel10)))))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jLabel8)
                        .addComponent(timeBeforeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(timeAfterTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(cancelButton)
                        .addComponent(okButton))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        getContentPane().setLayout(layout);

        pack();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        Train train = model.getSelectedTrain();
        // set values to train ...
        TrainType type = (TrainType) typeComboBox.getSelectedItem();
        train.setType(type != CreateTrainView.NO_TYPE ? type : null);
        if (!train.getAttribute(Train.ATTR_DIESEL).equals(dieselCheckBox.isSelected())) {
            train.setAttribute(Train.ATTR_DIESEL, dieselCheckBox.isSelected());
        }
        if (!train.getAttribute(Train.ATTR_ELECTRIC).equals(electricCheckBox.isSelected())) {
            train.setAttribute(Train.ATTR_ELECTRIC, electricCheckBox.isSelected());
        }
        train.getAttributes().setBool(Train.ATTR_SHOW_STATION_LENGTH, showLengthCheckBox.isSelected());
        train.getAttributes().setBool(Train.ATTR_EMPTY, emptyCheckBox.isSelected());
        if (!numberTextField.getText().equals(train.getNumber())) {
            train.setNumber(numberTextField.getText());
        }
        if (!descriptionTextField.getText().equals(train.getDescription())) {
            train.setDescription(descriptionTextField.getText());
        }
        Group sGroup = groupsComboBox.getGroupSelection().getGroup();
        train.getAttributes().setRemove(Train.ATTR_GROUP, sGroup);

        // weight
        Integer oldWI = (Integer) train.getAttribute(Train.ATTR_WEIGHT);
        Integer newWI = null;
        try {
            String weightStr = weightTextField.getText().trim();
            if (!"".equals(weightStr))
                newWI = Integer.valueOf(weightStr);
        } catch (NumberFormatException e) {
            LOG.warn("Couldn't convert weight to int.");
            newWI = oldWI;
        }
        train.getAttributes().setRemove(Train.ATTR_WEIGHT, newWI);

        // route
        try {
            TextTemplate newRI = routeEditBox.getTemplateEmpty();
            train.getAttributes().setRemove(Train.ATTR_ROUTE, newRI);
        } catch (GrafikonException e) {
            LOG.warn("Error creating template: {}", e.getMessage());
        }

        // check max speed - modify if changed
        try {
            int maxSpeed = Integer.parseInt(speedTextField.getText());
            if (maxSpeed != train.getTopSpeed() && maxSpeed > 0) {
                // modify top speed
                train.setTopSpeed(maxSpeed);
            }
            if (maxSpeed <= 0)
                LOG.warn("Speed has to be positive number: {}", maxSpeed);
        } catch (NumberFormatException e) {
            LOG.warn("Cannot convert speed to number: {}", speedTextField.getText());
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
            LOG.warn("Cannot convert technological time: {}, {}", timeBeforeTextField.getText(), timeAfterTextField.getText());
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
    private javax.swing.JButton fromNodeButton;
    private javax.swing.JButton insertButton;
    private javax.swing.JTextField numberTextField;
    private net.parostroj.timetable.gui.components.TextTemplateEditBox routeEditBox;
    private javax.swing.JCheckBox showLengthCheckBox;
    private javax.swing.JTextField speedTextField;
    private javax.swing.JComboBox stationsComboBox;
    private javax.swing.JTextField timeAfterTextField;
    private javax.swing.JTextField timeBeforeTextField;
    private javax.swing.JButton toNodeButton;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.JTextField weightTextField;
    private GroupsComboBox groupsComboBox;
}
