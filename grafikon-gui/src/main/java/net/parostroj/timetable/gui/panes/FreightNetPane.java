package net.parostroj.timetable.gui.panes;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.views.graph.FreightNetGraphAdapter;
import net.parostroj.timetable.gui.views.graph.FreightNetGraphComponent;
import net.parostroj.timetable.model.FreightNet.FreightNetNode;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.TrainEvent;

import com.mxgraph.swing.mxGraphOutline;

public class FreightNetPane extends javax.swing.JPanel implements StorableGuiData {

    private TrainDiagram diagram;
    private FreightNetGraphAdapter graph;
    private FreightNetGraphComponent graphComponent;
    private final JPanel panel;
    private mxGraphOutline graphOutline;

    public FreightNetPane() {
        setLayout(new BorderLayout());

        panel = new JPanel();
        add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(0, 0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(buttonPanel, BorderLayout.WEST);

        buttonPanel.setLayout(new GridLayout(0, 1));
        buttonPanel.add(GuiComponentUtils.createButton(GuiIcon.CONNECT, 2));
        buttonPanel.add(GuiComponentUtils.createButton(GuiIcon.SELECT, 2));
        buttonPanel.add(GuiComponentUtils.createButton(GuiIcon.EDIT, 2));
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
                        // graph.getSelectionModel().addListener(mxEvent.CHANGE, netEditModel);
                        graphComponent = new FreightNetGraphComponent(graph);
                        graphComponent.setPageBackgroundColor(panel.getBackground());
                        add(graphComponent, BorderLayout.CENTER);
                        graphOutline = graphComponent.createOutline();
                        panel.add(BorderLayout.CENTER, graphOutline);
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
