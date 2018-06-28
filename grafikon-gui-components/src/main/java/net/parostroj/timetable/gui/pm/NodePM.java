package net.parostroj.timetable.gui.pm;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.PMManager;

import net.parostroj.timetable.model.Node;

/**
 * @author jub
 */
public class NodePM extends AbstractPM implements IPM<Node> {

    ItemListPM<NodeTrackPM> tracks;
    ItemListPM<NodePortPM> ports;

    public NodePM() {
        this.tracks = new ItemListPM<>(() -> {
            NodeTrackPM trackPm = new NodeTrackPM();
            trackPm.number.setText("-");
            return trackPm;
        });
        this.ports = new ItemListPM<>(() -> {
            NodePortPM nodePortPM = new NodePortPM();
            return nodePortPM;
        });
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
        this.ports.clear();
        node.getPorts().forEach(port -> {
            NodePortPM nodePortPm = new NodePortPM();
            nodePortPm.init(port);
            ports.add(nodePortPm);
        });
    }

    public ListPM<NodeTrackPM> getTracks() {
        return tracks;
    }

    public ListPM<NodePortPM> getPorts() {
        return ports;
    }
}
