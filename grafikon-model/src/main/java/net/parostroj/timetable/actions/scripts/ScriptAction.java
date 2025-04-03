package net.parostroj.timetable.actions.scripts;

import java.util.Map;

import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TranslatedString;

public interface ScriptAction {

    String getId();

    TranslatedString getName();

    void execute(TrainDiagram diagram) throws GrafikonException;

    void execute(TrainDiagram diagram, Map<String, Object> binding) throws GrafikonException;

}
