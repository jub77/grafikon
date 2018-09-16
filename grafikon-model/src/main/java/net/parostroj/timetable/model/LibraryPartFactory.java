package net.parostroj.timetable.model;

/**
 * Factory for creating new train diagram parts that can be stored in the library.
 *
 * @author jub
 */
public class LibraryPartFactory implements PartFactory {

    @Override
    public OutputTemplate createOutputTemplate(String id) {
        return new OutputTemplate(id, null);
    }

    @Override
    public Node createNode(String id) {
        return new Node(id, null);
    }

    public EngineClass createEngineClass(String id) {
        return new EngineClass(id);
    }

    public LineClass createLineClass(String id) {
        return new LineClass(id);
    }

    @Override
    public TrainType createTrainType(String id) {
        return new TrainType(id, null);
    }

    public TrainTypeCategory createTrainTypeCategory(String id) {
        return new TrainTypeCategory(id);
    }
}
