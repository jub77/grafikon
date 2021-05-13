package net.parostroj.timetable.model.imports;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.*;

/**
 * Route importer.
 *
 * @author jub
 */
public class RouteImport extends Import {

    private static final Logger log = LoggerFactory.getLogger(RouteImport.class);

    public RouteImport(TrainDiagram diagram, ImportMatch match, boolean overwrite) {
        super(diagram, match, overwrite);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId importedObject) {
        // check class
        if (!(importedObject instanceof Route)) {
            // skip other objects
            return null;
        }
        Route importedRoute = (Route) importedObject;

        // check if the route already exist
        Route checkedRoute = this.getRoute(importedRoute);
        if (checkedRoute != null) {
            String message = "route already exists";
            this.addError(importedRoute, message);
            log.debug("{}: {}", message, checkedRoute);
            return null;
        }

        // create route
        NetSegment<?>[] segments = getSegments(importedRoute);
        if (segments == null) {
            String message = "nodes missing";
            this.addError(importedRoute, message);
            log.debug("{}: {}", message, importedRoute);
            return null;
        }

        Route route = new Route(this.getId(importedRoute), getDiagram(), segments);
        route.getAttributes().add(this.importAttributes(importedRoute.getAttributes()));

        // add to diagram
        this.getDiagram().getRoutes().add(route);
        this.addImportedObject(route);
        log.trace("Successfully imported route: {}", route);
        return route;
    }

    private NetSegment<?>[] getSegments(Route oRoute) {
        List<NetSegment<?>> result = new LinkedList<>();
        for (RouteSegment oSeg : oRoute.getSegments()) {
            if (oSeg instanceof Line) {
                result.add(this.getLine((Line) oSeg));
            } else if (oSeg instanceof Node) {
                result.add(this.getNode((Node) oSeg));
            }
        }
        return result.toArray(new NetSegment[0]);
    }
}
