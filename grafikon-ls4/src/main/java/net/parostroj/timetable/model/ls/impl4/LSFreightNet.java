package net.parostroj.timetable.model.ls.impl4;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.*;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.freight.ConnectionStrategyType;

/**
 * Class for storing net.
 *
 * @author jub
 */
@XmlRootElement(name = "freightNet")
@XmlType(propOrder = {"id", "attributes", "connections"})
public class LSFreightNet {

    private String id;
    private LSAttributes attributes;
    private List<LSFreightConnection> connections;

    public LSFreightNet() {
    }

    public LSFreightNet(FreightNet net) {
        this.attributes = new LSAttributes(net.getAttributes());
        this.connections = new ArrayList<>();
        for (FNConnection connection : net.getConnections()) {
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
    @XmlElement(name = "connection")
    public List<LSFreightConnection> getConnections() {
        if (connections == null) {
            connections = new ArrayList<>();
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

    public void createFreightNet(LSContext context) {
        FreightNet net = context.getDiagram().getFreightNet();
        net.getAttributes().add(this.getAttributes().createAttributes(context));
        if (net.getConnectionStrategyType() == ConnectionStrategyType.CUSTOM_CONNECTION_FILTER
                && net.getAttributes().get(FreightNet.ATTR_CUSTOM_CONNECTION_FILTER) == null) {
            net.setConnectionStrategyType(ConnectionStrategyType.REGION);
        }
    }
}
