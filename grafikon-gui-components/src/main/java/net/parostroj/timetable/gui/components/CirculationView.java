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
import java.util.*;

import net.parostroj.timetable.gui.actions.execution.SaveImageAction;
import net.parostroj.timetable.gui.dialogs.SaveImageDialog.Type;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.gt.*;
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
    private CirculationDrawColors drawColors;
    private int stepWidth;
    private float zoom;

    /** Creates new form CirculationView */
    public CirculationView() {
        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(null);
        this.stepWidth = 5;
        this.zoom = 1f;
    }


    @Override
    public void draw(Dimension size, File outputFile, Type type) throws OutputException {
        // ignore dimension - it is fixed for the circulation output
        OutputFactory factory = OutputFactory.newInstance("draw");
        Output output = factory.createOutput("circulations");
        Tuple<Integer> limits = this.getLimits();
        List<TrainsCycle> circulations = this.getCirculations();
        CirculationDrawParams cdParams = new CirculationDrawParams(circulations).setFrom(limits.first)
                .setTo(limits.second).setWidthInChars(stepWidth).setZoom(zoom).setColors(drawColors);
        output.write(output.getAvailableParams().setParam(Output.PARAM_OUTPUT_FILE, outputFile)
                .setParam(Output.PARAM_TRAIN_DIAGRAM, diagram).setParam(DrawParams.CD_PARAMS, Arrays.asList(cdParams))
                .setParam(DrawParams.OUTPUT_TYPE, type == Type.SVG ? FileOutputType.SVG : FileOutputType.PNG));
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
            CirculationDrawParams params = new CirculationDrawParams(circulations).setFrom(newLimits.first)
                    .setTo(newLimits.second).setWidthInChars(stepWidth).setZoom(zoom).setColors(drawColors);
            draw = new CirculationDraw(params);
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

    public void setDrawColors(CirculationDrawColors drawColors) {
        this.drawColors = drawColors;
        this.repaintAndUpdateSize();
    }

    public void timeLimitsUpdated() {
        this.repaintAndUpdateSize();
    }

    public void setStepWidth(int size) {
        this.stepWidth = size;
        this.repaintAndUpdateSize();
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
        this.repaintAndUpdateSize();
    }
}
