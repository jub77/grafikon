package net.parostroj.timetable.model;

import net.parostroj.timetable.model.library.Library;

/**
 * Factory for creating new train diagram parts that can be stored in the library.
 *
 * @author jub
 */
public class LibraryPartFactory implements PartFactory {

    public LibraryPartFactory(Library library) {
    }

    @Override
    public OutputTemplate createOutputTemplate(String id) {
        return new OutputTemplate(id, null);
    }

    @Override
    public Node createNode(String id, NodeType type, String name, String abbr) {
        return new Node(id, null, type, name, abbr);
    }

    public EngineClass createEngineClass(String id, String name) {
        return new EngineClass(id, name);
    }

    public LineClass createLineClass(String id, String name) {
        return new LineClass(id, name);
    }

    @Override
    public TrainType createTrainType(String id) {
        return new TrainType(id, null);
    }

    public TrainTypeCategory createTrainTypeCategory(String id) {
        return new TrainTypeCategory(id);
    }
}
