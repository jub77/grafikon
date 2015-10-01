package net.parostroj.timetable.model.imports;

import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.Group;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Imports groups.
 *
 * @author jub
 */
public class GroupImport extends Import {

    private static final Logger log = LoggerFactory.getLogger(GroupImport.class);

    public GroupImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        super(diagram, libraryDiagram, match);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId o) {
        // check class
        if (!(o instanceof Group))
            return null;
        Group importedGroup = (Group)o;

        // check existence
        Group checkedGroup = this.getGroup(importedGroup);
        if (checkedGroup != null) {
            String message = "group already exists";
            this.addError(importedGroup, message);
            log.debug("{}: {}", message, checkedGroup);
            return null;
        }

        // create new group
        Group group = getDiagram().createGroup(this.getId(importedGroup));
        group.getAttributes().add(this.importAttributes(importedGroup.getAttributes()));

        // add to diagram
        this.getDiagram().getGroups().add(group);
        this.addImportedObject(group);
        log.trace("Successfully imported group: " + group);
        return group;
    }
}
