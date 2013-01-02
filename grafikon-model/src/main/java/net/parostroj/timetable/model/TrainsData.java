package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.TrainDiagramEvent;

/**
 * Data about trains names creation a sorting, computing of times.
 *
 * @author jub
 */
public class TrainsData {

    private SortPattern trainSortPattern;
    private TextTemplate trainNameTemplate;
    private TextTemplate trainCompleteNameTemplate;
    private Script runningTimeScript;
    private TrainDiagram diagram;

    public TrainsData(TextTemplate trainNameTemplate, TextTemplate trainCompleteNameTemplate, SortPattern trainSortPattern, Script runningTimeScript) {
        this.trainNameTemplate = trainNameTemplate;
        this.trainSortPattern = trainSortPattern;
        this.trainCompleteNameTemplate = trainCompleteNameTemplate;
        this.runningTimeScript = runningTimeScript;
    }

    public TextTemplate getTrainNameTemplate() {
        return trainNameTemplate;
    }

    public void setTrainNameTemplate(TextTemplate trainNameTemplate) {
        TextTemplate oldValue = this.trainNameTemplate;
        this.trainNameTemplate = trainNameTemplate;
        this.diagram.fireEvent(new TrainDiagramEvent(diagram,
                new AttributeChange(TrainDiagram.ATTR_TRAIN_NAME_TEMPLATE,
                oldValue, this.trainNameTemplate)));
    }

    public TextTemplate getTrainCompleteNameTemplate() {
        return trainCompleteNameTemplate;
    }

    public void setTrainCompleteNameTemplate(TextTemplate trainCompleteNameTemplate) {
        TextTemplate oldValue = this.trainCompleteNameTemplate;
        this.trainCompleteNameTemplate = trainCompleteNameTemplate;
        this.diagram.fireEvent(new TrainDiagramEvent(diagram,
                new AttributeChange(TrainDiagram.ATTR_TRAIN_COMPLETE_NAME_TEMPLATE,
                oldValue, this.trainCompleteNameTemplate)));
    }

    public SortPattern getTrainSortPattern() {
        return trainSortPattern;
    }

    public void setTrainSortPattern(SortPattern trainSortPattern) {
        SortPattern oldValue = this.trainSortPattern;
        this.trainSortPattern = trainSortPattern;
        this.diagram.fireEvent(new TrainDiagramEvent(diagram, new AttributeChange("train.sort.pattern", oldValue, this.trainSortPattern)));
    }

    public Script getRunningTimeScript() {
        return runningTimeScript;
    }

    public void setRunningTimeScript(Script runningTimeScript) {
        Script oldValue = this.runningTimeScript;
        this.runningTimeScript = runningTimeScript;
        this.diagram.fireEvent(new TrainDiagramEvent(diagram, new AttributeChange("running.script", oldValue, this.runningTimeScript)));
    }

    void setDiagram(TrainDiagram diagram) {
        this.diagram = diagram;
    }
}
