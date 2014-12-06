package net.parostroj.timetable.model.ls.impl4;

import java.io.File;
import java.util.UUID;

import net.parostroj.timetable.actions.AfterLoadCheck;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Builder for TrainDiagram.
 *
 * @author jub
 */
public class TrainDiagramBuilder {

    private TrainDiagram diagram;
    private boolean trackChanges;

    public TrainDiagramBuilder(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    public TrainDiagramBuilder(LSTrainDiagram lsDiagram) throws LSException {
        // trains data
        TrainsData data = lsDiagram.getTrainsData().createTrainsData();
        this.diagram = new TrainDiagram(lsDiagram.getId(), data);
        // attributes
        Attributes attributes = lsDiagram.getAttributes().createAttributes(this.diagram);
        this.diagram.setAttributes(attributes);
        trackChanges = lsDiagram.isChangesTracking();
        // circulation types
        for (LSTrainsCycleType cType : lsDiagram.getCycleTypes()) {
            this.diagram.addCyclesType(cType.createTrainsCycleType(diagram));
        }
        // groups
        for (LSGroup lsGroup : lsDiagram.getGroups()) {
            this.diagram.addGroup(lsGroup.createGroup(diagram));
        }
        // add default trains cycle types (if already defined - no action)
        if (diagram.getDriverCycleType() == null) {
            diagram.addCyclesType(new TrainsCycleType(UUID.randomUUID().toString(), TrainsCycleType.DRIVER_CYCLE));
        }
        if (diagram.getEngineCycleType() == null) {
            diagram.addCyclesType(new TrainsCycleType(UUID.randomUUID().toString(), TrainsCycleType.ENGINE_CYCLE));
        }
        if (diagram.getTrainUnitCycleType() == null) {
            diagram.addCyclesType(new TrainsCycleType(UUID.randomUUID().toString(), TrainsCycleType.TRAIN_UNIT_CYCLE));
        }
    }

    public void setTrainsData(LSTrainsData lsData) throws LSException {
        TrainsData data = lsData.createTrainsData();
        this.diagram.setTrainsData(data);
    }

    public void setPenaltyTable(LSPenaltyTable lSPenaltyTable) {
        PenaltyTable table = lSPenaltyTable.createPenaltyTable();
        this.diagram.setPenaltyTable(table);
    }

    public void setNet(LSNet lsNet) throws LSException {
        Net net = lsNet.createNet(this.diagram);
        this.diagram.setNet(net);
        // add regions
        if (lsNet.getRegions() != null) {
            for (LSRegion lsRegion : lsNet.getRegions()) {
                net.getRegions().add(lsRegion.createRegion(diagram));
            }
        }
        // add line classes
        if (lsNet.getLineClasses() != null) {
            for (LSLineClass lsLineClass : lsNet.getLineClasses()) {
                net.addLineClass(lsLineClass.createLineClass());
            }
        }
        // create nodes ...
        if (lsNet.getNodes() != null) {
            for (LSNode lsNode : lsNet.getNodes()) {
                Node node = lsNode.createNode(diagram);
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
            Train from = diagram.getTrainById(lsConnection.getTrainFrom());
            Train to = diagram.getTrainById(lsConnection.getTrainTo());
            TimeInterval iFrom = from.getIntervalById(lsConnection.getIntervalFrom());
            TimeInterval iTo = to.getIntervalById(lsConnection.getIntervalTo());
            FNConnection connection = diagram.getFreightNet().addConnection(iFrom, iTo);
            connection.merge(lsConnection.getAttributes().createAttributes(diagram));
        }
    }

    public void setLocalization(LSLocalization lsLocalization) throws LSException {
        // add of localization items is implemented in LSLocalization class)
        lsLocalization.createLocalization(diagram);
    }

    public void setRoute(LSRoute lsRoute) throws LSException {
        Route route = lsRoute.createRoute(diagram.getNet());
        Route foundRoute = null;
        if ((foundRoute = diagram.getRouteById(route.getId())) != null) {
            diagram.removeRoute(foundRoute);
        }
        diagram.addRoute(route);
    }

    public void setTrainType(LSTrainType lsType) throws LSException {
        TrainType type = lsType.createTrainType(diagram);
        TrainType foundTrainType = null;
        if ((foundTrainType = diagram.getTrainTypeById(type.getId())) != null) {
            diagram.removeTrainType(foundTrainType);
        }
        diagram.addTrainType(type);
    }

    public void setTextItem(LSTextItem lsTextItem) throws LSException {
        TextItem item = lsTextItem.createTextItem(diagram);
        diagram.addTextItem(item);
    }

    public void setOutputTemplate(LSOutputTemplate lsOutputTemplate) throws LSException {
        OutputTemplate template = lsOutputTemplate.createOutputTemplate(diagram);
        diagram.addOutputTemplate(template);
    }

    public void setDiagramChangeSet(LSDiagramChangeSet lsChangeSet) {
        diagram.getChangesTracker().addVersion(lsChangeSet.getVersion(), lsChangeSet.getAuthor(), lsChangeSet.getDate());
        for (LSDiagramChange change : lsChangeSet.getChanges()) {
            diagram.getChangesTracker().addChange(change.createDiagramChange());
        }
    }

    public void setTrain(LSTrain lsTrain) throws LSException {
        Train train = lsTrain.createTrain(diagram);
        Train foundTrain = null;
        if ((foundTrain = diagram.getTrainById(train.getId())) != null) {
            diagram.removeTrain(foundTrain);
        }
        diagram.addTrain(train);
    }

    public void setEngineClass(LSEngineClass lsEngineClass) {
        EngineClass ec = lsEngineClass.createEngineClass(diagram.getNet());
        EngineClass foundEc = null;
        if ((foundEc = diagram.getEngineClassById(ec.getId())) != null) {
            diagram.removeEngineClass(foundEc);
        }
        diagram.addEngineClass(ec);
    }

    public void setTrainsCycle(LSTrainsCycle lsTrainsCycle) throws LSException {
        TrainsCycle cycle = lsTrainsCycle.createTrainsCycle(diagram);
        TrainsCycle foundCycle = null;
        if ((foundCycle = diagram.getCycleById(cycle.getId())) != null) {
            diagram.removeCycle(foundCycle);
        }
        diagram.addCycle(cycle);
    }

    public void addImage(LSImage lsImage) {
        TimetableImage image = lsImage.createTimetableImage(diagram);
        diagram.addImage(image);
    }

    public void addImageFile(String filename, File file) {
        for (TimetableImage image : diagram.getImages()) {
            if (image.getFilename().equals(filename)) {
                image.setImageFile(file);
                break;
            }
        }
    }

    public TrainDiagram getTrainDiagram() {
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
