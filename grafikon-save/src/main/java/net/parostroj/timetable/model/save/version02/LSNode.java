package net.parostroj.timetable.model.save.version02;

import java.util.UUID;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;

public class LSNode {

    private int id;

    private String uuid;

    private String name;

    private String abbr;

    private LSNodeTrack[] nodeTracks;

    private String nodeType;

    private LSAttributes attributes;

    private int x;

    private int y;

    public LSNode() {
    }

    public LSNode(Node node, LSTransformationData data) {
        id = data.getId();
        data.addObjectWithId(node, id);
        nodeTracks = new LSNodeTrack[node.getTracks().size()];
        int i = 0;
        for (NodeTrack nodeTrack : node.getTracks()) {
            LSNodeTrack lNodeTrack = new LSNodeTrack(nodeTrack, data);
            nodeTracks[i++] = lNodeTrack;
        }
        name = node.getName();
        abbr = node.getAbbr();
        attributes = new LSAttributes(node.getAttributes(), data);
        nodeType = node.getType().name();
        x = node.getLocation().getX();
        y = node.getLocation().getY();
        uuid = node.getId();
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the node tracks
     */
    public LSNodeTrack[] getNodeTracks() {
        return nodeTracks;
    }

    /**
     * @param nodeTracks the node tracks to set
     */
    public void setNodeTracks(LSNodeTrack[] nodeTracks) {
        this.nodeTracks = nodeTracks;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void visit(LSVisitor visitor) {
        visitor.visit(this);

        // visit all node tracks
        for (LSNodeTrack lsNodeTrack : nodeTracks) {
            lsNodeTrack.visit(visitor);
        }
    }
}
