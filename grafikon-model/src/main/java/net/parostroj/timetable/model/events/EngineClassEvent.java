package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Engine class event (mostly changes to weight table).
 *
 * @author jub
 */
public class EngineClassEvent extends GTEvent<EngineClass> {

    public EngineClassEvent(EngineClass source, GTEventType type) {
        super(source, type);
    }

    @Override
    public void accept(EventVisitor visitor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
