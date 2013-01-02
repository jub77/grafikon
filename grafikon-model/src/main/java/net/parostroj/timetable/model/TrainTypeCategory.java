package net.parostroj.timetable.model;

/**
 * Category of train types - freight, passenger ...
 *
 * @author jub
 */
public class TrainTypeCategory {

    private String id;
    private String name;
    private String key;

    public TrainTypeCategory(String id, String name, String key) {
        this.id = id;
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name + "<" + key + ">";
    }
}
