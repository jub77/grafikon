package net.parostroj.timetable.model;

/**
 * Line type.
 *
 * @author jub
 */
public enum LineType {
    SOLID(0, "solid"), DASH(1, "dash"), DASH_AND_DOT(2, "dash.and.dot"), DOT(3, "dot");

    private int value;
    private String key;

    private LineType(int value, String key) {
        this.value = value;
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public static LineType valueOf(Integer value) {
        if (value != null) {
            for (LineType type : values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
        }
        return LineType.SOLID;
    }
}
