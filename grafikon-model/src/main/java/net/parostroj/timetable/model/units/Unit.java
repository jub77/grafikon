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

    public static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    public BigDecimal getRatio();

    public BigDecimal convertTo(BigDecimal value, Unit to);

    public BigDecimal convertFrom(BigDecimal value, Unit from);

    public String getUnitString();

    public String getUnitString(Locale locale);

    public String getUnitsString();

    public String getUnitsString(Locale locale);

    public String getUnitsOfString();

    public String getUnitsOfString(Locale locale);

    @Override
    public String toString();
}
