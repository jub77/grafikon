/*
 * TrainsCyclesPane.java
 *
 * Created on 20.5.2010, 20:24:43
 */
package net.parostroj.timetable.gui.panes;

import java.awt.Color;

import net.parostroj.timetable.gui.AppPreferences;
import net.parostroj.timetable.gui.StorableGuiData;
import net.parostroj.timetable.gui.components.*;
import net.parostroj.timetable.gui.components.GTViewSettings.TrainColors;
import net.parostroj.timetable.gui.views.TCDelegate;
import net.parostroj.timetable.gui.views.TCDelegate.Action;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TrainsCyclePane.
 *
 * @author jub
 */
public class TrainsCyclesPane extends javax.swing.JPanel implements StorableGuiData, TCDelegate.Listener {

    private static final Logger LOG = LoggerFactory.getLogger(TrainsCyclesPane.class.getName());
    private TCDelegate delegate;

    private class HighligterAndSelector implements HighlightedTrains, TrainSelector, TrainColorChooser, TCDelegate.Listener {

        private final TrainColorChooser chooserDelegate;
        private final TrainSelector selectorDelegate;

        public HighligterAndSelector(TrainColorChooser chooser, TrainSelector selector) {
            this.chooserDelegate = chooser;
            this.selectorDelegate = selector;
        }
        private TrainsCycle last;

        @Override
        public boolean isHighlighedInterval(TimeInterval interval) {
            TimeInterval selectedInterval = selectorDelegate.getSelectedTrainInterval();
            if (selectedInterval == null) {
                return false;
            } else {
                return selectedInterval.getTrain() == interval.getTrain();
            }
        }

        @Override
        public void tcEvent(Action action, TrainsCycle cycle, Train train) {
            if (action == TCDelegate.Action.SELECTED_CHANGED) {
                last = delegate.getSelectedCycle();
                graphicalTimetableView.repaint();
            } else if (action == TCDelegate.Action.MODIFIED_CYCLE) {
                if (cycle == last) {
                    graphicalTimetableView.repaint();
                }
            } else if (action == TCDelegate.Action.REFRESH) {
                graphicalTimetableView.repaint();
            }
        }

        @Override
        public Color getColor() {
            return Color.GREEN;
        }

        @Override
        public void selectTrainInterval(TimeInterval interval) {
            selectorDelegate.selectTrainInterval(interval);
            graphicalTimetableView.repaint();
        }

        @Override
        public TimeInterval getSelectedTrainInterval() {
            return selectorDelegate.getSelectedTrainInterval();
        }

        @Override
        public Color getIntervalColor(TimeInterval interval) {
            if (last != null && interval.getTrain().isCovered(last, interval)) {
                return Color.RED;
            }
            return chooserDelegate.getIntervalColor(interval);
        }

        @Override
        public void editSelected() {
            // do nothing
        }
    }

    /** Creates new form TrainsCyclesPane */
    public TrainsCyclesPane() {
        initComponents();
        graphicalTimetableView = new GraphicalTimetableViewWithSave();
        GTLayeredPane scrollPane = new GTLayeredPane(graphicalTimetableView);
        splitPane.setBottomComponent(scrollPane);
    }

    @Override
    public void tcEvent(Action action, TrainsCycle cycle, Train train) {
        if (action == Action.DIAGRAM_CHANGE) {
            graphicalTimetableView.setTrainDiagram(delegate.getTrainDiagram());
        }
    }

    public void setModel(TCDelegate delegate, TrainColorChooser chooser) {
        this.delegate = delegate;
        HighligterAndSelector hts = new HighligterAndSelector(chooser, trainListView);
        GTViewSettings settings = graphicalTimetableView.getSettings();
        settings.set(GTViewSettings.Key.TRAIN_COLORS, TrainColors.BY_COLOR_CHOOSER);
        settings.set(GTViewSettings.Key.TRAIN_COLOR_CHOOSER, hts);
        settings.set(GTViewSettings.Key.HIGHLIGHTED_TRAINS, hts);
        graphicalTimetableView.setSettings(settings);
        delegate.addListener(hts);
        delegate.addListener(this);
        graphicalTimetableView.setTrainSelector(hts);
        trainListView.setModel(delegate);
        listView.setModel(delegate);
        detailsView.setModel(delegate);
    }

    private String getKey(String suffix) {
        String prefix = "custom";
        if (TrainsCycleType.DRIVER_CYCLE.equals(delegate.getType())) {
            prefix = "driver";
        } else if (TrainsCycleType.ENGINE_CYCLE.equals(delegate.getType())) {
            prefix = "engine";
        } else if (TrainsCycleType.TRAIN_UNIT_CYCLE.equals(delegate.getType())) {
            prefix = "trainunit";
        }
        return String.format("cycles.%s.%s", prefix, suffix);
    }

    @Override
    public void saveToPreferences(AppPreferences prefs) {
        prefs.setInt(getKey("divider"), splitPane.getDividerLocation());
        prefs.setString(getKey("gtv"), graphicalTimetableView.getSettings().getStorageString());
    }

    @Override
    public void loadFromPreferences(AppPreferences prefs) {
        splitPane.setDividerLocation(prefs.getInt(getKey("divider"), -1));
        try {
            GTViewSettings gtvs = GTViewSettings.parseStorageString(prefs.getString(getKey("gtv"), null));
            if (gtvs != null) {
                graphicalTimetableView.setSettings(graphicalTimetableView.getSettings().merge(gtvs));
            }
        } catch (Exception e) {
            LOG.warn("Wrong GTView settings - using default values.");
        }
    }

    private void initComponents() {
        splitPane = new javax.swing.JSplitPane();
        panel = new javax.swing.JPanel();
        trainListView = new net.parostroj.timetable.gui.views.TCTrainListView();
        innerPanel = new javax.swing.JPanel();
        detailsView = new net.parostroj.timetable.gui.views.TCDetailsView2();
        listView = new net.parostroj.timetable.gui.views.TCListView();

        setLayout(new java.awt.BorderLayout());

        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        panel.setLayout(new java.awt.BorderLayout());

        trainListView.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(trainListView, java.awt.BorderLayout.CENTER);

        innerPanel.setLayout(new java.awt.BorderLayout());

        detailsView.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 0));
        innerPanel.add(detailsView, java.awt.BorderLayout.NORTH);

        listView.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 0));
        innerPanel.add(listView, java.awt.BorderLayout.CENTER);

        panel.add(innerPanel, java.awt.BorderLayout.LINE_START);

        splitPane.setTopComponent(panel);

        add(splitPane, java.awt.BorderLayout.CENTER);
    }

    private net.parostroj.timetable.gui.views.TCDetailsView2 detailsView;
    private javax.swing.JPanel innerPanel;
    private net.parostroj.timetable.gui.views.TCListView listView;
    private javax.swing.JPanel panel;
    private javax.swing.JSplitPane splitPane;
    private net.parostroj.timetable.gui.views.TCTrainListView trainListView;

    private final GraphicalTimetableViewWithSave graphicalTimetableView;
}
