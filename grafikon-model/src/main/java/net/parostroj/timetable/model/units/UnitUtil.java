package net.parostroj.timetable.model.units;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utitility class for units.
 *
 * @author jub
 */
public class UnitUtil {

    private static final Logger LOG = LoggerFactory.getLogger(UnitUtil.class);

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

    public static final String FORMAT_F = "#0.########";

    public static String getStringValue(String formatPattern, Number value) {
        DecimalFormat format = new  DecimalFormat(formatPattern);
        format.setDecimalSeparatorAlwaysShown(false);
        format.setParseBigDecimal(true);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(BigDecimal.class);
        formatter.setMinimum(new BigDecimal(0));
        try {
            return formatter.valueToString(value);
        } catch (ParseException e) {
            LOG.error(e.getMessage(), e);
            return "-";
        }
    }
}
