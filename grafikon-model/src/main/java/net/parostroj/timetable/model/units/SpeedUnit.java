package net.parostroj.timetable.model.units;

import java.math.BigDecimal;

/**
 * Unit of speed.
 *
 * @author jub
 */
public enum SpeedUnit implements Unit {
    MPS(1, "mps"),
    KMPH(BigDecimal.valueOf(36, 1).pow(-1, Unit.MATH_CONTEXT), "kmph"),
    MPH(BigDecimal.valueOf(1609344, 3).divide(BigDecimal.valueOf(1000), Unit.MATH_CONTEXT).divide(BigDecimal.valueOf(36, 1), Unit.MATH_CONTEXT), "mph");

    private BigDecimal ratio;
    private String key;

    private SpeedUnit(long ratio, String key) {
        this(BigDecimal.valueOf(ratio), key);
    }

    private SpeedUnit(BigDecimal ratio, String key) {
        this.ratio = ratio;
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
        return UnitUtil.getText("unit." + key);
    }

    @Override
    public String getUnitsString() {
        return UnitUtil.getText("units." + key);
    }

    @Override
    public String getUnitsOfString() {
        return UnitUtil.getText("units.of." + key);
    }

    @Override
    public String toString() {
        return getUnitsString();
    }

    /**
     * returns unit of speed by key.
     *
     * @param key key
     * @return speed unit
     */
    public static SpeedUnit getByKey(String key) {
        for (SpeedUnit unit : values()) {
            if (unit.getKey().equals(key))
                return unit;
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println("R m/s: " + MPS.getRatio());
        System.out.println("R km/h: " + KMPH.getRatio());
        System.out.println("R mph: " + MPH.getRatio());
        System.out.println(UnitUtil.convert(new BigDecimal(1), KMPH, MPH));
        System.out.println(UnitUtil.convert(new BigDecimal(1), KMPH, MPS));
        System.out.println(UnitUtil.convert(new BigDecimal(1), MPS, KMPH));
        System.out.println(UnitUtil.convert(new BigDecimal(1), MPS, MPH));
    }
}
