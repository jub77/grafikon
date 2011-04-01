package net.parostroj.timetable.gui.components;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.AttributesListener;

/**
 * Table model for attributes.
 *
 * @author jub
 */
public class AttributesTableModel extends AbstractTableModel {

    public static final String USER_PREFIX = "user.";

    private Attributes attributes;
    private AttributesListener listener;
    private List<String> userNames;

    @Override
    public String getColumnName(int column) {
        return column == 0 ?
            ResourceLoader.getString("attributes.name") :
            ResourceLoader.getString("attributes.value");
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }

    @Override
    public int getRowCount() {
        return userNames != null ? userNames.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String name = userNames.get(rowIndex);
        if (columnIndex == 0) {
            return name.replace(USER_PREFIX, "");
        } else {
            return attributes.get(name);
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        this.attributes.set(userNames.get(rowIndex), value);
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void startEditing(Attributes attributes) {
        if (this.attributes != null)
            throw new IllegalStateException("Already editing");
        this.attributes = attributes;
        this.userNames = this.getUserNames(attributes.names());
        this.listener = new AttributesListener() {

            @Override
            public void attributeChanged(Attributes attributes, AttributeChange change) {
                if (change.getNewValue() != null)
                    addAttribute(change.getName());
                else
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
    
    private List<String> getUserNames(List<String> names) {
        List<String> result = new LinkedList<String>();
        for (String name : names) {
            if (name.startsWith(USER_PREFIX))
                result.add(name);
        }
        return result;
    }
    
    public List<String> getUserNames() {
        return userNames;
    }
}
