package net.parostroj.timetable.utils;

/**
 * Cached value.
 *
 * @author jub
 */
public class CachedValue<T> {

    private boolean cached;
    private T value;

    public CachedValue() {
    }

    public CachedValue(boolean cached, T value) {
        this.cached = cached;
        this.value = value;
    }

    public void clear() {
        cached = false;
    }

    public boolean set(T value) {
        boolean different = !StringUtil.compareWithNull(value, this.value) && cached;
        this.value = value;
        this.cached = true;
        return different;
    }

    public T getValue() {
        return value;
    }

    public boolean isCached() {
        return cached;
    }
}
