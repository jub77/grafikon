package net.parostroj.timetable.model;

import java.util.LinkedHashMap;

/**
 * Class for attributes (train or node).
 *
 * @author jub
 */
public class Attributes extends LinkedHashMap<String, Object> {

    /**
     * Default constructor.
     */
    public Attributes() {
    }

    /**
     * Copy constructor (shallow copy).
     *
     * @param attributes copied attributes
     */
    public Attributes(Attributes attributes) {
        super(attributes);
    }
}
