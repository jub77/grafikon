package net.parostroj.timetable.gui.components;

import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

import net.parostroj.timetable.gui.pm.ValuePM;

import org.beanfabrics.*;
import org.beanfabrics.event.WeakPropertyChangeListener;

public class BnButtonGroup<T> extends ButtonGroup implements View<ValuePM<T>>, ModelSubscriber {

    private final Link link = new Link(this);
    private final BGListener listener = new BGListener();
    private final Map<ButtonModel, T> map = new HashMap<ButtonModel, T>();
    private ValuePM<T> pModel;

    private class BGListener implements WeakPropertyChangeListener, Serializable {
        public void propertyChange(PropertyChangeEvent evt) {
            setSelectedValue(pModel.getValue());
        }
    }

    public void add(AbstractButton b, T value) {
        super.add(b);
        map.put(b.getModel(), value);
        if (b.isSelected() && pModel != null) {
            pModel.setValue(value);
        }
    }

    @Override
    public void setSelected(ButtonModel m, boolean b) {
        super.setSelected(m, b);
        if (this.pModel != null && b) {
            pModel.setValue(map.get(m));
        }
    }

    public void setSelectedValue(T value) {
        if (value == null) {
            throw new NullPointerException("Selected value cannot be null");
        }
        for (Map.Entry<ButtonModel, T> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                this.setSelected(entry.getKey(), true);
                break;
            }
        }
    }

    @Override
    public ValuePM<T> getPresentationModel() {
        return this.pModel;
    }

    @Override
    public void setPresentationModel(ValuePM<T> pModel) {
        if (this.pModel != null) {
            this.pModel.removePropertyChangeListener(this.listener);
        }
        this.pModel = pModel;
        if (this.pModel != null) {
            this.pModel.addPropertyChangeListener("value", this.listener);
        }
    }

    @Override
    public IModelProvider getModelProvider() {
        return this.link.getModelProvider();
    }

    @Override
    public void setModelProvider(IModelProvider provider) {
        this.link.setModelProvider(provider);
    }

    @Override
    public Path getPath() {
        return this.link.getPath();
    }

    @Override
    public void setPath(Path path) {
        this.link.setPath(path);
    }
}
