package net.parostroj.timetable.gui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.ini.IniConfig;
import net.parostroj.timetable.gui.ini.IniConfigSection;
import net.parostroj.timetable.gui.ini.StorableGuiData;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Storage of train view columns.
 *
 * @author jub
 */
public class TrainViewColumns implements StorableGuiData {

    private static final String CURRENT_COLUMNS_KEY = "columns";
    private static final String STORED_COLUMNS_KEY = "stored.columns";
    private static final String DELIMITER = ":";

    private static final Logger log = LoggerFactory.getLogger(TrainViewColumns.class);

    private final JTable trainTable;
    private final Map<String, String> columnConfigurations;

    public TrainViewColumns(JTable trainTable) {
        this.trainTable = trainTable;
        this.columnConfigurations = new HashMap<>();
    }

    @Override
    public IniConfigSection saveToPreferences(IniConfig prefs) {
        IniConfigSection section = getTrainsSection(prefs);
        // get displayed columns and save theirs order
        prefs.removeSection(CURRENT_COLUMNS_KEY);
        String order = createColumnConfiguration();
        if (order != null) {
            section.put(CURRENT_COLUMNS_KEY, order);
        }
        // stored columns
        section.remove(STORED_COLUMNS_KEY);
        columnConfigurations.forEach((key, config) -> section.add(STORED_COLUMNS_KEY, key + DELIMITER + config));
        return section;
    }

    @Override
    public IniConfigSection loadFromPreferences(IniConfig prefs) {
        IniConfigSection section = getTrainsSection(prefs);
        // set displayed columns (if the prefs are empty - show all)
        String cs = section.get(CURRENT_COLUMNS_KEY);
        cs = ObjectsUtil.checkAndTrim(cs);
        applyColumnConfiguration(cs);
        List<String> keysWithConfig = section.getAll(STORED_COLUMNS_KEY);
        if (keysWithConfig != null) {
            keysWithConfig.forEach(keyWithConfig -> {
                String[] items = keyWithConfig.split(DELIMITER);
                if (items.length == 2) {
                    columnConfigurations.put(items[0], items[1]);
                }
            });
        }
        return section;
    }

    public void storeColumnConfiguration(String key) {
        String configuration = this.createColumnConfiguration();
        columnConfigurations.put(key, configuration);
    }

    public void loadColumnConfiguration(String key) {
        String configuration = columnConfigurations.get(key);
        this.applyColumnConfiguration(configuration);
    }

    public void removeColumnConfiguration(String key) {
        columnConfigurations.remove(key);
    }

    public Collection<String> getColumnConfigurationKeys() {
        return columnConfigurations.keySet();
    }

    public void applyColumnConfiguration(String cs) {
        this.clearExistingColumns();
        List<TableColumn> shownColumns = new LinkedList<>();
        if (cs == null) {
            // all columns
            for (TrainTableColumn c : TrainTableColumn.values()) {
                shownColumns.add(c.createTableColumn());
            }
        } else {
            // extract
            String[] splitted = cs.split("\\|");
            for (String cStr : splitted) {
                parseOneColumn(shownColumns, cStr);
            }
        }
        // append columns to table
        TableColumnModel tcm = trainTable.getColumnModel();
        for (TableColumn column : shownColumns) {
            tcm.addColumn(column);
        }
    }

    private void parseOneColumn(List<TableColumn> shownColumns, String cStr) {
        String[] ss = cStr.split(",");
        try {
            TrainTableColumn column = TrainTableColumn.valueOf(ss[0]);
            TableColumn ac = column.createTableColumn();
            if (ss.length > 1) {
                int wInt = Integer.parseInt(ss[1]);
                if (wInt != 0) {
                    ac.setPreferredWidth(wInt);
                }
            }
            shownColumns.add(ac);
        } catch (NumberFormatException e) {
            log.warn("Cannot load columns order for train view: {}", cStr);
        } catch (Exception e) {
            log.warn("Error adding column to train view: {}", ss[0]);
        }
    }

    public String createColumnConfiguration() {
        TableColumnModel tcm = trainTable.getColumnModel();
        Enumeration<TableColumn> columns = tcm.getColumns();
        StringBuilder order = null;
        while (columns.hasMoreElements()) {
            TableColumn column = columns.nextElement();
            if (order != null) {
                order.append('|');
            } else {
                order = new StringBuilder();
            }
            order.append(TrainTableColumn.values()[column.getModelIndex()].name()).append(',')
                    .append(column.getPreferredWidth());
        }
        return order == null ? null : order.toString();
    }

    private void clearExistingColumns() {
        Collection<TableColumn> columns = new ArrayList<>(trainTable.getColumnCount());
        Enumeration<TableColumn> e = trainTable.getColumnModel().getColumns();
        while (e.hasMoreElements()) {
            columns.add(e.nextElement());
        }
        columns.forEach(trainTable::removeColumn);
    }

    private IniConfigSection getTrainsSection(IniConfig prefs) {
        return prefs.getSection("trains");
    }
}
