package net.parostroj.timetable.gui.wrappers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.parostroj.timetable.model.OutputTemplate;

/**
 * Delegate for output template.
 *
 * @author jub
 */
public class OutputTemplateWrapperDelegate extends BasicWrapperDelegate<OutputTemplate> {

    public static final Map<String, String> OUTPUT_MAPPING;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("pdf.groovy", "pdf");
        map.put("draw", "svg");
        map.put("groovy", "text");
        map.put("xml", "xml");
        OUTPUT_MAPPING = Collections.unmodifiableMap(map);
    }

    private boolean addType;

    public OutputTemplateWrapperDelegate() {
        this(true);
    }

    public OutputTemplateWrapperDelegate(boolean addType) {
        this.addType = addType;
    }

    private String getTypeInfo(OutputTemplate template) {
        if (template == null) {
            return "";
        }
        String output = template.getOutput();
        String mapped = OUTPUT_MAPPING.get(output);
        output = mapped != null ? mapped : output;
        return output;
    }

    @Override
    protected String toCompareString(OutputTemplate element) {
        return toString(element);
    }

    @Override
    public String toString(OutputTemplate element) {
        String text = element.getKey();
        if (element.getName() != null) {
            text = element.getName().translate();
        }
        return addType ?
                String.format("%2$s: %1$s", text, getTypeInfo(element)) :
                text;
    }
}
