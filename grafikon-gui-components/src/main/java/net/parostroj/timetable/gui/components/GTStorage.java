package net.parostroj.timetable.gui.components;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Predicate;

public class GTStorage {

    private final Map<Class<?>, RegionCollector<?>> collectors = new HashMap<Class<?>, RegionCollector<?>>();
    private final Map<Class<?>, Predicate<?>> filters = new HashMap<Class<?>, Predicate<?>>();

    public <T> void setCollector(Class<T> clazz, RegionCollector<T> collector) {
        collectors.put(clazz, collector);
    }

    @SuppressWarnings("unchecked")
    public <T> RegionCollector<T> getCollector(Class<T> clazz) {
        return (RegionCollector<T>) collectors.get(clazz);
    }

    public <T> void removeCollector(Class<T> clazz) {
        collectors.remove(clazz);
    }

    public void removeCollectors() {
        collectors.clear();
    }

    public Iterable<RegionCollector<?>> collectors() {
        return collectors.values();
    }

    public <T> void setFilter(Class<?> clazz, Predicate<T> filter) {
        filters.put(clazz, filter);
    }

    @SuppressWarnings("unchecked")
    public <T> Predicate<T> getFilter(Class<T> clazz) {
        return (Predicate<T>) filters.get(clazz);
    }

    public <T> void removeFilter(Class<T> clazz) {
        filters.remove(clazz);
    }

    public Iterable<Predicate<?>> filters() {
        return filters.values();
    }
}
