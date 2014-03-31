package net.parostroj.timetable.gui.panes;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.views.graph.FreightNetGraphAdapter;
import net.parostroj.timetable.gui.views.graph.FreightNetGraphComponent;
import net.parostroj.timetable.model.FreightNet.FreightNetConnection;
import net.parostroj.timetable.model.FreightNet.FreightNetNode;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.TrainEvent;

import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxRubberband;

public class FreightNetPane extends javax.swing.JPanel implements StorableGuiData {

    private TrainDiagram diagram;
    private FreightNetGraphAdapter graph;
    private FreightNetGraphComponent graphComponent;
    private final JPanel panel;
    private mxGraphOutline graphOutline;
    private mxRubberband selectionHandler;
    private final JButton removeButton;

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
        removeButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);
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

                        graph = new FreightNetGraphAdapter(diagram.getFreightNet().getGraph(), new FreightNetGraphAdapter.SelectionListener() {
                            @Override
                            public void selectedConnections(Collection<FreightNetConnection> connections) {
                                removeButton.setEnabled(!connections.isEmpty());
                            }
                        });
                        graphComponent = new FreightNetGraphComponent(graph);
                        graphComponent.setPageBackgroundColor(panel.getBackground());
                        add(graphComponent, BorderLayout.CENTER);
                        graphOutline = graphComponent.createOutline();
                        panel.add(BorderLayout.CENTER, graphOutline);

                        selectionHandler = new mxRubberband(graphComponent);
                    }
                }
            }

            @Override
            public void processTrainEvent(TrainEvent event) {
                if (event.getType() == GTEventType.ATTRIBUTE && event.getAttributeChange().checkName(Train.ATTR_NAME)) {
                    updateNode(event.getSource());
                }
            }
        });
    }

    private void updateNode(Train train) {
        FreightNetNode node = diagram.getFreightNet().getNode(train);
        graph.cellLabelChanged(graph.getVertexToCellMap().get(node), node, true);
    }
}
