package net.parostroj.timetable.loader;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainDiagramType;
import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.ls.*;

import java.util.zip.ZipInputStream;

interface LoadDelegate<T> {

    T load(ZipInputStream is) throws LSException;

    static LoadDelegate<TrainDiagram> createForTrainDiagram(TrainDiagramType type) {
        return is -> {
            LSFile ls = LSFileFactory.getInstance().createForLoad(is);
            return ls.load(type, is);
        };
    }

    static LoadDelegate<Library> createForLibrary(TrainDiagramType type) {
        return is -> {
            LSLibrary ls = LSLibraryFactory.getInstance().createForLoad(is);
            return ls.load(type, is);
        };
    }

    @SuppressWarnings("unchecked")
    static <T> LoadDelegate<T> createForClass(TrainDiagramType type, Class<T> clazz) {
        if (TrainDiagram.class.equals(clazz)) {
            return (LoadDelegate<T>) createForTrainDiagram(type);
        } else if (Library.class.equals(clazz)) {
            return (LoadDelegate<T>) createForLibrary(type);
        } else {
            throw new IllegalArgumentException("No load delegate defined for class: " + clazz);
        }
    }
}
