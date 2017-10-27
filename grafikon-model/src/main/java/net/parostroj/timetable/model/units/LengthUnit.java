package net.parostroj.timetable.model.units;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Unit of length.
 *
 * @author jub
 */
public enum LengthUnit implements Unit {
    MM(1, "mm"),
    CM(10, "cm"),
    M(1000, "m"),
    KM(1000000, "km"),
    INCH(BigDecimal.valueOf(254, 1), "inch"),
    YARD(BigDecimal.valueOf(9144, 1), "yard"),
    MILE(1609344, "mile"),
    AXLE("axle");

    private BigDecimal ratio;
    private String key;
    private boolean scaleDependent;

    private LengthUnit(String key) {
        this(null, key, false);
    }

    private LengthUnit(long ratio, String key) {
        this(BigDecimal.valueOf(ratio), key, true);
    }

    private LengthUnit(BigDecimal ratio, String key) {
        this(ratio, key, true);
    }

    private LengthUnit(BigDecimal ratio, String key, boolean scaleDependent) {
        this.ratio = ratio;
        this.key = key;
        this.scaleDependent = scaleDependent;
    }

    @Override
    public BigDecimal getRatio() {
        return ratio;
    }

    public String getKey() {
        return key;
    }

    public boolean isScaleDependent() {
        return scaleDependent;
    }

    /**
     * converts from this to some other unit.
     *
     * @param value value
     * @param to to unit
     * @return converted value
     */
    @Override
    public BigDecimal convertTo(BigDecimal value, Unit to) {
        return UnitUtil.convert(value, this, to);
    }

    /**
     * converts from other unit to this.
     *
     * @param value value
     * @param from from unit
     * @return converted value
     */
    @Override
    public BigDecimal convertFrom(BigDecimal value, Unit from) {
        return UnitUtil.convert(value, from, this);
    }

    @Override
    public String getUnitString(Locale locale) {
        return UnitUtil.getText("unit." + key, locale);
    }

    @Override
    public String getUnitString() {
        return getUnitString(Locale.getDefault());
    }

    @Override
    public String getUnitsString(Locale locale) {
        return UnitUtil.getText("units." + key, locale);
    }

    @Override
    public String getUnitsString() {
        return getUnitsString(Locale.getDefault());
    }

    @Override
    public String getUnitsOfString(Locale locale) {
        return UnitUtil.getText("units.of." + key, locale);
    }

    @Override
    public String getUnitsOfString() {
        return getUnitsOfString(Locale.getDefault());
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

    public static List<LengthUnit> getScaleDependent() {
        List<LengthUnit> dep = new LinkedList<>();
        for (LengthUnit unit : values())
            if (unit.isScaleDependent())
                dep.add(unit);
        return dep;
    }
}
