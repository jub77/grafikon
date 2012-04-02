package net.parostroj.timetable.gui.components;
import java.awt.Insets;

import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.OverlayLayout;


public class GTLayeredPane extends JLayeredPane {

    public GTLayeredPane(GraphicalTimetableView view) {
        super();
        this.setLayout(new OverlayLayout(this));
        
        JScrollPane sc = new JScrollPane();
        sc.setViewportView(view);
        
        Insets borderInsets = sc.getBorder().getBorderInsets(sc);
        
        this.add(sc, JLayeredPane.DEFAULT_LAYER);
        this.add(new GTStationNamesOverlay(view, borderInsets.top), JLayeredPane.PALETTE_LAYER);
    }
}
