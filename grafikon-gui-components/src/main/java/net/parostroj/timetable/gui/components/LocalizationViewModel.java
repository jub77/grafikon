package net.parostroj.timetable.gui.components;

import java.util.*;

import javax.swing.table.AbstractTableModel;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Localization;
import net.parostroj.timetable.model.Localization.Translation;

/**
 * Model for localization view.
 *
 * @author jub
 */
public class LocalizationViewModel extends AbstractTableModel {

    private final Localization localization;

    private List<Locale> columns;
    private List<String> keys;

    public LocalizationViewModel(Localization localization) {
        this.localization = localization;
    }

    @Override
    public String getColumnName(int column) {
        return column == 0 ? ResourceLoader.getString("localization.default.value") : getColumnsImpl().get(column - 1).getDisplayName();
    }

    @Override
    public int getRowCount() {
        return this.getKeysImpl().size();
    }

    @Override
    public int getColumnCount() {
        return this.getColumnsImpl().size() + 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return getKeysImpl().get(rowIndex);
        } else {
            String key = getKeysImpl().get(rowIndex);
            Locale locale = getColumnsImpl().get(columnIndex - 1);
            return localization.getTranslation(key, locale);
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Locale locale = getColumnsImpl().get(columnIndex - 1);
        String key = getKeysImpl().get(rowIndex);
        localization.addTranslation(key, new Translation(locale, (String) value));
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    private List<Locale> getColumnsImpl() {
        if (columns == null) {
            columns = new ArrayList<Locale>(localization.getLocales());
            Collections.sort(columns, new Comparator<Locale>() {
                @Override
                public int compare(Locale o1, Locale o2) {
                    return o1.getDisplayName().compareTo(o2.getDisplayName());
                }
            });
        }
        return columns;
    }

    private List<String> getKeysImpl() {
        if (keys == null) {
            keys = new ArrayList<String>(localization.getKeys());
            Collections.sort(keys);
        }
        return keys;
    }
}
