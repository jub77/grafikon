package net.parostroj.timetable.output2.gt;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import net.parostroj.timetable.output2.gt.DrawUtils.FontInfo;
import net.parostroj.timetable.utils.Tuple;

public interface GTDrawOrientationDelegate {

    public void drawLine(Graphics2D g, int x1, int y1, int x2, int y2);

    public Line2D createLine(double x1, double y1, double x2, double y2);

    public int getHoursSize(Dimension size);

    public int getStationsSize(Dimension size);

    public int getHoursStart(Point p);

    public int getStationsStart(Point p);

    public Tuple<Point2D> getDigitPoints(Line2D line, Rectangle2D dSize);

    public void adaptStart(Point start, int stationNames, FontInfo fi, int mWidth);

    public void drawHours(Graphics2D g, String hStr, int hoursLocation, Rectangle bounds, Point start, FontInfo fi, int mWidth);

    public void drawStationName(Graphics2D g, String name, Rectangle backRectangle, int stationLocation, FontInfo fi, int borderX, int borderY, Color background, int titleHeight);
}
