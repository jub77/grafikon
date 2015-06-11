package net.parostroj.timetable.output2.template;

import java.io.OutputStream;
import java.util.Locale;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;

public interface TemplateWriter {

    void write(OutputStream stream, OutputParams params, TrainDiagram diagram, Locale locale) throws OutputException;
}
