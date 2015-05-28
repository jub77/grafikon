package net.parostroj.timetable.output2.html.mvel2;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.OutputWithLocale;
import net.parostroj.timetable.output2.impl.Position;
import net.parostroj.timetable.output2.impl.PositionsExtractor;
import net.parostroj.timetable.output2.util.ResourceHelper;
import org.mvel2.templates.TemplateRuntime;

/**
 * End positions output to html.
 *
 * @author jub
 */
public class HtmlEndPositionsOutput extends OutputWithLocale {

    public HtmlEndPositionsOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        // extract positions
        PositionsExtractor pe = new PositionsExtractor(diagram);
        List<Position> engines = pe.getEndPositions(diagram.getEngineCycles(), null);
        List<Position> trainUnits = pe.getEndPositions(diagram.getTrainUnitCycles(), null);

        // call template
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("engines", engines);
        map.put("train_units", trainUnits);
        ResourceHelper.addTextsToMap(map, "end_positions_", this.getLocale(), "texts/html_texts");

        String template = ResourceHelper.readResource("templates/mvel2/end_positions.html", getClass().getClassLoader());
        String ret = (String) TemplateRuntime.eval(template, map);

        try {
            Writer writer = new OutputStreamWriter(stream, "utf-8");

            writer.write(ret);
            writer.flush();
        } catch (IOException e) {
            throw new OutputException(e);
        }
    }
}
