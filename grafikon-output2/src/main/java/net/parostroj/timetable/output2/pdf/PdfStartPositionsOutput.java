package net.parostroj.timetable.output2.pdf;

import java.io.*;
import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.impl.Position;
import net.parostroj.timetable.output2.impl.PositionsExtractor;
import net.parostroj.timetable.output2.impl.StartPositions;

/**
 * Pdf output for start positions.
 *
 * @author jub
 */
class PdfStartPositionsOutput extends PdfOutput {

    public PdfStartPositionsOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // extract positions
            PositionsExtractor pe = new PositionsExtractor(diagram);
            List<Position> engines = pe.getStartPositionsEngines();
            List<Position> trainUnits = pe.getStartPositionsTrainUnits();

            // call template
            StartPositions sp = new StartPositions();
            sp.setEnginesPositions(engines);
            sp.setTrainUnitsPositions(trainUnits);

            JAXBContext context = JAXBContext.newInstance(StartPositions.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            m.marshal(sp, os);
            os.flush();

            byte[] bytes = os.toByteArray();
            System.out.println(new String(bytes, "utf-8"));
            InputStream xml = new ByteArrayInputStream(bytes);

            if (params.paramExistWithValue(DefaultOutputParam.TEXT_TEMPLATE)) {
                TextTemplate textTemplate = params.getParam(DefaultOutputParam.TEXT_TEMPLATE).getValue(TextTemplate.class);
                textTemplate.evaluate(stream, Collections.<String, Object>singletonMap("stream", xml), this.getEncoding(params));
            } else {
                InputStream xsl = null;
                if (params.containsKey(DefaultOutputParam.TEMPLATE_STREAM)) {
                    xsl = params.getParam(DefaultOutputParam.TEMPLATE_STREAM).getValue(InputStream.class);
                } else {
                    xsl = this.getXslStream(params, "templates/pdf/start_positions.xsl", this.getClass().getClassLoader());
                }
                this.writeOutput(stream, xsl, xml);
            }
        } catch (OutputException e) {
            throw e;
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }

}
