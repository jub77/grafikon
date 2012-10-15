package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.changes.ChangesTracker;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.visitors.TrainDiagramTraversalVisitor;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Collection of all parts of graphical timetable.
 *
 * @author jub
 */
public class TrainDiagram implements AttributesHolder, ObjectWithId, Visitable, TrainDiagramAttributes {

    /** Id. */
    private final String id;
    /** Net. */
    private Net net;
    /** Predefined routes. */
    private final List<Route> routes;
    /** Trains. */
    private final List<Train> trains;
    /** Cycles. */
    private final Map<String, TrainsCycleType> cycles;
    /** List of images for trains timetable. */
    private final List<TimetableImage> images;
    /** Train types available. */
    private final List<TrainType> trainTypes;
    /** Attributes. */
    private Attributes attributes;
    /** Trains data. */
    private TrainsData trainsData;
    /** List of engine classes. */
    private final List<EngineClass> engineClasses;
    /** List of text items. */
    private final List<TextItem> textItems;
    /** List of output templates. */
    private final List<OutputTemplate> outputTemplates;
    /** Groups. */
    private List<Group> groups;
    /** Penalty table. */
    private PenaltyTable penaltyTable;

    private final GTListenerTrainDiagramImpl listener;
    private final ChangesTrackerImpl changesTracker;
    private final GTListenerSupport<TrainDiagramListener, TrainDiagramEvent> listenerSupport;
    private final GTListenerSupport<TrainDiagramListenerWithNested, TrainDiagramEvent> listenerSupportAll;
    private AttributesListener attributesListener;

    /**
     * Default constructor.
     */
    public TrainDiagram(String id, TrainsData data) {
        this.id = id;
        this.routes = new ArrayList<Route>();
        this.trains = new ArrayList<Train>();
        this.cycles = new HashMap<String, TrainsCycleType>();
        this.images = new LinkedList<TimetableImage>();
        this.engineClasses = new LinkedList<EngineClass>();
        this.textItems = new LinkedList<TextItem>();
        this.outputTemplates = new LinkedList<OutputTemplate>();
        this.penaltyTable = new PenaltyTable(IdGenerator.getInstance().getId());
        this.net = new Net(IdGenerator.getInstance().getId());
        this.trainTypes = new LinkedList<TrainType>();
        this.setAttributes(new Attributes());
        this.setTrainsData(data);
        this.listener = new GTListenerTrainDiagramImpl(this);
        this.listenerSupport = new GTListenerSupport<TrainDiagramListener, TrainDiagramEvent>(new GTEventSender<TrainDiagramListener, TrainDiagramEvent>() {

            @Override
            public void fireEvent(TrainDiagramListener listener, TrainDiagramEvent event) {
                listener.trainDiagramChanged(event);
            }
        });
        this.listenerSupportAll = new GTListenerSupport<TrainDiagramListenerWithNested, TrainDiagramEvent>(new GTEventSender<TrainDiagramListenerWithNested, TrainDiagramEvent>() {

            @Override
            public void fireEvent(TrainDiagramListenerWithNested listener, TrainDiagramEvent event) {
                if (event.getNestedEvent() != null)
                    listener.trainDiagramChangedNested(event);
                else
                    listener.trainDiagramChanged(event);
            }
        });
        this.net.addListener(listener);
        this.changesTracker = new ChangesTrackerImpl();
        this.addListenerWithNested(changesTracker);
    }

    /**
     * @return net
     */
    public Net getNet() {
        return net;
    }

    public void setNet(Net net) {
        if (this.net != null) {
            this.net.removeListener(listener);
        }
        this.net = net;
        this.net.addListener(listener);
    }

    public ChangesTracker getChangesTracker() {
        return changesTracker;
    }

    /**
     * @return predefined routes
     */
    public List<Route> getRoutes() {
        return Collections.unmodifiableList(this.routes);
    }

    public void addRoute(Route route) {
        this.routes.add(route);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.ROUTE_ADDED, route));
    }

    public void removeRoute(Route route) {
        this.routes.remove(route);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.ROUTE_REMOVED, route));
    }

    public Route getRouteById(String id) {
        return getById(id, routes);
    }

    /**
     * @return the trains
     */
    public List<Train> getTrains() {
        return Collections.unmodifiableList(this.trains);
    }

    public void addTrain(Train train) {
        train.addListener(listener);
        this.trains.add(train);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.TRAIN_ADDED, train));
        train.attach();
    }

    public void removeTrain(Train train) {
        train.detach();
        this.trains.remove(train);
        train.removeListener(listener);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.TRAIN_REMOVED, train));
    }

    public Train getTrainById(String id) {
        return getById(id, trains);
    }

    public Map<String, List<TrainsCycle>> getCyclesMap() {
        Map<String, List<TrainsCycle>> modMap = new HashMap<String, List<TrainsCycle>>();
        for (TrainsCycleType type : cycles.values()) {
            modMap.put(type.getName(), Collections.unmodifiableList(type.getCycles()));
        }
        return Collections.unmodifiableMap(modMap);
    }

    public Set<String> getCycleTypeNames() {
        return Collections.unmodifiableSet(cycles.keySet());
    }

    public Collection<TrainsCycleType> getCycleTypes() {
        return Collections.unmodifiableCollection(cycles.values());
    }

    public void addCyclesType(TrainsCycleType type) {
        if (!cycles.containsKey(type.getName())) {
            cycles.put(type.getName(), type);
            this.fireEvent(new TrainDiagramEvent(this, GTEventType.CYCLE_TYPE_ADDED, type));
        }
    }

    public TrainsCycleType getCyclesType(String typeName) {
        return cycles.get(typeName);
    }

    public void removeCyclesType(String typeName) {
        TrainsCycleType removed = cycles.get(typeName);
        if (removed != null) {
            // remove all cycles ...
            List<TrainsCycle> copy = new ArrayList<TrainsCycle>(removed.getCycles());
            for (TrainsCycle cycle : copy) {
                this.removeCycle(cycle);
            }
        }
        cycles.remove(typeName);
        if (removed != null) {
            this.fireEvent(new TrainDiagramEvent(this, GTEventType.CYCLE_TYPE_REMOVED, removed));
        }
    }

    public List<TrainsCycle> getCycles(String type) {
        return Collections.unmodifiableList(this.getCyclesIntern(type));
    }

    public void addCycle(TrainsCycle cycle) {
        cycle.addListener(listener);
        this.getCyclesIntern(cycle.getType().getName()).add(cycle);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.TRAINS_CYCLE_ADDED, cycle));
    }

    public void removeCycle(TrainsCycle cycle) {
        cycle.clear();
        this.getCyclesIntern(cycle.getType().getName()).remove(cycle);
        cycle.removeListener(listener);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.TRAINS_CYCLE_REMOVED, cycle));
    }

    public TrainsCycle getCycleById(String id) {
        for (TrainsCycleType type : cycles.values()) {
            TrainsCycle found = getById(id, type.getCycles());
            if (found != null)
                return found;
        }
        return null;
    }

    public TrainsCycle getCycleByIdAndType(String id, String type) {
        return getById(id, getCyclesIntern(type));
    }

    public Group getGroupById(String id) {
        return getById(id, groups);
    }

    private <T extends ObjectWithId> T getById(String id, Collection<T> items) {
        for (T item : items) {
            if (item.getId().equals(id))
                return item;
        }
        return null;
    }

    private List<TrainsCycle> getCyclesIntern(String type) {
        if (type == null)
            throw new IllegalArgumentException("Type cannot be null");
        if (!cycles.containsKey(type))
            throw new IllegalArgumentException("Unknown type: " + type);
        return cycles.get(type).getCycles();
    }

    public List<TimetableImage> getImages() {
        return Collections.unmodifiableList(images);
    }

    public void addImage(TimetableImage image) {
        this.images.add(image);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.IMAGE_ADDED, image));
    }

    public void addImage(TimetableImage image, int position) {
        this.images.add(position, image);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.IMAGE_ADDED, image));
    }

    public void removeImage(TimetableImage image) {
        this.images.remove(image);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.IMAGE_REMOVED, image));
    }

    public List<TrainType> getTrainTypes() {
        return Collections.unmodifiableList(trainTypes);
    }

    public void removeTrainType(TrainType type) {
        trainTypes.remove(type);
        type.removeListener(listener);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.TRAIN_TYPE_REMOVED, type));
    }

    public void addTrainType(TrainType type) {
        this.addTrainType(type, trainTypes.size());
    }

    public void addTrainType(TrainType type, int position) {
        type.addListener(listener);
        trainTypes.add(position, type);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.TRAIN_TYPE_ADDED, type));
    }

    public void moveTrainType(int from, int to) {
        TrainType moved = trainTypes.remove(from);
        if (moved != null) {
            trainTypes.add(to, moved);
            this.fireEvent(new TrainDiagramEvent(this, GTEventType.TRAIN_TYPE_MOVED, moved));
        }

    }

    public TrainType getTrainTypeById(String id) {
        return getById(id, trainTypes);
    }

    @Override
    public String toString() {
        return "Trains: " + trains.size() + ", Nodes: " + net.getNodes().size() + ", Lines: " + net.getLines().size();
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.set(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        if (this.attributes != null && attributesListener != null)
            this.attributes.removeListener(attributesListener);
        this.attributes = attributes;
        this.attributesListener = new AttributesListener() {

            @Override
            public void attributeChanged(Attributes attributes, AttributeChange change) {
                fireEvent(new TrainDiagramEvent(TrainDiagram.this, change));
            }
        };
        this.attributes.addListener(attributesListener);
    }

    public List<EngineClass> getEngineClasses() {
        return Collections.unmodifiableList(engineClasses);
    }

    public void addEngineClass(EngineClass engineClass) {
        this.addEngineClass(engineClass, engineClasses.size());
    }

    public void addEngineClass(EngineClass engineClass, int position) {
        engineClasses.add(position, engineClass);
        engineClass.addListener(listener);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.ENGINE_CLASS_ADDED, engineClass));
    }

    public void removeEngineClass(EngineClass engineClass) {
        engineClasses.remove(engineClass);
        engineClass.removeListener(listener);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.ENGINE_CLASS_REMOVED, engineClass));
    }

    public void moveEngineClass(int from, int to) {
        EngineClass eClass = engineClasses.remove(from);
        if (eClass != null) {
            engineClasses.add(to, eClass);
            this.fireEvent(new TrainDiagramEvent(this, GTEventType.ENGINE_CLASS_MOVED, eClass));
        }
    }

    public List<TextItem> getTextItems() {
        return Collections.unmodifiableList(textItems);
    }

    public List<OutputTemplate> getOutputTemplates() {
        return Collections.unmodifiableList(outputTemplates);
    }

    public void addTextItem(TextItem item) {
        this.addTextItem(item, textItems.size());
    }

    public void addOutputTemplate(OutputTemplate template) {
        this.addOutputTemplate(template, outputTemplates.size());
    }

    public List<Group> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    public void addGroup(Group group) {
        this.addGroup(group, groups.size());
    }

    public void addGroup(Group group, int position) {
        groups.add(position, group);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.GROUP_ADDED, group));
    }

    public void removeGroup(Group group) {
        groups.remove(group);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.GROUP_REMOVED, group));
    }

    public void addTextItem(TextItem item, int position) {
        item.addListener(listener);
        textItems.add(position, item);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.TEXT_ITEM_ADDED, item));
    }

    public void addOutputTemplate(OutputTemplate template, int position) {
        template.addListener(listener);
        outputTemplates.add(position, template);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.OUTPUT_TEMPLATE_ADDED, template));
    }

    public void removeTextItem(TextItem item) {
        textItems.remove(item);
        item.removeListener(listener);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.TEXT_ITEM_REMOVED, item));
    }

    public void removeOutputTemplate(OutputTemplate template) {
        outputTemplates.remove(template);
        template.removeListener(listener);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.OUTPUT_TEMPLATE_REMOVED, template));
    }

    public void moveTextItem(int from, int to) {
        TextItem moved = textItems.remove(from);
        if (moved != null) {
            textItems.add(to, moved);
            this.fireEvent(new TrainDiagramEvent(this, GTEventType.TEXT_ITEM_MOVED, moved));
        }
    }

    public void moveOutputTemplate(int from, int to) {
        OutputTemplate moved = outputTemplates.remove(from);
        if (moved != null) {
            outputTemplates.add(to, moved);
            this.fireEvent(new TrainDiagramEvent(this, GTEventType.OUTPUT_TEMPLATE_MOVED, moved));
        }
    }

    public EngineClass getEngineClassById(String id) {
        return getById(id, engineClasses);
    }

    public TextItem getTextItemById(String id) {
        return getById(id, textItems);
    }

    public OutputTemplate getOutputTemplateById(String id) {
        return getById(id, outputTemplates);
    }

    public TimetableImage getImageById(String id) {
        return getById(id, images);
    }

    public TrainsData getTrainsData() {
        return trainsData;
    }

    public void setTrainsData(TrainsData trainsData) {
        if (trainsData != null)
            trainsData.setDiagram(this);
        this.trainsData = trainsData;
    }

    public PenaltyTable getPenaltyTable() {
        return penaltyTable;
    }

    public void setPenaltyTable(PenaltyTable penaltyTable) {
        this.penaltyTable = penaltyTable;
    }

    @Override
    public String getId() {
        return id;
    }

    public void addListener(TrainDiagramListener listener) {
        listenerSupport.addListener(listener);
    }

    public void removeListener(TrainDiagramListener listener) {
        listenerSupport.removeListener(listener);
    }

    public void addListenerWithNested(TrainDiagramListenerWithNested listener) {
        listenerSupportAll.addListener(listener);
    }

    public void removeListenerWithNested(TrainDiagramListenerWithNested listener) {
        listenerSupportAll.removeListener(listener);
    }

    protected void fireNestedEvent(GTEvent<?> nestedEvent) {
        TrainDiagramEvent event = new TrainDiagramEvent(this, nestedEvent);
        this.fireEvent(event);
    }

    protected void fireEvent(TrainDiagramEvent e) {
        listenerSupportAll.fireEvent(e);
        if (e.getNestedEvent() == null) {
            listenerSupport.fireEvent(e);
        }
    }

    // -------------------------- creational methods ---------------------------

    /**
     * creates new train.
     *
     * @param id train id
     * @return a new train
     */
    public Train createTrain(String id) {
        return new Train(id, this);
    }

    /**
     * create new line.
     *
     * @param id line id
     * @param length length
     * @param from from node
     * @param to to node
     * @param topSpeed top speed
     * @return a new line
     */
    public Line createLine(String id, int length, Node from, Node to, int topSpeed) {
        return new Line(id, this, length, from, to, topSpeed);
    }

    /**
     * create new node.
     *
     * @param id id
     * @param type node type
     * @param name name
     * @param abbr abbreviation
     * @return a new node
     */
    public Node createNode(String id, NodeType type, String name, String abbr) {
        return new Node(id, this, type, name, abbr);
    }

    /**
     * creates new train type.
     *
     * @param id id
     * @return a new train type
     */
    public TrainType createTrainType(String id) {
        return new TrainType(id, this);
    }

    /**
     * creates new image.
     *
     * @param id id
     * @param filename filename
     * @param width width of the image in pixels
     * @param height height of the image in pixels
     * @return a new image
     */
    public TimetableImage createImage(String id, String filename, int width, int height) {
        return new TimetableImage(id, filename, width, height);
    }

    /**
     * creates group.
     *
     * @param id id
     * @return new group
     */
    public Group createGroup(String id) {
        return new Group(id);
    }

    /**
     * accepts visitor.
     *
     * @param visitor visitor
     */
    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * accepts traversal visitor.
     *
     * @param visitor visitor
     */
    public void accept(TrainDiagramTraversalVisitor visitor) {
        visitor.visit(this);
        net.accept(visitor);
        for (Route route : routes) {
            route.accept(visitor);
        }
        for (TrainType type : trainTypes) {
            type.accept(visitor);
        }
        for (EngineClass clazz : engineClasses) {
            clazz.accept(visitor);
        }
        for (TextItem item : textItems) {
            item.accept(visitor);
        }
        for (OutputTemplate template : outputTemplates) {
            template.accept(visitor);
        }
        for(Train train : trains) {
            train.accept(visitor);
        }
        for (TrainsCycleType type : cycles.values()) {
            type.accept(visitor);
        }
        for (TimetableImage image : images) {
            image.accept(visitor);
        }
        for (Group group : groups) {
            group.accept(visitor);
        }
        visitor.visitAfter(this);
    }

    public ObjectWithId getObjectById(String id) {
        if (getId().equals(id))
            return this;
        ObjectWithId object = getTrainById(id);
        if (object != null)
            return object;
        object = getTrainTypeById(id);
        if (object != null)
            return object;
        object = getNet().getObjectById(id);
        if (object != null)
            return object;
        object = getCycleById(id);
        if (object != null)
            return object;
        object = getEngineClassById(id);
        if (object != null)
            return object;
        object = getRouteById(id);
        if (object != null)
            return object;
        object = getTextItemById(id);
        if (object != null)
            return object;
        object = getOutputTemplateById(id);
        if (object != null)
            return object;
        object = getImageById(id);
        if (object != null)
            return object;
        object = getGroupById(id);
        return object;
    }
}
