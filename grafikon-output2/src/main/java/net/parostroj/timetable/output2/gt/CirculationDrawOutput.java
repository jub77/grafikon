package net.parostroj.timetable.output2.gt;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Locale;

import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.output2.DrawParams;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;

/**
 * Draw output for circulations.
 *
 * @author jub
 */
public class CirculationDrawOutput extends DrawOutput implements DrawParams {

    public CirculationDrawOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        Collection<TrainsCycle> circulations = this.getCirculations(params, diagram);
        CirculationDrawParams cdParams = this.getParams(params);

        CirculationDraw draw = new CirculationDraw(circulations, cdParams.getFrom(), cdParams.getTo(), cdParams.getStep());

        this.draw(cdParams.getOutputType(), stream, draw);
    }

    private CirculationDrawParams getParams(OutputParams params) {
        CirculationDrawParams cdParams = params.getParamValue(CD_PARAMS, CirculationDrawParams.class);
        if (cdParams == null) {
            // create default values
            cdParams = new CirculationDrawParams(0, TimeInterval.DAY, 5, GTDraw.OutputType.SVG);
        }
        return cdParams;
    }

    @SuppressWarnings("unchecked")
    private Collection<TrainsCycle> getCirculations(OutputParams params, TrainDiagram diagram) throws OutputException {
        Collection<TrainsCycle> circulations = params.getParamValue(CIRCULATIONS_PARAM, Collection.class);
        if (circulations == null) {
            circulations = diagram.getDriverCycles();
        }
        return circulations;
    }

    private void draw(GTDraw.OutputType outputType, OutputStream stream, final CirculationDraw draw) throws OutputException {
        this.draw(new Image() {

            @Override
            public Dimension getSize(Graphics2D g) {
                draw.updateValues(g);
                return draw.getSize();
            }

            @Override
            public void draw(Graphics2D g) {
                draw.draw(g);
            }
        }, outputType, stream);
    }
}
