package net.parostroj.timetable.model;

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
        this.trainNameTemplate = trainNameTemplate;
    }

    public TextTemplate getTrainCompleteNameTemplate() {
        return trainCompleteNameTemplate;
    }

    public void setTrainCompleteNameTemplate(TextTemplate trainCompleteNameTemplate) {
        this.trainCompleteNameTemplate = trainCompleteNameTemplate;
    }

    public SortPattern getTrainSortPattern() {
        return trainSortPattern;
    }

    public void setTrainSortPattern(SortPattern trainSortPattern) {
        this.trainSortPattern = trainSortPattern;
    }

    public Script getRunningTimeScript() {
        return runningTimeScript;
    }

    public void setRunningTimeScript(Script runningTimeScript) {
        this.runningTimeScript = runningTimeScript;
    }
}
