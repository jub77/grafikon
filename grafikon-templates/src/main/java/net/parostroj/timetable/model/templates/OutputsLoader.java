package net.parostroj.timetable.model.templates;

import net.parostroj.timetable.loader.DataItemLoader;
import net.parostroj.timetable.model.TrainDiagramType;
import net.parostroj.timetable.model.library.Library;

import java.net.URL;

public interface OutputsLoader {

    static DataItemLoader<Library> getDefault() {
        return DataItemLoader.getFromResources(TrainDiagramType.RAW, "/outputs", "list.yaml", Library.class);
    }

    static DataItemLoader<Library> getDefaultFromUrl(URL url) {
        return DataItemLoader.getFromUrl(TrainDiagramType.RAW, url, "outputs.yaml", Library.class);
    }
}
