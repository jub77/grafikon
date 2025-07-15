package net.parostroj.timetable.model.templates;

import net.parostroj.timetable.loader.DataItemLoader;
import net.parostroj.timetable.loader.LoadDelegateFactory;
import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.ls.LSFeature;
import net.parostroj.timetable.model.ls.LSLibraryFactory;

import java.net.URL;

public interface OutputsLoader {

    static DataItemLoader<Library> getDefault() {
        return DataItemLoader.getFromResourcesDirs("/outputs", "list.yaml",
                LoadDelegateFactory.createSourceForLS(LSLibraryFactory::getInstance, LSFeature.RAW_DIAGRAM));
    }

    static DataItemLoader<Library> getDefaultFromUrl(URL url) {
        return DataItemLoader.getFromUrl(url, "list.yaml",
                LoadDelegateFactory.createForLS(LSLibraryFactory::getInstance, LSFeature.RAW_DIAGRAM));
    }
}
