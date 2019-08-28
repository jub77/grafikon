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

    // deprecated
    private Boolean platform;

    public LSNodeTrack(NodeTrack track) {
        super(track);
    }

    public LSNodeTrack() {
    }

    public Boolean isPlatform() {
        return platform;
    }

    public void setPlatform(Boolean platform) {
        this.platform = platform;
    }

    public NodeTrack createNodeTrack(Node node, Function<String, ObjectWithId> mapping) throws LSException {
        NodeTrack nodeTrack = new NodeTrack(this.getId(), node);
        this.addValuesTrack(mapping, nodeTrack);
        if (getVersion() == 0) {
            nodeTrack.setPlatform(platform);
        }
        return nodeTrack;
    }
}
