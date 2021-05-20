package net.parostroj.timetable.output2.gt;

import java.util.*;
import java.util.function.Predicate;

public class GTStorage {

    private final Map<Class<?>, RegionCollector<?>> collectors = new LinkedHashMap<>();
    private final Map<Class<?>, Predicate<?>> filters = new HashMap<>();
    private final Map<String, Object> parameters = new HashMap<>();

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

    public Collection<Class<?>> getCollectorClasses() {
        return collectors.keySet();
    }

    public <T> void setFilter(Class<T> clazz, Predicate<T> filter) {
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

    public Collection<Class<?>> getFilterClasses() {
        return filters.keySet();
    }

    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    public <T> T getParameter(String key, Class<T> clazz) {
        return clazz.cast(this.parameters.get(key));
    }

    public void removeParameter(String key) {
        this.parameters.remove(key);
    }
}
