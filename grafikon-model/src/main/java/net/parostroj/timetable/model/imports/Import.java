package net.parostroj.timetable.model.imports;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final TrainDiagram libraryDiagram;

    private List<ImportError> errors;
    private Set<ObjectWithId> importedObjects;

    public Import(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        this.match = match;
        this.diagram = diagram;
        this.libraryDiagram = libraryDiagram;
        this.errors = new LinkedList<ImportError>();
        this.importedObjects = new HashSet<ObjectWithId>();
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
            return diagram.getTrainTypeById(origType.getId());
        } else {
            for (TrainType type : diagram.getTrainTypes()) {
                if (type.getAbbr().equals(origType.getAbbr()) && type.getDesc().equals(origType.getDesc())) {
                    return type;
                }
            }
        }
        return null;
    }

    protected Group getGroup(Group origGroup) {
        if (match == ImportMatch.ID) {
            return diagram.getGroupById(origGroup.getId());
        } else {
            for (Group g : diagram.getGroups()) {
                if (g.getName().equals(origGroup.getName()))
                    return g;
            }
        }
        return null;
    }

    protected Company getCompany(Company origCompany) {
        if (match == ImportMatch.ID) {
            return diagram.getCompanyById(origCompany.getId());
        } else {
            for (Company c : diagram.getCompanies()) {
                if (c.getAbbr().equals(origCompany.getAbbr()))
                    return c;
            }
        }
        return null;
    }

    protected Region getRegion(Region origRegion) {
        if (match == ImportMatch.ID) {
            return diagram.getNet().getRegionById(origRegion.getId());
        } else {
            for (Region r : diagram.getNet().getRegions()) {
                if (r.getName().equals(origRegion.getName()))
                    return r;
            }
        }
        return null;
    }

    protected TrainTypeCategory getTrainTypeCategory(TrainTypeCategory origCategory) {
        if (match == ImportMatch.ID) {
            return diagram.getPenaltyTable().getTrainTypeCategoryById(origCategory.getId());
        } else {
            for (TrainTypeCategory category : diagram.getPenaltyTable().getTrainTypeCategories()) {
                if (category.getName().equals(origCategory.getName()))
                    return category;
            }
        }
        return null;
    }

    protected Train getTrain(Train origTrain) {
        if (match == ImportMatch.ID) {
            return diagram.getTrainById(origTrain.getId());
        } else {
            for (Train train : diagram.getTrains()) {
                // compare number and type
                TrainType trainType = getTrainType(origTrain.getType());
                if (train.getNumber().equals(origTrain.getNumber()) && ObjectsUtil.compareWithNull(train.getType(), trainType)) {
                    return train;
                }
            }
        }
        return null;
    }

    protected TrainsCycle getCycle(TrainsCycle origCycle) {
        if (match == ImportMatch.ID) {
            return diagram.getCycleById(origCycle.getId());
        } else {
            for (TrainsCycle cycle : diagram.getCycles()) {
                TrainsCycleType cycleType = getCycleType(origCycle.getType());
                if (cycle.getName().equals(origCycle.getName()) && ObjectsUtil.compareWithNull(cycle.getType(), cycleType)) {
                    return cycle;
                }
            }
        }
        return null;
    }

    protected TrainsCycleType getCycleType(TrainsCycleType origCycleType) {
        // special handling for default types - engines, train units and drivers
        if (TrainsCycleType.isDefaultType(origCycleType)) {
            return diagram.getDefaultCycleType(origCycleType.getName());
        }
        if (match == ImportMatch.ID) {
            return diagram.getCycleTypeById(origCycleType.getId());
        } else {
            for (TrainsCycleType cycleType : diagram.getCycleTypes()) {
                if (cycleType.getName().equals(origCycleType.getName())) {
                    return cycleType;
                }
            }
        }
        return null;
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
            return diagram.getRouteById(origRoute.getId());
        } else {
            for (Route route : diagram.getRoutes()) {
                if (route.getName().equals(origRoute.getName()) && route.isNetPart() == origRoute.isNetPart() &&
                        route.isTrainRoute() == origRoute.isTrainRoute()) {
                    return route;
                }
            }
            return null;
        }
    }

    protected Node getNode(Node origNode) {
        if (match == ImportMatch.ID) {
            return diagram.getNet().getNodeById(origNode.getId());
        } else {
            for (Node node : diagram.getNet().getNodes()) {
                if (node.getName().equals(origNode.getName()))
                    return node;
            }
        }
        return null;
    }

    protected Track getTrack(RouteSegment seg, Track origTrack) {
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
            return diagram.getNet().getLineClassById(origLineClass.getId());
        else {
            for (LineClass lineClass : diagram.getNet().getLineClasses()) {
                if (lineClass.getName().equals(origLineClass.getName()))
                    return lineClass;
            }
            return null;
        }
    }

    protected EngineClass getEngineClass(EngineClass origEngineClass) {
        if (match == ImportMatch.ID)
            return diagram.getEngineClassById(origEngineClass.getId());
        else {
            for (EngineClass engineClass : diagram.getEngineClasses()) {
                if (engineClass.getName().equals(origEngineClass.getName()))
                    return engineClass;
            }
            return null;
        }
    }

    protected OutputTemplate getOutputTemplate(OutputTemplate origTemplate) {
        if (match == ImportMatch.ID)
            return diagram.getOutputTemplateById(origTemplate.getId());
        else {
            for (OutputTemplate template : diagram.getOutputTemplates()) {
                if (template.getName().equals(origTemplate.getName()))
                    return template;
            }
            return null;
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
        errors = new LinkedList<ImportError>();
        importedObjects = new HashSet<ObjectWithId>();
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

    public TrainDiagram getLibraryDiagram() {
        return libraryDiagram;
    }

    public List<ImportError> getErrors() {
        return errors;
    }

    public Set<ObjectWithId> getImportedObjects() {
        return importedObjects;
    }

    protected abstract ObjectWithId importObjectImpl(ObjectWithId o);

    public static Import getInstance(ImportComponent components, TrainDiagram diagram,
            TrainDiagram library, ImportMatch match) {
        switch (components) {
            case COMPANIES:
                return new CompanyImport(diagram, library, match);
            case REGIONS:
                return new RegionImport(diagram, library, match);
            case NODES:
                return new NodeImport(diagram, library, match);
            case GROUPS:
                return new GroupImport(diagram, library, match);
            case TRAINS:
                return new TrainImport(diagram, library, match);
            case TRAIN_TYPES:
                return new TrainTypeImport(diagram, library, match);
            case LINE_CLASSES:
                return new LineClassImport(diagram, library, match);
            case ENGINE_CLASSES:
                return new EngineClassImport(diagram, library, match);
            case OUTPUT_TEMPLATES:
                return new OutputTemplateImport(diagram, library, match);
            case TRAINS_CYCLES:
                return new TrainsCycleImport(diagram, library, match);
            case TRAINS_CYCLE_TYPES:
                return new TrainsCycleTypeImport(diagram, library, match);
            case LINES:
                return new LineImport(diagram, library, match);
            case ROUTES:
                return new RouteImport(diagram, library, match);
        }
        return null;
    }
}
