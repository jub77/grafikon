package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Region - for station attributes -> freight net support.
 *
 * @author jub
 */
public class Region implements Visitable, ObjectWithId, AttributesHolder, RegionAttributes, TrainDiagramPart {

    private final TrainDiagram diagram;
    private final String id;
    private String name;
    private final AttributesWrapper attributesWrapper;

    Region(String id, String name, TrainDiagram diagram) {
        this.id = id;
        this.name = name;
        this.diagram = diagram;
        this.attributesWrapper = new AttributesWrapper(
                (attrs, change) -> diagram.fireEvent(new TrainDiagramEvent(diagram, change, Region.this)));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TrainDiagram getDiagram() {
        return diagram;
    }

    @Override
    public <T> T getAttribute(String key, Class<T> clazz) {
        return attributesWrapper.getAttributes().get(key, clazz);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributesWrapper.getAttributes().set(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributesWrapper.getAttributes().remove(key);
    }

    @Override
    public Attributes getAttributes() {
        return attributesWrapper.getAttributes();
    }

    @Override
    public void setAttributes(Attributes attributes) {
        this.attributesWrapper.setAttributes(attributes);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!ObjectsUtil.compareWithNull(name, this.name)) {
            String oldName = this.name;
            this.name = name;
            this.diagram.fireEvent(new TrainDiagramEvent(diagram, new AttributeChange(ATTR_NAME, oldName, name), this));
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }
}
