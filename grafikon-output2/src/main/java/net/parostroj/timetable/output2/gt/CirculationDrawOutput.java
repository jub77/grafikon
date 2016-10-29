package net.parostroj.timetable.output2.gt;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.OutputStream;
import java.util.*;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Draw output for circulations.
 *
 * @author jub
 */
public class CirculationDrawOutput extends DrawOutput {

    public CirculationDrawOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        Collection<CirculationDrawParams> cdParamList = this.getParams(params, diagram);
        Collection<CirculationDraw> draws = new ArrayList<>(cdParamList.size());
        for (CirculationDrawParams cdParams : cdParamList) {
            cdParams.setLocale(getLocale());
            draws.add(new CirculationDraw(cdParams));
        }
        this.draw(this.getFileOutputType(params), stream, draws, new DrawLayout(DrawLayout.Orientation.TOP_DOWN));
    }

    private Collection<CirculationDrawParams> getParams(OutputParams params, TrainDiagram diagram) {
        Collection<?> cdParamList = params.getParamValue(CD_PARAMS, Collection.class);
        if (cdParamList == null || cdParamList.isEmpty()) {
            // create default values
            return Collections.singletonList(new CirculationDrawParams(diagram.getDriverCycleType().getCycles()));
        } else {
            return ObjectsUtil.checkedCollection(cdParamList, CirculationDrawParams.class);
        }
    }

    private void draw(FileOutputType outputType, OutputStream stream, final Collection<CirculationDraw> draws, DrawLayout layout) throws OutputException {
        this.draw(this.createImages(draws), outputType, stream, layout);
    }

    private Collection<Image> createImages(Collection<CirculationDraw> draws) {
        Collection<Image> images = new ArrayList<>(draws.size());
        for (CirculationDraw draw : draws) {
            images.add(this.createImage(draw));
        }
        return images;
    }

    private Image createImage(final CirculationDraw draw) {
        return new Image() {
            @Override
            public Dimension getSize(Graphics2D g) {
                draw.updateValues(g);
                return draw.getSize();
            }

            @Override
            public void draw(Graphics2D g) {
                draw.draw(g);
            }
        };
    }
}
