package net.parostroj.timetable.gui.dialogs;

import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.TrainDiagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Import line classes.
 *
 * @author jub
 */
public class LineClassImport extends Import {

    private static final Logger LOG = LoggerFactory.getLogger(LineClassImport.class.getName());

    public LineClassImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        super(diagram, libraryDiagram, match);
    }

    @Override
    protected void importObjectImpl(Object o) {
        // check class
        if (!(o instanceof LineClass))
            return;
        LineClass importedLineClass = (LineClass)o;

        // check existence
        LineClass checkedLineClass = this.getLineClass(importedLineClass);
        if (checkedLineClass != null) {
            String message = "Line class already exists: " + checkedLineClass;
            this.addError(importedLineClass, message);
            LOG.trace(message);
            return;
        }

        // create new line class
        LineClass lineClass = new LineClass(this.getId(importedLineClass), importedLineClass.getName());

        // add to diagram
        this.getDiagram().getNet().addLineClass(lineClass);
        this.addImportedObject(lineClass);
        LOG.trace("Successfully imported line class: " + lineClass);
    }
}
