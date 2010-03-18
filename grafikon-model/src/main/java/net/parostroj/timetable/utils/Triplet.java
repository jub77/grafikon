package net.parostroj.timetable.utils;

public class Triplet<T, V, U> {

    public T first;
    public V second;
    public U third;

    public Triplet(T first, V second, U third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public String toString() {
        return "<" + first + "," + second + "," + third + ">";
    }
}
