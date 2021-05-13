package net.parostroj.timetable.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Scale {

    private static final List<Scale> predefined;

    static {
        predefined = Collections.unmodifiableList(Arrays.asList(new Scale("H0", 87), new Scale("TT", 120), new Scale("N", 160), new Scale("1:1", 1)));
    }
    private final int ratio;
    private final String name;

    private Scale(String name, int ratio) {
        this.ratio = ratio;
        this.name = name;
    }

    public int getRatio() {
        return ratio;
    }

    public static List<Scale> getPredefined() {
        return predefined;
    }

    public String getName() {
        return name;
    }

    public static Scale getFromPredefined(String name) {
        int ratio = convertStringToRatio(name);
        for (Scale scale : predefined) {
            if (scale.getName().equals(name))
                return scale;
            if (scale.getRatio() == ratio)
                return scale;
        }
        return null;
    }

    public static String convertRatioToString(int ratio) {
        return "1:" + ratio;
    }

    public static int convertStringToRatio(String ratioString) {
        try {
            int index = ratioString.indexOf(':');
            if (index == -1)
                return -1;
            return Integer.parseInt(ratioString.substring(index));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static Scale fromString(String scaleString) {
        Scale scale = getFromPredefined(scaleString);
        if (scale == null) {
            int ratio = convertStringToRatio(scaleString);
            scale = new Scale(scaleString, ratio);
        }
        return scale;
    }

    public static Scale fromRatio(int ratio) {
        String ratioString = convertRatioToString(ratio);
        return fromString(ratioString);
    }

    @Override
    public String toString() {
        return name;
    }
}
