package net.parostroj.timetable.model;

public class LibraryPartFactory {

    private LibraryPartFactory() {}

    public static LibraryPartFactory getInstance() {
        return new LibraryPartFactory();
    }

    public OutputTemplate createOutputTemplate(String id, String name) {
        OutputTemplate template = new OutputTemplate(id, null);
        template.setName(name);
        return template;
    }

    public Node createNode(String id, NodeType type, String name, String abbr) {
        return new Node(id, null, type, name, abbr);
    }
}
