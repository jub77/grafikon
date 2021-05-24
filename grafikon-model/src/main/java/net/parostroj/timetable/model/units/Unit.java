package net.parostroj.timetable.model.units;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Locale;

/**
 * Unit.
 *
 * @author jub
 */
public interface Unit {

    MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    BigDecimal getRatio();

    BigDecimal convertTo(BigDecimal value, Unit to);

    BigDecimal convertFrom(BigDecimal value, Unit from);

    String getUnitString();

    String getUnitString(Locale locale);

    String getUnitsString();

    String getUnitsString(Locale locale);

    String getUnitsOfString();

    String getUnitsOfString(Locale locale);

    @Override
    String toString();
}
