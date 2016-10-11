package net.parostroj.timetable.model.units;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.text.NumberFormatter;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.ResourceBundleUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utitility class for units.
 *
 * @author jub
 */
public class UnitUtil {

    private static final Logger log = LoggerFactory.getLogger(UnitUtil.class);

    public static BigDecimal convert(BigDecimal value, Unit from, Unit to) {
        if (to == null || from == null)
            throw new IllegalArgumentException("Units cannot be null.");
        if (!to.getClass().equals(from.getClass()))
            throw new IllegalArgumentException("Units has to have the same class.");
        if (to.getRatio() == null || from.getRatio() == null)
            return value;
        if (from.equals(to) || (from.getRatio() != null && from.getRatio().equals(to.getRatio())))
            return value;
        BigDecimal cRatio = from.getRatio().divide(to.getRatio(), Unit.MATH_CONTEXT);
        return value.multiply(cRatio, Unit.MATH_CONTEXT);
    }

    public static int convert(BigDecimal value) throws ArithmeticException {
        return value.setScale(0, RoundingMode.HALF_UP).intValueExact();
    }

    public static final String FORMAT_F = "#0.########";

    public static String convertToString(String formatPattern, BigDecimal value) {
        DecimalFormat format = new  DecimalFormat(formatPattern);
        format.setDecimalSeparatorAlwaysShown(false);
        format.setParseBigDecimal(true);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(BigDecimal.class);
        formatter.setMinimum(new BigDecimal(0));
        try {
            return formatter.valueToString(value);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return "-";
        }
    }

    public static double getRouteLengthRatio(TrainDiagram diagram) {
        Double ratio = diagram.getAttribute(TrainDiagram.ATTR_ROUTE_LENGTH_RATIO, Double.class);
        int scaleRatio = diagram.getScale().getRatio();
        return ratio == null ? scaleRatio : ratio * scaleRatio;
    }

    public static double convertRouteLenght(double length, TrainDiagram diagram, double ratio) {
        double result = length * ratio;
        LengthUnit lengthUnit = diagram.getAttribute(TrainDiagram.ATTR_ROUTE_LENGTH_UNIT, LengthUnit.class);
        if (lengthUnit != null) {
            result = lengthUnit.convertFrom(BigDecimal.valueOf(result), LengthUnit.MM).doubleValue();
        }
        return result;
    }

    static String getText(String key, Locale locale) {
        ResourceBundle bundle = ResourceBundleUtil.getBundle("net.parostroj.timetable.model.unit_texts", UnitUtil.class.getClassLoader(), locale, Locale.ENGLISH);
        return bundle.getString(key);
    }
}
