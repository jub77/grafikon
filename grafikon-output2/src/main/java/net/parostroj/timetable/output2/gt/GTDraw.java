package net.parostroj.timetable.output2.gt;

import java.awt.Dimension;
import java.awt.Graphics2D;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.GTEvent;

public interface GTDraw {

    public String TRAIN_COLOR_CHOOSER = "gt.chooser";
    public String HIGHLIGHTED_TRAINS = "gt.highlight";

    public enum TrainColors {
        BY_TYPE, BY_COLOR_CHOOSER;
    }

    public enum Type {
        CLASSIC, WITH_TRACKS, CLASSIC_STATION_STOPS;
    }

    public enum Refresh {
        NONE, REPAINT, RECREATE, RECREATE_WITH_TIME;

        public Refresh update(Refresh refresh) {
            return (this.ordinal() > refresh.ordinal()) ? this : refresh;
        }
    }

    void draw(Graphics2D g);

    void paintStationNames(Graphics2D g);

    Route getRoute();

    int getX(int time);

    int getY(Node node, Track track);

    int getY(TimeInterval interval);

    Dimension getSize();

    Refresh processEvent(GTEvent<?> event);

    GTDrawSettings getSettings();
}
