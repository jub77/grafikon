package net.parostroj.timetable.model.library;

import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.TrainType;

/**
 * Types of items that can shared in the library.
 *
 * @author jub
 */
public enum LibraryItemType {
    NODE(Node.class), OUTPUT_TEMPLATE(OutputTemplate.class), ENGINE_CLASS(EngineClass.class),
    TRAIN_TYPE(TrainType.class), LINE_CLASS(LineClass.class);

    private Class<?> clazz;

    private LibraryItemType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getItemClass() {
        return clazz;
    }

    public static LibraryItemType getByItemClass(Class<?> clazz) {
        for (LibraryItemType type : values()) {
            if (type.getItemClass().equals(clazz)) {
                return type;
            }
        }
        return null;
    }
}
