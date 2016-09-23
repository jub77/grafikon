package net.parostroj.timetable.model.templates;

import java.io.IOException;
import java.io.InputStream;

import net.parostroj.timetable.model.TrainDiagram;

/**
 * Class for loading default model templates.
 *
 * @author jub
 */
class DefaultTemplateLoader<T> extends AbstractTemplateLoader<T> {

    public DefaultTemplateLoader(LoadDelegate<T> loadDelegate, Class<T> clazz) {
        super(loadDelegate);
        if (!TrainDiagram.class.equals(clazz)) {
            throw new IllegalArgumentException("There is only train diagram default loader available");
        }
    }

    private static final String TEMPLATE_LIST_FILE = "/templates/list.xml";
    private static final String TEMPLATES_LOCATION = "/templates/";

    @Override
    protected InputStream getTemplateListStream() throws IOException {
        return DefaultTemplateLoader.class.getResourceAsStream(TEMPLATE_LIST_FILE);
    }

    @Override
    protected InputStream getTemplateStream(Template template) throws IOException {
        return DefaultTemplateLoader.class.getResourceAsStream(TEMPLATES_LOCATION + template.getFilename());
    }

    @Override
    public String toString() {
        return "Default";
    }
}
