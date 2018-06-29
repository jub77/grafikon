package net.parostroj.timetable.gui.pm;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.IntegerPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.OnChange;

import net.parostroj.timetable.model.NodePort;

/**
 * @author jub
 */
public class NodePortPM extends AbstractPM implements IPM<NodePort> {

    TextPM portId;
    IntegerPM x;
    IntegerPM y;

    public NodePortPM() {
        this.x = new IntegerPM(0);
        this.x.setMandatory(true);
        this.y = new IntegerPM(0);
        this.y.setMandatory(true);
        this.portId = new TextPM();
        PMManager.setup(this);
        updatePortId();
    }

    @OnChange(path = { "x", "y" })
    public void updatePortId() {
        boolean xv = x.isValid();
        boolean yv = y.isValid();
        if (xv && yv) {
            portId.setText(String.format("[%d,%d]", x.getInteger(), y.getInteger()));
        }
    }


    @Override
    public void init(NodePort port) {
        this.x.setInteger(port.getLocation().getX());
        this.y.setInteger(port.getLocation().getY());
    }
}
