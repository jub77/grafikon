package net.parostroj.timetable.output2.xml;

import net.parostroj.timetable.output2.impl.StationTimetables;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import net.parostroj.timetable.actions.NodeFilter;
import net.parostroj.timetable.actions.NodeSort;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParam;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.OutputWithCharset;
import net.parostroj.timetable.output2.impl.StationTimetablesExtractor;

/**
 * Xml output for station timetables.
 *
 * @author jub
 */
class XmlStationTimetablesOutput extends OutputWithCharset {

    public XmlStationTimetablesOutput(Charset charset) {
        super(charset);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // extract positions
            StationTimetablesExtractor se = new StationTimetablesExtractor(diagram, this.getNodes(params, diagram));
            StationTimetables st = new StationTimetables(se.getStationTimetables());

            JAXBContext context = JAXBContext.newInstance(StationTimetables.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_ENCODING, this.getCharset().name());
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            Writer writer = new OutputStreamWriter(stream, this.getCharset());
            m.marshal(st, writer);
        } catch (JAXBException e) {
            throw new OutputException(e);
        }
    }

    private List<Node> getNodes(OutputParams params, TrainDiagram diagram) {
        OutputParam param = params.getParam("stations");
        if (param != null && param.getValue() != null) {
            return (List<Node>) param.getValue();
        }
        NodeSort s = new NodeSort(NodeSort.Type.ASC);
        return s.sort(diagram.getNet().getNodes(), new NodeFilter() {

            @Override
            public boolean check(Node node) {
                return node.getType().isStation() || node.getType().isStop();
            }
        });
    }
}
