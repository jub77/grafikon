package net.parostroj.timetable.gui.pm;

import java.util.Collection;
import java.util.Map;

import org.beanfabrics.model.ITextPM;

public interface IEnumeratedValuesPM<E> extends ITextPM {

    void setValue(E value);

    E getValue();

    void addValue(E value, String text);

    boolean removeValue(E value);

    void addValues(Map<E, String> valueMap);

    void removeAllValues();

    Collection<E> getValues();
}
