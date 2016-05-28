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
    private final ItemWithIdSet<Train> trains;
    /** Cycle types. */
    private final ItemWithIdSet<TrainsCycleType> cycleTypes;
    /** List of images for trains timetable. */
    private final ItemWithIdSet<TimetableImage> images;
    /** Train types available. */
    private final ItemWithIdList<TrainType> trainTypes;
    /** Attributes. */
    private final Attributes attributes;
    /** Trains data. */
    private final TrainsData trainsData;
    /** List of engine classes. */
    private final ItemWithIdSet<EngineClass> engineClasses;
    /** List of text items. */
    private final ItemWithIdList<TextItem> textItems;
    /** List of output templates. */
    private final ItemWithIdList<OutputTemplate> outputTemplates;
    /** Groups. */
    private final ItemWithIdSet<Group> groups;
    /** Companies */
    private final ItemWithIdSet<Company> companies;
    /** Penalty table. */
    private final ItemWithIdList<TrainTypeCategory> trainTypeCategories;

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
        this.partFactory = new TrainDiagramPartFactory(this);
        this.itemLists = new LinkedList<>();
        this.listener = event -> this.fireNestedEvent(event);
        this.routes = new ItemWithIdSetImpl<Route>(
                (type, item) -> fireCollectionEvent(type, item, null, null));
        this.trains = new ItemWithIdSetImpl<Train>((type, item) -> {
            if (type == Event.Type.ADDED) {
                item.attach();
            } else {
                item.detach();
            }
            this.fireCollectionEventObservable(type, item, null, null);
        });
        this.cycleTypes = new ItemWithIdSetImpl<TrainsCycleType>(
                (type, item) -> fireCollectionEventObservable(type, item, null, null));
        this.images = new ItemWithIdSetImpl<TimetableImage>(
                (type, item) -> fireCollectionEvent(type, item, null, null));
        this.engineClasses = new ItemWithIdSetImpl<EngineClass>(
                (type, item) -> fireCollectionEventObservable(type, item, null, null));
        this.textItems = new ItemWithIdListImpl<TextItem>(
                (type, item, newIndex, oldIndex) -> fireCollectionEventObservable(type, item, newIndex, oldIndex));
        this.outputTemplates = new ItemWithIdListImpl<OutputTemplate>(
                (type, item, newIndex, oldIndex) -> fireCollectionEventObservable(type, item, newIndex, oldIndex));
        this.groups = new ItemWithIdSetImpl<Group>(
                (type, item) -> fireCollectionEventListObject(type, item, null, null));
        this.companies = new ItemWithIdSetImpl<Company>(
                (type, item) -> fireCollectionEventListObject(type, item, null, null));
        this.trainTypeCategories = new ItemWithIdListImpl<TrainTypeCategory>(
                (type, item, newIndex, oldIndex) -> fireCollectionEventObservable(type, item, newIndex, oldIndex));
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
        this.freightNet = partFactory.createFreightNet(IdGenerator.getInstance().getId());
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
        this.validators.add(new TrainCycleTypeRemoveValidator(this));
        Collections.addAll(itemLists, routes, images, engineClasses, textItems, outputTemplates,
                groups, companies, trainTypes, trains, cycleTypes);
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
    public ItemWithIdSet<Train> getTrains() {
        return this.trains;
    }

    public Collection<TrainsCycle> getCycles() {
        List<TrainsCycle> result = new ArrayList<TrainsCycle>();
        for (TrainsCycleType type : cycleTypes) {
            result.addAll(type.getCycles());
        }
        return result;
    }

    public ItemWithIdSet<TrainsCycleType> getCycleTypes() {
        return cycleTypes;
    }

    public TrainsCycleType getEngineCycleType() {
        return this.getCycleTypeByNameImpl(TrainsCycleType.ENGINE_CYCLE);
    }

    public TrainsCycleType getTrainUnitCycleType() {
        return this.getCycleTypeByNameImpl(TrainsCycleType.TRAIN_UNIT_CYCLE);
    }

    public TrainsCycleType getDriverCycleType() {
        return this.getCycleTypeByNameImpl(TrainsCycleType.DRIVER_CYCLE);
    }

    public TrainsCycleType getDefaultCycleType(String typeName) {
        return this.getCycleTypeByNameImpl(typeName);
    }

    private TrainsCycleType getCycleTypeByNameImpl(String typeName) {
        for (TrainsCycleType type : cycleTypes) {
            if (typeName.equals(type.getName())) {
                return type;
            }
        }
        return null;
    }

    public TrainsCycle getCycleById(String id) {
        for (TrainsCycleType type : cycleTypes) {
            TrainsCycle found = type.getCycles().getById(id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public TrainsCycle getCycleByIdAndType(String id, TrainsCycleType type) {
        return getById(id, type.getCycles());
    }

    public FreightNet getFreightNet() {
        return freightNet;
    }

    private <T extends ObjectWithId> T getById(String id, Iterable<T> items) {
        return Iterables.tryFind(items, ModelPredicates.matchId(id)).orNull();
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
	public Attributes getAttributes() {
        return attributes;
    }

    public double getTimeScale() {
        return this.getAttribute(TrainDiagram.ATTR_TIME_SCALE, Double.class);
    }

    public Scale getScale() {
        return this.getAttribute(TrainDiagram.ATTR_SCALE, Scale.class);
    }

    public ItemWithIdSet<EngineClass> getEngineClasses() {
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

    public ItemWithIdList<TrainTypeCategory> getTrainTypeCategories() {
        return trainTypeCategories;
    }

    public TrainsData getTrainsData() {
        return trainsData;
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

    public Collection<Locale> getLocales() {
        return getAttributeAsCollection(ATTR_LOCALES, Locale.class, Collections.emptyList());
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
        for (TrainsCycleType type : cycleTypes) {
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
        object = getNet().getObjectById(id);
        if (object != null) {
            return object;
        }
        object = getCycleById(id);
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

    protected void fireCollectionEventObservable(Event.Type type, Observable item, Integer newIndex, Integer oldIndex) {
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
