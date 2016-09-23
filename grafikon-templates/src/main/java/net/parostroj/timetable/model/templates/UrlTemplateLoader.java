package net.parostroj.timetable.model.templates;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Loading of templates from base url.
 *
 * @author jub
 */
class UrlTemplateLoader<T> extends AbstractTemplateLoader<T> {

    private static final String TEMPLATE_LIST_FILE = "templates.xml";

    private final URL baseUrl;
    private final String templateListFile;

    public UrlTemplateLoader(URL baseUrl, LoadDelegate<T> loadDelegate) {
        this(baseUrl, TEMPLATE_LIST_FILE, loadDelegate);
    }

    public UrlTemplateLoader(URL baseUrl, String templateListFile, LoadDelegate<T> loadDelegate) {
        super(loadDelegate);
        this.baseUrl = baseUrl;
        this.templateListFile = templateListFile;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    @Override
    protected InputStream getTemplateListStream() throws IOException {
        return new URL(baseUrl.toString() + "/" + templateListFile).openStream();
    }

    @Override
    protected InputStream getTemplateStream(Template template) throws IOException {
        return new URL(baseUrl.toString() + "/" + template.getFilename()).openStream();
    }

    @Override
    public String toString() {
        return "Url: " + baseUrl;
    }
}
