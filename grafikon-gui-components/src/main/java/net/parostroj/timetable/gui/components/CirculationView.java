/*
 * CirculationView.java
 *
 * Created on 22.6.2011, 13:44:02
 */
package net.parostroj.timetable.gui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.parostroj.timetable.gui.actions.execution.SaveImageAction;
import net.parostroj.timetable.gui.dialogs.SaveImageDialog.Type;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.gt.CirculationDraw;
import net.parostroj.timetable.output2.gt.CirculationDrawParams;
import net.parostroj.timetable.output2.gt.GTDraw;
import net.parostroj.timetable.utils.Tuple;

/**
 * View with circulations of certain type.
 *
 * @author jub
 */
public class CirculationView extends javax.swing.JPanel implements SaveImageAction.DrawOutput {

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
    public void draw(Dimension size, File outputFile, Type type) throws OutputException {
        // ignore dimension - it is fixed for the circulation output
        OutputFactory factory = OutputFactory.newInstance("draw");
        Output output = factory.createOutput("circulations");
        Tuple<Integer> limits = this.getLimits();
        List<TrainsCycle> circulations = this.getCirculations();
        CirculationDrawParams cdParams = new CirculationDrawParams(limits.first, limits.second, stepWidth, type == Type.SVG ? GTDraw.OutputType.SVG : GTDraw.OutputType.PNG);
        output.write(output.getAvailableParams().setParam(DefaultOutputParam.OUTPUT_FILE, outputFile)
                .setParam(DefaultOutputParam.TRAIN_DIAGRAM, diagram).setParam(DrawParams.CD_PARAMS, cdParams)
                .setParam(DrawParams.CIRCULATIONS_PARAM, circulations));
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
        if (diagram != null && type != null) {
            List<TrainsCycle> circulations = getCirculations();
            Tuple<Integer> newLimits = this.getLimits();
            draw = new CirculationDraw(circulations, newLimits.first, newLimits.second, stepWidth);
        }
        this.repaint();
    }

    private Tuple<Integer> getLimits() {
        int newLowerLimit = 0, newUpperLimit = TimeInterval.DAY;
        Integer value = diagram.getAttribute(TrainDiagram.ATTR_FROM_TIME, Integer.class);
        if (value != null) {
            newLowerLimit = value.intValue();
        }
        value = diagram.getAttribute(TrainDiagram.ATTR_TO_TIME, Integer.class);
        if (value != null) {
            newUpperLimit = value.intValue();
        }
        return new Tuple<Integer>(newLowerLimit, newUpperLimit);
    }


    private List<TrainsCycle> getCirculations() {
        List<Wrapper<TrainsCycle>> wrappers = Wrapper.getWrapperList(diagram.getCycles(type));
        Collections.sort(wrappers);
        List<TrainsCycle> circulations = this.extract(wrappers);
        return circulations;
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
