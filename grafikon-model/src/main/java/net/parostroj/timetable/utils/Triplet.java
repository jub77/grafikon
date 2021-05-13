package net.parostroj.timetable.utils;

import java.util.Objects;

public class Triplet<T, V, U> {

    public T first;
    public V second;
    public U third;

    public Triplet() {}

    public Triplet(T first, V second, U third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public String toString() {
        return "<" + first + "," + second + "," + third + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Triplet<?, ?, ?> other = (Triplet<?, ?, ?>) obj;
        if (!Objects.equals(this.first, other.first)) {
            return false;
        }
        if (!Objects.equals(this.second, other.second)) {
            return false;
        }
        return Objects.equals(this.third, other.third);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 17 * hash + (this.second != null ? this.second.hashCode() : 0);
        hash = 17 * hash + (this.third != null ? this.third.hashCode() : 0);
        return hash;
    }
}
