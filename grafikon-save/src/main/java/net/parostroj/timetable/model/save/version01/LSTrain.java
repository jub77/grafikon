package net.parostroj.timetable.model.save.version01;

import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;

public class LSTrain {

    private int id;

    private String name;
    
    private String description;

    private String trainType;

    private LSTimeInterval[] timeIntervals;

    private int topSpeed;
    
    private boolean diesel;
    
    private boolean electric;
    
    private String routeInfo;
    
    private String weightInfo;

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
        electric = (Boolean)train.getAttribute("electric");
        diesel = (Boolean)train.getAttribute("diesel");
        routeInfo = (String)train.getAttribute("route.info");
        weightInfo = (String)train.getAttribute("weight.info");
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

    public boolean isDiesel() {
        return diesel;
    }

    public void setDiesel(boolean diesel) {
        this.diesel = diesel;
    }

    public boolean isElectric() {
        return electric;
    }

    public void setElectric(boolean electric) {
        this.electric = electric;
    }

    public String getRouteInfo() {
        return routeInfo;
    }

    public void setRouteInfo(String routeInfo) {
        this.routeInfo = routeInfo;
    }

    public String getWeightInfo() {
        return weightInfo;
    }

    public void setWeightInfo(String weightInfo) {
        this.weightInfo = weightInfo;
    }
}
