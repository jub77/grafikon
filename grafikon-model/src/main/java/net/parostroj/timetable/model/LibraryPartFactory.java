package net.parostroj.timetable.model;

/**
 * Factory for creating new train diagram parts that can be stored in the library.
 *
 * @author jub
 */
public class LibraryPartFactory {

    private LibraryPartFactory() {}

    public static LibraryPartFactory getInstance() {
        return new LibraryPartFactory();
    }

    public OutputTemplate createOutputTemplate(String id, String key) {
        OutputTemplate template = new OutputTemplate(id, null);
        template.setKey(key);
        return template;
    }

    public Node createNode(String id, NodeType type, String name, String abbr) {
        return new Node(id, null, type, name, abbr);
    }

    public EngineClass createEngineClass(String id, String name) {
        return new EngineClass(id, name);
    }

    public LineClass createLineClass(String id, String name) {
        return new LineClass(id, name);
    }

    public ObjectWithId createTrainType(String id, LocalizedString desc) {
        TrainType trainType = new TrainType(id, null);
        trainType.setDesc(desc);
        return trainType;
    }
}
