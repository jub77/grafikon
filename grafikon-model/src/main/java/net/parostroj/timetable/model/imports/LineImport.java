package net.parostroj.timetable.model.imports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.*;

/**
 * Importing lines.
 *
 * @author jub
 */
public class LineImport extends Import {

    private static final Logger log = LoggerFactory.getLogger(LineImport.class);

    public LineImport(TrainDiagram diagram, ImportMatch match, boolean overwrite) {
        super(diagram, match, overwrite);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId importedObject) {
        // check class
        if (!(importedObject instanceof Line)) {
            // skip other objects
            return null;
        }
        Line importedLine = (Line) importedObject;

        // check if the train already exist
        Line checkedLine = this.getLine(importedLine);
        if (checkedLine != null) {
            String message = "line already exists";
            this.addError(importedLine, message);
            log.debug("{}: {}", message, checkedLine);
            return null;
        }

        // create line
        Node iNodeFrom = this.getNode(importedLine.getFrom());
        Node iNodeTo = this.getNode(importedLine.getTo());

        if (iNodeFrom == null || iNodeTo == null) {
            String message = "nodes not found";
            this.addError(importedLine, message);
            log.debug("{}: {}", message, checkedLine);
            return null;
        }

        Line line = getDiagram().getPartFactory().createLine(this.getId(importedLine));
        line.setLength(importedLine.getLength());
        line.setTopSpeed(importedLine.getTopSpeed());
        line.getAttributes().add(this.importAttributes(importedLine.getAttributes()));

        // tracks
        for (LineTrack importedTrack : importedLine.getTracks()) {
            LineTrack track = new LineTrack(this.getId(importedTrack), line, importedTrack.getNumber());
            track.getAttributes().add(this.importAttributes(importedTrack.getAttributes()));
            line.getTracks().add(track);
        }

        // add to diagram
        this.getDiagram().getNet().addLine(line, iNodeFrom, iNodeTo);
        this.addImportedObject(line);
        log.trace("Successfully imported line: {}", line);
        return line;
    }

}
