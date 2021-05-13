package net.parostroj.timetable.model.save.version01;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.save.LSPenaltyTableHelper;
import net.parostroj.timetable.model.save.LSTrainTypeList;
import net.parostroj.timetable.utils.IdGenerator;

public class LSVisitorBuilder implements LSVisitor {

    private TrainDiagram diagram;
    private final Map<Integer, Object> ids = new HashMap<>();
    private final LSTrainTypeList trainTypeList;
    // last station
    private Node lastStation;
    // last train
    private Train lastTrain;
    private final List<Train> trains;

    public LSVisitorBuilder(LSTrainTypeList list) {
        this.trainTypeList = list;
        this.trains = new LinkedList<>();
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
        Node node = diagram.getPartFactory().createNode(this.createId());
        node.setType(type);
        node.setName(lsNode.getName());
        node.setAbbr(lsNode.getAbbr());
        node.setAttribute(Node.ATTR_INTERLOCKING_PLANT, lsNode.getInterlockingPlant());
        node.setLocation(new Location(lsNode.getX(), lsNode.getY()));
        ids.put(lsNode.getId(), node);

        // add to net
        diagram.getNet().addNode(node);
        // set last station
        lastStation = node;
    }

    @Override
    public void visit(LSNodeTrack lsNodeTrack) {
        NodeTrack stationTrack = new NodeTrack(this.createId(), lastStation, lsNodeTrack.getNumber());
        ids.put(lsNodeTrack.getId(), stationTrack);

        stationTrack.setPlatform(lsNodeTrack.isPlatform());

        // add to last station
        lastStation.getTracks().add(stationTrack);
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
        Line line = diagram.getPartFactory().createLine(this.createId());
        line.setLength(lsLine.getLength());
        LineTrack lt = new LineTrack(this.createId(), line, "1");
        line.getTracks().add(lt);
        ids.put(lsLine.getId(), line);

        line.setTopSpeed(lsLine.getTopSpeed() > 0 ? lsLine.getTopSpeed() : null);

        // add to net
        Net net = diagram.getNet();
        net.addLine(line, from, to);

        NodeTrack fromNt = (NodeTrack) ids.get(lsLine.getSourceTrackId());
        NodeTrack toNt = (NodeTrack) ids.get(lsLine.getTargetTrackId());
        TrackConnector fromConn = diagram.getPartFactory().createDefaultConnector(
                IdGenerator.getInstance().getId(),
                from,
                "2",
                Node.Side.RIGHT,
                Optional.ofNullable(fromNt));
        fromConn.setLineTrack(Optional.ofNullable(lt));
        from.getConnectors().add(fromConn);
        TrackConnector toConn = diagram.getPartFactory().createDefaultConnector(
                IdGenerator.getInstance().getId(),
                to,
                "1",
                Node.Side.LEFT,
                Optional.ofNullable(toNt));
        toConn.setLineTrack(Optional.ofNullable(lt));
        to.getConnectors().add(toConn);
    }

    @Override
    public void visit(LSTrain lsTrain) {
        TrainType type = trainTypeList.getTrainType(lsTrain.getTrainType());
        Train train = diagram.getPartFactory().createTrain(this.createId());
        train.setNumber(lsTrain.getName());
        train.setType(type);
        ids.put(lsTrain.getId(), train);

        if (lsTrain.getTopSpeed() > 0) {
            train.setTopSpeed(lsTrain.getTopSpeed());
        }
        train.setType(type);
        train.setDescription(lsTrain.getDescription());
        train.setAttribute(Train.ATTR_ELECTRIC, lsTrain.isElectric());
        train.setAttribute(Train.ATTR_DIESEL, lsTrain.isDiesel());
        train.setAttribute("weight.info", lsTrain.getWeightInfo());
        train.setAttribute("route.info", lsTrain.getRouteInfo());

        this.trains.add(train);

        // set last train
        lastTrain = train;
    }

    @Override
    public void visit(LSTimeInterval lsInterval) {
        // add station track
        Track track = (Track) ids.get(lsInterval.getStationTrackId());

        // add to the last train
        NetSegment<?> part = (NetSegment<?>) ids.get(lsInterval.getRoutePartId());
        TimeInterval interval = new TimeInterval(createId(), lastTrain, part, lsInterval.getStart(), lsInterval.getEnd(), lsInterval.getSpeed(), TimeIntervalDirection.toTimeIntervalDirection(lsInterval.getDirection()), track, 0);
        if (lsInterval.getComment() != null && !lsInterval.getComment().equals(""))
            interval.setAttribute(TimeInterval.ATTR_COMMENT, lsInterval.getComment());

        // add interval to train
        lastTrain.addInterval(interval);

        // add backward compactibility - owner is a line - add first track from line
        if (part instanceof Line) {
            interval.setTrack(((Line) part).getTracks().get(0));
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
        Route route = new Route(this.createId(), diagram);
        for (int id : lsRoute.getIds()) {
            NetSegment<?> segment = (NetSegment<?>) ids.get(id);
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
        TrainsCycle cycle = new TrainsCycle(this.createId(), diagram, lsCycle.getName(), lsCycle.getDescription(), type);
        cycle.setAttribute("comment", lsCycle.getComment());
        if (lsCycle.getItems() != null) {
            for (LSTrainsCycleItem item : lsCycle.getItems()) {
                Train train = (Train) ids.get(item.getTrainId());
                TrainsCycleItem tcItem = new TrainsCycleItem(cycle, train, LocalizedString.fromString(item.getComment()),
                        null, null);
                cycle.addItem(tcItem);
            }
        }

        cycle.getType().getCycles().add(cycle);
    }

    @Override
    public void visit(LSImage lsImage) {
        TrainDiagramPartFactory factory = diagram.getPartFactory();
        TimetableImage image = factory.createImage(factory.createId(), lsImage.getFilename(), 0, 0);
        diagram.getImages().add(image);
    }

    private String createId() {
        return IdGenerator.getInstance().getId();
    }
}
