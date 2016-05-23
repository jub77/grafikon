package net.parostroj.timetable.model.library;

public class LibraryItemDescription {

    private final LibraryItemType type;

    LibraryItemDescription(LibraryItemType type) {
        this.type = type;
    }

    public LibraryItemType getType() {
        return type;
    }
}
