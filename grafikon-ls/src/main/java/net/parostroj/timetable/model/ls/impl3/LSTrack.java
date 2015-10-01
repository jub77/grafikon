package net.parostroj.timetable.model.ls.impl3;

import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.Track;

/**
 * Abstract class for tracks storage.
 *
 * @author jub
 */
@XmlType(propOrder = {"id", "number", "attributes"})
abstract public class LSTrack {

    private String id;
    private String number;
    private LSAttributes attributes;

    public LSTrack(Track track) {
        this.id = track.getId();
        this.number = track.getNumber();
        this.attributes = new LSAttributes(track.getAttributes());
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

    protected void addValuesTrack(Track track) {
        track.getAttributes().add(attributes.createAttributes());
        track.setNumber(number);
    }
}
