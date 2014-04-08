package net.parostroj.timetable.gui.components;

import java.util.HashMap;
import java.util.Map;

import net.parostroj.timetable.filters.Filter;

public class GTStorage {

    private final Map<Class<?>, RegionCollector<?>> collectors = new HashMap<Class<?>, RegionCollector<?>>();
    private final Map<Class<?>, Filter<?>> filters = new HashMap<Class<?>, Filter<?>>();

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

    public <T> void setFilter(Class<?> clazz, Filter<T> filter) {
        filters.put(clazz, filter);
    }

    @SuppressWarnings("unchecked")
    public <T> Filter<T> getFilter(Class<T> clazz) {
        return (Filter<T>) filters.get(clazz);
    }

    public <T> void removeFilter(Class<T> clazz) {
        filters.remove(clazz);
    }

    public Iterable<Filter<?>> filters() {
        return filters.values();
    }
}
