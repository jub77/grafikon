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

    public RouteImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        super(diagram, libraryDiagram, match);
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
        RouteSegment[] segments = getSegments(importedRoute);
        if (segments == null) {
            String message = "nodes missing";
            this.addError(importedRoute, message);
            log.debug("{}: {}", message, importedRoute);
            return null;
        }

        Route route = new Route(this.getId(importedRoute), getDiagram(), importedRoute.getName(), segments);
        route.setNetPart(importedRoute.isNetPart());
        route.setTrainRoute(importedRoute.isTrainRoute());

        // add to diagram
        this.getDiagram().addRoute(route);
        this.addImportedObject(route);
        log.trace("Successfully imported route: " + route);
        return route;
    }

    public RouteSegment[] getSegments(Route oRoute) {
        List<RouteSegment> result = new LinkedList<RouteSegment>();
        for (RouteSegment oSeg : oRoute.getSegments()) {
            if (oSeg.isLine()) {
                result.add(this.getLine(oSeg.asLine()));
            } else if (oSeg.isNode()) {
                result.add(this.getNode(oSeg.asNode()));
            }
        }
        return result.toArray(new RouteSegment[0]);
    }
}
