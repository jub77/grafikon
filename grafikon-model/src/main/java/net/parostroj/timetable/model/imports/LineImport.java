package net.parostroj.timetable.model.imports;

import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Importing lines.
 *
 * @author jub
 */
public class LineImport extends Import {

    public LineImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        super(diagram, libraryDiagram, match);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId o) {
        return null;
    }

}
