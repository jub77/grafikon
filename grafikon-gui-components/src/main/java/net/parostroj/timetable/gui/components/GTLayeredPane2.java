package net.parostroj.timetable.gui.components;

public class GTLayeredPane2 extends GTLayeredPane {

    private final GTVButtonPanel buttonPanel;

    public GTLayeredPane2(GraphicalTimetableView view) {
        super(view);
        buttonPanel = new GTVButtonPanel(view, borderInsets);
        this.add(buttonPanel, new Integer(600));
    }
}
