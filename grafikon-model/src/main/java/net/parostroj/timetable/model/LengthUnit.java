package net.parostroj.timetable.model;

import java.util.ResourceBundle;

/**
 * Unit of length.
 *
 * @author jub
 */
public enum LengthUnit {
    MM(1, "mm", true),
    CM(10, "cm", true),
    M(1000, "m", true),
    KM(1000 * 1000, "km", true),
    INCH(25.4, "inch", true),
    YARD(914.4, "yard", true),
    MILE(1609.344 * 1000, "mile", true),
    AXLE(0, "axles", false);

    private double ratio;
    private String key;
    private boolean scaleDependent;

    private LengthUnit(double ratio, String key, boolean scaleDependent) {
        this.ratio = ratio;
        this.key = key;
        this.scaleDependent = scaleDependent;
    }

    public double getRatio() {
        return ratio;
    }

    public String getKey() {
        return key;
    }

    public boolean isScaleDependent() {
        return scaleDependent;
    }

    /**
     * converts from base mm to some other unit.
     *
     * @param value value
     * @return converted value
     */
    public double convertTo(int value) {
        if (!scaleDependent)
            throw new IllegalStateException("Cannot convert to scale independent value.");
        return value / ratio;
    }

    /**
     * converts to base mm from other unit.
     *
     * @param value value
     * @return converted value
     */
    public int convertFrom(double value) {
        if (!scaleDependent)
            throw new IllegalStateException("Cannot convert from scale independent value.");
        return (int)Math.round(value * ratio);
    }

    public String getUnitString() {
        return ResourceBundle.getBundle("net.parostroj.timetable.model.unit_texts").getString("unit." + key);
    }

    public String getUnitsString() {
        return ResourceBundle.getBundle("net.parostroj.timetable.model.unit_texts").getString("units." + key);
    }

    public String getUnitsOfString() {
        return ResourceBundle.getBundle("net.parostroj.timetable.model.unit_texts").getString("units.of" + key);
    }

    @Override
    public String toString() {
        return getUnitsString();
    }

    /**
     * returns unit of length by key.
     *
     * @param key key
     * @return length unit
     */
    public static LengthUnit getByKey(String key) {
        for (LengthUnit unit : values()) {
            if (unit.getKey().equals(key))
                return unit;
        }
        return null;
    }
}
