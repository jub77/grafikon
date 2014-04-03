package net.parostroj.timetable.utils;

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
        if (this.first != other.first && (this.first == null || !this.first.equals(other.first))) {
            return false;
        }
        if (this.second != other.second && (this.second == null || !this.second.equals(other.second))) {
            return false;
        }
        if (this.third != other.third && (this.third == null || !this.third.equals(other.third))) {
            return false;
        }
        return true;
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
