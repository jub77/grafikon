package net.parostroj.timetable.gui.components;

import java.awt.Graphics2D;

public class ManagedFreightGTDraw extends GTDrawDecorator {

    public ManagedFreightGTDraw(GTDraw draw) {
        super(draw);
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        // draw managed trains ...
    }
}
