package net.parostroj.timetable.gui.helpers;

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

    public Wrapper(T element) {
        this.setElement(element);
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
        return this.toString().compareTo(o.toString());
    }

    @SuppressWarnings("unchecked")
    public static <T> Wrapper<T> getWrapper(T o) {
        Wrapper<T> w = null;
        if (o instanceof Node) {
            w = (Wrapper)new NodeWrapper((Node)o);
        } else if (o instanceof Train) {
            w = (Wrapper)new TrainWrapper(
                    (Train) o,
                    TrainWrapper.Type.NAME,
                    new TrainComparator(TrainComparator.Type.ASC, ((Train)o).getTrainDiagram().getTrainsData().getTrainSortPattern()));
        } else if (o instanceof TrainType) {
            w = (Wrapper)new TrainsTypeWrapper((TrainType)o);
        } else if (o instanceof TrainsCycle) {
            w = (Wrapper)new TrainsCycleWrapper((TrainsCycle)o);
        } else if (o instanceof Route) {
            w = (Wrapper)new RouteWrapper((Route)o);
        } else if (o instanceof TimeIntervalWrapper) {
            w = (Wrapper)new TimeIntervalWrapper((TimeInterval)o);
        } else if (o instanceof LineClass) {
            w = (Wrapper)new LineClassWrapper((LineClass)o);
        } else if (o instanceof EngineClass) {
            w = (Wrapper)new EngineClassWrapper((EngineClass)o);
        } else {
            throw new IllegalArgumentException("Not supported type: " + o.getClass());
        }
        return w;
    }

    public static <T> List<Wrapper<T>> getWrapperList(List<T> objList) {
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
