package net.parostroj.timetable.model.ls.impl4;

import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.ls.LSException;

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

    public TextTemplate createTextTemplate() throws LSException {
        try {
            return TextTemplate.createTextTemplate(template, TextTemplate.Language.valueOf(language));
        } catch (GrafikonException e) {
            throw new LSException("Error reading template.", e);
        }
    }
}
