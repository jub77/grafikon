package net.parostroj.timetable.model.save.version01;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.save.LSTrainTypeList;

public class LSTransformationData {

    private final Map<Object, Integer> ids = new HashMap<>();

    private final LSTrainTypeList trainTypeList;

    public LSTransformationData(LSTrainTypeList list) {
        this.trainTypeList = list;
    }

    public int getIdForObject(Object object) {
        Integer res = ids.get(object);
        return Objects.requireNonNullElse(res, -1);
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
