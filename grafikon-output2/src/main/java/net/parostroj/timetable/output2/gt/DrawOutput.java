package net.parostroj.timetable.output2.gt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Locale;

import javax.imageio.ImageIO;

import net.parostroj.timetable.output2.*;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * Draw output - generic.
 *
 * @author jub
 */
public abstract class DrawOutput extends OutputWithLocale implements DrawParams {

    protected static interface Image {
        public void draw(Graphics2D g);
        public Dimension getSize(Graphics2D g);
    }

    public DrawOutput(Locale locale) {
        super(locale);
    }

    protected void draw(Image image, GTDraw.OutputType outputType, OutputStream stream) throws OutputException {
        switch (outputType) {
            case PNG:
                BufferedImage testImg = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
                Dimension pngSize = image.getSize(testImg.createGraphics());

                BufferedImage img = new BufferedImage(pngSize.width, pngSize.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = img.createGraphics();
                g2d.setColor(Color.white);
                g2d.fillRect(0, 0, pngSize.width, pngSize.height);
                image.draw(g2d);

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

                Dimension svgSize = image.getSize(g2db);

                g2db.setSVGCanvasSize(svgSize);

                image.draw(g2db);

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
