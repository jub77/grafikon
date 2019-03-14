package net.parostroj.timetable.gui.pm;

import java.util.Arrays;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.IntegerPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.OnChange;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodePort;

/**
 * @author jub
 */
public class NodePortPM extends AbstractPM {

    TextPM portId;
    IntegerPM position;
    EnumeratedValuesPM<Node.Side> orientation;
    ItemListPM<TrackConnectorPM> connectors;

    public NodePortPM() {
        this.position = new IntegerPM(0);
        this.position.setMandatory(true);
        this.portId = new TextPM();
        this.orientation = new EnumeratedValuesPM<>(
                EnumeratedValuesPM.createValueMap(Arrays.asList(Node.Side.values()), v -> v.toString()));
        this.connectors = new ItemListPM<>(() -> {
            TrackConnectorPM tc = new TrackConnectorPM();
            tc.number.setText("1");
            return tc;
        });
        PMManager.setup(this);
        updatePortId();
    }

    @OnChange(path = { "position", "orientation" })
    public void updatePortId() {
        boolean valid = position.isValid();
        if (valid) {
            portId.setText(String.format("[%s,%d]", orientation.getText(), position.getInteger()));
        }
    }

    public void init(NodePort port, NodePM nodePM) {
        this.position.setInteger(port.getPosition());
        this.orientation.setValue(port.getOrientation());
        this.connectors.clear();
        port.getConnectors().forEach(connector -> this.connectors.add(new TrackConnectorPM(connector, nodePM)));
    }

    public ListPM<TrackConnectorPM> getConnectors() {
        return connectors;
    }
}
