package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.parostroj.timetable.gui.components.FreightDestinationPanel.ColumnAdjuster;
import net.parostroj.timetable.gui.components.FreightDestinationPanel.DestinationTableModel;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.util.OutputFreightUtil;

/**
 * Panel with freight connection between two nodes.
 *
 * @author jub
 */
public class FreightConnectionPanel extends JPanel {

    private static final Wrapper<Node> EMPTY = Wrapper.getEmptyWrapper("-");

    private final WrapperListModel<Node> fromNode;
    private final WrapperListModel<Node> toNode;
    private final DestinationTableModel model;
    private final Runnable adjustColumnWidth;

    private TrainDiagram diagram;

    private final OutputFreightUtil util = new OutputFreightUtil();

    public FreightConnectionPanel() {
        JComboBox<Wrapper<Node>> fromComboBox = new JComboBox<>();
        JComboBox<Wrapper<Node>> toComboBox = new JComboBox<>();
        fromNode = new WrapperListModel<>(true);
        toNode = new WrapperListModel<>(true);
        fromComboBox.setModel(fromNode);
        toComboBox.setModel(toNode);

        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        add(topPanel, BorderLayout.NORTH);
        FlowLayout topLayout = new FlowLayout();
        topLayout.setAlignment(FlowLayout.LEFT);
        topPanel.setLayout(topLayout);
        topPanel.add(fromComboBox);
        topPanel.add(toComboBox);

        ItemListener nodeListener = event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                Node from = fromNode.getSelectedObject();
                Node to = toNode.getSelectedObject();
                updateView(from, to);
            }
        };
        fromComboBox.addItemListener(nodeListener);
        toComboBox.addItemListener(nodeListener);

        JButton refreshButton = GuiComponentUtils.createButton(GuiIcon.REFRESH, 1);
        topPanel.add(refreshButton);

        refreshButton.addActionListener(e -> this.updateView(fromNode.getSelectedObject(), toNode.getSelectedObject()));

        model = new DestinationTableModel();
        JTable table = new JTable(model);
        table.setTableHeader(null);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        adjustColumnWidth = new ColumnAdjuster(table);


        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), scroll.getBorder()));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scroll, BorderLayout.CENTER);
    }

    public void setDiagram(TrainDiagram diagram) {
        this.diagram = diagram;
        initializeNodeSelection(diagram, fromNode);
        initializeNodeSelection(diagram, toNode);
    }

    private void initializeNodeSelection(TrainDiagram diagram, WrapperListModel<Node> node) {
        node.clear();
        if (diagram != null) {
            node.setListOfWrappers(Wrapper.getWrapperList(diagram.getNet().getNodes()));
        }
        node.addWrapper(EMPTY);
        node.setSelectedItem(EMPTY);
        if (diagram != null) {
            diagram.getNet().addListener(event -> {
                if (event.getObject() instanceof Node) {
                    switch (event.getType()) {
                    case ADDED:
                        node.addWrapper(Wrapper.getWrapper((Node) event.getObject()));
                        break;
                    case REMOVED:
                        if (node.getSelectedObject() == event.getObject()) {
                            node.setSelectedItem(EMPTY);
                        }
                        node.removeObject((Node) event.getObject());
                        break;
                    default:
                        // nothing
                    }
                }
            });
        }
    }

    public void updateView(Node from, Node to) {
        // update selection
        if (fromNode.getSelectedObject() != from) {
            fromNode.setSelectedObject(from);
        }
        if (toNode.getSelectedObject() != to) {
            toNode.setSelectedObject(to);
        }
        model.clear();
        if (from != null && to != null) {
            util.createAnalyser(diagram);

            // TODO write connection
            model.addLine(from.toString(), to.toString());

            adjustColumnWidth.run();
        }
    }
}
