package net.parostroj.timetable.output2.xml;

import net.parostroj.timetable.output2.impl.EndPositions;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.OutputWithCharset;
import net.parostroj.timetable.output2.impl.Position;
import net.parostroj.timetable.output2.impl.PositionsExtractor;

/**
 * Xml export of end positions.
 *
 * @author jub
 */
class XmlEndPositionsOutput extends OutputWithCharset {

    public XmlEndPositionsOutput(Locale locale, Charset charset) {
        super(locale, charset);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // extract positions
            PositionsExtractor pe = new PositionsExtractor(diagram);
            List<Position> engines = pe.getEndPositions(diagram.getEngineCycleType().getCycles().toCollection(), null);
            List<Position> trainUnits = pe.getEndPositions(diagram.getTrainUnitCycleType().getCycles().toCollection(), null);

            EndPositions ep = new EndPositions();
            ep.setEnginesPositions(engines);
            ep.setTrainUnitsPositions(trainUnits);

            JAXBContext context = JAXBContext.newInstance(EndPositions.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_ENCODING, this.getCharset().name());
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            Writer writer = new OutputStreamWriter(stream, this.getCharset());

            m.marshal(ep, writer);
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }
}
