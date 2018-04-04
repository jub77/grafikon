package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import javax.swing.JScrollPane;

/**
 * Panel for elements selection from list.
 *
 * @author jub
 */
public class ElementSelectionListPanel<T> extends JPanel {

    private static final long serialVersionUID = 1L;

	private static final int VISIBLE_ROW_COUNT = 16;

    private WrapperListModel<T> items;

    private JList<Wrapper<T>> itemList;

    public ElementSelectionListPanel() {
        this(true);
    }

    public ElementSelectionListPanel(boolean sorted) {
        this.items = new WrapperListModel<>(sorted);
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        itemList = new JList<>();
        itemList.setVisibleRowCount(VISIBLE_ROW_COUNT);
        itemList.setModel(items);
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        scrollPane.setViewportView(itemList);

    }

    public void setMultipleSelection(boolean multiple) {
        itemList.setSelectionMode(
                multiple ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);
    }

    public void setListForSelection(Collection<Wrapper<T>> list) {
        items.clear();
        items.setListOfWrappers(new ArrayList<>(list));
    }

    public void setSelected(T selected) {
        this.setSelected(Collections.singletonList(selected));
    }

    public void setSelected(Collection<? extends T> selectedCollection) {
        itemList.clearSelection();
        for (T selected : selectedCollection) {
            int index = items.getIndexOfObject(selected);
            if (index != -1) {
                itemList.addSelectionInterval(index, index);
            }
        }
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        itemList.addListSelectionListener(listener);
    }

    public Collection<T> getSelected() {
        return itemList.getSelectedValuesList().stream().map(wrapper -> wrapper.getElement())
                .collect(Collectors.toList());
    }

    public boolean isSelectionEmpty() {
        return itemList.isSelectionEmpty();
    }

    public void setVisibleRows(int visibleRows) {
        this.itemList.setVisibleRowCount(visibleRows);
    }

    public void setSorted(boolean sorted) {
        this.items.setSorted(sorted);
    }
}
