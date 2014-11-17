package net.parostroj.timetable.output2.gt;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;

public class GTDrawOrientationFactory {

    public static GTDrawOrientationDelegate create(GTOrientation orientation) {
        switch (orientation) {
            case LEFT_RIGHT:
                return new GTDrawOrientationDelegate() {
                    @Override
                    public void drawLine(Graphics2D g, int x1, int y1, int x2, int y2) {
                        g.drawLine(x1, y1, x2, y2);
                    }

                    @Override
                    public Line2D createLine(double x1, double y1, double x2, double y2) {
                        return new Line2D.Double(x1, y1, x2, y2);
                    }

                    @Override
                    public int getHoursSize(Dimension size) {
                        return size.width;
                    }

                    @Override
                    public int getStationsSize(Dimension size) {
                        return size.height;
                    }

                    @Override
                    public int getHoursStart(Point p) {
                        return p.x;
                    }

                    @Override
                    public int getStationsStart(Point p) {
                        return p.y;
                    }
                };
            case TOP_DOWN:
                return new GTDrawOrientationDelegate() {
                    @Override
                    public void drawLine(Graphics2D g, int x1, int y1, int x2, int y2) {
                        g.drawLine(y1, x1, y2, x2);
                    }

                    @Override
                    public Line2D createLine(double x1, double y1, double x2, double y2) {
                        return y1 < y2 ?
                                new Line2D.Double(y1, x1, y2, x2) :
                                new Line2D.Double(y2, x2, y1, x1);
                    }

                    @Override
                    public int getHoursSize(Dimension size) {
                        return size.height;
                    }

                    @Override
                    public int getStationsSize(Dimension size) {
                        return size.width;
                    };

                    @Override
                    public int getHoursStart(Point p) {
                        return p.y;
                    }

                    @Override
                    public int getStationsStart(Point p) {
                        return p.x;
                    }
                };
            default:
                throw new IllegalArgumentException("Not allowed orientation");
        }
    }

    private GTDrawOrientationFactory() {
    }
}
