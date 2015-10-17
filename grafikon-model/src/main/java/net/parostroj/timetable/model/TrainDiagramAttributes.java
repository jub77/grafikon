package net.parostroj.timetable.model;

/**
 * Train diagram attributes.
 *
 * @author jub
 */
public interface TrainDiagramAttributes {
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
    public static final String ATTR_NET = "net";
    public static final String ATTR_FREIGHT_NET = "freight.net";
    public static final String ATTR_PENALTY_TABLE = "penalty.table";
}
