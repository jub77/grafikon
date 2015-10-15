package net.parostroj.timetable.model;

public class ItemWithIdListImpl<T extends ObjectWithId> extends ItemListImpl<T> implements ItemWithIdList<T> {

    public ItemWithIdListImpl() {
        super();
    }

    public ItemWithIdListImpl(boolean moveAllowed) {
        super(moveAllowed);
    }

    @Override
    public T getById(String id) {
        for (T item : this) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }
}
