package net.parostroj.timetable.output2.util;

import java.util.Arrays;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;

/**
 * Utility class for creating output parameters instance.
 *
 * @author jub
 */
public final class OutputParamsUtil {

    private OutputParamsUtil() {}

    public static OutputParams createParams(String... names) {
        OutputParams outputParams = new OutputParams();
        for (String name : names) {
            outputParams.setParam(name, null);
        }
        return outputParams;
    }

    public static void checkParamsAnd(OutputParams params, String... names) throws OutputException {
        for (String name : names) {
            if (!params.paramExist(name)) {
                throw new OutputException("Missing parameter: " + name);
            }
        }
    }

    public static void checkParamsOr(OutputParams params, String... names) throws OutputException {
        boolean found = false;
        for (String name : names) {
            if (params.paramExist(name)) {
                found = true;
                break;
            }
        }
        if (!found)
            throw new OutputException("No parameter found: " + Arrays.toString(names));
    }
}
