package net.parostroj.timetable.gui.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractListModel;

/**
 * List model with wrappers around object with ids.
 *
 * @author jub
 */
public class WrapperListModel<T> extends AbstractListModel {

    private Set<T> set;
    private List<Wrapper<T>> list;
    private boolean sorted;

    public WrapperListModel() {
        list = new ArrayList<Wrapper<T>>();
        sorted = true;
    }

    public WrapperListModel(List<Wrapper<T>> list) {
        this.list = list;
        this.sorted = true;
        this.sort(list);
    }

    public WrapperListModel(List<Wrapper<T>> list, Set<T> set) {
        this.list = list;
        this.set = set;
        this.sorted = true;
        this.sort(list);
    }

    public WrapperListModel(List<Wrapper<T>> list, Set<T> set, boolean sorted) {
        this.list = list;
        this.set = set;
        this.sorted = sorted;
        this.sort(list);
    }

    private void sort(List<? extends Wrapper<T>> ll) {
        if (sorted)
            Collections.sort(ll);
    }

    public void removeWrapper(Wrapper<T> w) {
        // remove from set
        if (set != null)
            set.remove(w.getElement());
        // remove from list
        int index = list.indexOf(w);
        if (index != -1) {
            list.remove(w);
            this.fireIntervalRemoved(this, index, index);
        }
    }

    public void removeObject(T object) {
        Wrapper<T> wrapper = this.getWrapperForObject(object);
        this.removeWrapper(wrapper);
    }

    public void addWrapper(Wrapper<T> w) {
        // add to set
        if (set != null)
            set.add(w.getElement());
        // add to list
        list.add(w);
        this.sort(list);
        int index = list.indexOf(w);
        this.fireIntervalAdded(this, index, index);
    }

    public List<Wrapper<T>> getListOfWrappers() {
        return list;
    }

    public Set<T> getSetOfObjects() {
        return set;
    }

    public void initializeSet() {
        this.set = new HashSet<T>();
        for (Wrapper<T> w : list) {
            this.set.add(w.getElement());
        }
    }

    public void setListOfWrappers(List<Wrapper<T>> list) {
        if (list.size() > 0)
            this.fireIntervalRemoved(this, 0, list.size() - 1);
        this.list = list;
        this.set = null;
        if (list.size() > 0)
            this.fireIntervalAdded(this, 0, list.size() - 1);
    }

    public int getIndexOfObject(T object) {
        int i = 0;
        for (Wrapper<T> wrapper : list) {
            if (wrapper.getElement().equals(object))
                return i;
            i++;
        }
        return -1;
    }

    public Wrapper<T> getWrapperForObject(T object) {
        for (Wrapper<T> wrapper : list) {
            if (wrapper.getElement().equals(object))
                return wrapper;
        }
        return null;
    }

    public void clear() {
        int size = list.size();
        list.clear();
        if (set != null)
            initializeSet();
        if (size != 0)
            this.fireIntervalRemoved(this, 0, size - 1);
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public Object getElementAt(int index) {
        return list.get(index);
    }
}
