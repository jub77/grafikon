package net.parostroj.timetable.output2.html.mvel2;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.parostroj.timetable.actions.NodeFilter;
import net.parostroj.timetable.actions.NodeSort;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputWithLocale;
import net.parostroj.timetable.output2.impl.StationTimetable;
import net.parostroj.timetable.output2.impl.StationTimetablesExtractor;
import net.parostroj.timetable.output2.util.ResourceHelper;
import org.mvel2.templates.TemplateRuntime;

/**
 * Implements html output for station timetable.
 *
 * @author jub
 */
public class HtmlStationTimetablesOutput extends OutputWithLocale {

    HtmlStationTimetablesOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputStream stream, TrainDiagram diagram) throws IOException {
        // extract positions
        StationTimetablesExtractor se = new StationTimetablesExtractor(diagram, this.getNodes(diagram));
        List<StationTimetable> timetables = se.getStationTimetables();

        // call template
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("stations", timetables);
        ResourceHelper.addTextsToMap(map, "stations_", this.getLocale(), "texts/html_texts");

        String template = ResourceHelper.readResource("/templates/mvel2/stations.html");
        String ret = (String) TemplateRuntime.eval(template, map);

        Writer writer = new OutputStreamWriter(stream, "utf-8");

        writer.write(ret);
        writer.flush();
    }

    private List<Node> getNodes(TrainDiagram diagram) {
        NodeSort s = new NodeSort(NodeSort.Type.ASC);
        return s.sort(diagram.getNet().getNodes(), new NodeFilter() {

            @Override
            public boolean check(Node node) {
                return node.getType().isStation() || node.getType().isStop();
            }
        });
    }
}
