package net.parostroj.timetable.gui.views;

import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Enumeration with columns for train view table.
 * 
 * @author jub
 */
public enum TrainTableColumn {
    NODE("train.table.node", 50, 300, 120, "w", String.class, false, null),
    START("train.table.starttime", 50, 50, 50, "w", String.class, true, null),
    END("train.table.endtime", 50, 50, 50, "lo", String.class, true, null),
    STOP("train.table.stop", 50, 50, 50, "flo", Integer.class, false, null),
    REAL_STOP("train.table.real.stop", 50, 50, 50, "w", Double.class, false, null),
    SPEED("train.table.speed", 50, 50, 50, "e", Integer.class, false, null),
    PLATFORM("train.table.platform", 50, 50, 50, "t", String.class, false, new TrackCellEditor()),
    WEIGHT("train.table.weight", 50, 100, 50, "w", Integer.class, false, null),
    LENGTH("train.table.length", 50, 100, 50, "w", Integer.class, false, null),
    CONFLICTS("train.table.conflicts", 50, 200, 75, "w", String.class, false, null),
    COMMENT_SHOWN("train.table.comment.shown", 30, 30, 30, "o", Boolean.class, false, null),
    COMMENT("train.table.comment", 1, Integer.MAX_VALUE, 150, "", String.class, false, null),
    OCCUPIED_ENTRY("train.table.occupied.track", 30, 30, 30, "fo", Boolean.class, false, null),
    SHUNT("train.table.shunt", 30, 30, 30, "fo", Boolean.class, false, null);
    
    private int index;
    
    private String key;
    
    private int minWidth;
    
    private int prefWidth;
    
    private int maxWidth;
    
    private Class<?> clazz;
    
    private boolean rightAling;
    
    private boolean even;
    
    private boolean odd;
    
    private boolean first;
    
    private boolean last;
    
    private boolean all;
    
    private boolean oneTrack;
    
    private TableCellEditor editor;

    private static class Counter {
        static AtomicInteger CNT = new AtomicInteger(0);
    }
    
    private TrainTableColumn(String key, int minWidth, int maxWidth, int prefWidth, String forbidden, Class<?> clazz, boolean rightAling, TableCellEditor editor) {
        this.index = Counter.CNT.getAndIncrement();
        this.key = key;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.prefWidth = prefWidth;
        this.clazz = clazz;
        this.rightAling = rightAling;
        this.editor = editor;
        // parse limits
        for (char limit : forbidden.toCharArray()) {
            switch(limit) {
                case 'e':
                    even = true;
                    break;
                case 'o':
                    odd = true;
                    break;
                case 'f':
                    first = true;
                    break;
                case 'l':
                    last = true;
                    break;
                case 'w':
                    all = true;
                    break;
                case 't':
                    oneTrack = true;
                    break;
            }
        }
    }

    public int getIndex() {
        return index;
    }

    public String getKey() {
        return key;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public int getPrefWidth() {
        return prefWidth;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public boolean isRightAling() {
        return rightAling;
    }

    public TableCellEditor getEditor() {
        return editor;
    }
    
    public boolean isAllowedToEdit(int row, int max, TimeInterval interval) {
        if (all)
            return false;
        if (even && row % 2 == 0)
            return false;
        if (odd && row % 2 != 0)
            return false;
        if (first && row == 0)
            return false;
        if (last && row == max)
            return  false;
        if (oneTrack) {
            if (interval.getOwner().getTracks().size() == 1)
                return false;
        }
        return true;
    }
    
    public TableColumn createTableColumn() {
        TableColumn tableColumn = new TableColumn(this.getIndex(), this.getPrefWidth());
        tableColumn.setMinWidth(this.getMinWidth());
        tableColumn.setMaxWidth(this.getMaxWidth());
        if (this.isRightAling()) {
            DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
            cellRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            tableColumn.setCellRenderer(cellRenderer);
        }
        if (this.getEditor() != null)
            tableColumn.setCellEditor(this.getEditor());
        String cName = ResourceLoader.getString(this.getKey());
        tableColumn.setHeaderValue(cName);
        return tableColumn;
    }

    public static TrainTableColumn getColumn(int index) {
        for (TrainTableColumn column : values()) {
            if (column.getIndex() == index)
                return column;
        }
        return null;
    }
}
