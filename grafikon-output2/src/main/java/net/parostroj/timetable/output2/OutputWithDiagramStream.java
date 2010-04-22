package net.parostroj.timetable.output2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
        OutputParamsUtil.checkParamsAnd(params, DefaultOutputParam.TRAIN_DIAGRAM);
        OutputParamsUtil.checkParamsOr(params, DefaultOutputParam.OUTPUT_FILE, DefaultOutputParam.OUTPUT_STREAM);
        TrainDiagram diagram = (TrainDiagram) params.getParam(DefaultOutputParam.TRAIN_DIAGRAM).getValue();
        OutputStream stream = null;
        if (params.paramExist(DefaultOutputParam.OUTPUT_STREAM) && params.getParam(DefaultOutputParam.OUTPUT_STREAM).getValue() != null)
            stream = (OutputStream) params.getParam(DefaultOutputParam.OUTPUT_STREAM).getValue();
        else {
            File oFile = (File) params.getParam(DefaultOutputParam.OUTPUT_FILE).getValue();
            try {
                stream = new FileOutputStream(oFile);
            } catch (FileNotFoundException e) {
                throw new OutputException("Cannot open output file.", e);
            }
        }
        if (diagram == null || stream == null) {
            throw new OutputException("Parameter cannot be null");
        }
        this.writeTo(params, stream, diagram);
        // close stream if created
        if (params.paramExist(DefaultOutputParam.OUTPUT_FILE))
            try {
                stream.close();
            } catch (IOException e) {
                throw new OutputException("Couldn't close stream.", e);
            }
    }

    @Override
    public OutputParams getAvailableParams() {
        return OutputParamsUtil.createParams(DefaultOutputParam.OUTPUT_STREAM, DefaultOutputParam.TRAIN_DIAGRAM);
    }

    protected abstract void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException;
}
