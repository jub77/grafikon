package net.parostroj.timetable.model.templates;

import net.parostroj.timetable.loader.DataItemLoader;
import net.parostroj.timetable.loader.LoadDelegateFactory;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSFeature;

import java.net.URL;

public interface TemplateLoader {

    static DataItemLoader<TrainDiagram> getDefault(LSFeature... features) {
        return DataItemLoader.getFromResources("/templates", "list.yaml", LoadDelegateFactory.createForTrainDiagram(features));
    }

    static DataItemLoader<TrainDiagram> getDefaultFromUrl(URL url, LSFeature... features) {
        return DataItemLoader.getFromUrl(url, "list.yaml", LoadDelegateFactory.createForTrainDiagram(features));
    }
}
