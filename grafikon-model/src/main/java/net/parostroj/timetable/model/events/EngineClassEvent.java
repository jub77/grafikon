package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.WeightTableRow;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Engine class event (mostly changes to weight table).
 *
 * @author jub
 */
public class EngineClassEvent extends GTEvent<EngineClass> {

    public static enum Type {
        ROW_ADDED, ROW_REMOVED, ROW_MODIFIED;
    }

    private WeightTableRow row;
    private Type type;

    public EngineClassEvent(EngineClass source, GTEventType type) {
        super(source, type);
    }

    public EngineClassEvent(EngineClass source, WeightTableRow row, Type type) {
        this(source, GTEventType.WEIGHT_TABLE_MODIFIED);
        this.row = row;
        this.type = type;
    }

    public EngineClassEvent(EngineClass source, AttributeChange change) {
        super(source, change);
    }

    public WeightTableRow getWeightTableRow() {
        return row;
    }

    public Type getTableActionType() {
        return type;
    }

    @Override
    public void accept(EventVisitor visitor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
