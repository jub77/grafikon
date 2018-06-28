package net.parostroj.timetable.gui.pm;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.support.Operation;
import org.beanfabrics.support.Validation;

import net.parostroj.timetable.model.Node;

/**
 * @author jub
 */
public class NodePM extends AbstractPM implements IPM<Node> {

    ListPM<NodeTrackPM> tracks;
    OperationPM moveTrackUp;
    OperationPM moveTrackDown;
    OperationPM deleteTracks;
    OperationPM createTrack;

    public NodePM() {
        this.tracks = new ListPM<>();
        this.moveTrackUp = new OperationPM();
        this.moveTrackDown = new OperationPM();
        this.deleteTracks = new OperationPM();
        this.createTrack = new OperationPM();
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
    }

    @Operation(path = { "moveTrackUp" })
    public void moveTrackUp() {
        int index = this.tracks.getSelection().getMinIndex();
        this.tracks.swap(index - 1, index);
    }

    @Operation(path = { "moveTrackDown" })
    public void moveTrackDown() {
        int index = this.tracks.getSelection().getMinIndex();
        this.tracks.swap(index, index + 1);
    }

    @Validation(path = { "moveTrackUp" })
    public boolean canMoveTrackUp() {
        int[] indexes = tracks.getSelection().getIndexes();
        return indexes.length == 1 && indexes[0] != 0;
    }

    @Operation(path = { "deleteTracks" })
    public void deleteTracks() {
        this.tracks.removeAll(this.tracks.getSelection().toCollection());
    }

    @Operation(path = { "createTrack" })
    public void createTrack() {
        NodeTrackPM element = new NodeTrackPM();
        element.number.setText("-");
        this.tracks.add(element);
    }

    @Validation(path = { "moveTrackDown" })
    public boolean canMoveTrackDown() {
        int[] indexes = tracks.getSelection().getIndexes();
        return indexes.length == 1 && indexes[0] != tracks.size() - 1;
    }

    @Validation(path = { "deleteTracks" })
    public boolean isTracksSelected() {
        return !tracks.getSelection().isEmpty();
    }

    public ListPM<NodeTrackPM> getTracks() {
        return tracks;
    }
}
