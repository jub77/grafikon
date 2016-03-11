package net.parostroj.timetable.model;

/**
 * Generic connection interface.
 *
 * @author jub
 */
public interface Connection<T> {
    T getFrom();
    T getTo();
}
