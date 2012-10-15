package net.parostroj.timetable.output2;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract output.
 *
 * @author jub
 */
public abstract class AbstractOutput implements Output {
    
    @Override
    public void write(OutputParam... params) throws OutputException {
        this.write(Arrays.asList(params));
    }

    @Override
    public void write(List<OutputParam> params) throws OutputException {
        OutputParams paramMap = new OutputParams();
        for (OutputParam param : params) {
            paramMap.setParam(param.getName(), param);
        }
        this.write(paramMap);
    }

    @Override
    public OutputParams getAvailableParams() {
        return null;
    }
}
