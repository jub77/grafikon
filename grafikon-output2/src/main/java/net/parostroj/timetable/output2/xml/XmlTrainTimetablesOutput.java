package net.parostroj.timetable.output2.xml;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.impl.SelectionHelper;
import net.parostroj.timetable.output2.impl.TrainTimetables;
import net.parostroj.timetable.output2.impl.TrainTimetablesExtractor;

/**
 * Xml output for train timetables.
 *
 * @author jub
 */
class XmlTrainTimetablesOutput extends OutputWithCharset {

    public XmlTrainTimetablesOutput(Charset charset) {
        super(charset);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // extract positions
            TrainTimetablesExtractor te = new TrainTimetablesExtractor(diagram, SelectionHelper.selectTrains(params, diagram));
            TrainTimetables tt = te.getTrainTimetables();

            JAXBContext context = JAXBContext.newInstance(TrainTimetables.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_ENCODING, this.getCharset().name());
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            Writer writer = new OutputStreamWriter(stream, this.getCharset());
            m.marshal(tt, writer);
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }
}
