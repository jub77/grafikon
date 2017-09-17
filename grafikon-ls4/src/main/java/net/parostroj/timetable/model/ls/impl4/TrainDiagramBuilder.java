package net.parostroj.timetable.model.ls.impl4;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.actions.AfterLoadCheck;
import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.AttributesHolder;
import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.FNConnection;
import net.parostroj.timetable.model.FreightNet;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Net;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.Output;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TextItem;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TimetableImage;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainDiagramFactory;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainTypeCategory;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Builder for TrainDiagram.
 *
 * @author jub
 */
public class TrainDiagramBuilder {

    private static final Logger log = LoggerFactory.getLogger(TrainDiagramBuilder.class);

    private TrainDiagram diagram;
    private final boolean trackChanges;
    private final Map<String, String> circulationSequenceMap;
    private final FileLoadSaveAttachments flsAttachments;

    public TrainDiagramBuilder(LSTrainDiagram lsDiagram, FileLoadSaveAttachments flsAttachments) throws LSException {
        this.flsAttachments = flsAttachments;
        circulationSequenceMap = new HashMap<>();
        // trains data
        this.diagram = new TrainDiagram(lsDiagram.getId());
        lsDiagram.getTrainsData().updateTrainsData(this.diagram.getTrainsData());
        // attributes
        Attributes attributes = lsDiagram.getAttributes().createAttributes(diagram::getObjectById);
        this.diagram.getAttributes().add(attributes);
        trackChanges = lsDiagram.isChangesTracking();
        // circulation types
        for (LSTrainsCycleType cType : lsDiagram.getCycleTypes()) {
            this.diagram.getCycleTypes().add(cType.createTrainsCycleType(diagram));
        }
        // groups
        for (LSGroup lsGroup : lsDiagram.getGroups()) {
            this.diagram.getGroups().add(lsGroup.createGroup(diagram));
        }
        // companies
        for (LSCompany lsCompany : lsDiagram.getCompanies()) {
            this.diagram.getCompanies().add(lsCompany.createCompany(diagram));
        }
        // add default trains cycle types (if already defined - no action)
        if (diagram.getDriverCycleType() == null) {
            diagram.getCycleTypes().add(TrainDiagramFactory.createDefaultTrainsCycleType(TrainsCycleType.DRIVER_CYCLE_KEY, diagram));
        }
        if (diagram.getEngineCycleType() == null) {
            diagram.getCycleTypes().add(TrainDiagramFactory.createDefaultTrainsCycleType(TrainsCycleType.ENGINE_CYCLE_KEY, diagram));
        }
        if (diagram.getTrainUnitCycleType() == null) {
            diagram.getCycleTypes().add(TrainDiagramFactory.createDefaultTrainsCycleType(TrainsCycleType.TRAIN_UNIT_CYCLE_KEY, diagram));
        }
    }

    public void setTrainsData(LSTrainsData lsData) throws LSException {
        lsData.updateTrainsData(this.diagram.getTrainsData());
    }

    public void setPenaltyTable(LSPenaltyTable lSPenaltyTable) throws LSException {
        List<TrainTypeCategory> categories = lSPenaltyTable.createPenaltyTable();
        diagram.getTrainTypeCategories().addAll(categories);
    }

    public void setTrainTypeCategory(LSTrainTypeCategory lsCategory) throws LSException {
        diagram.getTrainTypeCategories().add(lsCategory.createTrainTypeCategory());
    }

    public void setNet(LSNet lsNet) throws LSException {
        Net net = lsNet.createNet(this.diagram);
        this.diagram.setNet(net);
        // add regions
        if (lsNet.getRegions() != null) {
            Collection<DelayedAttributes<Region>> regions = new ArrayList<>();
            for (LSRegion lsRegion : lsNet.getRegions()) {
                DelayedAttributes<Region> daRegion = lsRegion.createRegion(diagram);
                net.getRegions().add(daRegion.getObject());
                regions.add(daRegion);
            }
            for (DelayedAttributes<Region> daRegion : regions) {
                daRegion.addAttributes();
            }
        }
        // add line classes
        if (lsNet.getLineClasses() != null) {
            for (LSLineClass lsLineClass : lsNet.getLineClasses()) {
                net.getLineClasses().add(lsLineClass.createLineClass());
            }
        }
        // create nodes ...
        if (lsNet.getNodes() != null) {
            for (LSNode lsNode : lsNet.getNodes()) {
                Node node = lsNode.createNode(diagram.getPartFactory(), diagram::getObjectById);
                net.addNode(node);
            }
        }
        // create lines ...
        if (lsNet.getLines() != null) {
            for (LSLine lsLine : lsNet.getLines()) {
                Line line = lsLine.createLine(diagram);
                Node from = net.getNodeById(lsLine.getFrom());
                Node to = net.getNodeById(lsLine.getTo());
                net.addLine(from, to, line);
            }
        }
    }

    public void setFreightNet(LSFreightNet lsFreightNet) throws LSException {
        FreightNet net = lsFreightNet.createFreightNet(diagram);
        this.diagram.setFreightNet(net);
        for (LSFreightConnection lsConnection : lsFreightNet.getConnections()) {
            Train from = diagram.getTrains().getById(lsConnection.getTrainFrom());
            Train to = diagram.getTrains().getById(lsConnection.getTrainTo());
            TimeInterval iFrom = from.getIntervalById(lsConnection.getIntervalFrom());
            TimeInterval iTo = to.getIntervalById(lsConnection.getIntervalTo());
            FNConnection connection = diagram.getFreightNet().addConnection(iFrom, iTo);
            connection.merge(lsConnection.getAttributes().createAttributes(diagram::getObjectById));
        }
    }

    public void setRoute(LSRoute lsRoute) throws LSException {
        Route route = lsRoute.createRoute(diagram.getNet());
        Route foundRoute = null;
        if ((foundRoute = diagram.getRoutes().getById(route.getId())) != null) {
            diagram.getRoutes().remove(foundRoute);
        }
        diagram.getRoutes().add(route);
    }

    public void setTrainType(LSTrainType lsType) throws LSException {
        TrainType type = lsType.createTrainType(diagram.getPartFactory(), diagram::getObjectById, diagram.getTrainTypeCategories()::getById);
        TrainType foundTrainType = null;
        if ((foundTrainType = diagram.getTrainTypes().getById(type.getId())) != null) {
            diagram.getTrainTypes().remove(foundTrainType);
        }
        diagram.getTrainTypes().add(type);
    }

    public void setTextItem(LSTextItem lsTextItem) throws LSException {
        TextItem item = lsTextItem.createTextItem(diagram);
        diagram.getTextItems().add(item);
    }

    public void setOutputTemplate(LSOutputTemplate lsOutputTemplate) throws LSException {
        OutputTemplate template = lsOutputTemplate.createOutputTemplate(diagram.getPartFactory(), diagram::getObjectById, flsAttachments);
        diagram.getOutputTemplates().add(template);
    }

    public void setOutput(LSOutput lsOutput) throws LSException {
        Output output = lsOutput.createOutput(diagram);
        diagram.getOutputs().add(output);
    }

    public void setDiagramChangeSet(LSDiagramChangeSet lsChangeSet) {
        diagram.getChangesTracker().addVersion(lsChangeSet.getVersion(), lsChangeSet.getAuthor(), lsChangeSet.getDate());
        for (LSDiagramChange change : lsChangeSet.getChanges()) {
            diagram.getChangesTracker().addChange(change.createDiagramChange());
        }
    }

    public void setTrain(LSTrain lsTrain) throws LSException {
        Train train = lsTrain.createTrain(diagram, delayedMapping(diagram::getObjectById));
        Train foundTrain = null;
        if ((foundTrain = diagram.getTrains().getById(train.getId())) != null) {
            diagram.getTrains().remove(foundTrain);
        }
        diagram.getTrains().add(train);
    }

    public void setEngineClass(LSEngineClass lsEngineClass) throws LSException {
        EngineClass ec = lsEngineClass.createEngineClass(diagram.getNet().getLineClasses()::getById);
        EngineClass foundEc = null;
        if ((foundEc = diagram.getEngineClasses().getById(ec.getId())) != null) {
            diagram.getEngineClasses().remove(foundEc);
        }
        diagram.getEngineClasses().add(ec);
    }

    public void setTrainsCycle(LSTrainsCycle lsTrainsCycle) throws LSException {
        TrainsCycle cycle = lsTrainsCycle.createTrainsCycle(diagram);
        TrainsCycle foundCycle = null;
        if ((foundCycle = diagram.getCycleById(cycle.getId())) != null) {
            foundCycle.getType().getCycles().remove(foundCycle);
        }
        cycle.getType().getCycles().add(cycle);
        // map of sequences
        if (lsTrainsCycle.getNext() != null) {
            circulationSequenceMap.put(lsTrainsCycle.getId(), lsTrainsCycle.getNext());
        }
    }

    public void addImage(LSImage lsImage) {
        TimetableImage image = lsImage.createTimetableImage(diagram);
        diagram.getImages().add(image);
    }

    public void addImageFile(String filename, File file) {
        for (TimetableImage image : diagram.getImages()) {
            if (image.getFilename().equals(filename)) {
                image.setImageFile(file);
                break;
            }
        }
    }

    private void finishDelaydObjectWithIds() {
        // trains references to trains in default category
        updateObjectWithIdReferences(
                diagram.getTrains()::getById,
                diagram.getTrains().stream(),
                train -> train.getAttributes());
    }

    private void updateObjectWithIdReferences(
            Function<String, ObjectWithId> objectMapping,
            Stream<? extends AttributesHolder> objectStream,
            Function<AttributesHolder, Map<String, Object>> attributesAccessor) {
        objectStream.forEach(holder -> {
            Map<String, Object> attributes = attributesAccessor.apply(holder);
            Iterator<Map.Entry<String, Object>> i = attributes.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, Object> entry = i.next();
                if (entry.getValue() instanceof DelayedObjectWithId) {
                    DelayedObjectWithId delayedObject = (DelayedObjectWithId) entry.getValue();
                    ObjectWithId object = objectMapping.apply(delayedObject.getId());
                    if (object == null) {
                        log.warn("Not found object {} with key {} in {}",
                                delayedObject.getId(), entry.getKey(), holder);
                        i.remove();
                    } else {
                        entry.setValue(object);
                    }
                }
            }
        });
    }

    private void finishCirculationSequences() {
        while (!circulationSequenceMap.isEmpty()) {
            addCirculationToSequence(circulationSequenceMap.values().iterator().next());
        }
    }

    private void addCirculationToSequence(String fromId) {
        String toId = circulationSequenceMap.remove(fromId);
        if (toId != null) {
            TrainsCycle from = diagram.getCycleById(fromId);
            TrainsCycle to = diagram.getCycleById(toId);
            // do not add if already part of sequence (circular - last one)
            if (!to.isPartOfSequence()) {
                from.connectToSequenceAsNext(to);
                addCirculationToSequence(toId);
            }
        }
    }

    private Function<String, ObjectWithId> delayedMapping(Function<String, ObjectWithId> mapping) {
        return id -> {
            ObjectWithId objectWithId = mapping.apply(id);
            return objectWithId == null ? new DelayedObjectWithId(id) : objectWithId;
        };
    }

    public TrainDiagram getTrainDiagram() {
        if (diagram == null) {
            throw new IllegalStateException("Diagram already created");
        }
        this.finishDelaydObjectWithIds();
        this.finishCirculationSequences();
        // after load check
        (new AfterLoadCheck()).check(diagram);
        // tracking of changes has to be enabled at the end, otherwise
        // it would also track changes caused by loading of the diagram
        diagram.getChangesTracker().setTrackingEnabled(trackChanges);
        if (trackChanges) {
            diagram.getChangesTracker().addVersion(null, null, null);
            diagram.getChangesTracker().setLastAsCurrent();
        }
        TrainDiagram retValue = diagram;
        diagram = null;
        return retValue;
    }
}
