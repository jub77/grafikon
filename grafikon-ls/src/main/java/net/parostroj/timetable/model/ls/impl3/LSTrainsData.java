package net.parostroj.timetable.model.ls.impl3;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.LSException;

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
            trainsData.setTrainNameTemplate(TrainType.getDefaultTrainNameTemplate());
            trainsData.setTrainCompleteNameTemplate(TrainType.getDefaultTrainCompleteNameTemplate());
            trainsData.setTrainSortPattern(trainSortPattern.createSortPattern());
            trainsData.setRunningTimeScript(null);
        } catch (GrafikonException e) {
            throw new LSException(e);
        }
    }
}
