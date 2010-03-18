package net.parostroj.timetable.output2.html;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.DefaultOutputParam;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.util.OutputParamsUtil;

/**
 * Output with two parameters - train diagram and output stream.
 *
 * @author jub
 */
abstract class OutputWithDiagramAndStream extends OutputWithLocale {

    public OutputWithDiagramAndStream(Locale locale) {
        super(locale);
    }

    @Override
    public void write(OutputParams params) throws OutputException {
        OutputParamsUtil.checkParams(params, DefaultOutputParam.OUTPUT_STREAM, DefaultOutputParam.TRAIN_DIAGRAM);
        TrainDiagram diagram = (TrainDiagram) params.getParam(DefaultOutputParam.TRAIN_DIAGRAM).getValue();
        OutputStream stream = (OutputStream) params.getParam(DefaultOutputParam.OUTPUT_STREAM).getValue();
        if (diagram == null || stream == null) {
            throw new OutputException("Parameter cannot be null");
        }
        try {
            this.writeTo(stream, diagram);
        } catch (IOException e) {
            throw new OutputException(e);
        }
    }

    @Override
    public OutputParams getParams() {
        return OutputParamsUtil.createParams(DefaultOutputParam.OUTPUT_STREAM, DefaultOutputParam.TRAIN_DIAGRAM);
    }

    protected abstract void writeTo(OutputStream stream, TrainDiagram diagram) throws IOException;
}
