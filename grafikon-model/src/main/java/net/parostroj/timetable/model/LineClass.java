package net.parostroj.timetable.model;

/**
 * Line class
 * 
 * @author jub
 */
public class LineClass implements ObjectWithId {

    private final String id;
    private String name;

    public LineClass(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
