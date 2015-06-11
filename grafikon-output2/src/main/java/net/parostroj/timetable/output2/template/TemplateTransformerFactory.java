package net.parostroj.timetable.output2.template;

import net.parostroj.timetable.output2.OutputException;

public interface TemplateTransformerFactory {

    TemplateTransformer get() throws OutputException;
}
