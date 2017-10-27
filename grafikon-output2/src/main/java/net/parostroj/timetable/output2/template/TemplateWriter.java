package net.parostroj.timetable.output2.template;

import java.io.OutputStream;
import java.util.Locale;

import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;

@FunctionalInterface
public interface TemplateWriter {

    void write(OutputStream stream, OutputParams params, Locale locale) throws OutputException;
}
