package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.parostroj.timetable.gui.components.ElementSelectionPanel;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.FNConnection;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrainDiagram;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.border.EtchedBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Edit dialog for freight net connections.
 *
 * @author jub
 */
public class EditFNConnetionDialog extends javax.swing.JDialog {

    private static final Logger log = LoggerFactory.getLogger(EditFNConnetionDialog.class);

    private final ElementSelectionPanel<Node> selectionPanel;
    private FNConnection connection;
    private final JTextField transLimitTextField;
    private final JCheckBox transLimitCheckBox;

    public EditFNConnetionDialog(Window parent, boolean modal) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        getContentPane().setLayout(new BorderLayout(0, 0));
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        JButton okButton = new JButton(ResourceLoader.getString("button.ok"));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writeValuesBack();
                setVisible(false);
            }
        });
        panel.add(okButton);

        JButton cancelButton = new JButton(ResourceLoader.getString("button.cancel"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        panel.add(cancelButton);

        JPanel editPanel = new JPanel();
        getContentPane().add(editPanel, BorderLayout.CENTER);
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));

        JPanel stopNodesPanel = new JPanel();
        editPanel.add(stopNodesPanel);
        stopNodesPanel.setBorder(new TitledBorder(null, ResourceLoader.getString("edit.fnc.last.nodes"),
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        stopNodesPanel.setLayout(new BorderLayout(0, 0));

        selectionPanel = new ElementSelectionPanel<Node>();
        stopNodesPanel.add(selectionPanel, BorderLayout.CENTER);

        JPanel transLimitPanel = new JPanel();
        FlowLayout fl_transLimitPanel = (FlowLayout) transLimitPanel.getLayout();
        fl_transLimitPanel.setAlignment(FlowLayout.LEFT);
        transLimitPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "TransLimit",
                TitledBorder.LEFT, TitledBorder.TOP, null, null));
        editPanel.add(transLimitPanel);

        transLimitCheckBox = new JCheckBox((String) null);
        transLimitPanel.add(transLimitCheckBox);
        transLimitCheckBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    transLimitTextField.setEnabled(false);
                    transLimitTextField.setText("");
                } else {
                    transLimitTextField.setEnabled(true);
                    transLimitTextField.setText("0");
                }
            }
        });

        JLabel limitLabel = new JLabel("Limit");
        transLimitPanel.add(limitLabel);

        transLimitTextField = new JTextField();
        transLimitTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        transLimitPanel.add(transLimitTextField);
        transLimitTextField.setColumns(5);
        transLimitTextField.setEnabled(false);
    }

    public void edit(Component center, FNConnection connection, TrainDiagram diagram) {
        this.connection = connection;

        // add nodes and data
        updateValues(diagram);

        pack();
        setLocationRelativeTo(center);
        setVisible(true);
    }

    private void updateValues(TrainDiagram diagram) {
        // last nodes
        selectionPanel.setListForSelection(Wrapper.getWrapperList(diagram.getNet().getNodes()));
        List<?> lastNodes = connection.get(FNConnection.ATTR_LAST_NODES, List.class);
        if (lastNodes != null) {
            for (Object node : lastNodes) {
                selectionPanel.addSelected(Wrapper.getWrapper((Node) node));
            }
        }
        // transition limit
        Integer transLimit = connection.get(FNConnection.ATTR_TRANSITION_LIMIT, Integer.class);
        transLimitCheckBox.setSelected(transLimit != null);
        transLimitTextField.setText(transLimit != null ? transLimit.toString() : "");
    }

    private void writeValuesBack() {
        // last nodes
        List<Wrapper<Node>> selectedList = selectionPanel.getSelectedList();
        List<Node> list = Wrapper.unwrap(selectedList);
        if (list.isEmpty()) {
            list = null;
        }
        connection.set(FNConnection.ATTR_LAST_NODES, list);
        // transition limit
        Integer transLimit = null;
        if (transLimitCheckBox.isSelected()) {
            try {
                transLimit = Integer.valueOf(transLimitTextField.getText());
            } catch (NumberFormatException e) {
                log.warn("Error converting: " + e.getMessage());
            }
        }
        connection.set(FNConnection.ATTR_TRANSITION_LIMIT, transLimit);
    }
}
