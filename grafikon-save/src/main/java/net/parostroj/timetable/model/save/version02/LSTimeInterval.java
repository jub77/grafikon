package net.parostroj.timetable.model.save.version02;

import net.parostroj.timetable.model.TimeInterval;

public class LSTimeInterval {

    private int start;

    private int end;

    private int ownerId;

    private int speed;

    private int trackId;

    private String type;
    
    private int direction;
    
    private String comment;

    private LSAttributes attributes;

    public LSTimeInterval(TimeInterval interval, LSTransformationData data) {
        start = interval.getStart();
        end = interval.getEnd();
        ownerId = data.getIdForObject(interval.getOwner());
        speed = interval.getSpeed();
        trackId = data.getIdForObject(interval.getTrack());
        direction = (interval.getDirection() != null) ? interval.getDirection().getNumerical() : 0;
        comment = (String)interval.getAttribute("comment");
        attributes = new LSAttributes(interval.getAttributes(), data);
    }

    public LSTimeInterval() {
    }

    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public int getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(int end) {
        this.end = end;
    }

    public void visit(LSVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @return the routePartId
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId the owner id to set
     */
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * @return the speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * @return the track id
     */
    public int getTrackId() {
        return trackId;
    }

    /**
     * @param trackId the track id to set
     */
    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the direction
     */
    public int getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

}
