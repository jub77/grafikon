/*
 * EditLineDialog.java
 *
 * Created on 30. září 2007, 11:13
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.util.*;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.UnitUtil;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.GroupLayout;

/**
 * Editation of a line.
 *
 * @author jub
 */
public class EditLineDialog extends javax.swing.JDialog {

    private static final Logger log = LoggerFactory.getLogger(EditLineDialog.class);

    private Line line;
    private final Map<LineTrack,Tuple<NodeTrack>> connections = new HashMap<LineTrack, Tuple<NodeTrack>>();
    private static final NodeTrack noneTrack = new NodeTrack(null, ResourceLoader.getString("node.track.none"));
    private static final LineClass noneLineClass = new LineClass(null, ResourceLoader.getString("line.class.none"));
    private List<LineTrack> removed;

    /** Creates new form EditLineDialog */
    public EditLineDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // set units
        lengthEditBox.setUnits(LengthUnit.getScaleDependent());
    }

    public void showDialog(Line line, LengthUnit lengthUnit) {
        this.line = line;
        TrainDiagram diagram = line.getTrainDiagram();

        lengthEditBox.setUnit(diagram.getAttributes().get(TrainDiagram.ATTR_EDIT_LENGTH_UNIT, LengthUnit.class, lengthUnit));

        // update track for from and to (direct)
        Node from = line.getFrom();
        Node to = line.getTo();
        fromToLabel.setText(from.getName() + " - " + to.getName());

        fromDirectTrackComboBox.removeAllItems();
        toDirectTrackComboBox.removeAllItems();
        fromDirectTrackComboBox.addItem(noneTrack);
        toDirectTrackComboBox.addItem(noneTrack);

        for (NodeTrack track : from.getTracks()) {
            fromDirectTrackComboBox.addItem(track);
        }

        for (NodeTrack track : to.getTracks()) {
            toDirectTrackComboBox.addItem(track);
        }

        if (line.getTopSpeed() == null) {
            speedTextField.setText("");
            unlimitedSpeedCheckBox.setSelected(true);
            speedTextField.setEditable(false);
        } else {
            unlimitedSpeedCheckBox.setSelected(false);
            speedTextField.setText(Integer.toString(line.getTopSpeed()));
            speedTextField.setEditable(true);
        }

        lengthEditBox.setValueInUnit(new BigDecimal(line.getLength()), LengthUnit.MM);

        controlledCheckBox.setSelected(Boolean.TRUE.equals(line.getAttribute(Line.ATTR_CONTROLLED, Boolean.class)));

        // update line class combo box
        List<LineClass> classes = line.getTrainDiagram().getNet().getLineClasses();
        lineClassComboBox.removeAllItems();
        lineClassComboBox.addItem(noneLineClass);
        for (LineClass clazz : classes) {
            lineClassComboBox.addItem(clazz);
        }
        if (line.getAttribute(Line.ATTR_CLASS, LineClass.class) == null)
            lineClassComboBox.setSelectedItem(noneLineClass);
        else
            lineClassComboBox.setSelectedItem(line.getAttribute(Line.ATTR_CLASS, LineClass.class));

        // update line class back combo box
        lineClassBackComboBox.removeAllItems();
        lineClassBackComboBox.addItem(noneLineClass);
        for (LineClass clazz : classes)
            lineClassBackComboBox.addItem(clazz);
        if (line.getAttribute(Line.ATTR_CLASS_BACK, LineClass.class) == null)
            lineClassBackComboBox.setSelectedItem(lineClassComboBox.getSelectedItem());
        else
            lineClassBackComboBox.setSelectedItem(line.getAttribute(Line.ATTR_CLASS_BACK, LineClass.class));

        this.updateTracks(null);

        this.pack();

        this.setVisible(true);
    }

    private void updateTracks(LineTrack selected) {
        removed = new LinkedList<LineTrack>();
        connections.clear();
        DefaultListModel listModel = new DefaultListModel();
        for (LineTrack track : line.getTracks()) {
            listModel.addElement(track);
            connections.put(track, new Tuple<NodeTrack>(track.getFromStraightTrack(), track.getToStraightTrack()));
        }
        trackList.setModel(listModel);
        this.updateSelectedTrack(selected);
    }

    private void updateSelectedTrack(LineTrack selected) {
        // set selected
        if (selected != null) {
            trackList.setSelectedValue(selected, true);
        }

        // set from and to track for each track
        if (selected != null) {
            fromDirectTrackComboBox.setEnabled(true);
            toDirectTrackComboBox.setEnabled(true);
            if (connections.get(selected).first == null)
                fromDirectTrackComboBox.setSelectedItem(noneTrack);
            else
                fromDirectTrackComboBox.setSelectedItem(connections.get(selected).first);
            if (connections.get(selected).second == null)
                toDirectTrackComboBox.setSelectedItem(noneTrack);
            else
                toDirectTrackComboBox.setSelectedItem(connections.get(selected).second);
        } else {
            fromDirectTrackComboBox.setEnabled(false);
            toDirectTrackComboBox.setEnabled(false);
            fromDirectTrackComboBox.setSelectedItem(noneTrack);
            toDirectTrackComboBox.setSelectedItem(noneTrack);
        }
    }

    private void writeValuesBack() {
        int length = line.getLength();
        try {
            length = UnitUtil.convert(lengthEditBox.getValueInUnit(LengthUnit.MM));
        } catch (ArithmeticException e) {
            log.warn("Value overflow: {}", lengthEditBox.getValueInUnit(LengthUnit.MM));
        }
        Integer speed = line.getTopSpeed();
        try {
            if (!unlimitedSpeedCheckBox.isSelected())
                speed = Integer.parseInt(speedTextField.getText());
            else
                speed = null;
        } catch (NumberFormatException e) {
            log.warn("Cannot convert string to int (speed).", e);
        }
        if (line.getLength() != length && length > 0) {
            line.setLength(length);
        }

        if (line.getTopSpeed() != speed) {
            line.setTopSpeed(speed);
        }

        // update line tracks back - remove
        for (LineTrack tbr : removed) {
            if (line.getTracks().contains(tbr))
                line.removeTrack(tbr);
        }
        Object[] els = ((DefaultListModel)trackList.getModel()).toArray();
        for (Object el : els) {
            LineTrack track = (LineTrack)el;
            NodeTrack fromT = connections.get(track).first;
            NodeTrack toT = connections.get(track).second;
            if (fromT != track.getFromStraightTrack())
                track.setFromStraightTrack(fromT);
            if (toT != track.getToStraightTrack())
                track.setToStraightTrack(toT);
            if (!line.getTracks().contains(track))
                line.addTrack(track);
        }

        Boolean bool = line.getAttribute(Line.ATTR_CONTROLLED, Boolean.class);
        if ((bool == null && controlledCheckBox.isSelected()) || (bool != null && controlledCheckBox.isSelected() != bool.booleanValue()))
            line.setAttribute(Line.ATTR_CONTROLLED, controlledCheckBox.isSelected());

        // set line class
        line.getAttributes().setRemove(Line.ATTR_CLASS, lineClassComboBox.getSelectedItem() == noneLineClass ? null : lineClassComboBox.getSelectedItem());
        // set line class back
        line.getAttributes().setRemove(Line.ATTR_CLASS_BACK, lineClassBackComboBox.getSelectedItem() == lineClassComboBox.getSelectedItem()
                        || lineClassBackComboBox.getSelectedItem() == noneLineClass ? null : lineClassBackComboBox.getSelectedItem());
    }

    private void initComponents() {
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        unlimitedSpeedCheckBox = new javax.swing.JCheckBox();
        speedTextField = new javax.swing.JTextField();
        speedTextField.setColumns(30);
        controlledCheckBox = new javax.swing.JCheckBox();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        fromDirectTrackComboBox = new javax.swing.JComboBox();
        toDirectTrackComboBox = new javax.swing.JComboBox();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        trackList = new javax.swing.JList();
        newTrackButton = GuiComponentUtils.createButton(GuiIcon.ADD, 1);
        deleteTrackButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 1);
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        lineClassComboBox = new javax.swing.JComboBox();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        fromToLabel = new javax.swing.JLabel();
        lengthEditBox = new net.parostroj.timetable.gui.components.ValueWithUnitEditBox();
        javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
        lineClassBackComboBox = new javax.swing.JComboBox();

        setTitle(ResourceLoader.getString("editline.title")); // NOI18N
        setResizable(false);

        jLabel1.setText(ResourceLoader.getString("editline.length")); // NOI18N

        unlimitedSpeedCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unlimitedSpeedCheckBoxActionPerformed(evt);
            }
        });

        jLabel2.setText(ResourceLoader.getString("editline.speed")); // NOI18N

        jLabel3.setText(ResourceLoader.getString("editline.direct.from")); // NOI18N

        fromDirectTrackComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fromDirectTrackComboBoxItemStateChanged(evt);
            }
        });

        toDirectTrackComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toDirectTrackComboBoxItemStateChanged(evt);
            }
        });

        jLabel4.setText(ResourceLoader.getString("editline.direct.to")); // NOI18N

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

        trackList.setVisibleRowCount(4);
        trackList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                trackListValueChanged(evt);
            }
        });
        scrollPane.setViewportView(trackList);

        newTrackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newTrackButtonActionPerformed(evt);
            }
        });

        deleteTrackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteTrackButtonActionPerformed(evt);
            }
        });

        jLabel5.setText(ResourceLoader.getString("editline.tracks")); // NOI18N

        jLabel6.setText(ResourceLoader.getString("editline.type.controlled") + ": "); // NOI18N

        lineClassComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lineClassChanged(evt);
            }
        });

        jLabel7.setText(ResourceLoader.getString("editline.lineclass")); // NOI18N

        jLabel8.setText(ResourceLoader.getString("editline.speed.unlimited") + ": "); // NOI18N

        fromToLabel.setText(" ");

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("gt_texts"); // NOI18N
        jLabel9.setText(bundle.getString("editline.lineclass.back")); // NOI18N

        lineClassBackComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lineClassChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(okButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(cancelButton))
                        .addComponent(fromToLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(jLabel9, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                    .addComponent(jLabel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(jLabel8)
                                .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(controlledCheckBox)
                                .addComponent(unlimitedSpeedCheckBox)
                                .addComponent(speedTextField, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                                .addComponent(fromDirectTrackComboBox, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                                .addComponent(toDirectTrackComboBox, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                        .addComponent(deleteTrackButton)
                                        .addComponent(newTrackButton)))
                                .addComponent(lineClassComboBox, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                                .addComponent(lengthEditBox, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                                .addComponent(lineClassBackComboBox, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(fromToLabel)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.CENTER)
                        .addComponent(jLabel1)
                        .addComponent(lengthEditBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.CENTER)
                        .addComponent(unlimitedSpeedCheckBox)
                        .addComponent(jLabel8))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(speedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.CENTER)
                        .addComponent(controlledCheckBox)
                        .addComponent(jLabel6))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lineClassComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(lineClassBackComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(fromDirectTrackComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(toDirectTrackComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(newTrackButton)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(deleteTrackButton)))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(cancelButton)
                                .addComponent(okButton)))
                        .addComponent(jLabel5))
                    .addContainerGap())
        );
        getContentPane().setLayout(layout);

        pack();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // do nothing
        this.setVisible(false);
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // write values back ...
        this.writeValuesBack();
        this.setVisible(false);
    }

    private void unlimitedSpeedCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
        speedTextField.setEditable(!unlimitedSpeedCheckBox.isSelected());
        if (unlimitedSpeedCheckBox.isSelected()) {
            speedTextField.setText("");
        } else {
            speedTextField.setText(Integer.toString(line.getTopSpeed()));
        }
    }

    private void newTrackButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // show dialog with name question
        String name = (String) JOptionPane.showInputDialog(this, "", null, JOptionPane.QUESTION_MESSAGE, null, null, "");
        if (name != null && !name.equals("")) {
            LineTrack track = new LineTrack(IdGenerator.getInstance().getId(), name);
            ((DefaultListModel)trackList.getModel()).addElement(track);
            connections.put(track, new Tuple<NodeTrack>(null, null));
            this.updateSelectedTrack(track);
        }
    }

    private void deleteTrackButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // remove track
        if (!trackList.isSelectionEmpty()) {
            LineTrack deleted = (LineTrack)trackList.getSelectedValue();
            if (!deleted.isEmpty() || trackList.getModel().getSize() == 1) {
                JOptionPane.showMessageDialog(this, ResourceLoader.getString("nl.error.notempty"),null,JOptionPane.ERROR_MESSAGE);
            } else {
                ((DefaultListModel)trackList.getModel()).removeElement(deleted);
                removed.add(deleted);
            }
        }
    }

    private void trackListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            LineTrack selected = (LineTrack)trackList.getSelectedValue();
            this.updateSelectedTrack(selected);
        }
    }

    private void toDirectTrackComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            NodeTrack changed = (NodeTrack)evt.getItem();
            if (changed == noneTrack)
                changed = null;
            Tuple<NodeTrack> tuple = connections.get(trackList.getSelectedValue());
            if (tuple != null)
                tuple.second = changed;
        }
    }

    private void fromDirectTrackComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            NodeTrack changed = (NodeTrack)evt.getItem();
            if (changed == noneTrack)
                changed = null;
            Tuple<NodeTrack> tuple = connections.get(trackList.getSelectedValue());
            if (tuple != null)
                tuple.first = changed;
        }
    }

    private void lineClassChanged(java.awt.event.ItemEvent evt) {
        // check consistency
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            Object lc = lineClassComboBox.getSelectedItem();
            Object lcb = lineClassBackComboBox.getSelectedItem();
            Object nlc = lc;
            Object nlcb = lcb;

            if (lc == noneLineClass) {
                nlc = noneLineClass;
                nlcb = noneLineClass;
            } else if (lc != noneLineClass && lcb == noneLineClass) {
                nlc = lc;
                nlcb = lc;
            }
            if (lc != nlc)
                lineClassComboBox.setSelectedItem(nlc);
            if (lcb != nlcb)
                lineClassBackComboBox.setSelectedItem(nlcb);
        }
    }

    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox controlledCheckBox;
    private javax.swing.JButton deleteTrackButton;
    private javax.swing.JComboBox fromDirectTrackComboBox;
    private javax.swing.JLabel fromToLabel;
    private net.parostroj.timetable.gui.components.ValueWithUnitEditBox lengthEditBox;
    private javax.swing.JComboBox lineClassBackComboBox;
    private javax.swing.JComboBox lineClassComboBox;
    private javax.swing.JButton newTrackButton;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField speedTextField;
    private javax.swing.JComboBox toDirectTrackComboBox;
    private javax.swing.JList trackList;
    private javax.swing.JCheckBox unlimitedSpeedCheckBox;
}
