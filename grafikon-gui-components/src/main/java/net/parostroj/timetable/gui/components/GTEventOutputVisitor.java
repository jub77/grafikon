package net.parostroj.timetable.gui.components;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.TimeConverter;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * The visitor transforms an event to a string representation.
 *
 * @author jub
 */
public class GTEventOutputVisitor implements EventVisitor {

    private static final String TIME_FORMAT = "%02d:%02d";
    private static final Logger LOG = Logger.getLogger(GTEventOutputVisitor.class.getName());

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
                    str.append("    Attribute: ").append(event.getAttributeChange().getName());
                if (event.getTrain() != null)
                    str.append("    Train: ").append(event.getTrain().getName()).append('\n');
                if (event.getRoute() != null)
                    str.append("    Route: ").append(event.getRoute().toString()).append('\n');
                if (event.getTrainType() != null)
                    str.append("    Train type: ").append(event.getTrainType().toString()).append('\n');
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
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
                if (event.getNode() != null)
                    str.append("    Node: ").append(event.getNode().getName()).append('\n');
                if (event.getLineClass() != null)
                    str.append("    Line class: ").append(event.getLineClass().getName()).append('\n');
                if (event.getLine() != null) {
                    str.append("    Line: ").append(event.getLine().getFrom().getName()).append('-');
                    str.append(event.getLine().getTo().getName()).append('\n');
                }
                if (event.getFromIndex() != 0 || event.getToIndex() != 0) {
                    str.append("    From index: ").append(Integer.toString(event.getFromIndex())).append('\n');
                    str.append("    To index  : ").append(Integer.toString(event.getToIndex())).append('\n');
                }
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
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
                    str.append("    Attribute: ").append(event.getAttributeChange().getName());
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
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
                    str.append("    Attribute: ").append(event.getAttributeChange().getName());
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
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
                    str.append("    Attribute: ").append(event.getAttributeChange().getName());
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
            LOG.log(Level.WARNING, e.getMessage(), e);
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
                    str.append("    Attribute: ").append(event.getAttributeChange().getName());
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
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
                    str.append("    Attribute: ").append(event.getAttributeChange().getName());
                if (event.getNewCycleItem() != null) {
                    str.append("    Cycle item: ").append(event.getNewCycleItem().getFromInterval().getOwnerAsNode().getAbbr());
                    str.append('-').append(event.getNewCycleItem().getToInterval().getOwnerAsNode().getAbbr()).append('\n');
                }
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
        }
    }
}
