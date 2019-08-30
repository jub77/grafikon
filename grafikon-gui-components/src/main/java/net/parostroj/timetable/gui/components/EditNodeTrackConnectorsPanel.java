package net.parostroj.timetable.gui.components;

import javax.swing.Box;
import javax.swing.BoxLayout;

import org.beanfabrics.Path;

import net.parostroj.timetable.gui.pm.NodePM;
import net.parostroj.timetable.gui.pm.TrackConnectorPM;

/**
 * Panel for editing port connectors.
 *
 * @author jub
 */
public class EditNodeTrackConnectorsPanel extends BaseEditPanel<NodePM> {

    private static final long serialVersionUID = 1L;

    public EditNodeTrackConnectorsPanel() {
        ItemSetEditPanel<TrackConnectorPM> connectorsPanel = new ItemSetEditPanel<>(new Path("connectorId"), 3);
        connectorsPanel.setModelProvider(localProvider);
        connectorsPanel.setPath(new Path("connectors"));

        EditNodeTrackConnectorPanel connectorPanel = new EditNodeTrackConnectorPanel();

        ListSelectionSupport<TrackConnectorPM> support = new ListSelectionSupport<>(
                connectorPanel,
                () -> getPresentationModel().getConnectors());
        localProvider.addModelProviderListener(new Path("connectors"), support);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(connectorsPanel);
        this.add(Box.createVerticalStrut(5));
        this.add(connectorPanel);
    }
}
