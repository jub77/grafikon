package net.parostroj.timetable.output2.html.groovy;

import groovy.lang.Writable;
import groovy.text.Template;
import java.io.*;
import java.util.*;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.impl.SelectionHelper;
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
            StationTimetablesExtractor se = new StationTimetablesExtractor(diagram, SelectionHelper.selectNodes(params, diagram));
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
}
