package net.parostroj.timetable.model.templates;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.FileLoadSave;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFileFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for loading model templates. It also returns list of available
 * templates.
 * 
 * @author jub
 */
public class TemplatesLoader {

    private static final Logger LOG = LoggerFactory.getLogger(TemplatesLoader.class.getName());
    private static final String TEMPLATE_LIST_FILE = "/templates/list.xml";
    private static final String TEMPLATES_LOCATION = "/templates/";
    private static TemplateList templateList;

    public static synchronized List<Template> getTemplates() {
        if (templateList == null) {
            // load template list
            try {
                JAXBContext context = JAXBContext.newInstance(TemplateList.class);
                Unmarshaller u = context.createUnmarshaller();
                InputStream is = TemplatesLoader.class.getResourceAsStream(TEMPLATE_LIST_FILE);
                try {
                    templateList = (TemplateList) u.unmarshal(is);
                } finally {
                    is.close();
                }
                LOG.debug("Loaded list of templates.");
            } catch (JAXBException e) {
                LOG.error("Cannot load list of templates.", e);
                // empty template list
                templateList = new TemplateList();
            } catch (IOException e) {
                LOG.error("Error reading/closing template file.", e);
                // empty template list
                templateList = new TemplateList();
            }
        }
        return templateList.getTemplates();
    }

    public TrainDiagram getTemplate(String name) throws LSException {
        for (Template template : getTemplates()) {
            if (template.getName().equals(name)) {
                // create file with template location
                InputStream iStream = TemplatesLoader.class.getResourceAsStream(TEMPLATES_LOCATION + template.getFilename());
                TrainDiagram diagram = null;
                try {
                    ZipInputStream is = new ZipInputStream(iStream);
                    FileLoadSave ls = LSFileFactory.getInstance().createForLoad(is);
                    diagram = ls.load(is);
                } finally {
                    try {
                        iStream.close();
                    } catch (IOException e) {
                        throw new LSException("Cannot load template.", e);
                    }
                }
                return diagram;
            }
        }
        // no template found
        return null;
    }
}
