package net.parostroj.timetable.model;

import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Track in the station.
 *
 * @author jub
 */
public class NodeTrack extends Track implements Visitable {
    /**
     * Constructor.
     *
     * @param id id
     * @param owner owner of the track
     */
    public NodeTrack(String id, Node owner) {
        super(id, owner);
    }

    /**
     * creates instance with specified track number.
     *
     * @param id id
     * @param owner owner of the track
     * @param number track number
     */
    public NodeTrack(String id, Node owner, String number) {
        super(id, owner, number);
    }

    @Override
    public Node getOwner() {
        return (Node) super.getOwner();
    }

    @Override
    public String toString() {
        return super.toString() + (isPlatform() ?  " [" : "");
    }

    /**
     * @return the platform
     */
    public boolean isPlatform() {
        return this.getAttributes().getBool(ATTR_PLATFORM);
    }

    /**
     * @param platform the platform to set
     */
    public void setPlatform(boolean platform) {
        this.getAttributes().setBool(ATTR_PLATFORM, platform);
    }

    /**
     * accepts visitor.
     *
     * @param visitor visitor
     */
    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }
}
