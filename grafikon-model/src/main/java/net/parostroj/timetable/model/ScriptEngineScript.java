package net.parostroj.timetable.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

import java.util.concurrent.ExecutionException;
import javax.script.*;

/**
 * Groovy script.
 *
 * @author jub
 */
public final class ScriptEngineScript implements Script {

    private static final Logger log = LoggerFactory.getLogger(ScriptEngineScript.class);
    private static final Cache<String, CompiledScript> scriptCache = CacheBuilder.newBuilder().softValues().build();

    private static final Map<Language, String> LANGUAGE_MAPPING = Collections.singletonMap(Language.GROOVY, "groovy");

    private final String sourceCode;
    private final Language language;
    private CompiledScript script;

    ScriptEngineScript(String sourceCode, Language language) throws GrafikonException {
        this.sourceCode = sourceCode;
        this.language = language;
    }

    @Override
    public String getSourceCode() {
        return sourceCode;
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    private void initialize() throws GrafikonException {
        try {
            script = scriptCache.get(this.getSourceCode(), () -> {
                ScriptEngineManager manager = new ScriptEngineManager();
                String engineName = LANGUAGE_MAPPING.get(this.getLanguage());
                if (engineName == null) {
                    throw new IllegalArgumentException("Unknown script language");
                }
                ScriptEngine engine = manager.getEngineByName(engineName);
                Compilable cEngine = (Compilable) engine;
                return cEngine.compile(this.getSourceCode());
            });
        } catch (ExecutionException e) {
            throw new GrafikonException("Couldn't create script", e.getCause(), GrafikonException.Type.SCRIPT);
        }
    }

    @Override
    public Object evaluateWithException(Map<String, Object> binding) throws GrafikonException {
        try {
            if (script == null) {
                initialize();
            }
            return script.eval(new SimpleBindings(binding));
        } catch (ScriptException e) {
            throw new GrafikonException("Couldn't evaluate script: " + e.getMessage(), e, GrafikonException.Type.SCRIPT);
        }
    }

    @Override
    public Object evaluate(Map<String, Object> binding) {
        try {
            return this.evaluateWithException(binding);
        } catch (GrafikonException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s[%d]", getLanguage(), sourceCode != null ? sourceCode.length() : 0);
    }
}
