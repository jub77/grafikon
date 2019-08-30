package net.parostroj.timetable.model.changes;

import net.parostroj.timetable.model.*;
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
        name = train.getDefaultName();
    }

    @Override
    public void visit(Node node) {
        name = node.getName();
    }

    @Override
    public void visit(Line line) {
        Node from = line.getFrom();
        Node to = line.getTo();
        name = (from != null ? from.getAbbr() : "-") + " - " + (to != null ? to.getAbbr() : "-");
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
        name = type.getDefaultAbbr() + " - " + type.getDesc();
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

    @Override
    public void visit(TrainsCycleType type) {
        name = type.getName().getDefaultString();
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

    @Override
    public void visit(OutputTemplate template) {
        name = template.getKey();
    }

    @Override
    public void visit(Group group) {
        name = group.getName();
    }

    @Override
    public void visit(FreightNet net) {
        name = null;
    }

    @Override
    public void visit(Region region) {
        name = region.getName();
    }

    @Override
    public void visit(Company company) {
        name = company.getAbbr();
    }

    @Override
    public void visit(Output output) {
        name = output.getName() != null ? output.getName().getDefaultString() : "";
    }

    @Override
    public void visit(TrackConnector connector) {
        name = connector.getNumber();
    }
}
