package net.parostroj.timetable.gui.panes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.components.GTLayeredPane2;
import net.parostroj.timetable.gui.components.GTViewSettings;
import net.parostroj.timetable.gui.components.GraphicalTimetableView;
import net.parostroj.timetable.gui.components.GraphicalTimetableView.MouseOverHandler;
import net.parostroj.timetable.gui.components.GraphicalTimetableViewWithSave;
import net.parostroj.timetable.gui.dialogs.EditFNConnetionDialog;
import net.parostroj.timetable.gui.ini.IniConfig;
import net.parostroj.timetable.gui.ini.IniConfigSection;
import net.parostroj.timetable.gui.ini.StorableGuiData;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.model.FNConnection;
import net.parostroj.timetable.model.FreightNet;
import net.parostroj.timetable.model.TimeConverter;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.EventProcessing;
import net.parostroj.timetable.output2.gt.GTDraw;
import net.parostroj.timetable.output2.gt.HighlightedTrains;
import net.parostroj.timetable.output2.gt.ManagedFreightGTDraw;
import net.parostroj.timetable.output2.gt.ManagedFreightGTDrawFactory;
import net.parostroj.timetable.output2.gt.RegionCollectorAdapter;
import net.parostroj.timetable.output2.gt.RegionSelector;
import net.parostroj.timetable.output2.gt.SelectorUtils;
import net.parostroj.timetable.utils.Tuple;
import net.parostroj.timetable.visitors.AbstractEventVisitor;

public class FreightNetPane2 extends JPanel implements StorableGuiData {

    private static final long serialVersionUID = 1L;

	private final class ConnectionSelector implements RegionSelector<FNConnection>, ManagedFreightGTDraw.Highlight {

        private FNConnection selected = null;

        @Override
        public boolean regionsSelected(List<FNConnection> regions) {
            return this.setSelected(SelectorUtils.select(regions, selected));
        }

        @Override
        public boolean editSelected() {
            if (selected != null) {
                EditFNConnetionDialog dialog = new EditFNConnetionDialog(GuiComponentUtils.getWindow(FreightNetPane2.this), true);
                dialog.edit(FreightNetPane2.this, selected, model.getDiagram());
                return true;
            } else {
                return false;
            }
        }

        @Override
        public FNConnection getSelectedConnection() {
            return selected;
        }

        @Override
        public List<FNConnection> getSelected() {
            return selected == null ? Collections.<FNConnection>emptyList() : Collections.singletonList(selected);
        }

        @Override
        public Color getColor() {
            return Color.GREEN;
        }

        public boolean setSelected(FNConnection conn) {
            FNConnection current = selected;
            selected = conn;
            if (conn != current) {
                graphicalTimetableView.repaint();
            }
            deleteButton.setEnabled(selected != null);
            editButton.setEnabled(selected != null);
            upButton.setEnabled(checkEnabledMoveConnection(selected, 1));
            downButton.setEnabled(checkEnabledMoveConnection(selected, -1));
            updateInfo();
            return selected != null;
        }
    }

    private final class HighlightSelection implements HighlightedTrains, RegionSelector<TimeInterval> {

        @Override
        public boolean isHighlighedInterval(TimeInterval interval) {
            return interval == connection.first || interval == connection.second;
        }

        @Override
        public Color getColor(TimeInterval interval) {
            return interval == connection.first ? Color.GREEN : Color.CYAN;
        }

        @Override
        public boolean regionsSelected(List<TimeInterval> intervals) {
            TimeInterval interval = this.chooseInterval(intervals);
            boolean enabled = false;
            if (interval == null) {
                connection.first = null;
                connection.second = null;
            } else {
                if (connection.first != null && connection.second != null) {
                    connection.first = null;
                    connection.second = null;
                }
                if (connection.first == null) {
                    connection.first = interval;
                } else {
                    if (connection.first.getOwnerAsNode() != interval.getOwnerAsNode() ||
                            connection.first == interval) {
                        connection.first = interval;
                    } else {
                        connection.second = interval;
                    }
                    enabled = checkEnabled();
                }
            }
            newButton.setEnabled(enabled);
            graphicalTimetableView.repaint();
            updateInfo();
            return interval != null;
        }

        @Override
        public List<TimeInterval> getSelected() {
            return connection.toList();
        }

        private TimeInterval lastInterval;
        private final Predicate<TimeInterval> nodeIntervalFilter = interval -> interval.isNodeOwner();

        private TimeInterval chooseInterval(List<TimeInterval> intervals) {
            TimeInterval selected = SelectorUtils.select(
                    Iterables.filter(intervals, interval -> interval.isStop()),
                    lastInterval,
                    nodeIntervalFilter);
            lastInterval = selected;
            return selected;
        }

        @Override
        public boolean editSelected() {
            // nothing ...
            return false;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(FreightNetPane2.class);

    private final GraphicalTimetableViewWithSave graphicalTimetableView;
    private final GTLayeredPane2 scrollPane;

    private final Tuple<TimeInterval> connection = new Tuple<>();

    private final JButton newButton;
    private final JButton deleteButton;
    private final JButton editButton;
    private final JButton upButton;
    private final JButton downButton;
    private final JTextField infoTextField;

    private ApplicationModel model;

    private final ConnectionSelector selector;

    public FreightNetPane2() {
        setLayout(new BorderLayout());
        graphicalTimetableView = new net.parostroj.timetable.gui.components.GraphicalTimetableViewWithSave();
        graphicalTimetableView
                .setSettings(graphicalTimetableView.getSettings()
                        .set(GTViewSettings.Key.ORIENTATION_MENU, false)
                        .set(GTViewSettings.Key.TYPE_LIST,
                                Arrays.asList(
                                        GTDraw.Type.CLASSIC_STATION_STOPS,
                                        GTDraw.Type.WITH_TRACKS)));
        selector = new ConnectionSelector();
        graphicalTimetableView.setDrawFactory(new ManagedFreightGTDrawFactory());
        graphicalTimetableView.setParameter(ManagedFreightGTDraw.HIGHLIGHT_KEY, selector);
        RegionCollectorAdapter<FNConnection> collector = new RegionCollectorAdapter<FNConnection>() {
            @Override
            public void processEvent(Event event) {
                AbstractEventVisitor visitor = new AbstractEventVisitor() {
                    @Override
                    public void visitFreightNetEvent(Event event) {
                        if (event.getType() == Event.Type.ADDED && event.getObject() instanceof FNConnection &&
                                getSelector().getSelected().contains(event.getObject())) {
                            getSelector().regionsSelected(Collections.<FNConnection>emptyList());
                        }
                    }
                };
                EventProcessing.visit(event, visitor);
            }
        };
        collector.setSelector(selector);
        graphicalTimetableView.addRegionCollector(FNConnection.class, collector);
        scrollPane = new GTLayeredPane2(graphicalTimetableView);
        // change cursor to hand if the stop interval can be selected
        graphicalTimetableView.setParameter(GraphicalTimetableView.MOUSE_OVER_HANDLER_KEY,
                (MouseOverHandler) intervals -> {
                    if (!intervals.isEmpty() && intervals.stream().anyMatch(i -> i.isStop())) {
                        scrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    } else {
                        scrollPane.setCursor(null);
                    }
                });
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        newButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);
        newButton.setEnabled(false);
        newButton.addActionListener(e -> {
            FreightNet fn = model.getDiagram().getFreightNet();
            FNConnection newOrExistingCconnection = fn.getConnection(connection.first, connection.second);
            if (newOrExistingCconnection == null) {
                newOrExistingCconnection =  fn.addConnection(connection.first, connection.second);
            }
            connection.first = null;
            connection.second = null;
            graphicalTimetableView.repaint();
            newButton.setEnabled(false);
            selector.setSelected(newOrExistingCconnection);
            updateInfo();
        });
        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> {
            FNConnection toBeDeleted = selector.getSelectedConnection();
            selector.setSelected(null);
            model.getDiagram().getFreightNet().removeConnection(toBeDeleted);
        });
        editButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 2);
        editButton.setEnabled(false);
        editButton.addActionListener(e -> {
            EditFNConnetionDialog dialog = new EditFNConnetionDialog(
                    GuiComponentUtils.getWindow(FreightNetPane2.this),
                    true);
            dialog.edit(FreightNetPane2.this, selector.selected, model.getDiagram());
        });

        upButton = GuiComponentUtils.createButton(GuiIcon.ARROW_UP, 2);
        upButton.setEnabled(false);
        upButton.addActionListener(e -> {
            moveConnection(1);
        });
        downButton = GuiComponentUtils.createButton(GuiIcon.ARROW_DOWN, 2);
        downButton.setEnabled(false);
        downButton.addActionListener(e -> {
            moveConnection(-1);
        });

        buttonPanel.add(newButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        add(buttonPanel, BorderLayout.NORTH);

        infoTextField = new JTextField();
        infoTextField.setEditable(false);
        buttonPanel.add(infoTextField);

        buttonPanel.add(upButton);
        buttonPanel.add(downButton);

        infoTextField.setColumns(35);
    }

    @Override
    public IniConfigSection saveToPreferences(IniConfig prefs) {
        IniConfigSection section = prefs.getSection("freigh.net");
        section.put("gtv", graphicalTimetableView.getSettings().getStorageString());
        return section;
    }

    @Override
    public IniConfigSection loadFromPreferences(IniConfig prefs) {
        IniConfigSection section = prefs.getSection("freigh.net");
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
        return section;
    }

    private void moveConnection(int change) {
        FNConnection conn = selector.getSelectedConnection();
        FreightNet net = model.getDiagram().getFreightNet();
        int currentIndex = net.getTrainsFrom(conn.getFrom()).indexOf(conn);
        net.moveConnection(conn, currentIndex + change);
        graphicalTimetableView.repaint();
        upButton.setEnabled(checkEnabledMoveConnection(conn, 1));
        downButton.setEnabled(checkEnabledMoveConnection(conn, -1));
    }

    private boolean checkEnabledMoveConnection(FNConnection conn, int indexChange) {
        if (conn == null) {
            return false;
        }
        FreightNet net = model.get().getFreightNet();
        List<FNConnection> conns = net.getTrainsFrom(conn.getFrom());
        int currentIndex = conns.indexOf(conn);
        int newIndex = currentIndex + indexChange;
        return newIndex >=0 && newIndex < conns.size();
    }

    private boolean checkEnabled() {
        return connection.first != null && connection.second != null &&
                connection.first.getOwnerAsNode() == connection.second.getOwnerAsNode() &&
                !connection.second.isLast() && !connection.first.isFirst();
    }

    private void updateInfo() {
        StringBuilder builder = new StringBuilder();
        FNConnection conn = selector.getSelectedConnection();
        TimeInterval from = conn != null ? conn.getFrom() : connection.first;
        TimeInterval to = conn != null ? conn.getTo() : connection.second;
        if (from != null) {
            builder.append(from.getOwnerAsNode().getAbbr()).append(": ");
            builder.append(getIntervalInfo(from, true));
        }
        if (to != null) {
            builder.append(" -> ").append(getIntervalInfo(to, false));
        }
        infoTextField.setText(builder.toString());
    }

    private String getIntervalInfo(TimeInterval interval, boolean first) {
        TimeConverter converter = model.getDiagram().getTimeConverter();
        return String.format("%s [%s]", interval.getTrain().getDefaultName(),
                converter.convertIntToText(first ? interval.getStart() : interval.getEnd()));
    }

    public void setModel(ApplicationModel model) {
        this.model = model;
        HighlightSelection hts = new HighlightSelection();
        graphicalTimetableView.setParameter(GTDraw.HIGHLIGHTED_TRAINS, hts);
        graphicalTimetableView.setRegionSelector(hts, TimeInterval.class);
        model.addListener(event -> {
            if (event.getType() == ApplicationModelEventType.SET_DIAGRAM_CHANGED) {
                graphicalTimetableView.setTrainDiagram(event.getModel().getDiagram());
            }
        });
    }
}
