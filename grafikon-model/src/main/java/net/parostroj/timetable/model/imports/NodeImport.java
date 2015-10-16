package net.parostroj.timetable.model.imports;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrainDiagram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Import of nodes.
 *
 * @author jub
 */
class NodeImport extends Import {

    private static final Logger log = LoggerFactory.getLogger(NodeImport.class);

    public NodeImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch importMatch) {
        super(diagram, libraryDiagram, importMatch);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId o) {
        // check class
        if (!(o instanceof Node))
            return null;
        Node importedNode = (Node)o;

        // check if the train already exist
        Node checkedNode = this.getNode(importedNode);
        if (checkedNode != null) {
            String message = "station already exists";
            this.addError(importedNode, message);
            log.debug("{}: {}", message, checkedNode);
            return null;
        }

        // create new node
        Node node = getDiagram().getPartFactory().createNode(this.getId(importedNode), importedNode.getType(),
                importedNode.getName(), importedNode.getAbbr());
        node.getAttributes().add(this.importAttributes(importedNode.getAttributes()));
        node.setLocation(importedNode.getLocation());
        // tracks
        for (NodeTrack importedTrack : importedNode.getTracks()) {
            NodeTrack track = new NodeTrack(this.getId(importedTrack), importedTrack.getNumber());
            track.setPlatform(importedTrack.isPlatform());
            track.getAttributes().add(this.importAttributes(importedTrack.getAttributes()));
            node.addTrack(track);
        }

        // add to diagram
        this.getDiagram().getNet().addNode(node);
        this.addImportedObject(node);
        log.trace("Successfully imported node: " + node);
        return node;
    }
}
