package net.parostroj.timetable.output2.gt;

import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

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

    protected void draw(Collection<Image> images, FileOutputType outputType, OutputStream stream, DrawLayout layout) throws OutputException {
        switch (outputType) {
            case PNG:
                this.processPng(images, stream, layout);
                break;
            case SVG:
                this.processSvg(images, stream, layout);
                break;
        }
    }

    private void processSvg(Collection<Image> images, OutputStream stream, DrawLayout layout) throws OutputException {
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

        List<Dimension> sizes = this.getSizes(images, g2d);
        Dimension size = this.getTotalSize(sizes, layout);
        g2d.setSVGCanvasSize(size);

        this.drawImages(sizes, images, g2d, layout);

        // write to ouput - do not use css style
        boolean useCSS = false;
        try {
            Writer out = new OutputStreamWriter(stream, "UTF-8");
            g2d.stream(out, useCSS);
        } catch (IOException e) {
            throw new OutputException(e.getMessage(), e);
        }
    }

    private void processPng(Collection<Image> images, OutputStream stream, DrawLayout layout) throws OutputException {
        BufferedImage testImg = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        List<Dimension> sizes = this.getSizes(images, testImg.createGraphics());
        Dimension size = this.getTotalSize(sizes, layout);

        BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, size.width, size.height);

        this.drawImages(sizes, images, g2d, layout);

        try {
            ImageIO.write(img, "png", stream);
        } catch (IOException e) {
            throw new OutputException(e.getMessage(), e);
        }
    }

    private void drawImages(List<Dimension> sizes, Collection<Image> images, Graphics2D g, DrawLayout layout) {
        int pos = 0;
        for (Image image : images) {
            Point location = this.getLocation(sizes, pos, layout);
            AffineTransform currentTransform = g.getTransform();
            AffineTransform newTransform = g.getTransform();
            newTransform.translate(location.getX(), location.getY());
            g.setTransform(newTransform);
            image.draw(g);
            g.setTransform(currentTransform);
            pos++;
        }
    }

    private List<Dimension> getSizes(Iterable<Image> images, Graphics2D g) {
        List<Dimension> sizes = new ArrayList<Dimension>();
        for (Image image : images) {
            sizes.add(image.getSize(g));
        }
        return sizes;
    }

    private Dimension getTotalSize(List<Dimension> sizes, DrawLayout layout) {
        switch (layout.getOrientation()) {
            case TOP_DOWN:
                int width = 0;
                int height = 0;
                for (Dimension s : sizes) {
                    width = Math.max(width, s.width);
                    height += s.height;
                }
                return new Dimension(width, height);
            default:
                throw new IllegalArgumentException();
        }
    }

    private Point getLocation(List<Dimension> sizes, int position, DrawLayout layout) {
        switch (layout.getOrientation()) {
            case TOP_DOWN:
                int y = 0;
                for (int i = 0; i < position; i++) {
                    y += sizes.get(i).height;
                }
                return new Point(0, y);
            default:
                throw new IllegalArgumentException();
        }
    }
}
