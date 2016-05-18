package net.parostroj.timetable.output2.xml;

import net.parostroj.timetable.output2.impl.StartPositions;

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
import net.parostroj.timetable.output2.impl.Cycles;
import net.parostroj.timetable.output2.impl.Position;
import net.parostroj.timetable.output2.impl.PositionsExtractor;

/**
 * Xml export of start positions.
 *
 * @author jub
 */
class XmlStartPositionsOutput extends OutputWithCharset {

    public XmlStartPositionsOutput(Locale locale, Charset charset) {
        super(locale, charset);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // extract positions
            PositionsExtractor pe = new PositionsExtractor(diagram);
            List<Position> engines = pe.getStartPositions(diagram.getEngineCycleType().getCycles(), null);
            List<Position> trainUnits = pe.getStartPositions(diagram.getTrainUnitCycleType().getCycles(), null);
            List<Cycles> customCycles = pe.getStartPositionsCustom(null);

            StartPositions sp = new StartPositions();
            sp.setEnginesPositions(engines);
            sp.setTrainUnitsPositions(trainUnits);
            sp.setCustomCycles(customCycles);

            JAXBContext context = JAXBContext.newInstance(StartPositions.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_ENCODING, this.getCharset().name());
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            Writer writer = new OutputStreamWriter(stream, this.getCharset());
            m.marshal(sp, writer);
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }
}
