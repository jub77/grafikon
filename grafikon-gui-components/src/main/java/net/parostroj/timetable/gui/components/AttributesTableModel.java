package net.parostroj.timetable.gui.components;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.events.AttributesListener;

/**
 * Table model for attributes.
 *
 * @author jub
 */
public class AttributesTableModel extends AbstractTableModel {

    private Attributes attributes;
    private AttributesListener listener;
    private List<String> userNames;
    private final String category;

    public AttributesTableModel(String category) {
        this.category = category;
    }

    @Override
    public String getColumnName(int column) {
        String name = null;
        switch (column) {
            case 0:
                name = ResourceLoader.getString("attributes.name");
                break;
            case 1:
                name = ResourceLoader.getString("attributes.type");
                break;
            case 2:
                name = ResourceLoader.getString("attributes.value");
                break;
        }
        return name;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2;
    }

    @Override
    public int getRowCount() {
        return userNames != null ? userNames.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String name = userNames.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return name;
            case 2:
                return attributes.get(category, name);
            default:
                return attributes.get(category, name).getClass().getSimpleName();
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        this.attributes.set(category, userNames.get(rowIndex), value);
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void startEditing(Attributes attributes) {
        if (this.attributes != null)
            throw new IllegalStateException("Already editing");
        this.attributes = attributes;
        this.userNames = new LinkedList<String>(attributes.getAttributesMap(category).keySet());
        this.listener = (attrs, change) -> {
            if (change.getNewValue() != null) {
                addAttribute(change.getName());
            } else {
                removeAttribute(change.getName());
            }
        };
        this.attributes.addListener(listener);
        this.fireTableDataChanged();
    }

    public Attributes stopEditing() {
        if (this.attributes != null && this.listener != null) {
            this.attributes.removeListener(this.listener);
        }
        return this.attributes;
    }

    protected void addAttribute(String name) {
        int position = userNames.indexOf(name);
        if (position == -1) {
            userNames.add(name);
            int size = userNames.size() - 1;
            this.fireTableRowsInserted(size, size);
        } else {
            this.fireTableRowsUpdated(position, position);
        }
    }

    protected void removeAttribute(String name) {
        int position = userNames.indexOf(name);
        userNames.remove(name);
        this.fireTableRowsDeleted(position, position);
    }

    public List<String> getUserNames() {
        return userNames;
    }
}
