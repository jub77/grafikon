package net.parostroj.timetable.gui.pm;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;
import org.beanfabrics.model.*;
import org.beanfabrics.support.OnChange;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

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
    EnumeratedValuesPM<LineTrack> lineTrack;
    ListPM<TrackConnectorSwitchPM> switches;

    private TrackConnector reference;

    public TrackConnectorPM() {
        this.connectorId = new TextPM();
        this.position = new IntegerPM(0);
        this.position.setMandatory(true);
        this.number = new TextPM();
        this.number.setMandatory(true);
        this.orientation = new EnumeratedValuesPM<>(EnumeratedValuesPM
                .createValueMap(Arrays.asList(Node.Side.values()), v -> getSideString(v)));
        this.lineTrack = new EnumeratedValuesPM<>();
        this.switches = new ListPM<>();
        this.number.getValidator().add(new EmptySpacesValidationRule(this.number));
        PMManager.setup(this);
        updateConnectorId();
    }

    @OnChange(path = { "position", "orientation", "number", "lineTrack" })
    public void updateConnectorId() {
        boolean valid = position.isValid();
        if (valid) {
            connectorId.setText(
                    String.format("%s [%s, %d] - %s",
                    number.getText(),
                    getSideString(orientation.getValue()),
                    position.getInteger(),
                    lineTrack.getText()));
        }
    }

    public TrackConnectorPM(TrackConnector connector, IListPM<NodeTrackPM> tracksPm,
            Iterable<LineTrack> lineTracks) {
        this();
        this.init(connector, tracksPm, lineTracks);
    }

    public void init(TrackConnector connector, IListPM<NodeTrackPM> tracksPm,
            Iterable<LineTrack> lineTracks) {
        this.reference = connector;
        this.switches.clear();
        this.number.setText(connector.getNumber());
        Set<TrackConnectorSwitch> switches = connector.getSwitches();
        ImmutableMap<NodeTrack ,TrackConnectorSwitch> nt = FluentIterable.from(switches).uniqueIndex(sw -> sw.getNodeTrack());
        tracksPm.forEach(ntPM -> {
            boolean contains = nt.keySet().contains(ntPM.getReference());
            boolean straight = contains ? nt.get(ntPM.getReference()).isStraight() : false;
            this.switches.add(new TrackConnectorSwitchPM(ntPM, contains, straight));
        });
        orientation.setValue(connector.getOrientation());
        position.setInteger(connector.getPosition());
        // line connection
        this.initLineTrack(connector.getNode(), lineTracks);
        lineTrack.setValue(lineTrack.getValue());
    }

    public void initLineTrack(Node node, Iterable<LineTrack> lineTracks) {
        lineTrack.removeAllValues();
        lineTrack.addValues(lineTracks, t -> this.getTextForLineTrack(node, t), "-");
        lineTrack.setValue(null);
    }

    private String getTextForLineTrack(Node node, LineTrack track) {
        Line line = track.getOwner();
        Node fromNode = line.getFrom();
        if (fromNode == node) {
            fromNode = line.getTo();
        }
        return String.format("%s [%s]", fromNode.getAbbr(), track.getNumber());
    }

    public ListPM<TrackConnectorSwitchPM> getSwitches() {
        return switches;
    }

    public EnumeratedValuesPM<LineTrack> getLineTrack() {
        return lineTrack;
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
                this.setConnectorValues(newConn);
                // no node tracks switches
                node.getConnectors().add(newConn);
                reference = newConn;
            } else {
                // modify existing
                this.setConnectorValues(reference);
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
                            setSwitchValues(t, sw);
                            reference.getSwitches().add(t);
                            return t;
                        });
                        setSwitchValues(tcs, sw);
                    });
        }
    }

    private void setSwitchValues(TrackConnectorSwitch sw, TrackConnectorSwitchPM swPm) {
        sw.setStraight(swPm.getStraight().getBoolean());
    }

    private void setConnectorValues(TrackConnector conn) {
        conn.setNumber(number.getText().trim());
        conn.setPosition(position.getInteger());
        conn.setOrientation(orientation.getValue());
        conn.setLineTrack(lineTrack.getValue());
    }

    public static String getSideString(Node.Side side) {
        return ResourceLoader.getString("ne.side." + (side == Node.Side.LEFT ? "left" : "right"));
    }
}
