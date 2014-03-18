package net.parostroj.timetable.gui.dialogs;

import java.util.Map;
import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.WeightTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Import engine classes.
 *
 * @author jub
 */
public class EngineClassImport extends Import {

    private static final Logger LOG = LoggerFactory.getLogger(EngineClassImport.class.getName());

    public EngineClassImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        super(diagram, libraryDiagram, match);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId o) {
        // check class
        if (!(o instanceof EngineClass))
            return null;
        EngineClass importedEngineClass = (EngineClass)o;

        // check existence
        EngineClass checkedEngineClass = this.getEngineClass(importedEngineClass);
        if (checkedEngineClass != null) {
            String message = "engine class already exists";
            this.addError(importedEngineClass, message);
            LOG.debug("{}: {}", message, checkedEngineClass);
            return null;
        }

        // create new engine class
        EngineClass engineClass = new EngineClass(this.getId(importedEngineClass), importedEngineClass.getName());

        // process weight rows
        for (WeightTableRow impRow : importedEngineClass.getWeightTable()) {
            WeightTableRow row = engineClass.createWeightTableRow(impRow.getSpeed());
            for (Map.Entry<LineClass, Integer> impEntry : impRow.getWeights().entrySet()) {
                LineClass lineClass = this.getLineClass(impEntry.getKey());
                if (lineClass == null) {
                    String message = "line class missing: " + impEntry.getKey().getName();
                    this.addError(importedEngineClass, message);
                    LOG.debug(message);
                    return null;
                }
                row.setWeightInfo(lineClass, impEntry.getValue());
            }
            engineClass.addWeightTableRow(row);
        }

        // add to diagram
        this.getDiagram().addEngineClass(engineClass);
        this.addImportedObject(engineClass);
        LOG.trace("Successfully imported engine class: " + engineClass);
        return engineClass;
    }
}
