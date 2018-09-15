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
     */
    public NodeTrack(String id) {
        super(id);
    }

    /**
     * creates instance with specified track number.
     *
     * @param id id
     * @param number track number
     */
    public NodeTrack(String id, String number) {
        super(id, number);
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
