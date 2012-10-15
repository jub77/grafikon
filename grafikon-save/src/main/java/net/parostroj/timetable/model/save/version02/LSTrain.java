package net.parostroj.timetable.model.save.version02;

import java.util.UUID;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;

public class LSTrain {

    private int id;
    
    private String uuid;

    private String name;
    
    private String description;

    private String trainType;

    private LSTimeInterval[] timeIntervals;

    private int topSpeed;

    private LSAttributes attributes;

    public LSTrain() {
    }

    public LSTrain(Train train, LSTransformationData data) {
        id = data.getId();
        data.addObjectWithId(train, id);
        trainType = data.getKeyForTrainType(train.getType());
        topSpeed = train.getTopSpeed();
        timeIntervals = new LSTimeInterval[train.getTimeIntervalList().size()];
        int i = 0;
        for (TimeInterval interval : train.getTimeIntervalList()) {
            LSTimeInterval lsInterval = new LSTimeInterval(interval, data);
            timeIntervals[i++] = lsInterval;
        }
        name = train.getNumber();
        description = train.getDescription();
        attributes = new LSAttributes(train.getAttributes(), data);
        uuid = train.getId();
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
     * @return the timeIntervals
     */
    public LSTimeInterval[] getTimeIntervals() {
        return timeIntervals;
    }

    /**
     * @param timeIntervals the timeIntervals to set
     */
    public void setTimeIntervals(LSTimeInterval[] timeIntervals) {
        this.timeIntervals = timeIntervals;
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

    /**
     * @return the trainType
     */
    public String getTrainType() {
        return trainType;
    }

    /**
     * @param trainType the trainType to set
     */
    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public void visit(LSVisitor visitor) {
        visitor.visit(this);

        // visit time intervals
        if (timeIntervals != null)
            for (LSTimeInterval lsInterval : timeIntervals) {
                lsInterval.visit(visitor);
            }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
