package net.parostroj.timetable.model.imports;

import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrackConnector;
import net.parostroj.timetable.model.TrackConnectorSwitch;
import net.parostroj.timetable.model.TrainDiagram;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Import of nodes.
 *
 * @author jub
 */
public class NodeImport extends Import {

    private static final Logger log = LoggerFactory.getLogger(NodeImport.class);

    public NodeImport(TrainDiagram diagram, ImportMatch importMatch, boolean overwrite) {
        super(diagram, importMatch, overwrite);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId o) {
        // check class
        if (!(o instanceof Node importedNode)) {
            return null;
        }

        // check if the train already exist
        Node checkedNode = this.getNode(importedNode);
        if (checkedNode != null) {
            if (overwrite) {
                this.getDiagram().getNet().removeNode(checkedNode);
            } else {
                String message = "station already exists";
                this.addError(importedNode, message);
                log.debug("{}: {}", message, checkedNode);
                return null;
            }
        }

        // create new node
        Node node = getDiagram().getPartFactory().createNode(this.getId(importedNode));
        node.getAttributes().add(this.importAttributes(importedNode.getAttributes()));
        node.setLocation(importedNode.getLocation());
        // tracks
        Map<String, NodeTrack> trackMap = new HashMap<>();
        for (NodeTrack importedTrack : importedNode.getTracks()) {
            NodeTrack track = new NodeTrack(this.getId(importedTrack), node, importedTrack.getNumber());
            trackMap.put(importedTrack.getId(), track);
            track.setPlatform(importedTrack.isPlatform());
            track.getAttributes().add(this.importAttributes(importedTrack.getAttributes()));
            node.getTracks().add(track);
        }
        // connectors
        for (TrackConnector importedConnector : importedNode.getConnectors()) {
            TrackConnector connector = this.getDiagram().getPartFactory()
                    .createConnector(this.getId(importedConnector), node);
            Attributes connAttr = new Attributes(importedConnector.getAttributes());
            // line track is not copied
            connAttr.remove(TrackConnector.ATTR_LINE_TRACK);
            connector.getAttributes().add(this.importAttributes(connAttr));
            node.getConnectors().add(connector);
            // switches
            for (TrackConnectorSwitch importedSw : importedConnector.getSwitches()) {
                TrackConnectorSwitch sw = connector.createSwitch(this.getId(importedSw));
                String switchTrackId = importedSw.getNodeTrack().getId();
                Attributes swAttr = new Attributes(importedSw.getAttributes());
                // node track is done after wards
                swAttr.remove(TrackConnectorSwitch.ATTR_TRACK);
                sw.getAttributes().add(this.importAttributes(swAttr));
                sw.setNodeTrack(trackMap.get(switchTrackId));
                connector.getSwitches().add(sw);
            }
        }

        // add to diagram
        this.getDiagram().getNet().addNode(node);
        this.addImportedObject(node);
        log.trace("Successfully imported node: {}", node);
        return node;
    }
}
