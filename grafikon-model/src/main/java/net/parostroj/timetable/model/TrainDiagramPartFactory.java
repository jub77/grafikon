package net.parostroj.timetable.model;

import java.util.Optional;

import net.parostroj.timetable.utils.IdGenerator;

public class TrainDiagramPartFactory implements PartFactory {

    private final TrainDiagram diagram;

    TrainDiagramPartFactory(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    /**
     * @return new (not used) id
     */
    public String createId() {
        return IdGenerator.getInstance().getId();
    }

    /**
     * creates new train.
     *
     * @param id train id
     * @return a new train
     */
    public Train createTrain(String id) {
        return new Train(id, diagram);
    }

    /**
     * create new line.
     *
     * @param id line id
     * @return a new line
     */
    public Line createLine(String id) {
        return new Line(id, diagram);
    }

    @Override
    public Node createNode(String id) {
        return new Node(id, diagram);
    }

    /**
     * Creates new track connector for node.
     *
     * @param id id
     * @param node node
     * @return a new connector
     */
    @Override
    public TrackConnector createConnector(String id, Node node) {
        return new TrackConnectorImpl(id, node);
    }

    /**
     * Creates default connector connected to all tracks with optional straight
     * track.
     *
     * @see #createConnector(String, Node)
     */
    public TrackConnector createDefaultConnector(String id, Node node, String number,
            Node.Side orientation, Optional<NodeTrack> straightTrack) {
        TrackConnector connector = this.createConnector(id, node);
        connector.setNumber(number);
        connector.setOrientation(orientation);
        NodeTrack st = straightTrack.orElse(null);
        node.tracks.forEach(track -> {
            TrackConnectorSwitch sw = connector.createSwitch(IdGenerator.getInstance().getId());
            sw.setNodeTrack(track);
            if (track == st) {
                sw.setStraight(true);
            }
            connector.getSwitches().add(sw);
        });
        return connector;
    }

    /**
     * creates new train type.
     *
     * @param id id
     * @return a new train type
     */
    @Override
    public TrainType createTrainType(String id) {
        return new TrainType(id, diagram);
    }

    /**
     * creates new image.
     *
     * @param id id
     * @param filename filename
     * @param width width of the image in pixels
     * @param height height of the image in pixels
     * @return a new image
     */
    public TimetableImage createImage(String id, String filename, int width, int height) {
        return new TimetableImage(id, filename, width, height);
    }

    /**
     * creates group.
     *
     * @param id id
     * @return new group
     */
    public Group createGroup(String id) {
        return new Group(id, diagram);
    }

    /**
     * Creates region.
     *
     * @param id id
     * @return new region
     */
    public Region createRegion(String id) {
        return new Region(id, diagram);
    }

    /**
     * Creates company.
     *
     * @param id id
     * @return new company
     */
    public Company createCompany(String id) {
        return new Company(id, diagram);
    }

    /**
     * Creates new cycle type.
     *
     * @param id id
     * @return new cycle type
     */
    public TrainsCycleType createCycleType(String id) {
        return new TrainsCycleType(id, diagram);
    }

    /**
     * Creates new output template.
     *
     * @param id id
     * @return new output template
     */
    @Override
    public OutputTemplate createOutputTemplate(String id) {
        if (!getDiagramType().isOutputTemplateAllowed()) {
            throw new GrafikonException("Output template forbidden");
        }
        return new OutputTemplate(id, diagram);
    }

    /**
     * Creates new output.
     *
     * @param id id
     * @return new output associated with diagram
     */
    public Output createOutput(String id) {
        return new Output(id, diagram);
    }

    /**
     * Creates script for defautl time computation.
     *
     * @return script for default time computation
     */
    public Script createDefaultTimeScript() {
        return Script.create(TrainDiagramFactory.getDefaultTimeScript(), Script.Language.GROOVY);
    }

    @Override
    public TrainDiagramType getDiagramType() {
        return diagram.getRuntimeInfo().getDiagramType();
    }
}
