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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tuple<V> other = (Tuple<V>) obj;
        if (this.first != other.first && (this.first == null || !this.first.equals(other.first))) {
            return false;
        }
        if (this.second != other.second && (this.second == null || !this.second.equals(other.second))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 37 * hash + (this.second != null ? this.second.hashCode() : 0);
        return hash;
    }
}
