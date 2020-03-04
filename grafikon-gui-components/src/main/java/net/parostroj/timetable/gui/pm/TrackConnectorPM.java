package net.parostroj.timetable.gui.pm;

import static java.util.stream.Collectors.toList;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.beanfabrics.event.ElementsAddedEvent;
import org.beanfabrics.event.ListAdapter;
import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.IListPM;
import org.beanfabrics.model.IntegerPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.OnChange;

import com.google.common.collect.FluentIterable;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.Node;
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
                .createValueMap(Arrays.asList(Node.Side.values()), TrackConnectorPM::getSideString));
        this.lineTrack = new EnumeratedValuesPM<>();
        this.switches = new ListPM<>();
        this.switches.addListListener(new ListAdapter() {
            private PropertyChangeListener l = e -> {
                TrackConnectorSwitchPM switchPM = (TrackConnectorSwitchPM) e.getSource();
                if (switchPM.getStraight().getBoolean()) {
                    // switch all others off
                    switches.forEach(s -> {
                        if (s != switchPM && s.getStraight().getBoolean()) {
                            s.getStraight().setBoolean(false);
                        }
                    });
                }
            };

            @Override
            public void elementsAdded(ElementsAddedEvent evt) {
                for (int i = evt.getBeginIndex(); i < evt.getBeginIndex() + evt.getLength(); i++) {
                    TrackConnectorSwitchPM switchPM = switches.getAt(i);
                    switchPM.addPropertyChangeListener("straight", l);
                }
            }
        });
        this.number.getValidator().add(new EmptySpacesValidationRule(this.number));
        PMManager.setup(this);
        updateConnectorId();
    }

    @OnChange(path = { "position", "orientation", "number", "lineTrack" })
    public void updateConnectorId() {
        boolean valid = position.isValid();
        if (valid) {
            connectorId.setText(
                    String.format("%s [%s] > %s",
                    number.getText(),
                    getSideString(orientation.getValue()),
                    lineTrack.getText()));
        }
    }

    public TrackConnectorPM(TrackConnector connector, IListPM<NodeTrackPM> tracksPm,
            Collection<LineTrack> lineTracks) {
        this();
        this.init(connector, tracksPm, lineTracks);
    }

    public void init(TrackConnector connector, IListPM<NodeTrackPM> tracksPm,
            Collection<LineTrack> lineTracks) {
        this.reference = connector;
        this.switches.clear();
        this.number.setText(connector.getNumber());
        Set<TrackConnectorSwitch> switches = connector.getSwitches();
        Map<NodeTrack, TrackConnectorSwitch> nt = FluentIterable.from(switches)
                .uniqueIndex(TrackConnectorSwitch::getNodeTrack);
        tracksPm.forEach(ntPM -> {
            boolean contains = nt.containsKey(ntPM.getReference());
            boolean straight = contains && nt.get(ntPM.getReference()).isStraight();
            TrackConnectorSwitchPM switchPM = new TrackConnectorSwitchPM(ntPM, contains, straight);
            // check if it should be editable...
            if (contains) {
                setConnectorEditable(ntPM, switchPM);
            }
            this.switches.add(switchPM);
        });
        orientation.setValue(connector.getOrientation());
        position.setInteger(connector.getPosition());
        // line connection
        this.initLineTrack(connector.getNode(), lineTracks, connector.getLineTrack());
        // do not allow editing of line track in use
        lineTrack.setEditable(connector.getLineTrack().map(lt -> lt.isEmpty()).orElse(true));
    }

    public void setConnectorEditable(NodeTrackPM ntPM, TrackConnectorSwitchPM switchPM) {
        NodeTrack nTrack = ntPM.getReference();
        if (reference == null || !reference.getLineTrack().isPresent() || nTrack == null) return;
        LineTrack cLineTrack = reference.getLineTrack().get();
        boolean isThroughConnector = nTrack.getTimeIntervalList().stream()
                .anyMatch(i -> i.getPreviousTrainInterval() != null
                        && i.getPreviousTrainInterval().getTrack() == cLineTrack
                        || i.getNextTrainInterval() != null
                                && i.getNextTrainInterval().getTrack() == cLineTrack);
        switchPM.connected.setEditable(!isThroughConnector);
    }

    public void initLineTrack(Node node, Collection<LineTrack> lineTracks, Optional<LineTrack> lt) {
        lineTrack.removeAllValues();
        // limit to current line track and empty line tracks
        lineTrack.addValues(lineTracks.stream().filter(t -> t == lt.orElse(null) || t.isEmpty())
                .collect(toList()), t -> this.getTextForLineTrack(node, t), "-");
        lineTrack.setValue(lt.orElse(null));
    }

    private String getTextForLineTrack(Node node, LineTrack track) {
        Line line = track.getOwner();
        Node fromNode = line.getFrom();
        if (fromNode == node) {
            fromNode = line.getTo();
        }
        return String.format("%s (%s)", track.getNumber(), fromNode.getAbbr());
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
                        .createConnector(IdGenerator.getInstance().getId(), node);
                newConn.setNumber(number.getText());
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
                                    IdGenerator.getInstance().getId());
                            t.setNodeTrack(sw.getTrack().getReference());
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
        conn.setLineTrack(Optional.ofNullable(lineTrack.getValue()));
    }

    public static String getSideString(Node.Side side) {
        return ResourceLoader.getString("ne.side." + (side == Node.Side.LEFT ? "left" : "right"));
    }
}
