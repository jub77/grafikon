package net.parostroj.timetable.gui.pm;

import net.parostroj.timetable.model.NodeTrack;
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
}
