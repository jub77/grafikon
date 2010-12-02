package net.parostroj.timetable.model.units;

import java.math.BigDecimal;
import java.math.MathContext;

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

    public String getUnitsString();

    public String getUnitsOfString();

    @Override
    public String toString();
}
