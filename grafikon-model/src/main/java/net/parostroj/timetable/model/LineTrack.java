package net.parostroj.timetable.model;

import java.util.Optional;

import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Line track.
 *
 * @author jub
 */
public class LineTrack extends Track implements Visitable {
    /**
     * Constructor.
     *
     * @param id id
     * @param owner owner of the track
     */
    public LineTrack(String id, Line owner) {
        super(id, owner);
    }

    @Override
    public Line getOwner() {
        return (Line) super.getOwner();
    }

    /**
     * Constructor with number of the track.
     *
     * @param id id
     * @param owner owner of the track
     * @param number track number
     */
    public LineTrack(String id, Line owner, String number) {
        super(id, owner, number);
    }

    public Optional<TrackConnector> getFromTrackConnector() {
        Line line = getOwner();
        Node fromNode = line.getDiagram().getNet().getFrom(line);
        return fromNode.getConnectors().find(conn -> conn.getLineTrack() == this);
    }

    public Optional<TrackConnector> getToTrackConnector() {
        Line line = getOwner();
        Node fromNode = line.getDiagram().getNet().getTo(line);
        return fromNode.getConnectors().find(conn -> conn.getLineTrack() == this);
    }

    public Optional<TrackConnector> getFromTrackConnector(TimeIntervalDirection direction) {
        return (direction == TimeIntervalDirection.FORWARD) ? getFromTrackConnector()
                : getToTrackConnector();
    }

    public Optional<TrackConnector> getToTrackConnector(TimeIntervalDirection direction) {
        return (direction == TimeIntervalDirection.FORWARD) ? getToTrackConnector()
                : getFromTrackConnector();
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
