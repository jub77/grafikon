package net.parostroj.timetable.model.imports;

import java.util.*;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.library.LibraryItemType;

/**
 * Export/Import components.
 *
 * @author jub
 */
public enum ImportComponent {
    COMPANIES("companies", Company.class, true),
    REGIONS("regions", Region.class, true),
    NODES("stations", Node.class, true, LibraryItemType.NODE),
    LINE_CLASSES("line_classes", LineClass.class, false, LibraryItemType.LINE_CLASS),
    LINES("lines", Line.class, true),
    ROUTES("routes", Route.class, true),
    TRAIN_TYPE_CATEGORIES("train_type_categories", TrainTypeCategory.class, true, LibraryItemType.TRAIN_TYPE_CATEGORY),
    TRAIN_TYPES("train_types", TrainType.class, false, LibraryItemType.TRAIN_TYPE),
    ENGINE_CLASSES("engine_classes", EngineClass.class, true, LibraryItemType.ENGINE_CLASS),
    GROUPS("groups", Group.class, true),
    TRAINS("trains", Train.class, true),
    TRAINS_CYCLE_TYPES("cycle_types", TrainsCycleType.class, false),
    TRAINS_CYCLES("cycles", TrainsCycle.class, true),
    OUTPUT_TEMPLATES("output_templates", OutputTemplate.class, true, LibraryItemType.OUTPUT_TEMPLATE);

    private String key;
    private Class<?> clazz;
    private boolean sorted;
    private LibraryItemType libraryItemType;

    private ImportComponent(String key, Class<?> clazz, boolean sorted) {
        this(key, clazz, sorted, null);
    }

    private ImportComponent(String key, Class<?> clazz, boolean sorted, LibraryItemType libraryItemType) {
        this.key = key;
        this.clazz = clazz;
        this.libraryItemType = libraryItemType;
        this.sorted = sorted;
    }

    public String getKey() {
        return key;
    }

    public Class<?> getComponentClass() {
        return clazz;
    }

    public LibraryItemType getLibraryItemType() {
        return libraryItemType;
    }

    public Set<ObjectWithId> getObjects(TrainDiagram diagram) {
        if (diagram == null)
            return Collections.emptySet();
        Set<ObjectWithId> map = new LinkedHashSet<>();
        switch (this) {
            case COMPANIES:
                map.addAll(diagram.getCompanies());
                break;
            case REGIONS:
                map.addAll(diagram.getNet().getRegions());
                break;
            case NODES:
                map.addAll(diagram.getNet().getNodes());
                break;
            case GROUPS:
                map.addAll(diagram.getGroups());
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
            case TRAINS_CYCLES:
                map.addAll(diagram.getCycles());
                break;
            case TRAINS_CYCLE_TYPES:
                for (TrainsCycleType type : diagram.getCycleTypes()) {
                    if (!TrainsCycleType.isDefaultType(type)) {
                        map.add(type);
                    }
                }
                break;
            case LINES:
                map.addAll(diagram.getNet().getLines());
                break;
            case ROUTES:
                map.addAll(diagram.getRoutes());
                break;
            case TRAIN_TYPE_CATEGORIES:
                map.addAll(diagram.getTrainTypeCategories());
        }
        return map;
    }

    public boolean isSorted() {
        return sorted;
    }

    public static ImportComponent getByComponentClass(Class<?> clazz) {
        for (ImportComponent comp : values()) {
            if (comp.getComponentClass().equals(clazz))
                return comp;
        }
        return null;
    }
}
