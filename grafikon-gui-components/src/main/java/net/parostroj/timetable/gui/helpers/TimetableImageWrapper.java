package net.parostroj.timetable.gui.helpers;

import net.parostroj.timetable.model.TimetableImage;

/**
 * Wrapper for timetable image.
 *
 * @author jub
 */
public class TimetableImageWrapper extends Wrapper<TimetableImage> {

    public TimetableImageWrapper(TimetableImage element) {
        super(element);
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
    public int compareTo(Wrapper<TimetableImage> o) {
        return getElement().getFilename().compareTo(o.getElement().getFilename());
    }

    @Override
    public String toString() {
        return getElement().toString();
    }
}
