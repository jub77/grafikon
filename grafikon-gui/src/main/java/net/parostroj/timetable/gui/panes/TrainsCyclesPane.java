/*
 * TrainsCyclesPane.java
 *
 * Created on 20.5.2010, 20:24:43
 */
package net.parostroj.timetable.gui.panes;

import java.awt.Color;
import javax.swing.JScrollPane;
import net.parostroj.timetable.gui.AppPreferences;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelListener;
import net.parostroj.timetable.gui.StorableGuiData;
import net.parostroj.timetable.gui.components.GTViewScrollPane;
import net.parostroj.timetable.gui.components.GTViewSettings;
import net.parostroj.timetable.gui.components.GraphicalTimetableView;
import net.parostroj.timetable.gui.components.GraphicalTimetableView.TrainColors;
import net.parostroj.timetable.gui.components.GraphicalTimetableViewWithSave;
import net.parostroj.timetable.gui.components.HighlightedTrains;
import net.parostroj.timetable.gui.components.TrainColorChooser;
import net.parostroj.timetable.gui.components.TrainSelector;
import net.parostroj.timetable.gui.views.TCDelegate;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainsCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TrainsCyclePane.
 *
 * @author jub
 */
public class TrainsCyclesPane extends javax.swing.JPanel implements StorableGuiData {

    private static final Logger LOG = LoggerFactory.getLogger(TrainsCyclesPane.class.getName());
    private TCDelegate delegate;

    private class HighligterAndSelector implements HighlightedTrains, TrainSelector, TrainColorChooser, ApplicationModelListener {

        private TrainColorChooser chooserDelegate;
        private TrainSelector selectorDelegate;

        public HighligterAndSelector(TrainColorChooser chooser, TrainSelector selector) {
            this.chooserDelegate = chooser;
            this.selectorDelegate = selector;
        }
        private TrainsCycle last;

        @Override
        public boolean isHighlighedInterval(TimeInterval interval) {
            TimeInterval selectedInterval = selectorDelegate.getSelectedTrainInterval();
            if (selectedInterval == null)
                return false;
            else
                return selectedInterval.getTrain() == interval.getTrain();
        }

        @Override
        public void modelChanged(ApplicationModelEvent event) {
            if (delegate.transformEventType(event.getType()) == TCDelegate.Action.SELECTED_CHANGED) {
                last = delegate.getSelectedCycle(event.getModel());
                graphicalTimetableView.repaint();
            } else if (delegate.transformEventType(event.getType()) == TCDelegate.Action.MODIFIED_CYCLE) {
                if (event.getObject() == last) {
                    graphicalTimetableView.repaint();
                }
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
    }

    /** Creates new form TrainsCyclesPane */
    public TrainsCyclesPane() {
        initComponents();
        graphicalTimetableView = new GraphicalTimetableViewWithSave();
        JScrollPane scrollPane = new GTViewScrollPane(graphicalTimetableView);
        splitPane.setBottomComponent(scrollPane);
    }

    public void setModel(final ApplicationModel model, final TCDelegate delegate, TrainColorChooser chooser) {
        this.delegate = delegate;
        HighligterAndSelector hts = new HighligterAndSelector(chooser, trainListView);
        listView.setModel(model, delegate);
        detailsView.setModel(model, delegate);
        trainListView.setModel(model, delegate);
        model.addListener(new ApplicationModelListener() {

            @Override
            public void modelChanged(ApplicationModelEvent event) {
                switch (event.getType()) {
                    case SET_DIAGRAM_CHANGED:
                        graphicalTimetableView.setTrainDiagram(model.getDiagram());
                        break;
                    default:
                        // nothing
                        break;
                }
            }
        });
        graphicalTimetableView.setTrainColors(TrainColors.BY_COLOR_CHOOSER, hts);
        model.addListener(hts);
        graphicalTimetableView.setHTrains(hts);
        graphicalTimetableView.setTrainSelector(hts);
    }

    private String getKey(String suffix) {
        String prefix = null;
        switch(delegate.getType()) {
            case DRIVER_CYCLE:
                prefix = "driver"; break;
            case ENGINE_CYCLE:
                prefix = "engine"; break;
            case TRAIN_UNIT_CYCLE:
                prefix = "trainunit"; break;
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
            graphicalTimetableView.setSettings(GTViewSettings.parseStorageString(prefs.getString(getKey("gtv"), null)));
        } catch (Exception e) {
            LOG.warn("Wrong GTView settings - using default values.");
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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

        detailsView.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 0));
        innerPanel.add(detailsView, java.awt.BorderLayout.NORTH);

        listView.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 0));
        innerPanel.add(listView, java.awt.BorderLayout.CENTER);

        panel.add(innerPanel, java.awt.BorderLayout.LINE_START);

        splitPane.setTopComponent(panel);

        add(splitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private net.parostroj.timetable.gui.views.TCDetailsView2 detailsView;
    private javax.swing.JPanel innerPanel;
    private net.parostroj.timetable.gui.views.TCListView listView;
    private javax.swing.JPanel panel;
    private javax.swing.JSplitPane splitPane;
    private net.parostroj.timetable.gui.views.TCTrainListView trainListView;
    // End of variables declaration//GEN-END:variables

    private GraphicalTimetableViewWithSave graphicalTimetableView;
}
