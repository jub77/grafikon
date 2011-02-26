package net.parostroj.timetable.model;

import java.util.Map;

/**
 * Script.
 *
 * @author jub
 */
public abstract class Script {

    public static enum Language {
        GROOVY, JAVASCRIPT;
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

    public abstract Object evaluate(Map<String, Object> binding);
    
    public abstract Object evaluateWithException(Map<String, Object> binding) throws GrafikonException;

    public static Script createScript(String sourceCode, Language language) throws GrafikonException {
        switch (language) {
            case GROOVY: case JAVASCRIPT:
                return new ScriptEngineScript(sourceCode, language);
            default:
                throw new IllegalArgumentException("No script for language available.");
        }
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
        if ((this.sourceCode == null) ? (other.sourceCode != null) : !this.sourceCode.equals(other.sourceCode)) {
            return false;
        }
        if (this.language != other.language) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.sourceCode != null ? this.sourceCode.hashCode() : 0);
        hash = 59 * hash + (this.language != null ? this.language.hashCode() : 0);
        return hash;
    }
}
