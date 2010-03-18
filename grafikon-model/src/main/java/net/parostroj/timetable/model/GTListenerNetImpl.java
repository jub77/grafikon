package net.parostroj.timetable.model;

import java.util.logging.Logger;
import net.parostroj.timetable.model.events.*;

/**
 * Gathers events from nodes and lines.
 * 
 * @author jub
 */
class GTListenerNetImpl implements NodeListener, LineListener {

    private static final Logger LOG = Logger.getLogger(GTListenerNetImpl.class.getName());
    private Net net;
    
    public GTListenerNetImpl(Net net) {
        this.net = net;
    }
    
    @Override
    public void nodeChanged(NodeEvent event) {
        net.fireNestedEvent(event);
    }

    @Override
    public void lineChanged(LineEvent event) {
        net.fireNestedEvent(event);
    }

}
