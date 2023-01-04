package net.parostroj.timetable.model.ls.impl3;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.LSException;

import static net.parostroj.timetable.model.ls.impl3.LSTrainType.DEFAULT_TRAIN_COMPLETE_NAME_TEMPLATE;
import static net.parostroj.timetable.model.ls.impl3.LSTrainType.DEFAULT_TRAIN_NAME_TEMPLATE;

/**
 * Storage for train types.
 *
 * @author jub
 */
@XmlRootElement(name = "trains_data")
@XmlType(propOrder = {"trainNameTemplate", "trainCompleteNameTemplate", "trainSortPattern"})
public class LSTrainsData {

    private String trainNameTemplate;
    private String trainCompleteNameTemplate;
    private LSSortPattern trainSortPattern;

    public LSTrainsData() {
    }

    public LSTrainsData(TrainsData data) {
        this();
        trainNameTemplate = data.getTrainNameTemplate().getTemplate();
        trainCompleteNameTemplate = data.getTrainCompleteNameTemplate().getTemplate();
        trainSortPattern = new LSSortPattern(data.getTrainSortPattern());
    }

    @XmlElement(name = "name_template")
    public String getTrainNameTemplate() {
        return trainNameTemplate;
    }

    public void setTrainNameTemplate(String trainNameTemplate) {
        this.trainNameTemplate = trainNameTemplate;
    }

    @XmlElement(name = "complete_name_template")
    public String getTrainCompleteNameTemplate() {
        return trainCompleteNameTemplate;
    }

    public void setTrainCompleteNameTemplate(String trainCompleteNameTemplate) {
        this.trainCompleteNameTemplate = trainCompleteNameTemplate;
    }

    @XmlElement(name = "sort_pattern")
    public LSSortPattern getTrainSortPattern() {
        return trainSortPattern;
    }

    public void setTrainSortPattern(LSSortPattern trainSortPattern) {
        this.trainSortPattern = trainSortPattern;
    }

    public void updateTrainsData(TrainsData trainsData) throws LSException {
        try {
            trainsData.setTrainNameTemplate(TextTemplate.createTextTemplate(DEFAULT_TRAIN_NAME_TEMPLATE, TextTemplate.Language.GROOVY));
            trainsData.setTrainCompleteNameTemplate(TextTemplate.createTextTemplate(DEFAULT_TRAIN_COMPLETE_NAME_TEMPLATE, TextTemplate.Language.GROOVY));
            trainsData.setTrainSortPattern(trainSortPattern.createSortPattern());
            trainsData.setRunningTimeScript(Script.createScript(
                    "int time = (int) Math.floor((((double) length) * scale * timeScale * 3.6) / (speed * 1000));\n" +
                    "int penalty = 0;\n" +
                    "if (toSpeed < speed) {\n" +
                    "  int penalty1 = penaltySolver.getDecelerationPenalty(speed);\n" +
                    "  int penalty2 = penaltySolver.getDecelerationPenalty(toSpeed);\n" +
                    "  penalty = penalty1 - penalty2;\n" +
                    "}\n" +
                    "if (fromSpeed < speed) {\n" +
                    "  int penalty1 = penaltySolver.getAccelerationPenalty(fromSpeed);\n" +
                    "  int penalty2 = penaltySolver.getAccelerationPenalty(speed);\n" +
                    "  penalty = penalty + penalty2 - penalty1;\n" +
                    "}\n" +
                    "time = time + (int)Math.round(penalty * 0.18d * timeScale);\n" +
                    "time = time + addedTime;\n" +
                    "time = ((int)((time + 40) / 60)) * 60;\n" +
                    "return time;\n", Script.Language.GROOVY));
        } catch (GrafikonException e) {
            throw new LSException(e);
        }
    }
}
