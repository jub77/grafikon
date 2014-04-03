package net.parostroj.timetable.gui.panes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.components.*;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FreightNetPane2 extends JPanel implements StorableGuiData {

    private class HighlightSelection implements HighlightedTrains, TrainSelector {

        @Override
        public boolean isHighlighedInterval(TimeInterval interval) {
            return interval == connection.first || interval == connection.second;
        }

        @Override
        public Color getColor() {
            return Color.GREEN;
        }

        @Override
        public void intervalsSelected(List<TimeInterval> intervals) {
            TimeInterval interval = intervals.isEmpty() ? null : intervals.get(0);
            boolean enabled = false;
            if (interval == null || !interval.isNodeOwner()) {
                connection.first = null;
                connection.second = null;
            } else {
                if (connection.first == null) {
                    connection.first = interval;
                } else if (connection.second == null) {
                    connection.second = interval;
                    enabled = checkEnabled();
                } else {
                    connection.first = null;
                    connection.second = null;
                }
            }
            newButton.setEnabled(enabled);
            graphicalTimetableView.repaint();
        }

        @Override
        public void editSelected() {
            // nothing ...
        }
    }

    private static final Logger log = LoggerFactory.getLogger(FreightNetPane2.class);

    private final GraphicalTimetableViewWithSave graphicalTimetableView;
    private final GTLayeredPane2 scrollPane;

    private final Tuple<TimeInterval> connection = new Tuple<TimeInterval>();

    private final JButton newButton;

    private ApplicationModel model;

    public FreightNetPane2() {
        setLayout(new BorderLayout());
        graphicalTimetableView = new net.parostroj.timetable.gui.components.GraphicalTimetableViewWithSave();
        graphicalTimetableView.setDrawFactory(new ManagedFreightGTDrawFactory());
        scrollPane = new GTLayeredPane2(graphicalTimetableView);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        newButton = new JButton(ResourceLoader.getString("button.new"));
        newButton.setEnabled(false);
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.getDiagram().getFreightNet().addConnection(connection.first, connection.second);
                connection.first = null;
                connection.second = null;
                graphicalTimetableView.repaint();
                newButton.setEnabled(false);
            }
        });
        buttonPanel.add(newButton);
        add(buttonPanel, BorderLayout.NORTH);
    }

    @Override
    public void saveToPreferences(AppPreferences prefs) {
        prefs.setString("freightnet.gtv", graphicalTimetableView.getSettings().getStorageString());
    }

    @Override
    public void loadFromPreferences(AppPreferences prefs) {
        GTViewSettings gtvs = null;
        try {
            gtvs = GTViewSettings.parseStorageString(prefs.getString("trains.gtv", null));
        } catch (Exception e) {
            // use default values
            log.warn("Wrong GTView settings - using default values.");
        }
        if (gtvs != null) {
            graphicalTimetableView.setSettings(graphicalTimetableView.getSettings().merge(gtvs));
        }
    }

    private boolean checkEnabled() {
        boolean result = false;
        if (connection.first.getOwnerAsNode() == connection.second.getOwnerAsNode()) {
            result = connection.first.getStart() < connection.second.getEnd();
        }
        return result;
    }

    public void setModel(ApplicationModel model) {
        this.model = model;
        HighlightSelection hts = new HighlightSelection();
        graphicalTimetableView.setSettings(
                graphicalTimetableView.getSettings().set(GTViewSettings.Key.HIGHLIGHTED_TRAINS, hts));
        graphicalTimetableView.setTrainSelector(hts);
        model.addListener(new ApplicationModelListener() {
            public void modelChanged(ApplicationModelEvent event) {
                if (event.getType() == ApplicationModelEventType.SET_DIAGRAM_CHANGED) {
                    scrollPane.setTrainDiagram(event.getModel().getDiagram());
                    graphicalTimetableView.setTrainDiagram(event.getModel().getDiagram());
                }
            }
        });
    }
}
