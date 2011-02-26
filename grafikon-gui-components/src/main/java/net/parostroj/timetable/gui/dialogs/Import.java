package net.parostroj.timetable.gui.dialogs;

import java.util.*;
import java.util.logging.Logger;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;

/**
 * Imports trains from one diagram to another.
 *
 * @author jub
 */
public abstract class Import {

    private static final Logger LOG = Logger.getLogger(Import.class.getName());

    private ImportMatch match;
    private TrainDiagram diagram;
    private TrainDiagram libraryDiagram;

    private List<ObjectWithId> errors;
    private Set<ObjectWithId> importedObjects;

    public Import(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        this.match = match;
        this.diagram = diagram;
        this.libraryDiagram = libraryDiagram;
    }

    protected TrainType getTrainType(TrainType origType) {
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
                if (train.getNumber().equals(origTrain.getNumber())) {
                    return train;
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

    protected String getId(ObjectWithId oid) {
        if (match == ImportMatch.ID) {
            return oid.getId();
        } else {
            return IdGenerator.getInstance().getId();
        }
    }

    protected void clean() {
        errors = new LinkedList<ObjectWithId>();
        importedObjects = new HashSet<ObjectWithId>();
    }

    public void importObjects(Collection<Object> objects) {
        this.clean();
        for (Object object : objects) {
            this.importObjectImpl(object);
        }
    }

    public void importObject(Object object) {
        this.clean();
        this.importObjectImpl(object);
    }

    protected void addError(ObjectWithId o, String explanation) {
        errors.add(o);
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

    public List<ObjectWithId> getErrors() {
        return errors;
    }

    public Set<ObjectWithId> getImportedObjects() {
        return importedObjects;
    }

    protected abstract void importObjectImpl(Object o);
}
