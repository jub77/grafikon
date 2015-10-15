package net.parostroj.timetable.model;

public class ItemWithIdListImpl<T extends ObjectWithId> extends ItemListImpl<T> implements ItemWithIdList<T> {

    public ItemWithIdListImpl() {
        super();
    }

    public ItemWithIdListImpl(net.parostroj.timetable.model.ItemListImpl.ItemListEventCallback<T> eventCallback) {
        super(eventCallback);
    }
}
