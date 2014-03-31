package net.parostroj.timetable.model.ls.impl4;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

import net.parostroj.timetable.model.FreightNet;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Class for storing net.
 *
 * @author jub
 */
@XmlRootElement(name = "freightNet")
@XmlType(propOrder = {"id", "attributes", "nodes", "connections"})
public class LSFreightNet {

    private String id;
    private LSAttributes attributes;
    private List<LSFreightNode> nodes;
    private List<LSFreightConnection> connections;

    public LSFreightNet() {
    }

    public LSFreightNet(FreightNet net) {
        this.id = net.getId();
        this.attributes = new LSAttributes(net.getAttributes());
        this.nodes = new ArrayList<LSFreightNode>();
        this.connections = new ArrayList<LSFreightConnection>();
        for (FreightNet.FreightNetNode node : net.getNodes()) {
            this.nodes.add(new LSFreightNode(node));
        }
        for (FreightNet.FreightNetConnection connection : net.getConnections()) {
            this.connections.add(new LSFreightConnection(connection));
        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElementWrapper
    @XmlElement(name = "node")
    public List<LSFreightNode> getNodes() {
        if (nodes == null) {
            nodes = new ArrayList<LSFreightNode>();
        }
        return nodes;
    }

    public void setNodes(List<LSFreightNode> nodes) {
        this.nodes = nodes;
    }

    @XmlElementWrapper
    @XmlElement(name = "connection")
    public List<LSFreightConnection> getConnections() {
        if (connections == null) {
            connections = new ArrayList<LSFreightConnection>();
        }
        return connections;
    }

    public void setConnections(List<LSFreightConnection> connections) {
        this.connections = connections;
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public FreightNet createFreightNet(TrainDiagram diagram) throws LSException {
        FreightNet net = new FreightNet(this.getId());
        net.setAttributes(this.getAttributes().createAttributes(diagram));
        return net;
    }
}
