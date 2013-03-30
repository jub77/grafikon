package net.parostroj.timetable.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import net.parostroj.timetable.utils.TimeUtil;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Utility class for converting time from text to int and backwards.
 *
 * @author jub
 */
public class TimeConverter {

    public static enum Rounding {
        MINUTE("minute"), HALF_MINUTE("half.minute"), TENTH_OF_MINUTE("tenth.of.minute");

        private final String key;

        private Rounding(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public static Rounding fromString(String str) {
            for (Rounding r : values()) {
                if (r.getKey().equals(str))
                    return r;
            }
            return null;
        }
    }

    private static final DateTimeFormatter PRINT = new DateTimeFormatterBuilder().appendHourOfDay(1).appendLiteral(':')
            .appendMinuteOfHour(2).toFormatter();
    private static final DateTimeFormatter PRINT_FULL = new DateTimeFormatterBuilder().appendHourOfDay(2)
            .appendLiteral(':').appendMinuteOfHour(2).toFormatter();

    private final DateTimeFormatter parse;
    private final DateTimeFormatter print;
    private final DateTimeFormatter printFull;
    private final DateTimeFormatter printXml;
    private final DecimalFormat duration;

    private final Rounding rounding;

    /**
     * creates instance with specific rounding.
     *
     * @param rounding
     *            rounding
     */
    public TimeConverter(Rounding rounding) {
        this.rounding = rounding;
        char separator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
        DateTimeFormatterBuilder parseBuilder = new DateTimeFormatterBuilder().appendHourOfDay(2).appendLiteral(':')
                .appendMinuteOfHour(2);
        parseBuilder.appendOptional(new DateTimeFormatterBuilder().appendLiteral(separator)
                .appendFractionOfMinute(0, 1).toParser());
        this.parse = parseBuilder.toFormatter();
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder().appendHourOfDay(1).appendLiteral(':')
                .appendMinuteOfHour(2);
        DateTimeFormatterBuilder builderFull = new DateTimeFormatterBuilder().appendHourOfDay(2).appendLiteral(':')
                .appendMinuteOfHour(2);
        if (rounding != Rounding.MINUTE) {
            builder.appendLiteral(separator).appendFractionOfMinute(1, 1);
            builderFull.appendLiteral(separator).appendFractionOfMinute(1, 1);
        }
        this.print = builder.toFormatter();
        this.printFull = builderFull.toFormatter();
        this.duration = new DecimalFormat("###.#");
        this.printXml = ISODateTimeFormat.hourMinuteSecond();
    }

    /**
     * @return rounding
     */
    public Rounding getRounding() {
        return rounding;
    }

    /**
     * adjusts time for rounding - add 30s or 15s.
     *
     * @param time time
     * @return adjusted time
     */
    public int round(int time) {
    	return round(time, rounding);
    }

    /**
     * implementation of rounding with optional parameter.
     */
    public static int round(int time, Rounding r) {
        switch (r) {
            case MINUTE:
                time = (time + 30) / 60 * 60;
                break;
            case HALF_MINUTE:
                time = (time + 15) / 30 * 30;
                break;
            case TENTH_OF_MINUTE:
                time = (time + 3) / 6 * 6;
                break;
        }
        return time;
    }

    /**
     * creates local time from seconds (it also normalizes time).
     *
     * @param time time
     * @return local time instance
     */
    private LocalTime getLocalTime(int time) {
    	return LocalTime.fromMillisOfDay(TimeUtil.normalizeTime(time) * 1000);
    }

    /**
     * converts from seconds to textual representation.
     *
     * @param time time in seconds
     * @return textual representation
     */
    public String convertIntToText(int time) {
    	return this.convertIntToText(time, false);
    }

    /**
     * converts from seconds to textual representation.
     *
     * @param time time in seconds
     * @param fixedWidth if two spaces should be added if there is no fraction of minute
     * @return textual representation
     */
    public String convertIntToText(int time, boolean fixedWidth) {
    	return this.convertIntToTextImpl(time, fixedWidth, PRINT, print);
    }

    /**
     * converts from seconds to textual representation (hours always two digits).
     *
     * @param time time in seconds
     * @return textual representation
     */
    public String convertIntToTextFull(int time) {
    	return this.convertIntToTextFull(time, false);
    }

    /**
     * converts from seconds to textual representation (hours always two digits).
     *
     * @param time time in seconds
     * @param fixedWidth if two spaces should be added if there is no fraction of minute
     * @return textual representation
     */
    public String convertIntToTextFull(int time, boolean fixedWidth) {
    	return this.convertIntToTextImpl(time, fixedWidth, PRINT_FULL, printFull);
    }

    private String convertIntToTextImpl(int time, boolean fixed, DateTimeFormatter shortFormat,
            DateTimeFormatter longFormat) {
        LocalTime localTime = this.getLocalTime(time);
        String timeStr = null;
        if (localTime.getSecondOfMinute() == 0 && !fixed) {
            timeStr = localTime.toString(shortFormat);
        } else {
            timeStr = localTime.toString(longFormat);
        }
        return timeStr;
    }

    /**
     * converts to format for xml.
     *
     * @param time time in seconds
     * @return text
     */
    public String convertIntToXml(int time) {
    	return printXml.print(this.getLocalTime(time));
    }

    /**
     * converts from seconds to textual representation (not normalized).
     *
     * @param time time in seconds
     * @return textual representation
     */
    public String convertIntToTextNNFull(int time) {
        LocalTime lt = this.getLocalTime(time);
        String result = lt.toString(printFull);
        if (time == TimeInterval.DAY) {
            result = result.replace("00:", "24:");
        }
        return result;
    }

    /**
     * @param time time in seconds
     * @return true if after rounding the result is half minute
     */
    public boolean isHalfMinute(int time) {
        if (rounding == Rounding.MINUTE)
            return false;
        else {
            time = round(time, Rounding.HALF_MINUTE);
            return ((time % 3600) / 30) % 2 == 1;
        }
    }

    /**
     * returns the last digit from minutes (needed by GT).
     *
     * @param time time in seconds
     * @return last digit of minutes
     */
    public String getLastDigitOfMinutes(int time) {
    	return Integer.toString(this.getLocalTime(time).getMinuteOfHour() % 10);
    }

    /**
     * converts text representation of minutes to int.
     *
     * @param text text
     * @return time in seconds
     * @throws ParseException
     */
    public int convertMinutesTextToInt(String text) throws ParseException {
    	Number number = duration.parse(text);
    	return round((int) (number.doubleValue() * 60));
    }

    /**
     * converts time to string with minutes
     *
     * @param time time in seconds
     * @return string with minutes
     */
    public String convertIntToMinutesText(int time) {
    	return duration.format(((double) time) / 60);
    }

    /**
     * converts from textual representation to seconds. It returns <code>-1</code>
     * if there was problem with converting the value.
     *
     * @param text textual representation
     * @return time in seconds from midnight
     */
    public int convertTextToInt(String text) {
        char separator = DecimalFormatSymbols.getInstance().getDecimalSeparator();

        // remove spaces
        text = text.trim();

        int size = 0;
        int[] values = new int[4];
        StringBuilder decimalBuilder = new StringBuilder();
        boolean hm = true;

        // get digits
        for (char ch : text.toCharArray()) {
            if (ch == separator)
                hm = false;
            if (Character.isDigit(ch)) {
                if (hm && size < 4) {
                    values[size++] = Character.digit(ch, 10);
                } else if (!hm) {
                    if (decimalBuilder.length() == 0)
                        decimalBuilder.append(separator);
                    decimalBuilder.append(ch);
                }
            }
        }

        int time = -1;
        // convert to time
        switch (size) {
            case 1:
                time = this.parse(String.format("0%s:00" + decimalBuilder.toString(), values[0]));
                break;
            case 2:
                time = this.parse(String.format("%s%s:00" + decimalBuilder.toString(), values[0], values[1]));
                break;
            case 3:
                time = this
                        .parse(String.format("0%s:%s%s" + decimalBuilder.toString(), values[0], values[1], values[2]));
                break;
            case 4:
                time = this.parse(String.format("%s%s:%s%s" + decimalBuilder.toString(), values[0], values[1],
                        values[2], values[3]));
                break;
        }
        // normalize time before return
        if (time == -1)
            return -1;
        else
            return round(TimeUtil.normalizeTime(time));
    }

    /**
     * conversion of predefined string to time in seconds.
     *
     * @param str string representation
     * @return time in seconds
     */
    private int parse(String str) {
    	return parse.parseLocalTime(str).getMillisOfDay() / 1000;
    }
}
