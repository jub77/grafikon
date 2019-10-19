package net.parostroj.timetable.model;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.Script.Language;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.utils.Conversions;
import net.parostroj.timetable.utils.IdGenerator;

/**
 * Factory for creating train diagram.
 *
 * @author jub
 */
public class TrainDiagramFactory {

    private static final Logger log = LoggerFactory.getLogger(TrainDiagramFactory.class);

    private static final String TIME_SCRIPT = "time.groovy";

    private TrainDiagramFactory() {
    }

    public static TrainDiagramFactory newInstance() {
        return new TrainDiagramFactory();
    }

    public TrainDiagram createDiagram() {
        try {
            TextTemplate name = TextTemplate.createTextTemplate("${train.attributes['electric']?'E':''}${train.attributes['diesel']?'M':''}${type.abbr} ${train.number}${train.attributes['optional'] ? ' PP' : ''}", TextTemplate.Language.GROOVY);
            TextTemplate completeName= TextTemplate.createTextTemplate("${train.attributes['electric']?'E':''}${train.attributes['diesel']?'M':''}${type.abbr} ${train.number}${train.attributes['optional'] ? ' PP' : ''}${train.description != '' ? (' ' + train.description) : ''}", TextTemplate.Language.GROOVY);
            SortPattern sPattern = new SortPattern("(\\d*)(.*)");
            sPattern.getGroups().add(new SortPatternGroup(1, SortPatternGroup.Type.NUMBER));
            sPattern.getGroups().add(new SortPatternGroup(2, SortPatternGroup.Type.STRING));

            String scriptCode = "";
            try {
                scriptCode = Conversions.loadFile(getClass().getResourceAsStream(TIME_SCRIPT));
            } catch (IOException e) {
                log.warn("Error loading time script.");
            }
            Script timeScript = Script.createScript(scriptCode, Language.GROOVY);

            TrainDiagram diagram = new TrainDiagram(IdGenerator.getInstance().getId());
            diagram.getTrainsData().setTrainNameTemplate(name);
            diagram.getTrainsData().setTrainCompleteNameTemplate(completeName);
            diagram.getTrainsData().setTrainSortPattern(sPattern);
            diagram.getTrainsData().setRunningTimeScript(timeScript);

            diagram.setAttribute(TrainDiagram.ATTR_SCALE, Scale.getFromPredefined("H0"));
            diagram.setAttribute(TrainDiagram.ATTR_TIME_SCALE, 5.0d);
            diagram.setAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE, 14000);
            diagram.setAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE_EMPTY, 6000);
            diagram.setAttribute(TrainDiagram.ATTR_LENGTH_PER_AXLE, 4500);
            diagram.setAttribute(TrainDiagram.ATTR_LENGTH_UNIT, LengthUnit.AXLE);
            diagram.setAttribute(TrainDiagram.ATTR_STATION_TRANSFER_TIME, 10);
            diagram.setAttribute(TrainDiagram.ATTR_CHANGE_DIRECTION_STOP, 20 * 60);

            diagram.getCycleTypes().add(createDefaultTrainsCycleType(TrainsCycleType.DRIVER_CYCLE_KEY, diagram));
            diagram.getCycleTypes().add(createDefaultTrainsCycleType(TrainsCycleType.ENGINE_CYCLE_KEY, diagram));
            diagram.getCycleTypes().add(createDefaultTrainsCycleType(TrainsCycleType.TRAIN_UNIT_CYCLE_KEY, diagram));

            return diagram;
        } catch (GrafikonException e) {
            log.error("Error creating diagram.");
            return null;
        }
    }

    public static TrainsCycleType createDefaultTrainsCycleType(String key, TrainDiagram diagram) {
        if (!TrainsCycleType.isDefaultType(key)) {
            throw new IllegalArgumentException("No default key for circulation: " + key);
        }
        TrainsCycleType cycleType = new TrainsCycleType(IdGenerator.getInstance().getId(), diagram);
        cycleType.setKey(key);
        cycleType.setName(TrainsCycleType.getNameForDefaultType(key));
        return cycleType;
    }
}
