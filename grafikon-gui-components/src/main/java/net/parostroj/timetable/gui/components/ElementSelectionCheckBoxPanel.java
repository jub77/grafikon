package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.stream.Collectors;

import javax.swing.*;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.parostroj.timetable.gui.wrappers.Wrapper;

/**
 * Panel for elements selection (using check boxes).
 *
 * @author jub
 */
public class ElementSelectionCheckBoxPanel<T> extends JPanel {

    private static final int VISIBLE_ROW_COUNT = 8;

    private int visibleRows = VISIBLE_ROW_COUNT;
    private List<Wrapper<T>> items;
    private BiMap<T, JCheckBox> itemMap;
    private boolean sorted;
    private ElementSelectionListener<T> listener;

    private JPanel panel;


    public ElementSelectionCheckBoxPanel() {
        this(true);
    }

    public ElementSelectionCheckBoxPanel(boolean sorted) {
        this.sorted = sorted;
        items = new ArrayList<>();
        itemMap = HashBiMap.create();
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane() {
            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                int itemsSize = items.isEmpty() ? 1 : items.size();
                size.height = size.height * visibleRows / itemsSize;
                return size;
            }
        };
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        scrollBar.setUnitIncrement(8);
        add(scrollPane);

        panel = new JPanel();
        scrollPane.setViewportView(panel);
        panel.setBackground(UIManager.getColor("List.background"));
        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);

        refreshItems();
    }

    public void setListForSelection(Collection<Wrapper<T>> list) {
        items.clear();
        itemMap.clear();
        items.addAll(list);
        if (sorted) {
            Collections.sort(items);
        }
        refreshItems();
    }

    public void setSelected(Collection<? extends T> selected) {
        for (JCheckBox comp : itemMap.values()) {
            comp.setSelected(false);
        }
        for (T item : selected) {
            itemMap.get(item).setSelected(true);
        }
    }

    public Set<T> getSelected() {
        return itemMap.entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .map(entry -> entry.getKey())
                .collect(Collectors.toSet());
    }

    public void setListener(ElementSelectionListener<T> listener) {
        this.listener = listener;
    }

    public void setVisibleRows(int visibleRows) {
        this.visibleRows = visibleRows;
    }

    public void setSorted(boolean sorted) {
        this.sorted = sorted;
    }

    private void refreshItems() {
        panel.removeAll();
        ItemListener l = e -> {
            if (listener != null) {
                listener.selection(
                        itemMap.inverse().get(e.getItemSelectable()),
                        e.getStateChange() == ItemEvent.SELECTED);
            }
        };
        if (items.isEmpty()) {
            // add dummy empty item
            panel.add(new JLabel("   "));
        } else {
            for (Wrapper<T> item : items) {
                JCheckBox checkBox = new JCheckBox(item.toString());
                checkBox.setBackground(UIManager.getColor("List.background"));
                checkBox.addItemListener(l);
                itemMap.put(item.getElement(), checkBox);
                panel.add(checkBox);
            }
            panel.add(Box.createVerticalGlue());
        }
    }
}
