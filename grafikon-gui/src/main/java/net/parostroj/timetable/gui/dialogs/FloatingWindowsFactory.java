package net.parostroj.timetable.gui.dialogs;

import java.awt.Frame;

import javax.swing.*;

import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.components.*;
import net.parostroj.timetable.gui.events.DiagramChangeMessage;
import net.parostroj.timetable.gui.events.TrainSelectionMessage;
import net.parostroj.timetable.gui.ini.IniConfig;
import net.parostroj.timetable.gui.ini.IniConfigSection;
import net.parostroj.timetable.gui.views.NetEditView;
import net.parostroj.timetable.mediator.GTEventsReceiverColleague;
import net.parostroj.timetable.mediator.Mediator;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creation of floating dialogs.
 *
 * @author jub
 */
public class FloatingWindowsFactory {

    private static final Logger log = LoggerFactory.getLogger(FloatingWindowsFactory.class);

    private FloatingWindowsFactory() {}

    private static FloatingWindow createTrainsWithConflictsDialog(final Frame frame, final Mediator mediator, final ApplicationModel model) {
        final TrainsWithConflictsPanel panel = new TrainsWithConflictsPanel(model::setSelectedTrain);
        final FloatingWindow dialog = new FloatingDialog(frame, panel, "dialog.trainconflicts.title", "train.conflicts");
        mediator.addColleague(new GTEventsReceiverColleague() {
            @Override
            public void processTrainEvent(Event event) {
                switch (event.getType()) {
                    case SPECIAL:
                        if (event.getData() instanceof SpecialTrainTimeIntervalList) {
                            panel.updateTrain((Train) event.getSource());
                        }
                        break;
                    case ATTRIBUTE:
                        if (event.getAttributeChange().checkName(Train.ATTR_NAME)) {
                            panel.refreshTrain((Train) event.getSource());
                        }
                        if (event.getAttributeChange().checkName(Train.ATTR_TECHNOLOGICAL_AFTER, Train.ATTR_TECHNOLOGICAL_BEFORE)) {
                            panel.updateTrain((Train) event.getSource());
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void processTrainDiagramEvent(Event event) {
                switch (event.getType()) {
                    case ADDED:
                        if (event.getObject() instanceof Train) {
                            panel.updateTrain((Train) event.getObject());
                        }
                        break;
                    case REMOVED:
                        if (event.getObject() instanceof Train) {
                            panel.removeTrain((Train) event.getObject());
                        }
                        break;
                    default:
                        break;
                }
            }
        }, Event.class);

        mediator.addColleague(message -> {
            TrainDiagram diagram = ((DiagramChangeMessage) message).diagram();
            panel.updateAllTrains(diagram != null ? diagram.getTrains() : null);
        }, DiagramChangeMessage.class);
        mediator.addColleague(message ->
                panel.updateSelectedTrain(((TrainSelectionMessage) message).train()), TrainSelectionMessage.class);

        return dialog;
    }

    private static FloatingWindow createTrainsWithZeroWeightsDialog(final Frame frame, final Mediator mediator, final ApplicationModel model) {
        final TrainsWithZeroWeightsPanel panel = new TrainsWithZeroWeightsPanel(model::setSelectedTrain);
        final FloatingDialog dialog = new FloatingDialog(frame, panel, "dialog.trainzeroweights.title", "train.zero.weights") {

            private static final long serialVersionUID = 1L;

			@Override
            public void setVisible(boolean b) {
                if (b) {
                    panel.updateAllTrains(model.getDiagram() != null ? model.getDiagram().getTrains() : null);
                } else {
                    panel.removeAllTrains();
                }
                super.setVisible(b);
            }
        };
        mediator.addColleague(new GTEventsReceiverColleague() {
            @Override
            public void receiveMessage(Object message) {
                // do not check if the dialog is not visible
                if (!dialog.isVisible()) {
                    return;
                }
                super.receiveMessage(message);
            }

            @Override
            public void processTrainEvent(Event event) {
                switch (event.getType()) {
                    case ATTRIBUTE:
                        if (event.getAttributeChange().checkName(Train.ATTR_WEIGHT)) {
                            panel.updateTrain((Train) event.getSource());
                        } else if (event.getAttributeChange().checkName(Train.ATTR_NAME)) {
                            panel.refreshTrain((Train) event.getSource());
                        }
                        break;
                    case SPECIAL:
                        if (event.getData() instanceof SpecialTrainTimeIntervalList) {
                            panel.updateTrain((Train) event.getSource());
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void processLineEvent(Event event) {
                if (event.getType() == Event.Type.ATTRIBUTE
                        && event.getAttributeChange().checkName(Line.ATTR_CLASS, Line.ATTR_CLASS_BACK)) {
                    ((Line) event.getSource()).getTracks().stream()
                            .flatMap(track -> track.getTimeIntervalList().stream())
                            .map(TimeInterval::getTrain)
                            .distinct()
                            .forEach(panel::updateTrain);
                }
            }

            @Override
            public void processTrainsCycleEvent(Event event) {
                if (TrainsCycleType.isEngineType(((TrainsCycle) event.getSource()).getType())) {
                    TrainsCycle cycle = (TrainsCycle) event.getSource();
                    for (TrainsCycleItem item : cycle) {
                        panel.updateTrain(item.getTrain());
                    }
                }
            }

            @Override
            public void processTrainDiagramEvent(Event event) {
                switch (event.getType()) {
                    case ADDED:
                        if (event.getObject() instanceof Train) {
                            panel.updateTrain((Train) event.getObject());
                        }
                        break;
                    case REMOVED:
                        if (event.getObject() instanceof Train) {
                            panel.removeTrain((Train) event.getObject());
                        }
                        break;
                    default:
                        break;
                }
            }
        }, Event.class);
        mediator.addColleague(message -> {
            TrainDiagram diagram = ((DiagramChangeMessage) message).diagram();
            panel.updateAllTrains(diagram != null ? diagram.getTrains() : null);
        }, DiagramChangeMessage.class);
        mediator.addColleague(message ->
                panel.updateSelectedTrain(((TrainSelectionMessage) message).train()), TrainSelectionMessage.class);

        return dialog;
    }

    private static FloatingWindow createEventsViewerDialog(final Frame frame, final Mediator mediator) {
        final EventsViewerPanel panel = new EventsViewerPanel();
        panel.addConverter(new GTEventTypeConverter());
        final FloatingDialog dialog = new FloatingDialog(frame, panel, "dialog.eventsviewer.title", "events.viewer") {

            private static final long serialVersionUID = 1L;

			@Override
            public IniConfigSection saveToPreferences(IniConfig prefs) {
                IniConfigSection section = super.saveToPreferences(prefs);
                section.put("divider", panel.getDividerLocation());
                section.put("limit", panel.getLimit());
                section.put("show.time", panel.isShowTime());
                section.put("write.to.log", panel.isWriteToLog());
                return section;
            }

            @Override
            public IniConfigSection loadFromPreferences(IniConfig prefs) {
                IniConfigSection section = super.loadFromPreferences(prefs);
                int divider = section.get("divider", Integer.class, panel.getDividerLocation());
                panel.setDividerLocation(divider);
                int limit = section.get("limit", Integer.class, panel.getLimit());
                panel.setLimit(limit);
                panel.setShowTime(section.get("show.time", Boolean.class, false));
                panel.setWriteToLog(section.get("write.to.log", Boolean.class, false));
                return section;
            }
        };

        mediator.addColleague(message -> {
            // do not process messages when the dialog is not visible ...
            if (!dialog.isVisible())
                return;
            panel.addEvent(message);
        });
        return dialog;
    }

    private static FloatingWindow createChangesTrackedDialog(Frame frame, ApplicationModel model) {
        final ChangesTrackerPanel panel = new ChangesTrackerPanel();
        model.getMediator().addColleague(message ->
                panel.setTrainDiagram(((DiagramChangeMessage) message).diagram()), DiagramChangeMessage.class);
        return new FloatingDialog(frame, panel, "dialog.changestracker.title", "changes.tracker") {

            private static final long serialVersionUID = 1L;

			@Override
            public IniConfigSection saveToPreferences(IniConfig prefs) {
                IniConfigSection section = super.saveToPreferences(prefs);
                section.put("divider", panel.getDividerLocation());
                section.put("divider2", panel.getDivider2Location());
                return section;
            }

            @Override
            public IniConfigSection loadFromPreferences(IniConfig prefs) {
                IniConfigSection section = super.loadFromPreferences(prefs);
                int divider = section.get("divider", Integer.class, panel.getDividerLocation());
                panel.setDividerLocation(divider);
                divider = section.get("divider2", Integer.class, panel.getDivider2Location());
                panel.setDivider2Location(divider);
                return section;
            }
        };
    }

    private static FloatingWindow createGTViewDialog(Frame frame, ApplicationModel model) {
        final GraphicalTimetableView gtView = GraphicalTimetableView.newBuilder()
                .forTrains(model.getMediator())
                .build();
        final JComponent scrollPane = gtView.newScrollPaneWithButtons();
        return new FloatingFrame(frame, scrollPane, "dialog.gtview.title", "gt.view") {

            private static final long serialVersionUID = 1L;

			@Override
            public IniConfigSection saveToPreferences(IniConfig prefs) {
                IniConfigSection section = super.saveToPreferences(prefs);
                section.put("gtv", gtView.getSettings().getStorageString());
                return section;
            }

            @Override
            public IniConfigSection loadFromPreferences(IniConfig prefs) {
                IniConfigSection section = super.loadFromPreferences(prefs);
                try {
                    GTViewSettings gtvs = GTViewSettings.parseStorageString(section.get("gtv"));
                    gtView.setSettings(gtView.getSettings().merge(gtvs));
                } catch (Exception e) {
                    log.warn("Wrong GTView settings - using default values.");
                }
                return section;
            }
        };
    }

    private static FloatingWindow createNetView(Frame frame, ApplicationModel model) {
        NetEditView netEditView = new NetEditView(model);
        return new FloatingFrame(frame, netEditView, "dialog.netview.title", "net.view");
    }

    private static FloatingWindow createFreightDestinationView(Frame frame, Mediator mediator) {
        final FreightPanel panel = new FreightPanel();
        FloatingDialog dialog = new FloatingDialog(frame, panel, "dialog.freightdestination.title", "freight.destination");
        mediator.addColleague(message -> panel.setDiagram(((DiagramChangeMessage) message).diagram()),
                DiagramChangeMessage.class);
        return dialog;
    }

    private static FloatingWindow createCirculationViewDialog(Frame frame, Mediator mediator) {
        final CirculationViewPanel panel = new CirculationViewPanel();
        mediator.addColleague(message -> panel.setDiagram(((DiagramChangeMessage) message).diagram()),
                DiagramChangeMessage.class);
        mediator.addColleague(new GTEventsReceiverColleague() {
            @Override
            public void processTrainDiagramEvent(Event event) {
                switch (event.getType()) {
                    case ADDED:
                        if (event.getObject() instanceof TrainsCycle) {
                            panel.circulationAdded((TrainsCycle) event.getObject());
                        } else if (event.getObject() instanceof TrainsCycleType) {
                            panel.typeAdded((TrainsCycleType) event.getObject());
                        }
                        break;
                    case REMOVED:
                        if (event.getObject() instanceof TrainsCycle) {
                            panel.circulationRemoved((TrainsCycle) event.getObject());
                        } else if (event.getObject() instanceof TrainsCycleType) {
                            panel.typeRemoved((TrainsCycleType) event.getObject());
                        }
                        break;
                    case ATTRIBUTE:
                        if (event.getAttributeChange().getName().equals(TrainDiagram.ATTR_FROM_TIME)
                                || event.getAttributeChange().getName().equals(TrainDiagram.ATTR_TO_TIME)) {
                            panel.timeLimitsUpdated();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void processTrainsCycleEvent(Event event) {
                panel.circulationUpdated((TrainsCycle) event.getSource());
            }
        }, Event.class);
        return new FloatingDialog(frame, panel, "dialog.circulationview.title", "circulations.view") {
            private static final long serialVersionUID = 1L;

			@Override
            public IniConfigSection saveToPreferences(IniConfig prefs) {
                IniConfigSection section = super.saveToPreferences(prefs);
                section.put("size", panel.getSizeSlider());
                section.put("zoom", panel.getZoomSlider());
                section.put("type", panel.getDrawType());
                return section;
            }

            @Override
            public IniConfigSection loadFromPreferences(IniConfig prefs) {
                IniConfigSection section = super.loadFromPreferences(prefs);
                panel.setSizeSlider(section.get("size", Integer.class, panel.getSizeSlider()));
                panel.setZoomSlider(section.get("zoom", Integer.class, panel.getZoomSlider()));
                panel.setDrawType(section.get("type", String.class, panel.getDrawType()));
                return section;
            }
        };
    }

    private static FloatingWindow createTimeIntervalsTrainsChanged(final Frame frame, final Mediator mediator, final ApplicationModel model) {
        final ChangedTrainsPanel panel = new ChangedTrainsPanel(model::setSelectedTrain);
        final FloatingWindow dialog = new FloatingDialog(frame, panel, "dialog.trainchanged.title", "changed.trains");
        mediator.addColleague(new GTEventsReceiverColleague() {
            @Override
            public void processTrainEvent(Event event) {
                if (!dialog.isVisible())
                    return;
                if (event.getType() == Event.Type.SPECIAL && event.getData() instanceof SpecialTrainTimeIntervalList) {
                    panel.addTrainToList((Train) event.getSource());
                }
            }
        }, Event.class);
        mediator.addColleague(message -> panel.clearTrainList(), DiagramChangeMessage.class);
        return dialog;
    }

    public static FloatingWindowsList createDialogs(Frame frame, Mediator mediator, ApplicationModel model) {
        FloatingWindowsList list = new FloatingWindowsList();
        list.add(createTrainsWithConflictsDialog(frame, mediator, model));
        list.add(createTrainsWithZeroWeightsDialog(frame, mediator, model));
        list.add(createEventsViewerDialog(frame, mediator));
        list.add(createChangesTrackedDialog(frame, model));
        list.add(createGTViewDialog(frame, model));
        list.add(createCirculationViewDialog(frame, mediator));
        list.add(createTimeIntervalsTrainsChanged(frame, mediator, model));
        list.add(createNetView(frame, model));
        list.add(createFreightDestinationView(frame, mediator));
        return list;
    }
}
