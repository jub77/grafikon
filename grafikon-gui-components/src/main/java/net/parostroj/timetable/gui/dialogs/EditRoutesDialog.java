package net.parostroj.timetable.gui.dialogs;

import java.util.*;
import javax.swing.DefaultListModel;
import net.parostroj.timetable.actions.NodeSort;
import net.parostroj.timetable.actions.RouteBuilder;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.*;

/**
 * Dialog for editing of routes.
 * 
 * @author jub
 */
public class EditRoutesDialog extends javax.swing.JDialog {

    private List<Node> throughNodes;
    private ThroughNodesDialog tnDialog;
    private TrainDiagram diagram;

    /** Creates new form EditRoutesDialog */
    public EditRoutesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        tnDialog = new ThroughNodesDialog(null, true);
    }
    
    public void showDialog(TrainDiagram diagram) {
        if (diagram == null)
            throw new IllegalArgumentException("Diagram cannot be null");
        this.diagram = diagram;
        this.updateValues();
        this.setVisible(true);
    }

    private void updateValues() {
        // update list of nodes
        NodeSort sort = new NodeSort(NodeSort.Type.ASC);
        fromComboBox.removeAllItems();
        toComboBox.removeAllItems();
        List<Node> nodes = sort.sort(diagram.getNet().getNodes());
        for (Node n : nodes) {
            if (n.getType() != NodeType.SIGNAL) {
                fromComboBox.addItem(n);
                toComboBox.addItem(n);
            }
        }
        // update list of routes
        routesList.setModel(new DefaultListModel());
        for (Route r : diagram.getRoutes()) {
            ((DefaultListModel) routesList.getModel()).addElement(new Wrapper<Route>(r));
        }

        deleteButton.setEnabled(routesList.getSelectedIndex() != -1);

        throughNodes = new ArrayList<Node>();
        throughTextField.setText(throughNodes.toString());

        this.pack();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        routesList = new javax.swing.JList();
        fromComboBox = new javax.swing.JComboBox();
        toComboBox = new javax.swing.JComboBox();
        newButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        throughButton = new javax.swing.JButton();
        throughTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        routeNameTextField = new javax.swing.JTextField();
        netPartCheckBox = new javax.swing.JCheckBox();

        FormListener formListener = new FormListener();

        setTitle(ResourceLoader.getString("edit.routes.title")); // NOI18N

        scrollPane.setMinimumSize(new java.awt.Dimension(0, 0));
        scrollPane.setPreferredSize(new java.awt.Dimension(200, 0));

        routesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        routesList.addListSelectionListener(formListener);
        scrollPane.setViewportView(routesList);

        newButton.setText(ResourceLoader.getString("button.new")); // NOI18N
        newButton.addActionListener(formListener);

        deleteButton.setText(ResourceLoader.getString("button.delete")); // NOI18N
        deleteButton.addActionListener(formListener);

        exitButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        exitButton.addActionListener(formListener);

        jLabel1.setText(ResourceLoader.getString("from.node")); // NOI18N

        jLabel2.setText(ResourceLoader.getString("to.node")); // NOI18N

        throughButton.setText(ResourceLoader.getString("edit.routes.through")); // NOI18N
        throughButton.addActionListener(formListener);

        throughTextField.setEditable(false);

        jLabel3.setText(ResourceLoader.getString("edit.routes.routename")); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("net/parostroj/timetable/gui/components_texts"); // NOI18N
        netPartCheckBox.setText(bundle.getString("edit.routes.net.part")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(exitButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(fromComboBox, 0, 140, Short.MAX_VALUE)
                            .addComponent(toComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 140, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(throughButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(throughTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(routeNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(netPartCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newButton, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(fromComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(toComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(routeNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(throughButton)
                            .addComponent(throughTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(netPartCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(exitButton)))
                .addContainerGap())
        );

        pack();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, javax.swing.event.ListSelectionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == newButton) {
                EditRoutesDialog.this.newButtonActionPerformed(evt);
            }
            else if (evt.getSource() == deleteButton) {
                EditRoutesDialog.this.deleteButtonActionPerformed(evt);
            }
            else if (evt.getSource() == exitButton) {
                EditRoutesDialog.this.exitButtonActionPerformed(evt);
            }
            else if (evt.getSource() == throughButton) {
                EditRoutesDialog.this.throughButtonActionPerformed(evt);
            }
        }

        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
            if (evt.getSource() == routesList) {
                EditRoutesDialog.this.routesListValueChanged(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_exitButtonActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        if (fromComboBox.getSelectedItem() == toComboBox.getSelectedItem()) {
            return;
        }
        RouteBuilder builder = new RouteBuilder();
        Route newRoute = null;
        List<Node> nodes = new LinkedList<Node>();
        nodes.add((Node) fromComboBox.getSelectedItem());
        nodes.addAll(throughNodes);
        nodes.add((Node) toComboBox.getSelectedItem());
        newRoute = builder.createRoute(UUID.randomUUID().toString(), diagram.getNet(), nodes);
        // do not create route with duplicate nodes
        if (newRoute.checkDuplicateNodes()) {
            return;
        }
        // set name
        if (routeNameTextField.getText() != null && !"".equals(routeNameTextField.getText())) {
            newRoute.setName(routeNameTextField.getText());
        }
        // net part
        newRoute.setNetPart(netPartCheckBox.isSelected());
        ((DefaultListModel) routesList.getModel()).addElement(new Wrapper<Route>(newRoute));
        diagram.addRoute(newRoute);
        // clear name
        routeNameTextField.setText("");
        netPartCheckBox.setSelected(false);
        // clear through option
        throughNodes = new ArrayList<Node>();
        throughTextField.setText(throughNodes.toString());
    }//GEN-LAST:event_newButtonActionPerformed

    private void routesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_routesListValueChanged
        if (!evt.getValueIsAdjusting()) {
            deleteButton.setEnabled(routesList.getSelectedIndex() != -1);
        }
    }//GEN-LAST:event_routesListValueChanged

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // delete route
        Route deletedRoute = (Route)((Wrapper<?>) routesList.getSelectedValue()).getElement();
        diagram.removeRoute(deletedRoute);
        ((DefaultListModel) routesList.getModel()).remove(routesList.getSelectedIndex());
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void throughButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_throughButtonActionPerformed
        tnDialog.setNodes(throughNodes, diagram.getNet().getNodes());
        tnDialog.setLocationRelativeTo(this);
        tnDialog.setVisible(true);

        // copy values back
        throughNodes = tnDialog.getNodes();
        throughTextField.setText(throughNodes.toString());
    }//GEN-LAST:event_throughButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton exitButton;
    private javax.swing.JComboBox fromComboBox;
    private javax.swing.JCheckBox netPartCheckBox;
    private javax.swing.JButton newButton;
    private javax.swing.JTextField routeNameTextField;
    private javax.swing.JList routesList;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JButton throughButton;
    private javax.swing.JTextField throughTextField;
    private javax.swing.JComboBox toComboBox;
    // End of variables declaration//GEN-END:variables
}
