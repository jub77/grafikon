package net.parostroj.timetable.model.ls.impl3;

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

    public TrainDiagramBuilder(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    public TrainDiagramBuilder(LSTrainDiagram lsDiagram) throws LSException {
        // trains data
        // attributes
        Attributes attributes = lsDiagram.getAttributes().createAttributes();
        this.diagram = new TrainDiagram(lsDiagram.getId());
        lsDiagram.getTrainsData().updateTrainsData(this.diagram.getTrainsData());
        this.diagram.getAttributes().add(attributes);
        // fill penalty table with predefined values
        LSPenaltyTableHelper.fillPenaltyTable(this.diagram.getTrainTypeCategories());
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

    public void setNet(LSNet lsNet) {
        Net net = this.diagram.getNet();
        // add line classes
        if (lsNet.getLineClasses() != null)
            for (LSLineClass lsLineClass : lsNet.getLineClasses()) {
                net.getLineClasses().add(lsLineClass.createLineClass());
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
                lsLine.createLine(diagram);
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
        TrainType type = lsType.createTrainType(diagram);
        TrainType foundTrainType = null;
        if ((foundTrainType = diagram.getTrainTypes().getById(type.getId())) != null) {
            diagram.getTrainTypes().remove(foundTrainType);
        }
        diagram.getTrainTypes().add(type);
    }

    public void setTrain(LSTrain lsTrain) {
        Train train = lsTrain.createTrain(diagram);
        Train foundTrain = null;
        if ((foundTrain = diagram.getTrains().getById(train.getId())) != null) {
            diagram.getTrains().remove(foundTrain);
        }
        diagram.getTrains().add(train);
    }

    public void setEngineClass(LSEngineClass lsEngineClass) {
        EngineClass ec = lsEngineClass.createEngineClass(diagram.getNet());
        EngineClass foundEc = null;
        if ((foundEc = diagram.getEngineClasses().getById(ec.getId())) != null) {
            diagram.getEngineClasses().remove(foundEc);
        }
        diagram.getEngineClasses().add(ec);
    }

    public void setTrainsCycle(LSTrainsCycle lsTrainsCycle) {
        TrainsCycle cycle = lsTrainsCycle.createTrainsCycle(diagram);
        TrainsCycle foundCycle = null;
        if ((foundCycle = diagram.getCycleByIdAndType(cycle.getId(), cycle.getType())) != null) {
            foundCycle.getType().getCycles().remove(foundCycle);
        }
        cycle.getType().getCycles().add(cycle);
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

    public TrainDiagram getTrainDiagram() {
        (new AfterLoadCheck()).check(diagram);
        TrainDiagram retValue = diagram;
        diagram = null;
        return retValue;
    }
}
