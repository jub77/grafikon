package net.parostroj.timetable.output2.xml;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.actions.TrainSort;
import net.parostroj.timetable.actions.TrainSortByNodeFilter;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.impl.TrainTimetables;
import net.parostroj.timetable.output2.impl.TrainTimetablesExtractor;

/**
 * Xml output for train timetables.
 *
 * @author jub
 */
class XmlTrainTimetablesOutput extends OutputWithCharset {

    public XmlTrainTimetablesOutput(Charset charset) {
        super(charset);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // extract positions
            TrainTimetablesExtractor te = new TrainTimetablesExtractor(diagram, this.getTrains(params, diagram));
            TrainTimetables tt = te.getTrainTimetables();

            JAXBContext context = JAXBContext.newInstance(TrainTimetables.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_ENCODING, this.getCharset().name());
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            Writer writer = new OutputStreamWriter(stream, this.getCharset());
            m.marshal(tt, writer);
        } catch (JAXBException e) {
            throw new OutputException(e);
        }
    }

    private List<Train> getTrains(OutputParams params, TrainDiagram diagram) {
        if (params.paramExistWithValue("trains")) {
            OutputParam param = params.getParam("trains");
            return (List<Train>) param.getValue();
        } else if (params.paramExistWithValue("station")) {
            Node station = (Node)params.getParam("station").getValue();
            return (new TrainSortByNodeFilter()).sortAndFilter(diagram.getTrains(), station);
        } else if (params.paramExistWithValue("driver_cycle")) {
            TrainsCycle cycle = (TrainsCycle)params.getParam("driver_cycle").getValue();
            List<Train> trains = new LinkedList<Train>();
            for (TrainsCycleItem item : cycle) {
                trains.add(item.getTrain());
            }
            return trains;
        } else {
            TrainSort s = new TrainSort(
                    new TrainComparator(TrainComparator.Type.ASC,
                    diagram.getTrainsData().getTrainSortPattern()));
            return s.sort(diagram.getTrains());
        }
    }
}
