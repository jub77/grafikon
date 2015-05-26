package net.parostroj.timetable.model;

import java.util.Locale;

import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Company.
 *
 * @author jub
 */
public class Company implements ObjectWithId, AttributesHolder, CompanyAttributes, Visitable {

    private final String id;

    private Attributes attributes;

    Company(String id) {
        this.id = id;
        this.setAttributes(new Attributes());
    }

    @Override
    public String getId() {
        return id;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public <T> T getAttribute(String key, Class<T> clazz) {
        return attributes.get(key, clazz);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.set(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    public String getName() {
        return attributes.get(ATTR_NAME, String.class);
    }

    public void setName(String name) {
        attributes.setRemove(ATTR_NAME, name);
    }

    public Locale getLocale() {
        return attributes.get(ATTR_LOCALE, Locale.class);
    }

    public void setLocale(Locale locale) {
        attributes.setRemove(ATTR_LOCALE, locale);
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
