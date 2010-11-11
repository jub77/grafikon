package net.parostroj.timetable.gui.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.parostroj.timetable.gui.helpers.Wrapper;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Export/Import components.
 *
 * @author jub
 */
public enum ImportComponents {
    TRAINS("import.trains"),
    NODES("import.stations"),
    TRAIN_TYPES("import.train_types"),
    LINE_CLASSES("import.line_classes");

    private String key;

    private ImportComponents(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return ResourceLoader.getString(key);
    }

    public Set<Object> getObjects(TrainDiagram diagram) {
        if (diagram == null)
            return Collections.emptySet();
        Set<Object> map = new HashSet<Object>();
        switch (this) {
            case NODES:
                map.addAll(diagram.getNet().getNodes());
                break;
            case TRAINS:
                map.addAll(diagram.getTrains());
                break;
            case TRAIN_TYPES:
                map.addAll(diagram.getTrainTypes());
                break;
            case LINE_CLASSES:
                map.addAll(diagram.getNet().getLineClasses());
                break;
        }
        return map;
    }

    public List<Wrapper<?>> getListOfWrappers(Set<Object> objects) {
        List<Wrapper<?>> list = new ArrayList<Wrapper<?>>(objects.size());
        for (Object oid : objects) {
            list.add(this.getWrapper(oid));
        }
        return list;
    }

    public Wrapper<?> getWrapper(Object oid) {
        return Wrapper.getWrapper(oid);
    }
}
