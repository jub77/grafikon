package net.parostroj.timetable.model.imports;

import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainTypeCategory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Imports train types.
 *
 * @author jub
 */
public class TrainTypeImport extends Import {

    private static final Logger log = LoggerFactory.getLogger(TrainTypeImport.class);

    public TrainTypeImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        super(diagram, libraryDiagram, match);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId o) {
        // check class
        if (!(o instanceof TrainType))
            return null;
        TrainType importedType = (TrainType)o;

        // check existence
        TrainType checkedType = this.getTrainType(importedType);
        if (checkedType != null) {
            String message = "train type already exists";
            this.addError(importedType, message);
            log.debug("{}: {}", message, checkedType);
            return null;
        }

        // get category
        TrainTypeCategory checkedCategory = this.getTrainTypeCategory(importedType.getCategory());
        if (checkedCategory == null) {
            String message = "category missing: " + importedType.getCategory();
            this.addError(importedType, message);
            log.debug(message);
            return null;
        }

        // create new type
        TrainType type = getDiagram().createTrainType(this.getId(importedType));
        type.setAbbr(importedType.getAbbr());
        type.setColor(importedType.getColor());
        type.setDesc(importedType.getDesc());
        type.setPlatform(importedType.isPlatform());
        type.setCategory(checkedCategory);
        type.setTrainCompleteNameTemplate(importedType.getTrainCompleteNameTemplate());
        type.setTrainNameTemplate(importedType.getTrainNameTemplate());

        // add to diagram
        this.getDiagram().getTrainTypes().add(type);
        this.addImportedObject(type);
        log.trace("Successfully imported type: " + type);
        return type;
    }
}
