package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.gui.utils.ResourceLoader;

/**
 * Length unit.
 *
 * @author jub
 */
public enum LengthUnit {
    MM(1, "mm"), CM(10, "cm"), M(1000, "m"), KM(1000 * 1000, "km"), INCH(25.4, "inch"), YARD(914.4, "yard"), MILE(1609.344 * 1000, "mile");

    private double ratio;
    private String key;
    private String value;

    static {
        for (LengthUnit unit : values())
            unit.initializeValue();
    }

    private LengthUnit(double ratio, String key) {
        this.ratio = ratio;
        this.key = key;
    }

    public double getRatio() {
        return ratio;
    }

    public String getKey() {
        return key;
    }

    private void initializeValue() {
        value = ResourceLoader.getString("unit." + key);
        if (value == null)
            value = key;
    }

    public String getValue() {
        return value;
    }

    /**
     * converts from base mm to some other unit.
     *
     * @param value value
     * @return converted value
     */
    public double convertTo(int value) {
        return value / ratio;
    }

    /**
     * converts to base mm from other unit.
     *
     * @param value value
     * @return converted value
     */
    public int convertFrom(double value) {
        return (int)Math.round(value * ratio);
    }
}
