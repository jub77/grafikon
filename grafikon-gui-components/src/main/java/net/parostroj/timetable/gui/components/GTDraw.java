package net.parostroj.timetable.gui.components;

import java.awt.Graphics2D;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.Train;

public interface GTDraw {

    void draw(Graphics2D g);

    void paintStationNames(Graphics2D g);

    Route getRoute();

    void setPositionX(int positionX);

    float getFontSize();

    void removedTrain(Train train);

    void changedTextTrain(Train train);

    void changedTextNode(Node node);

    void changedTextAllTrains();

}