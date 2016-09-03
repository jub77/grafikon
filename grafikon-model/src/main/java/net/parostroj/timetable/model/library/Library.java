package net.parostroj.timetable.model.library;

import java.util.Iterator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
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
import net.parostroj.timetable.model.PartFactory;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainTypeCategory;
import net.parostroj.timetable.model.WeightTableRow;

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

    public Library() {
        this(new LibraryAddHandler());
    }

    Library(LibraryAddHandler addHandler) {
        this.itemMap = ArrayListMultimap.create(LibraryItemType.values().length, 5);
        this.attributes = new Attributes();
        this.addHandler = addHandler;
        this.factory = new LibraryPartFactory(this);
        this.copyFactory = new CopyFactory(factory);
    }

    public Multimap<LibraryItemType, LibraryItem> getItems() {
        return itemMap;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    public LibraryItem addObject(ObjectWithId object) {
        return this.addImpl(object, LibraryItemType.getByItemClass(object.getClass()));
    }

    public LibraryItem importObject(ObjectWithId object) {
        LibraryItemType type = LibraryItemType.getByItemClass(object.getClass());
        LibraryItem item = null;
        switch (type) {
            case ENGINE_CLASS: item = this.importEngineClass((EngineClass) object); break;
            case LINE_CLASS: item = this.importLineClass((LineClass) object); break;
            case NODE: item = this.importNode((Node) object); break;
            case OUTPUT_TEMPLATE: item = this.importOutputTemplate((OutputTemplate) object); break;
            case TRAIN_TYPE: item = this.importTrainType((TrainType) object); break;
            case TRAIN_TYPE_CATEGORY: item = this.importTrainTypeCategory((TrainTypeCategory) object); break;
        }
        return item;
    }

    public LibraryItem importOutputTemplate(OutputTemplate template) {
        OutputTemplate templateCopy = copyFactory.copy(template, template.getId());
        addHandler.stripObjectIdAttributes(templateCopy);

        // create item and add it to library
        return addImpl(templateCopy, LibraryItemType.OUTPUT_TEMPLATE);
    }

    public LibraryItem importNode(Node node) {
        Node nodeCopy = copyFactory.copy(node, node.getId());
        addHandler.stripObjectIdAttributes(nodeCopy);
        for (NodeTrack track : nodeCopy.getTracks()) {
            addHandler.stripObjectIdAttributes(track);
        }

        // create item and add it to library
        return addImpl(nodeCopy, LibraryItemType.NODE);
    }

    public LibraryItem importEngineClass(EngineClass engineClass) {
        EngineClass engineClassCopy = copyFactory.copy(engineClass, engineClass.getId());
        for (WeightTableRow row : engineClassCopy.getWeightTable()) {
            for (LineClass origLineClass : ImmutableList.copyOf(row.getWeights().keySet())) {
                Integer weight = row.getWeight(origLineClass);
                row.removeWeightInfo(origLineClass);
                ObjectWithId currentLineClass = this.getObjectById(origLineClass.getId());
                if (currentLineClass == null || !(currentLineClass instanceof LineClass)) {
                    throw new IllegalArgumentException("Line class missing from library: " + origLineClass);
                }
                row.setWeightInfo((LineClass) currentLineClass, weight);
            }
        }
        return addImpl(engineClassCopy, LibraryItemType.ENGINE_CLASS);
    }

    public LibraryItem importTrainType(TrainType trainType) {
        TrainType trainTypeCopy = copyFactory.copy(trainType, trainType.getId());
        // TODO fix train name template reference
        if (trainType.getCategory() != null) {
            ObjectWithId category = this.getObjectById(trainType.getCategory().getId());
            if (category == null) {
                LibraryItem categoryItem = this.importTrainTypeCategory(trainType.getCategory());
                category = categoryItem.getObject();
            } else if (!(category instanceof TrainTypeCategory)) {
                throw new IllegalArgumentException("Wrong type of category: " + category);
            }
            trainTypeCopy.setCategory   ((TrainTypeCategory) category);
        }
        return addImpl(trainTypeCopy, LibraryItemType.TRAIN_TYPE);
    }

    public LibraryItem importLineClass(LineClass lineClass) {
        return addImpl(copyFactory.copy(lineClass, lineClass.getId()), LibraryItemType.LINE_CLASS);
    }

    public LibraryItem importTrainTypeCategory(TrainTypeCategory category) {
        return addImpl(copyFactory.copy(category, category.getId()), LibraryItemType.TRAIN_TYPE_CATEGORY);
    }

    public LibraryItem importOutputTemplate(String id, String name) {
        OutputTemplate template = factory.createOutputTemplate(id);
        template.setKey(name);
        return addImpl(template, LibraryItemType.OUTPUT_TEMPLATE);
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
        TrainType type = factory.createTrainType(id);
        type.setDesc(desc);
        return addImpl(type, LibraryItemType.TRAIN_TYPE);
    }

    public LibraryItem addTrainTypeCategory(String id, LocalizedString name, String key) {
        TrainTypeCategory category = factory.createTrainTypeCategory(id);
        category.setKey(key);
        category.setName(name);
        return addImpl(category, LibraryItemType.TRAIN_TYPE_CATEGORY);
    }

    private LibraryItem addImpl(ObjectWithId object, LibraryItemType type) {
        LibraryItem item = new LibraryItem(type, object);
        itemMap.put(type, item);
        return item;
    }

    @Override
    public Iterator<LibraryItem> iterator() {
        return itemMap.values().iterator();
    }

    @Override
    public String toString() {
        return String.format("Items: %d", itemMap.size());
    }

    public ObjectWithId getObjectById(String id) {
        return FluentIterable.from(itemMap.values())
                .transform(item -> item.getObject())
                .firstMatch(object -> object.getId().equals(id))
                .orNull();
    }

    public PartFactory getPartFactory() {
        return factory;
    }
}
