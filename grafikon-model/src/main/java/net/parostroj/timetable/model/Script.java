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
public interface Script {

    enum Language {
        GROOVY
    }

    String getSourceCode();

    Language getLanguage();

    Object evaluate(Map<String, Object> binding);

    default Object evaluateWithException(Map<String, Object> binding) throws GrafikonException {
        return evaluate(binding);
    }

    static Script createScript(String sourceCode, Language language) throws GrafikonException {
        if (language == Language.GROOVY) {
            return new ScriptEngineScript(sourceCode, language);
        }
        throw new IllegalArgumentException("No script for language available.");
    }
}
