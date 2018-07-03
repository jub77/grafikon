package net.parostroj.timetable.gui.components;

import javax.swing.Box;
import javax.swing.BoxLayout;

import org.beanfabrics.Path;

import net.parostroj.timetable.gui.pm.NodePortPM;
import net.parostroj.timetable.gui.pm.TrackConnectorPM;

/**
 * Panel for editing port connectors.
 *
 * @author jub
 */
public class EditNodePortTrackConnectorsPanel extends BaseEditPanel<NodePortPM> {

    private static final long serialVersionUID = 1L;

    public EditNodePortTrackConnectorsPanel() {
        ItemListEditPanel<TrackConnectorPM> connectorsPanel = new ItemListEditPanel<>(new Path("number"), 5);
        connectorsPanel.setModelProvider(localProvider);
        connectorsPanel.setPath(new Path("connectors"));

        EditNodePortTrackConnectorPanel connectorPanel = new EditNodePortTrackConnectorPanel();

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
