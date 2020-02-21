package net.parostroj.timetable.gui.pm;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.beanfabrics.Path;
import org.beanfabrics.event.BnPropertyChangeEvent;
import org.beanfabrics.event.ElementChangedEvent;
import org.beanfabrics.event.ElementsAddedEvent;
import org.beanfabrics.event.ElementsRemovedEvent;
import org.beanfabrics.event.ListAdapter;
import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.IListPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.PresentationModel;
import org.beanfabrics.model.SortKey;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.Operation;
import org.beanfabrics.support.Validation;
import org.beanfabrics.validation.ValidationState;

import com.google.common.collect.FluentIterable;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TrackConnector;

/**
 * @author jub
 */
public class NodePM extends AbstractPM implements IPM<Node> {

    private static final List<SortKey> CONNECTOR_SORT_KEY = Arrays.asList(
            new SortKey(true, new Path("orientation")),
            new SortKey(true, new Path("number")));

    ItemListPM<NodeTrackPM> tracks;
    ItemListPM<TrackConnectorPM> connectors;
    TextPM name;
    TextPM abbr;

    OperationPM ok = new OperationPM();

    private Node reference;
    private Collection<LineTrack> lineTracks = Collections.emptyList();
    private Supplier<TrackConnectorPM> trackConnectorSupplier;
    private boolean updateConnsBlock;

    public NodePM() {
        this.tracks = new ItemListPM<>(() -> {
            NodeTrackPM trackPm = new NodeTrackPM();
            trackPm.number.setText("1");
            trackPm.platform.setBoolean(true);
            return trackPm;
        });
        tracks.delete.getValidator().add(() -> {
            if (tracks.getSelection().size() == tracks.size()) {
                return ValidationState.create("At least one track");
            } else {
                Collection<NodeTrackPM> trackPMs = tracks.getSelection().toCollection();
                for (NodeTrackPM trackPM : trackPMs) {
                    if (trackPM.getReference() != null
                            && !trackPM.getReference().getTimeIntervalList().isEmpty()) {
                        return ValidationState.create("Not empty track");
                    }
                }
            }
            return null;
        });
        trackConnectorSupplier = () -> {
            TrackConnectorPM tc = new TrackConnectorPM();
            tc.number.setText("1");
            tracks.forEach(track -> {
                tc.getSwitches().add(new TrackConnectorSwitchPM(track, true, false));
            });
            tc.initLineTrack(reference, lineTracks, Optional.empty());
            return tc;
        };
        this.connectors = new ItemListPM<>(trackConnectorSupplier);
        this.connectors.addPropertyChangeListener(evt -> {
            Optional<EventObject> cause = unwrapCause(evt);
            // ElementChangedEvent
            cause.filter(ElementChangedEvent.class::isInstance)
                    .flatMap(NodePM::unwrapCause)
                    .map(BnPropertyChangeEvent.class::cast)
                    .filter(NodePM::checkLineTrackProperty)
                    .ifPresent(e -> {
                        updateConnectorsLineTrackChanged((TrackConnectorPM) e.getSource());
                    });
            // ElementsRemovedEvent
            cause.filter(ElementsRemovedEvent.class::isInstance)
                    .map(ElementsRemovedEvent.class::cast)
                    .ifPresent(e -> {
                        updateConnectorsCheckAllLineTracks();
                    });
        });
        this.connectors.delete.getValidator().add(() -> {
            Collection<TrackConnectorPM> cs = connectors.getSelection().toCollection();
            for (TrackConnectorPM c : cs) {
                if (c.getLineTrack().getValue() != null) {
                    return ValidationState.create("Cannot delete connected connector");
                }
            }
            return null;
        });
        this.tracks.addListListener(new TracksListener(this.connectors));
        this.name = new TextPM();
        this.name.setMandatory(true);
        this.name.getValidator().add(new EmptySpacesValidationRule(this.name));
        this.abbr = new TextPM();
        this.abbr.setMandatory(true);
        this.abbr.getValidator().add(new EmptySpacesValidationRule(this.abbr));
        PMManager.setup(this);
    }

    public NodePM(Node node) {
        this();
        init(node);
    }

    @Override
    public void init(Node node) {
        this.updateConnsBlock = true;
        this.reference = node;
        this.tracks.clear();
        this.connectors.clear();

        Set<Line> lines = node.getDiagram().getNet().getLinesOf(node);
        lineTracks = FluentIterable.from(lines)
                .transformAndConcat(line -> line.getTracks()).toList();

        node.getTracks().forEach(track -> {
            NodeTrackPM nodeTrackPm = new NodeTrackPM();
            nodeTrackPm.init(track);
            tracks.add(nodeTrackPm);
        });
        node.getConnectors().forEach(connector -> {
            TrackConnectorPM connectorPm = new TrackConnectorPM(connector, this.tracks, lineTracks);
            connectors.add(connectorPm);
        });
        this.connectors.sortBy(CONNECTOR_SORT_KEY);
        this.name.setText(node.getName());
        this.abbr.setText(node.getAbbr());
        this.updateConnsBlock = false;
        this.updateConnectorsCheckAllLineTracks();
    }

    public ListPM<NodeTrackPM> getTracks() {
        return tracks;
    }

    public ListPM<TrackConnectorPM> getConnectors() {
        return connectors;
    }

    public Node getReference() {
        return reference;
    }

    public void writeResult() {
        if (reference != null) {
            // remove
            Set<NodeTrack> keptTracks = FluentIterable.from(tracks)
                    .transform(NodeTrackPM::getReference).filter(Objects::nonNull).toSet();
            Set<NodeTrack> toBeDeletedTracks = new HashSet<>(reference.getTracks());
            toBeDeletedTracks.removeAll(keptTracks);
            reference.getTracks().removeAll(toBeDeletedTracks);
            // update
            int position = 0;
            for (NodeTrackPM track : tracks) {
                track.writeResult(reference, position);
                position++;
            }

            // remove
            Set<TrackConnector> keptConns = FluentIterable.from(connectors)
                    .transform(TrackConnectorPM::getReference).filter(Objects::nonNull).toSet();
            Set<TrackConnector> toBeDeletedConns = new HashSet<>(reference.getConnectors());
            toBeDeletedConns.removeAll(keptConns);
            reference.getConnectors().removeAll(toBeDeletedConns);
            // update
            for (TrackConnectorPM connector : connectors) {
                connector.writeResult(reference);
            }
            reference.setName(name.getText());
            reference.setAbbr(abbr.getText());
        }
    }

    @Validation(path = { "ok" })
    public boolean canWrite() {
        return isValid();
    }

    @Operation(path = "ok")
    public boolean ok() {
        writeResult();
        return true;
    }

    private static Optional<EventObject> unwrapCause(EventObject event) {
        if (event instanceof BnPropertyChangeEvent) {
            return Optional.ofNullable(((BnPropertyChangeEvent) event).getCause());
        } else if (event instanceof ElementChangedEvent) {
            return Optional.ofNullable(((ElementChangedEvent) event).getCause());
        }
        return Optional.empty();
    }

    private static boolean checkLineTrackProperty(PropertyChangeEvent event) {
        return event instanceof PropertyChangeEvent
                && "lineTrack".equals(((PropertyChangeEvent) event).getPropertyName());
    }

    private void updateConnectorsLineTrackChanged(TrackConnectorPM src) {
        LineTrack lt = src.getLineTrack().getValue();
        if (lt != null) {
            connectors.forEach(c -> {
                if (c != src && c.getLineTrack().getValue() == lt) {
                    c.getLineTrack().setValue(null);
                }
            });
        }

        updateConnectorsCheckAllLineTracks();
    }

    private void updateConnectorsCheckAllLineTracks() {
        if (this.updateConnsBlock) {
            return;
        }
        this.updateConnsBlock = true;
        try {
            // select empty connectors
            Set<LineTrack> selectedTracks = connectors.toCollection().stream()
                    .filter(c -> c.getLineTrack().getValue() != null)
                    .map(c -> c.getLineTrack().getValue()).collect(toSet());
            Set<LineTrack> notSelectedTracks = lineTracks.stream()
                    .filter(lt -> !selectedTracks.contains(lt)).collect(toSet());
            if (!notSelectedTracks.isEmpty()) {
                List<TrackConnectorPM> emptyConnectors = connectors.toCollection().stream()
                        .filter(c -> c.getLineTrack().getValue() == null).collect(toList());
                // add connectors if there is not enough
                Iterator<TrackConnectorPM> ecIterator = emptyConnectors.iterator();
                notSelectedTracks.stream()
                    .forEach(lt -> {
                        TrackConnectorPM tcPm = ecIterator.hasNext() ? ecIterator.next() : null;
                        if (tcPm == null) {
                            tcPm = trackConnectorSupplier.get();
                            connectors.add(tcPm);
                        }
                        tcPm.getLineTrack().setValue(lt);
                    });
            }
        } finally {
            this.updateConnsBlock = false;
        }
    }

    private static class TracksListener extends ListAdapter {

        private IListPM<TrackConnectorPM> connectors;

        public TracksListener(IListPM<TrackConnectorPM> connectors) {
            this.connectors = connectors;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void elementsAdded(ElementsAddedEvent evt) {
            int from = evt.getBeginIndex();
            IListPM<NodeTrackPM> source = (IListPM<NodeTrackPM>) evt.getSource();
            for (int i = from; i < from + evt.getLength(); i++) {
                NodeTrackPM trackPm = source.getAt(i);
                final int pos = i;
                connectors.forEach(connector -> {
                    TrackConnectorSwitchPM switchPM = new TrackConnectorSwitchPM(trackPm, true, false);
                    connector.setConnectorEditable(trackPm, switchPM);
                    connector.getSwitches().add(pos, switchPM);
                });
            }
        }

        @Override
        public void elementsRemoved(ElementsRemovedEvent evt) {
            Collection<? extends PresentationModel> removed = evt.getRemoved();
            connectors.forEach(connector -> {
                ListPM<TrackConnectorSwitchPM> tracks = connector.getSwitches();
                Iterator<TrackConnectorSwitchPM> iterator = tracks.iterator();
                while (iterator.hasNext()) {
                    TrackConnectorSwitchPM trackSi = iterator.next();
                    if (removed.contains(trackSi.track)) {
                        iterator.remove();
                    }
                }
            });
        }
    }
}
