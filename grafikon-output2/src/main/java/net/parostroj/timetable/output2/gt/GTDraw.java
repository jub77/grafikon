package net.parostroj.timetable.output2.gt;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.Event;

public interface GTDraw {

	interface Listener {
		void trainOnLine(Graphics2D g, TimeInterval timeInterval, Interval interval, Line2D line);
		void trainInStation(Graphics2D g, TimeInterval timeInterval, Interval interval, Line2D line);
	}

    String TRAIN_COLOR_CHOOSER = "gt.chooser";
    String HIGHLIGHTED_TRAINS = "gt.highlight";

    enum TrainColors {
        BY_TYPE, BY_COLOR_CHOOSER
    }

    enum Type {
        CLASSIC("classic"), WITH_TRACKS("tracks"), CLASSIC_STATION_STOPS("stops");

        private final String key;

        Type(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public static Type fromKey(String key) {
            for (Type type : values()) {
                if (type.key.equals(key)) {
                    return type;
                }
            }
            return null;
        }
    }

    enum Refresh {
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

    Refresh processEvent(Event event);

    GTDrawSettings getSettings();

    void addListener(Listener listener);

    void removeListener(Listener listener);
}
