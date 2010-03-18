package net.parostroj.timetable.model.save;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;
import net.parostroj.timetable.model.Language;
import net.parostroj.timetable.model.Script;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainsData;

/**
 * List of train types.
 * 
 * @author jub
 */
@XmlRootElement
public class LSTrainTypeList {
    private Map<TrainType, LSTrainType> mapping;
    
    private Map<String, TrainType> mappingByKey;
    
    private List<TrainType> trainTypeList;
    
    private TrainsData data;
    
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
    
    public TrainsData getTrainsData() {
        if (data == null)
            createData();
        return data;
    }
    
    private void createData() {
        data = new TrainsData(
                TextTemplate.createTextTemplate(trainNameTemplate, Language.MVEL),
                TextTemplate.createTextTemplate(trainCompleteNameTemplate, Language.MVEL),
                trainSortPattern != null ? trainSortPattern.getSortPattern() : null,
                Script.createScript(
                "int time = (int) Math.floor((((double) length) * scale * timeScale * 3.6) / (speed * 1000));\n" +
                "int penalty = 0;\n" +
                "if (toSpeed < speed) {\n" +
                "  int penalty1 = penaltySolver.getDecelerationPenalty(speed);\n" +
                "  int penalty2 = penaltySolver.getDecelerationPenalty(toSpeed);\n" +
                "  penalty = penalty1 - penalty2;\n" +
                "}\n" +
                "if (fromSpeed < speed) {\n" +
                "  int penalty1 = penaltySolver.getAccelerationPenalty(fromSpeed);\n" +
                "  int penalty2 = penaltySolver.getAccelerationPenalty(speed);\n" +
                "  penalty = penalty + penalty2 - penalty1;\n" +
                "}\n" +
                "time = time + (int)Math.round(penalty * 0.18d * timeScale);\n" +
                "time = ((int)((time + 40) / 60)) * 60;\n" +
                "return time;\n", Language.GROOVY));
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
