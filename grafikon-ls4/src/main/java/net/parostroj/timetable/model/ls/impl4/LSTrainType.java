package net.parostroj.timetable.model.ls.impl4;

import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainTypeCategory;

import java.awt.Color;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.utils.Conversions;

/**
 * Train type storage class.
 *
 * @author jub
 */
@XmlRootElement(name = "train_type")
@XmlType(propOrder = {"id", "abbr", "desc", "color", "categoryId", "platform", "trainNameTemplate",
        "trainCompleteNameTemplate", "attributes"})
public class LSTrainType {

    private String id;
    // not used anymore for serialization - backward compatibility
    private String abbr;
    // not used anymore for serialization
    private String desc;
    private String color;
    private String categoryId;
    private boolean platform;
    private LSTextTemplate trainNameTemplate;
    private LSTextTemplate trainCompleteNameTemplate;
    private LSAttributes attributes;

    public LSTrainType() {
    }

    public LSTrainType(TrainType type) {
        this.id = type.getId();
        this.platform = type.isPlatform();
        Color c = type.getColor();
        this.color = Conversions.convertColorToText(c);
        this.categoryId = type.getCategory() != null ? type.getCategory().getId() : null;
        this.trainNameTemplate = type.getTrainNameTemplate() != null ?
            new LSTextTemplate(type.getTrainNameTemplate()) : null;
        this.trainCompleteNameTemplate = type.getTrainCompleteNameTemplate() != null ?
            new LSTextTemplate(type.getTrainCompleteNameTemplate()) : null;
        this.attributes = new LSAttributes(type.getAttributes());
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

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public TrainType createTrainType(LSContext context) {
        TrainType type = context.getPartFactory().createTrainType(id);
        if (abbr != null) {
            type.setAbbr(LocalizedString.fromString(abbr));
        }
        type.setColor(Conversions.convertTextToColor(color));
        if (desc != null) {
            type.setDesc(LocalizedString.fromString(desc));
        }
        type.setPlatform(platform);
        if (trainNameTemplate != null) {
            type.setTrainNameTemplate(trainNameTemplate.createTextTemplate(context));
        }
        if (trainCompleteNameTemplate != null) {
            type.setTrainCompleteNameTemplate(trainCompleteNameTemplate.createTextTemplate(context));
        }
        type.setCategory((TrainTypeCategory) context.mapId(categoryId));
        if (attributes != null) {
            type.getAttributes().add(attributes.createAttributes(context));
        }
        return type;
    }
}
