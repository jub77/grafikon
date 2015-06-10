package net.parostroj.timetable.output2.html.groovy;

import groovy.text.Template;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.impl.StationTimetable;
import net.parostroj.timetable.output2.impl.StationTimetablesExtractor;
import net.parostroj.timetable.output2.util.ResourceHelper;
import net.parostroj.timetable.output2.util.SelectionHelper;

/**
 * Implements html output for station timetable.
 *
 * @author jub
 */
public class GspStationTimetablesOutput extends GspOutput {

    private static final String KEY_PREFIX = "stations_";

    public GspStationTimetablesOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // show circulations in adjacent sessions
            boolean adjacentSessions = params.getParamValue("adjacent.sessions", Boolean.class, false);

            // technological time
            boolean techTime = false;
            if (params.paramExistWithValue("tech.time")) {
                techTime = params.getParam("tech.time").getValue(Boolean.class);
            }
            // extract positions
            StationTimetablesExtractor se = new StationTimetablesExtractor(diagram,
                    SelectionHelper.selectNodes(params, diagram), techTime, adjacentSessions,
                    this.getLocale());
            List<StationTimetable> timetables = se.getStationTimetables();

            // call template
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("stations", timetables);
            map.put("adjacent_sessions", adjacentSessions);
            ResourceHelper.addTextsToMap(map, KEY_PREFIX, this.getLocale(), LOCALIZATION_BUNDLE);
            map.put(TRANSLATOR, ResourceHelper.getTranslator(LOCALIZATION_BUNDLE, diagram, KEY_PREFIX));
            this.addContext(params, map);

            if (params.paramExistWithValue(PARAM_TEMPLATE)) {
                TextTemplate textTemplate = params.getParam(PARAM_TEMPLATE).getValue(TextTemplate.class);
                textTemplate.evaluate(stream, map, this.getEncoding(params));
            } else {
                Template template = this.getTemplate(params, "templates/groovy/stations.gsp", this.getClass().getClassLoader());
                this.writeOutput(stream, template, map, this.getEncoding(params));
            }
        } catch (OutputException e) {
            throw e;
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }
}
