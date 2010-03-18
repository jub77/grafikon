package net.parostroj.timetable.gui.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import net.parostroj.timetable.gui.dialogs.SaveGTDialog;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * GT view with save dialog.
 * 
 * @author jub
 */
public class GraphicalTimetableViewWithSave extends GraphicalTimetableView {

    private SaveGTDialog dialog;

    public GraphicalTimetableViewWithSave() {
        super();
        dialog = new SaveGTDialog(null, true);
        
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
        dialog.setLocationRelativeTo(this.getParent());
        dialog.setVisible(true);

        if (!dialog.isSave()) {
            return;
        }
        // get values and provide save
        GTDraw drawFile = null;
        if (this.getType() == Type.CLASSIC) {
            drawFile = new GTDrawClassic(10, 20, 100, dialog.getSaveSize(), this.getRoute(), this.getTrainColors(), this.getTrainColorChooser(), null, null);
        } else if (this.getType() == Type.WITH_TRACKS) {
            drawFile = new GTDrawWithNodeTracks(10, 20, 100, dialog.getSaveSize(), this.getRoute(), this.getTrainColors(), this.getTrainColorChooser(), null, null);
        }
        this.setPreferencesToDraw(drawFile);

        if (dialog.getType() == SaveGTDialog.Type.PNG) {
            BufferedImage img = new BufferedImage(dialog.getSaveSize().width, dialog.getSaveSize().height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();
            g2d.setColor(Color.white);
            g2d.fillRect(0, 0, dialog.getSaveSize().width, dialog.getSaveSize().height);
            drawFile.draw(g2d);

            try {
                ImageIO.write(img, "png", dialog.getSaveFile());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, ResourceLoader.getString("save.image.error"), ResourceLoader.getString("save.image.error.text"), JOptionPane.ERROR_MESSAGE);
            }
        } else if (dialog.getType() == SaveGTDialog.Type.SVG) {
            DOMImplementation domImpl =
                    GenericDOMImplementation.getDOMImplementation();

            // Create an instance of org.w3c.dom.Document.
            String svgNS = "http://www.w3.org/2000/svg";
            Document document = domImpl.createDocument(svgNS, "svg", null);

            SVGGeneratorContext context = SVGGeneratorContext.createDefault(document);
            SVGGraphics2D g2d = new SVGGraphics2D(context, false);

            g2d.setSVGCanvasSize(dialog.getSaveSize());

            drawFile.draw(g2d);

            // write to ouput - do not use css style
            boolean useCSS = false;
            try {
                Writer out = new OutputStreamWriter(new FileOutputStream(dialog.getSaveFile()), "UTF-8");
                g2d.stream(out, useCSS);
            } catch (IOException e) {
                // do nothing for this moment
            }
        }
    }
}
