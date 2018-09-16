/*
 * EditLineDialog.java
 *
 * Created on 30. září 2007, 11:13
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.UnitUtil;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ObjectsUtil;
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

    private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(EditLineDialog.class);

    private static class LTModel extends javax.swing.DefaultListModel<LineTrack> {

        private static final long serialVersionUID = 1L;
    }

    private static final NodeTrack noneTrack = new NodeTrack(null, ResourceLoader.getString("node.track.none"));
    private static final LineClass noneLineClass;

    static {
        noneLineClass = new LineClass(null);
        noneLineClass.setName(ResourceLoader.getString("line.class.none"));
    }

    private Line line;
    private final Map<LineTrack,Tuple<NodeTrack>> connections = new HashMap<>();
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
        TrainDiagram diagram = line.getDiagram();

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
        } else {
            speedTextField.setText(Integer.toString(line.getTopSpeed()));
        }

        lengthEditBox.setValueInUnit(new BigDecimal(line.getLength()), LengthUnit.MM);

        controlledCheckBox.setSelected(Boolean.TRUE.equals(line.getAttribute(Line.ATTR_CONTROLLED, Boolean.class)));

        // update line class combo box
        List<LineClass> classes = line.getDiagram().getNet().getLineClasses();
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

        this.updateTracks();

        this.pack();

        this.setVisible(true);
    }

    private void updateTracks() {
        removed = new LinkedList<>();
        connections.clear();
        LTModel listModel = new LTModel();
        for (LineTrack track : line.getTracks()) {
            listModel.addElement(track);
            connections.put(track, new Tuple<>(track.getFromStraightTrack(), track.getToStraightTrack()));
        }
        trackList.setModel(listModel);
        if (!listModel.isEmpty()) {
            trackList.setSelectedIndex(0);
        }
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
            String speedText = ObjectsUtil.checkAndTrim(speedTextField.getText());
            if (speedText != null) {
                speed = Integer.parseInt(speedTextField.getText());
                if (speed <= 0) {
                    speed = null;
                }
            } else {
                speed = null;
            }
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
                line.getTracks().remove(tbr);
        }
        Object[] els = ((LTModel)trackList.getModel()).toArray();
        for (Object el : els) {
            LineTrack track = (LineTrack)el;
            NodeTrack fromT = connections.get(track).first;
            NodeTrack toT = connections.get(track).second;
            if (fromT != track.getFromStraightTrack())
                track.setFromStraightTrack(fromT);
            if (toT != track.getToStraightTrack())
                track.setToStraightTrack(toT);
            if (!line.getTracks().contains(track))
                line.getTracks().add(track);
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
        speedTextField = new javax.swing.JTextField();
        speedTextField.setColumns(30);
        speedTextField.setHorizontalAlignment(JTextField.RIGHT);
        controlledCheckBox = new javax.swing.JCheckBox();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        fromDirectTrackComboBox = new javax.swing.JComboBox<>();
        toDirectTrackComboBox = new javax.swing.JComboBox<>();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        trackList = new javax.swing.JList<>();
        javax.swing.JButton newTrackButton = GuiComponentUtils.createButton(GuiIcon.ADD, 1);
        javax.swing.JButton deleteTrackButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 1);
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        lineClassComboBox = new javax.swing.JComboBox<>();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        fromToLabel = new javax.swing.JLabel();
        lengthEditBox = new net.parostroj.timetable.gui.components.ValueWithUnitEditBox();
        javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
        lineClassBackComboBox = new javax.swing.JComboBox<>();

        setTitle(ResourceLoader.getString("editline.title")); // NOI18N
        setResizable(false);

        jLabel1.setText(ResourceLoader.getString("editline.length")); // NOI18N

        jLabel2.setText(ResourceLoader.getString("editline.speed")); // NOI18N

        jLabel3.setText(ResourceLoader.getString("editline.direct.from")); // NOI18N

        fromDirectTrackComboBox.addItemListener(this::fromDirectTrackComboBoxItemStateChanged);

        toDirectTrackComboBox.addItemListener(this::toDirectTrackComboBoxItemStateChanged);

        jLabel4.setText(ResourceLoader.getString("editline.direct.to")); // NOI18N

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(this::okButtonActionPerformed);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(this::cancelButtonActionPerformed);

        trackList.setVisibleRowCount(4);
        trackList.addListSelectionListener(this::trackListValueChanged);
        scrollPane.setViewportView(trackList);

        newTrackButton.addActionListener(this::newTrackButtonActionPerformed);

        deleteTrackButton.addActionListener(this::deleteTrackButtonActionPerformed);

        jLabel5.setText(ResourceLoader.getString("editline.tracks")); // NOI18N

        jLabel6.setText(ResourceLoader.getString("editline.type.controlled") + ": "); // NOI18N

        lineClassComboBox.addItemListener(this::lineClassChanged);

        jLabel7.setText(ResourceLoader.getString("editline.lineclass")); // NOI18N

        fromToLabel.setText(" ");

        jLabel9.setText(ResourceLoader.getString("editline.lineclass.back")); // NOI18N

        lineClassBackComboBox.addItemListener(this::lineClassChanged);

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
                                .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(controlledCheckBox)
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

    private void newTrackButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // show dialog with name question
        String name = (String) JOptionPane.showInputDialog(this, "", null, JOptionPane.QUESTION_MESSAGE, null, null, "");
        if (name != null && !name.equals("")) {
            LineTrack track = new LineTrack(IdGenerator.getInstance().getId(), name);
            ((LTModel) trackList.getModel()).addElement(track);
            connections.put(track, new Tuple<NodeTrack>(null, null));
            this.updateSelectedTrack(track);
        }
    }

    private void deleteTrackButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // remove track
        if (!trackList.isSelectionEmpty()) {
            LineTrack deleted = trackList.getSelectedValue();
            if (!deleted.isEmpty() || trackList.getModel().getSize() == 1) {
                JOptionPane.showMessageDialog(this, ResourceLoader.getString("nl.error.notempty"),null,JOptionPane.ERROR_MESSAGE);
            } else {
                ((LTModel) trackList.getModel()).removeElement(deleted);
                removed.add(deleted);
            }
        }
    }

    private void trackListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            LineTrack selected = trackList.getSelectedValue();
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

    private javax.swing.JCheckBox controlledCheckBox;
    private javax.swing.JComboBox<NodeTrack> fromDirectTrackComboBox;
    private javax.swing.JLabel fromToLabel;
    private net.parostroj.timetable.gui.components.ValueWithUnitEditBox lengthEditBox;
    private javax.swing.JComboBox<LineClass> lineClassBackComboBox;
    private javax.swing.JComboBox<LineClass> lineClassComboBox;
    private javax.swing.JTextField speedTextField;
    private javax.swing.JComboBox<NodeTrack> toDirectTrackComboBox;
    private javax.swing.JList<LineTrack> trackList;
}
