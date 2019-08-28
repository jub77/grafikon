package net.parostroj.timetable.model.save.version02;

import java.util.UUID;
import net.parostroj.timetable.model.LineTrack;

public class LSLineTrack {

    private int id;

    private String uuid;

    private String number;
    // deprecated (backward compatibility)
    private int sourceTrackId;
    // deprecated (backward compatibility)
    private int targetTrackId;

    private LSAttributes attributes;

    public LSLineTrack() {
    }

    public LSLineTrack(LineTrack lineTrack, LSTransformationData data) {
        id = data.getId();
        data.addObjectWithId(lineTrack, id);
        number = lineTrack.getNumber();

        attributes = new LSAttributes(lineTrack.getAttributes(), data);
        uuid = lineTrack.getId();
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
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(String number) {
        this.number = number;
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

    /**
     * Method for visitor pattern.
     *
     * @param visitor visitor
     */
    public void visit(LSVisitor visitor) {
        visitor.visit(this);
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
}
