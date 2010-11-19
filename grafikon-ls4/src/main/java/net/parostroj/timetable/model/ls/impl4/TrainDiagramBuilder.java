package net.parostroj.timetable.model.ls.impl4;

import java.io.File;
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
    
    public TrainDiagramBuilder(LSTrainDiagram lsDiagram) {
        // trains data
        TrainsData data = lsDiagram.getTrainsData().createTrainsData();
        // attributes
        Attributes attributes = lsDiagram.getAttributes().createAttributes();
        if (diagram == null)
            this.diagram = new TrainDiagram(lsDiagram.getId(), null);
        this.diagram.setTrainsData(data);
        this.diagram.setAttributes(attributes);
        trackChanges = lsDiagram.isChangesTrackingEnabled();
    }
    
    public void setTrainsData(LSTrainsData lsData) {
        TrainsData data = lsData.createTrainsData();
        this.diagram.setTrainsData(data);
    }

    public void setPenaltyTable(LSPenaltyTable lSPenaltyTable) {
        PenaltyTable table = lSPenaltyTable.createPenaltyTable();
        this.diagram.setPenaltyTable(table);
    }
    
    public void setNet(LSNet lsNet) {
        Net net = lsNet.createNet();
        this.diagram.setNet(net);
        // add line classes
        if (lsNet.getLineClasses() != null)
            for (LSLineClass lsLineClass : lsNet.getLineClasses()) {
                net.addLineClass(lsLineClass.createLineClass());
            }
        // create nodes ...
        if (lsNet.getNodes() != null)
            for (LSNode lsNode : lsNet.getNodes()) {
                Node node = lsNode.createNode(diagram);
                net.addNode(node);
            }
        // create lines ...
        if (lsNet.getLines() != null)
            for (LSLine lsLine : lsNet.getLines()) {
                Line line = lsLine.createLine(diagram);
                Node from = net.getNodeById(lsLine.getFrom());
                Node to = net.getNodeById(lsLine.getTo());
                net.addLine(from, to, line);
            }
    }
    
    public void setRoute(LSRoute lsRoute) throws LSException {
        Route route = lsRoute.createRoute(diagram.getNet());
        Route foundRoute = null;
        if ((foundRoute = diagram.getRouteById(route.getId())) != null) {
            diagram.removeRoute(foundRoute);
        }
        diagram.addRoute(route);
    }
    
    public void setTrainType(LSTrainType lsType) {
        TrainType type = lsType.createTrainType(diagram);
        TrainType foundTrainType = null;
        if ((foundTrainType = diagram.getTrainTypeById(type.getId())) != null) {
            diagram.removeTrainType(foundTrainType);
        }
        diagram.addTrainType(type);
    }
    
    public void setTextItem(LSTextItem lsTextItem) {
        TextItem item = lsTextItem.createTextItem(diagram);
        diagram.addTextItem(item);
    }

    public void setDiagramChangeSet(LSDiagramChangeSet lsChangeSet) {
        diagram.getChangesTracker().addVersion(lsChangeSet.getVersion(), lsChangeSet.getAuthor(), lsChangeSet.getDate());
        for (LSDiagramChange change : lsChangeSet.getChanges()) {
            diagram.getChangesTracker().addChange(change.createDiagramChange());
        }
    }

    public void setTrain(LSTrain lsTrain) {
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
    
    public void setTrainsCycle(LSTrainsCycle lsTrainsCycle) {
        TrainsCycle cycle = lsTrainsCycle.createTrainsCycle(diagram);
        TrainsCycle foundCycle = null;
        if ((foundCycle = diagram.getCycleByIdAndType(cycle.getId(), cycle.getType())) != null) {
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
        // tracking of changes has to be enabled at the end, otherwise
        // it would also track changes caused by loading of the diagram
        diagram.getChangesTracker().setTrackingEnabled(trackChanges);
        if (trackChanges) {
            diagram.getChangesTracker().addVersion(null, null, null);
            diagram.getChangesTracker().setLastAsCurrent();
        }
        // after load check
        (new AfterLoadCheck()).check(diagram);
        return diagram;
    }
}
