package net.parostroj.timetable.model.templates;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.ls.LSException;

abstract class AbstractTemplateLoader<T> implements TemplateLoader<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractTemplateLoader.class);

    private TemplateList templateList;
    private LoadDelegate<T> loadDelegate;

    public AbstractTemplateLoader(LoadDelegate<T> loadDelegate) {
        this.loadDelegate = loadDelegate;
    }

    @Override
    public List<Template> getTemplates() throws LSException {
        if (templateList == null) {
            // load template list
            try (InputStream is = getTemplateList()) {
                JAXBContext context = JAXBContext.newInstance(TemplateList.class);
                Unmarshaller u = context.createUnmarshaller();
                templateList = (TemplateList) u.unmarshal(is);
                log.debug("Loaded list of templates: {}", this);
            } catch (JAXBException e) {
                throw new LSException("Cannot deserialize list of templates: " + e.getMessage(), e);
            } catch (IOException e) {
                throw new LSException("Error reading list of templates: " + e.getMessage(), e);
            }
        }
        return templateList.getTemplates();
    }

    abstract protected InputStream getTemplateList() throws IOException;

    @Override
    public T loadTemplate(Template template) throws LSException {
        if (!templateList.getTemplates().contains(template)) {
            throw new IllegalArgumentException("Template does not belong to this loader: " + template);
        }
        // create file with template location
        T instance = null;
        try (InputStream iStream = getTemplateStream(template); ZipInputStream is = new ZipInputStream(iStream)) {
            instance = loadDelegate.load(is);
        } catch (IOException e) {
            throw new LSException("Error loading template: " + e.getMessage(), e);
        }
        return instance;
    }

    abstract protected InputStream getTemplateStream(Template template) throws IOException;
}
