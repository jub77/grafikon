package net.parostroj.timetable.gui.panes;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.components.*;
import net.parostroj.timetable.gui.dialogs.EditFNConnetionDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.output2.gt.*;
import net.parostroj.timetable.utils.Tuple;
import net.parostroj.timetable.visitors.AbstractEventVisitor;

import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

import javax.swing.JTextField;

public class FreightNetPane2 extends JPanel implements StorableGuiData {

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
            TimeInterval selected = SelectorUtils.select(intervals, lastInterval, nodeIntervalFilter);
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

    private final Tuple<TimeInterval> connection = new Tuple<TimeInterval>();

    private final JButton newButton;
    private final JButton deleteButton;
    private final JButton editButton;
    private final JTextField infoTextField;

    private ApplicationModel model;

    private final ConnectionSelector selector;

    public FreightNetPane2() {
        setLayout(new BorderLayout());
        graphicalTimetableView = new net.parostroj.timetable.gui.components.GraphicalTimetableViewWithSave();
        graphicalTimetableView.setSettings(graphicalTimetableView.getSettings().set(GTViewSettings.Key.ORIENTATION_MENU, false));
        selector = new ConnectionSelector();
        graphicalTimetableView.setDrawFactory(new ManagedFreightGTDrawFactory());
        graphicalTimetableView.setParameter(ManagedFreightGTDraw.HIGHLIGHT, selector);
        RegionCollectorAdapter<FNConnection> collector = new RegionCollectorAdapter<FNConnection>() {
            @Override
            public void processEvent(GTEvent<?> event) {
                AbstractEventVisitor visitor = new AbstractEventVisitor() {
                    @Override
                    public void visit(FreightNetEvent event) {
                        if (event.getType() == GTEventType.FREIGHT_NET_CONNECTION_REMOVED &&
                                getSelector().getSelected().contains(event.getConnection())) {
                            getSelector().regionsSelected(Collections.<FNConnection>emptyList());
                        }
                    }
                };
                event.accept(visitor);
            }
        };
        collector.setSelector(selector);
        graphicalTimetableView.addRegionCollector(FNConnection.class, collector);
        scrollPane = new GTLayeredPane2(graphicalTimetableView);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        newButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);
        newButton.setEnabled(false);
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FreightNet fn = model.getDiagram().getFreightNet();
                if (fn.getConnection(connection.first, connection.second) == null) {
                    fn.addConnection(connection.first, connection.second);
                }
                connection.first = null;
                connection.second = null;
                graphicalTimetableView.repaint();
                newButton.setEnabled(false);
                updateInfo();
            }
        });
        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FNConnection toBeDeleted = selector.getSelectedConnection();
                selector.setSelected(null);
                model.getDiagram().getFreightNet().removeConnection(toBeDeleted);
            }
        });
        editButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 2);
        editButton.setEnabled(false);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditFNConnetionDialog dialog = new EditFNConnetionDialog(GuiComponentUtils.getWindow(FreightNetPane2.this), true);
                dialog.edit(FreightNetPane2.this, selector.selected, model.getDiagram());
            }
        });
        buttonPanel.add(newButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        add(buttonPanel, BorderLayout.NORTH);

        infoTextField = new JTextField();
        infoTextField.setEditable(false);
        buttonPanel.add(infoTextField);
        infoTextField.setColumns(35);
    }

    @Override
    public Ini.Section saveToPreferences(Ini prefs) {
        Ini.Section section = AppPreferences.getSection(prefs, "freigh.net");
        section.put("gtv", graphicalTimetableView.getSettings().getStorageString());
        return section;
    }

    @Override
    public Ini.Section loadFromPreferences(Ini prefs) {
        Ini.Section section = AppPreferences.getSection(prefs, "freigh.net");
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
        return String.format("%s [%s]", interval.getTrain().getName(),
                converter.convertIntToText(first ? interval.getStart() : interval.getEnd()));
    }

    public void setModel(ApplicationModel model) {
        this.model = model;
        HighlightSelection hts = new HighlightSelection();
        graphicalTimetableView.setParameter(GTDraw.HIGHLIGHTED_TRAINS, hts);
        graphicalTimetableView.setRegionSelector(hts, TimeInterval.class);
        model.addListener(new ApplicationModelListener() {
            public void modelChanged(ApplicationModelEvent event) {
                if (event.getType() == ApplicationModelEventType.SET_DIAGRAM_CHANGED) {
                    graphicalTimetableView.setTrainDiagram(event.getModel().getDiagram());
                }
            }
        });
    }
}
