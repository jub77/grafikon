package net.parostroj.timetable.model.templates;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Loading of templates from base url.
 *
 * @author jub
 */
class UrlTemplateLoader extends AbstractTemplateLoader {

    private static final String TEMPLATE_LIST_FILE = "templates.xml";

    private final URL baseUrl;

    public UrlTemplateLoader(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    @Override
    protected InputStream getTemplateList() throws IOException {
        return new URL(baseUrl.toString() + "/" + TEMPLATE_LIST_FILE).openStream();
    }

    @Override
    protected InputStream getTemplate(Template template) throws IOException {
        return new URL(baseUrl.toString() + "/" + template.getFilename()).openStream();
    }
}
