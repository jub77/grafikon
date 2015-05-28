package net.parostroj.timetable.output2.html.groovy;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.DefaultOutputParam;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.util.ResourceHelper;

/**
 * Implements output for the whole diagram.
 *
 * @author jub
 */
public class GspDiagramOutput extends GspOutput {

    public GspDiagramOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // call template
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("diagram", diagram);
            map.put(TRANSLATOR, ResourceHelper.getTranslator(LOCALIZATION_BUNDLE, diagram));
            this.addContext(params, map);

            if (params.paramExistWithValue(DefaultOutputParam.TEXT_TEMPLATE)) {
                TextTemplate textTemplate = params.getParam(DefaultOutputParam.TEXT_TEMPLATE).getValue(TextTemplate.class);
                textTemplate.evaluate(stream, map, this.getEncoding(params));
            } else {
                // do nothing - no default template exists.
            }
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }
}
