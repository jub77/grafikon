package net.parostroj.timetable.loader;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainDiagramType;
import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.ls.*;

import java.util.zip.ZipInputStream;

@FunctionalInterface
public interface LoadDelegate<T> {

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
}
