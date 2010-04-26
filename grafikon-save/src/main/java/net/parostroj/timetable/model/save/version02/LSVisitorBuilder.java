package net.parostroj.timetable.model.save.version02;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.save.LSPenaltyTableHelper;
import net.parostroj.timetable.model.save.LSTrainTypeList;

public class LSVisitorBuilder implements LSVisitor {

    private TrainDiagram diagram;
    private Map<Integer, Object> ids = new HashMap<Integer, Object>();
    private LSTrainTypeList trainTypeList;
    // last node
    private Node lastNode;
    // last train
    private Train lastTrain;
    // last line
    private Line lastLine;
    private List<Train> trains;

    public LSVisitorBuilder(LSTrainTypeList list) {
        this.trainTypeList = list;
        this.trains = new LinkedList<Train>();
    }

    @Override
    public void visit(LSTrainDiagram lsDiagram) {
        // create empty net
        diagram = new TrainDiagram(UUID.randomUUID().toString(), trainTypeList.getTrainsData());
        LSPenaltyTableHelper.fillPenaltyTable(diagram.getPenaltyTable());
        trainTypeList.updateMapping(diagram);
        for (TrainType type : trainTypeList.getTrainTypeList()) {
            diagram.addTrainType(type);
        }
        if (lsDiagram.getAttributes() != null) {
            diagram.setAttributes(lsDiagram.getAttributes().convertToAttributes());
        }
    }

    @Override
    public void visit(LSNode lsNode) {
        NodeType type = NodeType.valueOf(lsNode.getNodeType());
        Node node = diagram.createNode(lsNode.getUuid(), type, lsNode.getName(), lsNode.getAbbr());
        if (lsNode.getAttributes() != null) {
            node.setAttributes(lsNode.getAttributes().convertToAttributes());
        }
        node.setPositionX(lsNode.getX());
        node.setPositionY(lsNode.getY());
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
            nodeTrack.setAttributes(lsNodeTrack.getAttributes().convertToAttributes());        // add to last node
        }
        lastNode.addTrack(nodeTrack);
    }

    /**
     * @return created train diagram
     */
    public TrainDiagram getTrainDiagram() {
        for (Train t : trains) {
            diagram.addTrain(t);
        }
        return diagram;
    }

    @Override
    public void visit(LSLine lsLine) {
        Node from = (Node) ids.get(lsLine.getSourceId());
        Node to = (Node) ids.get(lsLine.getTargetId());
        Line line = diagram.createLine(lsLine.getUuid(), lsLine.getLength(), from, to, lsLine.getTopSpeed());
        ids.put(lsLine.getId(), line);

        if (lsLine.getAttributes() != null) {
            line.setAttributes(lsLine.getAttributes().convertToAttributes());        // add to net
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
            lineTrack.setAttributes(lsLineTrack.getAttributes().convertToAttributes());        // add to last line
        }
        lastLine.addTrack(lineTrack);
    }

    @Override
    public void visit(LSTrain lsTrain) {
        TrainType type = trainTypeList.getTrainType(lsTrain.getTrainType());
        Train train = diagram.createTrain(lsTrain.getUuid());
        train.setNumber(lsTrain.getName());
        train.setType(type);
        ids.put(lsTrain.getId(), train);

        train.setTopSpeed(lsTrain.getTopSpeed());
        train.setType(type);
        train.setDescription(lsTrain.getDescription());
        if (lsTrain.getAttributes() != null) {
            train.setAttributes(lsTrain.getAttributes().convertToAttributes());
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
        RouteSegment part = (RouteSegment) ids.get(lsInterval.getOwnerId());
        TimeInterval interval = new TimeInterval(UUID.randomUUID().toString(), lastTrain, part, lsInterval.getStart(), lsInterval.getEnd(), lsInterval.getSpeed(), TimeIntervalDirection.toTimeIntervalDirection(lsInterval.getDirection()), track);
        if (lsInterval.getComment() != null && !lsInterval.getComment().equals(""))
            interval.setAttribute("comment", lsInterval.getComment());

        // add interval to train
        lastTrain.addInterval(interval);

        if (lsInterval.getAttributes() != null) {
            interval.setAttributes(lsInterval.getAttributes().convertToAttributes());
        }
    }

    @Override
    public void visit(LSModelInfo lsInfo) {
        if (lsInfo != null) {
            diagram.setAttribute("scale", Scale.fromString(lsInfo.getScale()));
            diagram.setAttribute("time.scale", Double.valueOf(lsInfo.getTimeScale()));
        }
    }

    @Override
    public void visit(LSRoute lsRoute) {
        Route route = new Route(lsRoute.getUuid(), lsRoute.getName());
        route.setNetPart(lsRoute.isNetPart());
        for (int id : lsRoute.getIds()) {
            RouteSegment segment = (RouteSegment) ids.get(id);
            route.getSegments().add(segment);
        }
        // add route to diagram
        diagram.addRoute(route);
    }

    @Override
    public void visit(LSTrainsCycle lsCycle) {
        TrainsCycleType type = TrainsCycleType.valueOf(lsCycle.getType());
        TrainsCycle cycle = new TrainsCycle(lsCycle.getUuid(), lsCycle.getName(), lsCycle.getDescription(), type);
        cycle.setAttribute("comment", lsCycle.getComment());
        if (lsCycle.getItems() != null) {
            for (LSTrainsCycleItem item : lsCycle.getItems()) {
                Train train = (Train) ids.get(item.getTrainId());
                TrainsCycleItem tcItem = new TrainsCycleItem(cycle, train, item.getComment(), null, null);
                cycle.addItem(tcItem);
            }
        }

        diagram.addCycle(cycle);
    }

    @Override
    public void visit(LSImage lsImage) {
        TimetableImage image = new TimetableImage(lsImage.getFilename(), lsImage.getImageWidth(), lsImage.getImageHeight());
        diagram.addImage(image);
    }
}
