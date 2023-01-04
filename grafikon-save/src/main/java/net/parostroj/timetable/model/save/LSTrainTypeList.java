package net.parostroj.timetable.model.save;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.*;

import static net.parostroj.timetable.model.save.LSTrainType.DEFAULT_TRAIN_COMPLETE_NAME_TEMPLATE;
import static net.parostroj.timetable.model.save.LSTrainType.DEFAULT_TRAIN_NAME_TEMPLATE;

/**
 * List of train types.
 *
 * @author jub
 */
@XmlRootElement
public class LSTrainTypeList {

    private static final Logger log = LoggerFactory.getLogger(LSTrainTypeList.class);

    private final Map<TrainType, LSTrainType> mapping;
    private final Map<String, TrainType> mappingByKey;
    private final List<TrainType> trainTypeList;
    private TrainsDataDto data;
    private String trainNameTemplate;
    private String trainCompleteNameTemplate;
    private LSSortPattern trainSortPattern;
    private LSTrainType[] trainType;

    public LSTrainTypeList() {
        mapping = new HashMap<TrainType, LSTrainType>();
        mappingByKey = new HashMap<String, TrainType>();
        trainTypeList = new LinkedList<TrainType>();
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

    public TrainsDataDto getTrainsData() {
        if (data == null)
            createData();
        return data;
    }

    private void createData() {
        try {
            data = new TrainsDataDto(
                TextTemplate.createTextTemplate(trainNameTemplate != null ? trainNameTemplate : DEFAULT_TRAIN_NAME_TEMPLATE, TextTemplate.Language.GROOVY),
                TextTemplate.createTextTemplate(trainCompleteNameTemplate != null ? trainCompleteNameTemplate : DEFAULT_TRAIN_COMPLETE_NAME_TEMPLATE, TextTemplate.Language.GROOVY),
                trainSortPattern != null ? trainSortPattern.getSortPattern() : null,
                null);
        } catch (GrafikonException e) {
            log.error("Couldn't create trains data." ,e);
        }
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
