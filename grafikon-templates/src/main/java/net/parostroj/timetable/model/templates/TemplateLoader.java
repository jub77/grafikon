package net.parostroj.timetable.model.templates;

import net.parostroj.timetable.loader.DataItemLoader;
import net.parostroj.timetable.loader.LoadDelegateFactory;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSFeature;
import net.parostroj.timetable.model.ls.LSFileFactory;

import java.net.URL;

public interface TemplateLoader {

    static DataItemLoader<TrainDiagram> getDefault(LSFeature... features) {
        return DataItemLoader.getFromResourcesDirs("/templates", "list.yaml",
                LoadDelegateFactory.createSourceForLS(LSFileFactory::getInstance, features));
    }

    static DataItemLoader<TrainDiagram> getDefaultFromUrl(URL url, LSFeature... features) {
        return DataItemLoader.getFromUrl(url, "list.yaml",
                LoadDelegateFactory.createForLS(LSFileFactory::getInstance, features));
    }
}
