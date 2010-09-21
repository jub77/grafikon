package net.parostroj.timetable.output2.html.groovy;

import groovy.lang.Writable;
import groovy.text.Template;
import java.io.*;
import java.util.*;
import net.parostroj.timetable.actions.NodeFilter;
import net.parostroj.timetable.actions.NodeSort;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.impl.StationTimetable;
import net.parostroj.timetable.output2.impl.StationTimetablesExtractor;
import net.parostroj.timetable.output2.util.ResourceHelper;

/**
 * Implements html output for station timetable.
 *
 * @author jub
 */
public class GspStationTimetablesOutput extends GspOutput {

    public GspStationTimetablesOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // extract positions
            StationTimetablesExtractor se = new StationTimetablesExtractor(diagram, this.getNodes(params, diagram));
            List<StationTimetable> timetables = se.getStationTimetables();

            // call template
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("stations", timetables);
            ResourceHelper.addTextsToMap(map, "stations_", this.getLocale(), "texts/html_texts");

            Template template = this.createTemplate(params, "/templates/groovy/stations.gsp");
            Writable result = template.make(map);
            Writer writer = new OutputStreamWriter(stream, "utf-8");
            result.writeTo(writer);
            writer.flush();
        } catch (Exception e) {
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
