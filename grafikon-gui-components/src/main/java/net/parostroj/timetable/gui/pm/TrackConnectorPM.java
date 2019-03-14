package net.parostroj.timetable.gui.pm;

import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TrackConnector;
import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;

import java.util.Set;

/**
 * Presentation model for {@link TrackConnector}.
 *
 * @author jub
 */
public class TrackConnectorPM extends AbstractPM {

    TextPM number;
    ListPM<SelectedItemPM<NodeTrackPM>> nodeTracks;

    private TrackConnector reference;

    public TrackConnectorPM() {
        this.number = new TextPM();
        this.number.setMandatory(true);
        this.nodeTracks = new ListPM<>();
        PMManager.setup(this);
    }

    public TrackConnectorPM(TrackConnector connector, NodePM nodePM) {
        this();
        this.init(connector, nodePM);
    }

    public void init(TrackConnector connector, NodePM nodePM) {
        this.reference = connector;
        this.number.setText(connector.getNumber());
        Set<NodeTrack> nt = connector.getNodeTracks();
        nodePM.getTracks()
                .forEach(
                        ntPM -> nodeTracks.add(new SelectedItemPM<>(nt.contains(ntPM.getReference()), ntPM)));
    }

    public void writeResult() {
        if (reference != null) {
            // TODO write logic
        }
    }
}
