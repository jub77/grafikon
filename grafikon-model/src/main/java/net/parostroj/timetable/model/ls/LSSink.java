package net.parostroj.timetable.model.ls;

public interface LSSink {
    Object getSink();

    static LSSink create(Object sink) {
        return () -> sink;
    }
}
