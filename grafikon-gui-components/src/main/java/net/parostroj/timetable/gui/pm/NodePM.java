package net.parostroj.timetable.gui.pm;

import net.parostroj.timetable.model.Node;
import org.beanfabrics.Path;
import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.SortKey;

import java.util.Arrays;

/**
 * @author jub
 */
public class NodePM extends AbstractPM implements IPM<Node> {

    ItemListPM<NodeTrackPM> tracks;
    ItemListPM<TrackConnectorPM> connectors;

    public NodePM() {
        this.tracks = new ItemListPM<>(() -> {
            NodeTrackPM trackPm = new NodeTrackPM();
            trackPm.number.setText("-");
            return trackPm;
        });
        this.connectors = new ItemListPM<>(() -> {
            TrackConnectorPM tc = new TrackConnectorPM();
            tc.number.setText("1");
            return tc;
        });
        this.connectors.setSorted(Arrays.asList(new SortKey(true, new Path("orientation")), new SortKey(true, new Path("position"))));
        PMManager.setup(this);
    }

    public NodePM(Node node) {
        this();
        init(node);
    }

    @Override
    public void init(Node node) {
        this.tracks.clear();
        node.getTracks().forEach(track -> {
            NodeTrackPM nodeTrackPm = new NodeTrackPM();
            nodeTrackPm.init(track);
            tracks.add(nodeTrackPm);
        });
        this.connectors.clear();
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
}
