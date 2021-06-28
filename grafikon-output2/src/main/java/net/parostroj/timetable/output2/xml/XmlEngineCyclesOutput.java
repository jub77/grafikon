package net.parostroj.timetable.output2.xml;

import net.parostroj.timetable.output2.impl.EngineCycles;

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
import net.parostroj.timetable.output2.impl.EngineCyclesExtractor;
import net.parostroj.timetable.output2.util.SelectionHelper;

/**
 * Xml output for engine cycles.
 *
 * @author jub
 */
class XmlEngineCyclesOutput extends OutputWithCharset {

    public XmlEngineCyclesOutput(Locale locale, Charset charset) {
        super(locale, charset);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            EngineCyclesExtractor tuce = new EngineCyclesExtractor(SelectionHelper.selectCycles(params, diagram, diagram.getEngineCycleType()));
            EngineCycles cycles = new EngineCycles(tuce.getEngineCycles());

            JAXBContext context = JAXBContext.newInstance(EngineCycles.class);
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
