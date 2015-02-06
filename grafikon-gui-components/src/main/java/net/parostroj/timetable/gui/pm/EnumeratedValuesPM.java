package net.parostroj.timetable.gui.pm;

import java.util.*;

import net.parostroj.timetable.gui.wrappers.WrapperConversion;

import org.beanfabrics.model.Options;
import org.beanfabrics.model.TextPM;

public class EnumeratedValuesPM<E> extends TextPM implements IEnumeratedValuesPM<E> {

    private final Options<E> options;
    private final WrapperConversion<? super E> conversion;

    public EnumeratedValuesPM(Collection<? extends E> values, WrapperConversion<? super E> conversion) {
        this.conversion = conversion;
        this.options = new Options<E>();
        for (E value : values) {
            this.options.put(value, conversion.toString(value));
        }
        this.setOptions(this.options);
        this.setRestrictedToOptions(true);
        this.setText(this.options.getValue(0));
    }

    @Override
    public WrapperConversion<? super E> getConversion() {
        return conversion;
    }

    @Override
    public void setValue(E value) {
        this.setText(conversion.toString(value));
    }

    @Override
    public E getValue() {
        return this.options.getKey(this.getText());
    }

    public static <V> WrapperConversion<V> createConversion(Collection<? extends V> values, Collection<String> textValues) {
        Iterator<String> si = textValues.iterator();
        Iterator<? extends V> vi = values.iterator();
        final Map<V, String> conversionMap = new HashMap<V, String>();
        while (vi.hasNext() && si.hasNext()) {
            conversionMap.put(vi.next(), si.next());
        }
        if (vi.hasNext() || si.hasNext()) {
            throw new IllegalArgumentException("Uneven number of arguments in collections");
        }
        return (V i) -> conversionMap.get(i);
    }

    public static <V> WrapperConversion<V> createConversion(Map<? extends V, String> map) {
        return (V i) -> map.get(i);
    }
}
