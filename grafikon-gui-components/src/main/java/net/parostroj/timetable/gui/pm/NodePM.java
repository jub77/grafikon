package net.parostroj.timetable.gui.pm;

import net.parostroj.timetable.model.Node;
import org.beanfabrics.Path;
import org.beanfabrics.event.ElementsAddedEvent;
import org.beanfabrics.event.ElementsRemovedEvent;
import org.beanfabrics.event.ListAdapter;
import org.beanfabrics.event.ListListener;
import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.IListPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.PresentationModel;
import org.beanfabrics.model.SortKey;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author jub
 */
public class NodePM extends AbstractPM implements IPM<Node> {

    ItemListPM<NodeTrackPM> tracks;
    ItemListPM<TrackConnectorPM> connectors;

    private ListListener tracksListener;

    public NodePM() {
        this.tracks = new ItemListPM<>(() -> {
            NodeTrackPM trackPm = new NodeTrackPM();
            trackPm.number.setText("1");
            return trackPm;
        });
        this.connectors = new ItemListPM<>(() -> {
            TrackConnectorPM tc = new TrackConnectorPM();
            tc.number.setText("1");
            return tc;
        });
        this.connectors.setSorted(Arrays.asList(new SortKey(true, new Path("orientation")), new SortKey(true, new Path("position"))));
        this.tracksListener = new TracksListener(this.connectors);
        this.tracks.addListListener(tracksListener);
        PMManager.setup(this);
    }

    public NodePM(Node node) {
        this();
        init(node);
    }

    @Override
    public void init(Node node) {
        this.tracks.clear();
        this.connectors.clear();
        node.getTracks().forEach(track -> {
            NodeTrackPM nodeTrackPm = new NodeTrackPM();
            nodeTrackPm.init(track);
            tracks.add(nodeTrackPm);
        });
        node.getConnectors().forEach(connector -> {
            TrackConnectorPM connectorPm = new TrackConnectorPM(connector, this);
            connectors.add(connectorPm);
        });
    }

    public ListPM<NodeTrackPM> getTracks() {
        return tracks;
    }

    public ListPM<TrackConnectorPM> getConnectors() {
        return connectors;
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
                    connector.nodeTracks.add(pos, new SelectedItemPM<NodeTrackPM>(false, trackPm));
                });
            }
        }

        @Override
        public void elementsRemoved(ElementsRemovedEvent evt) {
            Collection<? extends PresentationModel> removed = evt.getRemoved();
            connectors.forEach(connector -> {
                ListPM<SelectedItemPM<NodeTrackPM>> tracks = connector.nodeTracks;
                Iterator<SelectedItemPM<NodeTrackPM>> iterator = tracks.iterator();
                while (iterator.hasNext()) {
                    SelectedItemPM<NodeTrackPM> trackSi = iterator.next();
                    if (removed.contains(trackSi.item)) {
                        iterator.remove();
                    }
                }
            });
        }
    }
}
