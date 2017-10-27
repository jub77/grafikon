package net.parostroj.timetable.output2.template;

import java.io.InputStream;
import java.io.OutputStream;

import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;

@FunctionalInterface
public interface TemplateTransformer {

    void process(InputStream is, OutputStream os, OutputParams params) throws OutputException;
}
