package net.parostroj.timetable.model.ls.impl4;

import jakarta.xml.bind.annotation.XmlType;
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

    public TextTemplate createTextTemplate(LSContext context) {
        TextTemplate.Language lng = TextTemplate.Language.fromString(language);
        if (lng != null && context.getPartFactory().getType().isAllowed(lng)) {
            return context.getPartFactory().getType().createTextTemplate(template, lng);
        } else {
            return null;
        }
    }
}
