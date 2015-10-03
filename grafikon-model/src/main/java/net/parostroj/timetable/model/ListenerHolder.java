package net.parostroj.timetable.model;

public interface ListenerHolder<T> {

    void addListener(T listener);

    void removeListener(T listener);

    void removeAllListeners();
}
