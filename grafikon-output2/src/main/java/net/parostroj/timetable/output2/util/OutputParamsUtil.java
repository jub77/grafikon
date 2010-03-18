package net.parostroj.timetable.output2.util;

import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;

/**
 * Utility class for creating output parameters instance.
 *
 * @author jub
 */
public class OutputParamsUtil {

    public static OutputParams createParams(String... names) {
        OutputParams outputParams = new OutputParams();
        for (String name : names) {
            outputParams.setParam(name, null);
        }
        return outputParams;
    }

    public static void checkParams(OutputParams params, String... names) throws OutputException {
        for (String name : names) {
            if (!params.paramExist(name)) {
                throw new OutputException("Missing parameter: " + name);
            }
        }
    }
}
