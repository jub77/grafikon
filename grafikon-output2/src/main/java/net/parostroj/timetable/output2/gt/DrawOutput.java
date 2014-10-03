package net.parostroj.timetable.output2.gt;

import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

    protected FileOutputType getFileOutputType(OutputParams params) {
        FileOutputType type = params.getParamValue(OUTPUT_TYPE, FileOutputType.class);
        return type != null ? type : FileOutputType.SVG;
    }

    protected void draw(Image image, FileOutputType outputType, OutputStream stream) throws OutputException {
        switch (outputType) {
            case PNG:
                this.processPng(image, stream);
                break;
            case SVG:
                this.processSvg(image, stream);
                break;
        }
    }

    private void processSvg(Image image, OutputStream stream) throws OutputException {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        SVGGeneratorContext context = SVGGeneratorContext.createDefault(document);
        context.setGraphicContextDefaults(new SVGGeneratorContext.GraphicContextDefaults());
        // set default font
        context.getGraphicContextDefaults().setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        // set antialiasing
        Map<Key, Object> map = new HashMap<Key, Object>();
        map.put(RenderingHints.KEY_ANTIALIASING, Boolean.TRUE);
        map.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        context.getGraphicContextDefaults().setRenderingHints(new RenderingHints(map));

        SVGGraphics2D g2d = new SVGGraphics2D(context, false);
        Dimension size = image.getSize(g2d);
        g2d.setSVGCanvasSize(size);

        image.draw(g2d);

        // write to ouput - do not use css style
        boolean useCSS = false;
        try {
            Writer out = new OutputStreamWriter(stream, "UTF-8");
            g2d.stream(out, useCSS);
        } catch (IOException e) {
            throw new OutputException(e.getMessage(), e);
        }
    }

    private void processPng(Image image, OutputStream stream) throws OutputException {
        BufferedImage testImg = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        Dimension size = image.getSize(testImg.createGraphics());

        BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, size.width, size.height);
        image.draw(g2d);

        try {
            ImageIO.write(img, "png", stream);
        } catch (IOException e) {
            throw new OutputException(e.getMessage(), e);
        }
    }
}
