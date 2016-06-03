package net.parostroj.timetable.model.library;

import java.util.Iterator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.AttributesHolder;
import net.parostroj.timetable.model.CopyFactory;
import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.LibraryPartFactory;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.NodeType;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.utils.IdGenerator;

/**
 * Library of train diagram items, that can be reused.
 *
 * @author jub
 */
public class Library implements AttributesHolder, Iterable<LibraryItem> {

    private final Multimap<LibraryItemType, LibraryItem> itemMap;
    private final Attributes attributes;

    private final LibraryAddHandler addHandler;
    private final LibraryPartFactory factory;
    private final CopyFactory copyFactory;

    Library() {
        this(LibraryPartFactory.getInstance(), CopyFactory.getInstance(), new LibraryAddHandler());
    }

    Library(LibraryPartFactory factory, CopyFactory copyFactory, LibraryAddHandler addHandler) {
        this.itemMap = ArrayListMultimap.create(LibraryItemType.values().length, 5);
        this.attributes = new Attributes();
        this.addHandler = addHandler;
        this.factory = factory;
        this.copyFactory = copyFactory;
    }

    public Multimap<LibraryItemType, LibraryItem> getItems() {
        return itemMap;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    public LibraryItem add(OutputTemplate template) {
        OutputTemplate templateCopy = copyFactory.copy(template, getNewId());
        addHandler.stripObjectIdAttributes(templateCopy);

        // create item and add it to library
        return addImpl(templateCopy, LibraryItemType.OUTPUT_TEMPLATE);
    }

    public LibraryItem add(Node node) {
        Node nodeCopy = copyFactory.copy(node, getNewId());
        addHandler.stripObjectIdAttributes(nodeCopy);
        for (NodeTrack track : nodeCopy.getTracks()) {
            addHandler.stripObjectIdAttributes(track);
        }

        // create item and add it to library
        return addImpl(nodeCopy, LibraryItemType.NODE);
    }

    public LibraryItem add(EngineClass engineClass) {
        EngineClass engineClassCopy = copyFactory.copy(engineClass, getNewId());
        // TODO replace line classes

        return addImpl(engineClassCopy, LibraryItemType.ENGINE_CLASS);
    }

    public LibraryItem add(LineClass lineClass) {
        LineClass lineClassCopy = copyFactory.copy(lineClass, getNewId());
        return addImpl(lineClassCopy, LibraryItemType.LINE_CLASS);
    }

    public LibraryItem addOutputTemplate(String id, String name) {
        return addImpl(factory.createOutputTemplate(id, name), LibraryItemType.OUTPUT_TEMPLATE);
    }

    public LibraryItem addNode(String id, NodeType type, String name, String abbr) {
        return addImpl(factory.createNode(id, type, name, abbr), LibraryItemType.NODE);
    }

    public LibraryItem addEngineClass(String id, String name) {
        return addImpl(factory.createEngineClass(id, name), LibraryItemType.ENGINE_CLASS);
    }

    public LibraryItem addLineClass(String id, String name) {
        return addImpl(factory.createLineClass(id, name), LibraryItemType.LINE_CLASS);
    }

    private LibraryItem addImpl(ObjectWithId object, LibraryItemType type) {
        LibraryItem item = new LibraryItem(type, object);
        itemMap.put(type, item);
        return item;
    }

    private String getNewId() {
        return IdGenerator.getInstance().getId();
    }

    @Override
    public Iterator<LibraryItem> iterator() {
        return itemMap.values().iterator();
    }

    @Override
    public String toString() {
        return String.format("Items: %d", itemMap.size());
    }
}
