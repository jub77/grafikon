package net.parostroj.timetable.gui.components;

import java.awt.Insets;

import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.OverlayLayout;

import net.parostroj.timetable.model.TrainDiagram;


public class GTLayeredPane2 extends JLayeredPane {

    private final JScrollPane scrollPane;
    private final GTVButtonPanel buttonPanel;

    public GTLayeredPane2(GraphicalTimetableView view) {
        super();
        this.setLayout(new OverlayLayout(this));

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(view);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        Insets borderInsets = scrollPane.getBorder() != null ?
        		scrollPane.getBorder().getBorderInsets(scrollPane) :
        			new Insets(0, 0, 0, 0);

        this.add(scrollPane, JLayeredPane.DEFAULT_LAYER);
        this.add(new GTStationNamesOverlay(view, borderInsets.top), new Integer(400));
        buttonPanel = new GTVButtonPanel(view, borderInsets);
        this.add(buttonPanel, new Integer(600));
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setTrainDiagram(TrainDiagram td) {
        buttonPanel.setTrainDiagram(td);
    }
}
