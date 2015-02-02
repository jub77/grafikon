package net.parostroj.timetable.gui.pm;

import java.util.Arrays;
import java.util.Collection;

import net.parostroj.timetable.gui.wrappers.WrapperConversion;

import org.beanfabrics.model.Options;
import org.beanfabrics.model.TextPM;

public class EnumeratedValuesPM<E> extends TextPM {

    private final Options<E> options;
    private final WrapperConversion<? super E> conversion;

    public EnumeratedValuesPM(E[] values, WrapperConversion<? super E> conversion) {
        this(Arrays.asList(values), conversion);
    }

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

    public void setValue(E value) {
        this.setText(conversion.toString(value));
    }

    public E getValue() {
        return this.options.getKey(this.getText());
    }
}
