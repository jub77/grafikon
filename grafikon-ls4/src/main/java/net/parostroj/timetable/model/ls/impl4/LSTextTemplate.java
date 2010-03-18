package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.Language;
import net.parostroj.timetable.model.TextTemplate;

/**
 * Storage for text template.
 *
 * @author jub
 */
@XmlType(propOrder = {"template", "language"})
public class LSTextTemplate {
    private String template;
    private String language;

    public LSTextTemplate() {
    }

    public LSTextTemplate(TextTemplate textTemplate) {
        this.template = textTemplate.getTemplate();
        this.language = textTemplate.getLanguage().name();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public TextTemplate createTextTemplate() {
        return TextTemplate.createTextTemplate(template, Language.valueOf(language));
    }
}
