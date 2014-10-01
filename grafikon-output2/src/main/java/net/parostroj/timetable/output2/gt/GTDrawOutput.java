package net.parostroj.timetable.output2.gt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.OutputWithLocale;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * GTDraw output.
 *
 * @author cz2b10k5
 */
public class GTDrawOutput extends OutputWithLocale {

    private final GTDrawFactory drawFactory;

    public GTDrawOutput(Locale locale) {
        super(locale);
        drawFactory = new NormalGTDrawFactory();
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        Route route = this.getRoute(params, diagram);
        GTDrawParams gtParams = this.getParams(params);

        GTDraw draw = drawFactory.createInstance(gtParams.getType(), gtParams.getSettings(), route, new GTStorage());
        this.draw(gtParams.getSettings(), gtParams.getOutputType(), stream, draw);
    }

    private GTDrawParams getParams(OutputParams params) {
        GTDrawParams gtParams = params.getParamValue(GTDraw.GT_PARAMS, GTDrawParams.class);
        if (gtParams == null) {
            // create default values
            gtParams = new GTDrawParams();
        }
        return gtParams;
    }

    private Route getRoute(OutputParams params, TrainDiagram diagram) throws OutputException {
        Collection<?> routes = params.getParamValue(GTDraw.ROUTES_PARAM, Collection.class);
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

    private void draw(GTDrawSettings settings, GTDraw.OutputType outputType, OutputStream stream, GTDraw draw) throws OutputException {
        Dimension saveSize = settings.get(GTDrawSettings.Key.SIZE, Dimension.class);
        switch (outputType) {
            case PNG:
                BufferedImage img = new BufferedImage(saveSize.width, saveSize.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = img.createGraphics();
                g2d.setColor(Color.white);
                g2d.fillRect(0, 0, saveSize.width, saveSize.height);
                draw.draw(g2d);

                try {
                    ImageIO.write(img, "png", stream);
                } catch (IOException e) {
                    throw new OutputException(e.getMessage(), e);
                }
                break;
            case SVG:
                DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

                // Create an instance of org.w3c.dom.Document.
                String svgNS = "http://www.w3.org/2000/svg";
                Document document = domImpl.createDocument(svgNS, "svg", null);

                SVGGeneratorContext context = SVGGeneratorContext.createDefault(document);
                SVGGraphics2D g2db = new SVGGraphics2D(context, false);

                g2db.setSVGCanvasSize(saveSize);

                draw.draw(g2db);

                // write to ouput - do not use css style
                boolean useCSS = false;
                try {
                    Writer out = new OutputStreamWriter(stream, "UTF-8");
                    g2db.stream(out, useCSS);
                } catch (IOException e) {
                    throw new OutputException(e.getMessage(), e);
                }
                break;
        }
    }
}
