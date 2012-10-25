package net.parostroj.timetable.gui.dialogs;

import java.util.*;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.*;

/**
 * Export/Import components.
 *
 * @author jub
 */
public enum ImportComponent {
    NODES("import.stations", Node.class),
    TRAIN_TYPES("import.train_types", TrainType.class),
    LINE_CLASSES("import.line_classes", LineClass.class),
    ENGINE_CLASSES("import.engine_classes", EngineClass.class),
    TRAINS("import.trains", Train.class),
    OUTPUT_TEMPLATES("import.output_templates", OutputTemplate.class);

    private String key;
    private Class<?> clazz;

    private ImportComponent(String key, Class<?> clazz) {
        this.key = key;
        this.clazz = clazz;
    }

    public String getKey() {
        return key;
    }

    public Class<?> getComponentClass() {
        return clazz;
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
                break;
            case OUTPUT_TEMPLATES:
                map.addAll(diagram.getOutputTemplates());
                break;
        }
        return map;
    }

    public List<Wrapper<ObjectWithId>> getListOfWrappers(Collection<? extends ObjectWithId> objects) {
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

    public static ImportComponent getByComponentClass(Class<?> clazz) {
        for (ImportComponent comp : values()) {
            if (comp.getComponentClass().equals(clazz))
                return comp;
        }
        return null;
    }
}
