package net.parostroj.timetable.model;

public interface PartFactory {

    OutputTemplate createOutputTemplate(String id);

    Node createNode(String id, NodeType type, String name, String abbr);

    TrainType createTrainType(String id);
}
