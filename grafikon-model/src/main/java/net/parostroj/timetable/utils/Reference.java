package net.parostroj.timetable.utils;

/**
 * Reference.
 *
 * @author jub
 */
public interface Reference<T> {

	T get();

	void set(T object);
}
