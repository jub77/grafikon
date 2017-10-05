package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.Attributes;

/**
 * Listener for attributes.
 *
 * @author jub
 */
@FunctionalInterface
public interface AttributesListener {

    /**
     * Callback when some attribute changes.
     *
     * @param attributes attributes
     * @param change change
     */
    void attributeChanged(Attributes attributes, AttributeChange change);
}
