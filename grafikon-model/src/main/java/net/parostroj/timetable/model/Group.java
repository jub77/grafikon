package net.parostroj.timetable.model;

public class Group implements ObjectWithId {

    /** ID. */
    private final String id;
    /** Name */
    private String name;

    Group(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
