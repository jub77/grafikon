package net.parostroj.timetable.gui.pm;

import java.util.Collection;

import net.parostroj.timetable.gui.wrappers.WrapperConversion;

import org.beanfabrics.model.ITextPM;

public interface IEnumeratedValuesPM<E> extends ITextPM {

    void setValue(E value);

    E getValue();

    WrapperConversion<? super E> getConversion();

    Collection<E> getValues();
}