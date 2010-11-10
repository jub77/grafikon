/*
 * EditLineDialog.java
 *
 * Created on 30. září 2007, 11:13
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.event.ItemEvent;
import java.util.*;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Editation of a line.
 * 
 * @author jub
 */
public class EditLineDialog extends javax.swing.JDialog {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EditLineDialog.class.getName());
    private ApplicationModel model;
    private Line line;
    private Map<LineTrack,Tuple<NodeTrack>> connections = new HashMap<LineTrack, Tuple<NodeTrack>>();
    private static final NodeTrack noneTrack = new NodeTrack(null, ResourceLoader.getString("node.track.none"));
    private boolean modified;
    private static final LineClass noneLineClass = new LineClass(null, ResourceLoader.getString("line.class.none"));
    private List<LineTrack> removed;
    
    /** Creates new form EditLineDialog */
    public EditLineDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public void setModel(ApplicationModel model) {
        this.model = model;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
    
    public void setLine(Line line) {
        modified = false;
        this.line = line;

        lengthEditBox.setUnit(model.getProgramSettings().getLengthUnit());
        
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
        
        if (line.getTopSpeed() == Line.UNLIMITED_SPEED) {
            speedTextField.setText("");
            unlimitedSpeedCheckBox.setSelected(true);
            speedTextField.setEditable(false);
        } else {
            unlimitedSpeedCheckBox.setSelected(false);
            speedTextField.setText(Integer.toString(line.getTopSpeed()));
            speedTextField.setEditable(true);
        }
        
        lengthEditBox.setValue(line.getLength());
        
        controlledCheckBox.setSelected(Boolean.TRUE.equals(line.getAttribute("line.controlled")));
        
        // update line class combo box
        List<LineClass> classes = model.getDiagram().getNet().getLineClasses();
        lineClassComboBox.removeAllItems();
        lineClassComboBox.addItem(noneLineClass);
        for (LineClass clazz : classes) {
            lineClassComboBox.addItem(clazz);
        }
        if (line.getAttribute("line.class") == null)
            lineClassComboBox.setSelectedItem(noneLineClass);
        else
            lineClassComboBox.setSelectedItem(line.getAttribute("line.class"));
        
        this.updateTracks(null);

        this.pack();
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
        // recalculate flag
        boolean recalculate = false;
        modified = true;
        int length = lengthEditBox.getValue();
        int speed = line.getTopSpeed();
        try {
            if (!unlimitedSpeedCheckBox.isSelected())
                speed = Integer.parseInt(speedTextField.getText());
            else
                speed = Line.UNLIMITED_SPEED;
        } catch (NumberFormatException e) {
            LOGGER.warn("Cannot convert string to int (speed).", e);
        }
        if (line.getLength() != length && length > 0) {
            line.setLength(length);
            recalculate = true;
        }

        if (line.getTopSpeed() != speed) {
            line.setTopSpeed(speed);
            recalculate = true;
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

        Boolean bool = (Boolean) line.getAttribute("line.controlled");
        if ((bool == null && controlledCheckBox.isSelected()) || (bool != null && controlledCheckBox.isSelected() != bool.booleanValue()))
            line.setAttribute("line.controlled", controlledCheckBox.isSelected());

        // set line class
        if (lineClassComboBox.getSelectedItem() == noneLineClass)
            line.removeAttribute("line.class");
        else {
            if (lineClassComboBox.getSelectedItem() != line.getAttribute("line.class"))
                line.setAttribute("line.class", lineClassComboBox.getSelectedItem());
        }

        if (recalculate) {
            // collect trains
            Set<Train> trains = new HashSet<Train>();
            for (Track track : line.getTracks()) {
                for (TimeInterval interval : track.getTimeIntervalList()) {
                    trains.add(interval.getTrain());
                }
            }
            // recalculate collected trains
            for (Train train : trains) {
                train.recalculate();
                // event
                model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_TRAIN, model, train));
            }
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        unlimitedSpeedCheckBox = new javax.swing.JCheckBox();
        speedTextField = new javax.swing.JTextField();
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
        newTrackButton = new javax.swing.JButton();
        deleteTrackButton = new javax.swing.JButton();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        lineClassComboBox = new javax.swing.JComboBox();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        fromToLabel = new javax.swing.JLabel();
        lengthEditBox = new net.parostroj.timetable.gui.components.LengthEditBox();

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

        newTrackButton.setText(ResourceLoader.getString("button.new")); // NOI18N
        newTrackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newTrackButtonActionPerformed(evt);
            }
        });

        deleteTrackButton.setText(ResourceLoader.getString("button.delete")); // NOI18N
        deleteTrackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteTrackButtonActionPerformed(evt);
            }
        });

        jLabel5.setText(ResourceLoader.getString("editline.tracks")); // NOI18N

        jLabel6.setText(ResourceLoader.getString("editline.type.controlled") + ": "); // NOI18N

        jLabel7.setText(ResourceLoader.getString("editline.lineclass")); // NOI18N

        jLabel8.setText(ResourceLoader.getString("editline.speed.unlimited") + ": "); // NOI18N

        fromToLabel.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6)
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel8)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(controlledCheckBox)
                            .addComponent(unlimitedSpeedCheckBox)
                            .addComponent(speedTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                            .addComponent(fromDirectTrackComboBox, 0, 204, Short.MAX_VALUE)
                            .addComponent(toDirectTrackComboBox, 0, 204, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(deleteTrackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 107, Short.MAX_VALUE)
                                    .addComponent(newTrackButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(lineClassComboBox, 0, 204, Short.MAX_VALUE)
                            .addComponent(lengthEditBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(fromToLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fromToLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(lengthEditBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(unlimitedSpeedCheckBox)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(speedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(controlledCheckBox)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lineClassComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(fromDirectTrackComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(toDirectTrackComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(newTrackButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deleteTrackButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelButton)
                            .addComponent(okButton)))
                    .addComponent(jLabel5))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // do nothing
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // write values back ...
        this.writeValuesBack();
        this.setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void unlimitedSpeedCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unlimitedSpeedCheckBoxActionPerformed
        speedTextField.setEditable(!unlimitedSpeedCheckBox.isSelected());
        if (unlimitedSpeedCheckBox.isSelected()) {
            speedTextField.setText("");
        } else {
            speedTextField.setText(Integer.toString(line.getTopSpeed()));
        }
    }//GEN-LAST:event_unlimitedSpeedCheckBoxActionPerformed

    private void newTrackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newTrackButtonActionPerformed
        // show dialog with name question
        String name = JOptionPane.showInputDialog(this,"");
        if (name != null && !name.equals("")) {
            LineTrack track = new LineTrack(IdGenerator.getInstance().getId(), name);
            ((DefaultListModel)trackList.getModel()).addElement(track);
            connections.put(track, new Tuple<NodeTrack>(null, null));
            this.updateSelectedTrack(track);
        }
}//GEN-LAST:event_newTrackButtonActionPerformed

    private void deleteTrackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteTrackButtonActionPerformed
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
    }//GEN-LAST:event_deleteTrackButtonActionPerformed

    private void trackListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_trackListValueChanged
        if (!evt.getValueIsAdjusting()) {
            LineTrack selected = (LineTrack)trackList.getSelectedValue();
            this.updateSelectedTrack(selected);
        }
    }//GEN-LAST:event_trackListValueChanged

    private void toDirectTrackComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_toDirectTrackComboBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            NodeTrack changed = (NodeTrack)evt.getItem();
            if (changed == noneTrack)
                changed = null;
            Tuple<NodeTrack> tuple = connections.get((LineTrack)trackList.getSelectedValue());
            if (tuple != null)
                tuple.second = changed;
        }
    }//GEN-LAST:event_toDirectTrackComboBoxItemStateChanged

    private void fromDirectTrackComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fromDirectTrackComboBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            NodeTrack changed = (NodeTrack)evt.getItem();
            if (changed == noneTrack)
                changed = null;
            Tuple<NodeTrack> tuple = connections.get((LineTrack)trackList.getSelectedValue());
            if (tuple != null)
                tuple.first = changed;
        }
    }//GEN-LAST:event_fromDirectTrackComboBoxItemStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox controlledCheckBox;
    private javax.swing.JButton deleteTrackButton;
    private javax.swing.JComboBox fromDirectTrackComboBox;
    private javax.swing.JLabel fromToLabel;
    private net.parostroj.timetable.gui.components.LengthEditBox lengthEditBox;
    private javax.swing.JComboBox lineClassComboBox;
    private javax.swing.JButton newTrackButton;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField speedTextField;
    private javax.swing.JComboBox toDirectTrackComboBox;
    private javax.swing.JList trackList;
    private javax.swing.JCheckBox unlimitedSpeedCheckBox;
    // End of variables declaration//GEN-END:variables
    
}
