package net.parostroj.timetable.output2.xml;

import net.parostroj.timetable.output2.impl.DriverCycles;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import net.parostroj.timetable.actions.TrainsCycleSort;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParam;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.OutputWithCharset;
import net.parostroj.timetable.output2.impl.DriverCyclesExtractor;

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
            DriverCyclesExtractor dce = new DriverCyclesExtractor(diagram, getCycles(params, diagram));
            DriverCycles cycles = dce.getDriverCycles();

            JAXBContext context = JAXBContext.newInstance(DriverCycles.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_ENCODING, this.getCharset().name());
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            Writer writer = new OutputStreamWriter(stream, this.getCharset());
            m.marshal(cycles, writer);
        } catch (JAXBException e) {
            throw new OutputException(e);
        }
    }

    private List<TrainsCycle> getCycles(OutputParams params, TrainDiagram diagram) {
        OutputParam param = params.getParam("cycles");
        if (param != null && param.getValue() != null) {
            return (List<TrainsCycle>) param.getValue();
        }
        TrainsCycleSort s = new TrainsCycleSort(TrainsCycleSort.Type.ASC);
        return s.sort(diagram.getCycles(TrainsCycleType.DRIVER_CYCLE));
    }
}
