package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.AttributesChecker;
import net.parostroj.timetable.model.events.AttributesListener;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Class for attributes (train or node).
 *
 * @author jub
 */
public class Attributes implements Map<String, Object> {

    private static final AttributesChecker EMPTY = (attrs, change) -> true;

    private final List<AttributesListener> listeners = new LinkedList<>();
    private final Map<String, Object> values;
    private Map<String, Map<String, Object>> valuesWithCategory;
    private final AttributesChecker checker;

    private boolean skipListeners = false;

    /**
     * Default constructor.
     */
    public Attributes() {
        this(null, EMPTY);
    }

    /**
     * Creates attributes with initial listener.
     *
     * @param listener listener
     */
    public Attributes(AttributesListener listener) {
        this(listener, EMPTY);
    }

    /**
     * Creates attributes with initial listener and checker.
     *
     * @param listener listener
     * @param checker checker
     */
    public Attributes(AttributesListener listener, AttributesChecker checker) {
        this.values = new LinkedHashMap<>();
        if (listener != null) {
            this.addListener(listener);
        }
        this.checker = checker;
    }

    /**
     * Copy constructor (shallow copy).
     *
     * @param attributes copied attributes
     */
    public Attributes(Attributes attributes) {
        this.values = new LinkedHashMap<>(attributes.values);
        // categories ...
        for (String category : attributes.getCategories()) {
            if (valuesWithCategory == null) {
                valuesWithCategory = new HashMap<>();
            }
            valuesWithCategory.put(category, new LinkedHashMap<>(attributes.getMapForCategory(category)));
        }
        this.checker = EMPTY;
    }

    public void setRemove(String name, Object value) {
        this.setRemove(null, name, value);
    }

    public void setRemove(String category, String name, Object value) {
        if (value == null) {
            this.remove(category, name);
        } else {
            this.set(category, name, value);
        }
    }

    public void set(String name, Object value) {
        this.set(null, name, value);
    }

    public void set(String category, String name, Object value) {
        Map<String, Object> map = this.getMapForCategory(category);
        Object oldValue = map.get(name);
        if (!ObjectsUtil.compareWithNull(oldValue, value)) {
            AttributeChange change = new AttributeChange(name, oldValue, value, category);
            if (checker.check(this, change)) {
                map.put(name, value);
                this.fireChange(change);
            } else {
                throw new GrafikonException("Cannot change attribute " + name + " to " + value,
                        GrafikonException.Type.ATTRIBUTE);
            }
        }
    }

    public boolean getBool(String name) {
        return this.getBool(null, name);
    }

    public boolean getBool(String category, String name) {
        Boolean value = this.get(category, name, Boolean.class);
        return Boolean.TRUE.equals(value);
    }

    public void setBool(String name, boolean value) {
        this.setBool(null, name, value);
    }

    public void setBool(String category, String name, boolean value) {
        if (value) {
            this.set(category, name, Boolean.TRUE);
        } else {
            this.remove(category, name);
        }
    }

    public Object get(String name) {
        return this.get((String) null, name);
    }

    public <T> T get(String category, String name, Class<T> clazz) {
        return clazz.cast(this.get(category, name));
    }

    public <T> T get(String name, Class<T> clazz) {
        return clazz.cast(this.get(name));
    }

    public <T> T get(String category, String name, Class<T> clazz, T defaultValue) {
        T value = this.get(category, name, clazz);
        return value == null ? defaultValue : value;
    }

    public <T> T get(String name, Class<T> clazz, T defaultValue) {
        return this.get(null, name, clazz, defaultValue);
    }

    public <T> Collection<T> getAsCollection(String name, Class<T> clazz) {
        return this.getAsCollection(name, clazz, null);
    }

    public <T> Collection<T> getAsCollection(String name, Class<T> clazz, Collection<T> defaultValue) {
        Object object = this.get(null, name, Object.class, defaultValue);
        if (object != null && !(object instanceof Collection)) {
            throw new ClassCastException("Wrong type: " + object.getClass());
        }
        return object instanceof List
                ? ObjectsUtil.checkedList((List<?>) object, clazz)
                : (object instanceof Set ? ObjectsUtil.checkedSet((Set<?>) object, clazz) : ObjectsUtil.checkedCollection((Collection<?>) object, clazz));
    }

    public <T> Set<T> getAsSet(String name, Class<T> clazz) {
        return this.getAsSet(name, clazz, null);
    }

    public <T> Set<T> getAsSet(String name, Class<T> clazz, Set<T> defaultValue) {
        Object object = this.get(null, name, Object.class, defaultValue);
        if (object != null && !(object instanceof Set)) {
            throw new ClassCastException("Wrong type: " + object.getClass());
        }
        return ObjectsUtil.checkedSet((Set<?>) object, clazz);
    }

    public <T> List<T> getAsList(String name, Class<T> clazz) {
        return this.getAsList(name, clazz, null);
    }

    public <T> List<T> getAsList(String name, Class<T> clazz, List<T> defaultValue) {
        Object object = this.get(null, name, Object.class, defaultValue);
        if (object != null && !(object instanceof List)) {
            throw new ClassCastException("Wrong type: " + object.getClass());
        }
        return ObjectsUtil.checkedList((List<?>) object, clazz);
    }

    public <K, V> Map<K, V> getAsMap(String name, Class<K> keyClazz, Class<V> valueClazz) {
        return this.getAsMap(name, keyClazz, valueClazz, null);
    }

    public <K, V> Map<K, V> getAsMap(String name, Class<K> keyClazz, Class<V> valueClazz, Map<K, V> defaultValue) {
        Object object = this.get(null, name, Object.class, defaultValue);
        if (object != null && !(object instanceof Map)) {
            throw new ClassCastException("Wrong type: " + object.getClass());
        }
        return ObjectsUtil.checkedMap((Map<?, ?>) object, keyClazz, valueClazz);
    }

    public Object get(String category, String name) {
        if (this.mapExistsForCategory(category)) {
            return this.getMapForCategory(category).get(name);
        } else {
            return null;
        }
    }

    public Object remove(String name) {
        return this.remove(null, name);
    }

    public Object remove(String category, String name) {
        if (!this.mapExistsForCategory(category)) {
            return null;
        } else {
            Map<String, Object> map = this.getMapForCategory(category);
            Object o = map.get(name);
            if (o != null) {
                AttributeChange change = new AttributeChange(name, o, null, category);
                if (checker.check(this, change)) {
                    map.remove(name);
                    this.fireChange(change);
                } else {
                    throw new GrafikonException("Cannot remove attribute " + name,
                            GrafikonException.Type.ATTRIBUTE);
                }
            }
            return o;
        }
    }

    @Override
    public void clear() {
        this.clear(null);
    }

    public void clear(String category) {
        if (this.mapExistsForCategory(category)) {
            Set<String> keys = new HashSet<>(this.getMapForCategory(category).keySet());
            for (String key : keys) {
                this.remove(category, key);
            }
        }
    }

    public Set<String> getCategories() {
        if (valuesWithCategory == null) {
            return Collections.emptySet();
        } else {
            return valuesWithCategory.keySet();
        }
    }

    public Map<String, Object> getAttributesMap() {
        return this.getAttributesMap(null);
    }

    public Map<String, Object> getAttributesMap(String category) {
        if (this.mapExistsForCategory(category)) {
            return Collections.unmodifiableMap(this.getMapForCategory(category));
        } else {
            return Collections.emptyMap();
        }
    }

    public void addListener(AttributesListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(AttributesListener listener) {
        listeners.remove(listener);
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    protected void fireChange(AttributeChange change) {
        if (!skipListeners) {
            for (AttributesListener l : listeners) {
                l.attributeChanged(this, change);
            }
        }
    }

    private Map<String, Object> getMapForCategory(String category) {
        if (category == null) {
            return values;
        }
        if (valuesWithCategory == null) {
            valuesWithCategory = new HashMap<>();
        }
        if (!valuesWithCategory.containsKey(category)) {
            valuesWithCategory.put(category, new LinkedHashMap<String, Object>());
        }
        return valuesWithCategory.get(category);
    }

    private boolean mapExistsForCategory(String category) {
        if (category == null) {
            return values != null;
        } else if (valuesWithCategory != null) {
            return valuesWithCategory.containsKey(category);
        } else {
            return false;
        }
    }

    public void merge(Attributes from) {
        // add/remove ...
        for (String category : from.getCategories()) {
            this.merge(from, category);
        }
        // remove ...
        for (String category : this.getCategories()) {
            if (!from.getCategories().contains(category)) {
                this.merge(from, category);
            }
        }
        // default category
        this.merge(from, null);
    }

    public void merge(Attributes from, String category) {
        Map<String, Object> fromMap = from.getAttributesMap(category);
        Map<String, Object> toMap = this.getAttributesMap(category);
        // update modified ...
        for (String name : fromMap.keySet()) {
            if ((fromMap.get(name) != null && !fromMap.get(name).equals(toMap.get(name)))
                    || (fromMap.get(name) == null && toMap.get(name) != null)) {
                this.set(category, name, fromMap.get(name));
            }
        }
        // remove deleted
        for (String name : new LinkedList<>(toMap.keySet())) {
            if (!fromMap.containsKey(name)) {
                this.remove(category, name);
            }
        }
    }

    public void add(Attributes from) {
        for (String category : from.getCategories()) {
            this.add(from, category);
        }
        this.add(from, null);
    }

    public void add(Attributes from, String category) {
        Map<String, Object> fromMap = from.getAttributesMap(category);
        for (String name : fromMap.keySet()) {
            this.set(category, name, fromMap.get(name));
        }
    }

    public void addAttributesMap(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            this.set(entry.getKey(), entry.getValue());
        }
    }

    public void addAttributesMap(Map<String, Object> map, String category) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            this.set(category, entry.getKey(), entry.getValue());
        }
    }

    public void setSkipListeners(boolean skipListeners) {
        this.skipListeners = skipListeners;
    }

    public boolean isSkipListeners() {
        return skipListeners;
    }

    // ------------ Map methods ------------
    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return values.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return values.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return values.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        return values.keySet();
    }

    @Override
    public Collection<Object> values() {
        return values.values();
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return values.entrySet();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (!values.isEmpty()) {
            printAttributes("default", values, result);
        }
        if (valuesWithCategory != null) {
            for (Map.Entry<String, Map<String, Object>> categoryEntry : valuesWithCategory.entrySet()) {
                if (!categoryEntry.getValue().isEmpty()) {
                    printAttributes(categoryEntry.getKey(), categoryEntry.getValue(), result);
                }
            }
        }
        return result.toString();
    }

    private void printAttributes(String category, Map<String, Object> attrs, StringBuilder builder) {
        if (builder.length() != 0) {
            builder.append('\n');
        }
        builder.append(category).append(':');
        for (Map.Entry<String, Object> entry : attrs.entrySet()) {
            builder.append("\n  ").append(entry.getKey()).append('=').append(entry.getValue());
        }
    }
}
