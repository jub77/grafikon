package net.parostroj.timetable.output2.gt;

import java.awt.Dimension;
import java.awt.Graphics2D;

import net.parostroj.timetable.model.*;

public interface GTDraw {

    enum Change {
        REMOVED_TRAIN, TRAIN_TEXT_CHANGED, NODE_TEXT_CHANGED, ALL_TRAIN_TEXTS_CHANGED,
        TRAIN_INTERVALS_CHANGED
    }

    public enum TrainColors {
        BY_TYPE, BY_COLOR_CHOOSER;
    }

    public enum Type {
        CLASSIC, WITH_TRACKS, CLASSIC_STATION_STOPS;
    }

    void draw(Graphics2D g);

    void paintStationNames(Graphics2D g);

    Route getRoute();

    void setStationNamesPosition(int position);

    void changed(Change change, Object object);

    int getX(int time);

    int getY(Node node, Track track);

    int getY(TimeInterval interval);

    Dimension getSize();
}
