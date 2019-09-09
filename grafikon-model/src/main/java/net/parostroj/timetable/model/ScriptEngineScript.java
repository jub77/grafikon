package net.parostroj.timetable.model;

import java.util.Collections;
import java.util.Map;

import javax.script.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Groovy script.
 *
 * @author jub
 */
public final class ScriptEngineScript extends Script {

    static final Logger log = LoggerFactory.getLogger(ScriptEngineScript.class);

    private static final Map<Language, String> LANGUAGE_MAPPING = Collections
            .singletonMap(Language.GROOVY, "groovy");

    private CompiledScript script;

    protected ScriptEngineScript(String sourceCode, Language language, boolean initialize) throws GrafikonException {
        super(sourceCode, language);
        if (initialize)
            initialize();
    }

    private void initialize() throws GrafikonException {
        ScriptEngineManager manager = new ScriptEngineManager();
        String engineName = LANGUAGE_MAPPING.get(this.getLanguage());
        if (engineName == null) {
            throw new GrafikonException("Unknown script language", GrafikonException.Type.SCRIPT);
        }
        ScriptEngine engine = manager.getEngineByName(engineName);
        Compilable cEngine = (Compilable) engine;
        try {
            script = cEngine.compile(this.getSourceCode());
        } catch (ScriptException e) {
            throw new GrafikonException("Couldn't create script", e, GrafikonException.Type.SCRIPT);
        }
    }

    @Override
    public Object evaluateWithException(Map<String, Object> binding) throws GrafikonException {
        try {
            if (script == null)
                initialize();
            return script.eval(new SimpleBindings(binding));
        } catch (ScriptException e) {
            throw new GrafikonException("Couldn't evaluate script: " + e.getMessage(), e, GrafikonException.Type.SCRIPT);
        }
    }

    @Override
    public void freeResources() {
        script = null;
    }
}
