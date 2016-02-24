package net.parostroj.timetable.gui.components;

/**
 * Selection listener.
 *
 * @author jub
 */
@FunctionalInterface
public interface ElementSelectionListener<T> {
    void selection(T element, boolean selected);
}
