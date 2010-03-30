package net.parostroj.timetable.output2.html.groovy;

import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import java.io.*;
import java.net.URL;
import java.util.*;
import net.parostroj.timetable.actions.NodeFilter;
import net.parostroj.timetable.actions.NodeSort;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputWithLocale;
import net.parostroj.timetable.output2.impl.StationTimetable;
import net.parostroj.timetable.output2.impl.StationTimetablesExtractor;
import net.parostroj.timetable.output2.util.ResourceHelper;

/**
 * Implements html output for station timetable.
 *
 * @author jub
 */
public class GspStationTimetablesOutput extends OutputWithLocale {

    GspStationTimetablesOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputStream stream, TrainDiagram diagram) throws OutputException {
        // extract positions
        StationTimetablesExtractor se = new StationTimetablesExtractor(diagram, this.getNodes(diagram));
        List<StationTimetable> timetables = se.getStationTimetables();

        // call template
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("stations", timetables);
        ResourceHelper.addTextsToMap(map, "stations_", this.getLocale(), "texts/html_texts");

        SimpleTemplateEngine ste = new SimpleTemplateEngine();
        try {
            URL url = getClass().getResource("/templates/groovy/stations.gsp");
            Template template = ste.createTemplate(new InputStreamReader(url.openStream(), "utf-8"));
            Writable result = template.make(map);
            Writer writer = new OutputStreamWriter(stream, "utf-8");
            result.writeTo(writer);
            writer.flush();
        } catch (Exception e) {
            throw new OutputException(e);
        }
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
