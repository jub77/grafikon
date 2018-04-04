package net.parostroj.timetable.gui.dialogs;

import java.util.List;
import java.util.Set;

import net.parostroj.timetable.actions.ElementSort;
import net.parostroj.timetable.actions.NodeComparator;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.utils.ResourceLoader;

import java.awt.BorderLayout;

import javax.swing.border.EmptyBorder;

/**
 * List of nodes for selection.
 *
 * @author jub
 */
public class SelectNodesDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

	private Node selectedNode;

    /** Creates new form SelectNodesDialog */
    public SelectNodesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public void setNodes(Set<Node> nodes) {
        List<Node> sortedNodes = (new ElementSort<Node>(new NodeComparator())).sort(nodes);

        nodesComboBox.removeAllItems();
        for (Node node : sortedNodes) {
            nodesComboBox.addItem(node);
        }
        nodesComboBox.setSelectedIndex(0);
        this.selectedNode = null;
        this.pack();
    }

    public Node getSelectedNode() {
        return selectedNode;
    }

    private void initComponents() {
        nodesPanel = new javax.swing.JPanel();
        nodesPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        nodesComboBox = new javax.swing.JComboBox<Node>();
        javax.swing.JPanel buttonsPanel = new javax.swing.JPanel();
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();

        nodesPanel.setLayout(new BorderLayout(0, 0));

        nodesPanel.add(nodesComboBox, BorderLayout.NORTH);

        getContentPane().add(nodesPanel, java.awt.BorderLayout.CENTER);

        buttonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(evt -> {
            selectedNode = (Node)nodesComboBox.getSelectedItem();
            setVisible(false);
        });
        buttonsPanel.add(okButton);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(evt -> {
            selectedNode = null;
            setVisible(false);
        });
        buttonsPanel.add(cancelButton);

        getContentPane().add(buttonsPanel, java.awt.BorderLayout.SOUTH);

        setResizable(false);
        pack();
    }

    private javax.swing.JComboBox<Node> nodesComboBox;
    private javax.swing.JPanel nodesPanel;
}
