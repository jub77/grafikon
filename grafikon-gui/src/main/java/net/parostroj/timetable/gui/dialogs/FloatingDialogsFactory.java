package net.parostroj.timetable.gui.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.components.*;
import net.parostroj.timetable.gui.utils.NormalHTS;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.mediator.Mediator;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.model.events.GTEvent;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.model.events.TrainEvent;
import net.parostroj.timetable.model.events.TrainsCycleEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creation of floating dialogs.
 *
 * @author jub
 */
public class FloatingDialogsFactory {

    private static final Logger LOG = LoggerFactory.getLogger(FloatingDialogsFactory.class.getName());

    private static FloatingDialog createTrainsWithConflictsDialog(final Frame frame, final Mediator mediator, final ApplicationModel model) {
        final TrainsWithConflictsPanel panel = new TrainsWithConflictsPanel();
        panel.addTrainSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    JList list = (JList)e.getSource();
                    Wrapper<?> wrapper = (Wrapper<?>)list.getSelectedValue();
                    if (wrapper != null) {
                        if (wrapper.getElement() != model.getSelectedTrain()) {
                            model.setSelectedTrain((Train) wrapper.getElement());
                        }
                    }
                }
            }
        });
        final FloatingDialog dialog = new FloatingDialog(frame, panel, "dialog.trainconflicts.title", "train.conflicts");
        mediator.addColleague(new ApplicationGTEventColleague(true){

            @Override
            public void processTrainEvent(TrainEvent event) {
                switch (event.getType()) {
                    case TIME_INTERVAL_LIST:
                    case TECHNOLOGICAL:
                        panel.updateTrain((Train) event.getSource());
                        break;
                }
            }

            @Override
            public void processTrainDiagramEvent(TrainDiagramEvent event) {
                switch (event.getType()) {
                    case TRAIN_ADDED:
                        panel.updateTrain((Train)event.getObject());
                        break;
                    case TRAIN_REMOVED:
                        panel.removeTrain((Train)event.getObject());
                        break;
                }
            }

            @Override
            public void processApplicationEvent(ApplicationModelEvent event) {
                switch (event.getType()) {
                    case SELECTED_TRAIN_CHANGED:
                        panel.updateSelectedTrain((Train)event.getObject());
                        break;
                    case SET_DIAGRAM_CHANGED:
                        panel.setTrainComparator(model.getDiagram() != null ? new TrainComparator(TrainComparator.Type.ASC, model.getDiagram().getTrainsData().getTrainSortPattern()) : null);
                        panel.updateAllTrains(model.getDiagram() != null ? model.getDiagram().getTrains() : null);
                        break;
                }
            }


        });

        return dialog;
    }

    private static FloatingDialog createTrainsWithZeroWeightsDialog(final Frame frame, final Mediator mediator, final ApplicationModel model) {
        final TrainsWithZeroWeightsPanel panel = new TrainsWithZeroWeightsPanel();
        panel.addTrainSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    JList list = (JList)e.getSource();
                    Wrapper<?> wrapper = (Wrapper<?>)list.getSelectedValue();
                    if (wrapper != null) {
                        if (wrapper.getElement() != model.getSelectedTrain()) {
                            model.setSelectedTrain((Train) wrapper.getElement());
                        }
                    }
                }
            }
        });
        final FloatingDialog dialog = new FloatingDialog(frame, panel, "dialog.trainzeroweights.title", "train.zeroweights") {
            @Override
            public void setVisible(boolean b) {
                if (b) {
                    panel.setTrainComparator(model.getDiagram() != null ? new TrainComparator(TrainComparator.Type.ASC, model.getDiagram().getTrainsData().getTrainSortPattern()) : null);
                    panel.updateAllTrains(model.getDiagram() != null ? model.getDiagram().getTrains() : null);
                } else {
                    panel.removeAllTrains();
                }
                super.setVisible(b);
            }
        };
        mediator.addColleague(new ApplicationGTEventColleague(true){

            @Override
            public void receiveMessage(Object message) {
                // do not check if the dialog is not visible
                if (!dialog.isVisible()) {
                    return;
                }
                super.receiveMessage(message);
            }
            
            @Override
            public void processTrainEvent(TrainEvent event) {
                switch (event.getType()) {
                    case ATTRIBUTE:
                        if (event.getAttributeChange().getName().equals("weight"))
                            panel.updateTrain((Train) event.getSource());
                        break;
                    case TIME_INTERVAL_LIST:
                        panel.updateTrain((Train) event.getSource());
                        break;
                }
            }
            
            @Override
            public void processTrainsCycleEvent(TrainsCycleEvent event) {
                if (event.getSource().getType() == TrainsCycleType.ENGINE_CYCLE) {
                    TrainsCycle cycle = event.getSource();
                    for (TrainsCycleItem item : cycle) {
                        panel.updateTrain(item.getTrain());
                    }
                }
            }

            @Override
            public void processTrainDiagramEvent(TrainDiagramEvent event) {
                switch (event.getType()) {
                    case TRAIN_ADDED:
                        panel.updateTrain((Train)event.getObject());
                        break;
                    case TRAIN_REMOVED:
                        panel.removeTrain((Train)event.getObject());
                        break;
                }
            }

            @Override
            public void processApplicationEvent(ApplicationModelEvent event) {
                switch (event.getType()) {
                    case SELECTED_TRAIN_CHANGED:
                        panel.updateSelectedTrain((Train)event.getObject());
                        break;
                    case SET_DIAGRAM_CHANGED:
                        panel.setTrainComparator(model.getDiagram() != null ? new TrainComparator(TrainComparator.Type.ASC, model.getDiagram().getTrainsData().getTrainSortPattern()) : null);
                        panel.updateAllTrains(model.getDiagram() != null ? model.getDiagram().getTrains() : null);
                        break;
                }
            }


        });

        return dialog;
    }

    private static FloatingDialog createEventsViewerDialog(final Frame frame, final Mediator mediator, final ApplicationModel model) {
        final EventsViewerPanel panel = new EventsViewerPanel();
        panel.addConverter(new GTEventTypeConverter());
        panel.addConverter(new ApplicationEventTypeConverter());
        final FloatingDialog dialog = new FloatingDialog(frame, panel, "dialog.eventsviewer.title", "events.viewer") {

            @Override
            public void saveToPreferences(AppPreferences prefs) {
                super.saveToPreferences(prefs);
                prefs.setInt(createStorageKey("divider"), panel.getDividerLocation());
                prefs.setInt(createStorageKey("limit"), panel.getLimit());
                prefs.setBoolean(createStorageKey("show.time"), panel.isShowTime());
                prefs.setBoolean(createStorageKey("write.to.log"), panel.isWriteToLog());
            }

            @Override
            public void loadFromPreferences(AppPreferences prefs) {
                super.loadFromPreferences(prefs);
                int divider = prefs.getInt(createStorageKey("divider"), panel.getDividerLocation());
                panel.setDividerLocation(divider);
                int limit = prefs.getInt(createStorageKey("limit"), panel.getLimit());
                panel.setLimit(limit);
                panel.setShowTime(prefs.getBoolean(createStorageKey("show.time"), false));
                panel.setWriteToLog(prefs.getBoolean(createStorageKey("write.to.log"), false));
            }
        };

        mediator.addColleague(new ApplicationGTEventColleague(true) {

            @Override
            public void receiveMessage(Object message) {
                // do not process messages when the dialog is not visible ...
                if (!dialog.isVisible())
                    return;
                super.receiveMessage(message);
            }

            @Override
            public void processGTEventAll(GTEvent<?> event) {
                panel.addEvent(event);
            }

            @Override
            public void processApplicationEvent(ApplicationModelEvent event) {
                panel.addEvent(event);
            }
        });
        return dialog;
    }

    private static FloatingDialog createChangesTrackedDialog(Frame frame, Mediator mediator, ApplicationModel model) {
        final ChangesTrackerPanel panel = new ChangesTrackerPanel();
        model.addListener(new ApplicationModelListener() {
            @Override
            public void modelChanged(ApplicationModelEvent event) {
                if (event.getType() == ApplicationModelEventType.SET_DIAGRAM_CHANGED)
                    panel.setTrainDiagram(event.getModel().getDiagram());
            }
        });
        FloatingDialog dialog = new FloatingDialog(frame, panel, "dialog.changestracker.title", "changes.tracker") {

            @Override
            public void saveToPreferences(AppPreferences prefs) {
                super.saveToPreferences(prefs);
                prefs.setInt(createStorageKey("divider"), panel.getDividerLocation());
                prefs.setInt(createStorageKey("divider2"), panel.getDivider2Location());
            }

            @Override
            public void loadFromPreferences(AppPreferences prefs) {
                super.loadFromPreferences(prefs);
                int divider = prefs.getInt(createStorageKey("divider"), panel.getDividerLocation());
                panel.setDividerLocation(divider);
                divider = prefs.getInt(createStorageKey("divider2"), panel.getDivider2Location());
                panel.setDivider2Location(divider);
            }
        };
        return dialog;
    }

    private static FloatingDialog createGTViewDialog(Frame frame, Mediator mediator, ApplicationModel model) {
        final GraphicalTimetableView gtView = new GraphicalTimetableView();
        JScrollPane scrollPane = new GTViewScrollPane(gtView);
        NormalHTS hts = new NormalHTS(model, Color.GREEN, gtView);
        gtView.setSettings(gtView.getSettings().set(GTViewSettings.Key.HIGHLIGHTED_TRAINS, hts));
        gtView.setTrainSelector(hts);

        FloatingDialog dialog = new FloatingDialog(frame, scrollPane, "dialog.gtview.title", "gtview") {

            @Override
            public void saveToPreferences(AppPreferences prefs) {
                super.saveToPreferences(prefs);
                prefs.setString(createStorageKey("gtv"), gtView.getSettings().getStorageString());
            }

            @Override
            public void loadFromPreferences(AppPreferences prefs) {
                super.loadFromPreferences(prefs);
                try {
                    GTViewSettings gtvs = GTViewSettings.parseStorageString(prefs.getString(createStorageKey("gtv"), null));
                    gtView.setSettings(gtView.getSettings().merge(gtvs));
                } catch (Exception e) {
                    LOG.warn("Wrong GTView settings - using default values.");
                }
            }
        };
        dialog.setSize(new Dimension(400, 300)); // initial size
        return dialog;
    }

        public static FloatingDialogsList createDialogs(Frame frame, Mediator mediator, ApplicationModel model) {
        FloatingDialogsList list = new FloatingDialogsList();
        list.add(createTrainsWithConflictsDialog(frame, mediator, model));
        list.add(createTrainsWithZeroWeightsDialog(frame, mediator, model));
        list.add(createEventsViewerDialog(frame, mediator, model));
        list.add(createChangesTrackedDialog(frame, mediator, model));
        list.add(createGTViewDialog(frame, mediator, model));
        return list;
    }
}
