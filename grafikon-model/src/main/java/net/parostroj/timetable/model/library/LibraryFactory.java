package net.parostroj.timetable.model.library;

public class LibraryFactory {

    private LibraryFactory() {}

    public static LibraryFactory getInstance() {
        return new LibraryFactory();
    }

    public Library createLibrary() {
        return new Library();
    }
}
