package net.parostroj.timetable.utils;

import java.util.Objects;

public class Pair<T, V> {

    public T first;

    public V second;

    public Pair() {}

    public Pair(T first, V second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "<" + first + "," + second + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<?, ?> other = (Pair<?, ?>) obj;
        if (!Objects.equals(this.first, other.first)) {
            return false;
        }
        return Objects.equals(this.second, other.second);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 61 * hash + (this.second != null ? this.second.hashCode() : 0);
        return hash;
    }
}
