package net.parostroj.timetable.model.save.version01;

import net.parostroj.timetable.model.NodeTrack;

public class LSNodeTrack {

    private int id;

    private String number;

    private boolean platform;

    public LSNodeTrack() {
    }

    public LSNodeTrack(NodeTrack stationTrack, LSTransformationData data) {
        id = data.getId();
        data.addObjectWithId(stationTrack, id);
        number = stationTrack.getNumber();
        platform = stationTrack.isPlatform();
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

    public void visit(LSVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @return the platform
     */
    public boolean isPlatform() {
        return platform;
    }

    /**
     * @param platform the platform to set
     */
    public void setPlatform(boolean platform) {
        this.platform = platform;
    }
}
