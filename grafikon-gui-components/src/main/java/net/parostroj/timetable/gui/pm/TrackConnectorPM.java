package net.parostroj.timetable.gui.pm;

import java.util.Arrays;
import java.util.Set;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.IListPM;
import org.beanfabrics.model.IntegerPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.OnChange;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TrackConnector;

/**
 * Presentation model for {@link TrackConnector}.
 *
 * @author jub
 */
public class TrackConnectorPM extends AbstractPM {

    TextPM connectorId;
    TextPM number;
    IntegerPM position;
    EnumeratedValuesPM<Node.Side> orientation;
    ListPM<SelectedItemPM<NodeTrackPM>> nodeTracks;

    private TrackConnector reference;

    public TrackConnectorPM() {
        this.connectorId = new TextPM();
        this.position = new IntegerPM(0);
        this.position.setMandatory(true);
        this.number = new TextPM();
        this.number.setMandatory(true);
        this.orientation = new EnumeratedValuesPM<>(EnumeratedValuesPM
                .createValueMap(Arrays.asList(Node.Side.values()), v -> v.toString()));
        this.nodeTracks = new ListPM<>();
        PMManager.setup(this);
        updateConnectorId();
    }

    @OnChange(path = { "position", "orientation", "number" })
    public void updateConnectorId() {
        boolean valid = position.isValid();
        if (valid) {
            connectorId.setText(String.format("[%s,%d] %s", orientation.getText(),
                    position.getInteger(), number.getText()));
        }
    }

    public TrackConnectorPM(TrackConnector connector, NodePM nodePM) {
        this();
        this.init(connector, nodePM.tracks);
    }

    public void init(TrackConnector connector, IListPM<NodeTrackPM> tracksPm) {
        this.reference = connector;
        this.number.setText(connector.getNumber());
        Set<NodeTrack> nt = connector.getNodeTracks();
        tracksPm.forEach(ntPM -> nodeTracks
                .add(new SelectedItemPM<>(nt.contains(ntPM.getReference()), ntPM)));
    }

    public ListPM<SelectedItemPM<NodeTrackPM>> getNodeTracks() {
        return nodeTracks;
    }

    public void writeResult() {
        if (reference != null) {
            // TODO write logic
        }
    }
}
