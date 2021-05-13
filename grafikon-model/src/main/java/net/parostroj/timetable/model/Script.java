package net.parostroj.timetable.model;

import java.util.Map;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Script.
 *
 * @author jub
 */
public abstract class Script {

    private static final Logger log = LoggerFactory.getLogger(Script.class);

    public enum Language {
        GROOVY
    }

    private final String sourceCode;
    private final Language language;

    protected Script(String sourceCode, Language language) {
        this.sourceCode = sourceCode;
        this.language = language;
    }
    public String getSourceCode() {
        return sourceCode;
    }

    public Language getLanguage() {
        return language;
    }

    public Object evaluate(Map<String, Object> binding) {
        try {
            return this.evaluateWithException(binding);
        } catch (GrafikonException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public abstract Object evaluateWithException(Map<String, Object> binding) throws GrafikonException;

    public static Script createScript(String sourceCode, Language language) throws GrafikonException {
        return createScript(sourceCode, language, false);
    }

    public static Script createScript(String sourceCode, Language language, boolean initizalize) throws GrafikonException {
        if (language == Language.GROOVY) {
            return new ScriptEngineScript(sourceCode, language, initizalize);
        }
        throw new IllegalArgumentException("No script for language available.");
    }

    public void freeResources() {
        // nothing
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Script)) {
            return false;
        }
        final Script other = (Script) obj;
        if (!Objects.equals(this.sourceCode, other.sourceCode)) {
            return false;
        }
        return this.language == other.language;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.sourceCode != null ? this.sourceCode.hashCode() : 0);
        hash = 59 * hash + (this.language != null ? this.language.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("%s[%d]", getLanguage(), sourceCode != null ? sourceCode.length() : 0);
    }
}
