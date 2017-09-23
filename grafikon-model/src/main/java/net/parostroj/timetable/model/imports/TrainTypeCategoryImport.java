package net.parostroj.timetable.model.imports;

import net.parostroj.timetable.model.CopyFactory;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainTypeCategory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Import train type category.
 *
 * @author jub
 */
public class TrainTypeCategoryImport extends Import {

    private static final Logger log = LoggerFactory.getLogger(TrainTypeCategoryImport.class);

    private final CopyFactory copyFactory;

    public TrainTypeCategoryImport(TrainDiagram diagram, ImportMatch match, boolean overwrite) {
        super(diagram, match, overwrite);
        this.copyFactory = new CopyFactory(diagram.getPartFactory());
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId o) {
        // check class
        if (!(o instanceof TrainTypeCategory))
            return null;
        TrainTypeCategory importedCategory = (TrainTypeCategory) o;

        // check existence
        TrainTypeCategory checkedCategory = this.getTrainTypeCategory(importedCategory);
        if (checkedCategory != null) {
            String message = "train type category already exists";
            this.addError(importedCategory, message);
            log.debug("{}: {}", message, checkedCategory);
            return null;
        }

        // create new category
        TrainTypeCategory  category = copyFactory.copy(importedCategory, this.getId(importedCategory));

        // add to diagram
        this.getDiagram().getTrainTypeCategories().add(category);
        this.addImportedObject(category);
        log.trace("Successfully imported train type category: {}", category);
        return category;
    }
}
