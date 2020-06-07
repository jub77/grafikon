package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import net.parostroj.timetable.gui.actions.execution.RsActionHandler;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.freight.FreightAnalyser;
import net.parostroj.timetable.model.freight.NodeFreight;
import net.parostroj.timetable.output2.util.OutputFreightUtil;
import net.parostroj.timetable.utils.Tuple;

/**
 * Panel about freight destinations.
 *
 * @author jub
 */
public class FreightDestinationPanel extends JPanel {

    private static final long serialVersionUID = 1L;

	private static final int COMBO_BOX_LIST_SIZE = 12;

    private final WrapperListModel<Node> nodesModel;
    private final DestinationTableModel tableModel;
    private final Runnable adjustColumnWidth;
    private final FreightComboBoxHelper helper;

    private TrainDiagram diagram;

    private final OutputFreightUtil util = new OutputFreightUtil();

    public FreightDestinationPanel() {
        JComboBox<Wrapper<Node>> nodesComboBox = new JComboBox<>();
        nodesModel = new WrapperListModel<>(true);
        nodesComboBox.setModel(nodesModel);
        nodesComboBox.setMaximumRowCount(COMBO_BOX_LIST_SIZE);

        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        add(topPanel, BorderLayout.NORTH);
        FlowLayout topLayout = new FlowLayout();
        topLayout.setAlignment(FlowLayout.LEFT);
        topPanel.setLayout(topLayout);
        topPanel.add(nodesComboBox);

        nodesComboBox.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                Node node = nodesModel.getSelectedObject();
                updateView(node);
            }
        });

        JButton refreshButton = GuiComponentUtils.createButton(GuiIcon.REFRESH, 1);
        topPanel.add(refreshButton);

        refreshButton.addActionListener(e -> this.updateView(nodesModel.getSelectedObject()));

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
        this.helper.initializeNodeSelection(diagram, nodesModel);
    }

    public void updateView(Node node) {
        // update selection
        if (nodesModel.getSelectedObject() != node) {
            nodesModel.setSelectedObject(node);
        }
        tableModel.clear();
        if (node != null) {

            DataModel model = new DataModel();

            RsActionHandler.getInstance()
                .newExecution("freight_dest", GuiComponentUtils.getTopLevelComponent(this), diagram)
                .addConsumer((c, diagram) -> {
                    c.setWaitMessage(ResourceLoader.getString("wait.message.processing"));
                    c.setWaitDialogVisible(true);
                })
                .onBackground()
                .addConsumer((c, diagram) -> {
                    FreightAnalyser analyser = new FreightAnalyser(diagram.getFreightNet().getConnectionStrategy());

                    NodeFreight freight = analyser.getNodeFreightFrom(node);
                    addFreightToNodes(model, freight);
                    addFreightToRegions(model, freight);
                    addFreightToColors(model, freight);

                    addFreightTrainsFromNode(model, node, analyser);
                    addFreightTrainUnitsFromNode(model, node, analyser);
                })
                .addConsumer((c, diagram) -> {
                    tableModel.addLines(model);
                    adjustColumnWidth.run();
                })
                .execute();
        }
    }

    private Comparator<? super Tuple<String>> comparator(Collator collator) {
        return (a, b) -> collator.compare(a.first, b.first);
    }

    private void addFreightTrainUnitsFromNode(DataModel model, Node node, FreightAnalyser analyser) {
        Locale locale = Locale.getDefault();
        List<TimeInterval> intervals = analyser.getFreightTrainUnitIntervals(node);
        List<Tuple<String>> trains = intervals.stream()
                .map(i -> new Tuple<>(util.intervalToString(diagram, i, locale),
                        util.intervalFreightTrainUnitToString(diagram, i).stream()
                                .collect(Collectors.joining(", "))))
                .collect(Collectors.toList());

        model.addLinesWithEmpty(trains);
    }

    private void addFreightTrainsFromNode(DataModel model, Node node, FreightAnalyser analyser) {
        Locale locale = Locale.getDefault();
        List<TimeInterval> intervalsFrom = analyser.getFreightIntervalsFrom(node);
        List<Tuple<String>> trains = intervalsFrom.stream()
                .map(i -> new Tuple<>(util.intervalToString(diagram, i, locale),
                        util.freightListToString(analyser.getConnectionStrategy().getFreightToNodes(i), locale)
                                .stream().collect(Collectors.joining(", "))))
                .collect(Collectors.toList());

        model.addLinesWithEmpty(trains);
    }

    private void addFreightToNodes(DataModel model, NodeFreight nodeFreight) {
        Locale locale = Locale.getDefault();
        Collator collator = Collator.getInstance();
        List<Tuple<String>> lines = nodeFreight.getNodeConnections().stream()
                .filter(e -> e.getTo().isVisible())
                .map(e -> {
                    String node = util.freightNodeToString(e.getTo(), locale, false);
                    String trains = util.intervalsToString(diagram, e.getTransport().getTrains(), locale)
                            .stream().collect(Collectors.joining(", "));
                    return new Tuple<>(node, trains);
                })
                .sorted(comparator(collator)).collect(Collectors.toList());
        model.addLinesWithEmpty(lines);
    }

    private void addFreightToRegions(DataModel model, NodeFreight nodeFreight) {
        Locale locale = Locale.getDefault();
        Collator collator = Collator.getInstance();
        List<Tuple<String>> lines = nodeFreight.getRegionConnections().stream()
                .map(e -> {
                    String region = util.freightRegionsToString(e.getTo(), locale).stream()
                            .collect(Collectors.joining(", "));
                    String transport = util.transportToString(diagram, e.getTransport(), locale).stream()
                            .collect(Collectors.joining(", "));
                    return new Tuple<>(region, transport);
                })
                .sorted(comparator(collator))
                .collect(Collectors.toList());
        model.addLinesWithEmpty(lines);
    }

    private void addFreightToColors(DataModel model, NodeFreight nodeFreight) {
        Locale locale = Locale.getDefault();
        List<Tuple<String>> lines = nodeFreight.getFreightColorConnections().stream()
                .map(e -> {
                    String color = util.freightColorsToString(e.getTo(), locale).stream()
                            .collect(Collectors.joining(", "));
                    String transport = util.transportToString(diagram, e.getTransport(), locale).stream()
                            .collect(Collectors.joining(", "));
                    return new Tuple<>(color, transport);
                })
                .collect(Collectors.toList());
        model.addLinesWithEmpty(lines);
    }

    protected static final class ColumnAdjuster implements Runnable {

        private JTable table;

        public ColumnAdjuster(JTable table) {
            this.table = table;
        }

        @Override
        public void run() {
            int freeSpace = 5;
            final TableColumnModel columnModel = table.getColumnModel();
            int width = 0;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, 0);
                Component comp = table.prepareRenderer(renderer, row, 0);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }
            columnModel.getColumn(0).setMinWidth(width + freeSpace);
            columnModel.getColumn(0).setMaxWidth(width + freeSpace);
            columnModel.getColumn(0).setPreferredWidth(width + freeSpace);
            columnModel.getColumn(1).setPreferredWidth(table.getWidth() - columnModel.getColumn(0).getPreferredWidth());
        }
    }

    protected static final class DataModel extends ArrayList<Tuple<String>> {

        private static final long serialVersionUID = 1L;

		public void addLines(Collection<Tuple<String>> lines) {
            addAll(lines);
        }

        public void addLine(String node, String via) {
            add(new Tuple<>(node, via));
        }

        public void addLinesWithEmpty(Collection<Tuple<String>> lines) {
            if (size() != 0 && !lines.isEmpty()) {
                addLine("", "");
            }
            addLines(lines);
        }
    }

    protected static final class DestinationTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

		private final DataModel data = new DataModel();

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Tuple<String> tuple = data.get(rowIndex);
            return columnIndex == 0 ? tuple.first : tuple.second;
        }

        public void clear() {
            data.clear();
            this.fireTableDataChanged();
        }

        public void addLine(String node, String via) {
            data.addLine(node, via);
            this.fireTableRowsInserted(data.size() - 1, data.size() - 1);
        }

        public void addLines(Collection<Tuple<String>> lines) {
            int size = data.size();
            data.addLines(lines);
            this.fireTableRowsInserted(size, data.size() - 1);
        }

        public void addLinesWithEmpty(Collection<Tuple<String>> lines) {
            int size = data.size();
            data.addLinesWithEmpty(lines);
            this.fireTableRowsInserted(size, data.size() - 1);
        }
    }
}
