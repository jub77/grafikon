package net.parostroj.timetable.gui.components;

import java.awt.Graphics2D;

import net.parostroj.timetable.model.Route;

public abstract class GTDrawDecorator implements GTDraw {

    protected GTDraw draw;

    public GTDrawDecorator(GTDraw draw) {
        this.draw = draw;
    }

    public GTDraw getDraw() {
        return draw;
    }

    @Override
    public void draw(Graphics2D g) {
        draw.draw(g);
    }

    @Override
    public void paintStationNames(Graphics2D g) {
        draw.paintStationNames(g);
    }

    @Override
    public Route getRoute() {
        return draw.getRoute();
    }

    @Override
    public void setPositionX(int positionX) {
        draw.setPositionX(positionX);
    }

    @Override
    public float getFontSize() {
        return draw.getFontSize();
    }

    @Override
    public void changed(Change change, Object object) {
        draw.changed(change, object);
    }
}
