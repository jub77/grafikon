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
public abstract class OutputWithDiagramStream extends AbstractOutput {

    @Override
    public void write(OutputParams params) throws OutputException {
        OutputParamsUtil.checkParamsAnd(params, PARAM_TRAIN_DIAGRAM);
        OutputParamsUtil.checkParamsOr(params, PARAM_OUTPUT_FILE, PARAM_OUTPUT_STREAM);
        TrainDiagram diagram = (TrainDiagram) params.getParam(PARAM_TRAIN_DIAGRAM).getValue();
        if (params.paramExist(PARAM_OUTPUT_STREAM) && params.getParam(PARAM_OUTPUT_STREAM).getValue() != null) {
            OutputStream stream = (OutputStream) params.getParam(PARAM_OUTPUT_STREAM).getValue();
            testAndWrite(diagram, params, stream);
        }  else {
            File oFile = (File) params.getParam(PARAM_OUTPUT_FILE).getValue();
            try (OutputStream stream = new FileOutputStream(oFile)) {
                testAndWrite(diagram, params, stream);
            } catch (FileNotFoundException e) {
                throw new OutputException("Cannot open output file.", e);
            } catch (IOException e) {
                throw new OutputException("Error writing output", e);
            }
        }
    }

    private void testAndWrite(TrainDiagram diagram, OutputParams params, OutputStream stream) throws OutputException {
        if (diagram == null || stream == null) {
            throw new OutputException("Parameter cannot be null");
        }
        this.writeTo(params, stream, diagram);
    }

    @Override
    public OutputParams getAvailableParams() {
        return OutputParamsUtil.createParams(PARAM_OUTPUT_STREAM, PARAM_TRAIN_DIAGRAM);
    }

    protected String getEncoding(OutputParams params) {
        // default encoding -> utf-8
        String encoding = "utf-8";
        if (params.paramExistWithValue(PARAM_OUTPUT_ENCODING)) {
            encoding = params.getParam(PARAM_OUTPUT_ENCODING).getValue(String.class);
        }
        return encoding;
    }

    protected abstract void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException;
}
