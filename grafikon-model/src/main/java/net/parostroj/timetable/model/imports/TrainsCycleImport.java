package net.parostroj.timetable.model.imports;

import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Circulations import.
 *
 * @author jub
 */
public class TrainsCycleImport extends Import {

    public TrainsCycleImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        super(diagram, libraryDiagram, match);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId o) {
        return null;
    }

}
