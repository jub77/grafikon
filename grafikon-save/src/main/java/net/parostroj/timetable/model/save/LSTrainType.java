package net.parostroj.timetable.model.save;

import net.parostroj.timetable.model.TrainType;
import java.awt.Color;
import java.util.UUID;
import net.parostroj.timetable.model.Language;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainTypeCategory;
import net.parostroj.timetable.utils.Conversions;

/**
 * Train type storage class.
 *
 * @author jub
 */
public class LSTrainType {
    private String key;
    
    private String abbr;
    
    private String desc;
    
    private String color;
    
    private String braking;
    
    private boolean platform;
    
    private String trainNameTemplate;
    
    private String trainCompleteNameTemplate;
    
    public LSTrainType() {}
    
    public LSTrainType(TrainType type, String key) {
        this.key = key;
        this.abbr = type.getAbbr();
        this.desc = type.getDesc();
        this.platform = type.isPlatform();
        Color c = type.getColor();
        this.color = Conversions.convertColorToText(c);
        this.trainNameTemplate = type.getTrainNameTemplate() != null ?
            type.getTrainNameTemplate().getTemplate() : null;
        this.trainCompleteNameTemplate = type.getTrainCompleteNameTemplate() != null ?
            type.getTrainCompleteNameTemplate().getTemplate() : null;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getBraking() {
        return braking;
    }

    public void setBraking(String braking) {
        this.braking = braking;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isPlatform() {
        return platform;
    }

    public void setPlatform(boolean platform) {
        this.platform = platform;
    }

    public String getTrainNameTemplate() {
        return trainNameTemplate;
    }

    public void setTrainNameTemplate(String trainNameTemplate) {
        this.trainNameTemplate = trainNameTemplate;
    }

    public String getTrainCompleteNameTemplate() {
        return trainCompleteNameTemplate;
    }

    public void setTrainCompleteNameTemplate(String trainCompleteNameTemplate) {
        this.trainCompleteNameTemplate = trainCompleteNameTemplate;
    }

    public TrainType convertToTrainType(TrainDiagram diagram) {
        TrainType type =diagram.createTrainType(UUID.randomUUID().toString());
        type.setAbbr(this.abbr);
        type.setColor(Conversions.convertTextToColor(this.color));
        type.setDesc(this.desc);
        type.setPlatform(this.platform);
        type.setCategory(this.getCategory(diagram));
        type.setTrainNameTemplate(this.trainNameTemplate != null ?
            TextTemplate.createTextTemplate(this.trainNameTemplate, Language.MVEL) : null);
        type.setTrainCompleteNameTemplate(this.trainCompleteNameTemplate != null ?
            TextTemplate.createTextTemplate(this.trainCompleteNameTemplate, Language.MVEL) : null);
        return type;
    }

    private TrainTypeCategory getCategory(TrainDiagram diagram) {
        for (TrainTypeCategory cat : diagram.getPenaltyTable().getTrainTypeCategories()) {
            if (cat.getKey().equals(braking.toLowerCase()))
                return cat;
        }
        return null;
    }
}
