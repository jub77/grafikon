package net.parostroj.timetable.output2.gt;

import java.awt.*;
import java.awt.geom.Line2D;
import net.parostroj.timetable.model.Interval;
import net.parostroj.timetable.model.TimeInterval;

public abstract class GTDrawListenerAdapter implements GTDraw.Listener {
    @Override
    public void beforeTrainOnLine(Graphics2D g, TimeInterval timeInterval, Interval interval, Line2D line) {
        // do nothing
    }

    @Override
    public void beforeTrainInStation(Graphics2D g, TimeInterval timeInterval, Interval interval, Line2D line) {
        // do nothing
    }

    @Override
    public void afterTrainOnLine(Graphics2D g, TimeInterval timeInterval, Interval interval, Line2D line) {
        // do nothing
    }

    @Override
    public void afterTrainInStation(Graphics2D g, TimeInterval timeInterval, Interval interval, Line2D line) {
        // do nothing
    }

    public interface Call {
        void call(Graphics2D g, TimeInterval timeInterval, Interval interval, Line2D line);
    }

    public static GTDraw.Listener createBeforeInStation(Call call) {
        return new GTDrawListenerAdapter() {
            @Override
            public void beforeTrainInStation(Graphics2D g, TimeInterval timeInterval, Interval interval, Line2D line) {
                call.call(g, timeInterval, interval, line);
            }
        };
    }

    public static GTDraw.Listener createAfterInStation(Call call) {
        return new GTDrawListenerAdapter() {
            @Override
            public void afterTrainInStation(Graphics2D g, TimeInterval timeInterval, Interval interval, Line2D line) {
                call.call(g, timeInterval, interval, line);
            }
        };
    }

    public static GTDraw.Listener createBeforeOnLine(Call call) {
        return new GTDrawListenerAdapter() {
            @Override
            public void beforeTrainOnLine(Graphics2D g, TimeInterval timeInterval, Interval interval, Line2D line) {
                call.call(g, timeInterval, interval, line);
            }
        };
    }

    public static GTDraw.Listener createAfterOnLine(Call call) {
        return new GTDrawListenerAdapter() {
            @Override
            public void afterTrainOnLine(Graphics2D g, TimeInterval timeInterval, Interval interval, Line2D line) {
                call.call(g, timeInterval, interval, line);
            }
        };
    }
}
