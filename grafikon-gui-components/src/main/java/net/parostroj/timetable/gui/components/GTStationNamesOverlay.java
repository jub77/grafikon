package net.parostroj.timetable.gui.components;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class GTStationNamesOverlay extends JPanel {

    private final GraphicalTimetableView view;
    private final int topBorder;

    public GTStationNamesOverlay(GraphicalTimetableView view, int topBorder) {
        this.topBorder = topBorder;
        this.setOpaque(false);
        this.view = view;
        this.view.setDisableStationNames(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.translate(0, topBorder);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (view != null && view.getGtDraw() != null) {
            // set font size
            g.setFont(g.getFont().deriveFont(view.getGtDraw().getFontSize()));
            // draw
            view.getGtDraw().paintStationNames((Graphics2D) g);
        }
    }
}
