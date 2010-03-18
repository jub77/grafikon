package net.parostroj.timetable.model.ls.impl4;

import net.parostroj.timetable.model.NodeTrack;

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

    public NodeTrack createNodeTrack() {
        NodeTrack nodeTrack = new NodeTrack(this.getId());
        this.addValuesTrack(nodeTrack);
        nodeTrack.setPlatform(platform);
        return nodeTrack;
    }
}
