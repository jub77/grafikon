package net.parostroj.timetable.model.changes;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.EventVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Transforms diagram event into DiagramChange.
 *
 * @author jub
 */
public class TransformVisitor implements EventVisitor {
    private static final String ACTION_MISSING_TEXT = "Action missing: ";

    private DiagramChange change;
    private final EventToChangeConvert converter = new EventToChangeConvert();
    private final NamesVisitor names = new NamesVisitor();

    @Override
    public void visitDiagramEvent(Event event) {
        DiagramChange.Type type = getChangeType(event);
        DiagramChange.Action action = converter.getAction(event.getType());
        if (type != null) {
            if (action == null) {
                throw new IllegalArgumentException(ACTION_MISSING_TEXT + event.getType());
            }
            change = new DiagramChange(type, action, ((ObjectWithId) event.getObject()).getId());
            // get name
            change.setObject(this.getObjectStr(event.getObject()));
        } else {
            change = new DiagramChange(DiagramChange.Type.DIAGRAM, action,
                    event.getType() == Event.Type.OBJECT_ATTRIBUTE ?
                            ((ObjectWithId) event.getObject()).getId() :
                            ((ObjectWithId) event.getSource()).getId());
            if (event.getType() == Event.Type.OBJECT_ATTRIBUTE) {
                change.setObject(this.getObjectStr(event.getObject()));
            }
            if (action == null) {
                throw new IllegalArgumentException(ACTION_MISSING_TEXT + event.getType());
            }
            this.addDescription(event);
        }
    }

    @Override
    public void visitNetEvent(Event event) {
        DiagramChange.Type type = getChangeType(event);
        DiagramChange.Action action = converter.getAction(event.getType());
        if (type != null) {
            if (action == null)
                throw new IllegalArgumentException(ACTION_MISSING_TEXT + event.getType());
            change = new DiagramChange(type, action, ((ObjectWithId) event.getObject()).getId());
            // get name
            change.setObject(this.getObjectStr(event.getObject()));
        }
    }

    @Override
    public void visitFreightNetEvent(Event event) {
        DiagramChange.Type type = getChangeType(event);
        DiagramChange.Action action = converter.getAction(event.getType());
        ObjectWithId o = (ObjectWithId) event.getObject();
        if (type != null) {
            if (action == null) {
                throw new IllegalArgumentException(ACTION_MISSING_TEXT + event.getType());
            }
            change = new DiagramChange(type, action, o.getId());
        } else {
        	Object object = event.getObject();
        	if (object instanceof ObjectWithId) {
        		change = new DiagramChange(DiagramChange.Type.FREIGHT_NET, action, ((ObjectWithId) event.getObject()).getId());
        		if (action == null) {
        			throw new IllegalArgumentException(ACTION_MISSING_TEXT + event.getType());
        		}
        		this.addDescription(event);
        	} else {
        		throw new IllegalArgumentException("Object with missing id: " + event.getSource().getClass());
        	}
        }
        if (o != null) {
            change.setObject(this.getObjectStr(o));
        }
    }

    @Override
    public void visitNodeEvent(Event event) {
        change = new DiagramChange(DiagramChange.Type.NODE, ((ObjectWithId) event.getSource()).getId());
        change.setObject(((Node) event.getSource()).getName());
        change.setAction(DiagramChange.Action.MODIFIED);
        this.addDescription(event);
    }

    @Override
    public void visitLineEvent(Event event) {
        Line line = (Line) event.getSource();
        change = new DiagramChange(DiagramChange.Type.LINE, line.getId());
        change.setObject(line.getFrom().getName() + " - " + line.getTo().getName());
        change.setAction(DiagramChange.Action.MODIFIED);
        this.addDescription(event);
    }

    @Override
    public void visitTrackConnectorEvent(Event event) {
        TrackConnector conn = (TrackConnector) event.getSource();
        change = new DiagramChange(DiagramChange.Type.NODE, conn.getNode().getId());
        change.setObject(conn.getNumber());
        change.setAction(DiagramChange.Action.MODIFIED);
        this.addDescription(event);
    }

    @Override
    public void visitTrainEvent(Event event) {
        Train train = (Train) event.getSource();
        change = new DiagramChange(DiagramChange.Type.TRAIN, train.getId());
        change.setObject(train.getDefaultName());
        change.setAction(DiagramChange.Action.MODIFIED);
        this.addDescription(event);
        if (event.getType() == Event.Type.SPECIAL && event.getData() instanceof SpecialTrainTimeIntervalList) {
            SpecialTrainTimeIntervalList special = (SpecialTrainTimeIntervalList) event.getData();
            String desc = converter.getTilDesc(special.getType());
            DiagramChangeDescription dcd = new DiagramChangeDescription(desc);
            switch (special.getType()) {
                case SPEED:
                case STOP_TIME:
                case TRACK:
                    dcd.setParams(new Parameter(getSegmentDescription(getChangedInterval(event, special))));
                    break;
                default:
                    break;
            }
            change.addDescription(dcd);
        }
    }

    private TimeInterval getChangedInterval(Event event, SpecialTrainTimeIntervalList special) {
        return ((Train) event.getSource()).getTimeIntervalList().get(special.getChanged());
    }

    @Override
    public void visitTrainTypeEvent(Event event) {
        TrainType trainType = (TrainType) event.getSource();
        change = new DiagramChange(DiagramChange.Type.TRAIN_TYPE, trainType.getId());
        change.setObject(this.getObjectStr(trainType));
        change.setAction(DiagramChange.Action.MODIFIED);
        this.addDescription(event);
    }

    @Override
    public void visitTrainsCycleEvent(Event event) {
        TrainsCycle trainsCycle = (TrainsCycle) event.getSource();
        change = new DiagramChange(DiagramChange.Type.TRAINS_CYCLE, trainsCycle.getId());
        change.setObject(trainsCycle.getName());
        change.setAction(DiagramChange.Action.MODIFIED);
        String desc = this.addDescription(event);
        switch (event.getType()) {
            case ADDED:
            case MOVED:
            case REMOVED:
            case REPLACED:
                Train t = event.getObject() instanceof TrainsCycleItem ? ((TrainsCycleItem) event.getObject()).getTrain() : null;
                if (t != null) {
                    change.addDescription(new DiagramChangeDescription(desc, new Parameter(this.getObjectStr(t))));
                }
                break;
            case SPECIAL:
                if (event.getData() == Special.SEQUENCE) {
                    change.addDescription(new DiagramChangeDescription(desc, new Parameter("sequence", true)));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void visitTrainsCycleTypeEvent(Event event) {
        TrainsCycleType trainsCycleType = (TrainsCycleType) event.getSource();
        change = new DiagramChange(DiagramChange.Type.CYCLE_TYPE, trainsCycleType.getId());
        change.setObject(trainsCycleType.getKey());
        change.setAction(DiagramChange.Action.MODIFIED);
        this.addDescription(event);
    }

    @Override
    public void visitTextItemEvent(Event event) {
        TextItem textItem = (TextItem) event.getSource();
        change = new DiagramChange(DiagramChange.Type.TEXT_ITEM, textItem.getId());
        change.setObject(textItem.getName());
        change.setAction(DiagramChange.Action.MODIFIED);
        this.addDescription(event);
    }

    @Override
    public void visitOutputTemplateEvent(Event event) {
        OutputTemplate outputTemplate = (OutputTemplate) event.getSource();
        change = new DiagramChange(DiagramChange.Type.OUTPUT_TEMPLATE, outputTemplate.getId());
        change.setObject(outputTemplate.getKey());
        change.setAction(DiagramChange.Action.MODIFIED);
        this.addDescription(event);
    }

    @Override
    public void visitOutputEvent(Event event) {
        Output output = (Output) event.getSource();
        change = new DiagramChange(DiagramChange.Type.OUTPUT, output.getId());
        change.setObject(output.toString());
        change.setAction(DiagramChange.Action.MODIFIED);

    }

    @Override
    public void visitEngineClassEvent(Event event) {
        EngineClass engineClass = (EngineClass) event.getSource();
        change = new DiagramChange(DiagramChange.Type.ENGINE_CLASS, engineClass.getId());
        change.setObject(engineClass.getName());
        change.setAction(DiagramChange.Action.MODIFIED);
        String desc = this.addDescription(event);
        if (event.getObject() instanceof WeightTableRow) {
            int speed = ((WeightTableRow) event.getObject()).getSpeed();
            change.addDescription(new DiagramChangeDescription(desc, new Parameter(Integer.toString(speed))));
        }
    }

    @Override
    public void visitTrainTypeCategoryEvent(Event event) {
        TrainTypeCategory category = (TrainTypeCategory) event.getSource();
        change = new DiagramChange(DiagramChange.Type.TRAIN_TYPE_CATEGORY, category.getId());
        change.setObject(category.getName().getDefaultString());
        change.setAction(DiagramChange.Action.MODIFIED);
        String desc = this.addDescription(event);
        if (event.getObject() instanceof PenaltyTableRow) {
            int speed = ((PenaltyTableRow) event.getObject()).getSpeed();
            change.addDescription(new DiagramChangeDescription(desc, new Parameter(Integer.toString(speed))));
        }
    }

    @Override
    public void visitLineClassEvent(Event event) {
        // no change recorded
    }

    @Override
    public void visitOtherEvent(Event event) {
        // no change recorded
    }

    public DiagramChange getChange() {
        DiagramChange c = this.change;
        this.change = null;
        return c;
    }

    private String getObjectStr(Object object) {
        if (object instanceof Visitable) {
            ((Visitable)object).accept(names);
            return names.getName();
        } else {
            throw new IllegalArgumentException("Not known class: " + object.getClass());
        }
    }

    private String addDescription(Event event) {
        String desc = converter.getDesc(event.getType());
        AttributeChange aC;
        switch (event.getType()) {
            case ATTRIBUTE:
            case OBJECT_ATTRIBUTE:
                aC = event.getAttributeChange();
                change.addDescription(new DiagramChangeDescription(desc,
                        new Parameter(aC.getName(), aC.getCategory() == null)));
                break;
            default:
                break;
        }
        return desc;
    }

    private DiagramChange.Type getChangeType(Event event) {
        return event.getType().isList() ? converter.getType(event.getObject()) : null;
    }

    private String getSegmentDescription(TimeInterval interval) {
        if (interval.isLineOwner()) {
            return interval.getFrom().getAbbr() + "-" + interval.getTo().getAbbr();
        } else {
            return interval.getOwnerAsNode().getName();
        }
    }
}
