package net.parostroj.timetable.gui.pm;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.BooleanPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;

import net.parostroj.timetable.model.NodeTrack;

/**
 * @author jub
 */
public class NodeTrackPM extends AbstractPM implements IPM<NodeTrack> {

    TextPM number;
    BooleanPM platform;

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
    }
}
