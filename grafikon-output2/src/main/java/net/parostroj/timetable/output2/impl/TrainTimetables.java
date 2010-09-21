package net.parostroj.timetable.output2.impl;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Root element for train timetables.
 *
 * @author jub
 */
@XmlRootElement(name="train")
@XmlType(propOrder={"trainTimetables", "texts", "routeLengthUnit"})
public class TrainTimetables {

    private List<TrainTimetable> trainTimetables;
    private List<Text> texts;
    private String routeLengthUnit;

    public TrainTimetables() {
    }

    public TrainTimetables(List<TrainTimetable> trainTimetables) {
        this.trainTimetables = trainTimetables;
    }

    @XmlElement(name="train")
    public List<TrainTimetable> getTrainTimetables() {
        return trainTimetables;
    }

    public void setTrainTimetables(List<TrainTimetable> trainTimetables) {
        this.trainTimetables = trainTimetables;
    }

    @XmlElement(name="text")
    public List<Text> getTexts() {
        return texts;
    }

    public void setTexts(List<Text> texts) {
        this.texts = texts;
    }

    public String getRouteLengthUnit() {
        return routeLengthUnit;
    }

    public void setRouteLengthUnit(String routeLengthUnit) {
        this.routeLengthUnit = routeLengthUnit;
    }
}
