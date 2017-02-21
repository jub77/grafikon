package net.parostroj.timetable.gui.components;

import static java.util.stream.Collectors.toList;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.EventDispatchAfterModelAction;
import net.parostroj.timetable.gui.components.FreightDestinationPanel.ColumnAdjuster;
import net.parostroj.timetable.gui.components.FreightDestinationPanel.DataModel;
import net.parostroj.timetable.gui.components.FreightDestinationPanel.DestinationTableModel;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.events.Event.Type;
import net.parostroj.timetable.model.freight.DirectNodeConnection;
import net.parostroj.timetable.model.freight.FreightConnectionAnalyser;
import net.parostroj.timetable.model.freight.NodeFreightConnection;
import net.parostroj.timetable.model.freight.TrainConnection;
import net.parostroj.timetable.utils.TimeUtil;

/**
 * Panel with freight connection between two nodes.
 *
 * @author jub
 */
public class FreightConnectionPanel extends JPanel {

    private static final int COMBO_BOX_LIST_SIZE = 12;

    private final WrapperListModel<Node> fromNode;
    private final WrapperListModel<Node> toNode;
    private final DestinationTableModel tableModel;
    private final Runnable adjustColumnWidth;
    private final FreightComboBoxHelper helper;

    private TrainDiagram diagram;

    private JLabel stateIconLabel;
    private ImageIcon okIcon;
    private ImageIcon errorIcon;

    public FreightConnectionPanel() {
        JComboBox<Wrapper<Node>> fromComboBox = new JComboBox<>();
        JComboBox<Wrapper<Node>> toComboBox = new JComboBox<>();
        fromNode = new WrapperListModel<>(true);
        toNode = new WrapperListModel<>(true);
        fromComboBox.setModel(fromNode);
        toComboBox.setModel(toNode);
        fromComboBox.setMaximumRowCount(COMBO_BOX_LIST_SIZE);
        toComboBox.setMaximumRowCount(COMBO_BOX_LIST_SIZE);

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

        stateIconLabel = new JLabel();
        topPanel.add(stateIconLabel);

        okIcon = ResourceLoader.createImageIcon(GuiIcon.OK);
        errorIcon = ResourceLoader.createImageIcon(GuiIcon.ERROR);

        tableModel = new DestinationTableModel();
        JTable table = new JTable(tableModel);
        table.setTableHeader(null);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        adjustColumnWidth = new ColumnAdjuster(table);

        helper = new FreightComboBoxHelper();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), scroll.getBorder()));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scroll, BorderLayout.CENTER);
    }

    public void setDiagram(TrainDiagram diagram) {
        this.diagram = diagram;
        helper.initializeNodeSelection(diagram, fromNode);
        helper.initializeNodeSelection(diagram, toNode);
    }

    public void updateView(Node from, Node to) {
        // update selection
        if (fromNode.getSelectedObject() != from) {
            fromNode.setSelectedObject(from);
        }
        if (toNode.getSelectedObject() != to) {
            toNode.setSelectedObject(to);
        }
        tableModel.clear();
        stateIconLabel.setIcon(null);
        if (from != null && to != null && from != to) {

            DataModel model = new DataModel();
            ActionContext context = new ActionContext(GuiComponentUtils.getTopLevelComponent(this));
            ActionHandler.getInstance().execute(new EventDispatchAfterModelAction(context) {
                @Override
                protected void backgroundAction() {
                    setWaitMessage(ResourceLoader.getString("wait.message.processing"));
                    setWaitDialogVisible(true);
                    try {
                        FreightConnectionAnalyser connectionAnalyser = new FreightConnectionAnalyser(
                                diagram.getFreightNet().getConnectionStrategy());
                        NodeFreightConnection ncf = connectionAnalyser.analyse(from, to).stream()
                                .min(Comparator.comparingInt(NodeFreightConnection::getLength)).get();

                        stateIconLabel.setIcon(ncf.isComplete() ? okIcon : errorIcon);

                        ncf.getSteps().forEach(s -> {
                            List<String> list = convertStep(s);
                            String node = convertNode(s.getFrom());
                            for (String item : list) {
                                model.addLine(node, item);
                                node = null;
                            }
                        });

                        if (ncf.isComplete()) {
                            model.addLine(convertNode(to), "");
                        } else {
                            List<DirectNodeConnection> steps = ncf.getSteps();
                            if (steps != null && !steps.isEmpty()) {
                                model.addLine(convertNode(steps.get(steps.size() - 1).getTo()), "");
                            }
                        }
                    } finally {
                        setWaitDialogVisible(false);
                    }
                }

                @Override
                protected void eventDispatchActionAfter() {
                    tableModel.addLines(model);
                    adjustColumnWidth.run();
                }
            });
        }
    }

    private String convertNode(Node node) {
        if (node.isCenterOfRegions()) {
            return String.format("<html><b>%s</b></html>", node.getName());
        } else {
            return node.getName();
        }
    }

    private List<String> convertStep(DirectNodeConnection step) {
        return step.getConnections().stream()
                .sorted(this::compareLists)
                .map(this::convertPath)
                .collect(toList());
    }

    private String convertPath(List<TrainConnection> path) {
        StringBuilder result = new StringBuilder();
        Iterator<TrainConnection> i = path.iterator();
        TrainConnection conn = i.next();
        // first one
        result.append(convertConnectionTrain(conn));
        // rest
        while (i.hasNext()) {
            conn = i.next();
            result.append(" > ").append(conn.getFrom().getOwnerAsNode().getName()).append(" > ");
            result.append(convertConnectionTrain(conn));
        }
        return result.toString();
    }

    private String convertConnectionTrain(TrainConnection connection) {
        return String.format("%s (%s-%s)", connection.getFrom().getTrain().getName().translate(),
                diagram.getTimeConverter().convertIntToText(connection.getFrom().getEnd()),
                diagram.getTimeConverter().convertIntToText(connection.getTo().getStart()));
    }

    private int compareLists(List<TrainConnection> a, List<TrainConnection> b) {
        return TimeUtil.compareNormalizedEnds(a.get(0).getFrom(), b.get(0).getFrom());
    }
}

class FreightComboBoxHelper {

    static final Wrapper<Node> EMPTY = Wrapper.getEmptyWrapper("-");

    public void initializeNodeSelection(TrainDiagram diagram, WrapperListModel<Node> nodesModel) {
        nodesModel.clear();
        if (diagram != null) {
            nodesModel.setListOfWrappers(Wrapper.getWrapperList(
                    diagram.getNet().getNodes().stream().filter(n -> n.getType().isFreight()).collect(toList())));
        }
        nodesModel.addWrapper(EMPTY);
        nodesModel.setSelectedItem(EMPTY);
        if (diagram != null) {
            installListeners(diagram, nodesModel);
        }
    }

    private void installListeners(TrainDiagram diagram, WrapperListModel<Node> nodesModel) {
        diagram.getNet().addListener(event -> {
            if (event.getObject() instanceof Node) {
                Node node = (Node) event.getObject();
                switch (event.getType()) {
                case ADDED:
                    addNode(nodesModel, node);
                    break;
                case REMOVED:
                    removeNode(nodesModel, node);
                    break;
                default:
                    // nothing
                }
            }
        });
        diagram.getNet().addAllEventListener(e -> {
            if (e.getSource() instanceof Node && e.getType() == Type.ATTRIBUTE
                    && e.getAttributeChange().checkName(Node.ATTR_TYPE)) {
                Node node = (Node) e.getSource();
                if (node.getType().isFreight()) {
                    addNode(nodesModel, node);
                } else {
                    removeNode(nodesModel, node);
                }
            }
        });
    }

    private static void removeNode(WrapperListModel<Node> nodesModel, Node node) {
        if (nodesModel.getSelectedObject() == node) {
            nodesModel.setSelectedItem(EMPTY);
        }
        nodesModel.removeObject(node);
    }

    private static void addNode(WrapperListModel<Node> nodesModel, Node node) {
        if (node.getType().isFreight()) {
            nodesModel.addWrapper(Wrapper.getWrapper(node));
        }
    }
}
