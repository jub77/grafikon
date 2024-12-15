package net.parostroj.timetable.model.ls.impl4;

import jakarta.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.TrackConnector;
import net.parostroj.timetable.model.TrackConnectorSwitch;
import net.parostroj.timetable.model.ls.LSException;

/**
 * @author jub
 */
@XmlType(propOrder = { "id", "attributes" })
public class LSTrackConnectorSwitch {

    private String id;
    private LSAttributes attributes;

    public LSTrackConnectorSwitch() {
    }

    public LSTrackConnectorSwitch(TrackConnectorSwitch sw) {
        this.id = sw.getId();
        this.attributes = new LSAttributes(sw.getAttributes());
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

    public TrackConnectorSwitch createSwitch(TrackConnector connector, LSContext context) throws LSException {
        TrackConnectorSwitch sw = connector.createSwitch(id);
        if (this.attributes != null) {
            sw.getAttributes().add(this.attributes.createAttributes(context));
        }
        return sw;
    }
}
