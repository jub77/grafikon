package net.parostroj.timetable.gui.pm;

import java.util.Arrays;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.IntegerPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.OnChange;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodePort;

/**
 * @author jub
 */
public class NodePortPM extends AbstractPM implements IPM<NodePort> {

    TextPM portId;
    IntegerPM position;
    EnumeratedValuesPM<Node.Side> orientation;

    public NodePortPM() {
        this.position = new IntegerPM(0);
        this.position.setMandatory(true);
        this.portId = new TextPM();
        this.orientation = new EnumeratedValuesPM<>(
                EnumeratedValuesPM.createValueMap(Arrays.asList(Node.Side.values()), v -> v.toString()));
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


    @Override
    public void init(NodePort port) {
        this.position.setInteger(port.getPosition());
        this.orientation.setValue(port.getOrientation());;
    }
}
