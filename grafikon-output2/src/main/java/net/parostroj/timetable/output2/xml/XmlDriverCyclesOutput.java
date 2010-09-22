package net.parostroj.timetable.output2.xml;

import net.parostroj.timetable.output2.impl.DriverCycles;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.OutputWithCharset;
import net.parostroj.timetable.output2.impl.DriverCyclesExtractor;
import net.parostroj.timetable.output2.impl.SelectionHelper;

/**
 * Xml output for driver cycles.
 *
 * @author jub
 */
class XmlDriverCyclesOutput extends OutputWithCharset {

    public XmlDriverCyclesOutput(Charset charset) {
        super(charset);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            DriverCyclesExtractor dce = new DriverCyclesExtractor(diagram, SelectionHelper.selectCycles(params, diagram, TrainsCycleType.DRIVER_CYCLE));
            DriverCycles cycles = dce.getDriverCycles();

            JAXBContext context = JAXBContext.newInstance(DriverCycles.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_ENCODING, this.getCharset().name());
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            Writer writer = new OutputStreamWriter(stream, this.getCharset());
            m.marshal(cycles, writer);
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }
}
