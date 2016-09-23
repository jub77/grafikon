package net.parostroj.timetable.model.templates;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class for loading default model templates.
 *
 * @author jub
 */
class DefaultTemplateLoader<T> extends AbstractTemplateLoader<T> {

    public DefaultTemplateLoader(LoadDelegate<T> loadDelegate) {
        super(loadDelegate);
    }

    private static final String TEMPLATE_LIST_FILE = "/templates/list.xml";
    private static final String TEMPLATES_LOCATION = "/templates/";

    @Override
    protected InputStream getTemplateList() throws IOException {
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
