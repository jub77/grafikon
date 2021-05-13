package net.parostroj.timetable.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tuple<V> {

    public V first;

    public V second;

    public Tuple() {}

    public Tuple(V first, V second) {
        this.first = first;
        this.second = second;
    }

    public List<V> toList() {
        List<V> result = new ArrayList<>(2);
        if (first != null) {
            result.add(first);
        }
        if (second != null) {
            result.add(second);
        }
        return result;
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
        final Tuple<?> other = (Tuple<?>) obj;
        if (!Objects.equals(this.first, other.first)) {
            return false;
        }
        return Objects.equals(this.second, other.second);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 37 * hash + (this.second != null ? this.second.hashCode() : 0);
        return hash;
    }
}
