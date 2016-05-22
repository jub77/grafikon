package net.parostroj.timetable.model.library;

import net.parostroj.timetable.model.ObjectWithId;

public class LibraryItem {

    private LibraryItemDescription description;
    private ObjectWithId item;

    public LibraryItem(LibraryItemDescription description, ObjectWithId item) {
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
