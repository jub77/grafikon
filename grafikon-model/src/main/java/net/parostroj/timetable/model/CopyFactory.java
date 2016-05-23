package net.parostroj.timetable.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyFactory {

    private static final Logger log = LoggerFactory.getLogger(CopyFactory.class);

    private final TrainDiagram diagram;

    private CopyFactory(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    public static CopyFactory getInstance() {
        return new CopyFactory(null);
    }

    public static CopyFactory getInstance(TrainDiagram diagram) {
        return new CopyFactory(diagram);
    }

    public OutputTemplate copy(OutputTemplate template) {
        OutputTemplate copy = diagram == null ? new OutputTemplate(template.getId(), null) :
            diagram.getPartFactory().createOutputTemplate(template.getId());

        try {
            if (template.getTemplate() != null) {
                copy.setTemplate(TextTemplate.createTextTemplate(template.getTemplate().getTemplate(),
                        template.getTemplate().getLanguage()));
            }
        } catch (GrafikonException e) {
            log.error("Error creating copy of template.", e);
        }
        try {
            if (template.getScript() != null) {
                copy.setScript(Script.createScript(template.getScript().getSourceCode(),
                        template.getScript().getLanguage()));
            }
        } catch (GrafikonException e) {
            log.error("Error creating script.", e);
        }
        copy.getAttachments().addAll(template.getAttachments());
        copy.setName(template.getName());
        copy.getAttributes().add(template.getAttributes());

        return copy;
    }

    public Node copy(Node node) {
        Node copy = diagram == null ? new Node(node.getId(), null, node.getType(), node.getName(), node.getAbbr()) :
            diagram.getPartFactory().createNode(node.getId(), node.getType(), node.getName(), node.getAbbr());

        node.getAttributes().add(node.getAttributes());

        // copy tracks
        for (NodeTrack track : node.getTracks()) {
            NodeTrack copyTrack = new NodeTrack(track.getId(), track.getNumber());
            copyTrack.setPlatform(track.isPlatform());
            copyTrack.getAttributes().add(track.getAttributes());
            copy.addTrack(copyTrack);
        }

        return copy;
    }
}
