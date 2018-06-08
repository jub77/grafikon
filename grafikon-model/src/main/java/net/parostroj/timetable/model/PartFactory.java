package net.parostroj.timetable.model;

public interface PartFactory {

    OutputTemplate createOutputTemplate(String id);

    /**
     * Creates new node.
     *
     * @param id id
     * @param type node type
     * @param name name
     * @param abbr abbreviation
     * @return a new node
     */
    Node createNode(String id, NodeType type, String name, String abbr);

    TrainType createTrainType(String id);
}
