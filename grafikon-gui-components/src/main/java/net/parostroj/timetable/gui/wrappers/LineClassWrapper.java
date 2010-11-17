package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.LineClass;

/**
 * Line class wrapper.
 *
 * @author jub
 */
public class LineClassWrapper extends Wrapper<LineClass> {

    public LineClassWrapper(LineClass lineClass) {
        super(lineClass);
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

    public static String toString(LineClass lineClass) {
        return lineClass.getName();
    }
}
