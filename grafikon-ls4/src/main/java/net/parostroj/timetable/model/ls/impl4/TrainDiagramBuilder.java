package net.parostroj.timetable.model.ls.impl4;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.parostroj.timetable.actions.AfterLoadCheck;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Builder for TrainDiagram.
 *
 * @author jub
 */
public class TrainDiagramBuilder {

    private final LSContext context;
    private TrainDiagram diagram;
    private final boolean trackChanges;
    private final Map<String, String> circulationSequenceMap;
    private final FileLoadSaveAttachments flsAttachments;
    private final Collection<DelayedAttributes<?>> delayedAttributesList = new ArrayList<>();

    public TrainDiagramBuilder(LSTrainDiagram lsDiagram, FileLoadSaveAttachments flsAttachments) throws LSException {
        this.flsAttachments = flsAttachments;
        circulationSequenceMap = new HashMap<>();
        // trains data
        this.diagram = new TrainDiagram(lsDiagram.getId());
        this.context = new LSContext() {
            @Override
            public ObjectWithId mapId(String id) {
                return diagram.getObjectById(id);
            }

            @Override
            public PartFactory getPartFactory() {
                return diagram.getPartFactory();
            }

            @Override
            public TrainDiagram getDiagram() {
                return diagram;
            }
        };
        lsDiagram.getTrainsData().updateTrainsData(context);
        // attributes
        Attributes attributes = lsDiagram.getAttributes().createAttributes(context);
        this.diagram.getAttributes().add(attributes);
        trackChanges = lsDiagram.isChangesTracking();
        // circulation types
        for (LSTrainsCycleType cType : lsDiagram.getCycleTypes()) {
            this.diagram.getCycleTypes().add(cType.createTrainsCycleType(context));
        }
        // groups
        for (LSGroup lsGroup : lsDiagram.getGroups()) {
            this.diagram.getGroups().add(lsGroup.createGroup(context));
        }
        // companies
        for (LSCompany lsCompany : lsDiagram.getCompanies()) {
            this.diagram.getCompanies().add(lsCompany.createCompany(context));
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

    public void setTrainsData(LSTrainsData lsData) {
        lsData.updateTrainsData(context);
    }

    public void setPenaltyTable(LSPenaltyTable lSPenaltyTable) throws LSException {
        List<TrainTypeCategory> categories = lSPenaltyTable.createPenaltyTable();
        diagram.getTrainTypeCategories().addAll(categories);
    }

    public void setTrainTypeCategory(LSTrainTypeCategory lsCategory) throws LSException {
        diagram.getTrainTypeCategories().add(lsCategory.createTrainTypeCategory());
    }

    public void setNet(LSNet lsNet) throws LSException {
        Net net = this.diagram.getNet();
        // add regions
        if (lsNet.getRegions() != null) {
            Collection<DelayedAttributes<Region>> regions = new ArrayList<>();
            for (LSRegion lsRegion : lsNet.getRegions()) {
                DelayedAttributes<Region> daRegion = lsRegion.createRegion(context);
                net.getRegions().add(daRegion.getObject());
                regions.add(daRegion);
            }
            for (DelayedAttributes<Region> daRegion : regions) {
                daRegion.addAttributes(context);
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
                Node node = lsNode.createNode(context);
                net.addNode(node);
            }
        }
        // create lines ...
        if (lsNet.getLines() != null) {
            for (LSLine lsLine : lsNet.getLines()) {
                lsLine.createLine(context);
            }
        }
    }

    public void setFreightNet(LSFreightNet lsFreightNet) throws LSException {
        lsFreightNet.createFreightNet(context);
        for (LSFreightConnection lsConnection : lsFreightNet.getConnections()) {
            Train from = diagram.getTrains().getById(lsConnection.getTrainFrom());
            Train to = diagram.getTrains().getById(lsConnection.getTrainTo());
            TimeInterval iFrom = from.getIntervalById(lsConnection.getIntervalFrom());
            TimeInterval iTo = to.getIntervalById(lsConnection.getIntervalTo());
            FNConnection connection = diagram.getFreightNet().addConnection(iFrom, iTo);
            connection.merge(lsConnection.getAttributes().createAttributes(context));
        }
    }

    public void setRoute(LSRoute lsRoute) throws LSException {
        Route route = lsRoute.createRoute(diagram.getNet());
        Route foundRoute;
        if ((foundRoute = diagram.getRoutes().getById(route.getId())) != null) {
            diagram.getRoutes().remove(foundRoute);
        }
        diagram.getRoutes().add(route);
    }

    public void setTrainType(LSTrainType lsType) throws LSException {
        TrainType type = lsType.createTrainType(context);
        TrainType foundTrainType;
        if ((foundTrainType = diagram.getTrainTypes().getById(type.getId())) != null) {
            diagram.getTrainTypes().remove(foundTrainType);
        }
        diagram.getTrainTypes().add(type);
    }

    public void setTextItem(LSTextItem lsTextItem) throws LSException {
        TextItem item = lsTextItem.createTextItem(context);
        diagram.getTextItems().add(item);
    }

    public void setOutputTemplate(LSOutputTemplate lsOutputTemplate) throws LSException {
        if (context.getPartFactory().getType().isOutputTemplateAllowed()) {
            OutputTemplate template = lsOutputTemplate.createOutputTemplate(context, flsAttachments);
            diagram.getOutputTemplates().add(template);
        }
    }

    public void setOutput(LSOutput lsOutput) throws LSException {
        Output output = lsOutput.createOutput(context);
        diagram.getOutputs().add(output);
    }

    public void setDiagramChangeSet(LSDiagramChangeSet lsChangeSet) {
        diagram.getChangesTracker().addVersion(lsChangeSet.getVersion(), lsChangeSet.getAuthor(), lsChangeSet.getDate());
        for (LSDiagramChange change : lsChangeSet.getChanges()) {
            diagram.getChangesTracker().addChange(change.createDiagramChange());
        }
    }

    public void setTrain(LSTrain lsTrain) throws LSException {
        DelayedAttributes<Train> delayedAttributes = lsTrain.createTrain(context);
        delayedAttributesList.add(delayedAttributes);
        Train train = delayedAttributes.getObject();
        Train foundTrain;
        if ((foundTrain = diagram.getTrains().getById(train.getId())) != null) {
            diagram.getTrains().remove(foundTrain);
        }
        diagram.getTrains().add(train);
    }

    public void setEngineClass(LSEngineClass lsEngineClass) throws LSException {
        EngineClass ec = lsEngineClass.createEngineClass(diagram.getNet().getLineClasses()::getById);
        EngineClass foundEc;
        if ((foundEc = diagram.getEngineClasses().getById(ec.getId())) != null) {
            diagram.getEngineClasses().remove(foundEc);
        }
        diagram.getEngineClasses().add(ec);
    }

    public void setTrainsCycle(LSTrainsCycle lsTrainsCycle) throws LSException {
        TrainsCycle cycle = lsTrainsCycle.createTrainsCycle(context);
        TrainsCycle foundCycle;
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

    private void finishDelaydObjectWithIds() throws LSException {
        for (DelayedAttributes<?> delayedAttributes : delayedAttributesList) {
            delayedAttributes.addAttributes(context);
        }
    }

    public TrainDiagram getTrainDiagram() throws LSException {
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
