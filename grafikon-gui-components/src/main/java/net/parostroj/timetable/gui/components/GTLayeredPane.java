package net.parostroj.timetable.gui.components;
import java.awt.Insets;

import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.OverlayLayout;

import net.parostroj.timetable.gui.components.GTViewSettings.Key;
import net.parostroj.timetable.output2.gt.GTOrientation;

public class GTLayeredPane extends JLayeredPane {

    private final JScrollPane scrollPane;
    protected Insets borderInsets;

    public GTLayeredPane(GraphicalTimetableView view) {
        super();
        this.setLayout(new OverlayLayout(this));

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(view);
        this.updateScrollBars(view.settings.get(Key.ORIENTATION, GTOrientation.class));

        borderInsets = scrollPane.getBorder() != null ?
        		scrollPane.getBorder().getBorderInsets(scrollPane) :
        			new Insets(0, 0, 0, 0);

        this.add(scrollPane, JLayeredPane.DEFAULT_LAYER);
        this.add(new GTStationNamesOverlay(view, borderInsets.top, borderInsets.left), new Integer(400));
        view.addListener(new AbstractGTViewListener() {
            @Override
            public void settingsChanged(GTViewSettings settings) {
                updateScrollBars(settings.get(Key.ORIENTATION, GTOrientation.class));
            }
        });
    }

    protected void updateScrollBars(GTOrientation orientation) {
        scrollPane.setHorizontalScrollBarPolicy(orientation == GTOrientation.LEFT_RIGHT ? JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS : JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(orientation == GTOrientation.LEFT_RIGHT ? JScrollPane.VERTICAL_SCROLLBAR_NEVER : JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
