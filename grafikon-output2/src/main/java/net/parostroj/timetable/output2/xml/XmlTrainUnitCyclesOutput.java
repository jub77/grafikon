package net.parostroj.timetable.output2.xml;

import net.parostroj.timetable.output2.impl.TrainUnitCycles;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.OutputWithCharset;
import net.parostroj.timetable.output2.util.SelectionHelper;
import net.parostroj.timetable.output2.impl.TrainUnitCyclesExtractor;

/**
 * Xml output for train unit cycles.
 *
 * @author jub
 */
class XmlTrainUnitCyclesOutput extends OutputWithCharset {

    public XmlTrainUnitCyclesOutput(Locale locale, Charset charset) {
        super(locale, charset);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            TrainUnitCyclesExtractor tuce = new TrainUnitCyclesExtractor(SelectionHelper.selectCycles(params, diagram, diagram.getTrainUnitCycleType()), getLocale());
            TrainUnitCycles cards = new TrainUnitCycles(tuce.getTrainUnitCycles());

            JAXBContext context = JAXBContext.newInstance(TrainUnitCycles.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_ENCODING, this.getCharset().name());
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            Writer writer = new OutputStreamWriter(stream, this.getCharset());
            m.marshal(cards, writer);
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }
}
