package net.parostroj.timetable.model.units;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Unit of length.
 *
 * @author jub
 */
public enum LengthUnit implements Unit {
    MM(new BigDecimal(1), "mm", true),
    CM(new BigDecimal(10), "cm", true),
    M(new BigDecimal(1000), "m", true),
    KM(new BigDecimal(1000 * 1000), "km", true),
    INCH(new BigDecimal(25.4), "inch", true),
    YARD(new BigDecimal(914.4), "yard", true),
    MILE(new BigDecimal(1609.344 * 1000), "mile", true),
    AXLE(null, "axle", false);

    private BigDecimal ratio;
    private String key;
    private boolean scaleDependent;

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
    public String getUnitString() {
        return ResourceBundle.getBundle("net.parostroj.timetable.model.unit_texts").getString("unit." + key);
    }

    @Override
    public String getUnitsString() {
        return ResourceBundle.getBundle("net.parostroj.timetable.model.unit_texts").getString("units." + key);
    }

    @Override
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

    public static List<LengthUnit> getScaleDependent() {
        List<LengthUnit> dep = new LinkedList<LengthUnit>();
        for (LengthUnit unit : values())
            if (unit.isScaleDependent())
                dep.add(unit);
        return dep;
    }
}
