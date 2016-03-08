package net.parostroj.timetable.gui.components;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class GTStationNamesOverlay extends JPanel {

    private final GraphicalTimetableView view;
    private final int topBorder;
    private final int leftBorder;

    public GTStationNamesOverlay(GraphicalTimetableView view, int topBorder, int leftBorder) {
        this.topBorder = topBorder;
        this.leftBorder = leftBorder;
        this.setOpaque(false);
        this.view = view;
        this.view.setDisableStationNames(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.translate(leftBorder, topBorder);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (view != null && view.getGtDraw() != null) {
            view.getGtDraw().paintStationNames(g2d);
        }
    }
}
