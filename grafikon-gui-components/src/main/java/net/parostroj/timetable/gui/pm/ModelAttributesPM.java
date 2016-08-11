package net.parostroj.timetable.gui.pm;

import org.beanfabrics.model.AbstractPM;

import net.parostroj.timetable.model.Attributes;

public class ModelAttributesPM extends AbstractPM {

    private Attributes attributes;
    private String category;
    private boolean finished;

    public void init(Attributes attributes, String category) {
        Attributes oldValue = this.attributes;
        this.attributes = attributes;
        this.category = category;
        this.getPropertyChangeSupport().firePropertyChange("attributes", oldValue, this.attributes);
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public String getCategory() {
        return category;
    }

    public boolean isFinished() {
        return finished;
    }

    public Attributes getFinalAttributes() {
        // TODO allow repeated call??
        finished = true;
        this.getPropertyChangeSupport().firePropertyChange("finished", false, true);
        return this.attributes;
    }
}
