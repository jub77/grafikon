package net.parostroj.timetable.model;

import java.util.Locale;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Company.
 *
 * @author jub
 */
public class Company implements ObjectWithId, AttributesHolder, CompanyAttributes, Visitable, TrainDiagramPart {

    private final String id;
    private final TrainDiagram diagram;

    private final AttributesWrapper attributesWrapper;

    Company(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.attributesWrapper = new AttributesWrapper(
                (attrs, change) -> diagram.fireEvent(new TrainDiagramEvent(diagram, change, Company.this)));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TrainDiagram getDiagram() {
        return diagram;
    }

    public Attributes getAttributes() {
        return attributesWrapper.getAttributes();
    }

    public void setAttributes(Attributes attributes) {
        this.attributesWrapper.setAttributes(attributes);
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

    public String getAbbr() {
        return attributesWrapper.getAttributes().get(ATTR_ABBR, String.class);
    }

    public void setAbbr(String abbr) {
        attributesWrapper.getAttributes().setRemove(ATTR_ABBR, abbr);
    }

    public String getName() {
        return attributesWrapper.getAttributes().get(ATTR_NAME, String.class);
    }

    public void setName(String name) {
        attributesWrapper.getAttributes().setRemove(ATTR_NAME, name);
    }

    public Locale getLocale() {
        return attributesWrapper.getAttributes().get(ATTR_LOCALE, Locale.class);
    }

    public void setLocale(Locale locale) {
        attributesWrapper.getAttributes().setRemove(ATTR_LOCALE, locale);
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return this.getAbbr();
    }
}
