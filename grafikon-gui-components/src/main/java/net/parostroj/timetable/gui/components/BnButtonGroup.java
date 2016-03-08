package net.parostroj.timetable.gui.components;

import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

import net.parostroj.timetable.gui.pm.IEnumeratedValuesPM;

import org.beanfabrics.*;
import org.beanfabrics.event.WeakPropertyChangeListener;

public class BnButtonGroup<T> extends ButtonGroup implements View<IEnumeratedValuesPM<T>>, ModelSubscriber {

    private final Link link = new Link(this);
    private final BGListener listener = new BGListener();
    private final Map<ButtonModel, T> map = new HashMap<ButtonModel, T>();
    private IEnumeratedValuesPM<T> pModel;

    private class BGListener implements WeakPropertyChangeListener, Serializable {
        public void propertyChange(PropertyChangeEvent evt) {
            setSelectedValue(pModel.getValue());
        }
    }

    @Override
    public void add(AbstractButton b) {
        throw new UnsupportedOperationException();
    }

    public void add(AbstractButton b, T value) {
        super.add(b);
        map.put(b.getModel(), value);
        if (pModel != null) {
            if (b.isSelected()) {
                pModel.setValue(value);
            } else if (value.equals(pModel.getValue())) {
                b.setSelected(true);
            }
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
        for (Map.Entry<ButtonModel, T> entry : map.entrySet()) {
            if (value == entry.getValue() || (value != null && value.equals(entry.getValue()))) {
                this.setSelected(entry.getKey(), true);
                break;
            }
        }
    }

    @Override
    public IEnumeratedValuesPM<T> getPresentationModel() {
        return this.pModel;
    }

    @Override
    public void setPresentationModel(IEnumeratedValuesPM<T> pModel) {
        if (this.pModel != null) {
            this.pModel.removePropertyChangeListener(this.listener);
        }
        this.pModel = pModel;
        if (this.pModel != null) {
            this.pModel.addPropertyChangeListener("text", this.listener);
            T value = pModel.getValue();
            this.setSelectedValue(value);
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
