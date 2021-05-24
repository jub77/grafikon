package net.parostroj.timetable.output2.net;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.util.mxRectangle;
import groovy.lang.Closure;
import java.awt.*;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Locale;
import java.util.function.Function;
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

public class NetDrawOutput extends DrawOutput {

    public NetDrawOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        FileOutputType outputType = this.getFileOutputType(params);
        LengthUnit lu = this.getLengthUnit(params);
        SpeedUnit su = this.getSpeedUnit(params);
        NetGraphAdapter nga = new NetGraphAdapter(diagram.getNet().getGraph(),
                getNodeConversion(params),
                getLineConversion(params, lu, su));
        getNodeConversion(params);
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

    private Function<Node, String> getNodeConversion(OutputParams params) {
        Object closure = params.getParamValue("node.to.text", Object.class);
        if (closure == null) {
            return new NodeToTextBasic();
        } else {
            if (!(closure instanceof Closure)) {
                throw new IllegalArgumentException("Parameter node.to.text is not closure");
            }
            return node -> ((Closure<?>) closure).call(node).toString();
        }
    }

    private Function<Line, String> getLineConversion(OutputParams params, LengthUnit lu, SpeedUnit su) {
        Object closure = params.getParamValue("line.to.text", Object.class);
        if (closure == null) {
            return new LineToStringBasic(() -> lu, () -> su);
        } else {
            if (!(closure instanceof Closure)) {
                throw new IllegalArgumentException("Parameter line.to.text is not closure");
            }
            return line -> ((Closure<?>) closure).call(line).toString();
        }
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
