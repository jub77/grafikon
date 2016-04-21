package net.parostroj.timetable.output2;

import java.util.List;

/**
 * Output.
 *
 * @author jub
 */
public interface Output {

    public static final String LOCALIZATION_BUNDLE = "texts/output";

    public static final String PARAM_TRAIN_DIAGRAM = "diagram";
    public static final String PARAM_OUTPUT_STREAM = "output.stream";
    public static final String PARAM_OUTPUT_FILE = "output.file";
    public static final String PARAM_TEMPLATE = "template";
    public static final String PARAM_TEXT_TEMPLATE = "text.template";
    public static final String PARAM_CONTEXT = "context";
    public static final String PARAM_SETTINGS = "settings";
    public static final String PARAM_OUTPUT_ENCODING = "encoding";
    public static final String PARAM_RESOURCES = "resources";

    public void write(OutputParam... params) throws OutputException;

    public void write(List<OutputParam> params) throws OutputException;

    public void write(OutputParams params) throws OutputException;

    public OutputParams getAvailableParams();
}
