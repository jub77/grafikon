package net.parostroj.timetable.gui.dialogs;

import java.util.logging.Logger;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Import of nodes.
 *
 * @author jub
 */
class NodeImport extends Import {

    private static final Logger LOG = Logger.getLogger(NodeImport.class.getName());

    public NodeImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch importMatch) {
        super(diagram, libraryDiagram, importMatch);
    }

    @Override
    protected void importObjectImpl(Object o) {
        // check class
        if (!(o instanceof Node))
            return;
        Node importedNode = (Node)o;

        // check if the train already exist
        Node checkedNode = this.getNode(importedNode);
        if (checkedNode != null) {
            String message = "Station already exists: " + checkedNode;
            this.addError(importedNode, message);
            LOG.finer(message);
            return;
        }

        // create new node
        Node node = getDiagram().createNode(this.getId(importedNode), importedNode.getType(), importedNode.getName(), importedNode.getAbbr());
        node.setAttributes(importedNode.getAttributes());
        node.setPositionX(importedNode.getPositionX());
        node.setPositionY(importedNode.getPositionY());
        // tracks
        for (NodeTrack importedTrack : importedNode.getTracks()) {
            NodeTrack track = new NodeTrack(this.getId(importedTrack), importedTrack.getNumber());
            track.setPlatform(importedTrack.isPlatform());
            track.setAttributes(importedTrack.getAttributes());
            node.addTrack(track);
        }

        // add to diagram
        this.getDiagram().getNet().addNode(node);
        this.addImportedObject(node);
        LOG.finer("Successfully imported node: " + node);
    }
}
