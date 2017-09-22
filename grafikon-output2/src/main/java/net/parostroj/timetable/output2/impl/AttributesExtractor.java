package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.parostroj.timetable.model.Attributes;

/**
 * Extraction of user attributes.
 *
 * @author jub
 */
public class AttributesExtractor {

    private static final String ATTR_CATEGORY = "user";

    public List<Attribute> extract(Attributes attributes) {
        List<Attribute> result = new LinkedList<>();
        for (Map.Entry<String, Object> entry : attributes.getAttributesMap(ATTR_CATEGORY).entrySet()) {
            result.add(new Attribute(entry.getKey(), (String)entry.getValue()));
        }
        return result;
    }
}
