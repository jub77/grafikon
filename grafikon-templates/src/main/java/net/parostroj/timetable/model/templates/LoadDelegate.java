package net.parostroj.timetable.model.templates;

import java.util.zip.ZipInputStream;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFile;
import net.parostroj.timetable.model.ls.LSFileFactory;
import net.parostroj.timetable.model.ls.LSLibrary;
import net.parostroj.timetable.model.ls.LSLibraryFactory;

interface LoadDelegate<T> {

    T load(ZipInputStream is) throws LSException;

    static LoadDelegate<TrainDiagram> createForTrainDiagram() {
        return is -> {
            LSFile ls = LSFileFactory.getInstance().createForLoad(is);
            return ls.load(is);
        };
    }

    static LoadDelegate<Library> createForLibrary() {
        return is -> {
            LSLibrary ls = LSLibraryFactory.getInstance().createForLoad(is);
            return ls.load(is);
        };
    }

    @SuppressWarnings("unchecked")
    static <T> LoadDelegate<T> createForClass(Class<T> clazz) {
        if (TrainDiagram.class.equals(clazz)) {
            return (LoadDelegate<T>) createForTrainDiagram();
        } else if (Library.class.equals(clazz)) {
            return (LoadDelegate<T>) createForLibrary();
        } else {
            throw new IllegalArgumentException("No load delegate defined for class: " + clazz);
        }
    }
}
