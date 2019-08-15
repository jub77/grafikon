package net.parostroj.timetable.gui.pm;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.beanfabrics.Path;
import org.beanfabrics.event.ElementsAddedEvent;
import org.beanfabrics.event.ElementsRemovedEvent;
import org.beanfabrics.event.ListAdapter;
import org.beanfabrics.event.ListListener;
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
            new SortKey(true, new Path("position")));

    ItemListPM<NodeTrackPM> tracks;
    ItemListPM<TrackConnectorPM> connectors;
    TextPM name;
    TextPM abbr;

    OperationPM ok = new OperationPM();

    private ListListener tracksListener;

    private Node reference;
    private Iterable<LineTrack> lineTracks = Collections.emptyList();

    public NodePM() {
        this.tracks = new ItemListPM<>(() -> {
            NodeTrackPM trackPm = new NodeTrackPM();
            trackPm.number.setText("1");
            return trackPm;
        });
        this.connectors = new ItemListPM<>(() -> {
            TrackConnectorPM tc = new TrackConnectorPM();
            tc.number.setText("1");
            tracks.forEach(track -> {
                tc.getSwitches().add(new TrackConnectorSwitchPM(track, false, false));
            });
            tc.initLineTrack(reference, lineTracks);
            return tc;
        });
        this.connectors.setSorted(CONNECTOR_SORT_KEY);
        this.tracksListener = new TracksListener(this.connectors);
        this.tracks.addListListener(tracksListener);
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
        this.reference = node;
        this.tracks.clear();
        this.connectors.clear();

        Set<Line> lines = node.getDiagram().getNet().getLinesOf(node);
        lineTracks = FluentIterable.from(lines)
                .transformAndConcat(line -> line.getTracks());

        node.getTracks().forEach(track -> {
            NodeTrackPM nodeTrackPm = new NodeTrackPM();
            nodeTrackPm.init(track);
            tracks.add(nodeTrackPm);
        });
        node.getConnectors().forEach(connector -> {
            TrackConnectorPM connectorPm = new TrackConnectorPM(connector, this.tracks, lineTracks);
            connectors.add(connectorPm);
        });
        this.name.setText(node.getName());
        this.abbr.setText(node.getAbbr());
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
                    connector.getSwitches().add(pos, new TrackConnectorSwitchPM(trackPm, false, false));
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
