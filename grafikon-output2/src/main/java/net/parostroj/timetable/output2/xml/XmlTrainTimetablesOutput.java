package net.parostroj.timetable.output2.xml;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.util.SelectionHelper;
import net.parostroj.timetable.output2.impl.TrainTimetables;
import net.parostroj.timetable.output2.impl.TrainTimetablesExtractor;

/**
 * Xml output for train timetables.
 *
 * @author jub
 */
class XmlTrainTimetablesOutput extends OutputWithCharset {

    public XmlTrainTimetablesOutput(Locale locale, Charset charset) {
        super(locale, charset);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // extract tts
            List<Train> trains = SelectionHelper.selectTrains(params, diagram);
            List<Route> routes = SelectionHelper.getRoutes(params, diagram, trains);
            TrainsCycle cycle = SelectionHelper.getDriverCycle(params);
            TrainTimetablesExtractor te = new TrainTimetablesExtractor(diagram, trains, routes, cycle);
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
