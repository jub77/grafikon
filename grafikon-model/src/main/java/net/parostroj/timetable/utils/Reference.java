package net.parostroj.timetable.utils;

import java.util.function.Supplier;

/**
 * Reference.
 *
 * @author jub
 */
public interface Reference<T> extends Supplier<T> {

	void set(T object);
}
