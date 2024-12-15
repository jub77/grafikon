package net.parostroj.timetable.model.save;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlRootElement;

import net.parostroj.timetable.model.*;

/**
 * List of train types.
 *
 * @author jub
 */
@XmlRootElement
public class LSTrainTypeList {

    private final Map<TrainType, LSTrainType> mapping;
    private final Map<String, TrainType> mappingByKey;
    private final List<TrainType> trainTypeList;
    private TrainsDataDto data;
    private String trainNameTemplate;
    private String trainCompleteNameTemplate;
    private LSSortPattern trainSortPattern;
    private LSTrainType[] trainType;

    public LSTrainTypeList() {
        mapping = new HashMap<>();
        mappingByKey = new HashMap<>();
        trainTypeList = new LinkedList<>();
    }

    public LSTrainTypeList(List<TrainType> list, TrainsData data) {
        this();
        trainType = new LSTrainType[list.size()];
        int i = 0;
        for (TrainType type : list) {
            LSTrainType lsTrainType = new LSTrainType(type, Integer.toString(i));
            mapping.put(type, lsTrainType);
            trainType[i++] = lsTrainType;
        }
        trainNameTemplate = data.getTrainNameTemplate().getTemplate();
        trainCompleteNameTemplate = data.getTrainCompleteNameTemplate().getTemplate();
        trainSortPattern = new LSSortPattern(data.getTrainSortPattern());
    }

    public List<TrainType> getTrainTypeList() {
        return trainTypeList;
    }

    public TrainsDataDto getTrainsData(TrainDiagramType diagramType) {
        if (data == null)
            createData(diagramType);
        return data;
    }

    private void createData(TrainDiagramType diagramType) {
        TextTemplate trainName = trainNameTemplate != null ? diagramType.createTextTemplate(trainNameTemplate, TextTemplate.Language.GROOVY) : null;
        TextTemplate trainCompleteName = trainCompleteNameTemplate != null ? diagramType.createTextTemplate(trainCompleteNameTemplate, TextTemplate.Language.GROOVY) : null;
        data = new TrainsDataDto(
                trainName != null ? trainName : TrainType.getDefaultTrainNameTemplate(),
                trainCompleteName != null ? trainCompleteName : TrainType.getDefaultTrainCompleteNameTemplate(),
                trainSortPattern != null ? trainSortPattern.getSortPattern() : null,
                null);
    }

    public TrainType getTrainType(String key) {
        return mappingByKey.get(key);
    }

    public LSTrainType getLSTrainType(TrainType trainType) {
        return mapping.get(trainType);
    }

    public LSTrainType[] getTrainType() {
        return trainType;
    }

    public String getTrainNameTemplate() {
        return trainNameTemplate;
    }

    public void setTrainNameTemplate(String trainNameTemplate) {
        this.trainNameTemplate = trainNameTemplate;
    }

    public String getTrainCompleteNameTemplate() {
        return trainCompleteNameTemplate;
    }

    public void setTrainCompleteNameTemplate(String trainCompleteNameTemplate) {
        this.trainCompleteNameTemplate = trainCompleteNameTemplate;
    }

    public LSSortPattern getTrainSortPattern() {
        return trainSortPattern;
    }

    public void setTrainSortPattern(LSSortPattern trainSortPattern) {
        this.trainSortPattern = trainSortPattern;
    }

    public void setTrainType(LSTrainType[] trainType) {
        this.trainType = trainType;
    }

    public void updateMapping(TrainDiagram diagram) {
        // create mappings
        if (trainType != null) {
            for(LSTrainType lsTrainType : trainType) {
                TrainType tt = lsTrainType.convertToTrainType(diagram);
                mappingByKey.put(lsTrainType.getKey(), tt);
                trainTypeList.add(tt);
            }
        }
    }
}
