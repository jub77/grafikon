package net.parostroj.timetable.model;

/**
 * Item list with objects with id.
 *
 * @author jub
 *
 * @param <T> item of the list
 */
public interface ItemWithIdList<T extends ObjectWithId> extends ItemList<T> {

    T getById(String id);
}
