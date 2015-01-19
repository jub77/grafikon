package net.parostroj.timetable.model.save.version01;

import net.parostroj.timetable.model.TimeInterval;

public class LSTimeInterval {

    private int start;

    private int end;

    private int routePartId;

    private int speed;

    private int stationTrackId;

    private String type;

    private int direction;

    private String comment;

    public LSTimeInterval(TimeInterval interval, LSTransformationData data) {
        start = interval.getStart();
        end = interval.getEnd();
        routePartId = data.getIdForObject(interval.getOwner());
        speed = interval.getSpeedLimit();
        stationTrackId = data.getIdForObject(interval.getTrack());
        direction = (interval.getDirection() != null) ? interval.getDirection().getNumerical() : 0;
        comment = interval.getAttribute(TimeInterval.ATTR_COMMENT, String.class);
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
    public int getRoutePartId() {
        return routePartId;
    }

    /**
     * @param routePartId the routePartId to set
     */
    public void setRoutePartId(int routePartId) {
        this.routePartId = routePartId;
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
     * @return the stationTrackId
     */
    public int getStationTrackId() {
        return stationTrackId;
    }

    /**
     * @param stationTrackId the stationTrackId to set
     */
    public void setStationTrackId(int stationTrackId) {
        this.stationTrackId = stationTrackId;
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
}
