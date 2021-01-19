package net.parostroj.timetable.gui.views;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Collection;

import java.util.Map;
import java.util.Set;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Track;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.computation.TrainRouteTracksComputation;

/**
 * Cell editor for editing tracks.
 *
 * @author jub
 */
public class TrackCellEditor extends AbstractCellEditor implements TableCellEditor {

    private static final long serialVersionUID = 1L;

    private final JComboBox<Track> editor;
    private boolean ignoreAction = true;

    private final TrainRouteTracksComputation comp = new TrainRouteTracksComputation();

    public TrackCellEditor() {
        editor = new JComboBox<>();
        editor.addActionListener(e -> {
            if (!ignoreAction) {
                fireEditingStopped();
            }
        });
        editor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
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
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
            int row, int column) {
        ignoreAction = true;
        editor.removeAllItems();
        Train train = ((TrainTableModel) table.getModel()).getTrain();
        final TimeInterval interval = train.getTimeIntervalList().get(row);
        Map<TimeInterval, Set<Track>> available = comp.getAvailableTracksForTrain(train);
        Collection<? extends Track> tracks = available.get(interval);
        for (Track track : tracks) {
            editor.addItem(track);
        }

        editor.setSelectedItem(interval.getTrack());
        ignoreAction = false;

        return editor;
    }
}
