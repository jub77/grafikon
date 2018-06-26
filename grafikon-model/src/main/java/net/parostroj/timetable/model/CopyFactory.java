package net.parostroj.timetable.model;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.utils.IdGenerator;

public class CopyFactory {

    private static final Logger log = LoggerFactory.getLogger(CopyFactory.class);

    private final PartFactory partFactory;

    public CopyFactory(PartFactory partFactory) {
        this.partFactory = partFactory;
    }

    public OutputTemplate copy(OutputTemplate template, String id) {
        OutputTemplate copy = partFactory.createOutputTemplate(id);
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
        Node copy = partFactory.createNode(id, node.getType(), node.getName(), node.getAbbr());

        copy.getAttributes().add(node.getAttributes());

        // copy tracks
        for (NodeTrack track : node.getTracks()) {
            NodeTrack copyTrack = new NodeTrack(IdGenerator.getInstance().getId(), track.getNumber());
            copyTrack.setPlatform(track.isPlatform());
            copyTrack.getAttributes().add(track.getAttributes());
            copy.getTracks().add(copyTrack);
        }

        // copy location
        copy.setLocation(new Location(node.getLocation().getX(), node.getLocation().getY()));

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

    public TrainType copy(TrainType trainType, String id) {
        TrainType copy = partFactory.createTrainType(id);
        copy.setAbbr(trainType.getAbbr());
        copy.setDesc(trainType.getDesc());
        copy.setColor(trainType.getColor());
        copy.setPlatform(trainType.isPlatform());
        copy.setTrainNameTemplate(trainType.getTrainNameTemplate());
        copy.setTrainCompleteNameTemplate(trainType.getTrainCompleteNameTemplate());
        copy.setCategory(trainType.getCategory());
        copy.getAttributes().add(trainType.getAttributes());
        return copy;
    }

    public LineClass copy(LineClass lineClass, String id) {
        return new LineClass(id, lineClass.getName());
    }

    public TrainTypeCategory copy(TrainTypeCategory category, String id) {
        TrainTypeCategory copy = new TrainTypeCategory(id);
        copy.setKey(category.getKey());
        copy.setName(category.getName());

        for (PenaltyTableRow row : category.getPenaltyRows()) {
            copy.addRow(copy.createPenaltyTableRow(row.getSpeed(), row.getAcceleration(), row.getDeceleration()));
        }

        return copy;
    }
}
