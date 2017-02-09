package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import net.parostroj.timetable.model.freight.FreightConnectionAnalyser;
import net.parostroj.timetable.model.freight.NodeFreightConnection;
import net.parostroj.timetable.model.freight.TrainConnection;
import net.parostroj.timetable.model.freight.TrainPath;
import net.parostroj.timetable.utils.TimeUtil;

/**
 * Panel with freight connection between two nodes - one train path.
 *
 * @author jub
 */
public class FreightTrainPathPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(FreightTrainPathPanel.class);

    private static final int COLUMNS_SHUNT_DURATION = 4;
    private static final int COMBO_BOX_LIST_SIZE = 12;
    private static final int INITIAL_SHUNT_VALUE = 120;
    private static final int COLUMNS_START_TIME = 5;
    private static final String INITIAL_START_TIME = "7:00";

    private final WrapperListModel<Node> fromNode;
    private final WrapperListModel<Node> toNode;
    private final JTextField startTimeTextField;
    private final JTextField shuntDurationTextField;
    private final DestinationTableModel tableModel;
    private final Runnable adjustColumnWidth;
    private final FreightComboBoxHelper helper;

    private TrainDiagram diagram;

    private JLabel stateIconLabel;
    private ImageIcon okIcon;
    private ImageIcon errorIcon;

    public FreightTrainPathPanel() {
        JComboBox<Wrapper<Node>> fromComboBox = new JComboBox<>();
        JComboBox<Wrapper<Node>> toComboBox = new JComboBox<>();
        fromNode = new WrapperListModel<>(true);
        toNode = new WrapperListModel<>(true);
        fromComboBox.setModel(fromNode);
        toComboBox.setModel(toNode);
        fromComboBox.setMaximumRowCount(COMBO_BOX_LIST_SIZE);
        toComboBox.setMaximumRowCount(COMBO_BOX_LIST_SIZE);

        shuntDurationTextField = new JTextField(COLUMNS_SHUNT_DURATION);
        shuntDurationTextField.setHorizontalAlignment(JTextField.RIGHT);
        shuntDurationTextField.setText(Integer.toString(INITIAL_SHUNT_VALUE));

        startTimeTextField = new JTextField(COLUMNS_START_TIME);
        startTimeTextField.setHorizontalAlignment(JTextField.RIGHT);
        startTimeTextField.setText(INITIAL_START_TIME);


        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        add(topPanel, BorderLayout.NORTH);
        FlowLayout topLayout = new FlowLayout();
        topLayout.setAlignment(FlowLayout.LEFT);
        topPanel.setLayout(topLayout);
        topPanel.add(fromComboBox);
        topPanel.add(toComboBox);
        topPanel.add(new JLabel(ResourceLoader.getString("freight.trainpath.start"))); // NOI18N
        topPanel.add(startTimeTextField);
        topPanel.add(new JLabel(ResourceLoader.getString("freight.trainpath.shunting"))); // NOI18N
        topPanel.add(shuntDurationTextField);
        topPanel.add(new JLabel("min"));

        ItemListener nodeListener = event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                Node from = fromNode.getSelectedObject();
                Node to = toNode.getSelectedObject();
                updateView(from, to, getCenterShuntValue(), getStartTimeValue());
            }
        };
        fromComboBox.addItemListener(nodeListener);
        toComboBox.addItemListener(nodeListener);

        JButton refreshButton = GuiComponentUtils.createButton(GuiIcon.REFRESH, 1);
        topPanel.add(refreshButton);

        refreshButton.addActionListener(e -> this.updateView(
                fromNode.getSelectedObject(),
                toNode.getSelectedObject(),
                getCenterShuntValue(),
                getStartTimeValue()));

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

    private int getCenterShuntValue() {
        int shunt = INITIAL_SHUNT_VALUE;
        try {
            shunt = Integer.parseInt(shuntDurationTextField.getText());
        } catch (NumberFormatException e) {
            log.warn("Error parsing shunt value: " + shuntDurationTextField.getText());
            shuntDurationTextField.setText(Integer.toString(shunt));
        }
        return shunt * 60;
    }

    private int getStartTimeValue() {
        if (diagram == null) {
            return 0;
        }
        String timeText = startTimeTextField.getText();
        int startTime = diagram.getTimeConverter().convertTextToInt(timeText);
        if (startTime == -1) {
            startTime = 0;
        }
        startTimeTextField.setText(diagram.getTimeConverter().convertIntToText(startTime));
        return startTime;
    }

    public void setDiagram(TrainDiagram diagram) {
        this.diagram = diagram;
        helper.initializeNodeSelection(diagram, fromNode);
        helper.initializeNodeSelection(diagram, toNode);
    }

    public void updateView(Node from, Node to, int shuntDuration, int startTime) {
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
                        FreightConnectionAnalyser connectionAnalyser = new FreightConnectionAnalyser(diagram);
                        Set<NodeFreightConnection> conns = connectionAnalyser.analyse(from, to);
                        TrainPath trainPath = connectionAnalyser.getTrainPath(conns, startTime, shuntDuration);

                        stateIconLabel.setIcon(!trainPath.isEmpty() ? okIcon : errorIcon);

                        Integer time = null;

                        for(TrainConnection tc : trainPath) {
                            String right = convertConnectionTrain(tc);
                            String left = convertNode(
                                    tc.getFrom().getOwnerAsNode(),
                                    time == null ? null : TimeUtil.difference(time, tc.getStartTime()));
                            time = tc.getEndTime();
                            model.addLine(left, right);
                        }

                        if (!trainPath.isEmpty()) {
                            model.addLine(convertNode(to, null), "");
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

    private String convertNode(Node node, Integer difference) {
        String result = null;
        if (node.isCenterOfRegions()) {
            result = String.format("<b>%s</b>", node.getName());
        } else {
            result = node.getName();
        }
        if (difference != null) {
            result = String.format("%s [%smin]", result, diagram.getTimeConverter().convertIntToMinutesText(difference));
        }
        return node.isCenterOfRegions() ? String.format("<html>%s</html>", result) : result;
    }

    private String convertConnectionTrain(TrainConnection connection) {
        return String.format("%s (%s-%s)", connection.getFrom().getTrain().getName().translate(),
                diagram.getTimeConverter().convertIntToText(connection.getStartTime()),
                diagram.getTimeConverter().convertIntToText(connection.getEndTime()));
    }
}
