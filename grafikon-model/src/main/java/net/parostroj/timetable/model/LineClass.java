package net.parostroj.timetable.model;

import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Line class
 * 
 * @author jub
 */
public class LineClass implements ObjectWithId, Visitable {

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

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }
}
