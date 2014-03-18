package net.parostroj.timetable.model.save.version02;

import java.util.UUID;
import net.parostroj.timetable.model.Net;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.utils.Tuple;

public class LSLine {

    private int id;
    
    private String uuid;

    private int sourceId;

    private int targetId;
    
    private LSLineTrack[] lineTracks;
    
    private int length;

    private int topSpeed;

    private LSAttributes attributes;

    public LSLine() {
    }

    public LSLine(Net net, Line line, LSTransformationData data) {
        id = data.getId();
        data.addObjectWithId(line, id);

        Tuple<Node> ends = net.getNodes(line);
        sourceId = data.getIdForObject(ends.first);
        targetId = data.getIdForObject(ends.second);
        
        length = line.getLength();
        topSpeed = line.getTopSpeed();

        lineTracks = new LSLineTrack[line.getTracks().size()];
        int i = 0;
        for (LineTrack lineTrack : line.getTracks()) {
            LSLineTrack lLineTrack = new LSLineTrack(lineTrack, data);
            lineTracks[i++] = lLineTrack;
        }
        attributes = new LSAttributes(line.getAttributes(), data);
        uuid = line.getId();
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

    public void setLineTracks(LSLineTrack[] lineTracks) {
        this.lineTracks = lineTracks;
    }

    public LSLineTrack[] getLineTracks() {
        return lineTracks;
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public String getUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void visit(LSVisitor visitor) {
        visitor.visit(this);

        if (lineTracks != null)
            // visit all tracks
            for (LSLineTrack lsLineTrack : lineTracks) {
                lsLineTrack.visit(visitor);
            }
    }
}
