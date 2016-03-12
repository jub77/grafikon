package net.parostroj.timetable.gui;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Window;

public class StorableWindowData {

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public StorableWindowData(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

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
        return String.format("%d,%d,%d,%d", getX(), getY(), getWidth(), getHeight());
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
