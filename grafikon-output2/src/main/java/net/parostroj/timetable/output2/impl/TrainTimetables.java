package net.parostroj.timetable.output2.impl;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Root element for train timetables.
 *
 * @author jub
 */
@XmlRootElement(name="train")
public class TrainTimetables {

    private List<TrainTimetable> trainTimetables;

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
}
