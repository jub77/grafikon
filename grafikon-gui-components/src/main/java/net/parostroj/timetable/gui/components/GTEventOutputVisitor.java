package net.parostroj.timetable.gui.components;

import java.io.IOException;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.EventVisitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The visitor transforms an event to a string representation.
 *
 * @author jub
 */
public class GTEventOutputVisitor implements EventVisitor {

    private static final Logger log = LoggerFactory.getLogger(GTEventOutputVisitor.class);

    private final Appendable str;
    private final boolean full;

    public GTEventOutputVisitor(Appendable str, boolean full) {
        this.str = str;
        this.full = full;
    }

    @Override
    public void visitDiagramEvent(Event event) {
        try {
            str.append("TrainDiagramEvent[");
            TrainDiagram diagram = (TrainDiagram) event.getSource();
            str.append(Integer.toString(diagram.getTrains().size())).append(" trains]");
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getObject() instanceof Company)
                    str.append("    Company: ").append(event.getObject().toString()).append('\n');
                if (event.getObject() instanceof Group)
                    str.append("    Group: ").append(event.getObject().toString()).append('\n');
                if (event.getObject() instanceof Region)
                    str.append("    Region: ").append(event.getObject().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
                if (event.getObject() instanceof Train)
                    str.append("    Train: ").append(((Train)event.getObject()).getDefaultName()).append('\n');
                if (event.getObject() instanceof Route)
                    str.append("    Route: ").append(event.getObject().toString()).append('\n');
                if (event.getObject() instanceof TrainType)
                    str.append("    Train type: ").append(event.getObject().toString()).append('\n');
                if (event.getObject() instanceof TextItem)
                    str.append("    Text item: ").append(event.getObject().toString()).append('\n');
                if (event.getObject() instanceof TimetableImage)
                    str.append("    Image: ").append(event.getObject().toString()).append('\n');
                if (event.getObject() instanceof TrainsCycleType)
                    str.append("    Cycle type: ").append(((TrainsCycleType) event.getObject()).getName().getDefaultString());
                if (event.getObject() instanceof TrainsCycle)
                    str.append("    Cycle: ").append(((TrainsCycle) event.getObject()).getName());
                if (event.getObject() instanceof OutputTemplate)
                    str.append("    Output template: ").append(((OutputTemplate) event.getObject()).getKey());
                if (event.getObject() instanceof TrainTypeCategory)
                    str.append("    Train type category: ").append(((TrainTypeCategory) event.getObject()).getName().getDefaultString());
                if (event.getObject() instanceof Output)
                    str.append("    Output: ").append(((Output) event.getObject()).toString());
                if (event.getObject() instanceof EngineClass)
                    str.append("    Engine class: ").append(((EngineClass) event.getObject()).toString());
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visitNetEvent(Event event) {
        try {
            str.append("NetEvent[");
            Net net = (Net) event.getSource();
            str.append(Integer.toString(net.getNodes().size())).append(" nodes, ");
            str.append(Integer.toString(net.getLines().size())).append(" lines]");
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getObject() instanceof Node)
                    str.append("    Node: ").append(((Node) event.getObject()).getName()).append('\n');
                if (event.getObject() instanceof LineClass)
                    str.append("    Line class: ").append(((LineClass) event.getObject()).getName()).append('\n');
                if (event.getObject() instanceof Line) {
                    Line l = (Line) event.getObject();
                    String from = l.getFrom() != null ? l.getFrom().getName() : "-";
                    String to = l.getTo() != null ? l.getTo().getName() : "-";
                    str.append("    Line: ").append(from).append('-');
                    str.append(to).append('\n');
                }
                if (event.getObject() instanceof Region) {
                    str.append("    Region: ").append(((Region) event.getObject()).getName()).append('\n');
                }
                if (event.getData() instanceof ListData) {
                    ListData data = (ListData) event.getData();
                    if (data.getFromIndex() != null) {
                        str.append("    From index: ").append(Integer.toString(data.getFromIndex())).append('\n');
                    }
                    if (data.getToIndex() != null) {
                        str.append("    To index  : ").append(Integer.toString(data.getToIndex())).append('\n');
                    }
                }
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visitFreightNetEvent(Event event) {
        try {
            FreightNet freightNet = (FreightNet) event.getSource();
            str.append(event.getType() == Event.Type.OBJECT_ATTRIBUTE && event.getObject() instanceof FNConnection ? "FreightNet(connection)[" : "FreightNet[");
            str.append(Integer.toString(freightNet.getConnections().size())).append(" connections]");
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getObject() instanceof FNConnection) {
                    FNConnection connection = (FNConnection) event.getObject();
                    String from = connection.getFrom().getTrain().getDefaultName();
                    String to = connection.getTo().getTrain().getDefaultName();
                    String text = String.format("%s - %s (%s)", from, to, connection.getFrom().getOwnerAsNode().getAbbr());
                    str.append("    Connection: ").append(text).append('\n');
                }
                if (event.getAttributeChange() != null) {
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
                }
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visitNodeEvent(Event event) {
        try {
        	Node node = (Node) event.getSource();
            TimeConverter c = node.getDiagram().getTimeConverter();
            str.append("NodeEvent[");
            str.append(node.getAbbr());
            str.append(']');
            if (full) {
                str.append("\n  Node: ").append(node.getName());
                str.append("\n  Type: ").append(event.getType().toString());
                if (event.getObject() instanceof TimeInterval) {
                    TimeInterval interval = (TimeInterval) event.getObject();
                    str.append("\n    Train: ").append(interval.getTrain().getDefaultName());
                    str.append("\n    Track: ").append(interval.getTrack().getNumber());
                    str.append("\n    Time:  ").append(c.convertIntToText(interval.getStart()));
                    str.append("-").append(c.convertIntToText(interval.getEnd()));
                }
                if (event.getObject() instanceof Track)
                    str.append("\n    Track: ").append(((Track) event.getObject()).getNumber());
                if (event.getObject() instanceof TrackConnector)
                    str.append("\n    Track connector: ").append(((TrackConnector) event.getObject()).toString());
                if (event.getAttributeChange() != null)
                    str.append("\n    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visitLineEvent(Event event) {
        try {
        	Line line = (Line) event.getSource();
            TimeConverter c = line.getDiagram().getTimeConverter();
            str.append("LineEvent[");
            str.append(line.getFrom() != null ? line.getFrom().getAbbr() : "-");
            str.append('-');
            str.append(line.getTo() != null ? line.getTo().getAbbr() : "-");
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Line: ").append(line.getFrom() != null ? line.getFrom().getName() : "-").append('-');
                str.append(line.getTo() != null ? line.getTo().getName() : "-").append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getObject() instanceof TimeInterval) {
                    TimeInterval interval = (TimeInterval) event.getObject();
                    str.append("    Train: ").append(interval.getTrain().getDefaultName()).append('\n');
                    str.append("    Track: ").append(interval.getTrack().getNumber()).append('\n');
                    str.append("    Time:  ").append(c.convertIntToText(interval.getStart()));
                    str.append("-").append(c.convertIntToText(interval.getEnd()));
                    str.append('\n');
                    str.append("    Direction: ").append(interval.getFrom().getAbbr());
                    str.append("-").append(interval.getTo().getAbbr());
                    str.append('\n');
                }
                if (event.getObject() instanceof Track)
                    str.append("    Track: ").append(((Track) event.getObject()).getNumber()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visitTrackConnectorEvent(Event event) {
        try {
            TrackConnector conn = (TrackConnector) event.getSource();
            str.append("TrackConnectorEvent[");
            str.append(conn.getNode().getAbbr()).append('-');
            str.append(conn.getNumber());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Connector: ").append(conn.getNumber()).append('\n');
                str.append("  Type: ").append(event.getType().toString());
                if (event.getObject() instanceof TrackConnectorSwitch) {
                    TrackConnectorSwitch sw = (TrackConnectorSwitch) event.getObject();
                    str.append("\n    Switch: ").append(sw.getNodeTrack().getNumber());
                }
                if (event.getAttributeChange() != null)
                    str.append("\n    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visitTrainEvent(Event event) {
        try {
            Train train = (Train) event.getSource();
            str.append("TrainEvent[");
            str.append(train.getDefaultName());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Name: ").append(train.getDefaultCompleteName()).append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
                if (event.getObject() instanceof TrainsCycleItem) {
                    TrainsCycleItem item = (TrainsCycleItem) event.getObject();
                    str.append("    Cycle item: ").append(item.getFromInterval().getOwnerAsNode().getAbbr());
                    str.append('-').append(item.getToInterval().getOwnerAsNode().getAbbr()).append('\n');
                }
                if (event.getData() instanceof SpecialTrainTimeIntervalList) {
                    SpecialTrainTimeIntervalList special = (SpecialTrainTimeIntervalList) event.getData();
                    str.append("    Interval type: ").append(special.getType().toString()).append('\n');
                    str.append("    Change start:  ").append(Integer.toString(special.getStart())).append('\n');
                    str.append("    Change:        ").append(Integer.toString(special.getChanged())).append('\n');
                }
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visitTrainTypeEvent(Event event) {
        try {
            TrainType traintType = (TrainType) event.getSource();
            str.append("TrainTypeEvent[");
            str.append(traintType.getDefaultAbbr());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visitTrainsCycleEvent(Event event) {
        try {
            str.append("TrainsCycleEvent[");
            TrainsCycle trainsCycle = (TrainsCycle) event.getSource();
            str.append(trainsCycle.getName());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Name: ").append(trainsCycle.getName()).append('\n');
                str.append("  Cycle type: ").append(trainsCycle.getType().getName().getDefaultString()).append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange())).append('\n');
                if (event.getObject() instanceof TrainsCycleItem) {
                    TrainsCycleItem item = (TrainsCycleItem) event.getObject();
                    str.append("    Cycle item: ").append(item.getFromInterval().getOwnerAsNode().getAbbr());
                    str.append('-').append(item.getToInterval().getOwnerAsNode().getAbbr()).append('\n');
                    str.append("    Train: ").append(item.getTrain().getDefaultName()).append('\n');
                }
                if (event.getData() != null) {
                    str.append("    Special: ").append(event.getData().toString());
                }
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visitTrainsCycleTypeEvent(Event event) {
        try {
            str.append("TrainsCycleTypeEvent[");
            TrainsCycleType trainsCycleType = (TrainsCycleType) event.getSource();
            str.append(trainsCycleType.getName().getDefaultString());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null) {
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
                }
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visitTextItemEvent(Event event) {
        try {
            str.append("TextItemEvent[");
            str.append(((TextItem) event.getSource()).getName());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visitOutputTemplateEvent(Event event) {
        try {
            str.append("OutputTemplateEvent[");
            str.append(((OutputTemplate) event.getSource()).getKey());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visitOutputEvent(Event event) {
        try {
            str.append("OutputEvent[");
            str.append(((Output) event.getSource()).toString());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visitEngineClassEvent(Event event) {
        try {
            EngineClass engineClass = (EngineClass) event.getSource();
            str.append("EngineClassEvent[");
            str.append(engineClass.getName());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
                if (event.getObject() instanceof WeightTableRow) {
                    str.append("    Table action type: ").append(event.getType().toString()).append('\n');
                    str.append("    Weight table row speed: ").append(Integer.toString(((WeightTableRow) event.getObject()).getSpeed()));
                }
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visitLineClassEvent(Event event) {
        try {
            LineClass lineClass = (LineClass) event.getSource();
            str.append("LineClassEvent[");
            str.append(lineClass.getName());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visitTrainTypeCategoryEvent(Event event) {
        try {
            TrainTypeCategory category = (TrainTypeCategory) event.getSource();
            str.append("TrainTypeCategoryEvent[");
            str.append(category.getName().getDefaultString());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
                if (event.getObject() instanceof PenaltyTableRow) {
                    str.append("    Table action type: ").append(event.getType().toString()).append('\n');
                    str.append("    Penalty table row speed: ").append(Integer.toString(((PenaltyTableRow) event.getObject()).getSpeed()));
                }
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visitOtherEvent(Event event) {
        try {
            str.append("OtherEvent[");
            str.append(event.getSource().toString());
            str.append("]");
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    private String convertAttribute(AttributeChange change) {
        String categoryStr = change.getCategory() == null ? "" : "[" + change.getCategory() + "]";
        if (change.getNewValue() == null)
            return categoryStr + change.getName() + " (removed)";
        else
            return categoryStr + change.getName() + " (" + change.getNewValue() + ")";
    }
}
