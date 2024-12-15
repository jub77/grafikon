package net.parostroj.timetable.model.ls.impl4;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.Script;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainsData;

/**
 * Storage for train types.
 *
 * @author jub
 */
@XmlRootElement(name = "trains_data")
@XmlType(propOrder = {"trainNameTemplate", "trainCompleteNameTemplate", "trainSortPattern", "runningTimeScript"})
public class LSTrainsData {

    private LSTextTemplate trainNameTemplate;
    private LSTextTemplate trainCompleteNameTemplate;
    private LSSortPattern trainSortPattern;
    private LSScript runningTimeScript;

    public LSTrainsData() {
    }

    public LSTrainsData(TrainsData data) {
        this();
        trainNameTemplate = new LSTextTemplate(data.getTrainNameTemplate());
        trainCompleteNameTemplate = new LSTextTemplate(data.getTrainCompleteNameTemplate());
        trainSortPattern = new LSSortPattern(data.getTrainSortPattern());
        Script runningTimeScript = data.getRunningTimeScript();
        this.runningTimeScript = runningTimeScript != null ? new LSScript(runningTimeScript) : null;
    }

    @XmlElement(name = "name_template")
    public LSTextTemplate getTrainNameTemplate() {
        return trainNameTemplate;
    }

    public void setTrainNameTemplate(LSTextTemplate trainNameTemplate) {
        this.trainNameTemplate = trainNameTemplate;
    }

    @XmlElement(name = "complete_name_template")
    public LSTextTemplate getTrainCompleteNameTemplate() {
        return trainCompleteNameTemplate;
    }

    public void setTrainCompleteNameTemplate(LSTextTemplate trainCompleteNameTemplate) {
        this.trainCompleteNameTemplate = trainCompleteNameTemplate;
    }

    @XmlElement(name = "sort_pattern")
    public LSSortPattern getTrainSortPattern() {
        return trainSortPattern;
    }

    public void setTrainSortPattern(LSSortPattern trainSortPattern) {
        this.trainSortPattern = trainSortPattern;
    }

    public LSScript getRunningTimeScript() {
        return runningTimeScript;
    }

    @XmlElement(name = "running_time_script")
    public void setRunningTimeScript(LSScript runningTimeScript) {
        this.runningTimeScript = runningTimeScript;
    }

    public void updateTrainsData(LSContext context) {
        TrainsData trainsData = context.getDiagram().getTrainsData();
        TextTemplate name = trainNameTemplate.createTextTemplate(context);
        trainsData.setTrainNameTemplate(name != null ? name : TrainType.getDefaultTrainNameTemplate());
        TextTemplate completeName = trainCompleteNameTemplate.createTextTemplate(context);
        trainsData.setTrainCompleteNameTemplate(completeName != null ? completeName : TrainType.getDefaultTrainCompleteNameTemplate());
        trainsData.setTrainSortPattern(trainSortPattern.createSortPattern());
        if (runningTimeScript != null) {
            trainsData.setRunningTimeScript(runningTimeScript.createScript(context));
        }
    }
}
