package net.parostroj.timetable.loader;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainDiagramType;
import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.ls.*;

import java.util.zip.ZipInputStream;

@FunctionalInterface
public interface LoadDelegate<T> {

    T load(ZipInputStream is) throws LSException;

    static LoadDelegate<TrainDiagram> createForTrainDiagram(LSFeature... features) {
        return is -> {
            LSFile ls = LSFileFactory.getInstance().createForLoad(is);
            return ls.load(is, features);
        };
    }

    static LoadDelegate<Library> createForLibrary(LSFeature... features) {
        return is -> {
            LSLibrary ls = LSLibraryFactory.getInstance().createForLoad(is);
            return ls.load(is, features);
        };
    }
}
