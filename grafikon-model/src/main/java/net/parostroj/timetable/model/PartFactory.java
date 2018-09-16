package net.parostroj.timetable.model;

public interface PartFactory {

    OutputTemplate createOutputTemplate(String id);

    Node createNode(String id);

    TrainType createTrainType(String id);
}
