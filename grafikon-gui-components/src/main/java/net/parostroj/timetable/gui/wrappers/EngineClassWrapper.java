package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.EngineClass;

/**
 * Engine class wrapper.
 *
 * @author jub
 */
public class EngineClassWrapper extends Wrapper<EngineClass> {

    public EngineClassWrapper(EngineClass engineClass) {
        super(engineClass);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return toString(getElement());
    }

    public static String toString(EngineClass engineClass) {
        return engineClass.getName();
    }
}
