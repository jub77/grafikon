package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.changes.ChangesTracker;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.validators.*;
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
    /** Freight net. */
    private FreightNet freightNet;
    /** Predefined routes. */
    private final List<Route> routes;
    /** Trains. */
    private final List<Train> trains;
    /** Cycles. */
    private final Set<TrainsCycleType> cycles;
    /** List of images for trains timetable. */
    private final List<TimetableImage> images;
    /** Train types available. */
    private final List<TrainType> trainTypes;
    /** Attributes. */
    private final AttributesWrapper attributesWrapper;
    /** Trains data. */
    private TrainsData trainsData;
    /** List of engine classes. */
    private final List<EngineClass> engineClasses;
    /** List of text items. */
    private final List<TextItem> textItems;
    /** List of output templates. */
    private final ItemList<OutputTemplate> outputTemplates;
    /** Groups. */
    private final ItemList<Group> groups;
    /** Companies */
    private final ItemList<Company> companies;
    /** Penalty table. */
    private PenaltyTable penaltyTable;
    /** Localization. */
    private final Localization localization;

    private final List<TrainDiagramValidator> validators;
    private final GTListenerTrainDiagramImpl listener;
    private final ChangesTrackerImpl changesTracker;
    private final GTListenerSupport<TrainDiagramListener, TrainDiagramEvent> listenerSupport;
    private final GTListenerSupport<AllEventListener, GTEvent<?>> listenerSupportAll;
    private TimeConverter timeConverter;

    /**
     * Default constructor.
     */
    public TrainDiagram(String id, TrainsData data) {
        this.id = id;
        this.routes = new ArrayList<Route>();
        this.trains = new ArrayList<Train>();
        this.cycles = new HashSet<TrainsCycleType>();
        this.images = new LinkedList<TimetableImage>();
        this.engineClasses = new LinkedList<EngineClass>();
        this.textItems = new LinkedList<TextItem>();
        this.outputTemplates = new ItemListTrainDiagramEvent<OutputTemplate>(GTEventType.OUTPUT_TEMPLATE_ADDED, GTEventType.OUTPUT_TEMPLATE_REMOVED, GTEventType.OUTPUT_TEMPLATE_MOVED);
        this.groups = new ItemListTrainDiagramEvent<Group>(GTEventType.GROUP_ADDED, GTEventType.GROUP_REMOVED);
        this.companies = new ItemListTrainDiagramEvent<Company>(GTEventType.COMPANY_ADDED, GTEventType.COMPANY_REMOVED);
        this.penaltyTable = new PenaltyTable(IdGenerator.getInstance().getId());
        this.localization = new Localization();
        this.net = new Net(IdGenerator.getInstance().getId(), this);
        this.trainTypes = new LinkedList<TrainType>();
        this.attributesWrapper = new AttributesWrapper(
                (attrs, change) -> fireEvent(new TrainDiagramEvent(TrainDiagram.this, change)));
        this.setTrainsData(data);
        this.listener = new GTListenerTrainDiagramImpl(this);
        this.listenerSupport = new GTListenerSupport<TrainDiagramListener, TrainDiagramEvent>(
                (listener, event) -> listener.trainDiagramChanged(event));
        this.listenerSupportAll = new GTListenerSupport<AllEventListener, GTEvent<?>>(
                (listener, event) -> listener.changed(event));
        this.net.addAllEventListener(listener);
        this.changesTracker = new ChangesTrackerImpl();
        this.addAllEventListener(changesTracker);
        this.freightNet = new FreightNet(IdGenerator.getInstance().getId());
        this.freightNet.addListener(listener);
        this.validators = new ArrayList<TrainDiagramValidator>();
        this.validators.add(new TrainNamesValidator(this));
        this.validators.add(new TrainIntervalsValidator());
        this.validators.add(new LineClassRemoveValidator(this));
        this.validators.add(new GroupRemoveValidator(this));
        this.validators.add(new CompanyRemoveValidator(this));
        this.validators.add(new EngineClassRemoveValidator(this));
        this.validators.add(new LineValidator());
        this.validators.add(new TrainsCycleValidator());
        this.validators.add(new TrainTypeValidator(this));
        this.validators.add(new FreightNetValidator(this));
        this.validators.add(new RegionRemoveValidator(this));
        this.validators.add(new NodeValidator(this));
    }

    /**
     * @return net
     */
    public Net getNet() {
        return net;
    }

    public void setNet(Net net) {
        if (this.net != null) {
            this.net.removeAllEventListener(listener);
        }
        this.net = net;
        this.net.addAllEventListener(listener);
    }

    public void setFreightNet(FreightNet freightNet) {
        if (this.freightNet != null) {
            this.freightNet.removeListener(listener);
        }
        this.freightNet = freightNet;
        this.freightNet.addListener(listener);
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
        train.attach();
        this.fireEvent(new TrainDiagramEvent(this, GTEventType.TRAIN_ADDED, train));
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

    public Train getTrainByNumber(String number) {
        for (Train train : trains) {
            if (train.getNumber().equals(number)) {
                return train;
            }
        }
        return null;
    }

    public Collection<TrainsCycle> getCycles() {
        List<TrainsCycle> result = new ArrayList<TrainsCycle>();
        for (TrainsCycleType type : cycles) {
            result.addAll(type.getCycles());
        }
        return result;
    }

    public Collection<TrainsCycleType> getCycleTypes() {
        return Collections.unmodifiableCollection(cycles);
    }

    public List<TrainsCycle> getEngineCycles() {
        return this.getCycles(this.getEngineCycleType());
    }

    public TrainsCycleType getEngineCycleType() {
        return this.getCycleTypeByNameImpl(TrainsCycleType.ENGINE_CYCLE);
    }

    public TrainsCycleType getTrainUnitCycleType() {
        return this.getCycleTypeByNameImpl(TrainsCycleType.TRAIN_UNIT_CYCLE);
    }

    public List<TrainsCycle> getTrainUnitCycles() {
        return this.getCycles(this.getTrainUnitCycleType());
    }

    public TrainsCycleType getDriverCycleType() {
        return this.getCycleTypeByNameImpl(TrainsCycleType.DRIVER_CYCLE);
    }

    public TrainsCycleType getDefaultCycleType(String typeName) {
        return this.getCycleTypeByNameImpl(typeName);
    }

    public List<TrainsCycle> getDriverCycles() {
        return this.getCycles(this.getDriverCycleType());
    }

    private TrainsCycleType getCycleTypeByNameImpl(String typeName) {
        for (TrainsCycleType type : cycles) {
            if (typeName.equals(type.getName())) {
                return type;
            }
        }
        return null;
    }

    public void addCyclesType(TrainsCycleType type) {
        if (!cycles.contains(type)) {
            type.addListener(listener);
            cycles.add(type);
            this.fireEvent(new TrainDiagramEvent(this, GTEventType.CYCLE_TYPE_ADDED, type));
        }
    }

    public void removeCyclesType(TrainsCycleType type) {
        if (cycles.contains(type)) {
            // remove all cycles ...
            List<TrainsCycle> copy = new ArrayList<TrainsCycle>(type.getCycles());
            for (TrainsCycle cycle : copy) {
                this.removeCycle(cycle);
            }
            cycles.remove(type);
            type.removeListener(listener);
            this.fireEvent(new TrainDiagramEvent(this, GTEventType.CYCLE_TYPE_REMOVED, type));
        }
    }

    public List<TrainsCycle> getCycles(TrainsCycleType type) {
        return Collections.unmodifiableList(this.getCyclesIntern(type));
    }

    public List<TrainsCycle> getCycles(String typeName) {
        TrainsCycleType type = this.getCycleType(typeName);
        return type != null ? this.getCycles(type) : Collections.<TrainsCycle>emptyList();
    }

    public TrainsCycleType getCycleType(String typeName) {
        if (!TrainsCycleType.isDefaultType(typeName)) {
            throw new IllegalArgumentException("Only default types allowed");
        }
        return this.getCycleTypeByNameImpl(typeName);
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
        for (TrainsCycleType type : cycles) {
            TrainsCycle found = getById(id, type.getCycles());
            if (found != null)
                return found;
        }
        return null;
    }

    public TrainsCycleType getCycleTypeById(String id) {
        return this.getById(id, cycles);
    }

    public TrainsCycle getCycleByIdAndType(String id, TrainsCycleType type) {
        return getById(id, getCyclesIntern(type));
    }

    public Group getGroupById(String id) {
        return getById(id, groups);
    }

    public Company getCompanyById(String id) {
        return getById(id, companies);
    }

    public FreightNet getFreightNet() {
        return freightNet;
    }

    private <T extends ObjectWithId> T getById(String id, Iterable<T> items) {
        for (T item : items) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    private List<TrainsCycle> getCyclesIntern(TrainsCycleType type) {
        if (type == null)
            throw new IllegalArgumentException("Type cannot be null");
        if (!cycles.contains(type))
            throw new IllegalArgumentException("Unknown type: " + type);
        return type.getCycles();
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
    public <T> T getAttribute(String key, Class<T> clazz) {
        return attributesWrapper.getAttributes().get(key, clazz);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributesWrapper.getAttributes().set(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributesWrapper.getAttributes().remove(key);
    }

    @Override
	public Attributes getAttributes() {
        return attributesWrapper.getAttributes();
    }

    @Override
	public void setAttributes(Attributes attributes) {
        attributesWrapper.setAttributes(attributes);
    }

    public double getTimeScale() {
        return this.getAttribute(TrainDiagram.ATTR_TIME_SCALE, Double.class);
    }

    public Scale getScale() {
        return this.getAttribute(TrainDiagram.ATTR_SCALE, Scale.class);
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

    public ItemList<OutputTemplate> getOutputTemplates() {
        return outputTemplates;
    }

    public void addTextItem(TextItem item) {
        this.addTextItem(item, textItems.size());
    }

    public ItemList<Group> getGroups() {
        return groups;
    }

    public ItemList<Company> getCompanies() {
        return companies;
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

    public Localization getLocalization() {
        return localization;
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

    public void addAllEventListener(AllEventListener listener) {
        listenerSupportAll.addListener(listener);
    }

    public void removeAllEventListener(AllEventListener listener) {
        listenerSupportAll.removeListener(listener);
    }

    protected void fireNestedEvent(GTEvent<?> event) {
        processValidators(event);
        listenerSupportAll.fireEvent(event);
    }

    protected void fireEvent(TrainDiagramEvent event) {
        processValidators(event);
        listenerSupport.fireEvent(event);
        listenerSupportAll.fireEvent(event);
    }

    private void processValidators(GTEvent<?> event) {
        for (TrainDiagramValidator validator : validators) {
            validator.validate(event);
        }
    }

    /**
     * @return time converter
     */
    public TimeConverter getTimeConverter() {
        if (timeConverter == null) {
            String type = this.getAttributes().get(ATTR_TIME_CONVERTER, String.class);
            TimeConverter.Rounding rounding = TimeConverter.Rounding.MINUTE;
            if (type != null) {
                rounding = TimeConverter.Rounding.fromString(type);
            }
            timeConverter = new TimeConverter(rounding);
        }
        return timeConverter;
    }

    /**
     * @param timeConverter time converter to set
     */
    public void setTimeConverter(TimeConverter timeConverter) {
        if (timeConverter == null)
            throw new IllegalArgumentException("Converter cannot be null.");
        this.setAttribute(ATTR_TIME_CONVERTER, timeConverter.getRounding().getKey());
        this.timeConverter = timeConverter;
    }

    // -------------------------- creational methods ---------------------------

    /**
     * @return new (not used) id
     */
    public String createId() {
        return IdGenerator.getInstance().getId();
    }

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
    public Line createLine(String id, int length, Node from, Node to, Integer topSpeed) {
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
        return new Group(id, this);
    }

    /**
     * Creates region.
     *
     * @param id id
     * @param name name of region
     * @return new region
     */
    public Region createRegion(String id, String name) {
        return new Region(id, name, this);
    }

    /**
     * Creates company.
     *
     * @param id id
     * @return new company
     */
    public Company createCompany(String id) {
        return new Company(id, this);
    }

    /**
     * Creates new cycle type.
     *
     * @param id id
     * @return new cycle type
     */
    public TrainsCycleType createCycleType(String id) {
        return new TrainsCycleType(id, this);
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
        for (TrainsCycleType type : cycles) {
            type.accept(visitor);
        }
        for (TimetableImage image : images) {
            image.accept(visitor);
        }
        for (Group group : groups) {
            group.accept(visitor);
        }
        for (Company company : companies) {
            company.accept(visitor);
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
        object = getCycleTypeById(id);
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
        if (object !=null)
            return object;
        object = getCompanyById(id);
        return object;
    }

    private class ItemListTrainDiagramEvent<T> extends ItemList<T> {

        protected ItemListTrainDiagramEvent(GTEventType add, GTEventType remove) {
            super(add, remove);
        }

        public ItemListTrainDiagramEvent(GTEventType add, GTEventType remove, GTEventType move) {
            super(add, remove, move);
        }

        @Override
        protected void fireEvent(net.parostroj.timetable.model.ItemList.Type type, GTEventType eventType, T item,
                int newIndex, int oldIndex) {
            TrainDiagramEvent event = new TrainDiagramEvent(TrainDiagram.this, eventType, item);
            TrainDiagram.this.fireEvent(event);
        }
    }
}
