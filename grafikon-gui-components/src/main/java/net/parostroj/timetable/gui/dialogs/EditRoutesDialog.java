package net.parostroj.timetable.gui.dialogs;

import java.util.*;

import net.parostroj.timetable.actions.*;
import net.parostroj.timetable.gui.components.ChangeDocumentListener;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.RouteWrapperDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.ObjectsUtil;

import javax.swing.JPanel;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.BorderLayout;

import javax.swing.border.EmptyBorder;
import javax.swing.Box;
import javax.swing.BoxLayout;

/**
 * Dialog for editing of routes.
 *
 * @author jub
 */
public class EditRoutesDialog extends javax.swing.JDialog {

    private List<Node> throughNodes;
    private final ThroughNodesDialog tnDialog;
    private TrainDiagram diagram;
    private WrapperListModel<Route> routes;

    private static final RouteWrapperDelegate RW_DELEGATE = new RouteWrapperDelegate(RouteWrapperDelegate.Type.FULL_WITH_NET);

    /** Creates new form EditRoutesDialog */
    public EditRoutesDialog(java.awt.Window parent, boolean modal) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
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
        ElementSort<Node> sort = new ElementSort<Node>(new NodeComparator());
        fromComboBox.removeAllItems();
        toComboBox.removeAllItems();
        List<Node> nodes = sort.sort(diagram.getNet().getNodes());
        for (Node n : nodes) {
            if (n.getType() != NodeType.SIGNAL) {
                fromComboBox.addItem(n);
                toComboBox.addItem(n);
            }
        }
        routes = new WrapperListModel<Route>();
        for (Route r : diagram.getRoutes()) {
            routes.addWrapper(new Wrapper<Route>(r, RW_DELEGATE));
        }
        routesList.setModel(routes);

        deleteButton.setEnabled(routesList.getSelectedIndex() != -1);

        throughNodes = new ArrayList<Node>();
        throughTextField.setText(throughNodes.toString());
    }

    private void initComponents() {

        FormListener formListener = new FormListener();

        setTitle(ResourceLoader.getString("edit.routes.title")); // NOI18N
        newButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);
        newButton.setEnabled(false);
        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);

        deleteButton.addActionListener(formListener);

        newButton.addActionListener(formListener);
        getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel scrollPanel = new JPanel();
        scrollPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(scrollPanel, BorderLayout.CENTER);
        scrollPanel.setLayout(new BorderLayout(0, 0));
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        scrollPanel.add(scrollPane, BorderLayout.CENTER);
        routesList = new javax.swing.JList<Wrapper<Route>>();

        scrollPane.setMinimumSize(new java.awt.Dimension(0, 0));
        scrollPane.setPreferredSize(new java.awt.Dimension(300, 150));

        routesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        routesList.addListSelectionListener(formListener);
        scrollPane.setViewportView(routesList);
        JPanel rightPanel = new JPanel();
        rightPanel.setBorder(new EmptyBorder(5, 0, 5, 5));
        getContentPane().add(rightPanel, BorderLayout.EAST);
        GridBagLayout gbl_rightPanel = new GridBagLayout();
        gbl_rightPanel.rowWeights = new double[] { 0, 0, 0, 0, 0, 1.0 };
        rightPanel.setLayout(gbl_rightPanel);

        JPanel settingsPanel = new JPanel();
        GridBagConstraints gbc_settingsPanel = new GridBagConstraints();
        gbc_settingsPanel.gridwidth = 2;
        gbc_settingsPanel.insets = new Insets(0, 0, 5, 0);
        gbc_settingsPanel.fill = GridBagConstraints.BOTH;
        gbc_settingsPanel.gridx = 0;
        gbc_settingsPanel.gridy = 2;
        rightPanel.add(settingsPanel, gbc_settingsPanel);
        settingsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        throughButton = GuiComponentUtils.createButton(GuiIcon.DARROW_RIGHT, 2);
        settingsPanel.add(throughButton);

        settingsPanel.add(Box.createHorizontalStrut(3));
        netPartCheckBox = new javax.swing.JCheckBox();
        settingsPanel.add(netPartCheckBox);

        netPartCheckBox.setText(ResourceLoader.getString("edit.routes.net.part"));
        throughButton.addActionListener(formListener);
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        GridBagConstraints gbc_jLabel2 = new GridBagConstraints();
        gbc_jLabel2.anchor = GridBagConstraints.WEST;
        gbc_jLabel2.insets = new Insets(0, 0, 5, 5);
        gbc_jLabel2.gridx = 0;
        gbc_jLabel2.gridy = 1;
        rightPanel.add(jLabel2, gbc_jLabel2);

        jLabel2.setText(ResourceLoader.getString("to.node"));
        fromComboBox = new javax.swing.JComboBox<Node>();
        GridBagConstraints gbc_fromComboBox = new GridBagConstraints();
        gbc_fromComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_fromComboBox.anchor = GridBagConstraints.WEST;
        gbc_fromComboBox.insets = new Insets(0, 0, 5, 0);
        gbc_fromComboBox.gridx = 1;
        gbc_fromComboBox.gridy = 0;
        rightPanel.add(fromComboBox, gbc_fromComboBox);
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        GridBagConstraints gbc_jLabel1 = new GridBagConstraints();
        gbc_jLabel1.anchor = GridBagConstraints.WEST;
        gbc_jLabel1.insets = new Insets(0, 0, 5, 5);
        gbc_jLabel1.gridx = 0;
        gbc_jLabel1.gridy = 0;
        rightPanel.add(jLabel1, gbc_jLabel1);

        jLabel1.setText(ResourceLoader.getString("from.node"));

        JPanel namePanel = new JPanel();
        GridBagConstraints gbc_namePanel = new GridBagConstraints();
        gbc_namePanel.gridwidth = 2;
        gbc_namePanel.insets = new Insets(0, 0, 5, 0);
        gbc_namePanel.fill = GridBagConstraints.BOTH;
        gbc_namePanel.gridx = 0;
        gbc_namePanel.gridy = 4;
        rightPanel.add(namePanel, gbc_namePanel);
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        namePanel.add(jLabel3);

        jLabel3.setText(ResourceLoader.getString("edit.routes.routename"));

        namePanel.add(Box.createHorizontalStrut(3));
        routeNameTextField = new javax.swing.JTextField();
        routeNameTextField.setAlignmentX(1.0f);
        namePanel.add(routeNameTextField);
        routeNameTextField.getDocument().addDocumentListener(new ChangeDocumentListener() {
            @Override
            protected void change() {
                String text = ObjectsUtil.checkAndTrim(routeNameTextField.getText());
                newButton.setEnabled(text != null);
            }
        });

        JPanel buttonPanel = new JPanel();
        GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
        gbc_buttonPanel.gridwidth = 2;
        gbc_buttonPanel.anchor = GridBagConstraints.NORTHWEST;
        gbc_buttonPanel.gridx = 0;
        gbc_buttonPanel.gridy = 5;
        rightPanel.add(buttonPanel, gbc_buttonPanel);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.add(newButton);
        buttonPanel.add(Box.createHorizontalStrut(3));
        buttonPanel.add(deleteButton);
        throughTextField = new javax.swing.JTextField();
        GridBagConstraints gbc_throughTextField = new GridBagConstraints();
        gbc_throughTextField.gridwidth = 2;
        gbc_throughTextField.anchor = GridBagConstraints.WEST;
        gbc_throughTextField.insets = new Insets(0, 0, 5, 0);
        gbc_throughTextField.gridx = 0;
        gbc_throughTextField.gridy = 3;
        rightPanel.add(throughTextField, gbc_throughTextField);
        throughTextField.setColumns(20);

        throughTextField.setEditable(false);
        toComboBox = new javax.swing.JComboBox<Node>();
        GridBagConstraints gbc_toComboBox = new GridBagConstraints();
        gbc_toComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_toComboBox.insets = new Insets(0, 0, 5, 0);
        gbc_toComboBox.anchor = GridBagConstraints.WEST;
        gbc_toComboBox.gridx = 1;
        gbc_toComboBox.gridy = 1;
        rightPanel.add(toComboBox, gbc_toComboBox);

        pack();
        setMinimumSize(getSize());
    }

    private class FormListener implements java.awt.event.ActionListener, javax.swing.event.ListSelectionListener {
        FormListener() {
        }

        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == newButton) {
                EditRoutesDialog.this.newButtonActionPerformed(evt);
            } else if (evt.getSource() == deleteButton) {
                EditRoutesDialog.this.deleteButtonActionPerformed(evt);
            } else if (evt.getSource() == throughButton) {
                EditRoutesDialog.this.throughButtonActionPerformed(evt);
            }
        }

        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
            if (evt.getSource() == routesList) {
                EditRoutesDialog.this.routesListValueChanged(evt);
            }
        }
    }

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {
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
        if (newRoute == null || newRoute.checkDuplicateNodes()) {
            GuiComponentUtils.showError(ResourceLoader.getString("dialog.error.incorrect.values"), this);
            return;
        }
        // set name
        if (routeNameTextField.getText() != null && !"".equals(routeNameTextField.getText())) {
            newRoute.setName(routeNameTextField.getText());
        }
        // net part
        newRoute.setNetPart(netPartCheckBox.isSelected());
        routes.addWrapper(new Wrapper<Route>(newRoute, RW_DELEGATE));
        int newIndex = routes.getIndexOfObject(newRoute);
        routesList.setSelectedIndex(newIndex);
        routesList.ensureIndexIsVisible(newIndex);
        diagram.getRoutes().add(newRoute);
        // clear name
        routeNameTextField.setText("");
        netPartCheckBox.setSelected(false);
        // clear through option
        throughNodes = new ArrayList<Node>();
        throughTextField.setText(throughNodes.toString());
    }

    private void routesListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            deleteButton.setEnabled(routesList.getSelectedIndex() != -1);
        }
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // delete route
        int index = routesList.getSelectedIndex();
        Wrapper<Route> deletedRoute = routes.removeIndex(index);
        diagram.getRoutes().remove(deletedRoute.getElement());
        routesList.setSelectedIndex(index >= routes.getSize() ? routes.getSize() - 1 : index);
    }

    private void throughButtonActionPerformed(java.awt.event.ActionEvent evt) {
        tnDialog.setNodes(throughNodes, diagram.getNet().getNodes());
        tnDialog.setLocationRelativeTo(this);
        tnDialog.setVisible(true);

        // copy values back
        throughNodes = tnDialog.getNodes();
        throughTextField.setText(throughNodes.toString());
    }

    private javax.swing.JButton deleteButton;
    private javax.swing.JComboBox<Node> fromComboBox;
    private javax.swing.JCheckBox netPartCheckBox;
    private javax.swing.JButton newButton;
    private javax.swing.JTextField routeNameTextField;
    private javax.swing.JList<Wrapper<Route>> routesList;
    private javax.swing.JButton throughButton;
    private javax.swing.JTextField throughTextField;
    private javax.swing.JComboBox<Node> toComboBox;
}
