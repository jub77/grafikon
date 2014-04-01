package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.FNNode;

/**
 * Freight node.
 *
 * @author cz2b10k5
 */
@XmlType(propOrder = { "train", "x", "y", "attributes" })
public class LSFreightNode {

    private String train;
    private int x;
    private int y;
    private LSAttributes attributes;

    public LSFreightNode() {
    }

    public LSFreightNode(FNNode node) {
        this.train = node.getTrain().getId();
        this.x = node.getLocation().getX();
        this.y = node.getLocation().getY();
        this.attributes = new LSAttributes(node);
    }

    public String getTrain() {
        return train;
    }

    public void setTrain(String train) {
        this.train = train;
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

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }
}
