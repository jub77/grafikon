package net.parostroj.timetable.output2.gt;

import java.awt.*;
import java.awt.geom.*;

import net.parostroj.timetable.output2.gt.DrawUtils.FontInfo;
import net.parostroj.timetable.utils.Tuple;

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

                    @Override
                    public Tuple<Point2D> getDigitPoints(Line2D line, Rectangle2D dSize) {
                        Point2D startP = line.getP1();
                        Point2D endP = line.getP2();
                        if (line.getY1() < line.getY2()) {
                            startP.setLocation(startP.getX() + dSize.getWidth(), startP.getY() + dSize.getHeight() - 2);
                            endP.setLocation(endP.getX() - 1.5 * dSize.getWidth(), endP.getY() - 2);
                        } else {
                            startP.setLocation(startP.getX() + dSize.getWidth(), startP.getY() - 2);
                            endP.setLocation(endP.getX() - 1.5 * dSize.getWidth(), endP.getY() + dSize.getHeight() - 2);
                        }
                        return new Tuple<Point2D>(startP, endP);
                    }

                    @Override
                    public void adaptStart(Point start, int stationNames, FontInfo fi, int mWidth) {
                        start.translate(stationNames, fi.height - fi.descent);
                    }

                    @Override
                    public void drawHours(Graphics2D g, String hStr, int hoursLocation, Rectangle bounds, Point start,
                            FontInfo fi, int mWidth) {
                        g.drawString(hStr, hoursLocation - bounds.width / 2 + mWidth / 5, start.y - mWidth / 3);
                    }

                    @Override
                    public void drawStationName(Graphics2D g, String name, Rectangle backRectangle, int stationLocation,
                            FontInfo fi, int borderX, int borderY, Color background, int titleHeight) {
                        int sx = borderX;
                        int sy = stationLocation - fi.strikeThrough;
                        backRectangle.translate(sx, sy);
                        paintRectangleWithStationName(g, name, backRectangle, sx, sy, background);
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

                    @Override
                    public Tuple<Point2D> getDigitPoints(Line2D line, Rectangle2D dSize) {
                        boolean direction = line.getY1() < line.getY2();
                        Point2D startP = direction ? line.getP1() : line.getP2();
                        Point2D endP = direction ? line.getP2() : line.getP1();
                        if (direction) {
                            startP.setLocation(startP.getX(), startP.getY() + dSize.getHeight());
                            endP.setLocation(endP.getX() - dSize.getWidth(), endP.getY() - 3);
                        } else {
                            startP.setLocation(startP.getX() - dSize.getWidth(), startP.getY() + dSize.getHeight());
                            endP.setLocation(endP.getX(), endP.getY() - 3);
                        }
                        return new Tuple<Point2D>(startP, endP);
                    }

                    @Override
                    public void adaptStart(Point start, int stationNames, FontInfo fi, int mWidth) {
                        // hours width
                        start.translate(mWidth * 2, stationNames);
                    }

                    @Override
                    public void drawHours(Graphics2D g, String hStr, int hoursLocation, Rectangle bounds, Point start,
                            FontInfo fi, int mWidth) {
                        g.drawString(hStr, start.x - bounds.width - mWidth / 2, hoursLocation - fi.strikeThrough);
                    }

                    @Override
                    public void drawStationName(Graphics2D g, String name, Rectangle backRectangle,
                            int stationLocation, FontInfo fi, int borderX, int borderY, Color background, int titleHeight) {
                        int tx = stationLocation + fi.strikeThrough;
                        int ty = borderY + titleHeight;
                        AffineTransform oldTransform = g.getTransform();
                        AffineTransform newTransform = g.getTransform();
                        newTransform.translate(tx, ty);
                        newTransform.rotate(Math.PI / 2);
                        g.setTransform(newTransform);
                        paintRectangleWithStationName(g, name, backRectangle, 0, 0, background);
                        g.setTransform(oldTransform);
                    }
                };
            default:
                throw new IllegalArgumentException("Not allowed orientation");
        }
    }

    private static void paintRectangleWithStationName(Graphics2D g, String name, Rectangle r2, int x, int y, Color background) {
        if (background != null) {
            g.setColor(background);
            g.fill(r2);
        }
        g.setColor(Color.black);
        g.drawString(name, x, y);
    }

    private GTDrawOrientationFactory() {
    }
}
