package net.parostroj.timetable.model.library;

import java.util.ArrayList;
import java.util.Collection;

public class Library {

    private Collection<LibraryItem> items;

    public Collection<LibraryItem> getItems() {
        if (items == null) {
            items = new ArrayList<LibraryItem>();
        }
        return items;
    }
}
