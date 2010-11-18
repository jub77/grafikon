package net.parostroj.timetable.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import net.parostroj.timetable.gui.components.GTViewSettings.Type;
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
 * GT view with save dialog.
 * 
 * @author jub
 */
public class GraphicalTimetableViewWithSave extends GraphicalTimetableView {

    private static final Logger LOG = LoggerFactory.getLogger(GraphicalTimetableViewWithSave.class.getName());
    private SaveImageDialog dialog;

    public GraphicalTimetableViewWithSave() {
        super();
        
        // extend context menu
        JMenuItem saveMenuItem = new JMenuItem(ResourceLoader.getString("gt.save"));
        popupMenu.add(new JSeparator());
        popupMenu.add(saveMenuItem);
        // action
        saveMenuItem.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveMenuItemActionPerformed(e);
            }
        });
    }

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.getRoute() == null) {
            return;
        }
        if (dialog == null)
            dialog = new SaveImageDialog((Frame)this.getTopLevelAncestor(), true);
        dialog.setLocationRelativeTo(this.getParent());
        dialog.setSaveSize(this.getSize());
        dialog.setVisible(true);

        if (!dialog.isSave()) {
            return;
        }
        Dimension saveSize = dialog.getSaveSize();
        // get values and provide save
        GTDraw drawFile = null;
        GTViewSettings config = this.getSettings();
        config.set(GTViewSettings.Key.SIZE, saveSize);
        config.remove(GTViewSettings.Key.HIGHLIGHTED_TRAINS);
        if (this.settings.get(GTViewSettings.Key.TYPE) == Type.CLASSIC) {
            drawFile = new GTDrawClassic(config, this.getRoute(), null);
        } else if (this.settings.get(GTViewSettings.Key.TYPE) == Type.WITH_TRACKS) {
            drawFile = new GTDrawWithNodeTracks(config, this.getRoute(), null);
        }

        if (dialog.getType() == SaveImageDialog.Type.PNG) {
            BufferedImage img = new BufferedImage(saveSize.width, saveSize.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();
            g2d.setColor(Color.white);
            g2d.fillRect(0, 0, saveSize.width, saveSize.height);
            drawFile.draw(g2d);

            try {
                ImageIO.write(img, "png", dialog.getSaveFile());
            } catch (IOException e) {
                LOG.warn("Error saving file: " + dialog.getSaveFile(), e);
                JOptionPane.showMessageDialog(this, ResourceLoader.getString("save.image.error"), ResourceLoader.getString("save.image.error.text"), JOptionPane.ERROR_MESSAGE);
            }
        } else if (dialog.getType() == SaveImageDialog.Type.SVG) {
            DOMImplementation domImpl =
                    GenericDOMImplementation.getDOMImplementation();

            // Create an instance of org.w3c.dom.Document.
            String svgNS = "http://www.w3.org/2000/svg";
            Document document = domImpl.createDocument(svgNS, "svg", null);

            SVGGeneratorContext context = SVGGeneratorContext.createDefault(document);
            SVGGraphics2D g2d = new SVGGraphics2D(context, false);

            g2d.setSVGCanvasSize(saveSize);

            drawFile.draw(g2d);

            // write to ouput - do not use css style
            boolean useCSS = false;
            try {
                Writer out = new OutputStreamWriter(new FileOutputStream(dialog.getSaveFile()), "UTF-8");
                g2d.stream(out, useCSS);
            } catch (IOException e) {
                LOG.warn("Error saving file: " + dialog.getSaveFile(), e);
                JOptionPane.showMessageDialog(this, ResourceLoader.getString("save.image.error"), ResourceLoader.getString("save.image.error.text"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
