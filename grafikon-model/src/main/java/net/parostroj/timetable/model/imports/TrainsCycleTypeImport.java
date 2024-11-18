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

    public TrainsCycleTypeImport(TrainDiagram diagram, ImportMatch match, boolean overwrite) {
        super(diagram, match, overwrite);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId importedObject) {
        // check class
        if (!(importedObject instanceof TrainsCycleType importedCycleType)) {
            // skip other objects
            return null;
        }

        // check if cycle type already exist
        TrainsCycleType checkedCycleType = this.getCycleType(importedCycleType);
        if (checkedCycleType != null) {
            if (overwrite) {
                this.getDiagram().getCycleTypes().remove(checkedCycleType);
            } else {
                String message = "circulation type already exists";
                this.addError(importedCycleType, message);
                log.debug("{}: {}", message, checkedCycleType);
                return null;
            }
        }

        TrainsCycleType type = new TrainsCycleType(this.getId(importedCycleType), this.getDiagram());
        type.setKey(importedCycleType.getKey());
        type.setName(importedCycleType.getName());
        type.getAttributes().add(this.importAttributes(importedCycleType.getAttributes()));

        this.getDiagram().getCycleTypes().add(type);
        this.addImportedObject(type);
        log.trace("Successfully imported circulation type: {}", type);
        return type;
    }

}
