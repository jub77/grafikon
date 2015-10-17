package net.parostroj.timetable.model;

class ItemWithIdSetImpl<T extends ObjectWithId> extends ItemSetImpl<T> implements ItemWithIdSet<T> {

    public ItemWithIdSetImpl() {
        super();
    }

    public ItemWithIdSetImpl(net.parostroj.timetable.model.ItemSetImpl.ItemSetEventCallback<T> eventCallback) {
        super(eventCallback);
    }
}
