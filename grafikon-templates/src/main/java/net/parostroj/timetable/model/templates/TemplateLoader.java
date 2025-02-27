package net.parostroj.timetable.model.templates;

import net.parostroj.timetable.loader.DataItemLoader;
import net.parostroj.timetable.loader.LoadDelegate;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainDiagramType;

import java.net.URL;

public interface TemplateLoader {

    static DataItemLoader<TrainDiagram> getDefault() {
        return DataItemLoader.getFromResources("/templates", "list.yaml", LoadDelegate.createForTrainDiagram(TrainDiagramType.NORMAL));
    }

    static DataItemLoader<TrainDiagram> getDefaultFromUrl(URL url) {
        return DataItemLoader.getFromUrl(url, "list.yaml", LoadDelegate.createForTrainDiagram(TrainDiagramType.NORMAL));
    }
}
