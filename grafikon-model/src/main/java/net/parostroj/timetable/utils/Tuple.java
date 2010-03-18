package net.parostroj.timetable.utils;

public class Tuple<V> {

    public V first;

    public V second;

    public Tuple(V first, V second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "<" + first + "," + second + ">";
    }
}
