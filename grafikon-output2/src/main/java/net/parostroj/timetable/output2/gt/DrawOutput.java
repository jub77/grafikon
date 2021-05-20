package net.parostroj.timetable.output2.gt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.fonts.DefaultFontConfigurator;
import org.apache.fop.fonts.EmbedFontInfo;
import org.apache.fop.fonts.FontConfig;
import org.apache.fop.fonts.FontInfo;
import org.apache.fop.fonts.FontManager;
import org.apache.fop.fonts.FontSetup;
import org.apache.fop.render.pdf.PDFRendererConfig.PDFRendererConfigParser;
import org.apache.fop.svg.PDFDocumentGraphics2D;
import org.apache.xmlgraphics.java2d.GraphicContext;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import net.parostroj.timetable.output2.DrawParams;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.OutputWithLocale;
import net.parostroj.timetable.output2.pdf.PdfTransformer;

/**
 * Draw output - generic.
 *
 * @author jub
 */
public abstract class DrawOutput extends OutputWithLocale implements DrawParams {

    protected interface Image {
        void draw(Graphics2D g);

        Dimension getSize(Graphics2D g);
    }

    DrawOutput(Locale locale) {
        super(locale);
    }

    protected FileOutputType getFileOutputType(OutputParams params) {
        Object typeObject = params.getParamValue(OUTPUT_TYPE, Object.class);
        FileOutputType type = FileOutputType.SVG;
        if (typeObject instanceof FileOutputType) {
            type = (FileOutputType) typeObject;
        } else if (typeObject instanceof String) {
            FileOutputType parsedType = FileOutputType.fromString((String) typeObject);
            if (parsedType != null) {
                type = parsedType;
            }
        }
        return type;
    }

    protected void draw(Collection<Image> images, FileOutputType outputType, OutputStream stream, DrawLayout layout)
            throws OutputException {
        switch (outputType) {
        case PNG:
            this.processPng(images, stream, layout);
            break;
        case SVG:
            this.processSvg(images, stream, layout);
            break;
        case PDF:
            this.processPdf(images, stream, layout);
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
        Map<Key, Object> map = new HashMap<>();
        map.put(RenderingHints.KEY_ANTIALIASING, Boolean.TRUE);
        map.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        context.getGraphicContextDefaults().setRenderingHints(new RenderingHints(map));

        SVGGraphics2D g2d = new SVGGraphics2D(context, false);

        List<Dimension> sizes = this.getSizes(images, g2d);
        Dimension size = this.getTotalSize(sizes, layout);
        g2d.setSVGCanvasSize(size);

        this.drawImages(sizes, images, g2d, layout);

        // write to ouput - do not use css style
        try {
            Writer out = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
            g2d.stream(out, false);
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

    private void processPdf(Collection<Image> images, OutputStream stream, DrawLayout layout) throws OutputException {


        PDFDocumentGraphics2D g2d;
        try {
            g2d = new PDFDocumentGraphics2D();

            g2d.setGraphicContext(new GraphicContext());

            FopFactory fopFactory = PdfTransformer.createFopFactory();
            FOUserAgent userAgent = fopFactory.newFOUserAgent();
            FontConfig fc = userAgent.getRendererConfig(MimeConstants.MIME_PDF, new PDFRendererConfigParser()).getFontInfoConfig();
            FontManager fontManager = fopFactory.getFontManager();
            DefaultFontConfigurator fontInfoConfigurator = new DefaultFontConfigurator(fontManager, null, false);
            List<EmbedFontInfo> fontInfoList = fontInfoConfigurator.configure(fc);
            FontInfo fontInfo = new FontInfo();
            FontSetup.setup(fontInfo, fontInfoList, userAgent.getResourceResolver(), fontManager.isBase14KerningEnabled());
            g2d.setFontInfo(fontInfo);

            g2d.setFont(new Font("SansCondensed", Font.PLAIN, g2d.getFont().getSize()));

            List<Dimension> sizes = this.getSizes(images, g2d);
            Dimension size = this.getTotalSize(sizes, layout);

            g2d.setupDocument(stream, size.width, size.height);

            this.drawImages(sizes, images, g2d, layout);
            g2d.finish();

        } catch (IOException | FOPException e) {
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
        List<Dimension> sizes = new ArrayList<>();
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
