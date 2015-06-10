package net.parostroj.timetable.output2.groovy;

import java.util.Locale;
import java.util.Map;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.util.ResourceHelper;

public class DiagramTemplateBinding extends GroovyTemplateBinding {

    @Override
    protected void addSpecific(OutputParams params, Map<String, Object> map, TrainDiagram diagram, Locale locale) {
        map.put(TRANSLATOR, ResourceHelper.getTranslator(LOCALIZATION_BUNDLE, diagram));
    }
}
