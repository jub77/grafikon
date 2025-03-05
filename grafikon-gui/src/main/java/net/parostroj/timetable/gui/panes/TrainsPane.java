/*
 * TrainsPane.java
 *
 * Created on 3. září 2007, 10:22
 */
package net.parostroj.timetable.gui.panes;

import java.awt.BorderLayout;

import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.components.GTViewSettings;
import net.parostroj.timetable.gui.components.GraphicalTimetableView;
import net.parostroj.timetable.gui.ini.IniConfig;
import net.parostroj.timetable.gui.ini.IniConfigSection;
import net.parostroj.timetable.gui.ini.StorableGuiData;
import net.parostroj.timetable.gui.views.TrainListView.TreeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trains pane.
 *
 * @author jub
 */
public class TrainsPane extends javax.swing.JPanel implements StorableGuiData {

    private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(TrainsPane.class);

    public TrainsPane(ApplicationModel model) {
        initComponents(model);
    }

    public void resizeColumns() {
        trainView.resizeColumns();
    }

    public void sortColumns() {
        trainView.sortColumns();
    }

    @Override
    public IniConfigSection loadFromPreferences(IniConfig prefs) {
        IniConfigSection section = prefs.getSection("trains");
        int dividerLoc = section.get("divider", Integer.class, splitPane.getDividerLocation());
        int div = section.get("divider.2", Integer.class, trainsSplitPane.getDividerLocation());
        int preferredWidth = trainListView.getPreferredSize().width;
        if (preferredWidth < div) {
            trainsSplitPane.setDividerLocation(div);
        }
        GTViewSettings gtvs = null;
        try {
            gtvs = GTViewSettings.parseStorageString(section.get("gtv"));
        } catch (Exception e) {
            // use default values
            log.warn("Wrong GTView settings - using default values.");
        }
        if (gtvs != null) {
            graphicalTimetableView.setSettings(graphicalTimetableView.getSettings().merge(gtvs));
        }
        scrollPane.setVisible(section.get("show.gtview", Boolean.class, true));
        if (scrollPane.isVisible()) {
            splitPane.setDividerLocation(dividerLoc);
        } else {
            splitPane.setLastDividerLocation(dividerLoc);
        }
        String treeType = section.get("listtype", "TYPES");
        TreeType treeTypeEnum = TreeType.TYPES;
        try {
            treeTypeEnum = TreeType.valueOf(treeType);
        } catch (IllegalArgumentException e) {
            // ignore unknown value
        }
        trainListView.setTreeType(treeTypeEnum);

        trainView.loadFromPreferences(prefs);
        return section;
    }

    @Override
    public IniConfigSection saveToPreferences(IniConfig prefs) {
        IniConfigSection section = prefs.getSection("trains");
        section.put("divider", scrollPane.isVisible() ? splitPane.getDividerLocation() : splitPane.getLastDividerLocation());
        section.put("divider.2", trainsSplitPane.getDividerLocation());
        // save if the gtview in trains pane is visible
        section.put("show.gtview", scrollPane.isVisible());
        section.put("gtv", graphicalTimetableView.getSettings().getStorageString());
        section.put("listtype", trainListView.getTreeType().name());
        trainView.saveToPreferences(prefs);
        return section;
    }

    public void editColumns() {
        trainView.editColumns();
    }

    private void initComponents(ApplicationModel model) {
        splitPane = new javax.swing.JSplitPane();
        graphicalTimetableView = GraphicalTimetableView.newBuilder()
                .withSave()
                .forTrains(model.getMediator())
                .build();
        scrollPane = graphicalTimetableView.newScrollPaneWithButtons();
        trainsSplitPane = new javax.swing.JSplitPane();
        trainListView = new net.parostroj.timetable.gui.views.TrainListView(model);
        trainView = new net.parostroj.timetable.gui.views.TrainView(model);

        splitPane.setDividerLocation(350);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        setLayout(new BorderLayout(0, 0));

        splitPane.setBottomComponent(scrollPane);

        splitPane.setLeftComponent(trainsSplitPane);
        trainsSplitPane.setLeftComponent(trainListView);
        trainsSplitPane.setRightComponent(trainView);
        add(splitPane);
    }

    private net.parostroj.timetable.gui.components.GraphicalTimetableView graphicalTimetableView;
    private javax.swing.JSplitPane trainsSplitPane;
    private javax.swing.JComponent scrollPane;
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
