package net.parostroj.timetable.loader;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.ls.*;

import java.io.IOException;
import java.util.zip.ZipInputStream;

public final class LoadDelegateFactory {

    private LoadDelegateFactory() {}

    public static LoadDelegate<TrainDiagram> createForTrainDiagram(LSFeature... features) {
        return (is, item) -> {
            try (ZipInputStream zis = new ZipInputStream(is)) {
                LSSource source = LSSource.create(zis);
                LSFile ls = LSFileFactory.getInstance().createForLoad(source);
                return ls.load(source, features);
            } catch (IOException e) {
                throw new LSException("Error loading diagram: " + e.getMessage(), e);
            }
        };
    }

    public static LoadDelegate<Library> createForLibrary(LSFeature... features) {
        return (is, item) -> {
            try (ZipInputStream zis = new ZipInputStream(is)) {
                LSSource source = LSSource.create(zis);
                LSLibrary ls = LSLibraryFactory.getInstance().createForLoad(source);
                return ls.load(source, features);
            } catch (IOException e) {
                throw new LSException("Error loading library: " + e.getMessage(), e);
            }
        };
    }
}
