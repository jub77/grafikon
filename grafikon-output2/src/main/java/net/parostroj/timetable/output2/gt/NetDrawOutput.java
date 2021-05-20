package net.parostroj.timetable.output2.gt;

import java.io.OutputStream;
import java.util.Locale;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetDrawOutput extends DrawOutput {

    private static final Logger log = LoggerFactory.getLogger(NetDrawOutput.class);

    public NetDrawOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        log.info("NetDrawDutput - writeTo");
    }
}
