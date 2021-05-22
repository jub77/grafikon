package net.parostroj.timetable.output2.net;

import java.awt.*;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Locale;
import net.parostroj.timetable.model.TrainDiagram;
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
        this.draw(Collections.singletonList(createImage()),
                outputType, stream, new DrawLayout(DrawLayout.Orientation.TOP_DOWN));
    }

    private Image createImage() {
        return new Image() {
            @Override
            public Dimension getSize(Graphics2D g) {
                return null;
            }

            @Override
            public void draw(Graphics2D g) {
                // implement
            }
        };
    }
}
