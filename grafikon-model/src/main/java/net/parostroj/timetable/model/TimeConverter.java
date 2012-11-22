package net.parostroj.timetable.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import net.parostroj.timetable.utils.TimeUtil;

/**
 * Utility class for converting time from text to int and backwards.
 *
 * @author jub
 */
public class TimeConverter {

	public static enum Rounding {
		MINUTE("minute"), HALF_MINUTE("half.minute");

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

	private final DecimalFormat minFormat;
	private final DecimalFormat minFormat2;
	private final Rounding rounding;

	/**
	 * creates instance with specific rounding.
	 *
	 * @param rounding rounding
	 */
	public TimeConverter(Rounding rounding) {
		this.rounding = rounding;
		this.minFormat = new DecimalFormat(rounding == Rounding.HALF_MINUTE ? "#.#" : "#");
		this.minFormat2 = new DecimalFormat(rounding == Rounding.HALF_MINUTE ? "00.#" : "00");
		DecimalFormatSymbols symbols = new DecimalFormat().getDecimalFormatSymbols();
		symbols.setDecimalSeparator(',');
		this.minFormat.setDecimalFormatSymbols(symbols);
		this.minFormat2.setDecimalFormatSymbols(symbols);
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
    private int round(int time) {
    	if (rounding == Rounding.MINUTE) {
    		return (time + 30) / 60 * 60;
    	} else {
    		return (time + 15) / 30 * 30;
    	}
    }

    /**
     * converts from seconds to textual representation.
     *
     * @param time time in seconds
     * @return textual representation
     */
    public String convertIntToText(int time) {
    	return formatIntToText(time, "%d:%s");
    }

    /**
     * converts from seconds to textual representation (not normalized).
     *
     * @param time time in seconds
     * @return textual representation
     */
    public String convertIntToTextNN(int time) {
        int hours = getHoursNN(time);
        String minutes = minFormat2.format(getMinutes(time));
        return String.format("%1$d:%2$s", hours, minutes);
    }

    /**
     * converts from seconds to textual representation.
     *
     * @param time time in seconds
     * @param format format
     * @return textual representation
     */
    public String formatIntToText(int time, String format) {
        time = TimeUtil.normalizeTime(time);
        int hours = getHours(time);
        String minutes = minFormat2.format(getMinutes(time));
        return String.format(format, hours, minutes);
    }

    /**
     * returns minutes.
     *
     * @param time time in seconds
     * @return minutes
     */
    public double getMinutes(int time) {
        time = round(time);
        return ((double) (time % 3600)) / 60;
    }

    /**
     * @param time time in seconds
     * @return true if after rounding the result is half minute
     */
    public boolean isHalfMinute(int time) {
    	if (rounding == Rounding.MINUTE)
    		return false;
    	else {
    		time = round(time);
    		return ((time % 3600) / 30) % 2 == 1;
    	}
    }

    /**
     * returns hours.
     *
     * @param time time in seconds
     * @return hours
     */
    public int getHours(int time) {
        time = round(time);
        time = time / 3600;
        if (time == 24)
            time = 0;
        return time;
    }

    /**
     * returns hours (not normalized).
     *
     * @param time time in seconds
     * @return hours
     */
    public int getHoursNN(int time) {
        time = round(time);
        time = time / 3600;
        return time;
    }

    /**
     * returns the last digit from minutes (needed by GT).
     *
     * @param time time in seconds
     * @return last digit of minutes
     */
    public String getLastDigitOfMinutes(int time) {
    	TimeUtil.normalizeTime(time);
    	time = round(time);
        return Integer.toString((time / 60) % 10);
    }

    /**
     * converts text representation of minutes to int.
     *
     * @param text text
     * @return time in seconds
     * @throws ParseException
     */
    public int convertMinutesTextToInt(String text) throws ParseException {
    	Number number = minFormat.parse(text);
    	return round((int) (number.doubleValue() * 60));
    }

    /**
     * converts time to string with minutes
     *
     * @param time time in seconds
     * @return string with minutes
     */
    public String convertIntToMinutesText(int time) {
    	return minFormat.format(getMinutes(time));
    }

    /**
     * converts from textual representation to seconds. It returns <code>-1</code>
     * if there was problem with converting the value.
     *
     * @param text textual representation
     * @return time in seconds from midnight
     */
    public int convertTextToInt(String text) {
        // remove spaces
        text = text.trim();

        int size = 0;
        int[] values = new int[4];
        double decimal = 0;
        int ratio = 10;
        boolean hm = true;

        // get digits
        for (char ch : text.toCharArray()) {
        	if (ch == ',')
        		hm = false;
        	if (Character.isDigit(ch)) {
        		int digit = Character.digit(ch, 10);
	            if (hm && size < 4) {
	                values[size++] = digit;
	            } else if (!hm) {
	            	decimal = decimal + ((double) digit) / ratio;
	            	ratio = ratio * 10;
	            }
        	}
        }

        int time = -1;
        // convert to time
        switch (size) {
            case 1:
                time = values[0] * 3600;
                break;
            case 2:
                time = (values[0] * 10 + values[1]) * 3600;
                break;
            case 3:
                time = (values[0] * 3600) + (values[1] * 10 + values[2]) * 60;
                break;
            case 4:
                time = (values[0] * 10 + values[1]) * 3600 + (values[2] * 10 + values[3]) * 60;
                break;
        }
        if (time != -1 && ratio != 10) {
        	time += 60 * decimal;
        	time = round(time);
        }
        // normalize time before return
        if (time == -1)
            return -1;
        else
            return TimeUtil.normalizeTime(time);
    }
}
