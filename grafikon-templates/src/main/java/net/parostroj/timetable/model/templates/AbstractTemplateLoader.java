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

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFile;
import net.parostroj.timetable.model.ls.LSFileFactory;

abstract class AbstractTemplateLoader implements TemplateLoader {

    private static final Logger log = LoggerFactory.getLogger(AbstractTemplateLoader.class);

    private TemplateList templateList;

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
    public TrainDiagram getTemplate(String name) throws LSException {
        for (Template template : getTemplates()) {
            if (template.getName().equals(name)) {
                // create file with template location
                TrainDiagram diagram = null;
                try (InputStream iStream = getTemplate(template); ZipInputStream is = new ZipInputStream(iStream)) {
                    LSFile ls = LSFileFactory.getInstance().createForLoad(is);
                    diagram = ls.load(is);
                } catch (IOException e) {
                    throw new LSException("Error loading template: " + e.getMessage(), e);
                }
                return diagram;
            }
        }
        // no template found
        return null;
    }

    abstract protected InputStream getTemplate(Template template) throws IOException;
}
