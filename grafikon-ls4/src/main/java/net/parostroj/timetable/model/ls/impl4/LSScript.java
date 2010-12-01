package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.Language;
import net.parostroj.timetable.model.Script;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Storage for script.
 *
 * @author jub
 */
@XmlType(propOrder = {"sourceCode", "language"})
public class LSScript {
    private String sourceCode;
    private String language;

    public LSScript() {
    }

    public LSScript(Script script) {
        this.sourceCode = script.getSourceCode();
        this.language = script.getLanguage().name();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @XmlElement(name = "source_code")
    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public Script createScript() throws LSException {
        try {
            return Script.createScript(sourceCode, Language.valueOf(language));
        } catch (GrafikonException e) {
            throw new LSException("Error reading script.", e);
        }
    }
}
