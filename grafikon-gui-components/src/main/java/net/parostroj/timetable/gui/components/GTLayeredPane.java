package net.parostroj.timetable.gui.components;
import java.awt.Insets;

import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.OverlayLayout;


public class GTLayeredPane extends JLayeredPane {
    
    private JScrollPane scrollPane;

    public GTLayeredPane(GraphicalTimetableView view) {
        super();
        this.setLayout(new OverlayLayout(this));
        
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(view);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        
        Insets borderInsets = scrollPane.getBorder().getBorderInsets(scrollPane);
        
        this.add(scrollPane, JLayeredPane.DEFAULT_LAYER);
        this.add(new GTStationNamesOverlay(view, borderInsets.top), new Integer(400));
    }
    
    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
