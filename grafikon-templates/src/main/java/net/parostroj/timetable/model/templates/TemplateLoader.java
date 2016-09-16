package net.parostroj.timetable.model.templates;

import java.net.URL;
import java.util.List;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Class for loading model templates. It also returns list of available
 * templates.
 *
 * @author jub
 */
public interface TemplateLoader {

    public List<Template> getTemplates() throws LSException;

    public TrainDiagram getTemplate(String name) throws LSException;

    public static TemplateLoader getDefault() {
        return new DefaultTemplateLoader();
    }

    public static TemplateLoader getFromUrl(URL url) {
        return new UrlTemplateLoader(url);
    }
}
