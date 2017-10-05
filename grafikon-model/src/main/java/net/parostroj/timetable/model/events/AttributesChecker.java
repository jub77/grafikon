package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.Attributes;

/**
 * Checks if the attribute change is allowed.
 *
 * @author jub
 */
@FunctionalInterface
public interface AttributesChecker {

    /**
     * Checks if the attribute change is valid.
     *
     * @param attributes attributes
     * @param change change
     * @return if the change is allowed or not
     */
    boolean check(Attributes attributes, AttributeChange change);
}
