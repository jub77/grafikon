package net.parostroj.timetable.model;

import java.util.Map;

/**
 * Script.
 *
 * @author jub
 */
public abstract class Script {

    private final String sourceCode;

    protected Script(String sourceCode) {
        this.sourceCode = sourceCode;
    }
    public String getSourceCode() {
        return sourceCode;
    }

    public abstract Language getLanguage();

    public abstract Object evaluate(Map<String, Object> binding);

    public static Script createScript(String sourceCode, Language language) {
        switch (language) {
            case GROOVY:
                return new ScriptGroovy(sourceCode);
            default:
                throw new IllegalArgumentException("No script for language available.");
        }
    }
}
