package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.components.ElementSelectionPanel;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.FNConnection;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Edit dialog for freight net connections.
 *
 * @author jub
 */
public class EditFNConnetionDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

	enum FilterType {

        TO_NODES(FNConnection.ATTR_TO_NODES, "edit.fnc.to.nodes"),
        FROM_NODES(FNConnection.ATTR_FROM_NODES, "edit.fnc.from.nodes"),
        NODES_STOP_INCLUDE(FNConnection.ATTR_LAST_NODES, "edit.fnc.last.nodes"),
        NODES_STOP_EXCLUDE(FNConnection.ATTR_LAST_NODES_EXCLUDE, "edit.fnc.last.nodes.exclude");

        private String attributeName;
        private String resourceKey;

        private FilterType(String attributeName, String resourceKey) {
            this.attributeName = attributeName;
            this.resourceKey = resourceKey;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public String getResourceKey() {
            return resourceKey;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(EditFNConnetionDialog.class);

    private final Map<FilterType, ElementSelectionPanel<Node>> selectionPanels;
    private FNConnection connection;
    private final JTextField transLimitTextField;
    private final JCheckBox transLimitCheckBox;

    public EditFNConnetionDialog(Window parent, boolean modal) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        JButton okButton = new JButton(ResourceLoader.getString("button.ok"));
        okButton.addActionListener(e -> {
            writeValuesBack();
            setVisible(false);
        });
        panel.add(okButton);

        JButton cancelButton = new JButton(ResourceLoader.getString("button.cancel"));
        cancelButton.addActionListener(e -> setVisible(false));
        panel.add(cancelButton);

        JPanel editPanel = new JPanel();
        getContentPane().add(editPanel, BorderLayout.CENTER);
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        editPanel.add(tabbedPane);

        selectionPanels = new EnumMap<>(FilterType.class);
        for (FilterType filterType : FilterType.values()) {
            addSelectionPanel(filterType, tabbedPane);
        }

        JPanel transLimitPanel = new JPanel();
        FlowLayout fl_transLimitPanel = (FlowLayout) transLimitPanel.getLayout();
        fl_transLimitPanel.setAlignment(FlowLayout.LEFT);
        transLimitPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), ResourceLoader.getString("edit.fnc.trans.limit"),
                TitledBorder.LEFT, TitledBorder.TOP, null, null));
        editPanel.add(transLimitPanel);

        transLimitCheckBox = new JCheckBox((String) null);
        transLimitPanel.add(transLimitCheckBox);

        JLabel limitLabel = new JLabel(ResourceLoader.getString("edit.fnc.limit") + ": ");
        transLimitPanel.add(limitLabel);

        transLimitTextField = new JTextField();
        transLimitTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        transLimitPanel.add(transLimitTextField);
        transLimitTextField.setColumns(5);
        transLimitTextField.setEnabled(false);

        transLimitCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                this.transLimitTextField.setEnabled(false);
                this.transLimitTextField.setText("");
            } else {
                this.transLimitTextField.setEnabled(true);
                this.transLimitTextField.setText("0");
            };
        });
    }

    private void addSelectionPanel(FilterType filterType, JTabbedPane tabbedPane) {
        ElementSelectionPanel<Node> selectionPanel = new ElementSelectionPanel<Node>();
        selectionPanels.put(filterType, selectionPanel);
        selectionPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        tabbedPane.addTab(ResourceLoader.getString(filterType.getResourceKey()), null, selectionPanel, null);
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
        for (FilterType filterType : FilterType.values()) {
            updateSelectionPanel(diagram, filterType);
        }
        // transition limit
        Integer transLimit = connection.get(FNConnection.ATTR_TRANSITION_LIMIT, Integer.class);
        transLimitCheckBox.setSelected(transLimit != null);
        transLimitTextField.setText(transLimit != null ? transLimit.toString() : "");
    }

    private void updateSelectionPanel(TrainDiagram diagram, FilterType filterType) {
        ElementSelectionPanel<Node> selectionPanel = selectionPanels.get(filterType);
        selectionPanel.setListForSelection(Wrapper.getWrapperList(diagram.getNet().getNodes()));
        List<Node> lastNodes = connection.getAsList(filterType.getAttributeName(), Node.class);
        if (lastNodes != null) {
            for (Node node : lastNodes) {
                selectionPanel.addSelected(Wrapper.getWrapper(node));
            }
        }
    }

    private void writeValuesBack() {
        for (FilterType filterType : FilterType.values()) {
            writeBackSelectionPanel(filterType);
        }
        // transition limit
        Integer transLimit = null;
        if (transLimitCheckBox.isSelected()) {
            try {
                transLimit = Integer.valueOf(transLimitTextField.getText());
            } catch (NumberFormatException e) {
                log.warn("Error converting: {}", e.getMessage());
            }
        }
        connection.set(FNConnection.ATTR_TRANSITION_LIMIT, transLimit);
    }

    private void writeBackSelectionPanel(FilterType filterType) {
        ElementSelectionPanel<Node> selectionPanel = selectionPanels.get(filterType);
        List<Wrapper<Node>> selectedList = selectionPanel.getSelectedList();
        List<Node> list = Wrapper.unwrap(selectedList);
        if (list.isEmpty()) {
            list = null;
        }
        connection.set(filterType.getAttributeName(), list);
    }
}
