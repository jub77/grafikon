package net.parostroj.timetable.gui.components;

import java.io.IOException;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.TimeConverter;
import net.parostroj.timetable.visitors.EventVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The visitor transforms an event to a string representation.
 *
 * @author jub
 */
public class GTEventOutputVisitor implements EventVisitor {

    private static final String TIME_FORMAT = "%02d:%02d";
    private static final Logger LOG = LoggerFactory.getLogger(GTEventOutputVisitor.class.getName());

    private Appendable str;
    private boolean full;

    public GTEventOutputVisitor(Appendable str, boolean full) {
        this.str = str;
        this.full = full;
    }

    @Override
    public void visit(TrainDiagramEvent event) {
        try {
            str.append("TrainDiagramEvent[");
            str.append(Integer.toString(event.getSource().getTrains().size())).append(" trains]");
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
                if (event.getObject() instanceof Train)
                    str.append("    Train: ").append(((Train)event.getObject()).getName()).append('\n');
                if (event.getObject() instanceof Route)
                    str.append("    Route: ").append(event.getObject().toString()).append('\n');
                if (event.getObject() instanceof TrainType)
                    str.append("    Train type: ").append(event.getObject().toString()).append('\n');
                if (event.getObject() instanceof TextItem)
                    str.append("    Text item: ").append(event.getObject().toString()).append('\n');
                if (event.getObject() instanceof TimetableImage)
                    str.append("    Image: ").append(event.getObject().toString()).append('\n');
                if (event.getObject() instanceof TrainsCycleType)
                    str.append("    Cycle type: ").append(((TrainsCycleType)event.getObject()).getName());
                if (event.getObject() instanceof TrainsCycle)
                    str.append("    Cycle: ").append(((TrainsCycle)event.getObject()).getName());
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visit(NetEvent event) {
        try {
            str.append("NetEvent[");
            str.append(Integer.toString(event.getSource().getNodes().size())).append(" nodes, ");
            str.append(Integer.toString(event.getSource().getLines().size())).append(" lines]");
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getObject() instanceof Node)
                    str.append("    Node: ").append(((Node)event.getObject()).getName()).append('\n');
                if (event.getObject() instanceof LineClass)
                    str.append("    Line class: ").append(((LineClass)event.getObject()).getName()).append('\n');
                if (event.getObject() instanceof Line) {
                    str.append("    Line: ").append(((Line)event.getObject()).getFrom().getName()).append('-');
                    str.append(((Line)event.getObject()).getTo().getName()).append('\n');
                }
                if (event.getFromIndex() != 0 || event.getToIndex() != 0) {
                    str.append("    From index: ").append(Integer.toString(event.getFromIndex())).append('\n');
                    str.append("    To index  : ").append(Integer.toString(event.getToIndex())).append('\n');
                }
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visit(NodeEvent event) {
        try {
            str.append("NodeEvent[");
            str.append(event.getSource().getAbbr());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Node: ").append(event.getSource().getName()).append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getInterval() != null) {
                    str.append("    Train: ").append(event.getInterval().getTrain().getName()).append('\n');
                    str.append("    Track: ").append(event.getInterval().getTrack().getNumber()).append('\n');
                    str.append("    Time:  ").append(TimeConverter.formatIntToText(event.getInterval().getStart(), TIME_FORMAT));
                    str.append("-").append(TimeConverter.formatIntToText(event.getInterval().getEnd(), TIME_FORMAT));
                    str.append('\n');
                }
                if (event.getTrack() != null)
                    str.append("    Track: ").append(event.getTrack().getNumber()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visit(LineEvent event) {
        try {
            str.append("LineEvent[");
            str.append(event.getSource().getFrom().getAbbr());
            str.append('-');
            str.append(event.getSource().getTo().getAbbr());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Line: ").append(event.getSource().getFrom().getName()).append('-');
                str.append(event.getSource().getTo().getName()).append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getInterval() != null) {
                    str.append("    Train: ").append(event.getInterval().getTrain().getName()).append('\n');
                    str.append("    Track: ").append(event.getInterval().getTrack().getNumber()).append('\n');
                    str.append("    Time:  ").append(TimeConverter.formatIntToText(event.getInterval().getStart(), TIME_FORMAT));
                    str.append("-").append(TimeConverter.formatIntToText(event.getInterval().getEnd(), TIME_FORMAT));
                    str.append('\n');
                    str.append("    Direction: ").append(event.getInterval().getFrom().getAbbr());
                    str.append("-").append(event.getInterval().getTo().getAbbr());
                    str.append('\n');
                }
                if (event.getTrack() != null)
                    str.append("    Track: ").append(event.getTrack().getNumber()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visit(TrainEvent event) {
        try {
            str.append("TrainEvent[");
            str.append(event.getSource().getName());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Name: ").append(event.getSource().getCompleteName()).append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
                if (event.getCycleItem() != null) {
                    str.append("    Cycle item: ").append(event.getCycleItem().getFromInterval().getOwnerAsNode().getAbbr());
                    str.append('-').append(event.getCycleItem().getToInterval().getOwnerAsNode().getAbbr()).append('\n');
                }
                if (event.getTimeIntervalListType() != null) {
                    str.append("    Interval type: ").append(event.getTimeIntervalListType().toString()).append('\n');
                    str.append("    Change start:  ").append(Integer.toString(event.getIntervalChangeStart())).append('\n');
                    str.append("    Change:        ").append(Integer.toString(event.getChangedInterval())).append('\n');
                }
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visit(TrainTypeEvent event) {
        try {
            str.append("TrainTypeEvent[");
            str.append(event.getSource().getAbbr());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visit(TrainsCycleEvent event) {
        try {
            str.append("TrainsCycleEvent[");
            str.append(event.getSource().getName());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Cycle type: ").append(event.getSource().getType().toString()).append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange())).append('\n');
                if (event.getNewCycleItem() != null) {
                    str.append("    Cycle item: ").append(event.getNewCycleItem().getFromInterval().getOwnerAsNode().getAbbr());
                    str.append('-').append(event.getNewCycleItem().getToInterval().getOwnerAsNode().getAbbr()).append('\n');
                }
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visit(TextItemEvent event) {
        try {
            str.append("TextItemEvent[");
            str.append(event.getSource().getName());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visit(OutputTemplateEvent event) {
        try {
            str.append("OutputTemplateEvent[");
            str.append(event.getSource().getName());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    @Override
    public void visit(EngineClassEvent event) {
        try {
            str.append("EngineClassEvent[");
            str.append(event.getSource().getName());
            str.append(']');
            if (full) {
                str.append('\n');
                str.append("  Type: ").append(event.getType().toString()).append('\n');
                if (event.getAttributeChange() != null)
                    str.append("    Attribute: ").append(this.convertAttribute(event.getAttributeChange()));
                if (event.getTableActionType() != null && event.getWeightTableRow() != null) {
                    str.append("    Table action type: ").append(event.getTableActionType().toString()).append('\n');
                    str.append("    Weight table row speed: ").append(Integer.toString(event.getWeightTableRow().getSpeed()));
                }
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    private String convertAttribute(AttributeChange change) {
        String categoryStr = change.getCategory() == null ? "" : "[" + change.getCategory() + "]";
        if (change.getNewValue() == null)
            return categoryStr + change.getName() + " (removed)";
        else
            return categoryStr + change.getName();
    }
}
