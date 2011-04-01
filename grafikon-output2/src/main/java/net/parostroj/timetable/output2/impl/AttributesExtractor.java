package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;

import net.parostroj.timetable.model.Attributes;

/**
 * Extraction of user attributes.
 * 
 * @author jub
 */
public class AttributesExtractor {
    
    public static final String USER_PREFIX = "user."; 

    public List<Attribute> extract(Attributes attributes) {
        List<Attribute> result = new LinkedList<Attribute>();
        for (String name : attributes.names()) {
            if (name.startsWith(USER_PREFIX))
                result.add(new Attribute(name.replace(USER_PREFIX, ""), (String)attributes.get(name)));
        }
        return result;
    }
}
