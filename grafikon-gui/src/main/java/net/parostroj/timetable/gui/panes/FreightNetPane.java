package net.parostroj.timetable.gui.panes;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import net.parostroj.timetable.actions.FilteredIterableFactory;
import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.views.graph.FreightNetGraphAdapter;
import net.parostroj.timetable.gui.views.graph.FreightNetGraphComponent;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.FreightNetEvent;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.TrainEvent;
import net.parostroj.timetable.utils.Tuple;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.*;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraphSelectionModel;

public class FreightNetPane extends javax.swing.JPanel implements StorableGuiData {

    private TrainDiagram diagram;
    private FreightNetGraphAdapter graph;
    private FreightNetGraphComponent graphComponent;
    private final JPanel panel;
    private mxGraphOutline graphOutline;
    private mxRubberband selectionHandler;
    private final JButton removeButton;
    private final Action removeAction;

    public FreightNetPane() {
        setLayout(new BorderLayout());

        panel = new JPanel();
        add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(0, 0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(buttonPanel, BorderLayout.WEST);

        buttonPanel.setLayout(new GridLayout(0, 1));
        JToggleButton selectButton = GuiComponentUtils.createToggleButton(GuiIcon.SELECT, 2);
        buttonPanel.add(selectButton);
        JToggleButton connectButton = GuiComponentUtils.createToggleButton(GuiIcon.CONNECT, 2);
        buttonPanel.add(connectButton);
        removeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graph.getSelectionCount() > 0) {
                    Object[] cells = graph.getSelectionCells();
                    for (Object cell : cells) {
                        mxCell m = (mxCell) cell;
                        if (m.getValue() instanceof FreightNet.Connection) {
                            diagram.getFreightNet().removeConnection((FreightNet.Connection) m.getValue());
                        }
                    }
                }
            }
        };
        removeButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2, removeAction);
        removeButton.setEnabled(false);
        buttonPanel.add(removeButton);

        ButtonGroup bg = new ButtonGroup();
        bg.add(selectButton);
        bg.add(connectButton);
        selectButton.setSelected(true);

        selectButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (selectionHandler != null)
                    selectionHandler.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        connectButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (graphComponent != null)
                    graphComponent.getConnectionHandler().setEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
    }

    @Override
    public void saveToPreferences(AppPreferences prefs) {
    }

    @Override
    public void loadFromPreferences(AppPreferences prefs) {
    }

    public void setModel(ApplicationModel model) {
        model.getMediator().addColleague(new ApplicationGTEventColleague() {

            @Override
            public void processApplicationEvent(ApplicationModelEvent event) {
                if (event.getType() == ApplicationModelEventType.SET_DIAGRAM_CHANGED) {
                    diagram = event.getModel().get();
                    if (diagram != null) {
                        if (graphComponent != null) {
                            remove(graphComponent);
                            panel.remove(graphOutline);
                        }

                        graph = new FreightNetGraphAdapter(diagram.getFreightNet().getGraph());
                        graphComponent = new FreightNetGraphComponent(graph);
                        graphComponent.setPageBackgroundColor(panel.getBackground());
                        add(graphComponent, BorderLayout.CENTER);
                        graphOutline = graphComponent.createOutline();
                        panel.add(BorderLayout.CENTER, graphOutline);

                        selectionHandler = new mxRubberband(graphComponent);

                        mxConnectionHandler connectionHandler = graphComponent.getConnectionHandler();
                        mxCellMarker marker = connectionHandler.getMarker();
                        marker.setHotspot(1.0);
                        connectionHandler.setConnectPreview(new mxConnectPreview(graphComponent) {
                            @Override
                            protected Object createCell(mxCellState startState, String style) {
                                mxCell cell = (mxCell) super.createCell(startState, style);
                                if (graph.getModel().isEdge(cell)) {
                                    cell.setStyle((style == null || "".equals(style) ? "" : style + ";")
                                            + "endArrow=classic;startArrow=none");
                                }
                                return cell;
                            }

                            @Override
                            public Object stop(boolean commit, MouseEvent e) {
                                Object result = super.stop(commit, e);
                                if (commit && result instanceof mxCell && ((mxCell) result).isEdge()) {
                                    // check if the connection is possible an create a new connection


                                    mxCell cell = (mxCell) result;
                                    FreightNet.Node from = (FreightNet.Node) cell.getSource().getValue();
                                    FreightNet.Node to = (FreightNet.Node) cell.getTarget().getValue();

                                    createConnection(from, to);

                                    graph.removeCells(new Object[] { result });
                                }
                                return result;
                            }
                        });
                        graph.addListener(mxEvent.CELLS_MOVED, new mxEventSource.mxIEventListener() {
                            @Override
                            public void invoke(Object sender, mxEventObject evt) {
                                if (mxEvent.CELLS_MOVED.equals(evt.getName())) {
                                    Object[] cells = (Object[]) evt.getProperty("cells");
                                    if (cells != null) {
                                        for (Object cell : cells) {
                                            mxCell mxCell = (mxCell) cell;
                                            if (mxCell.getValue() instanceof FreightNet.Node) {
                                                FreightNet.Node node = (FreightNet.Node) mxCell.getValue();

                                                int x = (int) (mxCell.getGeometry().getX());
                                                int y = (int) (mxCell.getGeometry().getY());
                                                node.setLocation(new Location(x, y));
                                            }
                                        }
                                    }
                                }
                            }
                        });
                        graph.getSelectionModel().addListener(mxEvent.CHANGE, new mxIEventListener() {
                            @Override
                            public void invoke(Object sender, mxEventObject evt) {
                                mxGraphSelectionModel mm = (mxGraphSelectionModel) sender;
                                boolean selected = false;
                                if (mm.getCells().length > 0) {
                                    for (Object cell : mm.getCells()) {
                                        if (((mxCell) cell).getValue() instanceof FreightNet.Connection) {
                                            selected = true;
                                            break;
                                        }
                                    }
                                }
                                removeAction.setEnabled(selected);
                            }
                        });
                        graph.getModel().beginUpdate();
                        try {
                            for (FreightNet.Node node : diagram.getFreightNet().getNodes()) {
                                updateNodeLocation(node);
                            }
                        } finally {
                            graph.getModel().endUpdate();
                        }
                    }
                }
            }

            @Override
            public void processTrainEvent(TrainEvent event) {
                if (event.getType() == GTEventType.ATTRIBUTE && event.getAttributeChange().checkName(Train.ATTR_NAME)) {
                    updateNode(event.getSource());
                }
            }

            @Override
            public void processFreightNetEvent(FreightNetEvent event) {
                if (event.getType() == GTEventType.FREIGHT_NET_TRAIN_ADDED) {
                    updateNodeLocation((FreightNet.Node) event.getObject());
                }
            }
        });
    }

    private void updateNode(Train train) {
        FreightNet.Node node = diagram.getFreightNet().getNode(train);
        mxCell cell = graph.getVertexToCellMap().get(node);
        graph.cellLabelChanged(cell, node, true);
    }

    private void updateNodeLocation(FreightNet.Node node) {
        mxCell cell = graph.getVertexToCellMap().get(node);
        graph.moveCells(new Object[] { cell }, node.getLocation().getX(), node.getLocation().getY());
    }

    private void createConnection(FreightNet.Node from, FreightNet.Node to) {
        // compute common node
        // TODO include selection if more than one node is common
        Tuple<TimeInterval> selected = null;
        for (TimeInterval toInterval : FilteredIterableFactory.getNodeIntervalsFreightTo(to.getTrain().getTimeIntervalList())) {
            for (TimeInterval fromInterval : FilteredIterableFactory.getNodeIntervalsFreightFrom(from.getTrain().getTimeIntervalList())) {
                Node toNode = toInterval.getOwnerAsNode();
                Node fromNode = fromInterval.getOwnerAsNode();
                if (toNode == fromNode && fromInterval.getEnd() < toInterval.getStart()) {
                    selected = new Tuple<TimeInterval>(fromInterval, toInterval);
                }
                if (selected != null) {
                    break;
                }
            }
            if (selected != null) {
                break;
            }
        }
        if (selected != null) {
            diagram.getFreightNet().addConnection(from, to, selected.first, selected.second);
        }
    }
}
