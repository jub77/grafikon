/*
 * CreateTrainView.java
 *
 * Created on 26. srpen 2007, 12:53
 */
package net.parostroj.timetable.gui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import net.parostroj.timetable.actions.ElementSort;
import net.parostroj.timetable.actions.NodeComparator;
import net.parostroj.timetable.actions.RouteBuilder;
import net.parostroj.timetable.gui.commands.CreateTrainCommand;
import net.parostroj.timetable.gui.components.GroupSelect;
import net.parostroj.timetable.gui.components.GroupsComboBox;
import net.parostroj.timetable.gui.dialogs.ThroughNodesDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.ResourceLoader;

import javax.swing.JCheckBox;

/**
 * View for dialog with creating of the train.
 *
 * @author jub
 */
public class CreateTrainView extends javax.swing.JPanel {

    public static final TrainType NO_TYPE = new TrainType(null, null) {
        @Override
        public String toString() {return "-";}
    };

    private TrainDiagram diagram;
    private CreateTrainCommand createTrainCommand;
    private final ThroughNodesDialog tnDialog;
    private List<Node> throughNodes;

    /**
     * Creates new form CreateTrainView
     */
    public CreateTrainView() {
        initComponents();
        tnDialog = new ThroughNodesDialog(null, true);
    }

    public void setDiagram(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    public void updateView(Group selectedGroup) {
        this.createTrainCommand = null;
        DefaultComboBoxModel<Node> fromModel = new DefaultComboBoxModel<Node>();
        DefaultComboBoxModel<Node> toModel = new DefaultComboBoxModel<Node>();

        Collection<Node> v = diagram.getNet().getNodes();
        ElementSort<Node> sort = new ElementSort<Node>(new NodeComparator());
        List<Node> list = sort.sort(v);

        for (Node node : list) {
            if (node.getType() != NodeType.SIGNAL) {
                fromModel.addElement(node);
                toModel.addElement(node);
            }
        }

        fromComboBox.setModel(fromModel);
        toComboBox.setModel(toModel);

        // model for train types
        typeComboBox.setModel(new DefaultComboBoxModel<TrainType>(diagram.getTrainTypes().toArray(new TrainType[0])));
        typeComboBox.addItem(NO_TYPE);

        // reset through nodes
        throughNodes = new ArrayList<Node>();
        throughTextField.setText(throughNodes.toString());

        // update groups
        groupComboBox.updateGroups(diagram, new GroupSelect(selectedGroup != null ? GroupSelect.Type.GROUP : GroupSelect.Type.NONE, selectedGroup));
    }

    private void initComponents() {
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        fromComboBox = new javax.swing.JComboBox<Node>();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        toComboBox = new javax.swing.JComboBox<Node>();
        speedTextField = new javax.swing.JTextField();
        speedTextField.setColumns(10);
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        stopTextField = new javax.swing.JTextField();
        stopTextField.setColumns(10);
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox<TrainType>();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        startTimeTextField = new javax.swing.JTextField();
        startTimeTextField.setColumns(10);
        commentTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        dieselCheckBox = new javax.swing.JCheckBox();
        electricCheckBox = new javax.swing.JCheckBox();
        javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
        throughButton = new javax.swing.JButton();
        throughTextField = new javax.swing.JTextField();

        jLabel1.setText(ResourceLoader.getString("from.node")); // NOI18N

        jLabel2.setText(ResourceLoader.getString("to.node")); // NOI18N

        jLabel3.setText(ResourceLoader.getString("create.train.speed")); // NOI18N

        jLabel4.setText(ResourceLoader.getString("create.train.stop")); // NOI18N

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jLabel5.setText(ResourceLoader.getString("create.train.number")); // NOI18N

        jLabel6.setText(ResourceLoader.getString("create.train.type")); // NOI18N

        jLabel7.setText(ResourceLoader.getString("create.train.starttime")); // NOI18N

        jLabel8.setText(ResourceLoader.getString("create.train.description")); // NOI18N

        dieselCheckBox.setText(ResourceLoader.getString("create.train.diesel")); // NOI18N
        dieselCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        dieselCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        electricCheckBox.setText(ResourceLoader.getString("create.train.electric")); // NOI18N
        electricCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        electricCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel9.setText(ResourceLoader.getString("create.train.through")); // NOI18N

        throughButton.setText(ResourceLoader.getString("create.train.throughbutton")); // NOI18N
        throughButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                throughButtonActionPerformed(evt);
            }
        });

        throughTextField.setEditable(false);

        groupComboBox = new GroupsComboBox(false);

        JLabel label = new JLabel(ResourceLoader.getString("create.train.group"));

        managedFreightCheckBox = new JCheckBox(ResourceLoader.getString("edit.train.managed.freight"));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(okButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(cancelButton))
                        .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                .addComponent(jLabel8, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9)
                                .addComponent(label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(speedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel7)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(startTimeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addComponent(stopTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(throughTextField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(throughButton))
                                .addComponent(typeComboBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(toComboBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(fromComboBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(commentTextField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(nameTextField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(groupComboBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(managedFreightCheckBox)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(dieselCheckBox)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(electricCheckBox)))))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(typeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(groupComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(managedFreightCheckBox)
                        .addComponent(dieselCheckBox)
                        .addComponent(electricCheckBox))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(commentTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(fromComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(toComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(throughButton)
                        .addComponent(throughTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(speedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(startTimeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(stopTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(cancelButton)
                        .addComponent(okButton))
                    .addContainerGap())
        );
        this.setLayout(layout);
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // test needed values
        try {
            Integer speed = Integer.valueOf(speedTextField.getText());
            if (speed < 1)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this.getParent(), ResourceLoader.getString("create.train.trainspeedmissing"),
                    ResourceLoader.getString("create.train.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nameTextField.getText() == null || nameTextField.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this.getParent(), ResourceLoader.getString("create.train.trainnamemissing"),
                    ResourceLoader.getString("create.train.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (fromComboBox.getSelectedItem() == null || toComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this.getParent(), "",
                    ResourceLoader.getString("create.train.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        RouteBuilder routeBuilder = new RouteBuilder();
        Route route = null;
        if (throughNodes == null)
            route = routeBuilder.createRoute(null, diagram.getNet(), (Node) fromComboBox.getSelectedItem(), (Node) toComboBox.getSelectedItem());
        else {
            List<Node> r = new ArrayList<Node>();
            r.add((Node) fromComboBox.getSelectedItem());
            r.addAll(throughNodes);
            r.add((Node) toComboBox.getSelectedItem());
            route = routeBuilder.createRoute(null, diagram.getNet(), r);
        }

        if (route == null) {
            GuiComponentUtils.showError(ResourceLoader.getString("create.train.createtrainerror"), this.getParent());
            return;
        }

        // get start time
        int start = diagram.getTimeConverter().convertTextToInt(startTimeTextField.getText());
        if (start == -1)
            // midnight if cannot be parsed
            start = 0;

        Group group = groupComboBox.getGroupSelection().getGroup();

        // create command ...
        TrainType tType = (TrainType) typeComboBox.getSelectedItem();
        CreateTrainCommand createCommand = new CreateTrainCommand(
                nameTextField.getText(),
                tType != NO_TYPE ? tType : null,
                        Integer.valueOf(speedTextField.getText()),
                        route,
                        start,
                        (stopTextField.getText().equals("") ? 0 : Integer.valueOf(stopTextField.getText()) * 60),
                        commentTextField.getText(),
                        dieselCheckBox.isSelected(),
                        electricCheckBox.isSelected(), true, group, managedFreightCheckBox.isSelected());
        this.createTrainCommand = createCommand;
        // hide dialog
        this.closeDialog();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // hide dialog
        this.closeDialog();
    }

    public CreateTrainCommand getCreateTrainCommand() {
        return createTrainCommand;
    }

    private void throughButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // show through dialog
        tnDialog.setNodes(throughNodes, diagram.getNet().getNodes());
        tnDialog.setLocationRelativeTo(this);
        tnDialog.setVisible(true);
        throughNodes = tnDialog.getNodes();
        throughTextField.setText(throughNodes.toString());
    }

    private void closeDialog() {
        this.getTopLevelAncestor().setVisible(false);
    }

    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField commentTextField;
    private javax.swing.JCheckBox dieselCheckBox;
    private javax.swing.JCheckBox electricCheckBox;
    private javax.swing.JCheckBox managedFreightCheckBox;
    private javax.swing.JComboBox<Node> fromComboBox;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField speedTextField;
    private javax.swing.JTextField startTimeTextField;
    private javax.swing.JTextField stopTextField;
    private javax.swing.JButton throughButton;
    private javax.swing.JTextField throughTextField;
    private javax.swing.JComboBox<Node> toComboBox;
    private javax.swing.JComboBox<TrainType> typeComboBox;
    private GroupsComboBox groupComboBox;
}
