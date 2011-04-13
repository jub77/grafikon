package net.parostroj.timetable.output2.html.groovy;

import groovy.text.Template;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.DefaultOutputParam;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.impl.Position;
import net.parostroj.timetable.output2.impl.PositionsExtractor;
import net.parostroj.timetable.output2.util.ResourceHelper;

/**
 * End positions output to html.
 *
 * @author jub
 */
public class GspEndPositionsOutput extends GspOutput {

    public GspEndPositionsOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // extract positions
            PositionsExtractor pe = new PositionsExtractor(diagram);
            List<Position> engines = pe.getEndPositionsEngines();
            List<Position> trainUnits = pe.getEndPositionsTrainUnits();

            // call template
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("engines", engines);
            map.put("train_units", trainUnits);
            ResourceHelper.addTextsToMap(map, "end_positions_", this.getLocale(), "texts/html_texts");

            if (params.paramExistWithValue(DefaultOutputParam.TEXT_TEMPLATE)) {
                TextTemplate textTemplate = params.getParam(DefaultOutputParam.TEXT_TEMPLATE).getValue(TextTemplate.class);
                textTemplate.evaluate(stream, map);
            } else {
                Template template = this.getTemplate(params, "templates/groovy/end_positions.gsp", this.getClass().getClassLoader());
                this.writeOutput(stream, template, map);
            }
        } catch (OutputException e) {
            throw e;
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }
}
