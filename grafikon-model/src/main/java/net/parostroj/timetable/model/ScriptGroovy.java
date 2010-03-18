package net.parostroj.timetable.model;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.util.Map;

/**
 * Groovy script.
 *
 * @author jub
 */
public final class ScriptGroovy extends Script {

    private final groovy.lang.Script groovyScript;

    protected ScriptGroovy(String sourceCode) {
        super(sourceCode);
        groovyScript = new GroovyShell().parse(getSourceCode());
    }

    @Override
    public Language getLanguage() {
        return Language.GROOVY;
    }

    @Override
    public Object evaluate(Map<String, Object> binding) {
        groovyScript.setBinding(new Binding(binding));
        return groovyScript.run();
    }

}
