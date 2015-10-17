package net.parostroj.timetable.model.imports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.*;

/**
 * Import for trains cycle type.
 *
 * @author jub
 */
public class TrainsCycleTypeImport extends Import {

    private static final Logger log = LoggerFactory.getLogger(TrainsCycleTypeImport.class);

    public TrainsCycleTypeImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        super(diagram, libraryDiagram, match);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId importedObject) {
        // check class
        if (!(importedObject instanceof TrainsCycleType)) {
            // skip other objects
            return null;
        }
        TrainsCycleType importedCycleType = (TrainsCycleType) importedObject;

        // check if cycle type already exist
        TrainsCycleType checkedCycleType = this.getCycleType(importedCycleType);
        if (checkedCycleType != null) {
            String message = "circulation type already exists";
            this.addError(importedCycleType, message);
            log.debug("{}: {}", message, checkedCycleType);
            return null;
        }

        TrainsCycleType type = new TrainsCycleType(this.getId(importedCycleType), this.getDiagram());
        type.setName(importedCycleType.getName());
        type.setDescription(importedCycleType.getDescription());
        type.getAttributes().add(this.importAttributes(importedCycleType.getAttributes()));

        this.getDiagram().getCycleTypes().add(type);
        this.addImportedObject(type);
        log.trace("Successfully imported circulation type: " + type);
        return type;
    }

}
