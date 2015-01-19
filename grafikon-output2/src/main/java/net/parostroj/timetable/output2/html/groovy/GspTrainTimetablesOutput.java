package net.parostroj.timetable.output2.html.groovy;

import groovy.text.Template;

import java.io.File;
import java.io.OutputStream;
import java.util.*;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.DefaultOutputParam;
import net.parostroj.timetable.output2.ImageSaver;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.impl.TrainTimetables;
import net.parostroj.timetable.output2.impl.TrainTimetablesExtractor;
import net.parostroj.timetable.output2.util.ResourceHelper;
import net.parostroj.timetable.output2.util.SelectionHelper;

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
            // title page
            boolean titlePage = false;
            if (params.paramExistWithValue("title.page"))
                titlePage = params.getParam("title.page").getValue(Boolean.class);
            String pageSort = "two_sides"; //"one_side";
            if (params.paramExistWithValue("page.sort"))
                pageSort = params.getParam("page.sort").getValue(String.class);

            // extract tts
            List<Train> trains = SelectionHelper.selectTrains(params, diagram);
            List<Route> routes = SelectionHelper.getRoutes(params, diagram, trains);
            TrainsCycle cycle = SelectionHelper.getDriverCycle(params);
            TrainTimetablesExtractor tte = new TrainTimetablesExtractor(diagram, trains, routes, cycle, this.getLocale());
            TrainTimetables timetables = tte.getTrainTimetables();

            // call template
            Map<String, Object> map = new HashMap<String, Object>();
            Set<String> images = new HashSet<String>();
            map.put("trains", timetables);
            map.put("images", images);
            map.put("title_page", titlePage);
            map.put("page_sort", pageSort);
            map.put("freight", true);
            ResourceHelper.addTextsToMap(map, "dc_", this.getLocale(), "texts/html_texts");
            ResourceHelper.addTextsToMap(map, "trains_", this.getLocale(), "texts/html_texts");
            this.addContext(params, map);

            if (params.paramExistWithValue(DefaultOutputParam.TEXT_TEMPLATE)) {
                TextTemplate textTemplate = params.getParam(DefaultOutputParam.TEXT_TEMPLATE).getValue(TextTemplate.class);
                textTemplate.evaluate(stream, map, this.getEncoding(params));
            } else {
                Template template = this.getTemplate(params, "templates/groovy/trains.gsp", this.getClass().getClassLoader());
                this.writeOutput(stream, template, map, this.getEncoding(params));
            }

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
        } catch (OutputException e) {
            throw e;
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }
}
