package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JScrollPane;

import org.beanfabrics.Path;
import org.beanfabrics.model.ListPM;

import net.parostroj.timetable.gui.components.EditNodeTrackConnectorsPanel;
import net.parostroj.timetable.gui.components.EditNodeTracksPanel;
import net.parostroj.timetable.gui.components.ItemSelectionPanel;
import net.parostroj.timetable.gui.components.ListSelectionSupport;
import net.parostroj.timetable.gui.pm.NodePM;
import net.parostroj.timetable.gui.pm.TrackConnectorSwitchPM;

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
        ItemSelectionPanel<TrackConnectorSwitchPM> connTracksPanel = new ItemSelectionPanel<>(
                new Path("connected"), new Path("track.number"));
        ItemSelectionPanel<TrackConnectorSwitchPM> straightTracksPanel = new ItemSelectionPanel<>(
                new Path("straight"), new Path("track.number"));

        Box box = Box.createVerticalBox();
        box.add(tracksPanel);
        box.add(connectorsPanel);
        box.add(new JScrollPane(connTracksPanel));
        box.add(new JScrollPane(straightTracksPanel));
        this.setLayout(new BorderLayout());
        this.add(box, BorderLayout.CENTER);
        tracksPanel.setBorder(BorderFactory.createTitledBorder("Tracks"));
        connectorsPanel.setBorder(BorderFactory.createTitledBorder("Connectors"));

        // model
        tracksPanel.setModelProvider(localProvider);
        tracksPanel.setPath(new Path("this"));
        connectorsPanel.setModelProvider(localProvider);
        connectorsPanel.setPath(new Path("this"));

        ListSelectionSupport<ListPM<TrackConnectorSwitchPM>> support = new ListSelectionSupport<>(
                Arrays.asList(connTracksPanel, straightTracksPanel),
                () -> getPresentationModel().getConnectors(),
                conn -> conn != null ? conn.getSwitches() : null);
        localProvider.addModelProviderListener(new Path("connectors"), support);

        pack();
    }
}
