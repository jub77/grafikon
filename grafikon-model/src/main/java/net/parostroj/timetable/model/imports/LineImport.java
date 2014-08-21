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

    public LineImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        super(diagram, libraryDiagram, match);
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

        Line line = getDiagram().createLine(this.getId(importedLine), importedLine.getLength(), iNodeFrom, iNodeTo, importedLine.getTopSpeed());
        line.setAttributes(this.importAttributes(importedLine.getAttributes()));

        // tracks
        for (LineTrack importedTrack : importedLine.getTracks()) {
            LineTrack track = new LineTrack(this.getId(importedTrack), importedTrack.getNumber());
            track.setAttributes(this.importAttributes(importedTrack.getAttributes()));
            line.addTrack(track);
        }

        // add to diagram
        this.getDiagram().getNet().addLine(iNodeFrom, iNodeTo, line);
        this.addImportedObject(line);
        log.trace("Successfully imported line: " + line);
        return line;
    }

}
