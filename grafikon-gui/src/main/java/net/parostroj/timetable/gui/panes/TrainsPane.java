/*
 * TrainsPane.java
 *
 * Created on 3. září 2007, 10:22
 */
package net.parostroj.timetable.gui.panes;

import java.awt.BorderLayout;
import java.awt.Color;

import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.components.GTLayeredPane2;
import net.parostroj.timetable.gui.components.GTViewSettings;
import net.parostroj.timetable.gui.utils.NormalHTS;
import net.parostroj.timetable.gui.views.TrainListView.TreeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trains pane.
 *
 * @author jub
 */
public class TrainsPane extends javax.swing.JPanel implements StorableGuiData {

    private static final Logger LOG = LoggerFactory.getLogger(TrainsPane.class.getName());

    /** Creates new form TrainsPane */
    public TrainsPane() {
        initComponents();
    }

    public void resizeColumns() {
        trainView.resizeColumns();
    }

    public void sortColumns() {
        trainView.sortColumns();
    }

    /**
     * sets model.
     *
     * @param model application model
     */
    public void setModel(final ApplicationModel model) {
        trainListView.setModel(model);
        trainView.setModel(model);
        NormalHTS hts = new NormalHTS(model, Color.GREEN, graphicalTimetableView);
        graphicalTimetableView.setSettings(
                graphicalTimetableView.getSettings().set(GTViewSettings.Key.HIGHLIGHTED_TRAINS, hts));
        graphicalTimetableView.setTrainSelector(hts);
        model.addListener(new ApplicationModelListener() {
            public void modelChanged(ApplicationModelEvent event) {
                if (event.getType() == ApplicationModelEventType.SET_DIAGRAM_CHANGED) {
                    scrollPane.setTrainDiagram(event.getModel().getDiagram());
                }
            }
        });
    }

    @Override
    public void loadFromPreferences(AppPreferences prefs) {
        int dividerLoc = prefs.getInt("trains.divider", splitPane.getDividerLocation());
        int div = prefs.getInt("trains.divider.2", trainsSplitPane.getDividerLocation());
        int preferredWidth = trainListView.getPreferredSize().width;
        if (preferredWidth < div) {
            trainsSplitPane.setDividerLocation(div);
        }
        GTViewSettings gtvs = null;
        try {
            gtvs = GTViewSettings.parseStorageString(prefs.getString("trains.gtv", null));
        } catch (Exception e) {
            // use default values
            LOG.warn("Wrong GTView settings - using default values.");
        }
        if (gtvs != null) {
            graphicalTimetableView.setSettings(graphicalTimetableView.getSettings().merge(gtvs));
        }
        scrollPane.setVisible(prefs.getBoolean("trains.show.gtview", true));
        if (scrollPane.isVisible()) {
            splitPane.setDividerLocation(dividerLoc);
        } else {
            splitPane.setLastDividerLocation(dividerLoc);
        }
        String treeType = prefs.getString("trains.listtype", "TYPES");
        TreeType treeTypeEnum = TreeType.TYPES;
        try {
            treeTypeEnum = TreeType.valueOf(treeType);
        } catch (IllegalArgumentException e) {
            // ignore unknown value
        }
        trainListView.setTreeType(treeTypeEnum);

        trainView.loadFromPreferences(prefs);
    }

    @Override
    public void saveToPreferences(AppPreferences prefs) {
        prefs.setInt("trains.divider", scrollPane.isVisible() ? splitPane.getDividerLocation() : splitPane.getLastDividerLocation());
        prefs.setInt("trains.divider.2", trainsSplitPane.getDividerLocation());
        // save if the gtview in trains pane is visible
        prefs.setBoolean("trains.show.gtview", scrollPane.isVisible());
        prefs.setString("trains.gtv", graphicalTimetableView.getSettings().getStorageString());
        prefs.setString("trains.listtype", trainListView.getTreeType().name());
        trainView.saveToPreferences(prefs);
    }

    public void editColumns() {
        trainView.editColumns();
    }

    private void initComponents() {
        splitPane = new javax.swing.JSplitPane();
        graphicalTimetableView = new net.parostroj.timetable.gui.components.GraphicalTimetableViewWithSave();
        scrollPane = new GTLayeredPane2(graphicalTimetableView);
        trainsSplitPane = new javax.swing.JSplitPane();
        trainListView = new net.parostroj.timetable.gui.views.TrainListView();
        trainView = new net.parostroj.timetable.gui.views.TrainView();

        splitPane.setDividerLocation(350);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        setLayout(new BorderLayout(0, 0));

        splitPane.setBottomComponent(scrollPane);

        splitPane.setLeftComponent(trainsSplitPane);
        trainsSplitPane.setLeftComponent(trainListView);
        trainsSplitPane.setRightComponent(trainView);
        add(splitPane);
    }

    private net.parostroj.timetable.gui.components.GraphicalTimetableViewWithSave graphicalTimetableView;
    private javax.swing.JSplitPane trainsSplitPane;
    private GTLayeredPane2 scrollPane;
    private javax.swing.JSplitPane splitPane;
    private net.parostroj.timetable.gui.views.TrainListView trainListView;
    private net.parostroj.timetable.gui.views.TrainView trainView;

    public void setVisibilityOfGTView(boolean state) {
        if (state) {
            splitPane.setDividerLocation(splitPane.getLastDividerLocation());
        } else {
            splitPane.setLastDividerLocation(splitPane.getDividerLocation());
        }
        scrollPane.setVisible(state);
    }
}
