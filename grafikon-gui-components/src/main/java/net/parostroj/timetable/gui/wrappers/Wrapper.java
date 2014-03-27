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
    private final WrapperDelegate delegate;

    public Wrapper(T element) {
        this.wrappedElement = element;
        this.delegate = new ElementWrapperDelegate();
    }

    public Wrapper(T element, WrapperDelegate delegate) {
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

    public static <T> Wrapper<T> getWrapper(T o, WrapperDelegate delegate) {
        if (delegate == null) {
            if (o instanceof Train) {
                delegate = new TrainWrapperDelegate(TrainWrapperDelegate.Type.NAME, ((Train) o).getTrainDiagram().getTrainsData().getTrainComparator());
            } else if (o instanceof Route) {
                delegate = new RouteWrapperDelegate(RouteWrapperDelegate.Type.SHORT);
            } else if (o instanceof TrainsCycleItem) {
                delegate = new TrainsCycleItemWrapperDelegate();
            } else if (o instanceof ImportMatch) {
                delegate = new ImportMatchWrapperDelegate();
            } else if (o instanceof ImportComponent) {
                delegate = new ImportComponentWrapperDelegate();
            }
        }
        return delegate == null ? new Wrapper<T>(o) : new Wrapper<T>(o, delegate);
    }

    public static <T> List<Wrapper<T>> getWrapperList(T... objList) {
        return getWrapperList(Arrays.asList(objList), null);
    }

    public static <T> List<Wrapper<T>> getWrapperList(Collection<? extends T> objList) {
        return getWrapperList(objList, null);
    }

    public static <T> List<Wrapper<T>> getWrapperList(Collection<? extends T> objList, WrapperDelegate delegate) {
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
}
