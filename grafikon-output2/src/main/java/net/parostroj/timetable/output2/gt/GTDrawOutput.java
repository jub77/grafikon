package net.parostroj.timetable.output2.gt;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;

/**
 * GTDraw output.
 *
 * @author jub
 */
public class GTDrawOutput extends DrawOutput {

    private final GTDrawFactory drawFactory;

    public GTDrawOutput(Locale locale) {
        super(locale);
        drawFactory = new NormalGTDrawFactory();
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        GTDraw draw = this.getDraw(params);
        if (draw == null) {
            Route route = this.getRoute(params, diagram);
            GTDrawParams gtParams = this.getParams(params);
            draw = drawFactory.createInstance(gtParams.getType(), gtParams.getSettings(), route, new GTStorage());
        }
        this.draw(this.getFileOutputType(params), stream, draw);
    }

    private GTDraw getDraw(OutputParams params) {
        Collection<?> draws = params.getParamValue(GT_DRAWS, Collection.class);
        if (draws != null && !draws.isEmpty()) {
            return (GTDraw) draws.iterator().next();
        } else {
            return null;
        }
    }

    private GTDrawParams getParams(OutputParams params) {
        GTDrawParams gtParams = params.getParamValue(GT_PARAMS, GTDrawParams.class);
        if (gtParams == null) {
            // create default values
            gtParams = new GTDrawParams();
        }
        return gtParams;
    }

    private Route getRoute(OutputParams params, TrainDiagram diagram) throws OutputException {
        Collection<?> routes = params.getParamValue(ROUTES_PARAM, Collection.class);
        // only one route supported currently
        Route route = null;
        if (routes == null || routes.isEmpty()) {
            List<Route> diagramRoutes = diagram.getRoutes();
            if (diagramRoutes.isEmpty()) {
                throw new OutputException("Routes missing");
            }
            route = diagramRoutes.get(0);
        } else {
            route = (Route) routes.iterator().next();
        }
        return route;
    }

    private void draw(FileOutputType outputType, OutputStream stream, final GTDraw draw) throws OutputException {
        this.draw(new Image() {

            @Override
            public Dimension getSize(Graphics2D g) {
                return draw.getSize();
            }

            @Override
            public void draw(Graphics2D g) {
                draw.draw(g);
            }
        }, outputType, stream);
    }
}
