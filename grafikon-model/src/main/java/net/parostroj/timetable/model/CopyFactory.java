package net.parostroj.timetable.model;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.utils.IdGenerator;

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

    public OutputTemplate copy(OutputTemplate template, String id) {
        OutputTemplate copy = diagram == null ? new OutputTemplate(id, null) :
            diagram.getPartFactory().createOutputTemplate(id);

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

    public Node copy(Node node, String id) {
        Node copy = diagram == null ? new Node(id, null, node.getType(), node.getName(), node.getAbbr()) :
            diagram.getPartFactory().createNode(id, node.getType(), node.getName(), node.getAbbr());

        copy.getAttributes().add(node.getAttributes());

        // copy tracks
        for (NodeTrack track : node.getTracks()) {
            NodeTrack copyTrack = new NodeTrack(IdGenerator.getInstance().getId(), track.getNumber());
            copyTrack.setPlatform(track.isPlatform());
            copyTrack.getAttributes().add(track.getAttributes());
            copy.addTrack(copyTrack);
        }

        return copy;
    }

    public EngineClass copy(EngineClass engineClass, String id) {
        EngineClass copy = new EngineClass(id, engineClass.getName());
        // copy all data
        for (WeightTableRow row : engineClass.getWeightTable()) {
            WeightTableRow newRow = copy.createWeightTableRow(row.getSpeed());
            for (Map.Entry<LineClass, Integer> entry : row.getWeights().entrySet()) {
                newRow.setWeightInfo(entry.getKey(), entry.getValue());
            }
            copy.addWeightTableRow(newRow);
        }
        return copy;
    }
}
