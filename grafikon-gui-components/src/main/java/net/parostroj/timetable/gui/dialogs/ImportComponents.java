package net.parostroj.timetable.gui.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Export/Import components.
 *
 * @author jub
 */
public enum ImportComponents {
    NODES("import.stations"),
    TRAIN_TYPES("import.train_types"),
    LINE_CLASSES("import.line_classes"),
    ENGINE_CLASSES("import.engine_classes"),
    TRAINS("import.trains");

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

    public Set<ObjectWithId> getObjects(TrainDiagram diagram) {
        if (diagram == null)
            return Collections.emptySet();
        Set<ObjectWithId> map = new LinkedHashSet<ObjectWithId>();
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
            case ENGINE_CLASSES:
                map.addAll(diagram.getEngineClasses());
        }
        return map;
    }

    public List<Wrapper<ObjectWithId>> getListOfWrappers(Collection<ObjectWithId> objects) {
        List<Wrapper<ObjectWithId>> list = new ArrayList<Wrapper<ObjectWithId>>(objects.size());
        for (ObjectWithId oid : objects) {
            list.add(this.getWrapper(oid));
        }
        return list;
    }

    public Wrapper<ObjectWithId> getWrapper(ObjectWithId oid) {
        return Wrapper.getWrapper(oid);
    }

    public boolean sorted() {
        return this == NODES || this == TRAINS;
    }
}
