package net.parostroj.timetable.gui.pm;

import java.util.Optional;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrackConnector;
import net.parostroj.timetable.model.TrainDiagramPartFactory;
import net.parostroj.timetable.utils.IdGenerator;

/**
 * Presentation model for line track.
 *
 * @author jub
 */
public class LineTrackPM extends AbstractPM implements IPM<LineTrack> {

    TextPM number;

    private LineTrack reference;

    public LineTrackPM() {
        this.number = new TextPM();
        this.number.getValidator().add(new EmptySpacesValidationRule(this.number));
        this.number.setMandatory(true);
        PMManager.setup(this);
    }

    @Override
    public void init(LineTrack track) {
        number.setText(track.getNumber());
        this.reference = track;
    }

    public LineTrack getReference() {
        return this.reference;
    }

    public TextPM getNumber() {
        return number;
    }

    public void writeResult(Line line, int index) {
        if (line != null) {
            if (reference == null) {
                // new (no reference)
                LineTrack newTrack = new LineTrack(IdGenerator.getInstance().getId(), line,
                        number.getText().trim());
                line.getTracks().add(index, newTrack);
                reference = newTrack;
                Node fromNode = line.getFrom();
                Node toNode = line.getTo();
                TrainDiagramPartFactory factory = line.getDiagram().getPartFactory();
                this.addToConnector(fromNode, factory, newTrack, Node.Side.RIGHT, "2");
                this.addToConnector(toNode, factory, newTrack, Node.Side.LEFT, "1");
            } else {
                // modify existing
                reference.setNumber(number.getText().trim());
                int currentIndex = line.getTracks().indexOf(reference);
                if (index != currentIndex) {
                    line.getTracks().move(currentIndex, index);
                }
            }
        }
    }

    private void addToConnector(Node srcNode, TrainDiagramPartFactory factory,
            LineTrack track, Node.Side side, String name) {
        Optional<TrackConnector> connWithoutLineTrack = srcNode.getConnectors()
                .find(c -> !c.getLineTrack().isPresent());
        connWithoutLineTrack.orElseGet(() -> {
            TrackConnector connector = factory.createDefaultConnector(
                    IdGenerator.getInstance().getId(), srcNode, name, side,
                    Optional.empty());
            srcNode.getConnectors().add(connector);
            return connector;
        }).setLineTrack(Optional.of(track));
    }

    @Override
    public String toString() {
        return this.number.getText();
    }
}
