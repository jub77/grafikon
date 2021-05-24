package net.parostroj.timetable.model.units;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Unit of weight.
 *
 * @author jub
 */
public enum WeightUnit implements Unit {
    KG(1, "kg"),
    T(1000, "t");

    private final BigDecimal ratio;
    private final String key;

    WeightUnit(long ratio, String key) {
        this.ratio = BigDecimal.valueOf(ratio);
        this.key = key;
    }

    @Override
    public BigDecimal getRatio() {
        return ratio;
    }

    public String getKey() {
        return key;
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
        return getUnitString(Locale.getDefault());
    }

    @Override
    public String getUnitString(Locale locale) {
        return UnitUtil.getText("unit." + key, locale);
    }

    @Override
    public String getUnitsString() {
        return getUnitsString(Locale.getDefault());
    }

    @Override
    public String getUnitsString(Locale locale) {
        return UnitUtil.getText("units." + key, locale);
    }

    @Override
    public String getUnitsOfString() {
        return getUnitsOfString(Locale.getDefault());
    }

    @Override
    public String getUnitsOfString(Locale locale) {
        return UnitUtil.getText("units.of." + key, locale);
    }

    @Override
    public String toString() {
        return getUnitsString();
    }

    /**
     * returns unit of weight by key.
     *
     * @param key key
     * @return weight unit
     */
    public static WeightUnit getByKey(String key) {
        for (WeightUnit unit : values()) {
            if (unit.getKey().equals(key))
                return unit;
        }
        return null;
    }
}
