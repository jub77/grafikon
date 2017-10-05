package net.parostroj.timetable.model.events;

/**
 * @author jub
 */
public class ListData {

    private final Integer index;
    private final Integer fromIndex;
    private final Integer toIndex;

    private final Object oldItem;
    private final Object newItem;

    private ListData(Integer index, Integer fromIndex, Integer toIndex, Object oldItem, Object newItem) {
        this.index = index;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.oldItem = oldItem;
        this.newItem = newItem;
    }

    public Integer getFromIndex() {
        return fromIndex;
    }

    public Integer getIndex() {
        return index;
    }

    public Integer getToIndex() {
        return toIndex;
    }

    public Object getOldItem() {
        return oldItem;
    }

    public Object getNewItem() {
        return newItem;
    }

    @Override
    public String toString() {
        if (index != null) {
            return String.format("index: %d", index);
        } else if (fromIndex != null) {
            return String.format("from index: %d to index: %d", fromIndex, toIndex);
        } else {
            return String.format("from: %s to: %s", oldItem, newItem);
        }
    }

    public static ListData createData(Integer index1, Integer index2) {
        if (index1 == null && index2 == null) {
            return null;
        } else if (index2 == null) {
            return new ListData(index1, null, null, null, null);
        } else {
            return new ListData(null, index1, index2, null, null);
        }
    }

    public static ListData createData(Object oldItem, Object newItem) {
        return new ListData(null, null, null, oldItem, newItem);
    }
}
