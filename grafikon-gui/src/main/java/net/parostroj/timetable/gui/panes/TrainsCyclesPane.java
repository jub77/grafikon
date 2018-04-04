/*
 * TrainsCyclesPane.java
 *
 * Created on 20.5.2010, 20:24:43
 */
package net.parostroj.timetable.gui.panes;

import java.awt.Color;
import java.util.List;

import net.parostroj.timetable.gui.components.*;
import net.parostroj.timetable.gui.ini.IniConfig;
import net.parostroj.timetable.gui.ini.IniConfigSection;
import net.parostroj.timetable.gui.ini.StorableGuiData;
import net.parostroj.timetable.gui.views.TCDelegate;
import net.parostroj.timetable.gui.views.TCDelegate.Action;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.output2.gt.GTDraw.TrainColors;
import net.parostroj.timetable.output2.gt.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TrainsCyclePane.
 *
 * @author jub
 */
public class TrainsCyclesPane extends javax.swing.JPanel implements StorableGuiData, TCDelegate.Listener {

    private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(TrainsCyclesPane.class);

    private TCDelegate delegate;
    private String key;

    private class HighligterAndSelector implements HighlightedTrains, RegionSelector<TimeInterval>, TrainColorChooser, TCDelegate.Listener {

        private final TrainColorChooser chooserDelegate;
        private final RegionSelector<TimeInterval> selectorDelegate;

        public HighligterAndSelector(TrainColorChooser chooser, RegionSelector<TimeInterval> selector) {
            this.chooserDelegate = chooser;
            this.selectorDelegate = selector;
        }
        private TrainsCycle last;

        @Override
        public boolean isHighlighedInterval(TimeInterval interval) {
            return false;
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
        public Color getColor(TimeInterval interval) {
            return Color.GREEN;
        }

        @Override
        public boolean regionsSelected(List<TimeInterval> intervals) {
            boolean selected = selectorDelegate.regionsSelected(intervals);
            graphicalTimetableView.repaint();
            return selected;
        }

        @Override
        public List<TimeInterval> getSelected() {
            return selectorDelegate.getSelected();
        }

        @Override
        public Color getIntervalColor(TimeInterval interval) {
            if (last != null && interval.getTrain().isCovered(last, interval)) {
                return Color.RED;
            }
            return chooserDelegate.getIntervalColor(interval);
        }

        @Override
        public boolean editSelected() {
            // do nothing
            return false;
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
        graphicalTimetableView.setSettings(settings);
        graphicalTimetableView.setParameter(GTDraw.HIGHLIGHTED_TRAINS, hts);
        graphicalTimetableView.setParameter(GTDraw.TRAIN_COLOR_CHOOSER, hts);
        delegate.addListener(hts);
        delegate.addListener(this);
        graphicalTimetableView.setRegionSelector(hts, TimeInterval.class);
        trainListView.setModel(delegate);
        listView.setModel(delegate);
        detailsView.setModel(delegate);
    }

    public void setKey(String key) {
        this.key= key;
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public IniConfigSection saveToPreferences(IniConfig prefs) {
        IniConfigSection section = prefs.getSection(getKey());
        section.put("divider", splitPane.getDividerLocation());
        section.put("gtv", graphicalTimetableView.getSettings().getStorageString());
        return section;
    }

    @Override
    public IniConfigSection loadFromPreferences(IniConfig prefs) {
        IniConfigSection section = prefs.getSection(getKey());
        splitPane.setDividerLocation(section.get("divider", Integer.class, -1));
        try {
            GTViewSettings gtvs = GTViewSettings.parseStorageString(section.get("gtv"));
            if (gtvs != null) {
                graphicalTimetableView.setSettings(graphicalTimetableView.getSettings().merge(gtvs));
            }
        } catch (Exception e) {
            log.warn("Wrong GTView settings - using default values.");
        }
        return section;
    }

    private void initComponents() {
        splitPane = new javax.swing.JSplitPane();
        javax.swing.JPanel panel = new javax.swing.JPanel();
        trainListView = new net.parostroj.timetable.gui.views.TCTrainListView();
        javax.swing.JPanel innerPanel = new javax.swing.JPanel();
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
    private net.parostroj.timetable.gui.views.TCListView listView;
    private javax.swing.JSplitPane splitPane;
    private net.parostroj.timetable.gui.views.TCTrainListView trainListView;

    private final GraphicalTimetableViewWithSave graphicalTimetableView;
}
