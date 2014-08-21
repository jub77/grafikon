package net.parostroj.timetable.model;

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

    private static final Logger log = LoggerFactory.getLogger(ScriptEngineScript.class);

    private CompiledScript script;

    protected ScriptEngineScript(String sourceCode, Language language, boolean initialize) throws GrafikonException {
        super(sourceCode, language);
        if (initialize)
            initialize();
    }

    private void initialize() throws GrafikonException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName(this.getLanguage() == Language.GROOVY ? "groovy" : "javascript");
        Compilable cEngine = (Compilable) engine;
        try {
            script = cEngine.compile(this.getSourceCode());
        } catch (ScriptException e) {
            throw new GrafikonException("Couldn't create script.", e, GrafikonException.Type.SCRIPT);
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
