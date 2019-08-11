package net.parostroj.timetable.gui.pm;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.IListPM;
import org.beanfabrics.model.IntegerPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.OnChange;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Node.Side;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TrackConnector;
import net.parostroj.timetable.model.TrackConnectorSwitch;
import net.parostroj.timetable.utils.IdGenerator;

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
    ListPM<TrackConnectorSwitchPM> switches;

    private TrackConnector reference;

    public TrackConnectorPM() {
        this.connectorId = new TextPM();
        this.position = new IntegerPM(0);
        this.position.setMandatory(true);
        this.number = new TextPM();
        this.number.setMandatory(true);
        this.orientation = new EnumeratedValuesPM<>(EnumeratedValuesPM
                .createValueMap(Arrays.asList(Node.Side.values()), v -> v.toString()));
        this.switches = new ListPM<>();
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

    public TrackConnectorPM(TrackConnector connector, IListPM<NodeTrackPM> tracksPm) {
        this();
        this.init(connector, tracksPm);
    }

    public void init(TrackConnector connector, IListPM<NodeTrackPM> tracksPm) {
        this.reference = connector;
        this.number.setText(connector.getNumber());
        Set<TrackConnectorSwitch> switches = connector.getSwitches();
        ImmutableMap<NodeTrack ,TrackConnectorSwitch> nt = FluentIterable.from(switches).uniqueIndex(sw -> sw.getNodeTrack());
        tracksPm.forEach(ntPM -> {
            boolean contains = nt.keySet().contains(ntPM.getReference());
            boolean straight = contains ? nt.get(ntPM.getReference()).isStraight() : false;
            this.switches.add(new TrackConnectorSwitchPM(ntPM, contains, straight));
        });
    }

    public ListPM<TrackConnectorSwitchPM> getSwitches() {
        return switches;
    }

    public TrackConnector getReference() {
        return reference;
    }

    public void writeResult(Node node) {
        if (node != null) {
            if (reference == null) {
                // new (no reference)
                TrackConnector newConn = node.getDiagram().getPartFactory()
                        .createConnector(IdGenerator.getInstance().getId(), node, number.getText());
                newConn.setOrientation(Side.LEFT);
                newConn.setPosition(0);
                // no node tracks switches
                node.getConnectors().add(newConn);
                reference = newConn;
            } else {
                // modify existing
                reference.setNumber(number.getText());
                reference.setPosition(position.getInteger());
                reference.setOrientation(orientation.getValue());
                // remove non-connected switches
                Set<NodeTrack> connectedTracks = FluentIterable.from(switches)
                        .filter(sw -> sw.getConnected().getBoolean())
                        .transform(TrackConnectorSwitchPM::getTrack)
                        .transform(NodeTrackPM::getReference).toSet();
                reference.getSwitches()
                        .removeIf(sw -> !connectedTracks.contains(sw.getNodeTrack()));
            }
            // apply changed state
            FluentIterable.from(switches).filter(sw -> sw.getConnected().getBoolean())
                    .forEach(sw -> {
                        // get corresponding switch
                        Optional<TrackConnectorSwitch> cso = reference.getSwitches()
                                .find(s -> s.getNodeTrack() == sw.getTrack().getReference());
                        TrackConnectorSwitch tcs = cso.orElseGet(() -> {
                            TrackConnectorSwitch t = reference.createSwitch(
                                    IdGenerator.getInstance().getId(),
                                    sw.getTrack().getReference());
                            reference.getSwitches().add(t);
                            return t;
                        });
                        tcs.setStraight(sw.getStraight().getBoolean());
                    });
        }
    }
}
