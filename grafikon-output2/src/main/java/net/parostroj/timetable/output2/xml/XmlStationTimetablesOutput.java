package net.parostroj.timetable.output2.xml;

import net.parostroj.timetable.output2.impl.StationTimetables;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.OutputWithCharset;
import net.parostroj.timetable.output2.util.SelectionHelper;
import net.parostroj.timetable.output2.impl.StationTimetablesExtractor;

/**
 * Xml output for station timetables.
 *
 * @author jub
 */
class XmlStationTimetablesOutput extends OutputWithCharset {

    public XmlStationTimetablesOutput(Locale locale, Charset charset) {
        super(locale, charset);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // show circulations in adjacent sessions
            boolean adjacentSessions = params.getParamValue("adjacent.sessions", Boolean.class, false);

            // technological times
            boolean techTime = false;
            if (params.paramExistWithValue("tech.time")) {
                techTime = params.getParam("tech.time").getValue(Boolean.class);
            }
            // extract positions
            StationTimetablesExtractor se = new StationTimetablesExtractor(diagram,
                    SelectionHelper.selectNodes(params, diagram), techTime, adjacentSessions,
                    this.getLocale());
            StationTimetables st = new StationTimetables(se.getStationTimetables());

            JAXBContext context = JAXBContext.newInstance(StationTimetables.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_ENCODING, this.getCharset().name());
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            Writer writer = new OutputStreamWriter(stream, this.getCharset());
            m.marshal(st, writer);
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }
}
