package net.parostroj.timetable.model.library;

import java.util.Iterator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;

import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.AttributesHolder;
import net.parostroj.timetable.model.ObjectWithId;

/**
 * Library of train diagram items, that can be reused.
 *
 * @author jub
 */
public class Library implements AttributesHolder, Iterable<LibraryItem> {

    final Multimap<LibraryItemType, LibraryItem> itemMap;
    private final Attributes attributes;

    public Library() {
        this.itemMap = ArrayListMultimap.create(LibraryItemType.values().length, 5);
        this.attributes = new Attributes();
    }

    public Multimap<LibraryItemType, LibraryItem> getItems() {
        return itemMap;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public Iterator<LibraryItem> iterator() {
        return itemMap.values().iterator();
    }

    @Override
    public String toString() {
        return String.format("Items: %d", itemMap.size());
    }

    public ObjectWithId getObjectById(String id) {
        return FluentIterable.from(itemMap.values())
                .transform(LibraryItem::getObject)
                .firstMatch(object -> object.getId().equals(id))
                .orNull();
    }
}
