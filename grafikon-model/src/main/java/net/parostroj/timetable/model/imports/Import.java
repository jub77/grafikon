package net.parostroj.timetable.model.imports;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.Conversions;
import net.parostroj.timetable.utils.IdGenerator;

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

    private static final Logger LOG = LoggerFactory.getLogger(Import.class);

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
        if (orig instanceof TrainType)
            return this.getTrainType((TrainType) orig);
        else if (orig instanceof Train)
            return this.getTrain((Train) orig);
        else if (orig instanceof Node)
            return this.getNode((Node) orig);
        else if (orig instanceof LineClass)
            return this.getLineClass((LineClass) orig);
        else if (orig instanceof EngineClass)
            return this.getEngineClass((EngineClass) orig);
        else if (orig instanceof Group)
            return this.getGroup((Group) orig);
        else if (orig instanceof OutputTemplate)
            return this.getOutputTemplate((OutputTemplate) orig);
        else if (orig instanceof TrainsCycle) {
            return this.getCycle((TrainsCycle) orig);
        }
        else
            return null;
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
                if (train.getNumber().equals(origTrain.getNumber()) && Conversions.compareWithNull(train.getType(), trainType)) {
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
            Map<String, List<TrainsCycle>> map = diagram.getCyclesMap();
            for (List<TrainsCycle> list : map.values()) {
                for (TrainsCycle cycle : list) {
                    TrainsCycleType cycleType = getCycleType(origCycle.getType());
                    if (cycle.getName().equals(origCycle.getName()) && Conversions.compareWithNull(cycle.getType(), cycleType)) {
                        return cycle;
                    }
                }
            }
        }
        return null;
    }

    protected TrainsCycleType getCycleType(TrainsCycleType origCycleType) {
        // special handling for default types - engines, train units and drivers
        if (TrainsCycleType.isDefaultType(origCycleType.getName())) {
            return diagram.getCyclesType(origCycleType.getName());
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
                if (objectWithId == null)
                    LOG.warn("Couldn't find object with id: {} class: {}", ((ObjectWithId)entry.getValue()).getId(), entry.getValue().getClass());
                else
                    dest.set(entry.getKey(), objectWithId);
            } else {
                dest.set(entry.getKey(), entry.getValue());
            }
        }
        return dest;
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
            case NODES:
                return new NodeImport(diagram, library, match);
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
        }
        return null;
    }
}
