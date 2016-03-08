package net.parostroj.timetable.model.save.version01;

import java.util.HashMap;
import java.util.Map;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.save.LSTrainTypeList;

public class LSTransformationData {

    private Map<Object, Integer> ids = new HashMap<Object, Integer>();
    
    private LSTrainTypeList trainTypeList;
    
    public LSTransformationData(LSTrainTypeList list) {
        this.trainTypeList = list;
    }

    public int getIdForObject(Object object) {
        Integer res = ids.get(object);
        if (res == null) {
            return -1;
        } else {
            return res;
        }
    }

    public void addObjectWithId(Object object, int id) {
        ids.put(object, id);
    }
    
    public String getKeyForTrainType(TrainType type) {
        return trainTypeList.getLSTrainType(type).getKey();
    }

    private int c = 0;

    public int getId() {
        return c++;
    }
}
