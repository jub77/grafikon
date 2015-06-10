package net.parostroj.timetable.output2.template;

import java.util.Locale;
import java.util.Map;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;

public interface TemplateBindingHandler {

    Map<String, Object> get(TrainDiagram diagram, OutputParams params, Locale locale) throws OutputException;

    void postProcess(TrainDiagram diagram, OutputParams params, Map<String, Object> binding) throws OutputException;
}
