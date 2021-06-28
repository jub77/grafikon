package net.parostroj.timetable.model.templates;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.ls.LSException;

abstract class AbstractTemplateLoader<T> implements TemplateLoader<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractTemplateLoader.class);

    private TemplateList templateList;
    private final LoadDelegate<T> loadDelegate;

    AbstractTemplateLoader(LoadDelegate<T> loadDelegate) {
        this.loadDelegate = loadDelegate;
    }

    @Override
    public TemplateList getTemplateList() throws LSException {
        if (templateList == null) {
            // load template list
            try (InputStream is = getTemplateListStream()) {
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
        return templateList;
    }

    protected abstract  InputStream getTemplateListStream() throws IOException;

    @Override
    public T loadTemplate(Template template) throws LSException {
        // if no filename is defined - return null
        if (template.getFilename() == null) {
            return null;
        }
        // create file with template location
        T instance;
        try (InputStream iStream = getTemplateStream(template); ZipInputStream is = new ZipInputStream(iStream)) {
            instance = loadDelegate.load(is);
        } catch (IOException e) {
            throw new LSException("Error loading template: " + e.getMessage(), e);
        }
        return instance;
    }

    protected abstract InputStream getTemplateStream(Template template) throws IOException;
}
