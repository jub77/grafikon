package net.parostroj.timetable.model.templates;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * List of templates. Loaded by JAXB.
 * 
 * @author jub
 */
@XmlRootElement(name = "template_list")
public class TemplateList {

    private List<Template> templates;
    
    @XmlElement(name = "template")
    public List<Template> getTemplates() {
        return templates;
    }

    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }
}
