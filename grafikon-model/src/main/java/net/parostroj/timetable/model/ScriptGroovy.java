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
public final class ScriptGroovy extends Script {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptGroovy.class);
    
    private final CompiledScript script;

    protected ScriptGroovy(String sourceCode) throws GrafikonException {
        super(sourceCode);
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("groovy");
        Compilable cEngine = (Compilable) engine;
        try {
            script = cEngine.compile(sourceCode);
        } catch (ScriptException e) {
            throw new GrafikonException("Couldn't create template.", e, GrafikonException.Type.SCRIPT);
        }
    }

    @Override
    public Language getLanguage() {
        return Language.GROOVY;
    }

    @Override
    public Object evaluate(Map<String, Object> binding) {
        try {
            return this.evaluateWithException(binding);
        } catch (GrafikonException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Object evaluateWithException(Map<String, Object> binding) throws GrafikonException {
        try {
            return script.eval(new SimpleBindings(binding));
        } catch (ScriptException e) {
            throw new GrafikonException("Couldn't evaluate script.", e, GrafikonException.Type.SCRIPT);
        }
    }
}
