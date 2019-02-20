package net.parostroj.timetable.model.ls.impl4;

import java.util.function.Function;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Class for node tracks storage.
 *
 * @author jub
 */
public class LSNodeTrack extends LSTrack {

    private boolean platform;

    public LSNodeTrack(NodeTrack track) {
        super(track);
        this.platform = track.isPlatform();
    }

    public LSNodeTrack() {
    }

    public boolean isPlatform() {
        return platform;
    }

    public void setPlatform(boolean platform) {
        this.platform = platform;
    }

    public NodeTrack createNodeTrack(Node node, Function<String, ObjectWithId> mapping) throws LSException {
        NodeTrack nodeTrack = new NodeTrack(this.getId(), node);
        this.addValuesTrack(mapping, nodeTrack);
        nodeTrack.setPlatform(platform);
        return nodeTrack;
    }
}
