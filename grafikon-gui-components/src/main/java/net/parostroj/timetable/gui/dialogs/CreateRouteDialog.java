package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import net.parostroj.timetable.actions.ElementSort;
import net.parostroj.timetable.actions.NodeComparator;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.RouteSegment;
import net.parostroj.timetable.model.TrainDiagram;

public class CreateRouteDialog extends JDialog {

    private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
    private JTextField throughTextField;
    private ThroughNodesDialog tnDialog;
    private List<Node> throughNodes;
    private List<Node> availableNodes;
    private JComboBox<Wrapper<Node>> fromComboBox;
    private JComboBox<Wrapper<Node>> toComboBox;
    private boolean selected;

    /**
     * Create the dialog.
     */
    public CreateRouteDialog() {
        setModal(true);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
        contentPanel.setLayout(gbl_contentPanel);
        {
            fromComboBox = new JComboBox<Wrapper<Node>>();
            GridBagConstraints gbc_fromComboBox = new GridBagConstraints();
            gbc_fromComboBox.gridwidth = 2;
            gbc_fromComboBox.insets = new Insets(0, 0, 5, 0);
            gbc_fromComboBox.fill = GridBagConstraints.HORIZONTAL;
            gbc_fromComboBox.gridx = 0;
            gbc_fromComboBox.gridy = 0;
            contentPanel.add(fromComboBox, gbc_fromComboBox);
        }
        {
            toComboBox = new JComboBox<Wrapper<Node>>();
            GridBagConstraints gbc_toComboBox = new GridBagConstraints();
            gbc_toComboBox.gridwidth = 2;
            gbc_toComboBox.insets = new Insets(0, 0, 5, 0);
            gbc_toComboBox.fill = GridBagConstraints.HORIZONTAL;
            gbc_toComboBox.gridx = 0;
            gbc_toComboBox.gridy = 1;
            contentPanel.add(toComboBox, gbc_toComboBox);
        }
        {
            throughTextField = new JTextField();
            GridBagConstraints gbc_textField = new GridBagConstraints();
            gbc_textField.insets = new Insets(0, 0, 0, 5);
            gbc_textField.fill = GridBagConstraints.HORIZONTAL;
            gbc_textField.gridx = 0;
            gbc_textField.gridy = 2;
            contentPanel.add(throughTextField, gbc_textField);
            throughTextField.setColumns(10);
            throughTextField.setEditable(false);
        }
        {
            JButton throughButton = GuiComponentUtils.createButton(GuiIcon.DARROW_RIGHT, 2);
            GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
            gbc_btnNewButton.gridx = 1;
            gbc_btnNewButton.gridy = 2;
            throughButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ThroughNodesDialog dialog = getThroughDialog();
                    dialog.setNodes(throughNodes, availableNodes);
                    dialog.setLocationRelativeTo(CreateRouteDialog.this);
                    dialog.setVisible(true);
                    throughNodes = dialog.getNodes();
                    throughTextField.setText(throughNodes.toString());
                }
            });
            contentPanel.add(throughButton, gbc_btnNewButton);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton(ResourceLoader.getString("button.ok"));
                buttonPane.add(okButton);
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selected = true;
                        setVisible(false);
                    }
                });
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton(ResourceLoader.getString("button.cancel"));
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selected = false;
                        setVisible(false);
                    }
                });
                buttonPane.add(cancelButton);
            }
        }
        this.pack();
    }

    private ThroughNodesDialog getThroughDialog() {
        if (tnDialog == null) {
            tnDialog = new ThroughNodesDialog(null, true);
        }
        return tnDialog;
    }

    private void setValues(TrainDiagram diagram, List<? extends RouteSegment<?>> route) {
        ElementSort<Node> sort = new ElementSort<Node>(new NodeComparator());
        availableNodes = sort.sort(diagram.getNet().getNodes());
        fromComboBox.removeAllItems();
        toComboBox.removeAllItems();
        for (Wrapper<Node> w : Wrapper.getWrapperList(availableNodes)) {
            fromComboBox.addItem(w);
            toComboBox.addItem(w);
        }
        throughNodes = new LinkedList<Node>();
        if (route != null && route.size() >= 2) {
            fromComboBox.setSelectedItem(Wrapper.getWrapper(route.get(0)));
            toComboBox.setSelectedItem(Wrapper.getWrapper(route.get(route.size() - 1)));
            for (int i = 1; i < route.size() - 1; i++) {
                RouteSegment<?> segment = route.get(i);
                if (segment.isNode()) {
                    throughNodes.add(segment.asNode());
                }
            }
        }
        throughTextField.setText(throughNodes.toString());
    }

    private List<Node> createResult() {
        List<Node> result = new LinkedList<Node>();
        result.add((Node) ((Wrapper<?>) fromComboBox.getSelectedItem()).getElement());
        result.addAll(throughNodes);
        result.add((Node) ((Wrapper<?>) toComboBox.getSelectedItem()).getElement());
        return result;
    }

    public List<Node> showDialog(TrainDiagram diagram, List<? extends RouteSegment<?>> route) {
        this.setValues(diagram, route);
        this.pack();
        this.setVisible(true);
        if (selected) {
            return createResult();
        } else {
            return null;
        }
    }
}
