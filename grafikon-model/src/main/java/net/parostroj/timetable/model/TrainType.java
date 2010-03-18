package net.parostroj.timetable.model;

import java.awt.Color;
import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.TrainTypeEvent;
import net.parostroj.timetable.model.events.TrainTypeListener;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;

/**
 * Train type.
 *
 * @author jub
 */
public class TrainType implements ObjectWithId {
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
    private GTListenerSupport<TrainTypeListener, TrainTypeEvent> listenerSupport;

    /**
     * creates instance.
     *
     * @param id id
     */
    TrainType(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        listenerSupport = new GTListenerSupport<TrainTypeListener, TrainTypeEvent>(new GTEventSender<TrainTypeListener, TrainTypeEvent>() {

            @Override
            public void fireEvent(TrainTypeListener listener, TrainTypeEvent event) {
                listener.trainTypeChanged(event);
            }
        });
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
        String oldAbbr = this.abbr;
        this.abbr = abbr;
        this.listenerSupport.fireEvent(new TrainTypeEvent(this, new AttributeChange("abbr", oldAbbr, abbr)));
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
        Color oldColor = this.color;
        this.color = color;
        this.listenerSupport.fireEvent(new TrainTypeEvent(this, new AttributeChange("color", oldColor, color)));
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
        String oldDesc = this.desc;
        this.desc = desc;
        this.listenerSupport.fireEvent(new TrainTypeEvent(this, new AttributeChange("desc", oldDesc, desc)));
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
        boolean oldPlatform = this.platform;
        this.platform = platform;
        this.listenerSupport.fireEvent(new TrainTypeEvent(this, new AttributeChange("platform", oldPlatform, platform)));
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
        TrainTypeCategory oldCategory = this.category;
        this.category = category;
        this.listenerSupport.fireEvent(new TrainTypeEvent(this, new AttributeChange("category", oldCategory, category)));
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
        TextTemplate oldTemplate = this.trainNameTemplate;
        this.trainNameTemplate = trainNameTemplate;
        this.listenerSupport.fireEvent(new TrainTypeEvent(this, new AttributeChange("trainNameTemplate",oldTemplate, trainNameTemplate)));
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
        TextTemplate oldTemplate = this.trainCompleteNameTemplate;
        this.trainCompleteNameTemplate = trainCompleteNameTemplate;
        this.listenerSupport.fireEvent(new TrainTypeEvent(this, new AttributeChange("trainCompleteNameTemplate", oldTemplate, trainCompleteNameTemplate)));
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
        return template.evaluate(train, train.createTemplateBinding());
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
        return template.evaluate(train, train.createTemplateBinding());
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
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }
}
