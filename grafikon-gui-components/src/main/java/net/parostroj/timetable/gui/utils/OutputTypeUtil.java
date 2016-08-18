package net.parostroj.timetable.gui.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.parostroj.timetable.model.OutputTemplate;

public class OutputTypeUtil {

    private static final Map<String, String> OUTPUT_MAPPING;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("pdf.groovy", "pdf");
        map.put("draw", "svg");
        map.put("groovy", "text");
        map.put("xml", "xml");
        OUTPUT_MAPPING = Collections.unmodifiableMap(map);
    }

    public static String convertOutputType(OutputTemplate template) {
        String output = template.getOutput();
        String mapped = OUTPUT_MAPPING.get(output);
        return mapped == null ? output : mapped;
    }
}
