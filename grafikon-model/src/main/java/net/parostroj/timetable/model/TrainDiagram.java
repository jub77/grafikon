package net.parostroj.timetable.model;

import java.util.*;

import com.google.common.collect.Iterables;

import net.parostroj.timetable.filters.ModelPredicates;
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
public class TrainDiagram implements AttributesHolder, ObjectWithId, Visitable, TrainDiagramAttributes, Observable {

    /** Id. */
    private final String id;
    /** Net. */
    private Net net;
    /** Freight net. */
    private FreightNet freightNet;
    /** Predefined routes. */
    private final ItemWithIdSet<Route> routes;
    /** Trains. */
    private final List<Train> trains;
    /** Cycles. */
    private final Set<TrainsCycleType> cycles;
    /** List of images for trains timetable. */
    private final ItemWithIdSet<TimetableImage> images;
    /** Train types available. */
    private final ItemWithIdList<TrainType> trainTypes;
    /** Attributes. */
    private final Attributes attributes;
    /** Trains data. */
    private final TrainsData trainsData;
    /** List of engine classes. */
    private final ItemWithIdList<EngineClass> engineClasses;
    /** List of text items. */
    private final ItemWithIdList<TextItem> textItems;
    /** List of output templates. */
    private final ItemWithIdList<OutputTemplate> outputTemplates;
    /** Groups. */
    private final ItemWithIdSet<Group> groups;
    /** Companies */
    private final ItemWithIdSet<Company> companies;
    /** Penalty table. */
    private PenaltyTable penaltyTable;
    /** Localization. */
    private final Localization localization;

    private final TrainDiagramPartFactory partFactory;

    private final List<TrainDiagramValidator> validators;
    private final Listener listener;
    private final ChangesTrackerImpl changesTracker;
    private final ListenerSupport listenerSupport;
    private final ListenerSupport listenerSupportAll;
    private TimeConverter timeConverter;

    private final List<ItemWithIdIterable<? extends ObjectWithId>> itemLists;

    /**
     * Default constructor.
     */
    public TrainDiagram(String id) {
        this.id = id;
        this.itemLists = new LinkedList<>();
        this.listener = event -> this.fireNestedEvent(event);
        this.routes = new ItemWithIdSetImpl<Route>(
                (type, item) -> fireCollectionEvent(type, item, null, null));
        this.trains = new ArrayList<Train>();
        this.cycles = new HashSet<TrainsCycleType>();
        this.images = new ItemWithIdSetImpl<TimetableImage>(
                (type, item) -> fireCollectionEvent(type, item, null, null));
        this.engineClasses = new ItemWithIdListImpl<EngineClass>(
                (type, item, newIndex, oldIndex) -> fireCollectionEventObservable(type, item, newIndex, oldIndex));
        this.textItems = new ItemWithIdListImpl<TextItem>(
                (type, item, newIndex, oldIndex) -> fireCollectionEventObservable(type, item, newIndex, oldIndex));
        this.outputTemplates = new ItemWithIdListImpl<OutputTemplate>(
                (type, item, newIndex, oldIndex) -> fireCollectionEventObservable(type, item, newIndex, oldIndex));
        this.groups = new ItemWithIdSetImpl<Group>(
                (type, item) -> fireCollectionEventListObject(type, item, null, null));
        this.companies = new ItemWithIdSetImpl<Company>(
                (type, item) -> fireCollectionEventListObject(type, item, null, null));
        this.penaltyTable = new PenaltyTable(IdGenerator.getInstance().getId());
        this.localization = new Localization();
        this.net = new Net(IdGenerator.getInstance().getId(), this);
        this.trainTypes = new ItemWithIdListImpl<TrainType>(
                (type, item, newIndex, oldIndex) -> fireCollectionEventObservable(type, item, newIndex, oldIndex));
        this.attributes = new Attributes(
                (attrs, change) -> fireEvent(new Event(TrainDiagram.this, change)));
        this.trainsData = new TrainsData(this);
        this.listenerSupport = new ListenerSupport();
        this.listenerSupportAll = new ListenerSupport();
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
        Collections.addAll(itemLists, routes, images, engineClasses, textItems, outputTemplates, groups, companies, trainTypes);
        this.partFactory = new TrainDiagramPartFactory(this);
    }

    /**
     * @return net
     */
    public Net getNet() {
        return net;
    }

    public void setNet(Net net) {
        if (net != this.net) {
            if (this.net != null) {
                this.net.removeAllEventListener(listener);
            }
            Net oldNet = this.net;
            this.net = net;
            this.net.addAllEventListener(listener);
            this.fireEvent(new Event(this, new AttributeChange(TrainDiagram.ATTR_NET, oldNet, net)));
        }
    }

    public void setFreightNet(FreightNet freightNet) {
        if (freightNet != this.freightNet) {
            if (this.freightNet != null) {
                this.freightNet.removeListener(listener);
            }
            FreightNet oldFreightNet = this.freightNet;
            this.freightNet = freightNet;
            this.freightNet.addListener(listener);
            this.fireEvent(new Event(this, new AttributeChange(TrainDiagram.ATTR_FREIGHT_NET, oldFreightNet, freightNet)));
        }
    }

    public ChangesTracker getChangesTracker() {
        return changesTracker;
    }

    /**
     * @return predefined routes
     */
    public ItemWithIdSet<Route> getRoutes() {
        return routes;
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
        this.fireEvent(new Event(this, Event.Type.ADDED, train));
    }

    public void removeTrain(Train train) {
        train.detach();
        this.trains.remove(train);
        train.removeListener(listener);
        this.fireEvent(new Event(this, Event.Type.REMOVED, train));
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
            this.fireEvent(new Event(this, Event.Type.ADDED, type));
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
            this.fireEvent(new Event(this, Event.Type.REMOVED, type));
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
        this.fireEvent(new Event(this, Event.Type.ADDED, cycle));
    }

    public void removeCycle(TrainsCycle cycle) {
        cycle.clear();
        this.getCyclesIntern(cycle.getType()).remove(cycle);
        cycle.removeListener(listener);
        this.fireEvent(new Event(this, Event.Type.REMOVED, cycle));
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

    public FreightNet getFreightNet() {
        return freightNet;
    }

    private <T extends ObjectWithId> T getById(String id, Iterable<T> items) {
        return Iterables.tryFind(items, ModelPredicates.matchId(id)).orNull();
    }

    private List<TrainsCycle> getCyclesIntern(TrainsCycleType type) {
        if (type == null)
            throw new IllegalArgumentException("Type cannot be null");
        if (!cycles.contains(type))
            throw new IllegalArgumentException("Unknown type: " + type);
        return type.getCycles();
    }

    public ItemWithIdSet<TimetableImage> getImages() {
        return images;
    }

    public ItemWithIdList<TrainType> getTrainTypes() {
        return trainTypes;
    }

    @Override
    public String toString() {
        return "Trains: " + trains.size() + ", Nodes: " + net.getNodes().size() + ", Lines: " + net.getLines().size();
    }

    @Override
    public <T> T getAttribute(String key, Class<T> clazz) {
        return attributes.get(key, clazz);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.set(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    @Override
	public Attributes getAttributes() {
        return attributes;
    }

    public double getTimeScale() {
        return this.getAttribute(TrainDiagram.ATTR_TIME_SCALE, Double.class);
    }

    public Scale getScale() {
        return this.getAttribute(TrainDiagram.ATTR_SCALE, Scale.class);
    }

    public ItemWithIdList<EngineClass> getEngineClasses() {
        return engineClasses;
    }

    public ItemWithIdList<TextItem> getTextItems() {
        return textItems;
    }

    public ItemWithIdList<OutputTemplate> getOutputTemplates() {
        return outputTemplates;
    }

    public ItemWithIdSet<Group> getGroups() {
        return groups;
    }

    public ItemWithIdSet<Company> getCompanies() {
        return companies;
    }

    public TrainsData getTrainsData() {
        return trainsData;
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

    @Override
    public void addListener(Listener listener) {
        listenerSupport.addListener(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listenerSupport.removeListener(listener);
    }

    public void addAllEventListener(Listener listener) {
        listenerSupportAll.addListener(listener);
    }

    public void removeAllEventListener(Listener listener) {
        listenerSupportAll.removeListener(listener);
    }

    protected void fireNestedEvent(Event event) {
        processValidators(event);
        listenerSupportAll.fireEvent(event);
    }

    protected void fireEvent(Event event) {
        processValidators(event);
        listenerSupport.fireEvent(event);
        listenerSupportAll.fireEvent(event);
    }

    private void processValidators(Event event) {
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

    public TrainDiagramPartFactory getPartFactory() {
        return partFactory;
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
        if (getId().equals(id)) {
            return this;
        }
        ObjectWithId object;
        for (ItemWithIdIterable<? extends ObjectWithId> itemList : itemLists) {
            object = itemList.getById(id);
            if (object != null) {
                return object;
            }
        }
        object = getTrainById(id);
        if (object != null) {
            return object;
        }
        object = getNet().getObjectById(id);
        if (object != null) {
            return object;
        }
        object = getCycleById(id);
        if (object != null) {
            return object;
        }
        object = getCycleTypeById(id);
        return object;
    }

    private void fireCollectionEvent(Event.Type type, Object item, Integer newIndex, Integer oldIndex) {
        Event event = new Event(TrainDiagram.this, type, item, ListData.createData(oldIndex, newIndex));
        TrainDiagram.this.fireEvent(event);
    }

    private void fireCollectionEventListObject(Event.Type type, ItemListObject item, Integer newIndex, Integer oldIndex) {
        fireCollectionEvent(type, item, newIndex, oldIndex);
        switch (type) {
            case ADDED:
                item.added();
                break;
            case REMOVED:
                item.removed();
                break;
            default: // nothing
                break;
        }
    }

    private void fireCollectionEventObservable(Event.Type type, Observable item, Integer newIndex, Integer oldIndex) {
        fireCollectionEvent(type, item, newIndex, oldIndex);
        switch (type) {
            case ADDED:
                item.addListener(listener);
                break;
            case REMOVED:
                item.removeListener(listener);
                break;
            default: // nothing
                break;
        }
    }
}
