package net.parostroj.timetable.model.save.version02;

import java.util.UUID;
import net.parostroj.timetable.model.NodeTrack;

public class LSNodeTrack {

    private int id;
    
    private String uuid;

    private String number;

    private boolean platform;

    private LSAttributes attributes;

    public LSNodeTrack() {
    }

    public LSNodeTrack(NodeTrack nodeTrack, LSTransformationData data) {
        id = data.getId();
        data.addObjectWithId(nodeTrack, id);
        number = nodeTrack.getNumber();
        platform = nodeTrack.isPlatform();
        attributes = new LSAttributes(nodeTrack.getAttributes(), data);
        uuid = nodeTrack.getId();
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
