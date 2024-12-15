package net.parostroj.timetable.model.ls.impl4;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrackConnector;
import net.parostroj.timetable.model.TrackConnectorSwitch;
import net.parostroj.timetable.model.ls.LSException;

/**
 * @author jub
 */
@XmlType(propOrder = { "id", "attributes", "switches" })
public class LSTrackConnector {

    private String id;
    private LSAttributes attributes;
    private List<LSTrackConnectorSwitch> switches;

    public LSTrackConnector() {
    }

    public LSTrackConnector(TrackConnector connector) {
        this.id = connector.getId();
        this.attributes = new LSAttributes(connector.getAttributes(),
                TrackConnector.ATTR_LINE_TRACK);
        this.switches = new ArrayList<>();
        for (TrackConnectorSwitch sw : connector.getSwitches()) {
            switches.add(new LSTrackConnectorSwitch(sw));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    @XmlElement(name = "switch")
    public List<LSTrackConnectorSwitch> getSwitches() {
        return switches;
    }

    public void setSwitches(List<LSTrackConnectorSwitch> switches) {
        this.switches = switches;
    }

    public TrackConnector createConnector(LSContext context, Node node) throws LSException {
        Attributes attrs = attributes.createAttributes(context);
        TrackConnector conn = context.getPartFactory().createConnector(id, node);
        conn.getAttributes().add(attrs);
        if (this.switches != null) {
            for (LSTrackConnectorSwitch s : this.switches) {
                conn.getSwitches().add(s.createSwitch(conn, context.overrideMapping(node::getTrackById)));
            }
        }
        return conn;
    }
}
