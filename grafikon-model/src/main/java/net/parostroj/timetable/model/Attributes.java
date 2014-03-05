package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.AttributesListener;

/**
 * Class for attributes (train or node).
 *
 * @author jub
 */
public class Attributes implements Map<String, Object> {

    private final Set<AttributesListener> listeners = new HashSet<AttributesListener>();
    private final Map<String, Object> values;
    private Map<String, Map<String, Object>> valuesWithCategory;

    /**
     * Default constructor.
     */
    public Attributes() {
        values = new LinkedHashMap<String, Object>();
    }

    /**
     * Copy constructor (shallow copy).
     *
     * @param attributes copied attributes
     */
    public Attributes(Attributes attributes) {
        this.values = new LinkedHashMap<String, Object>(attributes.values);
        // categories ...
        for (String category : attributes.getCategories()) {
            if (valuesWithCategory == null)
                valuesWithCategory = new HashMap<String, Map<String,Object>>();
            valuesWithCategory.put(category, new LinkedHashMap<String, Object>(attributes.getMapForCategory(category)));
        }
    }

    public void set(String name, Object value) {
        this.set(name, value, null);
    }

    public void set(String name, Object value, String category) {
        Map<String, Object> map = this.getMapForCategory(category);
        Object oldValue = map.get(name);
        if ((oldValue != null && value == null) || (value != null && !value.equals(oldValue))) {
            map.put(name, value);
            this.fireChange(name, oldValue, value, category);
        }
    }

    public boolean getBool(String name) {
        return this.getBool(name, null);
    }

    public boolean getBool(String name, String category) {
        Boolean value = this.get(name, category, Boolean.class);
        return Boolean.TRUE.equals(value);
    }

    public void setBool(String name, boolean value) {
        this.setBool(name, null, value);
    }

    public void setBool(String name, String category, boolean value) {
        if (value) {
            this.set(name, Boolean.TRUE, category);
        } else {
            this.remove(name, category);
        }
    }

    public Object get(String name) {
        return this.get(name, (String) null);
    }

    public <T> T get(String name, String category, Class<T> clazz) {
        return clazz.cast(this.get(name, category));
    }

    public <T> T get(String name, Class<T> clazz) {
        return clazz.cast(this.get(name));
    }

    public <T> T get(String name, String category, Class<T> clazz, T defaultValue) {
        T value = this.get(name, category, clazz);
        return value == null ? defaultValue : value;
    }

    public <T> T get(String name, Class<T> clazz, T defaultValue) {
        return this.get(name, null, clazz, defaultValue);
    }

    public Object get(String name, String category) {
        if (this.mapExistsForCategory(category))
            return this.getMapForCategory(category).get(name);
        else
            return null;
    }

    public Object remove(String name) {
        return this.remove(name, null);
    }

    public Object remove(String name, String category) {
        if (!this.mapExistsForCategory(category))
            return null;
        else {
            Map<String, Object> map = this.getMapForCategory(category);
            Object o = map.remove(name);
            if (o != null)
                this.fireChange(name, o, null, category);
            return o;
        }
    }

    @Override
    public void clear() {
        this.clear(null);
    }

    public void clear(String category) {
        if (this.mapExistsForCategory(category)) {
            Set<String> keys = new HashSet<String>(this.getMapForCategory(category).keySet());
            for (String key : keys) {
                this.remove(key, category);
            }
        }
    }

    public Set<String> getCategories() {
        if (valuesWithCategory == null)
            return Collections.emptySet();
        else
            return valuesWithCategory.keySet();
    }

    public Map<String, Object> getAttributesMap() {
        return this.getAttributesMap(null);
    }

    public Map<String, Object> getAttributesMap(String category) {
        if (this.mapExistsForCategory(category))
            return Collections.unmodifiableMap(this.getMapForCategory(category));
        else
            return Collections.emptyMap();
    }

    public void addListener(AttributesListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AttributesListener listener) {
        listeners.remove(listener);
    }

    protected void fireChange(String name, Object oldV, Object newV, String category) {
        AttributeChange change = new AttributeChange(name, oldV, newV, category);
        this.fireChange(change);
    }

    protected void fireChange(AttributeChange change) {
        for (AttributesListener l : listeners)
            l.attributeChanged(this, change);
    }

    private Map<String, Object> getMapForCategory(String category) {
        if (category == null)
            return values;
        if (valuesWithCategory == null)
            valuesWithCategory = new HashMap<String, Map<String,Object>>();
        if (!valuesWithCategory.containsKey(category))
            valuesWithCategory.put(category, new LinkedHashMap<String, Object>());
        return valuesWithCategory.get(category);
    }

    private boolean mapExistsForCategory(String category) {
        if (category == null)
            return values != null;
        else if (valuesWithCategory != null)
            return valuesWithCategory.containsKey(category);
        else
            return false;
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
                    || (fromMap.get(name) == null && toMap.get(name) != null)){
                this.set(name, fromMap.get(name), category);
            }
        }
        // remove deleted
        for (String name : new LinkedList<String>(toMap.keySet())) {
            if (!fromMap.containsKey(name)) {
                this.remove(name, category);
            }
        }
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
}
