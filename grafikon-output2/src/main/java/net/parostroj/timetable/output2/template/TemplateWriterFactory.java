package net.parostroj.timetable.output2.template;

import net.parostroj.timetable.output2.OutputException;

public interface TemplateWriterFactory {

    TemplateWriter get() throws OutputException;
}
