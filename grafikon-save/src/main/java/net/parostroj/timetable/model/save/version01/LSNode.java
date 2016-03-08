package net.parostroj.timetable.model.save.version01;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;

public class LSNode {

    private int id;

    private String name;

    private String abbr;

    private LSNodeTrack[] nodeTracks;

    private String nodeType;

    private String interlockingPlant;

    private int x;

    private int y;

    public LSNode() {
    }

    public LSNode(Node node, LSTransformationData data) {
        id = data.getId();
        data.addObjectWithId(node, id);
        nodeTracks = new LSNodeTrack[node.getTracks().size()];
        int i = 0;
        for (NodeTrack stationTrack : node.getTracks()) {
            LSNodeTrack lStationTrack = new LSNodeTrack(stationTrack, data);
            nodeTracks[i++] = lStationTrack;
        }
        name = node.getName();
        abbr = node.getAbbr();
        interlockingPlant = node.getAttribute(Node.ATTR_INTERLOCKING_PLANT, String.class);
        nodeType = node.getType().name();
        x = node.getLocation().getX();
        y = node.getLocation().getY();
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
     * @return the stationTracks
     */
    public LSNodeTrack[] getNodeTracks() {
        return nodeTracks;
    }

    /**
     * @param nodeTracks the stationTracks to set
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

    public String getInterlockingPlant() {
        return interlockingPlant;
    }

    public void setInterlockingPlant(String interlockingPlant) {
        this.interlockingPlant = interlockingPlant;
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

    public void visit(LSVisitor visitor) {
        visitor.visit(this);

        // visit all station tracks
        for (LSNodeTrack lsStationTrack : nodeTracks) {
            lsStationTrack.visit(visitor);
        }
    }
}
