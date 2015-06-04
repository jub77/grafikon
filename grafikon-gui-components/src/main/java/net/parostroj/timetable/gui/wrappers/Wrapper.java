package net.parostroj.timetable.gui.wrappers;

import java.util.*;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.imports.ImportComponent;
import net.parostroj.timetable.model.imports.ImportMatch;

/**
 * Wrapper class for lists in GUI.
 *
 * @author jub
 */
public class Wrapper<T> implements Comparable<Wrapper<T>> {

    private T wrappedElement;
    private final WrapperDelegate<? super T> delegate;

    public Wrapper(T element) {
        this.wrappedElement = element;
        this.delegate = new ElementWrapperDelegate();
    }

    public Wrapper(T element, WrapperDelegate<? super T> delegate) {
        this.wrappedElement = element;
        this.delegate = delegate;
    }

    public void setElement(T elemenet) {
        this.wrappedElement = elemenet;
    }

    public T getElement() {
        return this.wrappedElement;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Wrapper<?> other = (Wrapper<?>) obj;
        if (this.wrappedElement != other.wrappedElement && (this.wrappedElement == null || !this.wrappedElement.equals(other.wrappedElement))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.wrappedElement != null ? this.wrappedElement.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Wrapper<T> o) {
        return delegate.compare(getElement(), o.getElement());
    }

    @Override
    public String toString() {
        return delegate.toString(getElement());
    }

    public static <T> Wrapper<T> getWrapper(T o) {
        return getWrapper(o, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> Wrapper<T> getWrapper(T o, WrapperDelegate<? super T> delegate) {
        if (delegate == null) {
            if (o instanceof Train) {
                delegate = (WrapperDelegate<? super T>) new TrainWrapperDelegate(TrainWrapperDelegate.Type.NAME, ((Train) o).getDiagram().getTrainsData().getTrainComparator());
            } else if (o instanceof Route) {
                delegate = (WrapperDelegate<? super T>) new RouteWrapperDelegate(RouteWrapperDelegate.Type.SHORT);
            } else if (o instanceof TrainsCycleItem) {
                delegate = (WrapperDelegate<? super T>) new TrainsCycleItemWrapperDelegate();
            } else if (o instanceof ImportMatch) {
                delegate = (WrapperDelegate<? super T>) new ImportMatchWrapperDelegate();
            } else if (o instanceof ImportComponent) {
                delegate = (WrapperDelegate<? super T>) new ImportComponentWrapperDelegate();
            } else if (o instanceof Locale) {
                delegate = (WrapperDelegate<? super T>) new WrapperDelegateAdapter<Locale>(l -> l.getDisplayName(l), l -> l != null ? l.toString() : "");
            }
        }
        return delegate == null ? new Wrapper<T>(o) : new Wrapper<T>(o, delegate);
    }

    @SafeVarargs
    public static <T> List<Wrapper<T>> asWrapperList(T... objList) {
        return getWrapperList(Arrays.asList(objList), null);
    }

    public static <T> List<Wrapper<T>> getWrapperList(Collection<? extends T> objList) {
        return getWrapperList(objList, null);
    }

    public static <T> List<Wrapper<T>> getWrapperList(Collection<? extends T> objList, WrapperDelegate<? super T> delegate) {
        List<Wrapper<T>> list = new LinkedList<Wrapper<T>>();
        Class<?> clazz = null;
        for (T o : objList) {
            list.add(getWrapper(o, delegate));
            if (clazz != null && !clazz.equals(o.getClass())) {
                throw new IllegalArgumentException("All element are expected to have the same class.");
            }
            clazz = o.getClass();
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T> WrapperDelegate<T> convert(WrapperDelegate<? extends T> delegate) {
        return (WrapperDelegate<T>) delegate;
    }

    public static <T> T unwrap(Wrapper<T> wrapper) {
        return wrapper.getElement();
    }

    public static <T> List<T> unwrap(List<Wrapper<T>> list) {
        List<T> result = new ArrayList<T>(list.size());
        for (Wrapper<T> w : list) {
            result.add(unwrap(w));
        }
        return result;
    }

    private static class PrototypeWrapper<T> extends Wrapper<T> {

        private final String prototypeValue;

        public PrototypeWrapper(String prototypeValue) {
            super(null);
            this.prototypeValue = prototypeValue;
        }

        @Override
        public String toString() {
            return prototypeValue;
        }
    }

    public static <V> Wrapper<V> getPrototypeWrapper(String prototypeValue) {
        return new PrototypeWrapper<V>(prototypeValue);
    }

    public static <V> Wrapper<V> getEmptyWrapper(String textValue) {
        return getPrototypeWrapper(textValue);
    }
}
