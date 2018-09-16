package net.parostroj.timetable.model.library;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;

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

public class LibraryBuilder {

    public static class Config {

        private boolean addMissing;
        private LibraryFactory libraryFactory;

        public Config setAddMissing(boolean addMissing) {
            this.addMissing = addMissing;
            return this;
        }

        public Config setLibraryFactory(LibraryFactory libraryFactory) {
            this.libraryFactory = libraryFactory;
            return this;
        }

        private LibraryFactory getLibraryFactory() {
            return libraryFactory == null ? LibraryFactory.getInstance() : libraryFactory;
        }

        public boolean isAddMissing() {
            return addMissing;
        }
    }

    private final Config config;
    private final LibraryAddHandler addHandler;
    private final LibraryPartFactory factory;
    private final CopyFactory copyFactory;
    private final Map<String, LibraryItem> items;

    public LibraryBuilder() {
        this(new Config());
    }

    public LibraryBuilder(Config config) {
        this.config = config;
        this.addHandler = new LibraryAddHandler();
        this.factory = new LibraryPartFactory();
        this.copyFactory = new CopyFactory(factory);
        this.items = new LinkedHashMap<>();
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
            default: throw new IllegalArgumentException("Unknown type of the object: " + object);
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
                if (currentLineClass == null) {
                    if (config.isAddMissing()) {
                        LibraryItem lineClassItem = this.importLineClass(origLineClass);
                        currentLineClass = lineClassItem.getObject();
                    } else {
                        throw new IllegalArgumentException("Line class missing from library: " + origLineClass);
                    }
                } else if (!(currentLineClass instanceof LineClass)) {
                    throw new IllegalArgumentException("Wrong type of line class: " + origLineClass);
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
                if (config.isAddMissing()) {
                    LibraryItem categoryItem = this.importTrainTypeCategory(trainType.getCategory());
                    category = categoryItem.getObject();
                } else {
                    throw new IllegalArgumentException("Train type category missing from library: " + trainType.getCategory());
                }
            } else if (!(category instanceof TrainTypeCategory)) {
                throw new IllegalArgumentException("Wrong type of category: " + category);
            }
            trainTypeCopy.setCategory((TrainTypeCategory) category);
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
        Node node = factory.createNode(id);
        node.setType(type);
        node.setName(name);
        node.setAbbr(abbr);
        return addImpl(node, LibraryItemType.NODE);
    }

    public LibraryItem addEngineClass(String id, String name) {
        EngineClass engineClass = factory.createEngineClass(id);
        engineClass.setName(name);
        return addImpl(engineClass, LibraryItemType.ENGINE_CLASS);
    }

    public LibraryItem addLineClass(String id, String name) {
        LineClass lineClass = factory.createLineClass(id);
        lineClass.setName(name);
        return addImpl(lineClass, LibraryItemType.LINE_CLASS);
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
        items.put(object.getId(), item);
        return item;
    }

    public ObjectWithId getObjectById(String id) {
        LibraryItem item = items.get(id);
        return item == null ? null : item.getObject();
    }

    public PartFactory getPartFactory() {
        return factory;
    }

    public Library build() {
        Library library = config.getLibraryFactory().createLibrary();
        for (LibraryItem item : items.values()) {
            library.itemMap.put(item.getType(), item);
        }
        return library;
    }
}
