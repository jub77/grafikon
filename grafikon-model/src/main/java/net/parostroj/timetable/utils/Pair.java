package net.parostroj.timetable.utils;

public class Pair<T, V> {

    public T first;

    public V second;

    public Pair(T first, V second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "<" + first + "," + second + ">";
    }
}
