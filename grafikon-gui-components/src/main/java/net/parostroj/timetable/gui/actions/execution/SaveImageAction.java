package net.parostroj.timetable.gui.actions.execution;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import net.parostroj.timetable.gui.dialogs.SaveImageDialog;
import net.parostroj.timetable.gui.utils.ResourceLoader;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * Action for saving image.
 * 
 * @author jub
 */
public class SaveImageAction extends EventDispatchAfterModelAction {
    
    public static interface Image {
        public void paintImage(Graphics g);
    }
    
    private static final Logger LOG = LoggerFactory.getLogger(SaveImageAction.class);
    private boolean error;
    private final SaveImageDialog dialog;
    private final Image image;
    
    public SaveImageAction(ActionContext context, SaveImageDialog dialog, Image image) {
        super(context);
        this.dialog = dialog;
        this.image = image;
    }

    @Override
    protected void backgroundAction() {
        setWaitMessage(ResourceLoader.getString("wait.message.image.save"));
        setWaitDialogVisible(true);
        long time = System.currentTimeMillis();
        try {
            Dimension saveSize = dialog.getSaveSize();
            if (dialog.getImageType() == SaveImageDialog.Type.PNG) {
                BufferedImage img = new BufferedImage(saveSize.width, saveSize.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = img.createGraphics();
                g2d.setColor(Color.white);
                g2d.fillRect(0, 0, saveSize.width, saveSize.height);
                image.paintImage(g2d);

                try {
                    ImageIO.write(img, "png", dialog.getSaveFile());
                } catch (IOException e) {
                    LOG.warn("Error saving file: " + dialog.getSaveFile(), e);
                    error = true;
                }
            } else if (dialog.getImageType() == SaveImageDialog.Type.SVG) {
                DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

                // Create an instance of org.w3c.dom.Document.
                String svgNS = "http://www.w3.org/2000/svg";
                Document document = domImpl.createDocument(svgNS, "svg", null);

                SVGGeneratorContext context = SVGGeneratorContext.createDefault(document);
                SVGGraphics2D g2d = new SVGGraphics2D(context, false);

                g2d.setSVGCanvasSize(saveSize);

                image.paintImage(g2d);

                // write to ouput - do not use css style
                boolean useCSS = false;
                try {
                    Writer out = new OutputStreamWriter(new FileOutputStream(dialog.getSaveFile()), "UTF-8");
                    g2d.stream(out, useCSS);
                } catch (IOException e) {
                    LOG.warn("Error saving file: " + dialog.getSaveFile(), e);
                    error = true;
                }
            }
        } finally {
            LOG.debug("Image save finished in {}ms", System.currentTimeMillis() - time);
            setWaitDialogVisible(false);
        }
    }

    @Override
    protected void eventDispatchActionAfter() {
        if (error) {
            JOptionPane.showMessageDialog(context.getLocationComponent(), ResourceLoader.getString("save.image.error"), ResourceLoader.getString("save.image.error.text"), JOptionPane.ERROR_MESSAGE);
        }
    }
}
