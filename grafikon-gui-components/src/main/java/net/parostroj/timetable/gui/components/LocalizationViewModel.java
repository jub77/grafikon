package net.parostroj.timetable.gui.components;

import java.util.*;

import javax.swing.table.AbstractTableModel;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Localization;
import net.parostroj.timetable.model.Localization.Translation;
import net.parostroj.timetable.utils.Pair;

/**
 * Model for localization view.
 *
 * @author jub
 */
public class LocalizationViewModel extends AbstractTableModel {

    private final Localization localization;
    private final Map<Locale, String> localeMap;

    private List<Pair<String, Locale>> columns;
    private List<String> keys;


    public LocalizationViewModel(Localization localization, Map<Locale, String> localeMap) {
        this.localization = localization;
        this.localeMap = localeMap;
    }

    @Override
    public String getColumnName(int column) {
        return column == 0 ? ResourceLoader.getString("localization.default.value") : getColumnsImpl().get(column - 1).first;
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
            Locale locale = getColumnsImpl().get(columnIndex - 1).second;
            return localization.getTranslation(key, locale);
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Locale locale = getColumnsImpl().get(columnIndex - 1).second;
        String key = getKeysImpl().get(rowIndex);
        localization.addTranslation(key, new Translation(locale, (String) value));
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    private List<Pair<String, Locale>> getColumnsImpl() {
        if (columns == null) {
            Map<Locale, String> map = localeMap;
            columns = new ArrayList<Pair<String, Locale>>(localization.getLocales().size());
            for (Locale l : localization.getLocales()) {
                columns.add(new Pair<String, Locale>(map.get(l), l));
            }
            Collections.sort(columns, new Comparator<Pair<String, Locale>>() {
                @Override
                public int compare(Pair<String, Locale> o1, Pair<String, Locale> o2) {
                    if (o1.first == null && o2.first == null) {
                        return 0;
                    } else if (o1.first == null) {
                        return -1;
                    } else if (o2.first == null) {
                        return 1;
                    } else {
                        return o1.first.compareTo(o2.first);
                    }
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
