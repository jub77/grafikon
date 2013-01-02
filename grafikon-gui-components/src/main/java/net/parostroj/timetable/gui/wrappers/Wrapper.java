package net.parostroj.timetable.gui.wrappers;

import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.model.*;

/**
 * Wrapper class for lists in GUI.
 *
 * @author jub
 */
public class Wrapper<T> implements Comparable<Wrapper<T>> {

    private T wrappedElement;
    private WrapperDelegate delegate;

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
        Wrapper<T> w = null;
        if (o instanceof Train) {
            w = new Wrapper<T>(o, new TrainWrapperDelegate(
                    TrainWrapperDelegate.Type.NAME,
                    new TrainComparator(TrainComparator.Type.ASC, ((Train)o).getTrainDiagram().getTrainsData().getTrainSortPattern())));
        } else if (o instanceof Route) {
            w = new Wrapper<T>(o, new RouteWrapperDelegate(RouteWrapperDelegate.Type.SHORT));
        } else {
            w = new Wrapper<T>(o);
        }
        return w;
    }

    public static <T> List<Wrapper<T>> getWrapperList(List<? extends T> objList) {
        List<Wrapper<T>> list = new LinkedList<Wrapper<T>>();
        Class<?> clazz = null;
        for (T o : objList) {
            list.add(getWrapper(o));
            if (clazz != null && !clazz.equals(o.getClass())) {
                throw new IllegalArgumentException("All element are expected to have the same class.");
            }
            clazz = o.getClass();
        }
        return list;
    }
}
