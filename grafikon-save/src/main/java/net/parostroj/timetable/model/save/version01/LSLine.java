package net.parostroj.timetable.model.save.version01;

import net.parostroj.timetable.model.Net;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.utils.Tuple;

public class LSLine {

    private int id;

    private int sourceId;

    private int targetId;
    
    private int sourceTrackId;
    
    private int targetTrackId;

    private int length;

    private int topSpeed;

    public LSLine() {
    }

    public LSLine(Net net, Line line, LSTransformationData data) {
        id = data.getId();
        data.addObjectWithId(line, id);

        Tuple<Node> ends = net.getNodes(line);
        sourceId = data.getIdForObject(ends.first);
        targetId = data.getIdForObject(ends.second);
        
        sourceTrackId = data.getIdForObject(line.getTracks().get(0).getFromStraightTrack());
        targetTrackId = data.getIdForObject(line.getTracks().get(0).getToStraightTrack());

        length = line.getLength();
        topSpeed = line.getTopSpeed();
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the source_id
     */
    public int getSourceId() {
        return sourceId;
    }

    /**
     * @param source_id the source_id to set
     */
    public void setSourceId(int source_id) {
        this.sourceId = source_id;
    }

    /**
     * @return the target_id
     */
    public int getTargetId() {
        return targetId;
    }

    /**
     * @param target_id the target_id to set
     */
    public void setTargetId(int target_id) {
        this.targetId = target_id;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the topSpeed
     */
    public int getTopSpeed() {
        return topSpeed;
    }

    /**
     * @param topSpeed the topSpeed to set
     */
    public void setTopSpeed(int topSpeed) {
        this.topSpeed = topSpeed;
    }

    public int getSourceTrackId() {
        return sourceTrackId;
    }

    public void setSourceTrackId(int sourceTrackId) {
        this.sourceTrackId = sourceTrackId;
    }

    public int getTargetTrackId() {
        return targetTrackId;
    }

    public void setTargetTrackId(int targetTrackId) {
        this.targetTrackId = targetTrackId;
    }

    public void visit(LSVisitor visitor) {
        visitor.visit(this);
    }
}
