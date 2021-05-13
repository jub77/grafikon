package net.parostroj.timetable.model;

import java.awt.Color;

import net.parostroj.timetable.model.Train.NameType;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Train type.
 *
 * @author jub
 */
public class TrainType implements ObjectWithId, Visitable, AttributesHolder, Observable, TrainDiagramPart {

    public static final String ATTR_SHOW_WEIGHT_INFO = "weight.info";
    public static final String ATTR_ABBR = "abbr";
    public static final String ATTR_COLOR = "color";
    public static final String ATTR_DESC = "desc";
    public static final String ATTR_PLATFORM = "platform";
    public static final String ATTR_TRAIN_NAME_TEMPLATE = "trainNameTemplate";
    public static final String ATTR_TRAIN_COMPLETE_NAME_TEMPLATE = "trainCompleteNameTemplate";
    public static final String ATTR_CATEGORY = "category";
    public static final String ATTR_LINE_TYPE = "line.type";
    public static final String ATTR_LINE_WIDTH = "line.width";
    public static final String ATTR_LINE_LENGTH = "line.length";

    /** Train diagram. */
    private final TrainDiagram diagram;
    /** Id. */
    private final String id;
    /** Color for GT. */
    private Color color;
    /** Category. */
    private TrainTypeCategory category;
    /** Needs platform in the station. */
    private boolean platform;
    /** Template for train name. */
    private TextTemplate trainNameTemplate;
    /** Template for complete train name. */
    private TextTemplate trainCompleteNameTemplate;
    /** Listener support. */
    private final ListenerSupport listenerSupport;
    /** Attributes */
    private final Attributes attributes;

    /**
     * creates instance.
     *
     * @param id id
     */
    protected TrainType(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        listenerSupport = new ListenerSupport();
        attributes = new Attributes(
                (attrs, change) -> listenerSupport.fireEvent(new Event(TrainType.this, change)));
    }

    /**
     * @return the id
     */
    @Override
    public String getId() {
        return id;
    }

    @Override
    public TrainDiagram getDiagram() {
        return diagram;
    }

    /**
     * @return the abbreviation (default string of localized abbreviation - backward compatibility)
     */
    public String getDefaultAbbr() {
        LocalizedString abbr = attributes.get(ATTR_ABBR, LocalizedString.class);
        return abbr != null ? abbr.getDefaultString() : null;
    }


    /**
     * @return localized abbreviation
     */
    public LocalizedString getAbbr() {
        return attributes.get(ATTR_ABBR, LocalizedString.class);
    }

    /**
     * @param abbr localized abbreviation to be set
     */
    public void setAbbr(LocalizedString abbr) {
        attributes.setRemove(ATTR_ABBR, abbr);
    }

    /**
     * @return color for GT
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to be set
     */
    public void setColor(Color color) {
        if (!ObjectsUtil.compareWithNull(color, this.color)) {
            Color oldColor = this.color;
            this.color = color;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_COLOR, oldColor, color)));
        }
    }

    /**
     * @return description of the type
     */
    public LocalizedString getDesc() {
        return attributes.get(ATTR_DESC, LocalizedString.class);
    }

    /**
     * @param desc description to set
     */
    public void setDesc(LocalizedString desc) {
        attributes.setRemove(ATTR_DESC, desc);
    }

    /**
     * @return if the type needs platform in the station
     */
    public boolean isPlatform() {
        return platform;
    }

    /**
     * @param platform sets if the type needs the platform in the station
     */
    public void setPlatform(boolean platform) {
        if (platform != this.platform) {
            boolean oldPlatform = this.platform;
            this.platform = platform;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_PLATFORM, oldPlatform,
                    platform)));
        }
    }

    /**
     * @return category of train type
     */
    public TrainTypeCategory getCategory() {
        return category;
    }

    /**
     * @param category sets category of train type
     */
    public void setCategory(TrainTypeCategory category) {
        if (!ObjectsUtil.compareWithNull(category, this.category)) {
            TrainTypeCategory oldCategory = this.category;
            this.category = category;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_CATEGORY, oldCategory,
                    category)));
        }
    }

    /**
     * @return train name template
     */
    public TextTemplate getTrainNameTemplate() {
        return trainNameTemplate;
    }

    /**
     * @param trainNameTemplate sets train name template
     */
    public void setTrainNameTemplate(TextTemplate trainNameTemplate) {
        if (!ObjectsUtil.compareWithNull(trainNameTemplate, this.trainNameTemplate)) {
            TextTemplate oldTemplate = this.trainNameTemplate;
            this.trainNameTemplate = trainNameTemplate;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_TRAIN_NAME_TEMPLATE,
                    oldTemplate, trainNameTemplate)));
        }
    }

    /**
     * @return train complete name template
     */
    public TextTemplate getTrainCompleteNameTemplate() {
        return trainCompleteNameTemplate;
    }

    /**
     * @param trainCompleteNameTemplate sets template with complete train name
     */
    public void setTrainCompleteNameTemplate(TextTemplate trainCompleteNameTemplate) {
        if (!ObjectsUtil.compareWithNull(trainCompleteNameTemplate, this.trainCompleteNameTemplate)) {
            TextTemplate oldTemplate = this.trainCompleteNameTemplate;
            this.trainCompleteNameTemplate = trainCompleteNameTemplate;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(
                    ATTR_TRAIN_COMPLETE_NAME_TEMPLATE, oldTemplate, trainCompleteNameTemplate)));
        }
    }

    public TextTemplate getNameTemplate(NameType nameType) {
        if (nameType == NameType.COMPLETE) {
            return (trainCompleteNameTemplate == null) ?
                            getDiagram().getTrainsData().getTrainCompleteNameTemplate() :
                                trainCompleteNameTemplate;
        } else {
            return (trainNameTemplate == null) ?
                            getDiagram().getTrainsData().getTrainNameTemplate() :
                                trainNameTemplate;
        }
    }

    /**
     * adds listener to train.
     * @param listener listener
     */
    @Override
    public void addListener(Listener listener) {
        listenerSupport.addListener(listener);
    }

    /**
     * removes listener from train.
     * @param listener listener
     */
    @Override
    public void removeListener(Listener listener) {
        listenerSupport.removeListener(listener);
    }

    public void removeAllListeners() {
        listenerSupport.removeAllListeners();
    }

    @Override
    public String toString() {
        LocalizedString desc = getDesc();
        return getDefaultAbbr() + " - " + (desc == null ? "<none>" : desc.getDefaultString());
    }

    /**
     * accepts visitor.
     *
     * @param visitor visitor
     */
    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    public double getLineWidth() {
        return attributes.get(ATTR_LINE_WIDTH, Double.class, 1.0d);
    }

    public double getLineLength() {
        return attributes.get(ATTR_LINE_LENGTH, Double.class, 1.0d);
    }

    public LineType getLineType() {
        return LineType.valueOf(
                attributes.get(ATTR_LINE_TYPE, Integer.class, LineType.SOLID.getValue()));
    }
}
