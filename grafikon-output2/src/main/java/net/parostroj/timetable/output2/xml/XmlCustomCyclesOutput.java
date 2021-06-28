package net.parostroj.timetable.output2.xml;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParam;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.OutputWithCharset;
import net.parostroj.timetable.output2.impl.CustomCycles;
import net.parostroj.timetable.output2.impl.CustomCyclesExtractor;
import net.parostroj.timetable.output2.util.SelectionHelper;

/**
 * Xml output for custtom cycles.
 *
 * @author jub
 */
class XmlCustomCyclesOutput extends OutputWithCharset {

    public XmlCustomCyclesOutput(Locale locale, Charset charset) {
        super(locale, charset);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // check for type
            OutputParam param = params.get("cycle_type");
            TrainsCycleType type = param != null ? param.getValue(TrainsCycleType.class) : null;
            // extract
            CustomCyclesExtractor tuce = new CustomCyclesExtractor(SelectionHelper.selectCycles(params, diagram, type));
            CustomCycles cycles = new CustomCycles(tuce.getCycles());

            JAXBContext context = JAXBContext.newInstance(CustomCycles.class);
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
