/*
 * EditNodeDialog.java
 *
 * Created on 28. září 2007, 16:49
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.util.*;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListModel;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.wrappers.NodeTypeWrapperDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.UnitUtil;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.GroupLayout;
import javax.swing.JPanel;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.awt.Component;
import java.awt.BorderLayout;

/**
 * Edit dialog for node.
 *
 * @author jub
 */
public class EditNodeDialog extends javax.swing.JDialog {

    private static final Logger LOG = LoggerFactory.getLogger(EditNodeDialog.class);

    private static Region NONE_REGION = new Region(null, "-");

    private static class EditTrack {
        public NodeTrack track;
        public String number;
        public boolean platform;
        public boolean lineEnd;

        public EditTrack(NodeTrack track) {
            this.track = track;
            this.number = track.getNumber();
            this.platform = track.isPlatform();
            this.lineEnd = track.getAttributes().getBool(Track.ATTR_LINE_END);
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
            track.getAttributes().setBool(Track.ATTR_LINE_END, lineEnd);
        }
    }

    private Node node;
    private List<EditTrack> removed;
    private Collection<FreightColor> colors;
    private final WrapperListModel<NodeType> types;

    /** Creates new form EditNodeDialog */
    public EditNodeDialog(java.awt.Frame parent) {
        super(parent);
        initComponents();

        types = new WrapperListModel<NodeType>(false);

        // fill combo box
        NodeTypeWrapperDelegate delegate = new NodeTypeWrapperDelegate();
        for (NodeType type : NodeType.values()) {
            types.addWrapper(Wrapper.getWrapper(type, delegate));
        }
        typeComboBox.setModel(types);

        // set units
        lengthEditBox.setUnits(LengthUnit.getScaleDependent());
        lengthCheckBox = new javax.swing.JCheckBox();
        lengthPanel.add(lengthCheckBox, BorderLayout.EAST);

                lengthCheckBox.addItemListener(new java.awt.event.ItemListener() {
                    public void itemStateChanged(java.awt.event.ItemEvent evt) {
                        lengthCheckBoxItemStateChanged(evt);
                    }
                });
    }

    public void showDialog(Node node, LengthUnit unit) {
        this.node = node;
        this.lengthEditBox.setUnit(unit);
        this.updateValues();
        this.setVisible(true);
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
        types.setSelectedObject(node.getType());
        signalsCheckBox.setSelected(Node.IP_NEW_SIGNALS.equals(node.getAttribute(Node.ATTR_INTERLOCKING_PLANT)));
        controlCheckBox.setSelected(Boolean.TRUE.equals(node.getAttribute(Node.ATTR_CONTROL_STATION)));
        trapezoidCheckBox.setSelected(Boolean.TRUE.equals(node.getAttribute(Node.ATTR_TRAPEZOID_SIGN)));
        updateColors();

        regionComboBox.removeAllItems();
        // add current regions
        regionComboBox.addItem(NONE_REGION);
        for (Region region : node.getTrainDiagram().getNet().getRegions().get()) {
            regionComboBox.addItem(region);
        }
        // select region ...
        Region region = node.getAttributes().get(Node.ATTR_REGION, Region.class);
        regionComboBox.setSelectedItem(region == null ? NONE_REGION : region);

        // set node length
        Integer length = (Integer) node.getAttribute(Node.ATTR_LENGTH);
        if (length != null) {
            lengthEditBox.setValueInUnit(new BigDecimal(length), LengthUnit.MM);
        } else {
            lengthEditBox.setValue(new BigDecimal(0));
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

    @SuppressWarnings("unchecked")
    private void updateColors() {
        colors = (Collection<FreightColor>) node.getAttribute(Node.ATTR_FREIGHT_COLORS);
        if (colors == null) {
            colors = Collections.emptyList();
        }
    }

    private void writeValuesBack() {
        String newName = nameTextField.getText();
        if (!"".equals(newName) && !newName.equals(node.getName())) {
            node.setName(nameTextField.getText());
        }
        String newAbbr = abbrTextField.getText();
        if (!"".equals(newAbbr) && !newAbbr.equals(node.getAbbr())) {
            node.setAbbr(abbrTextField.getText());
        }

        node.getAttributes().setRemove(Node.ATTR_INTERLOCKING_PLANT, signalsCheckBox.isSelected() ? Node.IP_NEW_SIGNALS : null);

        NodeType newType = types.getSelectedObject();
        if (node.getType() != newType)
            node.setType(newType);

        node.getAttributes().setBool(Node.ATTR_CONTROL_STATION, controlCheckBox.isSelected());
        node.getAttributes().setBool(Node.ATTR_TRAPEZOID_SIGN, trapezoidCheckBox.isSelected());

        // length
        if (lengthCheckBox.isSelected()) {
            try {
                Integer length = UnitUtil.convert(lengthEditBox.getValueInUnit(LengthUnit.MM));
                Integer oldLength = (Integer) node.getAttribute(Node.ATTR_LENGTH);
                if (!length.equals(oldLength))
                    node.setAttribute(Node.ATTR_LENGTH, length);
            } catch (ArithmeticException e) {
                LOG.warn("Value overflow: {}", lengthEditBox.getValueInUnit(LengthUnit.MM));
                LOG.warn(e.getMessage());
            }
        } else {
            node.removeAttribute(Node.ATTR_LENGTH);
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

        // colors
        if (colors.isEmpty()) {
            colors = null;
        }
        node.getAttributes().setRemove(Node.ATTR_FREIGHT_COLORS, colors);

        // region
        Region region = (Region) regionComboBox.getSelectedItem();
        if (region == NONE_REGION) {
            region = null;
        }
        node.getAttributes().setRemove(Node.ATTR_REGION, region);
    }

    private void initComponents() {
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        nameTextField.setColumns(40);
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        abbrTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox();
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        trackList = new javax.swing.JList();
        newTrackButton = GuiComponentUtils.createButton(GuiIcon.ADD, 1);
        renameTrackButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 1);
        deleteTrackButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 1);
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();
        platformCheckBox = new javax.swing.JCheckBox();
        lineEndCheckBox = new javax.swing.JCheckBox();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();

        regionComboBox = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setModal(true);
        setResizable(false);

        jLabel1.setText(ResourceLoader.getString("ne.name")); // NOI18N

        jLabel2.setText(ResourceLoader.getString("ne.abbr")); // NOI18N

        jLabel3.setText(ResourceLoader.getString("ne.type")); // NOI18N

        jLabel5.setText(ResourceLoader.getString("ne.region") + ":"); // NOI18N

        typeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                typeComboBoxItemStateChanged(evt);
            }
        });

        trackList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
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

        renameTrackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameTrackButtonActionPerformed(evt);
            }
        });

        deleteTrackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteTrackButtonActionPerformed(evt);
            }
        });

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeValuesBack();
                setVisible(false);
            }
        });

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setVisible(false);
            }
        });

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

        JPanel panel = new JPanel();

        colorsButton = new JButton(ResourceLoader.getString("ne.colors") + "...");
        colorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FreightColorsDialog dialog = new FreightColorsDialog(EditNodeDialog.this);
                dialog.setLocationRelativeTo(EditNodeDialog.this);
                List<FreightColor> result = dialog.showDialog(colors);
                if (result != null) {
                    colors = result;
                }
                dialog.dispose();
            }
        });

        lengthPanel = new JPanel();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(platformCheckBox)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lineEndCheckBox))
                        .addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(panel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(newTrackButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(renameTrackButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(deleteTrackButton)
                            .addGap(18)
                            .addComponent(colorsButton)
                            .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                            .addComponent(okButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(cancelButton))
                        .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jLabel5, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(regionComboBox, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                        .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(nameTextField, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                        .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(abbrTextField, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                        .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lengthPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                        .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(typeComboBox, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(abbrTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(regionComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(typeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(7)
                    .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lengthPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addGap(10)))
                    .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(platformCheckBox)
                        .addComponent(lineEndCheckBox))
                    .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(newTrackButton)
                            .addComponent(renameTrackButton)
                            .addComponent(deleteTrackButton))
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(cancelButton)
                            .addComponent(okButton)
                            .addComponent(colorsButton)))
                    .addContainerGap())
        );
        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5});
        lengthPanel.setLayout(new BorderLayout(0, 0));
        lengthEditBox = new net.parostroj.timetable.gui.components.ValueWithUnitEditBox();
        lengthPanel.add(lengthEditBox, BorderLayout.CENTER);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
        signalsCheckBox = new javax.swing.JCheckBox();
        panel.add(signalsCheckBox);

        signalsCheckBox.setText(ResourceLoader.getString("ne.new.signals"));
        trapezoidCheckBox = new javax.swing.JCheckBox();
        panel.add(trapezoidCheckBox);

        trapezoidCheckBox.setText(ResourceLoader.getString("ne.trapezoid.table"));
        controlCheckBox = new javax.swing.JCheckBox();
        panel.add(controlCheckBox);

        controlCheckBox.setText(ResourceLoader.getString("ne.control.station"));
        getContentPane().setLayout(layout);

        pack();
    }

    private void newTrackButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String name = (String) JOptionPane.showInputDialog(this, "", null, JOptionPane.QUESTION_MESSAGE, null, null, "");
        if (name != null && !name.equals("")) {
            NodeTrack track = new NodeTrack(IdGenerator.getInstance().getId(), name);
            track.setPlatform(true);
            ((DefaultListModel) trackList.getModel()).addElement(new EditTrack(track));
        }
    }

    private void renameTrackButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (!trackList.isSelectionEmpty()) {
            EditTrack track = (EditTrack) trackList.getSelectedValue();
            String name = (String) JOptionPane.showInputDialog(this, "", null, JOptionPane.QUESTION_MESSAGE, null, null, track.number);
            if (name != null && !name.equals("")) {
                track.number = name;
            }
            trackList.repaint();
        }
    }

    private void deleteTrackButtonActionPerformed(java.awt.event.ActionEvent evt) {
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
    }

    private Wrapper<?> lastSelectedType;

    private void typeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            // selected ... (SIGNAL is allowed only if there is only one track)
            if (types.getSelectedObject() == NodeType.SIGNAL) {
                if (trackList.getModel().getSize() != 1) {
                    typeComboBox.setSelectedItem(lastSelectedType);
                }
            }

            boolean signal = types.getSelectedObject() == NodeType.SIGNAL;
            newTrackButton.setEnabled(!signal);
            renameTrackButton.setEnabled(!signal);
            deleteTrackButton.setEnabled(!signal);
        } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
            lastSelectedType = (Wrapper<?>) evt.getItem();
        }
    }

    private void trackListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            EditTrack selected = (EditTrack) trackList.getSelectedValue();
            this.updateSelectedTrack(selected);
        }
    }

    private void platformCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        EditTrack selected = (EditTrack)trackList.getSelectedValue();
        if ((evt.getStateChange() == ItemEvent.SELECTED || evt.getStateChange() == ItemEvent.DESELECTED) && selected != null) {
            selected.platform = platformCheckBox.isSelected();
        }
    }

    private void lineEndCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        EditTrack selected = (EditTrack) trackList.getSelectedValue();
        if ((evt.getStateChange() == ItemEvent.SELECTED || evt.getStateChange() == ItemEvent.DESELECTED) && selected != null) {
            selected.lineEnd = lineEndCheckBox.isSelected();
        }
    }

    private void lengthCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        lengthEditBox.setEnabled(evt.getStateChange() == ItemEvent.SELECTED);
    }

    private javax.swing.JTextField abbrTextField;
    private javax.swing.JCheckBox controlCheckBox;
    private javax.swing.JButton deleteTrackButton;
    private final javax.swing.JCheckBox lengthCheckBox;
    private net.parostroj.timetable.gui.components.ValueWithUnitEditBox lengthEditBox;
    private javax.swing.JCheckBox lineEndCheckBox;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton newTrackButton;
    private javax.swing.JCheckBox platformCheckBox;
    private javax.swing.JButton renameTrackButton;
    private javax.swing.JCheckBox signalsCheckBox;
    private javax.swing.JList trackList;
    private javax.swing.JCheckBox trapezoidCheckBox;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.JComboBox regionComboBox;
    private JButton colorsButton;
    private JPanel lengthPanel;
}
