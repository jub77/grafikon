package net.parostroj.timetable.output2;

import java.util.List;

/**
 * Output.
 *
 * @author jub
 */
public interface Output {

    String PARAM_TRAIN_DIAGRAM = "diagram";
    String PARAM_OUTPUT_STREAM = "output.stream";
    String PARAM_OUTPUT_FILE = "output.file";
    String PARAM_TEXT_TEMPLATE = "text.template";
    String PARAM_CONTEXT = "context";
    String PARAM_OUTPUT_ENCODING = "encoding";
    String PARAM_RESOURCES = "resources";

    void write(OutputParam... params) throws OutputException;

    void write(List<OutputParam> params) throws OutputException;

    void write(OutputParams params) throws OutputException;

    OutputParams getAvailableParams();
}
