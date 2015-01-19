package net.parostroj.timetable.actions.scripts;

import java.util.Map;

import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.Script;
import net.parostroj.timetable.model.TrainDiagram;

public interface ScriptAction {

    String getId();

    String getName();

    Script getScript();

    String getLocalizedName();

    void execute(TrainDiagram diagram) throws GrafikonException;

    void execute(TrainDiagram diagram, Map<String, Object> binding) throws GrafikonException;

}