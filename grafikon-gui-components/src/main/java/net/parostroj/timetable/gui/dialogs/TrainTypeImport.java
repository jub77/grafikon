package net.parostroj.timetable.gui.dialogs;

import java.util.logging.Logger;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;

/**
 * Imports train types.
 *
 * @author jub
 */
public class TrainTypeImport extends Import {

    private static final Logger LOG = Logger.getLogger(TrainTypeImport.class.getName());

    public TrainTypeImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        super(diagram, libraryDiagram, match);
    }

    @Override
    protected void importObjectImpl(Object o) {
        // check class
        if (!(o instanceof TrainType))
            return;
        TrainType importedType = (TrainType)o;

        // check existence
        TrainType checkedType = this.getTrainType(importedType);
        if (checkedType != null) {
            String message = "Train type already exists: " + checkedType;
            this.addError(importedType, message);
            LOG.finer(message);
            return;
        }

        // create new type
        TrainType type = getDiagram().createTrainType(this.getId(importedType));
        type.setAbbr(importedType.getAbbr());
        type.setColor(importedType.getColor());
        type.setDesc(importedType.getDesc());
        type.setPlatform(importedType.isPlatform());
        type.setCategory(importedType.getCategory());
        type.setTrainCompleteNameTemplate(importedType.getTrainCompleteNameTemplate());
        type.setTrainNameTemplate(importedType.getTrainNameTemplate());

        // add to diagram
        this.getDiagram().addTrainType(type);
        this.addImportedObject(type);
        LOG.finer("Successfully imported type: " + type);
    }
}
