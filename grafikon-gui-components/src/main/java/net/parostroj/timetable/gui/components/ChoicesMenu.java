package net.parostroj.timetable.gui.components;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ChoicesMenu<T> extends JMenu {

    public interface Listener<V> {
        public void changed(V value, boolean selected);
    }

    private final BiMap<T, ButtonModel> items;
    private final List<Listener<T>> listeners;
    private final ItemListener itemListener;

    private boolean fireLock;

    public ChoicesMenu() {
        this(null);
    }

    public ChoicesMenu(String text) {
        super(text);
        this.items = HashBiMap.create();
        this.listeners = new ArrayList<Listener<T>>();
        this.itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                fireChanged(items.inverse().get(((AbstractButton) e.getItem()).getModel()), e.getStateChange() == ItemEvent.SELECTED);
            }
        };
    }

    public void setItemState(T value, boolean selected) {
        this.setItemState(value, selected, false);
    }

    public void setItemState(T value, boolean selected, boolean eventFireLock) {
        this.fireLock = eventFireLock;
        this.items.get(value).setSelected(true);
        this.fireLock = false;
    }

    public boolean getItemState(T value) {
        return this.items.get(value).isSelected();
    }

    public void addItem(String text, T value) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(text);
        item.addItemListener(itemListener);
        this.add(item);
        this.items.put(value, item.getModel());
    }

    public void addListener(Listener<T> l) {
        this.listeners.add(l);
    }

    private void fireChanged(T value, boolean selected) {
        if (!fireLock) {
            for (Listener<T> l : listeners) {
                l.changed(value, selected);
            }
        }
    }
}
