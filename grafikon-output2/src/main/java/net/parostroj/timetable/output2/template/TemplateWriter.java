package net.parostroj.timetable.output2.template;

import java.io.OutputStream;
import java.util.Map;

import net.parostroj.timetable.output2.OutputException;

public interface TemplateWriter {

    void write(OutputStream stream, Map<String, Object> binding) throws OutputException;
}
