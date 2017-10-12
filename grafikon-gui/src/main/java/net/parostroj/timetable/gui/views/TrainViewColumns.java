package net.parostroj.timetable.gui.views;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.AppPreferences;
import net.parostroj.timetable.gui.StorableGuiData;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Storage of train view columns.
 *
 * @author jub
 */
public class TrainViewColumns implements StorableGuiData {

    private static final Logger log = LoggerFactory.getLogger(TrainViewColumns.class);

    private JTable trainTable;

    public TrainViewColumns(JTable trainTable) {
        this.trainTable = trainTable;
    }

    @Override
    public Section saveToPreferences(Ini prefs) {
        Ini.Section section = getTrainsSection(prefs);
        // get displayed columns and save theirs order
        prefs.remove("columns");
        String order = createStringForColumns();
        if (order != null) {
            section.put("columns", order);
        }
        return section;
    }

    @Override
    public Section loadFromPreferences(Ini prefs) {
        Ini.Section section = getTrainsSection(prefs);
        // set displayed columns (if the prefs are empty - show all)
        String cs = section.get("columns");
        cs = ObjectsUtil.checkAndTrim(cs);
        applyStringForColumns(cs);
        return section;
    }

    private void applyStringForColumns(String cs) {
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
                String[] ss = cStr.split(",");
                try {
                    TrainTableColumn column = TrainTableColumn.valueOf(ss[0]);
                    if (column != null) {
                        TableColumn ac = column.createTableColumn();
                        if (ss.length > 1) {
                            int wInt = Integer.parseInt(ss[1]);
                            if (wInt != 0) {
                                ac.setPreferredWidth(wInt);
                            }
                        }
                        shownColumns.add(ac);
                    }
                } catch (NumberFormatException e) {
                    log.warn("Cannot load columns order for train view: {}", cStr);
                } catch (Exception e) {
                    log.warn("Error adding column to train view: {}", ss[0]);
                }
            }
        }
        // append columns to table
        TableColumnModel tcm = trainTable.getColumnModel();
        for (TableColumn column : shownColumns) {
            tcm.addColumn(column);
        }
    }

    public String createStringForColumns() {
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

    private Ini.Section getTrainsSection(Ini prefs) {
        return AppPreferences.getSection(prefs, "trains");
    }
}
