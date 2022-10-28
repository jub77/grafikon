package net.parostroj.timetable.gui.pm;

import java.util.function.Function;

import org.beanfabrics.model.AbstractPM;

import net.parostroj.timetable.model.Attributes;

public class ModelAttributesPM extends AbstractPM {

    private Attributes attributes;
    private String category;
    private boolean finished;
    private Function<String, String> nameTranslation;

    public void init(Attributes attributes, String category) {
        this.init(attributes, category, null);
    }

    public void init(Attributes attributes, String category, Function<String, String> nameTranslation) {
        Attributes oldValue = this.attributes;
        this.attributes = attributes;
        this.category = category;
        this.nameTranslation = nameTranslation;
        this.getPropertyChangeSupport().firePropertyChange("attributes", oldValue, this.attributes);
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public String getCategory() {
        return category;
    }

    public Function<String, String> getNameTranslation() {
        return nameTranslation;
    }

    public boolean isFinished() {
        return finished;
    }

    public Attributes getFinalAttributes() {
        finished = true;
        this.getPropertyChangeSupport().firePropertyChange("finished", false, true);
        return this.attributes;
    }
}
