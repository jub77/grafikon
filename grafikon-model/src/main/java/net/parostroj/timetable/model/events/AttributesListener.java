package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.Attributes;

/**
 * Listener for attributes.
 * 
 * @author jub
 */
public interface AttributesListener {

    public void attributeChanged(Attributes attributes, AttributeChange change);
}
