package net.parostroj.timetable.model.freight;

/**
 * Generic connection interface.
 *
 * @author jub
 */
public interface Connection<F, T> {
    F getFrom();
    T getTo();
}
