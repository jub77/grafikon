package net.parostroj.timetable.model;

import java.util.Collection;
import java.util.Locale;

import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.utils.ResourceBundleUtil;

/**
 * Type of objects in model (e.g. for selection).
 *
 * @author jub
 */
public enum ModelObjectType {

    NODE("node", Node.class, diagram -> diagram.getNet().getNodes()),
    LINE("line", Line.class, diagram -> diagram.getNet().getLines()),
    TRAIN("train", Train.class, diagram -> diagram.getTrains()),
    CIRCULATION("circulation", TrainsCycle.class, diagram -> diagram.getCycles()),
    DRIVER_CIRCULATION("driver.circulation", TrainsCycle.class, diagram -> diagram.getDriverCycleType().getCycles()),
    TRAIN_UNIT_CIRCULATION("train.unit.circulation", TrainsCycle.class, diagram -> diagram.getTrainUnitCycleType().getCycles()),
    ENGINE_CIRCULATION("engine.circulation", TrainsCycle.class, diagram -> diagram.getEngineCycleType().getCycles());

    private String key;
    private Class<?> type;
    private ObjectExtractor extractor;

    private ModelObjectType(String key, Class<?> type, ObjectExtractor extractor) {
        this.key = key;
        this.type = type;
        this.extractor = extractor;
    }

    public String getKey() {
        return key;
    }

    public Class<?> getType() {
        return type;
    }

    public <T> Collection<T> extract(TrainDiagram diagram, Class<T> type) {
        return ObjectsUtil.checkedCollection(extractor.extract(diagram), type);
    }

    public static ModelObjectType getByKey(String key) {
        for (ModelObjectType type : values()) {
            if (type.getKey().equals(key)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return key;
    }

    public String getText() {
        return getText(Locale.getDefault());
    }

    public String getText(Locale locale) {
        return ResourceBundleUtil.getBundle("net.parostroj.timetable.model.model_object_type_texts", ModelObjectType.class.getClassLoader(), locale, Locale.ENGLISH).getString(key);
    }

    private interface ObjectExtractor {
        Collection<?> extract(TrainDiagram diagram);
    }
}
