package net.parostroj.timetable.gui.dialogs;

import java.awt.Frame;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.components.ChangesTrackerPanel;
import net.parostroj.timetable.gui.components.EventsViewerPanel;
import net.parostroj.timetable.gui.components.GTEventTypeConverter;
import net.parostroj.timetable.gui.components.TrainsWithConflictsPanel;
import net.parostroj.timetable.gui.helpers.TrainWrapper;
import net.parostroj.timetable.mediator.Mediator;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.net.ChangesTracker;

/**
 * Factory for creation of floating dialogs.
 *
 * @author jub
 */
public class FloatingDialogsFactory {

    public static FloatingDialog createTrainsWithConflictsDialog(final Frame frame, final Mediator mediator, final ApplicationModel model) {
        final TrainsWithConflictsPanel panel = new TrainsWithConflictsPanel();
        panel.addTrainSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    JList list = (JList)e.getSource();
                    TrainWrapper wrapper = (TrainWrapper)list.getSelectedValue();
                    if (wrapper != null) {
                        if (wrapper.getElement() != model.getSelectedTrain()) {
                            model.setSelectedTrain(wrapper.getElement());
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
                        panel.updateTrain(event.getTrain());
                        break;
                    case TRAIN_REMOVED:
                        panel.removeTrain(event.getTrain());
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

    public static FloatingDialog createEventsViewerDialog(final Frame frame, final Mediator mediator, final ApplicationModel model) {
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

    public static FloatingDialog createChangesTrackedDialog(Frame frame, Mediator mediator, ApplicationModel model) {
        ChangesTrackerPanel panel = new ChangesTrackerPanel();
        ChangesTracker tracker = new ChangesTracker();
        mediator.addColleague(tracker, GTEvent.class);
        FloatingDialog dialog = new FloatingDialog(frame, panel, "dialog.changestracker.title", "changes.tracker");
        return dialog;
    }

    public static FloatingDialogsList createDialogs(Frame frame, Mediator mediator, ApplicationModel model) {
        FloatingDialogsList list = new FloatingDialogsList();
        list.add(createTrainsWithConflictsDialog(frame, mediator, model));
        list.add(createEventsViewerDialog(frame, mediator, model));
        list.add(createChangesTrackedDialog(frame, mediator, model));
        return list;
    }
}
