package net.parostroj.timetable.model.library;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.AttributesHolder;
import net.parostroj.timetable.model.CopyFactory;
import net.parostroj.timetable.model.LibraryPartFactory;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.NodeType;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.OutputTemplate;

public class Library implements AttributesHolder, Iterable<LibraryItem> {

    private final Collection<LibraryItem> items;
    private final Attributes attributes;

    private final LibraryAddHandler addHandler;
    private final LibraryPartFactory factory;

    Library() {
        items = new ArrayList<>();
        attributes = new Attributes();
        addHandler = new LibraryAddHandler();
        factory = LibraryPartFactory.getInstance();
    }

    public Collection<LibraryItem> getItems() {
        return items;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    public LibraryItem add(OutputTemplate template) {
        OutputTemplate templateCopy = CopyFactory.getInstance().copy(template);
        addHandler.stripObjectIdAttributes(templateCopy);

        // create item and add it to library
        return addImpl(templateCopy, LibraryItemType.OUTPUT_TEMPLATE);
    }

    public LibraryItem add(Node node) {
        Node nodeCopy = CopyFactory.getInstance().copy(node);
        addHandler.stripObjectIdAttributes(nodeCopy);
        for (NodeTrack track : nodeCopy.getTracks()) {
            addHandler.stripObjectIdAttributes(track);
        }

        // create item and add it to library
        return addImpl(nodeCopy, LibraryItemType.NODE);
    }

    public LibraryItem addOutputTemplate(String id, String name) {
        return addImpl(factory.createOutputTemplate(id, name), LibraryItemType.OUTPUT_TEMPLATE);
    }

    public LibraryItem addNode(String id, NodeType type, String name, String abbr) {
        return addImpl(factory.createNode(id, type, name, abbr), LibraryItemType.NODE);
    }

    private LibraryItem addImpl(ObjectWithId object, LibraryItemType type) {
        LibraryItem item = new LibraryItem(type, object);
        items.add(item);
        return item;
    }

    @Override
    public Iterator<LibraryItem> iterator() {
        return items.iterator();
    }

    @Override
    public String toString() {
        return String.format("Items: %d", items.size());
    }
}
