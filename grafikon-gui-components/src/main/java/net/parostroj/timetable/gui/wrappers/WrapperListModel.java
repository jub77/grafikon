package net.parostroj.timetable.gui.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 * List model with wrappers around object with ids.
 *
 * @author jub
 */
public class WrapperListModel<T> extends AbstractListModel<Wrapper<T>> implements ComboBoxModel<Wrapper<T>> {

    public interface ObjectListener<T> {
        void added(T object, int index);
        void removed(T object);
        void moved(T object, int fromIndex, int toIndex);
    }

    private Set<T> set;
    private List<Wrapper<T>> list;
    private final boolean sorted;
    private Wrapper<T> selectedItem;
    private ObjectListener<T> listener;

    public WrapperListModel() {
        this(true);
    }

    public WrapperListModel(boolean sorted) {
        this.list = new ArrayList<Wrapper<T>>();
        this.sorted = sorted;
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

    public void setObjectListener(ObjectListener<T> listener) {
        this.listener = listener;
    }

    public ObjectListener<T> getObjectListener() {
        return listener;
    }

    private void sort(List<? extends Wrapper<T>> ll) {
        if (sorted) {
            Collections.sort(ll);
        }
    }

    public void removeWrapper(Wrapper<T> w) {
        if (w == null) {
            return;
        }
        int index = list.indexOf(w);
        if (index != -1) {
            this.removeIndex(index);
        }
    }

    public void removeObject(T object) {
        int index = this.getIndexOfObject(object);
        if (index != -1) {
            this.removeIndex(index);
        }
    }

    public Wrapper<T> removeIndex(int index) {
        Wrapper<T> w = list.get(index);
        // remove from set
        if (set != null) {
            set.remove(w.getElement());
        }
        // remove from list
        if (this.listener != null) {
            this.listener.removed(w.getElement());
        }
        list.remove(index);
        this.fireIntervalRemoved(this, index, index);
        return w;
    }

    public Wrapper<T> getIndex(int index) {
        return list.get(index);
    }

    public void refreshIndex(int index) {
        this.fireContentsChanged(this, index, index);
    }

    public void refreshObject(T object) {
        int index = this.getIndexOfObject(object);
        if (index != -1) {
            this.refreshIndex(index);
        }
    }

    public void moveIndexDown(int index) {
        if (this.sorted) {
            throw new IllegalStateException("Cannot move in sorted list.");
        }
        if (index < getSize() - 1) {
            Wrapper<T> removed = list.remove(index);
            list.add(index + 1, removed);
            this.fireContentsChanged(this, index, index + 1);
            if (this.listener != null) {
                this.listener.moved(removed.getElement(), index, index + 1);
            }
        }
    }

    public void moveIndexUp(int index) {
        if (this.sorted) {
            throw new IllegalStateException("Cannot move in sorted list.");
        }
        if (index > 0) {
            Wrapper<T> removed = list.remove(index);
            list.add(index - 1, removed);
            this.fireContentsChanged(this, index - 1, index);
            if (this.listener != null) {
                this.listener.moved(removed.getElement(), index, index - 1);
            }
        }
    }

    public void addWrapper(Wrapper<T> w) {
        // add to set
        if (set != null) {
            set.add(w.getElement());
        }
        // add to list
        list.add(w);
        this.sort(list);
        int index = list.indexOf(w);
        this.fireIntervalAdded(this, index, index);
        if (this.listener != null) {
            this.listener.added(w.getElement(), index);
        }
    }

    public void addWrapper(Wrapper<T> w, int index) {
        if (this.sorted) {
            throw new IllegalStateException("Cannot insert in specific place in sorted list.");
        }
        if (set != null) {
            set.add(w.getElement());
        }
        list.add(index, w);
        this.fireIntervalAdded(this, index, index);
        if (this.listener != null) {
            this.listener.added(w.getElement(), index);
        }
    }

    public void setWrapper(Wrapper<T> w, int index) {
        if (this.sorted) {
            throw new IllegalStateException("Cannot insert in specific place in sorted list.");
        }
        Wrapper<T> old = list.get(index);
        if (set != null) {
            set.remove(old.getElement());
            set.add(w.getElement());
        }
        list.set(index, w);
        this.fireContentsChanged(this, index, index);
        if (this.listener != null) {
            this.listener.removed(old.getElement());
            this.listener.added(w.getElement(), index);
        }
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
        if (list.size() > 0) {
            this.fireIntervalRemoved(this, 0, list.size() - 1);
        }
        this.list = list;
        this.set = null;
        this.sort(list);
        if (list.size() > 0) {
            this.fireIntervalAdded(this, 0, list.size() - 1);
        }
    }

    public int getIndexOfObject(T object) {
        int i = 0;
        for (Wrapper<T> wrapper : list) {
            if (wrapper.getElement().equals(object)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public Wrapper<T> getWrapperForObject(T object) {
        for (Wrapper<T> wrapper : list) {
            if (wrapper.getElement().equals(object)) {
                return wrapper;
            }
        }
        return null;
    }

    public void clear() {
        int size = list.size();
        list.clear();
        if (set != null) {
            initializeSet();
        }
        if (size != 0) {
            this.fireIntervalRemoved(this, 0, size - 1);
        }
    }

    @Override
    public int getSize() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public Wrapper<T> getElementAt(int index) {
        return list.get(index);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setSelectedItem(Object anItem) {
        selectedItem = (Wrapper<T>) anItem;
        this.fireContentsChanged(this, -1, -1);
    }

    @Override
    public Object getSelectedItem() {
        return selectedItem;
    }

    public T getSelectedObject() {
        return selectedItem != null ? selectedItem.getElement() : null;
    }

    public Wrapper<T> getSelectedWrapper() {
        return selectedItem;
    }

    public void setSelectedObject(T object) {
        int index = getIndexOfObject(object);
        if (index != -1) {
            setSelectedItem(getIndex(index));
        } else {
            setSelectedItem(null);
        }
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
