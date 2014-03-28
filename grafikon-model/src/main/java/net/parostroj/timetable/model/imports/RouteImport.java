package net.parostroj.timetable.model.imports;

import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Route importer.
 *
 * @author jub
 */
public class RouteImport extends Import {

    public RouteImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        super(diagram, libraryDiagram, match);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId o) {
        return null;
    }

}
