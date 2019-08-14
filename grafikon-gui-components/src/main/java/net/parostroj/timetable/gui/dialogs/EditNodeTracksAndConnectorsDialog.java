package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JScrollPane;

import org.beanfabrics.Path;
import org.beanfabrics.swing.table.BnColumn;
import org.beanfabrics.swing.table.BnTable;

import net.parostroj.timetable.gui.components.EditNodeTrackConnectorsPanel;
import net.parostroj.timetable.gui.components.EditNodeTracksPanel;
import net.parostroj.timetable.gui.components.ListSelectionSupport;
import net.parostroj.timetable.gui.pm.NodePM;
import net.parostroj.timetable.gui.utils.ResourceLoader;

/**
 * Dialog for editing node tracks and connectors.
 *
 * @author jub
 */
public class EditNodeTracksAndConnectorsDialog extends BaseEditDialog<NodePM> {

    private static final long serialVersionUID = 1L;

    public EditNodeTracksAndConnectorsDialog(Window owner, boolean modal) {
        super(owner, modal);

        EditNodeTracksPanel tracksPanel = new EditNodeTracksPanel();
        EditNodeTrackConnectorsPanel connectorsPanel = new EditNodeTrackConnectorsPanel();

        BnTable switchesTable = new BnTable();
        switchesTable.addColumn(new BnColumn(new Path("track.number"), ResourceLoader.getString("ne.track")));
        switchesTable.addColumn(new BnColumn(new Path("connected"), ResourceLoader.getString("ne.switches.connected")));
        switchesTable.addColumn(new BnColumn(new Path("straight"), ResourceLoader.getString("ne.switches.straight")));

        Dimension size = switchesTable.getPreferredScrollableViewportSize();
        size.height = 5 * switchesTable.getRowHeight();
        switchesTable.setPreferredScrollableViewportSize(size);

        Box box = Box.createVerticalBox();
        box.add(tracksPanel);
        box.add(connectorsPanel);
        JScrollPane switchesScrollPane = new JScrollPane(switchesTable);
        box.add(switchesScrollPane);
        this.setLayout(new BorderLayout());
        this.add(box, BorderLayout.CENTER);
        tracksPanel
                .setBorder(BorderFactory.createTitledBorder(ResourceLoader.getString("ne.tracks")));
        connectorsPanel.setBorder(
                BorderFactory.createTitledBorder(ResourceLoader.getString("ne.connectors")));
        switchesScrollPane.setBorder(
                BorderFactory.createTitledBorder(ResourceLoader.getString("ne.switches")));

        // model
        tracksPanel.setModelProvider(localProvider);
        tracksPanel.setPath(new Path("this"));
        connectorsPanel.setModelProvider(localProvider);
        connectorsPanel.setPath(new Path("this"));
        switchesTable.setModelProvider(localProvider);
        switchesTable.setPath(new Path("connectors"));

        ListSelectionSupport<?> support = new ListSelectionSupport<>(
                switchesTable,
                () -> getPresentationModel().getConnectors(),
                conn -> conn != null ? conn.getSwitches() : null);
        localProvider.addModelProviderListener(new Path("connectors"), support);

        pack();
    }
}
