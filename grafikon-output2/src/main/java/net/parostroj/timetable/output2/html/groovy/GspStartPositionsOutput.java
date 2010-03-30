package net.parostroj.timetable.output2.html.groovy;

import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputWithLocale;
import net.parostroj.timetable.output2.impl.Position;
import net.parostroj.timetable.output2.impl.PositionsExtractor;
import net.parostroj.timetable.output2.util.ResourceHelper;

/**
 * Starting positions output to html.
 *
 * @author jub
 */
public class GspStartPositionsOutput extends OutputWithLocale {

    public GspStartPositionsOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputStream stream, TrainDiagram diagram) throws OutputException {
        // extract positions
        PositionsExtractor pe = new PositionsExtractor(diagram);
        List<Position> engines = pe.getStartPositionsEngines();
        List<Position> trainUnits = pe.getStartPositionsTrainUnits();

        // call template
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("engines", engines);
        map.put("train_units", trainUnits);
        ResourceHelper.addTextsToMap(map, "start_positions_", this.getLocale(), "texts/html_texts");

        SimpleTemplateEngine ste = new SimpleTemplateEngine();
        try {
            URL url = getClass().getResource("/templates/groovy/start_positions.gsp");
            Template template = ste.createTemplate(new InputStreamReader(url.openStream(), "utf-8"));
            Writable result = template.make(map);
            Writer writer = new OutputStreamWriter(stream, "utf-8");
            result.writeTo(writer);
            writer.flush();
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }
}
