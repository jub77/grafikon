package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
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

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.Node;
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

    private static final Wrapper<Node> EMPTY = Wrapper.getEmptyWrapper("-");

    private final WrapperListModel<Node> nodesModel;
    private final DestinationTableModel model;
    private final Runnable adjustColumnWidth;

    private TrainDiagram diagram;

    private final OutputFreightUtil util = new OutputFreightUtil();

    public FreightDestinationPanel() {
        JComboBox<Wrapper<Node>> nodesComboBox = new JComboBox<>();
        nodesModel = new WrapperListModel<>(true);
        nodesComboBox.setModel(nodesModel);

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

        model = new DestinationTableModel();
        JTable table = new JTable(model);
        table.setTableHeader(null);

        adjustColumnWidth = () -> {
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
        };

        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), scroll.getBorder()));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scroll, BorderLayout.CENTER);
    }

    public void setDiagram(TrainDiagram diagram) {
        this.diagram = diagram;
        nodesModel.clear();
        if (diagram != null) {
            nodesModel.setListOfWrappers(Wrapper.getWrapperList(diagram.getNet().getNodes()));
        }
        nodesModel.addWrapper(EMPTY);
        nodesModel.setSelectedItem(EMPTY);
        if (diagram != null) {
            diagram.getNet().addListener(event -> {
                if (event.getObject() instanceof Node) {
                    switch (event.getType()) {
                    case ADDED:
                        nodesModel.addWrapper(Wrapper.getWrapper((Node) event.getObject()));
                        break;
                    case REMOVED:
                        nodesModel.removeObject((Node) event.getObject());
                        break;
                    default:
                        // nothing
                    }
                }
            });
        }
    }

    public void updateView(Node node) {
        // update selection
        if (nodesModel.getSelectedObject() != node) {
            nodesModel.setSelectedObject(node);
        }
        model.clear();
        if (node != null) {
            FreightAnalyser analyser = new FreightAnalyser(diagram);

            NodeFreight freight = analyser.getFreightNodeRegionsFrom(node);
            this.addFreightToNodes(freight);
            this.addFreightToRegions(freight);
            adjustColumnWidth.run();
        }
    }

    private void addFreightToNodes(NodeFreight nodeTransport) {
        Locale locale = Locale.getDefault();
        Collator collator = Collator.getInstance();
        List<Tuple<String>> lines = nodeTransport.getDirectConnections().stream()
                .filter(e -> e.getTo().isVisible())
                .map(e -> {
                    String node = util.freightNodeToString(e, locale, false);
                    String trains = util.intervalsToString(diagram, e.getTransport().getTrains(), locale).stream()
                            .collect(Collectors.joining(", "));
                    return new Tuple<>(node, trains);
                })
                .sorted((a, b) -> collator.compare(a.first, b.first)).collect(Collectors.toList());
        model.addLines(lines);
    }

    private void addFreightToRegions(NodeFreight regionTransport) {
        Locale locale = Locale.getDefault();
        Collator collator = Collator.getInstance();
        List<Tuple<String>> lines = regionTransport.getRegionConnections().stream()
                .map(e -> {
                    String node = util.freightRegionToString(e, locale);
                    String through = e.getTransport().getTrains() != null
                            ? util.intervalsToString(diagram, e.getTransport().getTrains(), locale).stream().collect(
                                    Collectors.joining(", "))
                            : util.freightRegionToString(e.getTransport().getConnection(), locale);
                    return new Tuple<>(node, through);
                })
                .filter(t -> !t.first.equals(t.second))
                .distinct()
                .sorted((a, b) -> collator.compare(a.first, b.first))
                .collect(Collectors.toList());
        if (model.getRowCount() != 0) {
            model.addLine("", "");
        }
        model.addLines(lines);
    }

    protected static class DestinationTableModel extends AbstractTableModel {

        private List<Tuple<String>> data = new ArrayList<>();

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
            data.add(new Tuple<>(node, via));
            this.fireTableRowsInserted(data.size() - 1, data.size() - 1);
        }

        public void addLines(Collection<Tuple<String>> lines) {
            int size = data.size();
            data.addAll(lines);
            this.fireTableRowsInserted(size, data.size() - 1);
        }
    }
}
