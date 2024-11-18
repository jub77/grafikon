package net.parostroj.timetable.model;

import java.time.Instant;
import java.util.*;

import com.google.common.collect.Iterables;

import net.parostroj.timetable.filters.ModelPredicates;
import net.parostroj.timetable.model.changes.ChangesTracker;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.validators.*;
import net.parostroj.timetable.visitors.TrainDiagramTraversalVisitor;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Collection of all parts of graphical timetable.
 *
 * @author jub
 */
public class TrainDiagram implements AttributesHolder, ObjectWithId, Visitable, ObservableObject,
        CompounedObservable, ObjectWithVersion {

    public static final String ATTR_SCALE = "scale";
    public static final String ATTR_TIME_SCALE = "time.scale";
    public static final String ATTR_STATION_TRANSFER_TIME = "station.transfer.time";
    public static final String ATTR_LENGTH_UNIT = "length.unit";
    public static final String ATTR_WEIGHT_PER_AXLE = "weight.per.axle";
    public static final String ATTR_WEIGHT_PER_AXLE_EMPTY = "weight.per.axle.empty";
    public static final String ATTR_LENGTH_PER_AXLE = "length.per.axle";
    public static final String ATTR_ROUTE_LENGTH_RATIO = "route.length.ratio";
    public static final String ATTR_ROUTE_LENGTH_UNIT = "route.length.unit";
    public static final String ATTR_ROUTE_VALIDITY = "route.validity";
    public static final String ATTR_ROUTE_NODES = "route.nodes";
    public static final String ATTR_ROUTE_NUMBERS = "route.numbers";
    public static final String ATTR_FROM_TIME = "from.time";
    public static final String ATTR_TO_TIME = "to.time";
    public static final String ATTR_TRAIN_NAME_TEMPLATE = "train.name.template";
    public static final String ATTR_TRAIN_COMPLETE_NAME_TEMPLATE = "train.complete.name.template";
    public static final String ATTR_TIME_CONVERTER = "time.converter";
    public static final String ATTR_EDIT_LENGTH_UNIT = "edit.length.unit";
    public static final String ATTR_EDIT_SPEED_UNIT = "edit.speed.unit";
    public static final String ATTR_INFO = "info";
    public static final String ATTR_RUNNING_SCRIPT = "running.script";
    public static final String ATTR_TRAIN_SORT_PATTERN = "train.sort.pattern";
    public static final String ATTR_LOCALES = "locales";
    public static final String ATTR_SAVE_VERSION = "save.version";
    public static final String ATTR_SAVE_TIMESTAMP = "save.timestamp";
    public static final String ATTR_SAVE_USER = "save.user";
    public static final String ATTR_CHANGE_DIRECTION_STOP = "change.direction.stop";
    public static final String ATTR_VERSION = "version";

    /** Id. */
    private final String id;
    /** Net. */
    private final Net net;
    /** Freight net. */
    private final FreightNet freightNet;
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
    private final ItemWithIdSet<OutputTemplate> outputTemplates;
    /** List of outputs. */
    private final ItemWithIdSet<Output> outputs;
    /** Groups. */
    private final ItemWithIdSet<Group> groups;
    /** Companies */
    private final ItemWithIdSet<Company> companies;
    /** Penalty table. */
    private final ItemWithIdList<TrainTypeCategory> trainTypeCategories;

    private final TrainDiagramPartFactory partFactory;

    private final List<TrainDiagramValidator> validators;
    private final SystemListener listener;
    private final ChangesTrackerImpl changesTracker;
    private final ListenerSupport listenerSupport;
    private final ListenerSupport listenerSupportAll;
    private final RuntimeInfo runtimeInfo;
    private TimeConverter timeConverter;

    private final Iterable<ItemWithIdIterable<?>> itemLists;

    /**
     * Default constructor.
     */
    public TrainDiagram(String id) {
        this.id = id;
        this.partFactory = new TrainDiagramPartFactory(this);
        this.listener = this::fireNestedEvent;
        this.routes = new ItemWithIdSetImpl<>(this::fireEvent);
        this.trains = new ItemWithIdSetImpl<>((type, item) -> {
            if (type == Event.Type.ADDED) {
                item.attach();
            } else {
                item.detach();
            }
            this.fireEvent(type, item);
        });
        this.cycleTypes = new ItemWithIdSetImpl<>(this::fireEvent);
        this.images = new ItemWithIdSetImpl<>(this::fireEvent);
        this.engineClasses = new ItemWithIdSetImpl<>(this::fireEvent);
        this.textItems = new ItemWithIdListImpl<>(this::fireEvent);
        this.outputTemplates = new ItemWithIdSetImpl<>(this::fireEvent);
        this.outputs = new ItemWithIdSetImpl<>(this::fireEvent);
        this.groups = new ItemWithIdSetImpl<>(this::fireEvent);
        this.companies = new ItemWithIdSetImpl<>(this::fireEvent);
        this.trainTypeCategories = new ItemWithIdListImpl<>(this::fireEvent);
        this.net = new Net(this);
        this.trainTypes = new ItemWithIdListImpl<>(this::fireEvent);
        this.attributes = new Attributes(this::fireEvent);
        this.trainsData = new TrainsData(this);
        this.listenerSupport = new ListenerSupport();
        this.listenerSupportAll = new ListenerSupport();
        this.runtimeInfo = new RuntimeInfo(this::fireEvent);
        this.net.addAllEventListener(listener);
        this.changesTracker = new ChangesTrackerImpl();
        this.addAllEventListener(changesTracker);
        this.freightNet = new FreightNet(this);
        this.freightNet.addListener(listener);
        this.validators = new ArrayList<>();
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
        this.validators.add(new RegionValidator(this));
        this.validators.add(new NodeValidator(this));
        this.validators.add(new TrackConnectorValidator());
        this.validators.add(new TrainCycleTypeRemoveValidator(this));
        this.validators.add(new OutputTemplateRemoveValidator(this));
        this.validators.add(new OutputValidator(this));
        this.validators.add(new PreviousNextTrainValidator());
        this.itemLists = List.of(routes, images, engineClasses, textItems, outputTemplates,
                groups, companies, trainTypes, trains, cycleTypes, outputs);
    }

    /**
     * @return net
     */
    public Net getNet() {
        return net;
    }

    /**
     * @return freight net
     */
    public FreightNet getFreightNet() {
        return freightNet;
    }

    /**
     * @return runtime info container
     */
    public RuntimeInfo getRuntimeInfo() {
        return runtimeInfo;
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
        List<TrainsCycle> result = new ArrayList<>();
        for (TrainsCycleType type : cycleTypes) {
            result.addAll(type.getCycles());
        }
        return result;
    }

    public ItemWithIdSet<TrainsCycleType> getCycleTypes() {
        return cycleTypes;
    }

    public TrainsCycleType getEngineCycleType() {
        return this.getCycleTypeByKey(TrainsCycleType.ENGINE_CYCLE_KEY);
    }

    public TrainsCycleType getTrainUnitCycleType() {
        return this.getCycleTypeByKey(TrainsCycleType.TRAIN_UNIT_CYCLE_KEY);
    }

    public TrainsCycleType getDriverCycleType() {
        return this.getCycleTypeByKey(TrainsCycleType.DRIVER_CYCLE_KEY);
    }

    public TrainsCycleType getCycleTypeByKey(String key) {
        for (TrainsCycleType type : cycleTypes) {
            if (key.equals(type.getKey())) {
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

    private <T extends ObjectWithId> T getById(String id, Iterable<T> items) {
        return Iterables.tryFind(items, ModelPredicates.matchId(id)::test).orNull();
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

    public ItemWithIdSet<OutputTemplate> getOutputTemplates() {
        return outputTemplates;
    }

    public ItemWithIdSet<Output> getOutputs() {
        return outputs;
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
    public ModelVersion getVersion() {
        return getAttribute(ATTR_VERSION, ModelVersion.class, ModelVersion.initialModelVersion());
    }

    public int getSaveVersion() {
        return getAttribute(ATTR_SAVE_VERSION, Integer.class, 0);
    }

    public void setSaveVersion(int version) {
        setAttribute(ATTR_SAVE_VERSION, version);
    }

    public Instant getSaveTimestamp() {
        Long timestamp = getAttribute(ATTR_SAVE_TIMESTAMP, Long.class, null);
        return timestamp == null ? null : Instant.ofEpochMilli(timestamp);
    }

    public void setSaveTimestamp(Instant timestamp) {
        setRemoveAttribute(ATTR_SAVE_TIMESTAMP,
                timestamp == null ? null : timestamp.toEpochMilli());
    }

    public String getSaveUser() {
        return getAttribute(ATTR_SAVE_USER, String.class);
    }

    public void setSaveUser(String user) {
        setRemoveAttribute(ATTR_SAVE_USER, user);
    }

    @Override
    public void addListener(Listener listener) {
        listenerSupport.addListener(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listenerSupport.removeListener(listener);
    }

    @Override
    public void addAllEventListener(Listener listener) {
        listenerSupportAll.addListener(listener);
    }

    @Override
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

    private void fireEvent(Attributes attrs, AttributeChange change) {
        fireEvent(change);
    }

    private void fireEvent(AttributeChange change) {
        fireEvent(new Event(this, change));
    }

    private void processValidators(Event event) {
        if (!event.isConsumed()) {
            for (TrainDiagramValidator validator : validators) {
                validator.validate(event);
                if (event.isConsumed()) break;
            }
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
        for (Output output : outputs) {
            output.accept(visitor);
        }
        visitor.visitAfter(this);
    }

    public ObjectWithId getObjectById(String id) {
        if (getId().equals(id)) {
            return this;
        }
        ObjectWithId object;
        for (ItemWithIdIterable<?> itemList : itemLists) {
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

    protected void fireEvent(Event.Type type, Object item) {
        this.fireEvent(type, item, null, null);
    }

    private void fireEvent(Event.Type type, Object item, Integer newIndex, Integer oldIndex) {
        Event event = new Event(TrainDiagram.this, type, item, ListData.createData(oldIndex, newIndex));
        this.fireEvent(event);
        if (item instanceof ItemCollectionObject o) {
            switch (type) {
                case ADDED -> o.added();
                case REMOVED -> o.removed();
                default -> {}
            }
        }
        if (item instanceof ObservableObject o) {
            switch (type) {
                case ADDED -> o.addListener(listener);
                case REMOVED -> o.removeListener(listener);
                default -> {}
            }
        }
    }
}
