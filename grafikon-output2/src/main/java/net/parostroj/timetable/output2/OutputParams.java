package net.parostroj.timetable.output2;

import java.util.HashMap;

/**
 * Map of parameters.
 *
 * @author jub
 */
public class OutputParams extends HashMap<String, OutputParam> {

    public OutputParams setParam(String name, Object value) {
        OutputParam param = this.get(name);
        if (param == null) {
            param = new OutputParam(name, value);
        }
        this.put(name, param);
        return this;
    }

    public OutputParams removeParam(String name) {
        this.remove(name);
        return this;
    }

    public OutputParams setParam(OutputParam param) {
        this.put(param.getName(), param);
        return this;
    }

    public OutputParam getParam(String name) {
        return this.get(name);
    }

    public boolean paramExist(String name) {
        return this.containsKey(name);
    }
}
