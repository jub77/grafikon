package net.parostroj.timetable.gui.pm;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.utils.IdGenerator;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.BooleanPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;

/**
 * @author jub
 */
public class NodeTrackPM extends AbstractPM implements IPM<NodeTrack> {

    TextPM number;
    BooleanPM platform;

    private NodeTrack reference;

    public NodeTrackPM() {
        this.number = new TextPM();
        this.number.getValidator().add(new EmptySpacesValidationRule(this.number));
        this.number.setMandatory(true);
        this.platform = new BooleanPM();
        PMManager.setup(this);
    }

    @Override
    public void init(NodeTrack track) {
        number.setText(track.getNumber());
        platform.setBoolean(track.isPlatform());
        this.reference = track;
    }

    public NodeTrack getReference() {
        return this.reference;
    }

    public TextPM getNumber() {
        return number;
    }

    public void writeResult(Node node, int index) {
        if (node != null) {
            if (reference == null) {
                // new (no reference)
                NodeTrack newTrack = new NodeTrack(IdGenerator.getInstance().getId(), node, number.getText().trim());
                node.getTracks().add(index, newTrack);
                reference = newTrack;
            } else {
                // modify existing
                reference.setNumber(number.getText().trim());
                reference.setPlatform(platform.getBoolean());
                int currentIndex = node.getTracks().indexOf(reference);
                if (index != currentIndex) {
                    node.getTracks().move(currentIndex, index);
                }
            }
        }
    }

    @Override
    public String toString() {
        return this.number.getText();
    }
}
