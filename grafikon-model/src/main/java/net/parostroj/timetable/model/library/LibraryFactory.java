package net.parostroj.timetable.model.library;

/**
 * Library factory - creates empty new library.
 *
 * @author jub
 */
public class LibraryFactory {

    private LibraryFactory() {}

    public static LibraryFactory getInstance() {
        return new LibraryFactory();
    }

    public Library createLibrary() {
        return new Library();
    }
}
