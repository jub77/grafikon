package net.parostroj.timetable.output2;

import java.io.OutputStream;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.util.OutputParamsUtil;

/**
 * Output with two parameters - train diagram and output stream.
 *
 * @author jub
 */
abstract public class OutputWithDiagramStream extends AbstractOutput {

    @Override
    public void write(OutputParams params) throws OutputException {
        OutputParamsUtil.checkParams(params, DefaultOutputParam.OUTPUT_STREAM, DefaultOutputParam.TRAIN_DIAGRAM);
        TrainDiagram diagram = (TrainDiagram) params.getParam(DefaultOutputParam.TRAIN_DIAGRAM).getValue();
        OutputStream stream = (OutputStream) params.getParam(DefaultOutputParam.OUTPUT_STREAM).getValue();
        if (diagram == null || stream == null) {
            throw new OutputException("Parameter cannot be null");
        }
        this.writeTo(stream, diagram);
    }

    @Override
    public OutputParams getParams() {
        return OutputParamsUtil.createParams(DefaultOutputParam.OUTPUT_STREAM, DefaultOutputParam.TRAIN_DIAGRAM);
    }

    protected abstract void writeTo(OutputStream stream, TrainDiagram diagram) throws OutputException;
}
