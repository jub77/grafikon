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
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.NodeType;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.TrainType;
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

    public LibraryItem add(ObjectWithId object) {
        LibraryItemType type = LibraryItemType.getByItemClass(object.getClass());
        LibraryItem item = null;
        switch (type) {
            case ENGINE_CLASS: item = this.addEngineClass((EngineClass) object); break;
            case LINE_CLASS: item = this.addLineClass((LineClass) object); break;
            case NODE: item = this.addNode((Node) object); break;
            case OUTPUT_TEMPLATE: item = this.addOutputTemplate((OutputTemplate) object); break;
            case TRAIN_TYPE: item = this.addTrainType((TrainType) object); break;
        }
        return item;
    }

    public LibraryItem addOutputTemplate(OutputTemplate template) {
        OutputTemplate templateCopy = copyFactory.copy(template, getNewId());
        addHandler.stripObjectIdAttributes(templateCopy);

        // create item and add it to library
        return addImpl(templateCopy, LibraryItemType.OUTPUT_TEMPLATE);
    }

    public LibraryItem addNode(Node node) {
        Node nodeCopy = copyFactory.copy(node, getNewId());
        addHandler.stripObjectIdAttributes(nodeCopy);
        for (NodeTrack track : nodeCopy.getTracks()) {
            addHandler.stripObjectIdAttributes(track);
        }

        // create item and add it to library
        return addImpl(nodeCopy, LibraryItemType.NODE);
    }

    public LibraryItem addEngineClass(EngineClass engineClass) {
        EngineClass engineClassCopy = copyFactory.copy(engineClass, getNewId());
        // TODO replace line classes
        return addImpl(engineClassCopy, LibraryItemType.ENGINE_CLASS);
    }

    public LibraryItem addTrainType(TrainType trainType) {
        TrainType trainTypeCopy = copyFactory.copy(trainType, getNewId());
        // TODO fix train name template reference
        return addImpl(trainTypeCopy, LibraryItemType.TRAIN_TYPE);
    }

    public LibraryItem addLineClass(LineClass lineClass) {
        return addImpl(copyFactory.copy(lineClass, getNewId()), LibraryItemType.LINE_CLASS);
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

    public LibraryItem addTrainType(String id, LocalizedString desc) {
        return addImpl(factory.createTrainType(id, desc), LibraryItemType.TRAIN_TYPE);
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
