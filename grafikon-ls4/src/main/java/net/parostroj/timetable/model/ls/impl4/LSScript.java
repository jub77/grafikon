package net.parostroj.timetable.model.ls.impl4;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.Script;

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

    public Script createScript(LSContext context) {
        Script.Language lng = Script.Language.fromString(language);
        if (lng != null && context.getPartFactory().getType().isAllowed(lng)) {
            return Script.create(sourceCode, lng);
        } else {
            return null;
        }
    }
}
