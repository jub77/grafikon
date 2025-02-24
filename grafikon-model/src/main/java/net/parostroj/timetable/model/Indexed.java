package net.parostroj.timetable.model;

public interface Indexed<T> {
    T get(int index);

    T getFirst();

    T getLast();

    int size();
}
