package net.parostroj.timetable.gui.components;

import javax.swing.JScrollPane;

/**
 * ScrollPane for GTView.
 *
 * @author jub
 */
public class GTViewScrollPane extends JScrollPane {

    public GTViewScrollPane(GraphicalTimetableView view) {
        super(view);
        this.getViewport().addChangeListener(view);
        this.getHorizontalScrollBar().setBlockIncrement(1000);
        this.getHorizontalScrollBar().setUnitIncrement(100);
    }

}
