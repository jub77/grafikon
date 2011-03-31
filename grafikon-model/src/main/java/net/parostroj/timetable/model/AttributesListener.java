package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.AttributeChange;

/**
 * Listener for attributes.
 * 
 * @author jub
 */
public interface AttributesListener {

    public void attributeChanged(Attributes attributes, AttributeChange change);
}
