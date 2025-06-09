package net.parostroj.timetable.model.ls;

public interface LSSource {
    Object getSource();

    static LSSource create(Object source) {
        return () -> source;
    }
}
