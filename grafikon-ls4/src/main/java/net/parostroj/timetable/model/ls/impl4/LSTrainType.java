package net.parostroj.timetable.model.ls.impl4;

import net.parostroj.timetable.model.TrainType;
import java.awt.Color;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.Conversions;

/**
 * Train type storage class.
 *
 * @author jub
 */
@XmlRootElement(name = "train_type")
@XmlType(propOrder = {"id", "abbr", "desc", "color", "categoryId", "platform", "trainNameTemplate", "trainCompleteNameTemplate"})
public class LSTrainType {

    private String id;
    private String abbr;
    private String desc;
    private String color;
    private String categoryId;
    private boolean platform;
    private LSTextTemplate trainNameTemplate;
    private LSTextTemplate trainCompleteNameTemplate;

    public LSTrainType() {
    }

    public LSTrainType(TrainType type) {
        this.id = type.getId();
        this.abbr = type.getAbbr();
        this.desc = type.getDesc();
        this.platform = type.isPlatform();
        Color c = type.getColor();
        this.color = Conversions.convertColorToText(c);
        this.categoryId = type.getCategory().getId();
        this.trainNameTemplate = type.getTrainNameTemplate() != null ?
            new LSTextTemplate(type.getTrainNameTemplate()) : null;
        this.trainCompleteNameTemplate = type.getTrainCompleteNameTemplate() != null ?
            new LSTextTemplate(type.getTrainCompleteNameTemplate()) : null;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    @XmlElement(name = "category_id")
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isPlatform() {
        return platform;
    }

    public void setPlatform(boolean platform) {
        this.platform = platform;
    }

    @XmlElement(name = "name_template")
    public LSTextTemplate getTrainNameTemplate() {
        return trainNameTemplate;
    }

    public void setTrainNameTemplate(LSTextTemplate trainNameTemplate) {
        this.trainNameTemplate = trainNameTemplate;
    }

    @XmlElement(name = "complete_name_template")
    public LSTextTemplate getTrainCompleteNameTemplate() {
        return trainCompleteNameTemplate;
    }

    public void setTrainCompleteNameTemplate(LSTextTemplate trainCompleteNameTemplate) {
        this.trainCompleteNameTemplate = trainCompleteNameTemplate;
    }
    
    public TrainType createTrainType(TrainDiagram diagram) {
        TrainType type = diagram.createTrainType(id);
        type.setAbbr(abbr);
        type.setColor(Conversions.convertTextToColor(color));
        type.setDesc(desc);
        type.setPlatform(platform);
        type.setCategory(diagram.getPenaltyTable().getTrainTypeCategoryById(categoryId));
        type.setTrainCompleteNameTemplate(trainCompleteNameTemplate != null ?
            trainCompleteNameTemplate.createTextTemplate() : null);
        type.setTrainNameTemplate(trainNameTemplate != null ?
            trainNameTemplate.createTextTemplate() : null);
        return type;
    }
}
