package net.parostroj.timetable.output2.gt;

import java.awt.Dimension;
import java.awt.Graphics2D;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.Event;

public abstract class GTDrawDecorator implements GTDraw {

    protected GTDraw decoratedDraw;

    public GTDrawDecorator(GTDraw draw) {
        this.decoratedDraw = draw;
    }

    public GTDraw getDraw() {
        return decoratedDraw;
    }

    @Override
    public void draw(Graphics2D g) {
        decoratedDraw.draw(g);
    }

    @Override
    public void paintStationNames(Graphics2D g) {
        decoratedDraw.paintStationNames(g);
    }

    @Override
    public Route getRoute() {
        return decoratedDraw.getRoute();
    }

    @Override
    public int getX(int time) {
        return decoratedDraw.getX(time);
    }

    @Override
    public int getY(Node node, Track track) {
        return decoratedDraw.getY(node, track);
    }

    @Override
    public int getY(TimeInterval interval) {
        return decoratedDraw.getY(interval);
    }

    @Override
    public Dimension getSize() {
        return decoratedDraw.getSize();
    }

    @Override
    public Refresh processEvent(Event event) {
        return decoratedDraw.processEvent(event);
    }

    @Override
    public void addListener(Listener listener) {
    	decoratedDraw.addListener(listener);
    }

    @Override
    public void removeListener(Listener listener) {
    	decoratedDraw.removeListener(listener);
    }

    @Override
    public GTDrawSettings getSettings() {
        return decoratedDraw.getSettings();
    }
}
