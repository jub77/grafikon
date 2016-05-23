package net.parostroj.timetable.model.library;

import net.parostroj.timetable.model.ObjectWithId;

public class LibraryItem {

    private final LibraryItemDescription description;
    private final ObjectWithId item;

    LibraryItem(LibraryItemDescription description, ObjectWithId item) {
        this.description = description;
        this.item = item;
    }

    public LibraryItemDescription getDescription() {
        return description;
    }

    public ObjectWithId getItem() {
        return item;
    }
}
