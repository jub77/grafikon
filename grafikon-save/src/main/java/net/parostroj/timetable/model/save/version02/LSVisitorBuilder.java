package net.parostroj.timetable.model.save.version02;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.save.LSPenaltyTableHelper;
import net.parostroj.timetable.model.save.LSTrainTypeList;
import net.parostroj.timetable.utils.IdGenerator;

public class LSVisitorBuilder implements LSVisitor {

    private TrainDiagram diagram;
    private final Map<Integer, Object> ids = new HashMap<Integer, Object>();
    private final LSTrainTypeList trainTypeList;
    // last node
    private Node lastNode;
    // last train
    private Train lastTrain;
    // last line
    private Line lastLine;
    private final List<Train> trains;

    public LSVisitorBuilder(LSTrainTypeList list) {
        this.trainTypeList = list;
        this.trains = new LinkedList<Train>();
    }

    @Override
    public void visit(LSTrainDiagram lsDiagram) {
        // create empty net
        diagram = new TrainDiagram(IdGenerator.getInstance().getId());
        trainTypeList.getTrainsData().copyValuesTo(diagram.getTrainsData());
        LSPenaltyTableHelper.fillPenaltyTable(diagram.getTrainTypeCategories());
        trainTypeList.updateMapping(diagram);
        for (TrainType type : trainTypeList.getTrainTypeList()) {
            diagram.getTrainTypes().add(type);
        }
        if (lsDiagram.getAttributes() != null) {
            diagram.getAttributes().add(lsDiagram.getAttributes().convertToAttributes());
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

    @Override
    public void visit(LSNode lsNode) {
        NodeType type = NodeType.valueOf(lsNode.getNodeType());
        Node node = diagram.getPartFactory().createNode(lsNode.getUuid(), type, lsNode.getName(), lsNode.getAbbr());
        if (lsNode.getAttributes() != null) {
            node.getAttributes().add(lsNode.getAttributes().convertToAttributes());
        }
        node.setLocation(new Location(lsNode.getX(), lsNode.getY()));
        ids.put(lsNode.getId(), node);

        // add to net
        diagram.getNet().addNode(node);
        // set last node
        lastNode = node;
    }

    @Override
    public void visit(LSNodeTrack lsNodeTrack) {
        NodeTrack nodeTrack = new NodeTrack(lsNodeTrack.getUuid(), lsNodeTrack.getNumber());
        ids.put(lsNodeTrack.getId(), nodeTrack);

        nodeTrack.setPlatform(lsNodeTrack.isPlatform());
        if (lsNodeTrack.getAttributes() != null) {
            nodeTrack.getAttributes().add(lsNodeTrack.getAttributes().convertToAttributes());        // add to last node
        }
        lastNode.addTrack(nodeTrack);
    }

    /**
     * @return created train diagram
     */
    public TrainDiagram getTrainDiagram() {
        for (Train t : trains) {
            diagram.getTrains().add(t);
        }
        return diagram;
    }

    @Override
    public void visit(LSLine lsLine) {
        Node from = (Node) ids.get(lsLine.getSourceId());
        Node to = (Node) ids.get(lsLine.getTargetId());
        Line line = diagram.getPartFactory().createLine(lsLine.getUuid(), lsLine.getLength(), from, to, lsLine.getTopSpeed());
        ids.put(lsLine.getId(), line);

        if (lsLine.getAttributes() != null) {
            line.getAttributes().add(lsLine.getAttributes().convertToAttributes());        // add to net
        }
        Net net = diagram.getNet();
        net.addLine(from, to, line);

        // add as last line
        lastLine = line;
    }

    @Override
    public void visit(LSLineTrack lsLineTrack) {
        LineTrack lineTrack = new LineTrack(lsLineTrack.getUuid(), lsLineTrack.getNumber());
        lineTrack.setFromStraightTrack((NodeTrack) ids.get(lsLineTrack.getSourceTrackId()));
        lineTrack.setToStraightTrack((NodeTrack) ids.get(lsLineTrack.getTargetTrackId()));
        ids.put(lsLineTrack.getId(), lineTrack);

        if (lsLineTrack.getAttributes() != null) {
            lineTrack.getAttributes().add(lsLineTrack.getAttributes().convertToAttributes());        // add to last line
        }
        lastLine.addTrack(lineTrack);
    }

    @Override
    public void visit(LSTrain lsTrain) {
        TrainType type = trainTypeList.getTrainType(lsTrain.getTrainType());
        Train train = diagram.getPartFactory().createTrain(lsTrain.getUuid());
        train.setNumber(lsTrain.getName());
        train.setType(type);
        ids.put(lsTrain.getId(), train);

        if (lsTrain.getTopSpeed() > 0) {
            train.setTopSpeed(lsTrain.getTopSpeed());
        }
        train.setType(type);
        train.setDescription(lsTrain.getDescription());
        if (lsTrain.getAttributes() != null) {
            train.getAttributes().add(lsTrain.getAttributes().convertToAttributes());
        }
        this.trains.add(train);

        // set last train
        lastTrain = train;
    }

    @Override
    public void visit(LSTimeInterval lsInterval) {
        // add node track
        Track track = (Track) ids.get(lsInterval.getTrackId());

        // add to the last train
        RouteSegment<?> part = (RouteSegment<?>) ids.get(lsInterval.getOwnerId());
        TimeInterval interval = new TimeInterval(IdGenerator.getInstance().getId(), lastTrain, part, lsInterval.getStart(), lsInterval.getEnd(), lsInterval.getSpeed(), TimeIntervalDirection.toTimeIntervalDirection(lsInterval.getDirection()), track, 0);
        if (lsInterval.getComment() != null && !lsInterval.getComment().equals(""))
            interval.setAttribute(TimeInterval.ATTR_COMMENT, lsInterval.getComment());

        // add interval to train
        lastTrain.addInterval(interval);

        if (lsInterval.getAttributes() != null) {
            interval.getAttributes().add(lsInterval.getAttributes().convertToAttributes());
        }
    }

    @Override
    public void visit(LSModelInfo lsInfo) {
        if (lsInfo != null) {
            diagram.setAttribute(TrainDiagram.ATTR_SCALE, Scale.fromString(lsInfo.getScale()));
            diagram.setAttribute(TrainDiagram.ATTR_TIME_SCALE, Double.valueOf(lsInfo.getTimeScale()));
        }
    }

    @Override
    public void visit(LSRoute lsRoute) {
        Route route = new Route(lsRoute.getUuid(), diagram, lsRoute.getName());
        route.setNetPart(lsRoute.isNetPart());
        for (int id : lsRoute.getIds()) {
            RouteSegment<?> segment = (RouteSegment<?>) ids.get(id);
            route.getSegments().add(segment);
        }
        // add route to diagram
        diagram.getRoutes().add(route);
    }

    @Override
    public void visit(LSTrainsCycle lsCycle) {
        TrainsCycleType type = null;
        for (TrainsCycleType t : diagram.getCycleTypes()) {
            if (lsCycle.getType().equals(t.getKey())) {
                type = t;
            }
        }
        TrainsCycle cycle = new TrainsCycle(lsCycle.getUuid(), diagram, lsCycle.getName(), lsCycle.getDescription(), type);
        cycle.setAttribute("comment", lsCycle.getComment());
        if (lsCycle.getItems() != null) {
            for (LSTrainsCycleItem item : lsCycle.getItems()) {
                Train train = (Train) ids.get(item.getTrainId());
                TrainsCycleItem tcItem = new TrainsCycleItem(cycle, train, LocalizedString.fromString(item.getComment()), null, null);
                cycle.addItem(tcItem);
            }
        }

        cycle.getType().getCycles().add(cycle);
    }

    @Override
    public void visit(LSImage lsImage) {
        TrainDiagramPartFactory factory = diagram.getPartFactory();
        TimetableImage image = factory.createImage(factory.createId(), lsImage.getFilename(),
                lsImage.getImageWidth(), lsImage.getImageHeight());
        diagram.getImages().add(image);
    }
}
