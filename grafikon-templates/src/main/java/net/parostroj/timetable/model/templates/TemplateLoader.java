package net.parostroj.timetable.model.templates;

import java.net.URL;
import java.util.List;

import net.parostroj.timetable.model.ls.LSException;

/**
 * Class for loading model templates. It also returns list of available
 * templates.
 *
 * @author jub
 */
public interface TemplateLoader<T> {

    public List<Template> getTemplates() throws LSException;

    public T loadTemplate(Template template) throws LSException;

    public static <T> TemplateLoader<T> getDefault(Class<T> clazz) {
        return new DefaultTemplateLoader<>(LoadDelegate.createForClass(clazz), clazz);
    }

    public static <T> TemplateLoader<T> getFromUrl(URL url, Class<T> clazz) {
        return new UrlTemplateLoader<>(url, LoadDelegate.createForClass(clazz));
    }
}
