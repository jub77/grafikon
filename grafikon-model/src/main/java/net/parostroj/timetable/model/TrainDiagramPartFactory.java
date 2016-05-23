package net.parostroj.timetable.model;

import net.parostroj.timetable.utils.IdGenerator;

public class TrainDiagramPartFactory {

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
     * @param length length
     * @param from from node
     * @param to to node
     * @param topSpeed top speed
     * @return a new line
     */
    public Line createLine(String id, int length, Node from, Node to, Integer topSpeed) {
        return new Line(id, diagram, length, from, to, topSpeed);
    }

    /**
     * create new node.
     *
     * @param id id
     * @param type node type
     * @param name name
     * @param abbr abbreviation
     * @return a new node
     */
    public Node createNode(String id, NodeType type, String name, String abbr) {
        return new Node(id, diagram, type, name, abbr);
    }

    /**
     * creates new train type.
     *
     * @param id id
     * @return a new train type
     */
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
    public FreightNet createFreightNet(String id) {
        return new FreightNet(id, diagram);
    }

    /**
     * Creates new output template.
     *
     * @param id id
     * @return new output template
     */
    public OutputTemplate createOutputTemplate(String id) {
        return new OutputTemplate(id, diagram);
    }
}
