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
    
    private Set<AttributesListener> listeners = new HashSet<AttributesListener>();
    private Map<String, Object> values;
    private List<String> names = new LinkedList<String>();

    /**
     * Default constructor.
     */
    public Attributes() {
        values = new HashMap<String, Object>();
    }
    
    /**
     * Copy constructor (shallow copy).
     *
     * @param attributes copied attributes
     */
    public Attributes(Attributes attributes) {
        this();
        for (String name : attributes.names) {
            this.set(name, attributes.get(name));
        }
    }

    public void set(String name, Object value) {
        if (!names.contains(name))
            names.add(name);
        Object oldValue = this.get(name);
        values.put(name, value);
        this.fireChange(name, oldValue, value);
    }

    public Object get(String name) {
        return values.get(name);
    }

    public Object remove(String name) {
        if (names.contains(name))
            names.remove(name);
        Object o = values.remove(name);
        if (o != null)
            this.fireChange(name, o, null);
        return o;
    }

    public int size() {
        return values.size();
    }

    public void clear() {
        List<String> namesCopy = new LinkedList<String>(names);
        for (String name : namesCopy) {
            this.remove(name);
        }
    }

    public List<String> names() {
        return Collections.unmodifiableList(names);
    }

    public void addListener(AttributesListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AttributesListener listener) {
        listeners.remove(listeners);
    }

    protected void fireChange(String name, Object oldV, Object newV) {
        AttributeChange change = new AttributeChange(name, oldV, newV);
        this.fireChange(change);
    }

    protected void fireChange(AttributeChange change) {
        for (AttributesListener l : listeners)
            l.attributeChanged(this, change);
    }

    // -------------- Map methods ----------------
    
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
