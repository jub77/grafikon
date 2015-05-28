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
import net.parostroj.timetable.output2.impl.Cycles;
import net.parostroj.timetable.output2.impl.Position;
import net.parostroj.timetable.output2.impl.PositionsExtractor;
import net.parostroj.timetable.output2.util.ResourceHelper;

/**
 * Starting positions output to html.
 *
 * @author jub
 */
public class GspStartPositionsOutput extends GspOutput {

    private static final String KEY_PREFIX = "start_positions_";

    public GspStartPositionsOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // extract positions
            PositionsExtractor pe = new PositionsExtractor(diagram);
            List<Position> engines = pe.getStartPositions(diagram.getEngineCycles());
            List<Position> trainUnits = pe.getStartPositions(diagram.getTrainUnitCycles());
            List<Cycles> customCycles = pe.getStartPositionsCustom();

            // call template
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("engines", engines);
            map.put("train_units", trainUnits);
            map.put("custom_cycles", customCycles);
            ResourceHelper.addTextsToMap(map, KEY_PREFIX, this.getLocale(), LOCALIZATION_BUNDLE);
            map.put(TRANSLATOR, ResourceHelper.getTranslator(LOCALIZATION_BUNDLE, diagram, KEY_PREFIX));
            this.addContext(params, map);

            if (params.paramExistWithValue(DefaultOutputParam.TEXT_TEMPLATE)) {
                TextTemplate textTemplate = params.getParam(DefaultOutputParam.TEXT_TEMPLATE).getValue(TextTemplate.class);
                textTemplate.evaluate(stream, map, this.getEncoding(params));
            } else {
                Template template = this.getTemplate(params, "templates/groovy/start_positions.gsp", this.getClass().getClassLoader());
                this.writeOutput(stream, template, map, this.getEncoding(params));
            }
        } catch (OutputException e) {
            throw e;
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }
}
