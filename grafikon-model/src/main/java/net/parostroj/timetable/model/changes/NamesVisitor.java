package net.parostroj.timetable.model.changes;

import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.Net;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TextItem;
import net.parostroj.timetable.model.TimetableImage;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;

/**
 * Getting names from of objects.
 *
 * @author jub
 */
public class NamesVisitor implements TrainDiagramVisitor {

    private String name;

    @Override
    public void visit(TrainDiagram diagram) {
        name = null;
    }

    @Override
    public void visit(Net net) {
        name = null;
    }

    @Override
    public void visit(Train train) {
        name = train.getCompleteName();
    }

    @Override
    public void visit(Node node) {
        name = node.getName();
    }

    @Override
    public void visit(Line line) {
        name = line.getFrom().getAbbr() + " - " + line.getTo().getAbbr();
    }

    @Override
    public void visit(LineTrack track) {
        name = track.getNumber();
    }

    @Override
    public void visit(NodeTrack track) {
        name = track.getNumber();
    }

    @Override
    public void visit(TrainType type) {
        name = type.getAbbr() + " - " + type.getDesc();
    }

    @Override
    public void visit(Route route) {
        name = route.getName();
    }

    @Override
    public void visit(EngineClass engineClass) {
        name = engineClass.getName();
    }

    @Override
    public void visit(TrainsCycle cycle) {
        name = cycle.getName();
    }

    @Override
    public void visit(TextItem item) {
        name = item.getName();
    }

    @Override
    public void visit(TimetableImage image) {
        name = image.getFilename();
    }

    public String getName() {
        String n = name;
        this.name = null;
        return n;
    }

    @Override
    public void visit(LineClass lineClass) {
        name = lineClass.getName();
    }
}
