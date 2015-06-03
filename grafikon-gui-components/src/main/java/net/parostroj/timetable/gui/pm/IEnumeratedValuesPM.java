package net.parostroj.timetable.gui.pm;

import java.util.Collection;

import org.beanfabrics.model.ITextPM;

public interface IEnumeratedValuesPM<E> extends ITextPM {

    void setValue(E value);

    E getValue();

    void addValue(E value, String text);

    boolean removeValue(E value);

    Collection<E> getValues();
}
