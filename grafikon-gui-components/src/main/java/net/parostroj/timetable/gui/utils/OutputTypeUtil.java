package net.parostroj.timetable.gui.utils;

import java.util.Map;

import net.parostroj.timetable.model.OutputTemplate;

public final class OutputTypeUtil {

    private static final Map<String, String> OUTPUT_MAPPING;

    static {
        OUTPUT_MAPPING = Map.of(
                "pdf.groovy", "pdf",
                "draw", "svg",
                "groovy", "text",
                "xml", "xml");
    }

    private OutputTypeUtil() {}

    public static String convertOutputType(OutputTemplate template) {
        String output = template.getOutput();
        String mapped = OUTPUT_MAPPING.get(output);
        return mapped == null ? output : mapped;
    }
}
