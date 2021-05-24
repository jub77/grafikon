package net.parostroj.timetable.output2.net;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.util.mxRectangle;
import java.awt.*;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Locale;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.gt.DrawLayout;
import net.parostroj.timetable.output2.gt.DrawOutput;
import net.parostroj.timetable.output2.gt.FileOutputType;
import org.jgrapht.ListenableGraph;

public class NetDrawOutput extends DrawOutput {

    public NetDrawOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        FileOutputType outputType = this.getFileOutputType(params);
        LengthUnit lu = this.getLengthUnit(params);
        SpeedUnit su = this.getSpeedUnit(params);
        NetGraphAdapter nga = new NetGraphAdapter(diagram.getNet().getGraph(), Node::getName, line -> "");
//                () -> lu, () -> su);
        this.draw(Collections.singletonList(createImage(nga)),
                outputType, stream, new DrawLayout(DrawLayout.Orientation.TOP_DOWN));
    }

    private LengthUnit getLengthUnit(OutputParams params) {
        String luStr = params.getParamValue("length.unit", String.class, "cm");
        LengthUnit lu = LengthUnit.getByKey(luStr);
        return lu != null ? lu : LengthUnit.CM;
    }

    private SpeedUnit getSpeedUnit(OutputParams params) {
        String suStr = params.getParamValue("speed.unit", String.class, "kmph");
        SpeedUnit su = SpeedUnit.getByKey(suStr);
        return su != null ? su : SpeedUnit.KMPH;
    }

    private Image createImage(NetGraphAdapter nga) {
        return new Image() {
            @Override
            public Dimension getSize(Graphics2D g) {
                mxRectangle bounds = nga.getGraphBounds();
                int border = nga.getBorder();
                return new Dimension(
                        (int) Math.round(bounds.getX() + bounds.getWidth()) + border
                                + 1, (int) Math.round(bounds.getY()
                        + bounds.getHeight())
                        + border + 1);
            }

            @Override
            public void draw(Graphics2D g) {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                nga.drawGraph(new mxGraphics2DCanvas(g));
            }
        };
    }
}
