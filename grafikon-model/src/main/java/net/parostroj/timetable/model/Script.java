package net.parostroj.timetable.model;

import java.util.Map;

/**
 * Script.
 *
 * @author jub
 */
public interface Script {

    enum Language {
        GROOVY;

        public static Language fromString(String str) {
            for (Language language : values()) {
                if (language.name().equals(str)) {
                    return language;
                }
            }
            return null;
        }
    }

    String getSourceCode();

    Language getLanguage();

    Object evaluate(Map<String, Object> binding);

    default Object evaluateWithException(Map<String, Object> binding) throws GrafikonException {
        return evaluate(binding);
    }

    static Script create(String sourceCode, Language language) {
        if (language == Language.GROOVY) {
            return new ScriptEngineScript(sourceCode, language);
        }
        throw new IllegalArgumentException("No script for language available.");
    }
}
