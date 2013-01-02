package net.parostroj.timetable.model.units;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utitility class for units.
 * 
 * @author jub
 */
public class UnitUtil {

    public static BigDecimal convert(BigDecimal value, Unit from, Unit to) {
        if (to == null || from == null)
            throw new IllegalArgumentException("Units cannot be null.");
        if (!to.getClass().equals(from.getClass()))
            throw new IllegalArgumentException("Units has to have the same class.");
        if (to.getRatio() == null || from.getRatio() == null)
            return value;
        BigDecimal cRatio = from.getRatio().divide(to.getRatio(), Unit.MATH_CONTEXT);
        return value.multiply(cRatio, Unit.MATH_CONTEXT);
    }

    public static int convert(BigDecimal value) throws ArithmeticException {
        return value.setScale(0, RoundingMode.HALF_UP).intValueExact();
    }
}
