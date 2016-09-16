package net.parostroj.timetable.model.templates;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class for loading default model templates.
 *
 * @author jub
 */
class DefaultTemplateLoader extends AbstractTemplateLoader {

    private static final String TEMPLATE_LIST_FILE = "/templates/list.xml";
    private static final String TEMPLATES_LOCATION = "/templates/";

    @Override
    protected InputStream getTemplateList() throws IOException {
        return DefaultTemplateLoader.class.getResourceAsStream(TEMPLATE_LIST_FILE);
    }

    @Override
    protected InputStream getTemplate(Template template) throws IOException {
        return DefaultTemplateLoader.class.getResourceAsStream(TEMPLATES_LOCATION + template.getFilename());
    }
}
