package net.parostroj.timetable.model.save;

import net.parostroj.timetable.model.*;

public class TrainsDataDto {

    private final SortPattern trainSortPattern;
    private final TextTemplate trainNameTemplate;
    private final TextTemplate trainCompleteNameTemplate;
    private final Script runningTimeScript;

    public TrainsDataDto(TextTemplate trainNameTemplate, TextTemplate trainCompleteNameTemplate,
            SortPattern trainSortPattern, Script runningTimeScript) {
        this.trainNameTemplate = trainNameTemplate;
        this.trainCompleteNameTemplate = trainCompleteNameTemplate;
        this.trainSortPattern = trainSortPattern;
        this.runningTimeScript = runningTimeScript;
    }

    public SortPattern getTrainSortPattern() {
        return trainSortPattern;
    }

    public TextTemplate getTrainNameTemplate() {
        return trainNameTemplate;
    }

    public TextTemplate getTrainCompleteNameTemplate() {
        return trainCompleteNameTemplate;
    }

    public Script getRunningTimeScript() {
        return runningTimeScript;
    }

    public void copyValuesTo(TrainsData data) {
        data.setTrainNameTemplate(trainNameTemplate);
        data.setTrainCompleteNameTemplate(trainCompleteNameTemplate);
        data.setTrainSortPattern(trainSortPattern);
        data.setRunningTimeScript(runningTimeScript);
    }
}
