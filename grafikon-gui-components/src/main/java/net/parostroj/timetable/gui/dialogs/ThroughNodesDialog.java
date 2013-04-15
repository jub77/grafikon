package net.parostroj.timetable.gui.dialogs;

import java.util.*;
import javax.swing.DefaultListModel;
import net.parostroj.timetable.actions.NodeSort;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Node;

/**
 * Dialog that allows to create list of nodes.
 *
 * @author jub
 */
public class ThroughNodesDialog extends javax.swing.JDialog {

    private List<Node> nodes;

    /** Creates new form ThroughNodesDialog */
    public ThroughNodesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /**
     * sets list of nodes.
     *
     * @param nodes list of nodes
     */
    public void setNodes(List<Node> nodes, Collection<Node> allNodes) {
        this.nodes = nodes;

        // update values for nodes
        NodeSort sort = new NodeSort(NodeSort.Type.ASC);
        List<Node> sorted = sort.sort(allNodes);
        nodeComboBox.removeAllItems();
        for (Node node : sorted) {
            nodeComboBox.addItem(node);
        }

        // update list
        DefaultListModel m = new DefaultListModel();
        for (Node n : nodes) {
            m.addElement(n);
        }
        nodeList.setModel(m);

        this.pack();
    }

    /**
     * @return list of nodes
     */
    public List<Node> getNodes() {
        return nodes;
    }

    private void initComponents() {
        scrollPane = new javax.swing.JScrollPane();
        nodeList = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        nodeComboBox = new javax.swing.JComboBox();
        okButton = new javax.swing.JButton();

        nodeList.setPrototypeCellValue("mmmmmmmmmmmmmmmmmmmm");
        scrollPane.setViewportView(nodeList);

        addButton.setText(ResourceLoader.getString("ted.add")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText(ResourceLoader.getString("ted.remove")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        nodeComboBox.setPrototypeDisplayValue("mmmmmmmmmmmmmm");

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                    .addComponent(removeButton, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                    .addComponent(nodeComboBox, 0, 117, Short.MAX_VALUE)
                    .addComponent(okButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(nodeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)))
                .addContainerGap())
        );

        pack();
    }

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (nodeComboBox.getSelectedItem() != null) {
            ((DefaultListModel)nodeList.getModel()).addElement(nodeComboBox.getSelectedItem());
            nodeList.setSelectedIndex(nodeList.getModel().getSize() - 1);
        }
    }

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (!nodeList.isSelectionEmpty()) {
            int index = nodeList.getSelectedIndex();
            ((DefaultListModel)nodeList.getModel()).remove(index);
            if (index >= nodeList.getModel().getSize())
                index--;
            nodeList.setSelectedIndex(index);
        }
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // copy list of nodes back to nodes
        Enumeration<?> e = ((DefaultListModel)nodeList.getModel()).elements();
        nodes = new ArrayList<Node>();
        while (e.hasMoreElements()) {
            nodes.add((Node)e.nextElement());
        }
        this.setVisible(false);
    }

    private javax.swing.JButton addButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox nodeComboBox;
    private javax.swing.JList nodeList;
    private javax.swing.JButton okButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JScrollPane scrollPane;
}
