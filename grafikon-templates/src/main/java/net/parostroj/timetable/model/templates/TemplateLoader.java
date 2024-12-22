package net.parostroj.timetable.model.templates;

import net.parostroj.timetable.loader.DataItemLoader;
import net.parostroj.timetable.model.TrainDiagram;

import java.net.URL;

public interface TemplateLoader {

    static DataItemLoader<TrainDiagram> getDefault() {
        return DataItemLoader.getFromResources("/templates", "list.yaml", TrainDiagram.class);
    }

    static DataItemLoader<TrainDiagram> getDefaultFromUrl(URL url) {
        return DataItemLoader.getFromUrl(url, "templates.yaml", TrainDiagram.class);
    }
}
