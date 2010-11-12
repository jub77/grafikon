/*
 * EditNodeDialog.java
 *
 * Created on 28. září 2007, 16:49
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.event.ItemEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import net.parostroj.timetable.gui.views.NodeTypeWrapper;
import net.parostroj.timetable.model.LengthUnit;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.NodeType;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Edit dialog for node.
 *
 * @author jub
 */
public class EditNodeDialog extends javax.swing.JDialog {

    private static class EditTrack {
        public NodeTrack track;
        public String number;
        public Boolean platform;
        public Boolean lineEnd;

        public EditTrack(NodeTrack track) {
            this.track = track;
            this.number = track.getNumber();
            this.platform = track.isPlatform();
            this.lineEnd = Boolean.TRUE.equals(track.getAttribute("line.end"));
        }

        @Override
        public String toString() {
            return number;
        }

        /**
         * writes changed values back to track.
         */
        private void writeValuesBack() {
            if (!number.equals(track.getNumber()))
                track.setNumber(number);
            if (platform != track.isPlatform())
                track.setPlatform(platform);
            Boolean bool = Boolean.TRUE.equals(track.getAttribute("line.end"));
            if (!Boolean.valueOf(lineEnd).equals(bool))
                track.setAttribute("line.end", lineEnd);
        }
    }

    private Node node;
    private List<EditTrack> removed;
    private boolean modified;

    /** Creates new form EditNodeDialog */
    public EditNodeDialog(java.awt.Frame parent) {
        super(parent);
        initComponents();

        // fill combo box
        for (NodeType type : NodeType.values()) {
            typeComboBox.addItem(NodeTypeWrapper.getWrapper(type));
        }
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void setNode(Node node, LengthUnit unit) {
        this.node = node;
        this.modified = false;
        this.lengthEditBox.setUnit(unit);
        this.updateValues();
    }

    private void updateSelectedTrack(EditTrack track) {
        boolean enabled = track != null;
        lineEndCheckBox.setEnabled(enabled);
        platformCheckBox.setEnabled(enabled);
        if (enabled) {
            platformCheckBox.setSelected(track.platform);
            lineEndCheckBox.setSelected(track.lineEnd);
        }
    }

    private void updateValues() {
        removed = new LinkedList<EditTrack>();
        nameTextField.setText(node.getName());
        abbrTextField.setText(node.getAbbr());
        typeComboBox.setSelectedItem(NodeTypeWrapper.getWrapper(node.getType()));
        signalsCheckBox.setSelected("new.signals".equals(node.getAttribute("interlocking.plant")));
        controlCheckBox.setSelected(Boolean.TRUE.equals(node.getAttribute("control.station")));
        trapezoidCheckBox.setSelected(Boolean.TRUE.equals(node.getAttribute("trapezoid.sign")));

        // set node length
        Integer length = (Integer)node.getAttribute("length");
        if (length != null) {
            lengthEditBox.setValue(length);
        } else {
            lengthEditBox.setValue(0);
        }
        lengthCheckBox.setSelected(length != null);
        lengthEditBox.setEnabled(length != null);

        // get node tracks
        DefaultListModel listModel = new DefaultListModel();
        for (NodeTrack track : node.getTracks()) {
            listModel.addElement(new EditTrack(track));
        }
        trackList.setModel(listModel);
    }

    private void writeValuesBack() {
        this.modified = true;
        String newName = nameTextField.getText();
        if (!"".equals(newName) && !newName.equals(node.getName())) {
            node.setName(nameTextField.getText());
        }
        String newAbbr = abbrTextField.getText();
        if (!"".equals(newAbbr) && !newAbbr.equals(node.getAbbr())) {
            node.setAbbr(abbrTextField.getText());
        }
        boolean signalChanged = "new.signals".equals(node.getAttribute("interlocking.plant"));
        if (signalChanged != signalsCheckBox.isSelected())
            if (signalsCheckBox.isSelected()) {
                node.setAttribute("interlocking.plant", "new.signals");
            } else {
                node.removeAttribute("interlocking.plant");
            }
        NodeType newType = ((NodeTypeWrapper) typeComboBox.getSelectedItem()).getType();
        if (node.getType() != newType)
            node.setType(newType);
        Boolean bool = (Boolean) node.getAttribute("control.station");
        if ((bool == null && controlCheckBox.isSelected()) || (bool != null && controlCheckBox.isSelected() != bool.booleanValue()))
            node.setAttribute("control.station", controlCheckBox.isSelected());
        bool = (Boolean) node.getAttribute("trapezoid.sign");
        if ((bool == null && trapezoidCheckBox.isSelected()) || (bool != null && trapezoidCheckBox.isSelected() != bool.booleanValue()))
            node.setAttribute("trapezoid.sign", trapezoidCheckBox.isSelected());

        // length
        if (lengthCheckBox.isSelected()) {
            Integer length = lengthEditBox.getValue();
            Integer oldLength = (Integer) node.getAttribute("length");
            if (!length.equals(oldLength))
                node.setAttribute("length", length);
        } else {
            node.removeAttribute("length");
        }

        // remove removed tracks
        for (EditTrack ret : removed) {
            if (node.getTracks().contains(ret.track))
                node.removeTrack(ret.track);
        }
        // add/modify new/existing
        ListModel m = trackList.getModel();
        for (int i = 0; i < m.getSize(); i++) {
            EditTrack t = (EditTrack) m.getElementAt(i);
            // modify value
            t.writeValuesBack();
            // add new track
            if (!node.getTracks().contains(t.track))
                node.addTrack(t.track);
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
        nameTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        abbrTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox();
        signalsCheckBox = new javax.swing.JCheckBox();
        controlCheckBox = new javax.swing.JCheckBox();
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        trackList = new javax.swing.JList();
        newTrackButton = new javax.swing.JButton();
        renameTrackButton = new javax.swing.JButton();
        deleteTrackButton = new javax.swing.JButton();
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();
        trapezoidCheckBox = new javax.swing.JCheckBox();
        platformCheckBox = new javax.swing.JCheckBox();
        lineEndCheckBox = new javax.swing.JCheckBox();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        lengthEditBox = new net.parostroj.timetable.gui.components.LengthEditBox();
        lengthCheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setModal(true);
        setResizable(false);

        jLabel1.setText(ResourceLoader.getString("ne.name")); // NOI18N

        jLabel2.setText(ResourceLoader.getString("ne.abbr")); // NOI18N

        jLabel3.setText(ResourceLoader.getString("ne.type")); // NOI18N

        typeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                typeComboBoxItemStateChanged(evt);
            }
        });

        signalsCheckBox.setText(ResourceLoader.getString("ne.new.signals")); // NOI18N

        controlCheckBox.setText(ResourceLoader.getString("ne.control.station")); // NOI18N

        trackList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
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

        renameTrackButton.setText(ResourceLoader.getString("button.rename")); // NOI18N
        renameTrackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameTrackButtonActionPerformed(evt);
            }
        });

        deleteTrackButton.setText(ResourceLoader.getString("button.delete")); // NOI18N
        deleteTrackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteTrackButtonActionPerformed(evt);
            }
        });

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

        trapezoidCheckBox.setText(ResourceLoader.getString("ne.trapezoid.table")); // NOI18N

        platformCheckBox.setText(ResourceLoader.getString("ne.platform")); // NOI18N
        platformCheckBox.setEnabled(false);
        platformCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                platformCheckBoxItemStateChanged(evt);
            }
        });

        lineEndCheckBox.setText(ResourceLoader.getString("ne.line.end")); // NOI18N
        lineEndCheckBox.setEnabled(false);
        lineEndCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lineEndCheckBoxItemStateChanged(evt);
            }
        });

        jLabel4.setText(ResourceLoader.getString("ne.length")); // NOI18N

        lengthCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lengthCheckBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(platformCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lineEndCheckBox))
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                    .addComponent(trapezoidCheckBox)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(newTrackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(renameTrackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 112, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteTrackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(signalsCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(controlCheckBox))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(abbrTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                            .addComponent(nameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                            .addComponent(typeComboBox, 0, 261, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lengthEditBox, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lengthCheckBox)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(abbrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(lengthEditBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lengthCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(signalsCheckBox)
                    .addComponent(controlCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(trapezoidCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(platformCheckBox)
                    .addComponent(lineEndCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newTrackButton)
                    .addComponent(renameTrackButton)
                    .addComponent(deleteTrackButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        this.writeValuesBack();
        this.setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void newTrackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newTrackButtonActionPerformed
        String name = JOptionPane.showInputDialog(this, "");
        if (name != null && !name.equals("")) {
            NodeTrack track = new NodeTrack(IdGenerator.getInstance().getId(), name);
            track.setPlatform(true);
            ((DefaultListModel) trackList.getModel()).addElement(new EditTrack(track));
        }
    }//GEN-LAST:event_newTrackButtonActionPerformed

    private void renameTrackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameTrackButtonActionPerformed
        if (!trackList.isSelectionEmpty()) {
            EditTrack track = (EditTrack) trackList.getSelectedValue();
            String name = JOptionPane.showInputDialog(this, "", track.number);
            if (name != null && !name.equals("")) {
                track.number = name;
            }
            trackList.repaint();
        }
    }//GEN-LAST:event_renameTrackButtonActionPerformed

    private void deleteTrackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteTrackButtonActionPerformed
        // removing
        if (!trackList.isSelectionEmpty()) {
            EditTrack track = (EditTrack) trackList.getSelectedValue();
            // test node track
            if (!track.track.isEmpty() || trackList.getModel().getSize() == 1) {
                JOptionPane.showMessageDialog(this, ResourceLoader.getString("nl.error.notempty"), null, JOptionPane.ERROR_MESSAGE);
            } else {
                ((DefaultListModel) trackList.getModel()).removeElement(track);
                // add to removed
                removed.add(track);
            }
        }
    }//GEN-LAST:event_deleteTrackButtonActionPerformed
    private NodeTypeWrapper lastSelectedType;

    private void typeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_typeComboBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            // selected ... (SIGNAL is allowed only if there is only one track)
            if (((NodeTypeWrapper) typeComboBox.getSelectedItem()).getType() == NodeType.SIGNAL) {
                if (trackList.getModel().getSize() != 1) {
                    typeComboBox.setSelectedItem(lastSelectedType);
                }
            }

            boolean signal = (((NodeTypeWrapper) typeComboBox.getSelectedItem()).getType() == NodeType.SIGNAL);
            newTrackButton.setEnabled(!signal);
            renameTrackButton.setEnabled(!signal);
            deleteTrackButton.setEnabled(!signal);
        } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
            lastSelectedType = (NodeTypeWrapper) evt.getItem();
        }
    }//GEN-LAST:event_typeComboBoxItemStateChanged

    private void trackListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_trackListValueChanged
        if (!evt.getValueIsAdjusting()) {
            EditTrack selected = (EditTrack) trackList.getSelectedValue();
            this.updateSelectedTrack(selected);
        }
    }//GEN-LAST:event_trackListValueChanged

    private void platformCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_platformCheckBoxItemStateChanged
        EditTrack selected = (EditTrack)trackList.getSelectedValue();
        if ((evt.getStateChange() == ItemEvent.SELECTED || evt.getStateChange() == ItemEvent.DESELECTED) && selected != null) {
            selected.platform = platformCheckBox.isSelected();
        }
    }//GEN-LAST:event_platformCheckBoxItemStateChanged

    private void lineEndCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lineEndCheckBoxItemStateChanged
        EditTrack selected = (EditTrack) trackList.getSelectedValue();
        if ((evt.getStateChange() == ItemEvent.SELECTED || evt.getStateChange() == ItemEvent.DESELECTED) && selected != null) {
            selected.lineEnd = lineEndCheckBox.isSelected();
        }
    }//GEN-LAST:event_lineEndCheckBoxItemStateChanged

    private void lengthCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lengthCheckBoxItemStateChanged
        lengthEditBox.setEnabled(evt.getStateChange() == ItemEvent.SELECTED);
    }//GEN-LAST:event_lengthCheckBoxItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField abbrTextField;
    private javax.swing.JCheckBox controlCheckBox;
    private javax.swing.JButton deleteTrackButton;
    private javax.swing.JCheckBox lengthCheckBox;
    private net.parostroj.timetable.gui.components.LengthEditBox lengthEditBox;
    private javax.swing.JCheckBox lineEndCheckBox;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton newTrackButton;
    private javax.swing.JCheckBox platformCheckBox;
    private javax.swing.JButton renameTrackButton;
    private javax.swing.JCheckBox signalsCheckBox;
    private javax.swing.JList trackList;
    private javax.swing.JCheckBox trapezoidCheckBox;
    private javax.swing.JComboBox typeComboBox;
    // End of variables declaration//GEN-END:variables
}
