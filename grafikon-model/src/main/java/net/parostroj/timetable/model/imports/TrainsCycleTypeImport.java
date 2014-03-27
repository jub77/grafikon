package net.parostroj.timetable.model.imports;

import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Import for trains cycle type.
 *
 * @author jub
 */
public class TrainsCycleTypeImport extends Import {

    public TrainsCycleTypeImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        super(diagram, libraryDiagram, match);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId o) {
        return null;
    }

}
