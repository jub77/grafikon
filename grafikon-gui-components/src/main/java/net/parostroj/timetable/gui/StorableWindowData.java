package net.parostroj.timetable.gui;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Window;

public record StorableWindowData(int x, int y, int width, int height) {

    public void applyTo(Window component) {
        Component parent = component.getOwner();
        Rectangle bounds = new Rectangle(x, y, width, height);
        if (parent != null) {
            bounds.translate(parent.getX(), parent.getY());
        }
        component.setBounds(bounds);
    }

    @Override
    public String toString() {
        return String.format("%d,%d,%d,%d", x(), y(), width(), height());
    }

    public static StorableWindowData getFrom(Window component) {
        Component parent = component.getOwner();
        int x = component.getX();
        int y = component.getY();
        int width = component.getWidth();
        int height = component.getHeight();
        if (parent != null) {
            x -= parent.getX();
            y -= parent.getY();
        }
        return new StorableWindowData(x, y, width, height);
    }
}
