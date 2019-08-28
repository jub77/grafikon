package net.parostroj.timetable.model.ls.impl4;

import java.util.function.Function;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.Track;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Abstract class for tracks storage.
 *
 * @author jub
 */
@XmlType(propOrder = {"id", "number", "attributes"})
abstract public class LSTrack {

    private String id;
    // deprecated
    private String number;
    private LSAttributes attributes;

    private int version;

    public LSTrack(Track track) {
        this.id = track.getId();
        this.attributes = new LSAttributes(track.getAttributes());
        this.version = 1;
    }

    public LSTrack() {
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @XmlAttribute
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    protected void addValuesTrack(Function<String, ObjectWithId> mapping, Track track) throws LSException {
        track.getAttributes().add(attributes.createAttributes(mapping));
        if (version == 0) {
            track.setNumber(number);
        }
    }
}
