package net.parostroj.timetable.gui.components;

public class GTLayeredPane2 extends GTLayeredPane {

    private static final long serialVersionUID = 1L;

	private final GTVButtonPanel buttonPanel;

    public GTLayeredPane2(GraphicalTimetableView view) {
        super(view);
        buttonPanel = new GTVButtonPanel(view, borderInsets);
        this.add(buttonPanel, Integer.valueOf(600));
    }
}
