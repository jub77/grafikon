package net.parostroj.timetable.model;

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
    public Node createNode(String id, NodeType type, String name, String abbr) {
        return new Node(id, diagram, type, name, abbr);
    }

    /**
     * Creates new track connector for node.
     *
     * @param id id
     * @param nodePort node port
     * @param number number of the connector
     * @return a new connector
     */
    public TrackConnector createConnector(String id, NodePort nodePort, String number) {
        return new TrackConnectorImpl(id, nodePort, number);
    }

    /**
     * Creates new node port for node.
     *
     * @param node node
     * @param side side of the node port
     * @return a new node port
     */
    public NodePort createNodePort(Node node, Node.Side side) {
        return node.createNodePort(side);
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
     * Creates new freight net.
     *
     * @param id id
     * @return new freight net
     */
    public FreightNet createFreightNet() {
        return new FreightNet(diagram);
    }

    /**
     * Creates new output template.
     *
     * @param id id
     * @return new output template
     */
    @Override
    public OutputTemplate createOutputTemplate(String id) {
        return new OutputTemplate(id, diagram);
    }

    /**
     * Creates new output.
     *
     * @param id id
     * @return new output associated with diagram
     */
    public Output createOutput(String id) {
        return new Output(id);
    }
}
