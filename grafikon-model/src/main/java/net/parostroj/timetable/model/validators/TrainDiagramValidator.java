package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.events.Event;

/**
 * Validator to keep the train diagram valid.
 *
 * @author jub
 */
public interface TrainDiagramValidator {

    boolean validate(Event event);
}
