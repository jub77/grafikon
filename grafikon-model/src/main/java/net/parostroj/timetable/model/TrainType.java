package net.parostroj.timetable.model;

import java.awt.Color;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Train type.
 *
 * @author jub
 */
public class TrainType implements ObjectWithId, Visitable, AttributesHolder, TrainTypeAttributes {
    /** Train diagram. */
    private final TrainDiagram diagram;
    /** Id. */
    private final String id;
    /** Abbreviation of the type. */
    private String abbr;
    /** Description of the type. */
    private String desc;
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
    private final GTListenerSupport<TrainTypeListener, TrainTypeEvent> listenerSupport;
    /** Attributes */
    private Attributes attributes;
    private AttributesListener attributesListener;

    /**
     * creates instance.
     *
     * @param id id
     */
    protected TrainType(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        listenerSupport = new GTListenerSupport<TrainTypeListener, TrainTypeEvent>(new GTEventSender<TrainTypeListener, TrainTypeEvent>() {

            @Override
            public void fireEvent(TrainTypeListener listener, TrainTypeEvent event) {
                listener.trainTypeChanged(event);
            }
        });
        this.setAttributes(new Attributes());
    }

    /**
     * @return the id
     */
    @Override
    public String getId() {
        return id;
    }

    public TrainDiagram getTrainDiagram() {
        return diagram;
    }

    /**
     * @return the abbreviation
     */
    public String getAbbr() {
        return abbr;
    }

    /**
     * @param abbr the abbreviation to set
     */
    public void setAbbr(String abbr) {
        if (!ObjectsUtil.compareWithNull(abbr, this.abbr)) {
            String oldAbbr = this.abbr;
            this.abbr = abbr;
            this.listenerSupport.fireEvent(new TrainTypeEvent(this, new AttributeChange(ATTR_ABBR, oldAbbr, abbr)));
        }
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
            this.listenerSupport.fireEvent(new TrainTypeEvent(this, new AttributeChange(ATTR_COLOR, oldColor, color)));
        }
    }

    /**
     * @return description of the type
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc description to set
     */
    public void setDesc(String desc) {
        if (!ObjectsUtil.compareWithNull(desc, this.desc)) {
            String oldDesc = this.desc;
            this.desc = desc;
            this.listenerSupport.fireEvent(new TrainTypeEvent(this, new AttributeChange(ATTR_DESC, oldDesc, desc)));
        }
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
            this.listenerSupport.fireEvent(new TrainTypeEvent(this, new AttributeChange(ATTR_PLATFORM, oldPlatform,
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
            this.listenerSupport.fireEvent(new TrainTypeEvent(this, new AttributeChange(ATTR_CATEGORY, oldCategory,
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
            this.listenerSupport.fireEvent(new TrainTypeEvent(this, new AttributeChange(ATTR_TRAIN_NAME_TEMPLATE,
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
            this.listenerSupport.fireEvent(new TrainTypeEvent(this, new AttributeChange(
                    ATTR_TRAIN_COMPLETE_NAME_TEMPLATE, oldTemplate, trainCompleteNameTemplate)));
        }
    }

    /**
     * formats the train name according to template.
     *
     * @param train train
     * @return formatted train name
     */
    public String formatTrainName(Train train) {
        TextTemplate template = (trainNameTemplate == null) ?
            getTrainDiagram().getTrainsData().getTrainNameTemplate() :
            trainNameTemplate;
        return template.evaluate(train);
    }

    /**
     * formats the train complete name according to template.
     *
     * @param train train
     * @return formatted complete train name
     */
    public String formatTrainCompleteName(Train train) {
        TextTemplate template = (trainCompleteNameTemplate == null) ?
            getTrainDiagram().getTrainsData().getTrainCompleteNameTemplate() :
            trainCompleteNameTemplate;
        return template.evaluate(train);
    }

    /**
     * adds listener to train.
     * @param listener listener
     */
    public void addListener(TrainTypeListener listener) {
        listenerSupport.addListener(listener);
    }

    /**
     * removes listener from train.
     * @param listener listener
     */
    public void removeListener(TrainTypeListener listener) {
        listenerSupport.removeListener(listener);
    }

    @Override
    public String toString() {
        return abbr + " - " + desc;
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

    /**
     * @return attributes
     */
    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    /**
     * @param attributes attributes to be set
     */
    @Override
    public void setAttributes(Attributes attributes) {
        if (this.attributes != null && attributesListener != null)
            this.attributes.removeListener(attributesListener);
        this.attributes = attributes;
        this.attributesListener = new AttributesListener() {

            @Override
            public void attributeChanged(Attributes attributes, AttributeChange change) {
                listenerSupport.fireEvent(new TrainTypeEvent(TrainType.this, change));
            }
        };
        this.attributes.addListener(attributesListener);
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.set(key, value);
    }
}
