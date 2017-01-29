package net.parostroj.timetable.model.imports;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Imports trains from one diagram to another.
 *
 * @author jub
 */
public abstract class Import {

    public static class ImportError {
        private final ObjectWithId object;
        private final String text;

        public ImportError(ObjectWithId object, String text) {
            this.object = object;
            this.text = text;
        }

        public ObjectWithId getObject() {
            return object;
        }

        public String getText() {
            return text;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(Import.class);

    private final ImportMatch match;
    private final TrainDiagram diagram;
    private final Map<String, Object> settings;

    protected final boolean overwrite;

    private List<ImportError> errors;
    private Set<ObjectWithId> importedObjects;

    public Import(TrainDiagram diagram, ImportMatch match, boolean overwrite) {
        this.match = match;
        this.overwrite = overwrite;
        this.diagram = diagram;
        this.errors = new LinkedList<>();
        this.importedObjects = new HashSet<>();
        this.settings = new HashMap<>();
    }

    protected ObjectWithId getObjectWithId(ObjectWithId orig) {
        ObjectWithId foundObject = null;
        if (orig instanceof TrainType) {
            foundObject = this.getTrainType((TrainType) orig);
        } else if (orig instanceof Train) {
            foundObject = this.getTrain((Train) orig);
        } else if (orig instanceof Node) {
            foundObject = this.getNode((Node) orig);
        } else if (orig instanceof LineClass) {
            foundObject = this.getLineClass((LineClass) orig);
        } else if (orig instanceof EngineClass) {
            foundObject = this.getEngineClass((EngineClass) orig);
        } else if (orig instanceof Group) {
            foundObject = this.getGroup((Group) orig);
        } else if (orig instanceof OutputTemplate) {
            foundObject = this.getOutputTemplate((OutputTemplate) orig);
        } else if (orig instanceof TrainsCycle) {
            foundObject = this.getCycle((TrainsCycle) orig);
        } else if (orig instanceof Region) {
            foundObject = this.getRegion((Region) orig);
        } else if (orig instanceof Company) {
            foundObject = this.getCompany((Company) orig);
        }
        // fallback in case of ID match (not listed items) ...
        if (match == ImportMatch.ID) {
            foundObject = diagram.getObjectById(orig.getId());
        }

        // log if not found
        if (orig != null && foundObject == null) {
            log.warn("Couldn't find object with id: {} class: {}", orig.getId(), orig.getClass().getName());
        }

        return foundObject;
    }

    protected TrainType getTrainType(TrainType origType) {
        if (origType == null) {
            return null;
        }
        if (match == ImportMatch.ID) {
            return diagram.getTrainTypes().getById(origType.getId());
        } else {
            return diagram.getTrainTypes().find(
                    type -> type.getDefaultAbbr().equals(origType.getDefaultAbbr()) && type.getDesc().equals(origType.getDesc()));
        }
    }

    protected Group getGroup(Group origGroup) {
        if (match == ImportMatch.ID) {
            return diagram.getGroups().getById(origGroup.getId());
        } else {
            return diagram.getGroups().find(group -> group.getName().equals(origGroup.getName()));
        }
    }

    protected Company getCompany(Company origCompany) {
        if (match == ImportMatch.ID) {
            return diagram.getCompanies().getById(origCompany.getId());
        } else {
            return diagram.getCompanies()
                    .find(company -> company.getAbbr().equals(origCompany.getAbbr()));
        }
    }

    protected Region getRegion(Region origRegion) {
        if (match == ImportMatch.ID) {
            return diagram.getNet().getRegions().getById(origRegion.getId());
        } else {
            return diagram.getNet().getRegions()
                    .find(region -> region.getName().equals(origRegion.getName()));
        }
    }

    protected TrainTypeCategory getTrainTypeCategory(TrainTypeCategory origCategory) {
        if (origCategory == null) {
            return null;
        }
        if (match == ImportMatch.ID) {
            return diagram.getTrainTypeCategories().getById(origCategory.getId());
        } else {
            return Iterables.tryFind(diagram.getTrainTypeCategories(),
                    category -> category.getName().equals(origCategory.getName())).orNull();
        }
    }

    protected Train getTrain(Train origTrain) {
        if (match == ImportMatch.ID) {
            return diagram.getTrains().getById(origTrain.getId());
        } else {
            return Iterables.tryFind(diagram.getTrains(), train -> {
                TrainType trainType = getTrainType(origTrain.getType());
                return train.getNumber().equals(origTrain.getNumber())
                        && ObjectsUtil.compareWithNull(train.getType(), trainType);
            }).orNull();
        }
    }

    protected TrainsCycle getCycle(TrainsCycle origCycle) {
        if (match == ImportMatch.ID) {
            return diagram.getCycleById(origCycle.getId());
        } else {
            return Iterables.tryFind(diagram.getCycles(), cycle -> {
                TrainsCycleType cycleType = getCycleType(origCycle.getType());
                return cycle.getName().equals(origCycle.getName())
                        && ObjectsUtil.compareWithNull(cycle.getType(), cycleType);
            }).orNull();
        }
    }

    protected TrainsCycleType getCycleType(TrainsCycleType origCycleType) {
        // special handling for default types - engines, train units and drivers
        if (TrainsCycleType.isDefaultType(origCycleType)) {
            return diagram.getCycleTypeByKey(origCycleType.getKey());
        }
        if (match == ImportMatch.ID) {
            return diagram.getCycleTypes().getById(origCycleType.getId());
        } else {
            return Iterables
                    .tryFind(diagram.getCycleTypes(),
                            cycleType -> cycleType.getName().equals(origCycleType.getName()))
                    .orNull();
        }
    }

    protected Line getLine(Line origLine) {
        if (match == ImportMatch.ID) {
            return diagram.getNet().getLineById(origLine.getId());
        } else {
            Node n1 = this.getNode(origLine.getFrom());
            Node n2 = this.getNode(origLine.getTo());
            return diagram.getNet().getLine(n1, n2);
        }
    }

    protected Route getRoute(Route origRoute) {
        if (match == ImportMatch.ID) {
            return diagram.getRoutes().getById(origRoute.getId());
        } else {
            return diagram.getRoutes()
                    .find(route -> route.getName().equals(origRoute.getName())
                            && route.isNetPart() == origRoute.isNetPart()
                            && route.isTrainRoute() == origRoute.isTrainRoute());
        }
    }

    protected Node getNode(Node origNode) {
        if (match == ImportMatch.ID) {
            return diagram.getNet().getNodeById(origNode.getId());
        } else {
            return Iterables.tryFind(diagram.getNet().getNodes(),
                    node -> node.getName().equals(origNode.getName())).orNull();
        }
    }

    protected Track getTrack(RouteSegment<? extends Track> seg, Track origTrack) {
        List<? extends Track> tracks = seg.getTracks();
        for (Track track : tracks) {
            if (match == ImportMatch.ID) {
                if (track.getId().equals(origTrack.getId()))
                    return track;
            } else {
                if (track.getNumber().equals(origTrack.getNumber()))
                    return track;
            }
        }
        return null;
    }

    protected LineClass getLineClass(LineClass origLineClass) {
        if (match == ImportMatch.ID)
            return diagram.getNet().getLineClasses().getById(origLineClass.getId());
        else {
            return diagram.getNet().getLineClasses()
                    .find(lineClass -> lineClass.getName().equals(origLineClass.getName()));
        }
    }

    protected EngineClass getEngineClass(EngineClass origEngineClass) {
        if (match == ImportMatch.ID)
            return diagram.getEngineClasses().getById(origEngineClass.getId());
        else {
            return diagram.getEngineClasses()
                    .find(engineClass -> engineClass.getName().equals(origEngineClass.getName()));
        }
    }

    protected OutputTemplate getOutputTemplate(OutputTemplate origTemplate) {
        if (match == ImportMatch.ID)
            return diagram.getOutputTemplates().getById(origTemplate.getId());
        else {
            return diagram.getOutputTemplates()
                    .find(template -> template.getKey().equals(origTemplate.getKey()));
        }
    }

    protected Attributes importAttributes(Attributes orig) {
        Attributes dest = new Attributes();
        // copy values
        for (Map.Entry<String, Object> entry : orig.entrySet()) {
            if (entry.getValue() instanceof ObjectWithId) {
                ObjectWithId objectWithId = this.getObjectWithId((ObjectWithId) entry.getValue());
                if (objectWithId != null) {
                    dest.set(entry.getKey(), objectWithId);
                }
            } else if (entry.getValue() instanceof Collection
                    && containsObjectWithId((Collection<?>) entry.getValue())) {
                Collection<?> collection = getObjectsWithId(
                        entry.getValue() instanceof Set ? new HashSet<>() : new ArrayList<>(),
                        (Collection<?>) entry.getValue());
                if (!collection.isEmpty()) {
                    dest.set(entry.getKey(), collection);
                }
            } else if (entry.getValue() instanceof Map && containsObjectWithId((Map<?, ?>) entry.getValue())) {
                Map<?,?> map = getObjectsWithId(new HashMap<>(), (Map<?, ?>) entry.getValue());
                if (!map.isEmpty()) {
                    dest.set(entry.getKey(), map);
                }
            } else {
                dest.set(entry.getKey(), entry.getValue());
            }
        }
        return dest;
    }

    protected Map<?, ?> getObjectsWithId(Map<Object, Object> dest, Map<?, ?> orig) {
        for (Map.Entry<?, ?> entry : orig.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            Object newKey = key instanceof ObjectWithId ? this.getObjectWithId((ObjectWithId) key) : key;
            Object newValue = value instanceof ObjectWithId ? this.getObjectWithId((ObjectWithId) value) : value;
            if (newValue != null && newKey != null) {
                dest.put(newKey, newValue);
            }
        }
        return dest;
    }

    protected Collection<?> getObjectsWithId(Collection<Object> dest, Collection<?> orig) {
        for (Object object : orig) {
            if (object instanceof ObjectWithId) {
                ObjectWithId objectWithId = this.getObjectWithId((ObjectWithId) object);
                if (objectWithId != null) {
                    dest.add(objectWithId);
                }
            } else {
                dest.add(object);
            }
        }
        return dest;
    }

    private boolean containsObjectWithId(Map<?,?> map) {
        boolean hasItems = map != null && !map.isEmpty();
        boolean containsObjectWithId = false;
        if (hasItems) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof ObjectWithId || entry.getValue() instanceof ObjectWithId) {
                    containsObjectWithId = true;
                    break;
                }
            }
        }
        return hasItems && containsObjectWithId;
    }

    private boolean containsObjectWithId(Collection<?> collection) {
        boolean hasItems = collection != null && !collection.isEmpty();
        boolean containsObjectWithId = false;
        if (hasItems) {
            for (Object o : collection) {
                if (o instanceof ObjectWithId) {
                    containsObjectWithId = true;
                    break;
                }
            }
        }
        return hasItems && containsObjectWithId;
    }

    protected String getId(ObjectWithId oid) {
        if (match == ImportMatch.ID) {
            return oid.getId();
        } else {
            return IdGenerator.getInstance().getId();
        }
    }

    protected void clean() {
        errors = new LinkedList<>();
        importedObjects = new HashSet<>();
    }

    public void importObjects(Collection<? extends ObjectWithId> objects) {
        for (ObjectWithId object : objects) {
            this.importObjectImpl(object);
        }
    }

    public ObjectWithId importObject(ObjectWithId object) {
        return this.importObjectImpl(object);
    }

    protected void addError(ObjectWithId o, String explanation) {
        errors.add(new ImportError(o, explanation));
    }

    protected void addImportedObject(ObjectWithId o) {
        importedObjects.add(o);
    }

    public TrainDiagram getDiagram() {
        return diagram;
    }

    public List<ImportError> getErrors() {
        return errors;
    }

    public Set<ObjectWithId> getImportedObjects() {
        return importedObjects;
    }

    protected abstract ObjectWithId importObjectImpl(ObjectWithId o);

    public static Import getInstance(ImportComponent components, TrainDiagram diagram,
            ImportMatch match, boolean overwrite) {
        switch (components) {
            case COMPANIES:
                return new CompanyImport(diagram, match, overwrite);
            case REGIONS:
                return new RegionImport(diagram, match, overwrite);
            case NODES:
                return new NodeImport(diagram, match, overwrite);
            case GROUPS:
                return new GroupImport(diagram, match, overwrite);
            case TRAINS:
                return new TrainImport(diagram, match, overwrite);
            case TRAIN_TYPES:
                return new TrainTypeImport(diagram, match, overwrite);
            case LINE_CLASSES:
                return new LineClassImport(diagram, match, overwrite);
            case ENGINE_CLASSES:
                return new EngineClassImport(diagram, match, overwrite);
            case OUTPUT_TEMPLATES:
                return new OutputTemplateImport(diagram, match, overwrite);
            case TRAINS_CYCLES:
                return new TrainsCycleImport(diagram, match, overwrite);
            case TRAINS_CYCLE_TYPES:
                return new TrainsCycleTypeImport(diagram, match, overwrite);
            case LINES:
                return new LineImport(diagram, match, overwrite);
            case ROUTES:
                return new RouteImport(diagram, match, overwrite);
            case TRAIN_TYPE_CATEGORIES:
                return new TrainTypeCategoryImport(diagram, match, overwrite);
        }
        throw new IllegalArgumentException("No import for component type: " + components);
    }

    public void setProperty(String key, Object value) {
        this.settings.put(key, value);
    }

    public Object getProperty(String key) {
        return this.settings.get(key);
    }
}
