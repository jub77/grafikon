package net.parostroj.timetable.model;

import java.util.*;
import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.GTEvent;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.model.events.TrainDiagramListener;
import net.parostroj.timetable.model.events.TrainDiagramListenerWithNested;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.visitors.TrainDiagramTraversalVisitor;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;

/**
 * Collection of all parts of graphical timetable.
 *
 * @author jub
 */
public class TrainDiagram implements AttributesHolder, ObjectWithId {

    /** Id. */
    private final String id;
    /** Net. */
    private Net net;
    /** Predefined routes. */
    private List<Route> routes;
    /** Trains. */
    private List<Train> trains;
    /** Cycles. */
    private Map<TrainsCycleType, List<TrainsCycle>> cycles;
    /** List of images for trains timetable. */
    private List<TimetableImage> images;
    /** Train types available. */
    private List<TrainType> trainTypes;
    /** Attributes. */
    private Attributes attributes;
    /** Trains data. */
    private TrainsData trainsData;
    /** List of engine classes. */
    private List<EngineClass> engineClasses;
    /** List of text items. */
    private List<TextItem> textItems;
    /** Penalty table. */
    private PenaltyTable penaltyTable;

    private GTListenerTrainDiagramImpl listener;
    private GTListenerSupport<TrainDiagramListener, TrainDiagramEvent> listenerSupport;
    private GTListenerSupport<TrainDiagramListenerWithNested, TrainDiagramEvent> listenerSupportAll;

    /**
     * Default constructor.
     */
    public TrainDiagram(String id, TrainsData data) {
        this.id = id;
        this.routes = new ArrayList<Route>();
        this.trains = new ArrayList<Train>();
        this.cycles = new EnumMap<TrainsCycleType, List<TrainsCycle>>(TrainsCycleType.class);
        this.images = new LinkedList<TimetableImage>();
        this.engineClasses = new LinkedList<EngineClass>();
        this.textItems = new LinkedList<TextItem>();
        this.penaltyTable = new PenaltyTable(IdGenerator.getInstance().getId());
        this.net = new Net(IdGenerator.getInstance().getId());
        this.trainTypes = new LinkedList<TrainType>();
        this.attributes = new Attributes();
        this.trainsData = data;
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
        for (Route route : routes) {
            if (route.getId().equals(id)) {
                return route;
            }
        }
        return null;
    }

    /**
     * @return the trains
     */
    public List<Train> getTrains() {
        return Collections.unmodifiableList(this.trains);
    }

    public void addTrain(Train train) {
        train.addListener(listener);
        train.attach();
        this.trains.add(train);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.TRAIN_ADDED, train));
    }

    public void removeTrain(Train train) {
        train.detach();
        this.trains.remove(train);
        train.removeListener(listener);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.TRAIN_REMOVED, train));
    }

    public Train getTrainById(String id) {
        for (Train train : trains) {
            if (train.getId().equals(id)) {
                return train;
            }
        }
        return null;
    }

    public Map<TrainsCycleType, List<TrainsCycle>> getCyclesMap() {
        EnumMap<TrainsCycleType, List<TrainsCycle>> modMap = new EnumMap<TrainsCycleType, List<TrainsCycle>>(TrainsCycleType.class);
        for (Map.Entry<TrainsCycleType, List<TrainsCycle>> entry : cycles.entrySet()) {
            modMap.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(cycles);
    }

    public List<TrainsCycle> getCycles(TrainsCycleType type) {
        return Collections.unmodifiableList(this.getCyclesIntern(type));
    }

    public void addCycle(TrainsCycle cycle) {
        cycle.addListener(listener);
        this.getCyclesIntern(cycle.getType()).add(cycle);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.TRAINS_CYCLE_ADDED, cycle));
    }

    public void removeCycle(TrainsCycle cycle) {
        cycle.clear();
        this.getCyclesIntern(cycle.getType()).remove(cycle);
        cycle.removeListener(listener);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.TRAINS_CYCLE_REMOVED, cycle));
    }

    public TrainsCycle getCycleById(String id) {
        for (Map.Entry<TrainsCycleType, List<TrainsCycle>> entry : cycles.entrySet()) {
            for (TrainsCycle cycle : entry.getValue()) {
                if (cycle.getId().equals(id))
                    return cycle;
            }
        }
        return null;
    }

    public TrainsCycle getCycleByIdAndType(String id, TrainsCycleType type) {
        for (TrainsCycle cycle : getCyclesIntern(type)) {
            if (cycle.getId().equals(id))
                return cycle;
        }
        return null;
    }

    private List<TrainsCycle> getCyclesIntern(TrainsCycleType type) {
        if (!cycles.containsKey(type)) {
            cycles.put(type, new ArrayList<TrainsCycle>());
        }
        return cycles.get(type);
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

    public void setTrainType(TrainType type, int position) {
        trainTypes.set(position, type);
    }

    public TrainType getTrainTypeById(String id) {
        for (TrainType type : trainTypes) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        return null;
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
        Object oldValue = attributes.get(key);
        attributes.put(key, value);
        this.fireEvent(new TrainDiagramEvent(this, new AttributeChange(key, oldValue, value)));
    }

    @Override
    public Object removeAttribute(String key) {
        Object o = attributes.remove(key);
        this.fireEvent(new TrainDiagramEvent(this, new AttributeChange(key, o, null)));
        return o;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public List<EngineClass> getEngineClasses() {
        return Collections.unmodifiableList(engineClasses);
    }

    public void addEngineClass(EngineClass engineClass) {
        engineClasses.add(engineClass);
    }

    public void addEngineClass(EngineClass engineClass, int position) {
        engineClasses.add(position, engineClass);
    }

    public void removeEngineClass(EngineClass engineClass) {
        engineClasses.remove(engineClass);
    }

    public List<TextItem> getTextItems() {
        return Collections.unmodifiableList(textItems);
    }

    public void addTextItem(TextItem item) {
        this.addTextItem(item, textItems.size());
    }

    public void addTextItem(TextItem item, int position) {
        item.addListener(listener);
        textItems.add(position, item);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.TEXT_ITEM_ADDED, item));
    }

    public void removeTextItem(TextItem item) {
        textItems.remove(item);
        item.removeListener(listener);
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.TEXT_ITEM_REMOVED, item));
    }

    public void moveTextItem(int from, int to) {
        TextItem moved = textItems.remove(from);
        if (moved != null) {
            textItems.add(to, moved);
            this.fireEvent(new TrainDiagramEvent(this, GTEventType.TEXT_ITEM_MOVED, moved));
        }
    }

    public EngineClass getEngineClassById(String id) {
        for (EngineClass ec : engineClasses) {
            if (ec.getId().equals(id))
                return ec;
        }
        return null;
    }

    public TextItem getTextItemById(String id) {
        for (TextItem item : textItems) {
            if (item.getId().equals(id))
                return item;
        }
        return null;
    }

    public TrainsData getTrainsData() {
        return trainsData;
    }

    public void setTrainsData(TrainsData trainsData) {
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
     * @param id ide
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
     * accepts visitor.
     *
     * @param visitor visitor
     */
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
        for(Train train : trains) {
            train.accept(visitor);
        }
        for (List<TrainsCycle> list : cycles.values()) {
            for (TrainsCycle cycle : list) {
                cycle.accept(visitor);
            }
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
        return object;
    }
}
