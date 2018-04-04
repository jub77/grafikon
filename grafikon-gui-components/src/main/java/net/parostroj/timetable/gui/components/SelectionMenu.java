package net.parostroj.timetable.gui.components;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.*;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class SelectionMenu<T> extends JMenu {

    private static final long serialVersionUID = 1L;

	public interface Listener<V> {
        public void selected(V value);
    }

    private final BiMap<T, ButtonModel> items;
    private final ButtonGroup buttonGroup;
    private final List<Listener<T>> listeners;
    private final ItemListener itemListener;

    private boolean fireLock;

    public SelectionMenu() {
        this(null);
    }

    public SelectionMenu(String text) {
        super(text);
        this.buttonGroup = new ButtonGroup();
        this.items = HashBiMap.create();
        this.listeners = new ArrayList<>();
        this.itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    fireSelected(items.inverse().get(((AbstractButton) e.getItem()).getModel()));
                }
            }
        };
    }

    public void setSelectedItem(T value) {
        this.setSelectedItem(value, false);
    }

    public void setSelectedItem(T value, boolean eventFireLock) {
        this.fireLock = eventFireLock;
        ButtonModel model = this.items.get(value);
        if (model != null) {
            model.setSelected(true);
        } else {
            this.buttonGroup.clearSelection();
        }
        this.fireLock = false;
    }

    public T getSelected() {
        ButtonModel selection = this.buttonGroup.getSelection();
        return selection == null ? null : this.items.inverse().get(selection);
    }

    public void setAllItemsVisible(boolean visible) {
        for (int i = 0; i < getItemCount(); i++) {
            this.getItem(i).setVisible(visible);
        }
    }

    public void setItemVisible(T value, boolean visible) {
        for (int i = 0; i < getItemCount(); i++) {
            ButtonModel itemModel = items.get(value);
            JMenuItem item = this.getItem(i);
            if (item.getModel() == itemModel) {
                item.setVisible(visible);
                break;
            }
        }
    }

    public void addItem(String text, T value) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(text);
        item.addItemListener(itemListener);
        this.add(item);
        this.items.put(value, item.getModel());
        this.buttonGroup.add(item);
    }

    public void removeAllItems() {
        Enumeration<AbstractButton> elements = buttonGroup.getElements();
        while (elements.hasMoreElements()) {
            AbstractButton element = elements.nextElement();
            this.remove(element);
            this.items.inverse().remove(element.getModel());
        }
    }

    public void addListener(Listener<T> l) {
        this.listeners.add(l);
    }

    private void fireSelected(T value) {
        if (!fireLock) {
            for (Listener<T> l : listeners) {
                l.selected(value);
            }
        }
    }
}
