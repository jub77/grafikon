package net.parostroj.timetable.gui.views;

import java.awt.Component;
import java.awt.Graphics;
import java.text.DecimalFormatSymbols;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Enumeration with columns for train view table.
 *
 * @author jub
 */
public enum TrainTableColumn {
    NODE("train.table.node", 50, 300, 120, "w", String.class, false, null), START("train.table.starttime", 50, 70, 50,
            "fc", String.class, true, null), END("train.table.endtime", 50, 70, 50, "loc", String.class, true, null), STOP(
            "train.table.stop", 50, 50, 50, "flo", String.class, true, null), REAL_STOP("train.table.real.stop", 50,
            50, 50, "w", Double.class, false, null), SPEED_LIMIT("train.table.speed.limit", 50, 50, 50, "e", Integer.class, false,
            null), SPEED("train.table.speed", 50, 50, 50, "w", Integer.class, false, null),
            ADDED_TIME("train.table.added.time", 50, 50, 50, "e", String.class, true, null), PLATFORM(
            "train.table.platform", 50, 50, 50, "t", String.class, false, new TrackCellEditor()), WEIGHT(
            "train.table.weight", 50, 100, 50, "w", Integer.class, false, null), LENGTH("train.table.length", 50, 100,
            50, "w", Integer.class, false, null), CONFLICTS("train.table.conflicts", 50, 200, 75, "w", String.class,
            false, null), COMMENT_SHOWN("train.table.comment.shown", 30, 30, 30, "o", Boolean.class, false, null), COMMENT(
            "train.table.comment", 1, Integer.MAX_VALUE, 150, "", String.class, false, null), OCCUPIED_ENTRY(
            "train.table.occupied.track", 30, 30, 30, "fo", Boolean.class, false, null), SHUNT("train.table.shunt", 30,
            30, 30, "fo", Boolean.class, false, null), SET_SPEED("train.table.set.speed", 50, 50, 50, "e", Integer.class, false, null),
            IGNORE_LENGTH("train.table.ignore.length", 30, 30, 30, "o", Boolean.class, false, null),
            MANAGED_FREIGHT("train.table.managed.freight", 30, 30, 30, "o", Boolean.class, false, null),
            FREIGHT_TO_STATIONS("train.table.freight.to.stations", 50, 300, 75, "w", String.class, false, null);

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
    private boolean time;

    private static class Counter {
        static AtomicInteger CNT = new AtomicInteger(0);
    }

    private static class TimeCellRenderer implements TableCellRenderer {

        private final static String END;
        private TableCellRenderer wrapped;
        private final int width;
        private final Icon icon;

        static {
            StringBuilder b = new StringBuilder();
            b.append(DecimalFormatSymbols.getInstance().getDecimalSeparator());
            b.append('0');
            END = b.toString();
        }

        public TimeCellRenderer(TableCellRenderer wrapped) {
            this.wrapped = wrapped;
            JLabel l = (JLabel) wrapped;
            this.width = SwingUtilities.computeStringWidth(l.getFontMetrics(l.getFont()), END);
            this.icon = new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    // nothing - empty space
                }

                @Override
                public int getIconWidth() {
                    return width;
                }

                @Override
                public int getIconHeight() {
                    return 1;
                }
            };
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            String time = (String) value;
            Component component = null;
            if (time != null && time.endsWith(END)) {
                String text = time.substring(0, time.length() - 2);
                component = wrapped.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
                final JLabel l = (JLabel) component;
                l.setIcon(icon);
                l.setIconTextGap(0);
                l.setHorizontalTextPosition(JLabel.LEADING);
                l.setAlignmentX(JLabel.RIGHT);
            } else {
                component = wrapped.getTableCellRendererComponent(table, time, isSelected, hasFocus, row, column);
                JLabel l = (JLabel) component;
                l.setIcon(null);
                l.setIconTextGap(0);
                l.setHorizontalTextPosition(JLabel.LEADING);
                l.setAlignmentX(JLabel.RIGHT);
            }
            return component;
        }
    }

    private TrainTableColumn(String key, int minWidth, int maxWidth, int prefWidth, String forbidden, Class<?> clazz,
            boolean rightAling, TableCellEditor editor) {
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
            switch (limit) {
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
                case 'c':
                    time = true;
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
            return false;
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
            if (time) {
                tableColumn.setCellRenderer(new TimeCellRenderer(cellRenderer));
            }
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

    public static int getIndex(TableColumnModel model, TrainTableColumn column) {
        Enumeration<TableColumn> e = model.getColumns();
        int i = 0;
        while (e.hasMoreElements()) {
            TableColumn tc = e.nextElement();
            if (tc.getModelIndex() == column.getIndex())
                return i;
            i++;
        }
        return -1;
    }
}
