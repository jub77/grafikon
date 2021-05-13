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
public class Company implements ObjectWithId, AttributesHolder, Visitable, TrainDiagramPart, ItemCollectionObject {

    public static final String ATTR_ABBR = "abbr";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_LOCALE = "locale";
    public static final String ATTR_PART_NAME = "part";

    private final String id;
    private final TrainDiagram diagram;

    private final Attributes attributes;

    private boolean events;

    Company(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.attributes = new Attributes(
                (attrs, change) -> { if (events) diagram.fireEvent(new Event(diagram, Company.this, change)); });
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
    public Attributes getAttributes() {
        return attributes;
    }

    public String getAbbr() {
        return attributes.get(ATTR_ABBR, String.class);
    }

    public void setAbbr(String abbr) {
        attributes.setRemove(ATTR_ABBR, abbr);
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
    public void added() {
        this.events = true;
    }

    @Override
    public void removed() {
        this.events = false;
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
