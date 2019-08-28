package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Class for storing nodes.
 *
 * @author jub
 */
@XmlRootElement(name = "node")
@XmlType(name = "node", propOrder = {"id", "name", "abbr", "type", "attributes", "x", "y", "tracks"})
public class LSNode {

    private String id;
    // deprecated
    private String name;
    // deprecated
    private String abbr;
    private LSAttributes attributes;
    private List<LSNodeTrack> tracks;
    // deprecated
    private String type;
    // deprecated
    private Integer x;
    // deprecated
    private Integer y;

    private int version;

    public LSNode(Node node) {
        this.id = node.getId();
        this.attributes = new LSAttributes(node.getAttributes());
        this.tracks = new LinkedList<>();
        for (NodeTrack track : node.getTracks()) {
            this.tracks.add(new LSNodeTrack(track));
        }
        this.version = 1;
    }

    public LSNode() {
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElementWrapper
    @XmlElement(name = "track")
    public List<LSNodeTrack> getTracks() {
        return tracks;
    }

    public void setTracks(List<LSNodeTrack> tracks) {
        this.tracks = tracks;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    @XmlAttribute
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Node createNode(PartFactory partFactory, Function<String, ObjectWithId> mapping) throws LSException {
        Node node = partFactory.createNode(id);
        // if version < 1 - use deprecated properties
        if (version < 1) {
            node.setType(NodeType.fromString(type));
            node.setName(name);
            node.setAbbr(abbr);
            node.setLocation(new Location(x, y));
        }
        node.getAttributes().add(attributes.createAttributes(mapping));
        // tracks
        if (this.tracks != null) {
            for (LSNodeTrack track : this.tracks) {
                node.getTracks().add(track.createNodeTrack(node, mapping));
            }
        }
        return node;
    }
}
