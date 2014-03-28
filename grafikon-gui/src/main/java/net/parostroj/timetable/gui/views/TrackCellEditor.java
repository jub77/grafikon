package net.parostroj.timetable.gui.views;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Track;

/**
 * Cell editor for editing tracks.
 *
 * @author jub
 */
public class TrackCellEditor extends AbstractCellEditor implements TableCellEditor {

    private final JComboBox editor;
    private boolean ignoreAction = true;

    public TrackCellEditor() {
        editor = new JComboBox();
        editor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ignoreAction) {
                    fireEditingStopped();
                }
            }
        });
    }

    @Override
    public Object getCellEditorValue() {
        return editor.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        ignoreAction = true;
        editor.removeAllItems();
        TimeInterval interval = ((TrainTableModel) table.getModel()).getTrain().getTimeIntervalList().get(row);
        List<? extends Track> tracks = interval.getOwner().getTracks();

        for (Track track : tracks) {
            editor.addItem(track);
        }

        editor.setSelectedItem(interval.getTrack());
        ignoreAction = false;

        return editor;
    }
}
