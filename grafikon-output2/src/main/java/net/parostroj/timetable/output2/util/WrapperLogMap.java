package net.parostroj.timetable.output2.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

public class WrapperLogMap<K, V> implements Map<K, V> {

    private final Map<K, V> map;
    private final Logger log;
    private final String id;

    public WrapperLogMap(Map<K, V> map, Logger log, String id) {
        this.map = map;
        this.log = log;
        this.id = id;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        V value = map.get(key);
        if (value == null) {
            log.warn("{} - missing value for key: {}", id, key);
        }
        return value;
    }

    @Override
    public V put(K key, V value) {
        return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }

}
