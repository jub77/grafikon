/*
 * CirculationView.java
 *
 * Created on 22.6.2011, 13:44:02
 */
package net.parostroj.timetable.gui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.parostroj.timetable.gui.actions.execution.SaveImageAction;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.gt.CirculationDraw;

/**
 * View with circulations of certain type.
 *
 * @author jub
 */
public class CirculationView extends javax.swing.JPanel implements SaveImageAction.Image {

    private CirculationDraw draw;
    private TrainDiagram diagram;
    private TrainsCycleType type;
    private int stepWidth;

    /** Creates new form CirculationView */
    public CirculationView() {
        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(null);
        this.stepWidth = 5;
    }

    @Override
    public void paintImage(Graphics g) {
        this.paint(g);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        if (draw!= null) {
            if (draw.updateValues(g2d)) {
                this.revalidate();
            }
            draw.draw(g2d);
        }
    }

    public void setDiagram(TrainDiagram diagram) {
        this.type = null;
        this.diagram = diagram;
        this.repaintAndUpdateSize();
    }

    private void repaintAndUpdateSize() {
        int newLowerLimit = 0, newUpperLimit = TimeInterval.DAY;
        if (diagram != null && type != null) {
            Integer value = diagram.getAttribute(TrainDiagram.ATTR_FROM_TIME, Integer.class);
            if (value != null) {
                newLowerLimit = value.intValue();
            }
            value = diagram.getAttribute(TrainDiagram.ATTR_TO_TIME, Integer.class);
            if (value != null) {
                newUpperLimit = value.intValue();
            }
            List<Wrapper<TrainsCycle>> wrappers = Wrapper.getWrapperList(diagram.getCycles(type));
            Collections.sort(wrappers);
            draw = new CirculationDraw(this.extract(wrappers), newLowerLimit, newUpperLimit, stepWidth);
        }
        this.repaint();
    }

    private List<TrainsCycle> extract(List<Wrapper<TrainsCycle>> wrappers) {
        List<TrainsCycle> result = new ArrayList<TrainsCycle>(wrappers.size());
        for (Wrapper<TrainsCycle> wrapper : wrappers) {
            result.add(wrapper.getElement());
        }
        return result;
    }


    public int getCount() {
        return draw == null ? 0 : draw.getRows();
    }

    @Override
    public Dimension getPreferredSize() {
        return draw != null ? draw.getSize() : new Dimension(0,0);
    }

    public void circulationRemoved(TrainsCycle circulation) {
        this.repaintAndUpdateSize();
    }

    public void circulationAdded(TrainsCycle circulation) {
        this.repaintAndUpdateSize();
    }

    public void circulationUpdated(TrainsCycle circulation) {
        this.repaintAndUpdateSize();
    }

    public void setType(TrainsCycleType type) {
        this.type = type;
        this.repaintAndUpdateSize();
    }

    public void timeLimitsUpdated() {
        this.repaintAndUpdateSize();
    }

    public void setStepWidth(int size) {
        this.stepWidth = size;
        this.repaintAndUpdateSize();
    }
}
