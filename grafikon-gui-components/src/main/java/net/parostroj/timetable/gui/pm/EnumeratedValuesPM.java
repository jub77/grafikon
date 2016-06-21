package net.parostroj.timetable.gui.pm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.beanfabrics.model.Options;
import org.beanfabrics.model.TextPM;

import net.parostroj.timetable.gui.wrappers.WrapperConversion;

/**
 * Class representing options for e.g. combo box selection.
 *
 * @author jub
 *
 * @param <E> type of list of values
 */
public class EnumeratedValuesPM<E> extends TextPM implements IEnumeratedValuesPM<E> {

    public EnumeratedValuesPM() {
        this(new Options<>());
        this.setRestrictedToOptions(false);
    }

    public EnumeratedValuesPM(Options<E> options) {
        this.setOptions(options);
        this.setRestrictedToOptions(true);
        if (!options.isEmpty()) {
            this.setText(this.getOptionsImpl().getValue(0));
        }
    }

    public EnumeratedValuesPM(Map<E, String> valueMap) {
        if (valueMap.isEmpty()) {
            throw new IllegalArgumentException("At least one value has to be present");
        }
        this.setOptions(new Options<>());
        this.getOptionsImpl().putAll(valueMap);
        this.setRestrictedToOptions(true);
        this.setText(this.getOptionsImpl().getValue(0));
    }

    public EnumeratedValuesPM(Map<E, String> valueMap, String nullValueText) {
        this.setOptions(new Options<>());
        this.getOptionsImpl().put(null, nullValueText);
        this.getOptionsImpl().putAll(valueMap);
        this.setRestrictedToOptions(true);
        this.setText(this.getOptionsImpl().getValue(0));
    }

    @Override
    public void setValue(E value) {
        this.setText(this.getOptionsImpl().get(value));
    }

    @Override
    public E getValue() {
        Options<E> options = this.getOptionsImpl();
        return options.containsValue(this.getText()) ? options.getKey(this.getText()) : null;
    }

    @Override
    public void addValue(E value, String text) {
        this.getOptionsImpl().put(value, text);
    }

    @Override
    public boolean removeValue(E value) {
        Options<E> options = this.getOptionsImpl();
        boolean removed = false;
        if (options.containsKey(value)) {
            options.remove(value);
            removed = true;
        }
        return removed;
    }

    @Override
    public Collection<E> getValues() {
        return this.getOptionsImpl().keySet();
    }

    @SuppressWarnings("unchecked")
    private Options<E> getOptionsImpl() {
        return this.getOptions();
    }

    public static <V> Map<V, String> createValueMapWithNull(Collection<? extends V> values, WrapperConversion<V> conversion) {
        final Map<V, String> conversionMap = new LinkedHashMap<V, String>();
        conversionMap.put(null, conversion.toString(null));
        return createValueMapImpl(values, conversion, conversionMap);
    }

    public static <V> Map<V, String> createValueMap(Collection<? extends V> values, WrapperConversion<V> conversion) {
        final Map<V, String> conversionMap = new LinkedHashMap<V, String>();
        return createValueMapImpl(values, conversion, conversionMap);
    }

    private static <V> Map<V, String> createValueMapImpl(Collection<? extends V> values, WrapperConversion<V> conversion,
            final Map<V, String> conversionMap) {
        for (V value : values) {
            conversionMap.put(value, conversion.toString(value));
        }
        return conversionMap;
    }

    public static <V> Map<V, String> createValueMap(Collection<? extends V> values, Collection<String> textValues) {
        Iterator<String> si = textValues.iterator();
        Iterator<? extends V> vi = values.iterator();
        final Map<V, String> conversionMap = new LinkedHashMap<V, String>();
        while (vi.hasNext() && si.hasNext()) {
            conversionMap.put(vi.next(), si.next());
        }
        if (vi.hasNext() || si.hasNext()) {
            throw new IllegalArgumentException("Uneven number of arguments in collections");
        }
        return conversionMap;
    }

    public static class Builder<T> {
        private Map<T, String> map;
        private String nullValue;
        private WrapperConversion<T> conversion;

        private Builder() {
            map = new HashMap<>();
        }

        public Builder<T> add(T value, String text) {
            map.put(value, text);
            return this;
        }

        public Builder<T> add(T value) {
            if (conversion == null) throw new IllegalStateException("Conversion missing");
            map.put(value, conversion.toString(value));
            return this;
        }

        public Builder<T> setConversion(WrapperConversion<T> conversion) {
            this.conversion = conversion;
            return this;
        }

        public Builder<T> setNullValue(String nullValue) {
            this.nullValue = nullValue;
            return this;
        }

        public EnumeratedValuesPM<T> build() {
            if (nullValue == null) {
                return new EnumeratedValuesPM<>(map);
            } else {
                return new EnumeratedValuesPM<>(map, nullValue);
            }
        }
    }

    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }
}
