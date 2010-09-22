package net.parostroj.timetable.output2.html.groovy;

import groovy.lang.Writable;
import groovy.text.Template;
import java.io.*;
import java.util.*;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.impl.SelectionHelper;
import net.parostroj.timetable.output2.impl.TrainTimetables;
import net.parostroj.timetable.output2.impl.TrainTimetablesExtractor;
import net.parostroj.timetable.output2.util.ResourceHelper;

/**
 * Implements html output for train timetables.
 *
 * @author jub
 */
public class GspTrainTimetablesOutput extends GspOutput {

    public GspTrainTimetablesOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // extract positions
            TrainTimetablesExtractor tte = new TrainTimetablesExtractor(diagram, SelectionHelper.selectTrains(params, diagram));
            TrainTimetables timetables = tte.getTrainTimetables();

            // call template
            Map<String, Object> map = new HashMap<String, Object>();
            Set<String> images = new HashSet<String>();
            map.put("trains", timetables);
            map.put("images", images);
            ResourceHelper.addTextsToMap(map, "trains_", this.getLocale(), "texts/html_texts");

            Template template = this.createTemplate(params, "/templates/groovy/trains.gsp");
            Writable result = template.make(map);
            Writer writer = new OutputStreamWriter(stream, "utf-8");
            result.writeTo(writer);
            writer.flush();

            // write images if possible
            if (params.paramExist(DefaultOutputParam.OUTPUT_FILE)) {
                File file = (File)params.getParam(DefaultOutputParam.OUTPUT_FILE).getValue();
                file = file.getParentFile();
                // for all images ...
                ImageSaver saver = new ImageSaver(diagram);
                for (String image : images) {
                    saver.saveImage(image, file);
                }
            }

        } catch (Exception e) {
            throw new OutputException(e);
        }
    }
}
