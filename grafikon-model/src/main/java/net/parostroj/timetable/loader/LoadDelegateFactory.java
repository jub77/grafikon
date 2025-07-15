package net.parostroj.timetable.loader;

import net.parostroj.timetable.model.ls.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import java.util.zip.ZipInputStream;

public final class LoadDelegateFactory {

    private LoadDelegateFactory() {}

    public static <T> LoadDelegate<T, InputStream> createForLS(Supplier<LSFactory<? extends LS<T>, T>> lsSource, LSFeature... features) {
        return (is, item) -> {
            try (ZipInputStream zis = new ZipInputStream(is)) {
                LSSource source = LSSource.create(zis);
                LS<T> ls = lsSource.get().createForLoad(zis);
                return ls.load(source, features);
            } catch (IOException e) {
                throw new LSException("Error loading diagram: " + e.getMessage(), e);
            }
        };
    }

    public static <T> LoadDelegate<T, LSSource> createSourceForLS(Supplier<LSFactory<? extends LS<T>, T>> lsSource, LSFeature... features) {
        return (source, dataItem) -> {
            try (source) {
                LS<T> ls = lsSource.get().createForLoad();
                return ls.load(source, features);
            }
        };
    }
}
