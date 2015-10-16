package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.TrainsData;
import net.parostroj.timetable.model.ls.LSException;

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
        runningTimeScript = new LSScript(data.getRunningTimeScript());
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

    public void updateTrainsData(TrainsData trainsData) throws LSException {
        trainsData.setTrainNameTemplate(trainNameTemplate.createTextTemplate());
        trainsData.setTrainCompleteNameTemplate(trainCompleteNameTemplate.createTextTemplate());
        trainsData.setTrainSortPattern(trainSortPattern.createSortPattern());
        trainsData.setRunningTimeScript(runningTimeScript.createScript());
    }
}
