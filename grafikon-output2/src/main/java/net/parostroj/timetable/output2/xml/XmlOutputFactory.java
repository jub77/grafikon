package net.parostroj.timetable.output2.xml;

import java.nio.charset.Charset;
import net.parostroj.timetable.output2.Output;
import net.parostroj.timetable.output2.OutputFactory;

/**
 * Xml output factory.
 *
 * @author jub
 */
public class XmlOutputFactory extends OutputFactory {

    private static final String TYPE = "xml";

    private Charset getCharset() {
        Charset charset = (Charset) this.getParameter("charset");
        if (charset == null) {
            charset = Charset.forName("utf-8");
        }
        return charset;
    }

    @Override
    public Output createOutput(String type) {
        if ("starts".equals(type))
            return new XmlStartPositionsOutput(this.getCharset());
        else if ("ends".equals(type))
            return new XmlEndPositionsOutput(this.getCharset());
        else if ("stations".equals(type))
            return new XmlStationTimetablesOutput(this.getCharset());
        else
            throw new RuntimeException("Unknown type.");
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
