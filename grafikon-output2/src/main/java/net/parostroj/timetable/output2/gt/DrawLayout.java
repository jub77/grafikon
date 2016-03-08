package net.parostroj.timetable.output2.gt;

public class DrawLayout {

    public enum Orientation {
        LEFT_RIGHT, TOP_DOWN;
    }

    private final Orientation orientation;

    public DrawLayout() {
        this(Orientation.TOP_DOWN);
    }

    public DrawLayout(Orientation orientation) {
        this.orientation = orientation;
    }

    public Orientation getOrientation() {
        return orientation;
    }
}
