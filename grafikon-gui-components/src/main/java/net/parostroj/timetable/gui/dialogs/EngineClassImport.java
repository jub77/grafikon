package net.parostroj.timetable.gui.dialogs;

import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.TrainDiagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Import engine classes.
 *
 * @author jub
 */
public class EngineClassImport extends Import<EngineClass> {

    private static final Logger LOG = LoggerFactory.getLogger(EngineClassImport.class.getName());

    public EngineClassImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        super(diagram, libraryDiagram, match);
    }

    @Override
    protected void importObjectImpl(EngineClass o) {
        // check class
        if (!(o instanceof EngineClass))
            return;
        EngineClass importedEngineClass = (EngineClass)o;

        // check existence
        EngineClass checkedEngineClass = this.getEngineClass(importedEngineClass);
        if (checkedEngineClass != null) {
            String message = "Engine class already exists: " + checkedEngineClass;
            this.addError(importedEngineClass, message);
            LOG.trace(message);
            return;
        }

        // create new engine class
        EngineClass engineClass = new EngineClass(this.getId(importedEngineClass), importedEngineClass.getName());

        // add to diagram
        this.getDiagram().addEngineClass(engineClass);
        this.addImportedObject(engineClass);
        LOG.trace("Successfully imported engine class: " + engineClass);
    }
}
